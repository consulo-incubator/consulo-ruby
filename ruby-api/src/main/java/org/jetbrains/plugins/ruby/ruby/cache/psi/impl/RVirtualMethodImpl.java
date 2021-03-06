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

package org.jetbrains.plugins.ruby.ruby.cache.psi.impl;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualName;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RubyVirtualElementVisitor;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RVirtualPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.AccessModifier;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.ArgumentInfo;
import org.jetbrains.plugins.ruby.ruby.presentation.RMethodPresentationUtil;
import org.jetbrains.plugins.ruby.ruby.presentation.RPresentationConstants;
import com.intellij.navigation.ItemPresentation;
import consulo.ui.image.Image;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg, Roman Chernyathcik
 * @date: Oct 2, 2006
 */
public class RVirtualMethodImpl extends RVirtualContainerBase implements RVirtualMethod
{

	private final List<ArgumentInfo> myArgumentInfos;

	public RVirtualMethodImpl(final RVirtualContainer parentContainer, @Nonnull final RVirtualName name, @Nonnull final List<ArgumentInfo> arguments, final AccessModifier defaultChildAccessModifier, @Nonnull final RFileInfo containingFileInfo)
	{
		super(parentContainer, name, defaultChildAccessModifier, containingFileInfo);
		myArgumentInfos = arguments;
	}


	@Override
	@Nonnull
	public ItemPresentation getPresentation()
	{
		return RMethodPresentationUtil.getPresentation(this);
	}

	@Nullable
	public Image getIcon(final int flags)
	{
		return RMethodPresentationUtil.getIcon(this, flags);
	}

	@Override
	public void accept(@Nonnull RubyVirtualElementVisitor visitor)
	{
		visitor.visitRVirtualMethod(this);
	}

	@Override
	@Nonnull
	public List<ArgumentInfo> getArgumentInfos()
	{
		return myArgumentInfos;
	}

	@Override
	@Nonnull
	public String getPresentableName()
	{
		final int options = RPresentationConstants.SHOW_FULL_NAME | RPresentationConstants.SHOW_PARAMETERS;
		return RMethodPresentationUtil.formatName(this, options);
	}

	/**
	 * Method assumes that both method have equal parent container
	 *
	 * @param otherMethod other method (virtual or psi)
	 * @return true if methods equals.
	 */
	@Override
	public boolean equalsToMethod(@Nonnull final RVirtualMethod otherMethod)
	{
		return RVirtualPsiUtil.areMethodsEqual(this, otherMethod);
	}

	public String toString()
	{
		return "def [" + ((RVirtualElementBase) getVirtualName()).getId() + "] " + getPresentableName();
	}

	@Override
	public StructureType getType()
	{
		return StructureType.METHOD;
	}

}
