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

/**
 * Provides some basic file system utilities for manipulating a file.
 *
 * @author Scott A. Hady
 * @version 0.2
 * @since 0.2
 */
public class FileUtilities {

	/**
	 * Find a canonical path for a given location.
	 *
	 * @param targetLocation
	 *            File.
	 * @return canonicalPath - String.
	 */
	public static String findCanonicalPath(final File targetLocation) {
		try {
			return targetLocation.getCanonicalPath();
		} catch (final Exception e) {
			Logger.getInstance().exception(
					"Unable to Determine Canonical Path for Target ["
							+ targetLocation + "].", e);
			return null;
		}
	}

	/**
	 * Find the canonical location for a given location.
	 *
	 * @param targetLocation
	 *            File.
	 * @return canonicalLocation - File.
	 */
	public static File findCanonicalLocation(final File targetLocation) {
		try {
			return targetLocation.getCanonicalFile();
		} catch (final Exception e) {
			Logger.getInstance().exception(
					"Unable to Determine Canonical Location for Target ["
							+ targetLocation + "].", e);
			return null;
		}
	}

	/**
	 * Find the subpath from a parent path to a child path.
	 *
	 * @param targetParentPath
	 *            String.
	 * @param targetChildPath
	 *            String.
	 * @return subPath String.
	 */
	public static String findSubPath(final String targetParentPath,
			final String targetChildPath) {
		return targetChildPath.replace(targetParentPath, "");
	}

	/**
	 * Find the subpath from a parent location to a child location.
	 *
	 * @param targetParentLocation
	 *            File.
	 * @param targetChildLocation
	 *            File.
	 * @return subPath - String.
	 */
	public static String findSubPath(final File targetParentLocation,
			final File targetChildLocation) {
		return FileUtilities.findSubPath(
				FileUtilities.findCanonicalPath(targetParentLocation),
				FileUtilities.findCanonicalPath(targetChildLocation));
	}

}
