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
package org.hydra.startup;

import org.hydra.utilities.Logger;

public class StartUpCLI {
	/**
	 * Main Method for execution it exits with the standard exit codes an may be
	 * used for command scripting (0) is success and (1) is failure.
	 * 
	 * @param args
	 *            String[].
	 */
	public static void main(final String[] args) {
		try {
			final StarterCLI starter = new StarterCLI(args);
			starter.start();
			System.exit(0);
		} catch (final Exception e) {
			Logger.getInstance().critical(
					"FAILURE: System Unable to Initialize. Exiting...", e);
			System.out.println("");
			System.exit(1);
		}
	}

	/**
	 * Main Method that returns an int based on success(0) or failure (1) and is
	 * useful as a subprogram within another java program or a JUnit test.
	 * 
	 * @param args
	 *            String[].
	 * @return success - int.
	 */
	public static int intReturningMain(final String[] args) {
		try {
			final StarterCLI starter = new StarterCLI(args);
			starter.start();
			return 0;
		} catch (final Exception e) {
			Logger.getInstance().critical(
					"FAILURE: System Unable to Initialize. Exiting...", e);
			return 1;
		}

	}
}
