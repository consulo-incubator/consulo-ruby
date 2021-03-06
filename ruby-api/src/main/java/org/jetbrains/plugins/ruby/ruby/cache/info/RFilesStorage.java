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

package org.jetbrains.plugins.ruby.ruby.cache.info;

import java.io.Serializable;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg, Roman Chernyatchik
 * @date: 21.10.2006
 */

/**
 * Info about files storage
 */
public interface RFilesStorage extends Serializable
{
	public enum FileStatus
	{
		NOT_FOUND,
		OBSOLETTE,
		UP_TO_DATE
	}

	public void init(@Nonnull final Project project);

	@Nullable
	public RFileInfo getInfoByUrl(@Nonnull final String url);

	@Nullable
	public RFileInfo removeInfoByUrl(@Nonnull final String url);

	public void addRInfo(@Nonnull final RFileInfo rInfo);

	@Nonnull
	public Set<String> getAllUrls();

	public FileStatus getFileStatus(@Nonnull final VirtualFile file);

	/**
	 * @param url Url to check for existance
	 * @return true if this url contains in storage, even if corresponding RFileInfo is null now
	 *         not updated yet
	 */
	public boolean containsUrl(@Nonnull final String url);

	/**
	 * Adds url to file storage, even if file info will be generated later.
	 *
	 * @param url Url to add
	 */
	void addUrl(@Nonnull String url);
}
