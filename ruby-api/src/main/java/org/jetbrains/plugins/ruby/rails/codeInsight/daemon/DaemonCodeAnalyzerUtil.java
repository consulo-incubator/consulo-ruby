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

package org.jetbrains.plugins.ruby.rails.codeInsight.daemon;

import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.impl.DocumentMarkupModel;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 04.02.2007
 */
public class DaemonCodeAnalyzerUtil
{
	private static final Key<RailsLineMarkerInfo[]> RAILS_MARKERS_IN_EDITOR_DOCUMENT_KEY = Key.create("DaemonCodeAnalyzerUtil.RAILS_MARKERS_IN_EDITOR_DOCUMENT_KEY");

	@Nullable
	public static RailsLineMarkerInfo[] getLineMarkers(final Document document, final Project project)
	{
		ApplicationManager.getApplication().assertIsDispatchThread();
		MarkupModel markup = DocumentMarkupModel.forDocument(document, project, false);
		return markup.getUserData(RAILS_MARKERS_IN_EDITOR_DOCUMENT_KEY);
	}

	public static void setRubyLineMarkers(final Document document, final RailsLineMarkerInfo[] lineMarkers, final Project project)
	{
		ApplicationManager.getApplication().assertIsDispatchThread();
		MarkupModel markup = DocumentMarkupModel.forDocument(document, project, false);
		markup.putUserData(RAILS_MARKERS_IN_EDITOR_DOCUMENT_KEY, lineMarkers);
	}
}