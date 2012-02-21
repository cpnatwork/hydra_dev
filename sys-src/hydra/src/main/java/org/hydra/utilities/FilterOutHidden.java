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
package org.hydra.utilities;

import java.io.File;
import java.io.FileFilter;

/**
 * Implements the FileFilter interface and may be used to list both the
 * subdirectories that are not the hydra repository and files that are not
 * hidden in a given directory.
 *
 * @since 0.1
 * @author Scott A. Hady
 * @version $Id$
 */
public class FilterOutHidden implements FileFilter {

	/** The repository name. */
	private final String repositoryName = ".hydra";

	/**
	 * {@inheritDoc}
	 *
	 * Returns whether the given target is a directory and not the repository or
	 * not.
	 */
	@Override
	public boolean accept(final File file) {
		return (!file.getName().equals(this.repositoryName) && !file.isHidden());
	}

}
