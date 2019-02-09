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

package org.jetbrains.plugins.ruby.ruby.codeInsight.paramDefs;

import javax.annotation.Nonnull;

import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;

/**
 * @author yole
 */
public class ParamContext
{
	private RPsiElement myValueElement;
	private RCall myCall;
	private int myIndex;
	private String myHashKey;

	public ParamContext(@Nonnull final RPsiElement valueElement, @Nonnull RCall call, int index, String hashKey)
	{
		myValueElement = valueElement;
		myCall = call;
		myIndex = index;
		myHashKey = hashKey;
	}

	@Nonnull
	public RPsiElement getValueElement()
	{
		return myValueElement;
	}

	@Nonnull
	public RCall getCall()
	{
		return myCall;
	}

	public int getIndex()
	{
		return myIndex;
	}

	public String getHashKey()
	{
		return myHashKey;
	}

	public Module getModule()
	{
		return ModuleUtil.findModuleForPsiElement(myValueElement);
	}

	public Project getProject()
	{
		Module module = getModule();
		return module == null ? null : module.getProject();
	}
}
