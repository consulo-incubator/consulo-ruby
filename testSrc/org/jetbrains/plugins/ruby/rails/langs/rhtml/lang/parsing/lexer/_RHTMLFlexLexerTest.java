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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.lexer;

import com.intellij.lexer.FlexAdapter;
import junit.framework.Test;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.ruby.testCases.LexerTestCase;

import java.io.Reader;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 31.03.2007
 */
public class _RHTMLFlexLexerTest extends LexerTestCase {
    @NonNls
    private static final String DATA_PATH = PathUtil.getDataPath(_RHTMLFlexLexerTest.class) + "/_rhtml";

    public _RHTMLFlexLexerTest() {
        super(DATA_PATH);
    }

    protected void setUp() {
        super.setUp();
        //setLexer(new _RHTMLLexer());
        setLexer(new FlexAdapter(new _RHTMLFlexLexer((Reader)null)));        
    }

    public static Test suite() {
        return new _RHTMLFlexLexerTest();
    }
}