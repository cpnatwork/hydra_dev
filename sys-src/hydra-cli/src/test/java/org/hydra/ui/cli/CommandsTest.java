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
package org.hydra.ui.cli;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.hydra.core.InvalidElementException;
import org.hydra.core.Stage;
import org.hydra.ui.UIWriter;
import org.hydra.ui.commands.CmdStatus;
import org.hydra.ui.commands.CommandCLI;
import org.hydra.utilities.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * The Class CommandsTest.
 */
public class CommandsTest {

	/** The cmd. */
	CommandCLI cmd;

	/** The stage. */
	Stage stage;

	/**
	 * Start class test.
	 * 
	 * @throws FileNotFoundException
	 *             the file not found exception
	 */
	@BeforeClass
	public static void startClassTest() throws FileNotFoundException {
		TH.setupLogging();
		Logger.getInstance().info("COMMANDS TEST");
		UIWriter.setOutStream(new PrintStream(new FileOutputStream(TH.clioFile)));
	}

	/**
	 * After class.
	 */
	@AfterClass
	public static void afterClass() {
		UIWriter.setOutStream(System.out);
	}

	/**
	 * Setup test.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Before
	public void setupTest() throws InvalidElementException {
		TH.setupTestingEnvironment(true, true);
		this.stage = new Stage();
		TH.setupLogging();
	}

	/**
	 * COMMANDS ***************************************************************.
	 */

	/**
	 * Cmd_ cmd status.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void cmd_CmdStatus() throws InvalidElementException {
		final CLI cli = new CLI();
		this.cmd = new CmdStatus(cli);
		Assert.assertTrue("1 Did Not Succeed.", this.cmd.execute());
		this.cmd.setVerbosity(1);
		Assert.assertTrue("2 Did Not Succeed.", this.cmd.execute());
		// Accepts
		Assert.assertTrue("3 Doesn't Accept 'status'.",
				this.cmd.accepts("status"));
		Assert.assertTrue("3 Doesn't Accept 's'.", this.cmd.accepts("s"));
		// Not Accepts
		Assert.assertFalse("4 Accepts 'other'.", this.cmd.accepts("other"));
		Assert.assertFalse("4 Accepts 'status one'.",
				this.cmd.accepts("status one"));
		// Check Not Exits & Usage
		Assert.assertFalse("5 Exits.", this.cmd.exits());
		Assert.assertTrue("5 Usage.",
				this.cmd.getUsage().startsWith("status(s)"));
	}

}
