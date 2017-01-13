/*
 * Copyright 2000-2008 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.plugins.ruby.ruby.cache;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RComponents;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.RubyFilesCache;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.impl.RubyFilesCacheImpl;
import org.jetbrains.plugins.ruby.ruby.cache.index.DeclarationsIndex;
import org.jetbrains.plugins.ruby.ruby.cache.index.impl.DeclarationsIndexImpl;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.SymbolsCache;
import org.jetbrains.plugins.ruby.ruby.module.RubyModuleListenerAdapter;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;
import com.intellij.ProjectTopics;
import com.intellij.ide.startup.StartupManagerEx;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootListener;
import com.intellij.openapi.roots.impl.DirectoryIndex;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.messages.MessageBusConnection;
import consulo.bundle.SdkTableListener;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg, Roman.Chernyatchik
 * @date: Jan 23, 2007
 */
public class RubySdkCachesManager implements ProjectComponent
{

	private static final Logger LOG = Logger.getInstance(RubySdkCachesManager.class.getName());

	// map with RubyFilesCaches for each sdk
	private Map<Sdk, RubyFilesCache> sdk2RubyFilesCache = new HashMap<Sdk, RubyFilesCache>();

	@NonNls
	private static final String RUBY_CACHE_DIR = "ruby_caches";
	@NonNls
	private static final String RUBY_CACHE_FILE = "sdk";

	private RubyModuleListenerAdapter rModuleListener;
	private ModuleRootListener moduleRootListener;
	private SdkTableListener jdkTableListener;
	private Project myProject;
	private MessageBusConnection myConnection;

	private SymbolsCache symbolsCache;

	//Directory index must be inizialized before RubySdkCacheManager
	//RubySdkCacheManager must refister pre startup activity after DirectoryIndex
	//PsiManger should build caches(thus create PsiManagerImp) before RubySDKCacheManager to
	//  prevent [RUBY-1184]
	@SuppressWarnings({
			"UnusedDeclaration",
			"UnusedParameters"
	})
	public RubySdkCachesManager(@NotNull final Project project, @NotNull final DirectoryIndex dirIndex, @NotNull final PsiManager psiManager)
	{
		myProject = project;
		createListeners();

		myConnection = myProject.getMessageBus().connect();

		final StartupManagerEx startupManagerEx = StartupManagerEx.getInstanceEx(myProject);

		//Initialize cahe. Cache should be signed on StartupFileSystemSynchronizer
		startupManagerEx.registerPreStartupActivity(new Runnable()
		{
			@Override
			public void run()
			{
				initSkdCaches(myProject);

				// registering listeners
				myConnection.subscribe(ProjectTopics.MODULES, rModuleListener);
				//SdkTable.getInstance().addListener(jdkTableListener);
				myConnection.subscribe(ProjectTopics.PROJECT_ROOTS, moduleRootListener);
			}
		});
		//SetupCache. Create words index.
		startupManagerEx.registerPostStartupActivity(new Runnable()
		{
			@Override
			public void run()
			{
				setupAllCaches(false);
			}
		});
	}

	public void registerSymbolsCache(@NotNull final SymbolsCache cache)
	{
		symbolsCache = cache;
	}

	private void createListeners()
	{
		rModuleListener = new RubyModuleListenerAdapter()
		{
			@Override
			public void moduleAdded(final Project project, final Module module)
			{
				//rebuild caches for this module
				modulesChanged(project, module);
				rebuildCachesForModules(module);
			}

			@Override
			public void moduleRemoved(final Project project, final Module module)
			{
				//delete caches for this module
				modulesChanged(project, module);
				deleteBuildInCachesForModules(module);
			}
		};

		moduleRootListener = new ModuleRootListener()
		{
			@Override
			public void beforeRootsChange(final ModuleRootEvent event)
			{
				// Do nothing
			}

			@Override
			public void rootsChanged(final ModuleRootEvent event)
			{
				final Project project = (Project) event.getSource();
				if(project != myProject)
				{
					return;
				}

				initAndSetupSkdCaches(project, true);
			}
		};

		jdkTableListener = new SdkTableListener.Adapter()
		{
			@Override
			public void sdkRemoved(final Sdk sdk)
			{
				if(RubySdkUtil.isKindOfRubySDK(sdk))
				{
					removeSDK(sdk, true);
				}
			}

			@Override
			public void sdkNameChanged(final Sdk sdk, final String previousName)
			{
				if(RubySdkUtil.isKindOfRubySDK(sdk))
				{
					renameSDK(sdk, previousName);
				}
			}
		};
	}

