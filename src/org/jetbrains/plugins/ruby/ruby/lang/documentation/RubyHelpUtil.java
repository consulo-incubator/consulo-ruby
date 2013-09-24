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

package org.jetbrains.plugins.ruby.ruby.lang.documentation;

import java.util.LinkedList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualElement;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualStructuralElement;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.ResolveUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.LastSymbolStorage;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.RubyOverrideImplementUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Type;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Types;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.JavaSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolUtil;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RVirtualPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.AccessModifier;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RBodyStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RCompoundStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RAssignmentExpression;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.AccessModifiersUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.expressions.RAssignmentExpressionNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RConstant;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.fields.FieldType;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.fields.RField;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.global.RGlobalVariable;
import org.jetbrains.plugins.ruby.ruby.presentation.RContainerPresentationUtil;
import org.jetbrains.plugins.ruby.ruby.presentation.SymbolPresentationUtil;
import com.intellij.codeInsight.documentation.DocumentationManagerUtil;
import com.intellij.codeInsight.javadoc.JavaDocUtil;
import com.intellij.lang.ASTNode;
import com.intellij.lang.LanguageDocumentation;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Apr 22, 2007
 */
public class RubyHelpUtil implements MarkupConstants {
    @Nullable
    public static String getHelpByElement(@NotNull RPsiElement element){
        if (element instanceof RContainer){
            final FileSymbol fileSymbol = LastSymbolStorage.getInstance(element.getProject()).getSymbol();
            return createHelpBySymbol(fileSymbol, SymbolUtil.getSymbolByContainer(fileSymbol, (RContainer) element), element);
        }

        final String help = getHelpBySymbols(ResolveUtil.resolveToSymbols(element));
        if (help!=null){
            return help;
        }

        final String shortHelp = getShortDescription(element, true);
        final StringBuilder builder = new StringBuilder();
        if (shortHelp != null){
            builder.append(shortHelp);
            builder.append(HR);
        }

        String psiHelp = getPsiHelp(element);
        if (psiHelp!=null){
            psiHelp = MarkupUtil.processText(psiHelp);
        }

        if (psiHelp != null) {
            builder.append(BOLD_PREFIX).append(RBundle.message("ruby.doc.documentation")).append(":").append(BOLD_SUFFIX).append(BR);
            builder.append(psiHelp);
        } else {
            builder.append(RBundle.message("ruby.doc.not.found"));
        }

        return builder.length()>0 ? builder.toString() : null;
    }


