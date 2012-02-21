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
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * The Class HydraTest.
 */
public class StartUpCLITest {

	/**
	 * Start class test.
	 */
	@BeforeClass
	public static void startClassTest() {
		TH.setupLogging();
		Logger.getInstance().clearLog();
		Logger.getInstance().info("HYDRA TEST");
	}

	/**
	 * Setup test.
	 */
	@Before
	public void setupTest() {
		TH.setupLogging();
	}

	/**
	 * Hydra_without directory arg.
	 */
	@Test
	public void hydra_withoutDirectoryArg() {
		final String[] args = { "--v", "0", "null" };
		Assert.assertEquals("Should Return 0 as Successful Exit.", 0,
				StartUpCLI.intReturningMain(args));
	}

	/**
	 * Hydra_with args.
	 */
	@Test
	public void hydra_withArgs() {
		TH.setupTestingEnvironment(true, true);
		final String[] args = { "--v", "0", "--cwd", TH.workspace.getPath(),
				"help" };
		Assert.assertEquals("Should Return 0 as Successful Exit.", 0,
				StartUpCLI.intReturningMain(args));
	}

	/**
	 * Hydra_initialize.
	 */
	@Test
	public void hydra_initialize() {
		TH.setupTestingEnvironment(true, true);
		final String[] args = { "--v", "0", "--cwd", TH.workspace.getPath(),
				"--initialize", "exit" };
		Assert.assertEquals("Should Return 0 as Successful Exit.", 0,
				StartUpCLI.intReturningMain(args));
	}

	/**
	 * Hydra_incorrect cwd.
	 */
	@Test
	public void hydra_incorrectCWD() {
		TH.setupTestingEnvironment(true, true);
		final String[] args = { "--v", "0", "--cwd", "../../..", "help" };
		Assert.assertEquals("Should Return 0 as Successful Exit.", 1,
				StartUpCLI.intReturningMain(args));
	}

}
