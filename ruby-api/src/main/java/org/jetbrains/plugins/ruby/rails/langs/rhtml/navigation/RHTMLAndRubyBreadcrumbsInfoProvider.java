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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.navigation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.RHTMLFoldingBuilder;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.eRubyLanguage;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.RHTMLTokenType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.impl.rhtmlRoot.RHTMLRubyInjectionTag;
import org.jetbrains.plugins.ruby.ruby.lang.RubyLanguage;
import org.jetbrains.plugins.ruby.ruby.lang.psi.PresentableElementType;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RFileImpl;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.html.HTMLLanguage;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.ui.breadcrumbs.BreadcrumbsProvider;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Sep 13, 2007
 */

public class RHTMLAndRubyBreadcrumbsInfoProvider implements BreadcrumbsProvider
{
	public static class Ruby extends RHTMLAndRubyBreadcrumbsInfoProvider
	{
		public Ruby()
		{
			super(RubyLanguage.INSTANCE);
		}
	}

	public static class eRuby extends RHTMLAndRubyBreadcrumbsInfoProvider
	{
		public eRuby()
		{
			super(eRubyLanguage.INSTANCE);
		}
	}

	private Language myLanguage;

	public RHTMLAndRubyBreadcrumbsInfoProvider(Language language)
	{
		myLanguage = language;
	}

	@Nonnull
	@Override
	public Language getLanguage()
	{
		return myLanguage;
	}

	@Override
	public boolean acceptElement(@Nonnull final PsiElement element)
	{
		if(isInRubyDebugMode(element))
		{
			return true;
		}

		final PsiFile psiFile = element.getContainingFile();
		if(psiFile == null)
		{
			return false;
		}

		if(psiFile.getLanguage() == eRubyLanguage.INSTANCE)
		{
			return true;
		}

		final PsiFile origFile = psiFile.getOriginalFile();
		if(origFile != null && origFile.getLanguage() == eRubyLanguage.INSTANCE)
		{
			return true;
		}

		//for debug purposes.
		//noinspection RedundantIfStatement
		if(psiFile instanceof RFileImpl)
		{
			return true;
		}

		return false;
	}

	@Override
	@Nonnull
	public String getElementInfo(@Nonnull final PsiElement psiElement)
	{
		return getPresentation(psiElement, true);
	}

	@Override
	@Nullable
	public String getElementTooltip(@Nonnull final PsiElement psiElement)
	{
		return getPresentation(psiElement, false);
	}

	@Override
	@Nullable
	public PsiElement getParent(@Nonnull final PsiElement psiElement)
	{
		if(isInRubyDebugMode(psiElement))
		{
			return BreadcrumbsProvider.super.getParent(psiElement);
		}

		if(psiElement instanceof XmlDocument || psiElement instanceof PsiFile || psiElement instanceof PsiDirectory)
		{
			return null;
		}

		final Language lang = getLangageForElement(psiElement);
		final FileViewProvider provider = psiElement.getContainingFile().getViewProvider();
		final int startOffset = psiElement.getTextRange().getStartOffset();

		if(lang == eRubyLanguage.INSTANCE)
		{
			//Parent is RHTML
			//Omit new Line
			final ASTNode node = psiElement.getNode();
			if(node != null && RHTMLTokenType.OMIT_NEW_LINE == node.getElementType())
			{
				return psiElement.getParent();
			}

			//Parent is HTML
			//Outer for RHTML in HTML
			final PsiElement htmlElem = provider.findElementAt(startOffset, HTMLLanguage.INSTANCE);
			if(htmlElem != null)
			{
				//search max upper tree
				PsiElement parent = htmlElem.getParent();
				while(parent != null && parent.getTextRange() != null &&
						parent.getTextRange().getStartOffset() == startOffset)
				{
					parent = parent.getParent();
				}
				return parent;
			}
		}
		else if(lang == RubyLanguage.INSTANCE)
		{
			//RUBY_CODE_CHARACTERS
			PsiElement parent = provider.findElementAt(startOffset, eRubyLanguage.INSTANCE);


			//noinspection ConstantConditions
			while(parent != null && parent.getNode() != null && !RHTMLTokenType.RHTML_SEPARATORS_STARTS.contains(parent.getNode().getElementType()))
			{
				parent = parent.getPrevSibling();
			}

			//noinspection ConstantConditions
			if(parent != null && parent.getNode() != null && RHTMLTokenType.RHTML_SEPARATORS_STARTS.contains(parent.getNode().getElementType()))
			{
				return parent;
			}
		}
		return BreadcrumbsProvider.super.getParent(psiElement);
	}

