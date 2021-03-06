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

package org.jetbrains.plugins.ruby.rails.actions.templates;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.plugins.ruby.RBundle;
import com.intellij.ide.fileTemplates.CreateFromTemplateActionReplacer;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Oct 6, 2007
 */
public class RailsCreateFromTemplateActionReplacer implements CreateFromTemplateActionReplacer
{
	public static final String RHTML_TEMPLATE_NAME = RBundle.message("template.rhtml.script.name");

	public static final String RXML_TEMPLATE_NAME = RBundle.message("template.rxml.script.name");

	@Override
	@Nullable
	public AnAction replaceCreateFromFileTemplateAction(@Nonnull final FileTemplate fileTemplate)
	{
		final String templateName = fileTemplate.getName();
		if(templateName.equals(RHTML_TEMPLATE_NAME))
		{
			return new RailsCreateFromTemplateAction(fileTemplate)
			{
				@Override
				@Nonnull
				protected CreateFileFromTemplateDialog createDilog(final Project project, final PsiDirectory dir, final FileTemplate selectedTemplate)
				{
					return new RHTMLCreateViewFromTemplateDialog(project, dir, fileTemplate);
				}
			};
		}
		else if(templateName.equals(RXML_TEMPLATE_NAME))
		{
			return new RailsCreateFromTemplateAction(fileTemplate)
			{
				@Override
				@Nonnull
				protected CreateFileFromTemplateDialog createDilog(final Project project, final PsiDirectory dir, final FileTemplate selectedTemplate)
				{
					return new RXMLCreateViewFromTemplateDialog(project, dir, fileTemplate);
				}
			};
		}
		return null;
	}

}