    @Nullable
    private static String createHelpBySymbol(@Nullable final FileSymbol fileSymbol,
                                             @Nullable final Symbol symbol,
                                             @Nullable final RPsiElement container) {
        if (symbol == null){
            return null;
        }
        if (Types.JAVA.contains(symbol.getType())){
            //noinspection ConstantConditions
            return LanguageDocumentation.INSTANCE.forLanguage(JavaLanguage.INSTANCE).generateDoc(((JavaSymbol) symbol).getPsiElement(), null);
        }
        final StringBuilder builder = new StringBuilder();
        final Project project = symbol.getProject();

// Searching for psiElement to get help
        final RVirtualElement prototype;
        final RPsiElement elementToShowHelpFor;

        if (container != null){
            elementToShowHelpFor = container;
            prototype = RVirtualPsiUtil.findVirtualContainer((RContainer) container);
        } else {
            prototype = symbol.getLastVirtualPrototype(fileSymbol);
            if (prototype==null){
                return null;
            }
            elementToShowHelpFor = RVirtualPsiUtil.findPsiByVirtualElement(prototype, project);
        }
        if (elementToShowHelpFor==null){
            return null;
        }

        String help = getPsiHelp(elementToShowHelpFor);
        if (help!=null){
            help = MarkupUtil.processText(help);
        }

        // add symbol type text
        final String typeText = getTypeText(symbol);
        if (typeText!=null){
            builder.append(typeText).append(SPACE);
        }
        // add presentable name
        MarkupUtil.appendBoldCode(builder, getPresentableName(elementToShowHelpFor));
        if (elementToShowHelpFor instanceof RVirtualContainer){
            final String access = getVisibility((RVirtualContainer) elementToShowHelpFor);
            builder.insert(0, SPACE).insert(0, access);
            builder.append(SPACE).append(RBundle.message("in")).append(SPACE);
            MarkupUtil.appendCode(builder, RContainerPresentationUtil.getLocation((RVirtualContainer) elementToShowHelpFor));
        }

        builder.append(HR);
        if (help != null) {
            MarkupUtil.appendBold(builder, RBundle.message("ruby.doc.documentation") + ":");
            builder.append(BR).append(help);
        } else {
            builder.append(RBundle.message("ruby.doc.not.found"));
        }

        if (!Types.ATTR_METHODS.contains(symbol.getType())){
// Adding info about overriden/aliased symbols
            final List<Symbol> symbols = RubyOverrideImplementUtil.getOverridenSymbols(fileSymbol, symbol);
            final List elements = RubyOverrideImplementUtil.getOverridenElements(fileSymbol, symbol, prototype, symbols);
            final int size = elements.size();
            if (size > 0){
                if (symbol.getType() == Type.ALIAS){
                    builder.append(BR).append(BR);
                    MarkupUtil.appendBold(builder, RBundle.message("aliases") + ":");
                    builder.append(BR);
                    MarkupUtil.appendCode(builder, symbol.getLinkedSymbol().getName());
                    builder.append(SPACE).append(RBundle.message("in"));
                } else {
                    builder.append(BR).append(BR);
                    MarkupUtil.appendBold(builder, RBundle.message("overrides") + ":");
                    builder.append(BR);
                    MarkupUtil.appendCode(builder, symbol.getLinkedSymbol().getName());
                    builder.append(SPACE).append(RBundle.message("in"));
                }

                addLinksByElements(builder, project, elements, size);
            }

// Adding info about implemented java methods
            final List implemented = RubyOverrideImplementUtil.getImplementedJavaMethods(symbols);
            final int implementedSize = implemented.size();
            if (implementedSize > 0){
                    builder.append(BR).append(BR);
                    MarkupUtil.appendBold(builder, RBundle.message("implements") + ":");
                    builder.append(BR);
                    MarkupUtil.appendCode(builder, symbol.getLinkedSymbol().getName());
                    builder.append(SPACE).append(RBundle.message("in"));

                addLinksByElements(builder, project, implemented, implementedSize);
            }
        }
        return builder.length()>0 ? builder.toString() : null;
    }

    /*
     * Adds links for elements(PsiMethods or Ruby elements) to builder
     */
    private static void addLinksByElements(@NotNull final StringBuilder builder,
                                           @NotNull final Project project,
                                           @NotNull final List elements,
                                           final int size) {
        boolean infoAdded = false;
        for (int i = 0; i < size; i++) {
            final Object element = elements.get(i);
            Object psiElem = null;
            String label = null;
            String ref = null;

            // Ruby element
            if (element instanceof RVirtualStructuralElement){
                psiElem = RVirtualPsiUtil.findInPsi(project, (RVirtualStructuralElement) element);
                label = RContainerPresentationUtil.getLocation((RVirtualStructuralElement) element);
                ref = ELEMENT + i;
            } else

                // Pure Java method
            if (element instanceof PsiMethod){
                psiElem = element;
                label = ((PsiMethod) element).getContainingClass().getQualifiedName();
                ref = JavaDocUtil.getReferenceText(project, (PsiMethod) element);
            }

            if (psiElem != null && label!=null && ref!=null) {
                if (infoAdded){
                    builder.append(COMMA);
                    builder.append(SPACE);
                }
                infoAdded = true;
                builder.append(SPACE);
				DocumentationManagerUtil.createHyperlink(builder, ref, label, false);
            }
        }
    }

