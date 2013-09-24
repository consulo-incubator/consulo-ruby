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

package org.jetbrains.plugins.ruby.rails.run.configuration;

import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.RailsUtil;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.AbstractRubyRunConfiguration;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.RubyRunConfigurationUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Apr 26, 2008
 */
public class RailsRunConfigurationUtil {
    /**
     * Checks that SDK is valid Ruby kind SDK with Rails Support
     * @param conf RunConfiguration
     * @param isExecution Execution or validating mode
     * @throws Exception Instection errors (ExecutionException or RuntimeException)
     */
    public static void inspectRailsSDK(final AbstractRubyRunConfiguration conf,
                                       final boolean isExecution) throws Exception {
        // Is Valid Ruby SDK
        RubyRunConfigurationUtil.inspectSDK(conf, isExecution);
        // SDK inspection
        if (!RailsUtil.hasRailsSupportInSDK(conf.getSdk())) {
            RubyRunConfigurationUtil.throwExecutionOrRuntimeException(RBundle.message("sdk.error.no.rails.found"), isExecution);
        }
    }
}