	private void deleteBuildInCachesForModules(final Module module)
	{
		symbolsCache.clearCaches();
	}

	private void rebuildCachesForModules(final Module module)
	{
		symbolsCache.recreateBuiltInCaches(new Module[]{module});
	}

	/**
	 * @param sdk Sdk to get RFilesStorage
	 * @return FilesCache for sdk
	 */
	@Nullable
	public RubyFilesCache getSdkFilesCache(@NotNull final Sdk sdk)
	{
		return sdk2RubyFilesCache.get(sdk);
	}

	/**
	 * @param sdk Sdk to get WordsIndex
	 * @return WordsIndex for sdk
	 */
	@Nullable
	public DeclarationsIndex getSdkDeclarationsIndex(@Nullable final Sdk sdk)
	{
		if(sdk == null)
		{
			return null;
		}
		LOG.assertTrue(RubySdkUtil.isKindOfRubySDK(sdk), "Ruby SDK type expected, but was: " + sdk.getName());

		final RubyFilesCache cache = getSdkFilesCache(sdk);
		return cache != null ? cache.getDeclarationsIndex() : null;
	}

	@Override
	public void projectOpened()
	{
		// Do nothing
	}

	private void modulesChanged(@NotNull final Project project, @NotNull final Module module)
	{
		if(RModuleUtil.hasRubySupport(module))
		{
			initAndSetupSkdCaches(project, true);
		}
	}

	@Override
	public void projectClosed()
	{
		// unregistering listeners
		//  SdkTable.getInstance().removeListener(jdkTableListener);

		myConnection.disconnect();

		// dispose all the filesCaches
		for(Sdk sdk : sdk2RubyFilesCache.keySet())
		{
			final RubyFilesCache sdkCache = sdk2RubyFilesCache.get(sdk);
			sdkCache.saveCacheToDisk();
		}
	}

	@Override
	@NonNls
	@NotNull
	public String getComponentName()
	{
		return RComponents.RUBY_SDK_CACHE_MANAGER;
	}


	@Override
	public void initComponent()
	{
		//Do nothing
	}

	@Override
	public void disposeComponent()
	{
		sdk2RubyFilesCache.clear();
	}

	public void initSkdCaches(final Project project)
	{
		initAndSetupSkdCaches(project, false, false);
	}

	public void initAndSetupSkdCaches(final Project project, final boolean runProcessWithProgressSynchronously)
	{
		initAndSetupSkdCaches(project, true, runProcessWithProgressSynchronously);
	}

	protected void initAndSetupSkdCaches(final Project project, final boolean doSetup, final boolean runProcessWithProgressSynchronously)
	{
		final Set<Sdk> usedSDKs = collectSDKsUsedInRORModules(project);
		// initially new SDKs - copy of used SDKs
		final HashSet<Sdk> newSDKs = new HashSet<Sdk>(usedSDKs);
		// removing unused sdks
		for(Sdk cachedSdk : new HashSet<Sdk>(sdk2RubyFilesCache.keySet()))
		{
			newSDKs.remove(cachedSdk);
			if(usedSDKs.contains(cachedSdk))
			{
				sdk2RubyFilesCache.get(cachedSdk).setCacheRootURLs(RubySdkUtil.getSdkRootsWithAllGems(cachedSdk));
			}
			else
			{
				removeSDK(cachedSdk, false);
			}
		}

		// adding new Skds
		for(Sdk newSdk : newSDKs)
		{
			addSDK(newSdk, runProcessWithProgressSynchronously, doSetup);
		}
	}

	private Set<Sdk> collectSDKsUsedInRORModules(final Project project)
	{
		final Set<Sdk> usedSDKs = new LinkedHashSet<Sdk>();

		// Searching for all used sdk
		for(Module module : RModuleUtil.getAllModulesWithRubySupport(project))
		{
			// Check module sdk
			final Sdk sdk = RModuleUtil.getModuleOrJRubyFacetSdk(module);
			if(sdk != null && RubySdkUtil.isKindOfRubySDK(sdk))
			{
				usedSDKs.add(sdk);
			}
		}
		return usedSDKs;
	}

	/**
	 * @param sdk Jdk to get cached info for
	 * @return Path to file where cached information is saved.
	 */
	@NotNull
	private String generateCacheFilePath(@NotNull final Sdk sdk)
	{
		return generateCacheFilePath(sdk, sdk.getName());
	}

