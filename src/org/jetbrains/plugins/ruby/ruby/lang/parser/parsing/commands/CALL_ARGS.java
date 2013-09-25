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

package org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.commands;


import com.intellij.openapi.util.Ref;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.ParsingMethod;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.arg.ARG;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.assocs.ASSOC_OR_ARG;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.ErrorMsg;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.ListParsingUtil;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 27.06.2006
 */
public class CALL_ARGS  implements RubyTokenTypes {
/*CALL_ARGS	: ARGS
		| ARGS [`,' ASSOCS] [`,' `*' ARG] [`,' `&' ARG]
		| ASSOCS [`,' `*' ARG] [`,' `&' ARG]
		| `*' ARG [`,' `&' ARG]
		| `&' ARG
		| COMMAND
*/

    @NotNull
    public static IElementType parse(final RBuilder builder){
        if (!builder.compare(BNF.tCALL_ARG_FIRST_TOKEN)){
            return RubyElementTypes.EMPTY_INPUT;
        }

        RMarker statementMarker = builder.mark();
        IElementType result = COMMAND_OR_ARG.parse(builder);
        if (result==RubyElementTypes.COMMAND_CALL){
            statementMarker.done(RubyElementTypes.LIST_OF_EXPRESSIONS);
            return RubyElementTypes.LIST_OF_EXPRESSIONS;
        }


        final Ref<Boolean> assocSeen = new Ref<Boolean>(false);
        final Ref<Boolean> starSeen = new Ref<Boolean>(false);
        final Ref<Boolean> amperSeen = new Ref<Boolean>(false);
        ParsingMethod parsingMethod = new ParsingMethod(){
            @Override
			@NotNull
            public IElementType parse(final RBuilder builder){
                if(amperSeen.get()){
                    return RubyElementTypes.EMPTY_INPUT;
                }

                if(builder.compare(tAMPER)){
                    amperSeen.set(true);
                    return parseBlockToArg(builder);
                }

                if(builder.compare(tSTAR) && !starSeen.get()){
                    starSeen.set(true);
                    return parseArrayToArguments(builder);
                }

                IElementType result = ASSOC_OR_ARG.parse(builder);
                if (result == RubyElementTypes.ASSOC){
                    assocSeen.set(true);
                    return result;
                }

                if (assocSeen.get() && result!=RubyElementTypes.ASSOC){
                    builder.error(ErrorMsg.expected(tASSOC));
                }
                return result;
            }
        };

        if (result!=RubyElementTypes.EMPTY_INPUT){
            if (builder.compare(tASSOC)){
                ASSOC_OR_ARG.parseWithLeadArg(builder, statementMarker, result);
                assocSeen.set(true);
                statementMarker = statementMarker.precede();
            }
            if (builder.compare(tCOMMA)){
                ListParsingUtil.parseCommaDelimitedExpressionWithLeadExpr(builder, result, parsingMethod);
                statementMarker.done(RubyElementTypes.LIST_OF_EXPRESSIONS);
                return RubyElementTypes.LIST_OF_EXPRESSIONS;
            }
            statementMarker.done(RubyElementTypes.LIST_OF_EXPRESSIONS);
            return RubyElementTypes.LIST_OF_EXPRESSIONS;
        }

        int count = ListParsingUtil.parseCommaDelimitedExpressions(builder, parsingMethod);
        if(count==0){
            statementMarker.rollbackTo();
            return RubyElementTypes.EMPTY_INPUT;
        }
        statementMarker.done(RubyElementTypes.LIST_OF_EXPRESSIONS);
        return RubyElementTypes.LIST_OF_EXPRESSIONS;
    }


    /**
     * Parsing *ARG - array to arguments
     * @param builder Current builder
     * @return result of parsing
     */
    public static IElementType parseArrayToArguments(final RBuilder builder){
        RMarker marker  = builder.mark();
        builder.match(tSTAR);
        if(ARG.parse(builder)==RubyElementTypes.EMPTY_INPUT){
            builder.error(ErrorMsg.EXPRESSION_EXPECTED_MESSAGE);
        }
        marker.done(RubyElementTypes.ARRAY_TO_ARGS);
        return RubyElementTypes.ARRAY_TO_ARGS;
    }

    /**
     * Parsing &ARG - block to arg
     * @param builder Current builder
     * @return parsing result
     */
    public static IElementType parseBlockToArg(final RBuilder builder){
        RMarker marker  = builder.mark();
        builder.match(tAMPER);
        if(ARG.parse(builder)==RubyElementTypes.EMPTY_INPUT){
            builder.error(ErrorMsg.EXPRESSION_EXPECTED_MESSAGE);
        }
        marker.done(RubyElementTypes.BLOCK_TO_ARG);
        return RubyElementTypes.BLOCK_TO_ARG;
    }

}