    private static String getVisibility(@NotNull final RVirtualContainer container) {
        final AccessModifier accessModifier = container.getAccessModifier();
        if (accessModifier == AccessModifier.PRIVATE){
            return RBundle.message("ruby.doc.private");
        }
        if (accessModifier == AccessModifier.PROTECTED){
            return RBundle.message("ruby.doc.protected");
        }
        if (accessModifier == AccessModifier.PUBLIC){
            return RBundle.message("ruby.doc.public");
        }
        throw new IllegalStateException("Wrong access modifier for container " + container);
    }

    @Nullable
    public static String getTypeText(@NotNull final Symbol symbol) {
        final Type type = symbol.getType();
        if (type == Type.MODULE){
            return RBundle.message("ruby.doc.module");
        }
        if (type == Type.CLASS){
            return RBundle.message("ruby.doc.class");
        }
        if (type == Type.INSTANCE_METHOD){
            return RBundle.message("ruby.doc.method.instance");
        }
        if (type == Type.CLASS_METHOD){
            return RBundle.message("ruby.doc.method.class");
        }
        if (type == Type.CONSTANT){
            return RBundle.message("constant");
        }
        if (type == Type.INSTANCE_FIELD){
            return RBundle.message("instance.variable");
        }
        if (type == Type.CLASS_FIELD){
            return RBundle.message("class.variable");
        }
        if (type == Type.CLASS_INSTANCE_FIELD){
            return RBundle.message("instance.variable");
        }
        if (type == Type.GLOBAL_VARIABLE){
            return RBundle.message("global.variable");
        }
        return null;
    }

    @Nullable
    private static String getHelpBySymbols(@NotNull final List<Symbol> list) {
        if (list.isEmpty()){
            return null;
        }
        Symbol symbol = list.get(0);
        final FileSymbol fileSymbol = LastSymbolStorage.getInstance(symbol.getProject()).getSymbol();
        if (list.size()==1){
            return createHelpBySymbol(fileSymbol, symbol, null);
        }
        final StringBuilder buffer = new StringBuilder();
        buffer.append(RBundle.message("ruby.doc.select.symbol"));
        for (int i = 0; i < list.size(); i++) {
            symbol = list.get(i);
            buffer.append(BR);
            final String label = SymbolPresentationUtil.getPresentableNameWithLocation(fileSymbol, symbol);
            if (label!=null){
				DocumentationManagerUtil.createHyperlink(buffer, SYMBOL + i, label, false);
            }
        }
        return buffer.toString();
    }

    @Nullable
    public static String getShortDescription(@Nullable final PsiElement element,
                                             final boolean isHtmlOutput){
        if (element == null){
            return null;
        }
        if (element instanceof PsiFile || !(element instanceof RPsiElement)) {
            return null;
        }

        // Show help by symbol for container if can be found
        if (element instanceof RVirtualContainer){
            final Symbol symbol = SymbolUtil.getSymbolByContainer(LastSymbolStorage.getInstance(element.getProject()).getSymbol(), (RContainer) element);
            if (symbol == null){
                return null;
            }
            final String typeText = RubyHelpUtil.getTypeText(symbol);
            final String path = SymbolUtil.getPresentablePath(symbol);

            return typeText!=null ? typeText + " " + path : path;
        }

        // Show help by psiElement
        final String name = RubyHelpUtil.getPresentableName(element);
        final String formattedName = isHtmlOutput
                ? SPACE + MarkupUtil.boldCode(name)
                : " " + name;
        if (element instanceof RIdentifier) {
            final RIdentifier id = (RIdentifier) element;
            if (id.isParameter()) {
                return RBundle.message("parameter") + formattedName;
            }
            if (id.isLocalVariable()) {
                return RBundle.message("local.variable") + formattedName;
            }
            return null;
        }
        if (element instanceof RConstant){
            final RConstant constant = (RConstant) element;
            if (constant.isInDefinition()){
                return RBundle.message("constant") + formattedName;
            }
        }
        if (element instanceof RField){
            final RField field = (RField) element;
            final String typeText = field.getType() == FieldType.CLASS_VARIABLE
                    ? RBundle.message("class.variable")
                    : RBundle.message("instance.variable");
            return typeText + formattedName;
        }
        if (element instanceof RGlobalVariable){
            final RGlobalVariable var = (RGlobalVariable) element;
            if (var.isInDefinition()){
                return RBundle.message("global.variable") + formattedName;
            }
        }
        return formattedName;
    }

