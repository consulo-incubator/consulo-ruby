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

package org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.data;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Oct 30, 2007
 */
public class LoadPath
{
	private final Object LOCK = new Object();

	private final LoadPath myBaseLoadPath;

	private Set<VirtualFile> myLoadPathes = new HashSet<VirtualFile>();

	public LoadPath(@Nullable final LoadPath loadPath)
	{
		myBaseLoadPath = loadPath;
	}

	@Nonnull
	public Set<VirtualFile> getLoadPathFiles()
	{
		final HashSet<VirtualFile> all = new HashSet<VirtualFile>();
		addAll(all);
		return all;
	}

	protected void addAll(@Nonnull final Set<VirtualFile> set)
	{
		if(myBaseLoadPath != null)
		{
			myBaseLoadPath.addAll(set);
		}
		synchronized(LOCK)
		{
			set.addAll(myLoadPathes);
		}
	}

	public void addLoadPathUrl(@Nonnull final String loadPathUrl)
	{
		final VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(loadPathUrl);
		if(file != null)
		{
			synchronized(LOCK)
			{
				myLoadPathes.add(file);
			}
		}
	}
}
