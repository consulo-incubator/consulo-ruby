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

package org.jetbrains.plugins.ruby.jruby.codeInsight.resolve;

import com.intellij.psi.PsiReference;
import com.intellij.util.text.StringTokenizer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.stringLiterals.RBaseString;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Sep 12, 2007
 */
public class JavaReferencesBuilder {

    @NotNull
    public static List<PsiReference> createReferences(@NotNull final RPsiElement element,
                                                      @NotNull final RBaseString rBaseString){
        final ArrayList<PsiReference> list = new ArrayList<PsiReference>();
        if (rBaseString.hasExpressionSubstitutions()) {
            return list;
        }
        //noinspection ConstantConditions
        int offset = rBaseString.getTextOffset() - element.getTextOffset() + rBaseString.getFirstChild().getTextLength();
        final String content = rBaseString.getContent();
        if (content.length() == 0){
            list.add(new JavaReference(null, element, rBaseString, offset, ""));
            return list;
        }
        final StringTokenizer tokenizer = new StringTokenizer(content, ".");
        JavaReference lastJavaReference = null;
        while (tokenizer.hasMoreTokens()){
            int index = tokenizer.getCurrentPosition();
            if (index!=0){
                index+=1;
            }
            final String token = tokenizer.nextToken();
            lastJavaReference = new JavaReference(lastJavaReference, element, rBaseString, offset + index, token);
            list.add(lastJavaReference);
        }
        return list;
    }
}
