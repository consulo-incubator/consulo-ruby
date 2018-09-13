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

package org.jetbrains.plugins.ruby.ruby.codeInsight.references.psi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.completion.RubyLookupItem;
import org.jetbrains.plugins.ruby.ruby.codeInsight.references.RPsiPolyvariantReference;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.ResolveUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.ClassesOnlyAutocompleteFilter;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.EmptyAutocompleteFilter;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.ScopeAutocompleteFilter;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.ScopeSymbolsUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.TypeSet;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Types;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolUtil;
import org.jetbrains.plugins.ruby.ruby.lang.findUsages.RubyUsageType;
import org.jetbrains.plugins.ruby.ruby.lang.findUsages.RubyUsageTypeProvider;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.names.RSuperClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.classes.RSuperClassNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RNamedElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Aug 31, 2007
 */
public class RNamedReference implements RPsiPolyvariantReference
{
	protected RNamedElement myElement;

	public RNamedReference(@NotNull final RNamedElement element)
	{
		myElement = element;
	}

	@Override
	public final PsiElement getElement()
	{
		return myElement;
	}

	@Override
	@NotNull
	public final PsiElement getRefValue()
	{
		return myElement;
	}

	@Override
	public final TextRange getRangeInElement()
	{
		return new TextRange(0, myElement.getTextLength());
	}

	@Override
	public final String getCanonicalText()
	{
		return myElement.getText();
	}

	@Override
	public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException
	{
		return myElement.setName(newElementName);
	}

	// IDEA calls bindToElement if we rename/move Java class
	@Override
	public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException
	{
		if(element instanceof PsiClass)
		{
			return handleElementRename(((PsiClass) element).getName());
		}
		return null;
	}

	@Override
	public boolean isReferenceTo(final PsiElement element)
	{
		return ResolveUtil.isReferenceTo(this, element);
	}

	@Override
	public boolean isSoft()
	{
		return true;
	}

	@Override
	@Nullable
	public PsiElement resolve()
	{
		return ResolveUtil.resolvePolyVarReference(this);
	}

	private static class MyResolver implements ResolveCache.PolyVariantResolver<RNamedReference>
	{
		public static MyResolver INSTANCE = new MyResolver();

		@Override
		public ResolveResult[] resolve(RNamedReference ref, boolean incompleteCode)
		{
			return ref.multiResolveInner(incompleteCode);
		}
	}

	@Override
	@NotNull
	public final ResolveResult[] multiResolve(final boolean incompleteCode)
	{
		final PsiManager manager = getElement().getManager();
		if(manager instanceof PsiManagerImpl)
		{
			final ResolveCache cache = ResolveCache.getInstance(manager.getProject());
			return cache.resolveWithCaching(this, MyResolver.INSTANCE, false, false);
		}
		else
		{
			return multiResolveInner(incompleteCode);
		}
	}

	@SuppressWarnings({"UnusedDeclaration"})
	@NotNull
	protected ResolveResult[] multiResolveInner(boolean incompleteCode)
	{
		if(((RPsiElementBase) myElement).isClassOrModuleName())
		{
			return new ResolveResult[]{
					new ResolveResult()
					{
						@Override
						@Nullable
						public PsiElement getElement()
						{
							RubyUsageTypeProvider.setType(RNamedReference.this, RubyUsageType.DECLARATION);
							return myElement.getParentContainer();
						}

						@Override
						public boolean isValidResult()
						{
							return true;
						}
					}
			};
		}
		final List<ResolveResult> list = new ArrayList<ResolveResult>();
		RubyUsageTypeProvider.setType(RNamedReference.this, RubyUsageType.UNCLASSIFIED);
		final FileSymbol fileSymbol = ((RPsiElementBase) myElement).forceFileSymbolUpdate();
		for(Symbol symbol : multiResolveToSymbols(fileSymbol))
		{
			ResolveUtil.addVariants(fileSymbol, myElement.getProject(), list, symbol);
		}
		return list.toArray(new ResolveResult[list.size()]);
	}


	@Override
	public final Object[] getVariants()
	{
		final FileSymbol fileSymbol = ((RPsiElementBase) myElement).forceFileSymbolUpdate();
		if(fileSymbol == null)
		{
			return EMPTY_ARRAY;
		}

		myElement.putCopyableUserData(REFERENCE_BEING_COMPLETED, Boolean.TRUE);
		try
		{
			// RUBY-1363. Completion after "class Name <" should show only class names
			final RSuperClass superClass = RSuperClassNavigator.getByPsiElement(myElement);
			final ScopeAutocompleteFilter filter = superClass != null ? new ClassesOnlyAutocompleteFilter() : new EmptyAutocompleteFilter();

			final List<RubyLookupItem> variants = ScopeSymbolsUtil.getScopeSymbolsAndKeywordsLookupItems(fileSymbol, myElement, filter);
			return variants.toArray(new Object[variants.size()]);
		}
		finally
		{
			myElement.putCopyableUserData(REFERENCE_BEING_COMPLETED, null);
		}
	}

	@Override
	@NotNull
	public List<Symbol> multiResolveToSymbols(@Nullable final FileSymbol fileSymbol)
	{
		if(((RPsiElementBase) myElement).isClassOrModuleName())
		{
			return Collections.emptyList();
		}
		//noinspection ConstantConditions
		return multiresolveToSymbols(fileSymbol, myElement.getName(), false, Types.EMPTY_CONTEXT_RESOLVE_TYPES);
	}

	@NotNull
	protected List<Symbol> multiresolveToSymbols(@Nullable final FileSymbol fileSymbol, @NotNull final String name, final boolean global, final TypeSet acceptableTypeSet)
	{
		RContainer container = myElement.getParentContainer();
		assert container != null;

		// We should find look for symbol in parent of given class if we`re inside the superclass
		if(PsiTreeUtil.getParentOfType(myElement, RSuperClass.class) != null)
		{
			container = container.getParentContainer();
			assert container != null;
		}

		final Symbol symbol = SymbolUtil.getSymbolByContainer(fileSymbol, container);
		if(symbol == null)
		{
			return Collections.emptyList();
		}
		final Symbol res = SymbolUtil.findSymbol(fileSymbol, symbol, name, global, acceptableTypeSet);
		if(res != null)
		{
			return Arrays.asList(res);
		}
		return Collections.emptyList();
	}
}
