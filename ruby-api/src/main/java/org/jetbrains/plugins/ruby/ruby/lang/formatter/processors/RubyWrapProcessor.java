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

package org.jetbrains.plugins.ruby.ruby.lang.formatter.processors;

import com.intellij.formatting.Wrap;
import com.intellij.formatting.WrapType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.formatter.RubyBlock;
import org.jetbrains.plugins.ruby.ruby.lang.formatter.models.wrap.RNotWraped;
import org.jetbrains.plugins.ruby.ruby.lang.formatter.models.wrap.RWrapCOMPSTMT;
import org.jetbrains.plugins.ruby.ruby.lang.formatter.models.wrap.RWrapLastChild;
import org.jetbrains.plugins.ruby.ruby.lang.formatter.models.wrap.RWrapedAlways;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RCompoundStatement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 01.08.2006
 */
public class RubyWrapProcessor
{
	/**
	 * Calculates wrap between parent and child nodes
	 *
	 * @param parent      parent node
	 * @param child       child node
	 * @param childCount  Total count of parent children
	 * @param childNumber The number of current child
	 * @return wrap
	 */
	@Nullable
	public static Wrap getChildWrap(@Nonnull final RubyBlock parent, @Nonnull final ASTNode child, final int childCount, final int childNumber)
	{
		final PsiElement psiParent = parent.getNode().getPsi();
		final PsiElement psiChild = child.getPsi();

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		///////// RNotWraped //////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		if(psiChild instanceof RNotWraped)
		{
			return Wrap.createWrap(WrapType.NONE, true);
		}


		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		///////// RWrapedAlways //////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		if(psiChild instanceof RWrapedAlways)
		{
			return Wrap.createWrap(WrapType.ALWAYS, true);
		}


		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		///////// RWrapLastChild ///////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		if(psiParent instanceof RWrapLastChild)
		{
			if(childNumber == childCount - 1)
			{
				return Wrap.createWrap(WrapType.ALWAYS, true);
			}
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		///////// RWrapCOMPSTMT ////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		if(psiParent instanceof RWrapCOMPSTMT)
		{
			if(psiChild instanceof RCompoundStatement)
			{
				return Wrap.createWrap(WrapType.ALWAYS, true);
			}
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		///////// Default wrap /////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		return null;
	}
}
