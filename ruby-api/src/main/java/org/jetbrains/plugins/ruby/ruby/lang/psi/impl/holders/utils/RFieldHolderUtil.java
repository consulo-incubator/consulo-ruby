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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualField;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.FieldDefinition;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RFieldHolder;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.FieldDefinitionImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.fields.RClassVariable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.fields.RField;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.fields.RInstanceVariable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;
import com.intellij.psi.PsiElement;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 08.08.2006
 */
public class RFieldHolderUtil
{

	/**
	 * Adds a new field to the list of Rfields
	 *
	 * @param list   List of RFields, already found
	 * @param rField Field expression to add
	 */
	private static void addField(final List<FieldDefinition> list, final RField rField)
	{
		for(FieldDefinition f : list)
		{
			if(f.isFor(rField))
			{
				return;
			}
		}

		list.add(new FieldDefinitionImpl(rField));
	}

	@Nonnull
	public static List<FieldDefinition> gatherFieldDescriptions(final RFieldHolder holder)
	{
		final ArrayList<FieldDefinition> list = new ArrayList<FieldDefinition>();

		final RubyElementVisitor fieldVisitor = new RubyElementVisitor()
		{

			@Override
			public void visitRClassVariable(final RClassVariable rClassVariable)
			{
				addField(list, rClassVariable);
			}

			@Override
			public void visitRInstanceVariable(final RInstanceVariable rInstanceVariable)
			{
				addField(list, rInstanceVariable);
			}

			@Override
			public void visitElement(final PsiElement element)
			{
				if(element instanceof RFieldHolder)
				{
					return;
				}
				element.acceptChildren(this);
			}

		};
		holder.acceptChildren(fieldVisitor);
		return list;
	}


	public static FieldDefinition getDefinition(RFieldHolder holder, RVirtualField field)
	{
		return getDefinition(holder.getFieldsDefinitions(), field);
	}

	@Nullable
	public static FieldDefinition getDefinition(@Nonnull final List<FieldDefinition> list, @Nonnull final RVirtualField field)
	{
		for(FieldDefinition definition : list)
		{
			if(definition.isFor(field))
			{
				return definition;
			}
		}
		return null;
	}

}
