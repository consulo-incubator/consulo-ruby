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

package org.jetbrains.plugins.ruby.addins.gems;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.run.CommandLineArgumentsProvider;
import org.jetbrains.plugins.ruby.ruby.run.ConsoleRunner;
import org.jetbrains.plugins.ruby.ruby.run.Output;
import org.jetbrains.plugins.ruby.ruby.run.RubyScriptRunner;
import org.jetbrains.plugins.ruby.ruby.run.RubyScriptRunnerArgumentsProvider;
import org.jetbrains.plugins.ruby.ruby.run.RunContentDescriptorFactory;
import org.jetbrains.plugins.ruby.ruby.run.Runner;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Dec 1, 2007
 */
public class GemsRunner
{
	/**
	 * Runs gem executable ruby script (script must be located in gems bin directory) in console.
	 * After process has finished method will refresh module content
	 * Also RubyErrorFilter will be automatically attached to console.
	 *
	 * @param module                  Current module, my be uncommited
	 * @param sdk                     SDK because module may be uncomitted
	 * @param consoleTitle            Title for console
	 * @param userActions             if these actions are not null, its will be added to console toolbar
	 * @param runInBackgroundThread   Run operation in background thread and show modal dialog
	 * @param gemExecutableScriptName Gem executable ruby script name
	 * @param workingDir              Working directory, null to inherit parent add home directory
	 * @param provider                Provides commandline arguments
	 * @param descriptorFactory       User Factory for creating non default run content descriptors
	 * @param filters                 Console Filters
	 * @param onDone                  Will be executed after gem will have been finished
	 */
	public static void runGemScriptInConsoleAndRefreshModule(@Nonnull final Module module, @Nonnull final Sdk sdk, @Nonnull final String consoleTitle, @Nullable final AnAction[] userActions, final boolean runInBackgroundThread, @Nonnull final String gemExecutableScriptName, @Nullable final String workingDir, @Nullable final CommandLineArgumentsProvider provider, @Nullable final RunContentDescriptorFactory descriptorFactory, final Filter[] filters, @Nullable final Runnable onDone)
	{

		final String[] scriptCommands = getGemExecutableScriptCommand(sdk, gemExecutableScriptName, true);
		if(scriptCommands == null)
		{
			return;
		}

		final ProcessAdapter processListener = new ProcessAdapter()
		{
			@Override
			public void processTerminated(ProcessEvent event)
			{
				RModuleUtil.refreshRubyModuleTypeContent(module);
				if(onDone != null)
				{
					onDone.run();
				}
			}
		};
		final RubyScriptRunnerArgumentsProvider resultProvider = new RubyScriptRunnerArgumentsProvider(scriptCommands, provider, null);

		ConsoleRunner.run(module.getProject(), processListener, filters, userActions, runInBackgroundThread, consoleTitle, workingDir, resultProvider, descriptorFactory);
	}

	/**
	 * Returns gem executable ruby script command
	 *
	 * @param sdk            Ruby sdk
	 * @param rubyScriptName Gem executable ruby script name(e.g.: rails, spec, rake,..)
	 * @param showErrMsg     determinates if error message dialog must be shown
	 * @return true if gem executable ruby script can be found in gems bin folder
	 */
	@Nullable
	public static String[] getGemExecutableScriptCommand(@Nullable final Sdk sdk, @Nonnull final String rubyScriptName, final boolean showErrMsg)
	{
		try
		{
			RubyScriptRunner.validateSDK(sdk);

			final String scriptPath = GemUtil.getGemExecutableRubyScriptPath(sdk, rubyScriptName);
			if(scriptPath != null)
			{
				final String[] defaultParams = RubyScriptRunner.getVMDefaultParams(sdk);
				final String[] params = new String[defaultParams.length + 1];
				System.arraycopy(defaultParams, 0, params, 0, defaultParams.length);
				params[params.length - 1] = scriptPath;

				return params;
			}

			throw new ExecutionException(RBundle.message("execution.error.no.executable.cmd.for.gem", rubyScriptName));
		}
		catch(ExecutionException e)
		{
			if(showErrMsg)
			{
				RubyScriptRunner.showExecutionErrorDialog(e);
			}
		}
		return null;
	}


	/**
	 * Returns output after execution.
	 *
	 * @param sdk              Ruby sdk
	 * @param project          Project
	 * @param rubyScriptName   Gem Executable ruby script name. (e.g. rails, spec, rake)
	 * @param workingDir       working directory
	 * @param mode             Execution mode
	 * @param showStdErrErrors If true, all data from stderr will be shown as errors in Message tab. In this case project must be not null!
	 * @param errorTitle       If showStdErrors is set this title will be used in console. If null, standart error title will be used.
	 * @param arguments        Script arguments
	 * @return Output
	 */
	@Nullable
	public static Output runGemsExecutableScript(@Nullable final Sdk sdk, @Nullable final Project project, @Nonnull final String rubyScriptName, @Nullable final String workingDir, @Nonnull final Runner.ExecutionMode mode, final boolean showStdErrErrors, @Nullable final String errorTitle, @Nonnull final String... arguments)
	{

		final String scriptPath = GemUtil.getGemExecutableRubyScriptPath(sdk, rubyScriptName);
		if(scriptPath == null)
		{
			return null;
		}
		return RubyScriptRunner.runRubyScript(sdk, project, scriptPath, workingDir, mode, showStdErrErrors, errorTitle, arguments);
	}
}
