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

import java.io.FileOutputStream;
import java.io.PrintStream;

import org.hydra.core.InvalidElementException;
import org.hydra.core.Stage;
import org.hydra.ui.UIWriter;
import org.hydra.ui.commands.CommandCLI;
import org.hydra.ui.commands.CommandSet;
import org.hydra.ui.commands.SCmdCommit;
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
	 * Cli_ creation.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void cli_Creation() throws InvalidElementException {
		this.cli = new CLI();
		final CommandCLI cmd = this.cli.getCommand("unknown command");
	}

	/**
	 * Cli_ creation2.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void cli_Creation2() throws InvalidElementException {
		this.cli = new CLI(new Stage(), new CommandSet());
	}

	/**
	 * Cli_get and set verbosity.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void cli_getAndSetVerbosity() throws InvalidElementException {
		this.cli = new CLI();
		this.cli.setVerbosity(7);
		Assert.assertEquals("Verbosity Should be 7.", 7,
				this.cli.getVerbosity());
	}

	/**
	 * Cli_get command prompt.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void cli_getCommandPrompt() throws InvalidElementException {
		this.cli = new CLI();
		Assert.assertEquals("Should be autotest", "[HVS:autotest] >",
				this.cli.getCommandPrompt());
	}

	/**
	 * Cli_failed command.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void cli_failedCommand() throws InvalidElementException {
		this.cli = new CLI();
		Assert.assertTrue(this.cli.executeCommand("scommit xxx") instanceof SCmdCommit);
	}

	/**
	 * Cloi_ write to file.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void cloi_WriteToFile() throws Exception {
		this.cli = new CLI();
		final UIWriter writer = UIWriter.getInstance();
		Assert.assertNotNull("UIWriter is Null.", writer);
		final PrintStream outStream = new PrintStream(new FileOutputStream(
				TH.clioFile));
		UIWriter.setOutStream(outStream);
		Assert.assertEquals("Should Be Same OuputStream.", outStream,
				UIWriter.getOutStream());
		writer.println("Here is some Text.");
		writer.print("Here is some other Text.");

	}
}