	/**
	 * @param sdk  Sdk to get cached info for
	 * @param name Sdk name
	 * @return Path to file where cached information is saved.
	 */
	@NotNull
	private String generateCacheFilePath(@NotNull final Sdk sdk, @NotNull final String name)
	{
		return PathManager.getSystemPath() + "/" + RUBY_CACHE_DIR + "/" + RUBY_CACHE_FILE + "/" + name + "_" + sdk.getHomePath().hashCode();
	}

	protected void addSDK(@NotNull final Sdk sdk, boolean runProcessWithProgressSynchronously, boolean doSetup)
	{
		assert !sdk2RubyFilesCache.containsKey(sdk);
		final RubyFilesCache newSdkCache = new RubyFilesCacheImpl(myProject, sdk.getName());
		newSdkCache.setCacheRootURLs(RubySdkUtil.getSdkRootsWithAllGems(sdk));
		newSdkCache.setCacheFilePath(generateCacheFilePath(sdk));
		newSdkCache.initFileCacheAndRegisterListeners();


		// Associating wordsIndex with sdkFilesCache
		newSdkCache.registerDeaclarationsIndex(new DeclarationsIndexImpl(myProject));

		if(doSetup)
		{
			newSdkCache.setupFileCache(runProcessWithProgressSynchronously);
		}
		sdk2RubyFilesCache.put(sdk, newSdkCache);
	}

	private void setupAllCaches(final boolean runProcessWithProgressSynchronously)
	{
		final Collection<RubyFilesCache> caches = sdk2RubyFilesCache.values();

		for(RubyFilesCache cache : caches)
		{
			cache.setupFileCache(runProcessWithProgressSynchronously);
		}
	}

	protected void removeSDK(@NotNull final Sdk sdk, final boolean removeCacheFromDisk)
	{
		final RubyFilesCache cache = sdk2RubyFilesCache.remove(sdk);
		if(cache != null)
		{
			if(removeCacheFromDisk)
			{
				cache.removeCacheFile();
			}
			else
			{
				cache.saveCacheToDisk();
			}
			// Manually dispose here
			Disposer.dispose(cache);
		}
	}


	protected void renameSDK(final Sdk sdk, final String previousName)
	{
		final File prevDataFile = new File(generateCacheFilePath(sdk, previousName));
		final File newDataFile = new File(generateCacheFilePath(sdk));
		try
		{
			if(!prevDataFile.exists())
			{
				return;
			}
			final File parentDir = prevDataFile.getParentFile();

			if(!newDataFile.exists())
			{
				newDataFile.mkdirs();
			}

			if(!newDataFile.exists())
			{
				LOG.warn("Can't create [" + newDataFile.getPath() + "]");
			}

			if(newDataFile.isDirectory())
			{
				newDataFile.delete();
			}
			prevDataFile.renameTo(newDataFile);
			parentDir.delete();
		}
		catch(Exception e)
		{
			LOG.warn("Cache file [" + prevDataFile.getPath() + "] wasn't renamed to [" +
					newDataFile.getPath() + "]");
			LOG.warn(e);
		}
	}

	public static RubySdkCachesManager getInstance(@NotNull final Project project)
	{
		return project.getComponent(RubySdkCachesManager.class);
	}

	@Nullable
	public RubyFilesCache getFirstCacheByFile(@NotNull final VirtualFile file)
	{
		final Sdk sdk = getFirstSdkForFile(file);
		return sdk != null ? getSdkFilesCache(sdk) : null;
	}

	/**
	 * First first sdk, contained file
	 *
	 * @param file Virtual file to search for
	 * @return Sdk or null
	 */
	@Nullable
	public Sdk getFirstSdkForFile(@NotNull final VirtualFile file)
	{
		final String rootUrl = file.getUrl();
		final boolean isDirectory = file.isDirectory();
		for(Sdk sdk : sdk2RubyFilesCache.keySet())
		{
			final RubyFilesCache cache = getSdkFilesCache(sdk);
			if(cache != null)
			{
				// If we search for directory
				if(isDirectory)
				{
					for(String root : cache.getCacheRootURLs())
					{
						if(rootUrl.startsWith(root))
						{
							return sdk;
						}
					}
				}
				else
				{
					if(cache.containsUrl(rootUrl))
					{
						return sdk;
					}
				}
			}
		}
		return null;
	}

}
