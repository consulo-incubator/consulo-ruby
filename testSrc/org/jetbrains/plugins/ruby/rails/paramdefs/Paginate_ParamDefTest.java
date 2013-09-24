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

package org.jetbrains.plugins.ruby.rails.paramdefs;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Jul 2, 2008
 */
public class Paginate_ParamDefTest extends AbstractParamDefTest {
    public void testPaginateModelCompletion() throws Exception {
        doTestCompletion("paginate", "app/controllers/completion.rb",
                "foos", "bars");
    }

    public void testPaginateModelResolve() throws Exception {
        doTestResolveToFile("paginate", "app/controllers/resolve_model.rb",
                "app/models/foo.rb");
    }
}