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

package org.jetbrains.plugins.ruby.ruby.presentation;

import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualMethod;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualSingletonMethod;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.ArgumentInfo;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RArgumentList;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.methods.RCommandArgumentListImpl;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Iconable;
import consulo.ui.image.Image;
import consulo.ui.image.ImageEffects;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 29.10.2006
 */
public class RMethodPresentationUtil implements RPresentationConstants
{

	public static Image getIcon(final RVirtualMethod rMethod)
	{
		if(rMethod instanceof RVirtualSingletonMethod)
		{
			return ImageEffects.layered(RubyIcons.RUBY_METHOD_NODE, RubyIcons.RUBY_ATTR_STATIC);
		}
		return RubyIcons.RUBY_METHOD_NODE;
	}

	/**
	 * Computes icon for RVirtualMethod and RVirtualSingletonMethod.
	 * Be careful, if flags contains information about visibility, method uses
	 * RIconsUtils.getIconWithModifiers()
	 *
	 * @param rMethod RVirtualMethod
	 * @param flags   com.intellij.openapi.util.Iconable flags
	 * @return Icon
	 */
	public static Image getIcon(final RVirtualMethod rMethod, final int flags)
	{
		if((flags & Iconable.ICON_FLAG_VISIBILITY) == Iconable.ICON_FLAG_VISIBILITY)
		{
			return RContainerPresentationUtil.getIconWithModifiers(rMethod);
		}
		return getIcon(rMethod);
	}

	@Nonnull
	public static ItemPresentation getPresentation(final RVirtualMethod rMethod)
	{
		final Image icon = getIcon(rMethod, Iconable.ICON_FLAG_VISIBILITY);
		return new PresentationData(rMethod.getPresentableName(), TextUtil.wrapInParens(getLocation(rMethod)), icon, null);
	}

	private static String getLocation(final RVirtualMethod rMethod)
	{
		return RContainerPresentationUtil.getLocation(rMethod);
	}

	/**
	 * Formats method representation according options
	 *
	 * @param method  Ruby method
	 * @param options Seee RPresentationConstants
	 * @return formated method representation
	 */
	public static String formatName(@Nonnull final RVirtualMethod method, final int options)
	{
		final StringBuilder buffer = new StringBuilder();
		if((options & SHOW_NAME) != 0)
		{
			buffer.append(method.getName());
		}
		else if((options & SHOW_FULL_NAME) != 0)
		{
			buffer.append(method.getFullName());
		}

		if(method instanceof RMethod)
		{
			if((options & SHOW_PARAMETERS) != 0)
			{
				final RArgumentList argumentList = ((RMethod) method).getArgumentList();
				if(argumentList != null && !argumentList.getArguments().isEmpty())
				{
					buffer.append(TextUtil.SPACE_STRING);
					final boolean includeDefaultValues = (options & SHOW_INITIALIZER) != 0;
					buffer.append(argumentList.getPresentableName(includeDefaultValues));
				}
			}
		}
		else
		{
			if((options & SHOW_PARAMETERS) != 0)
			{
				final List<ArgumentInfo> argumentInfos = method.getArgumentInfos();
				if(argumentInfos.size() != 0)
				{
					buffer.append(TextUtil.SPACE_STRING);
					buffer.append(RCommandArgumentListImpl.getPresentableName(argumentInfos));
				}
			}
		}

		return buffer.toString();
	}
}
