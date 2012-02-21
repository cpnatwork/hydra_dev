/**************************************************************************
 * Hydra: multi-headed version control system
 * (originally for the alpha-Flow project)
 * ==============================================
 * Copyright (C) 2009-2012 by 
 *   - Christoph P. Neumann (http://www.chr15t0ph.de)
 *   - Scott Hady
 **************************************************************************
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 **************************************************************************
 * $Id$
 *************************************************************************/
package org.hydra.persistence;

import java.io.File;

/**
 * Interface defining the storage functionality needed to support the DAOs.
 *
 * @author Scott A. Hady
 * @version 0.2
 * @since 0.2
 */
public interface StorageStrategy {

	/**
	 * Transfer the contents of a file in the workspace to the repository.
	 *
	 * @param workspaceFile
	 *            File.
	 * @param repositoryFile
	 *            File.
	 * @return success - boolean.
	 */
	public boolean transferToRepository(File workspaceFile, File repositoryFile);

	/**
	 * Transfer the contents of a file from the repository to the workspace.
	 *
	 * @param workspaceFile
	 *            File.
	 * @param repositoryFile
	 *            File.
	 * @return success - boolean.
	 */
	public boolean transferFromRepository(File workspaceFile,
			File repositoryFile);

}