    @NotNull
    public static String getPresentableName(@NotNull final PsiElement element){
        if (element instanceof RVirtualContainer){
            final String name = ((RVirtualContainer) element).getName();
            if (element instanceof RMethod){
                return ((RMethod) element).getPresentableName(true);
            }
            return name;
        }
        return element.getText();
    }

    public static List<PsiComment> getPsiComments(@NotNull final RPsiElement element){
        PsiElement anchor = element;

// We should set anchor as assignment in this case
        if (element instanceof RConstant && ((RConstant) element).isInDefinition() ||
                element instanceof RGlobalVariable && ((RGlobalVariable) element).isInDefinition()){
            final RAssignmentExpression assignment = RAssignmentExpressionNavigator.getAssignmentByLeftPart(element);
            assert assignment!=null;
            anchor = assignment;
        }


        final List<PsiComment> comments = getPsiCommentsByAnchor(anchor);
        if (!comments.isEmpty()){
            return comments;
        }


// RUBY-610 fix
        final RPsiElement prev = PsiTreeUtil.getPrevSiblingOfType(anchor, RPsiElement.class);
        if (prev instanceof RIdentifier &&
                AccessModifiersUtil.getModifierByName(prev.getText()) != AccessModifier.UNKNOWN){
            anchor = prev;
        }
        return getPsiCommentsByAnchor(anchor);
    }

    @Nullable
    public static String getPsiHelp(@NotNull final RPsiElement element){
        final List<PsiComment> list = getPsiComments(element);
        final StringBuffer buffer = new StringBuffer();
        for (PsiComment psiComment : list) {
            if (buffer.length()>0){
                buffer.append("\n");
            }
            buffer.append(psiComment.getText());
        }
        return buffer.length()>0 ? buffer.toString() : null;
    }

    @NotNull
    private static PsiElement prepareAnchor(@NotNull PsiElement anchor){
        PsiElement parent;
        while (anchor.getPrevSibling()==null &&
                ((parent = anchor.getParent()) instanceof RCompoundStatement || parent instanceof RBodyStatement)){
            anchor = parent;
        }
        return anchor;
    }

    @NotNull
    private static List<PsiComment> getPsiCommentsByAnchor(@NotNull final PsiElement anchor) {
        final LinkedList<PsiComment> comments = new LinkedList<PsiComment>();

        PsiElement comment = prepareAnchor(anchor);
        while ((comment = getPrevComment(comment)) instanceof PsiComment){
            comments.addFirst((PsiComment) comment);
        }

        return comments;
    }

    @Nullable
    public static PsiComment getPrevComment(@NotNull final PsiElement anchorElement) {
        PsiElement comment = anchorElement.getPrevSibling();
        while (!(isRubyLineComment(comment))){
            if (comment instanceof PsiWhiteSpace){
                comment = comment.getPrevSibling();
            } else {
                return null;
            }
        }
        return ((PsiComment) comment);
    }

    @SuppressWarnings({"BooleanMethodIsAlwaysInverted"})
    private static boolean isRubyLineComment(@Nullable final PsiElement comment) {
        if (comment == null){
            return false;
        }
        final ASTNode node = comment.getNode();
        return node != null && node.getElementType() == RubyTokenTypes.TLINE_COMMENT;
    }
}