	private boolean isInRubyDebugMode(@Nonnull final PsiElement element)
	{
		final PsiFile psiFile = element.getContainingFile();
		return psiFile != null && psiFile instanceof RFileImpl;
	}

	private String getInjectionPresentation(final PsiElement start, final PsiElement startOmit, final PsiElement end, final PsiElement endOmit)
	{
		final StringBuilder builder = new StringBuilder();
		if(start != null)
		{
			//noinspection ConstantConditions
			builder.append(getPrestentation(start.getNode().getElementType()));
		}
		if(startOmit != null)
		{
			//noinspection ConstantConditions
			final IElementType omitNodeType = startOmit.getNode().getElementType();
			if(omitNodeType == RHTMLTokenType.OMIT_NEW_LINE)
			{
				//noinspection ConstantConditions
				builder.append(getPrestentation(omitNodeType));
			}
		}
		builder.append("...");
		if(endOmit != null && startOmit != endOmit)
		{
			//noinspection ConstantConditions
			final IElementType omitNodeType = endOmit.getNode().getElementType();
			if(omitNodeType == RHTMLTokenType.OMIT_NEW_LINE)
			{
				//noinspection ConstantConditions
				builder.append(getPrestentation(omitNodeType));
			}
		}
		if(end != null)
		{
			//noinspection ConstantConditions
			builder.append(getPrestentation(end.getNode().getElementType()));
		}
		return builder.toString();
	}

	private String getPrestentation(@Nonnull final IElementType type)
	{
		if(type instanceof PresentableElementType)
		{
			return ((PresentableElementType) type).getPresentableName();
		}
		return type.toString();
	}

	private String getPresentation(@Nonnull final PsiElement psiElement, final boolean notForToolTip)
	{
		if(isInRubyDebugMode(psiElement))
		{
			return notForToolTip ? psiElement.toString() : RBundle.message("breadcrumbs.rhtml.tooltip.textrange", psiElement.getTextRange());
		}

		final Language lang = getLangageForElement(psiElement);
		if(lang == RubyLanguage.INSTANCE)
		{
			return RBundle.message("breadcrumbs.rhtml.presentation.ruby.injection");
		}
		else if(lang == eRubyLanguage.INSTANCE)
		{
			final ASTNode node;

			if(psiElement instanceof RHTMLRubyInjectionTag)
			{
				final PsiElement startElem = psiElement.getFirstChild();
				node = startElem != null ? startElem.getNode() : null;
			}
			else
			{
				node = psiElement.getNode();
			}


			if(node != null)
			{
				final IElementType type = node.getElementType();
				if(RHTMLTokenType.RHTML_SEPARATORS_STARTS.contains(type) || RHTMLTokenType.RHTML_SEPARATORS_ENDS.contains(type))
				{
					if(notForToolTip)
					{
						return getPrestentation(type);
					}
					final PsiElement injection = node.getPsi().getParent();
					assert injection != null;

					final PsiElement start = injection.getFirstChild();
					final PsiElement startOmit = (start == null ? null : start.getNextSibling());
					final PsiElement end = injection.getLastChild();
					final PsiElement endOmit = (end == null ? null : end.getPrevSibling());

					return getInjectionPresentation(start, startOmit, end, endOmit);
				}
				else if(type == RHTMLTokenType.OMIT_NEW_LINE)
				{
					return RBundle.message("breadcrumbs.rhtml.presentation.omit.new.line");
				}
				else if(RHTMLTokenType.RHTML_ALL_COMMENT_TOKENS.contains(type))
				{
					return notForToolTip ? RBundle.message("breadcrumbs.rhtml.presentation.comment") : RHTMLFoldingBuilder.RHTML_COMMENT_FOLD_TEXT;
				}
			}
		}
		return psiElement.toString();
	}

	private Language getLangageForElement(@Nonnull final PsiElement psiElement)
	{
		if(psiElement instanceof PsiWhiteSpace)
		{
			return psiElement.getParent().getLanguage();
		}
		return psiElement.getLanguage();
	}
}
