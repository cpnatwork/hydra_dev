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

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.hydra.core.Configuration;
import org.hydra.core.InvalidElementException;
import org.hydra.core.Stage;
import org.hydra.ui.UI;
import org.hydra.ui.UIWriter;
import org.hydra.ui.commands.CmdUnrecognized;
import org.hydra.ui.commands.Command;
import org.hydra.ui.commands.CommandCLI;
import org.hydra.ui.commands.CommandRegex;
import org.hydra.ui.commands.CommandSet;

/**
 * Command line User Interface which is responsible for receiving input from a
 * user and printing command results to the terminal or command line.
 *
 * @since 0.1
 * @version 0.2
 * @author Scott A. Hady
 */
public class CLI extends UI {

	/** The config. */
	private Configuration config = null;

	/** The writer. */
	private UIWriter writer = null;

	/**
	 * Default Constructor.
	 *
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public CLI() throws InvalidElementException {
		super();
		this.config = Configuration.getInstance();
		this.writer = UIWriter.getInstance();
	}

	/**
	 * Specialized constructor that takes the state and the command set to be
	 * used.
	 *
	 * @param stage
	 *            Stage.
	 * @param commands
	 *            CommandSet.
	 */
	public CLI(final Stage stage, final CommandSet commands) {
		super(stage, commands);
		this.config = Configuration.getInstance();
		this.writer = UIWriter.getInstance();
	}

	/**
	 * UI METHODS (OVERRIDDEN)
	 * *************************************************.
	 * 
	 * @param commandLine
	 *            the command line
	 * @return the command
	 */

	/**
	 * Return the command in the command list that accept the given command
	 * string.
	 *
	 * @param commandLine
	 *            String.
	 * @return acceptingCommand - CommandCLI.
	 */
	public CommandCLI getCommand(final String commandLine) {
		for (final Command cmd : this.commands.toArray()) {
			if (cmd instanceof CommandCLI) {
				((CommandCLI) cmd).setCommandLine(commandLine);
				if (((CommandCLI) cmd).recognizes())
					return (CommandCLI) cmd;
			}
		}
		return new CmdUnrecognized(commandLine);
	}

	/**
	 * Return a string representaion of the command prompt.
	 *
	 * @return commandPrompt - String.
	 */
	public String getCommandPrompt() {
		return "[HVS:" + this.config.getWorkspace().getName() + "]"
				+ this.config.calculatePathExtension() + " >";
	}

	/**
	 * Request and accept user input from the command line.
	 *
	 * @return commandLine - String.
	 */
	public String getInput() {
		final BufferedReader br = new BufferedReader(new InputStreamReader(
				System.in));
		this.writer.print(this.getCommandPrompt());
		try {
			return br.readLine();
		} catch (final Exception e) {
			this.logger.critical("Unable to Get Input", e);
			return "help";
		}
	}

	/**
	 * Return the user interface's writer.
	 *
	 * @return writer - UIWriter.
	 */
	public UIWriter getWriter() {
		return this.writer;
	}

	/**
	 * Execute a single command from a given string.
	 *
	 * @param commandLine
	 *            String.
	 * @return executedCommand - Command.
	 */
	public CommandCLI executeCommand(final String commandLine) {
		final CommandCLI cmd = this.getCommand(commandLine);
		if (cmd.process()) {
			cmd.execute();
		} else {
			this.writer.println(cmd.getProcessingFailure(),
					Command.DEFAULT_VERBOSITY);
		}
		return cmd;
	}

	/**
	 * UI METHODS (OVERRIDDEN) ************************************************.
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Start interacting with user over command line.
	 */
	@Override
	public void interact() {
		CommandCLI cmd = new CommandRegex(CommandRegex.DEFAULT_NAME,
				CommandRegex.DEFAULT_ID);
		while (!cmd.exits()) {
			cmd = this.executeCommand(this.getInput());
		}
	}

}
