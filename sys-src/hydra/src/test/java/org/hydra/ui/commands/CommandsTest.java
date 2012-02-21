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
package org.hydra.ui.commands;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.hydra.TH;
import org.hydra.core.Configuration;
import org.hydra.core.InvalidElementException;
import org.hydra.core.Stage;
import org.hydra.ui.UIWriter;
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

	@Test
	public void cmd_Base() {
		this.cmd = new CommandRegex(CommandRegex.DEFAULT_NAME,
				CommandRegex.DEFAULT_ID);
		this.cmd.setVerbosity(0);
		Assert.assertFalse("CMD Should Fail.", this.cmd.execute());
		Assert.assertTrue("CMD Doesn't Accepts ''.", this.cmd.accepts(""));
		Assert.assertFalse("CMD Exits.", this.cmd.exits());
		Assert.assertTrue("CMD Usage.",
				this.cmd.getUsage().endsWith("Null Command Functionality."));
	}

	/**
	 * Cmd_ unrecognized.
	 */
	@Test
	public void cmd_Unrecognized() {
		this.cmd = new CmdUnrecognized("monkey command");
		Assert.assertFalse("CMDUnrecognized Should Fail.", this.cmd.execute());
		this.cmd.setVerbosity(1);
		Assert.assertFalse("CMDUnrecognized Should Fail.", this.cmd.execute());
		Assert.assertTrue("CMDUnrecognized Doesn't Accepts 'monkey command'.",
				this.cmd.accepts("monkey command"));
		Assert.assertFalse("CMDUnrecognized Exits.", this.cmd.exits());
		Assert.assertTrue("CMD Usage.",
				this.cmd.getUsage().startsWith("<unknown-cmdStr>"));
	}

	/**
	 * Cmd_ help.
	 */
	@Test
	public void cmd_Help() {
		this.cmd = new CmdHelp();
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		this.cmd.setVerbosity(1);
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		// Check Acceptable
		Assert.assertTrue("Doesn't Accepts help.", this.cmd.accepts("help"));
		Assert.assertTrue("Doesn't Accepts h.", this.cmd.accepts("h"));
		Assert.assertTrue("Doesn't Accept ' HELP '.",
				this.cmd.accepts(" HELP "));
		// Check Unacceptable
		Assert.assertFalse("Accepts 'other'.", this.cmd.accepts("other"));
		Assert.assertFalse("Exits.", this.cmd.exits());
		final CommandSet commands = new CommandSet();
		commands.add(new CommandRegex(CommandRegex.DEFAULT_NAME,
				CommandRegex.DEFAULT_ID));
		commands.add(new CmdUnrecognized("unrecognized command"));
		commands.add(new CmdHelp());
		this.cmd = new CmdHelp(commands);
		this.cmd.setVerbosity(1);
		Assert.assertTrue("Usage.", this.cmd.execute());
	}

	/**
	 * Cmd_ exit.
	 */
	@Test
	public void cmd_Exit() {
		this.cmd = new CmdExit();
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		this.cmd.setVerbosity(1);
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		// Check Acceptable
		Assert.assertTrue("Doesn't Accept 'exit'.", this.cmd.accepts("exit"));
		Assert.assertTrue("Doesn't Accept 'e'.", this.cmd.accepts("e"));
		Assert.assertTrue("Doesn't Accept 'quit'.", this.cmd.accepts("quit"));
		Assert.assertTrue("Doesn't Accept 'q'.", this.cmd.accepts("q"));
		Assert.assertTrue("Doesn't Accepts ' exit '.",
				this.cmd.accepts(" exit "));
		Assert.assertTrue("Doesn't Accept 'EXIT'.", this.cmd.accepts("EXIT"));
		// Check Unacceptable
		Assert.assertFalse("Accepts 'other'.", this.cmd.accepts("other"));
		Assert.assertFalse("Accepts 'exit more'.",
				this.cmd.accepts("exit more"));
		Assert.assertTrue("Does Not Exit.", this.cmd.exits());
		Assert.assertTrue("Usage.",
				this.cmd.getUsage().startsWith("exit(e)|quit(q)"));
	}

	/**
	 * Cmd_ verbose.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void cmd_Verbose() throws InvalidElementException {
		this.cmd = new CmdVerbose(1);
		Assert.assertTrue("Return Success.", this.cmd.execute());
		this.cmd.setVerbosity(1);
		Assert.assertTrue("Return Success.", this.cmd.execute());
		Assert.assertEquals("TestUI Verbosity Not Set to 1.", 1,
				UIWriter.getVerbosity());
		Assert.assertTrue("Doesn't Accept 'verbosity 0'.",
				this.cmd.accepts("verbosity 0"));
		Assert.assertTrue("Doesn't Accept 'v 10'.", this.cmd.accepts("v 10"));
		Assert.assertFalse("Accepts 'other'.", this.cmd.accepts("other"));
		Assert.assertFalse("Exits.", this.cmd.exits());
		Assert.assertTrue("Usage.",
				this.cmd.getUsage().startsWith("verbosity(v)"));
	}

	/**
	 * Cmd_ list.
	 */
	@Test
	public void cmd_List() {
		this.cmd = new CmdList();
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		this.cmd.setVerbosity(1);
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		// Check Acceptable
		Assert.assertTrue("Doesn't Accept 'list'.", this.cmd.accepts("list"));
		Assert.assertTrue("Doesn't Accept 'ls'.", this.cmd.accepts("ls"));
		Assert.assertTrue("Doesn't Accept 'list -d1'.",
				this.cmd.accepts("list -d1"));
		Assert.assertTrue("Doesn't Accept 'list -d0 subDir'.",
				this.cmd.accepts("list -d0 subDir"));
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		Assert.assertTrue("Doesn't Accept 'list subDir'.",
				this.cmd.accepts("list subDir"));
		Assert.assertTrue("Doesn't Accept 'list subDir/'.",
				this.cmd.accepts("list subDir/"));
		Assert.assertTrue("Doesn't Accept 'list subDir\\'.",
				this.cmd.accepts("list subDir\\"));
		Assert.assertTrue("Doesn't Accept 'list -d1 subDir'.",
				this.cmd.accepts("list -d1 subDir"));
		Assert.assertTrue("Doesn't Accept 'list ..'.",
				this.cmd.accepts("list .."));
		Assert.assertTrue("Doesn't Accept 'list .'.",
				this.cmd.accepts("list ."));
		// Check Unacceptable
		Assert.assertFalse("Accepts 'list -dx'.", this.cmd.accepts("list -dx"));
		Assert.assertFalse("Accepts 'list-dx'.", this.cmd.accepts("list-dx"));
		Assert.assertFalse("Accepts 'other'.", this.cmd.accepts("other"));
		Assert.assertFalse("Accepts 'other two three four'.",
				this.cmd.accepts("other two three four"));
		Assert.assertFalse("Accepts 'listx'.", this.cmd.accepts("listx"));
		// Check Exit and Usage
		Assert.assertFalse("Exits.", this.cmd.exits());
		Assert.assertTrue("Usage.", this.cmd.getUsage().startsWith("list(ls)"));
	}

	/**
	 * Cmd_ log.
	 */
	@Test
	public void cmd_Log() {
		this.cmd = new CmdLog();
		this.cmd.setVerbosity(1);
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		// Check Acceptance
		Assert.assertTrue("Doesn't Accept 'log'.", this.cmd.accepts("log"));
		Assert.assertTrue("Doesn't Accept 'log 1'.", this.cmd.accepts("log 1"));
		Assert.assertTrue("Doesn't Accept 'log 100'.",
				this.cmd.accepts("log 100"));
		Assert.assertTrue("Doesn't Accept 'log 0'.", this.cmd.accepts("log 0"));
		Assert.assertTrue("Doesn't Accept '  log  10  '.",
				this.cmd.accepts("  log  10  "));
		// Check Rejection
		Assert.assertFalse("Accepts 'log -1'.", this.cmd.accepts("log -1"));
		Assert.assertFalse("Accepts 'log 1 1'.", this.cmd.accepts("log 1 1"));
		Assert.assertFalse("Accepts 'log 1.2'.", this.cmd.accepts("log 1.2"));
		// Check Exit & Usage
		Assert.assertFalse("log Exits.", this.cmd.exits());
		Assert.assertTrue("log Usage Incorrect.", this.cmd.getUsage()
				.startsWith("log {<num>}"));
	}

	/**
	 * Cmd_ set user.
	 */
	@Test
	public void cmd_SetUser() {
		final Configuration config = Configuration.getInstance();
		config.setUserId("unknown");
		Assert.assertEquals("Configuration UserID Not Changed.", "unknown",
				config.getUserId());
		this.cmd = new CmdSetUser("Scott");
		this.cmd.setVerbosity(1);
		Assert.assertTrue("1 Should Succeed.", this.cmd.execute());
		Assert.assertEquals("1 Configuration UserID Not Changed.", "Scott",
				config.getUserId());
		this.cmd = new CmdSetUser();
		Assert.assertTrue("2 Should Succeed.", this.cmd.execute());
		Assert.assertEquals("2 Configuration UserID Not Changed.", "unknown",
				config.getUserId());
		// Accept and Execute
		Assert.assertTrue("3 Doesn't Accept 'Scott'.",
				this.cmd.accepts("setUser Scott"));
		Assert.assertTrue("3 Should Succeed.", this.cmd.execute());
		Assert.assertEquals("3 Configuration UserID Not Changed.", "Scott",
				config.getUserId());
		// Not Acceptable ('\n' and '::>>')
		Assert.assertFalse("4 Accepts EOL.",
				this.cmd.accepts("setUser Scott\nHady"));
		Assert.assertFalse("4 Accepts SEPARATOR.",
				this.cmd.accepts("setUser Scott::>>Hady"));
		Assert.assertFalse("4 Accepts ''.", this.cmd.accepts("setUser"));
		// Check Not Exits & Usage
		Assert.assertFalse("5 Exits.", this.cmd.exits());
		Assert.assertTrue("5 Usage.",
				this.cmd.getUsage().startsWith("setUser <userId>"));
	}

}
