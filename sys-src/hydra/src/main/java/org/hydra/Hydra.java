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
package org.hydra;

import org.hydra.persistence.DataAccessObject;

/**
 * Main point of entry for the hydra program.
 * 
 * @author Scott A. Hady
 * @version 0.2
 * @since 0.1
 */
public class Hydra {

	/** The Constant NAME. */
	private static final String NAME = "Hydra";

	/** The Constant DESCRIPTION. */
	private static final String DESCRIPTION = "Multi-Headed Version Control System";

	/** The Constant VERSION. */
	private static final String VERSION = "0.2";

	/** The Constant BUILD_NUMBER. */
	private static final String BUILD_NUMBER = "201107-01";

	/**
	 * Default Constructor with no parameters.
	 */
	public Hydra() {

	}

	/**
	 * Retrieve the program's name.
	 * 
	 * @return name - String.
	 */
	public static String getName() {
		return Hydra.NAME;
	}

	/**
	 * Retrieve a short description of the program.
	 * 
	 * @return description - String.
	 */
	public static String getDescription() {
		return Hydra.DESCRIPTION;
	}

	/**
	 * Retrieve the current verison number.
	 * 
	 * @return version - String.
	 */
	public static String getVersion() {
		return Hydra.VERSION;
	}

	/**
	 * Retrieve the build reference number for this version.
	 * 
	 * @return buildNumber - String.
	 */
	public static String getBuildNumber() {
		return Hydra.BUILD_NUMBER;
	}

	/**
	 * Retrieve a short header for the program.
	 * 
	 * @return shortHeader - String.
	 */
	public static String getShortHeader() {
		return Hydra.NAME + " [v" + Hydra.VERSION + " - " + Hydra.BUILD_NUMBER
				+ "]";
	}

	/**
	 * Retrieve a long header for the prgram.
	 * 
	 * @return longHeader - String.
	 */
	public static String getLongHeader() {
		return Hydra.NAME + " - " + Hydra.DESCRIPTION + "\nVersion: "
				+ Hydra.VERSION + " Build: " + Hydra.BUILD_NUMBER
				+ " Storage: " + DataAccessObject.STORAGE_STRATEGY;
	}

}
