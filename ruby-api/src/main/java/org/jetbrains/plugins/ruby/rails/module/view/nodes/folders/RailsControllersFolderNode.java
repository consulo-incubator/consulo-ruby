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

package org.jetbrains.plugins.ruby.rails.module.view.nodes.folders;

import javax.annotation.Nonnull;

import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.rails.module.view.RailsNodeVisitor;
import org.jetbrains.plugins.ruby.rails.module.view.RailsProjectNodeComparator;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.treeStructure.SimpleNodeVisitor;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 29.09.2006
 */
public class RailsControllersFolderNode extends ControllerSubFolderNode
{
	private static final String CONTROLLERS_VIEW_NAME = RBundle.message("rails.project.module.view.nodes.controllers.presentable");

	public RailsControllersFolderNode(final Module module, final VirtualFile controllersRoot)
	{
		super(module, controllersRoot, null, initPresentationData());
	}

	private static PresentationData initPresentationData()
	{
		return new PresentationData(CONTROLLERS_VIEW_NAME, CONTROLLERS_VIEW_NAME, RailsIcons.RAILS_CONTROLERS_NODES, null);
	}

	@Override
	public void accept(final SimpleNodeVisitor visitor)
	{
		if(visitor instanceof RailsNodeVisitor)
		{
			((RailsNodeVisitor) visitor).visitControllerNode();
			return;
		}
		super.accept(visitor);

	}

	@Override
	@Nonnull
	public RailsProjectNodeComparator.NodeType getType()
	{
		return RailsProjectNodeComparator.NodeType.SPECIAL_FOLDER;
	}
}