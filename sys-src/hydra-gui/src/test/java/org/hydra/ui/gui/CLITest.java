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
package org.hydra.ui.gui;

import org.hydra.core.InvalidElementException;
import org.hydra.core.Stage;
import org.hydra.ui.cli.CLI;
import org.hydra.ui.commands.CmdUnrecognized;
import org.hydra.ui.commands.CommandCLI;
import org.hydra.ui.commands.CommandSet;
import org.hydra.ui.gui.commands.GUICmdGraphRevert;
import org.hydra.utilities.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * The Class CLITest.
 */
public class CLITest {

	/** The cli. */
	CLI cli;

	/**
	 * Start class test.
	 */
	@BeforeClass
	public static void startClassTest() {
		TH.setupLogging();
		Logger.getInstance().info("CLI TEST");
	}

	/**
	 * Setup test.
	 */
	@Before
	public void setupTest() {
		TH.setupTestingEnvironment(true, true);
	}

	/**
	 * Cli_non cli command.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void cli_nonCLICommand() throws InvalidElementException {
		final CommandSet commands = new CommandSet();
		final Stage stage = new Stage();
		final GUICmdGraphRevert guiCmd = new GUICmdGraphRevert(
				new HydraExplorer(stage));
		commands.add(guiCmd);
		this.cli = new CLI(stage, commands);
		final CommandCLI command = this.cli.getCommand("No Valid Commands");
		Assert.assertTrue(command instanceof CmdUnrecognized);
		Assert.assertTrue(this.cli.executeCommand("No Valid Commands") instanceof CmdUnrecognized);
	}

}
