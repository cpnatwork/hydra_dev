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

/**
 * Interface for commandline interface commands. The typical processing order
 * these commands are {@link #setCommandLine}, {@link #recognizes},
 * {@link #process} then {@link #execute} or {@link #getProcessingFailure}
 * depending on the processing result.
 *
 * @since 0.2
 * @version 0.2
 * @author Scott A. Hady
 */
public abstract class CommandCLI extends Command {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4512774647682767817L;
	/** The cmd line. */
	protected String cmdLine = null;

	/**
	 * Specialized constructor that specifies the commands name and id.
	 *
	 * @param commandName
	 *            String.
	 * @param commandId
	 *            String.
	 */
	public CommandCLI(final String commandName, final String commandId) {
		super(commandName, commandId);
	}

	/**
	 * COMMANDCLI METHODS *****************************************************.
	 * 
	 * @param commandLine
	 *            the command line
	 * @return true, if successful
	 */

	/**
	 * Convienence Method - Combines {@link #setCommandLine},
	 * {@link #recognizes}, and {@link #process}.
	 *
	 * @param commandLine
	 *            String.
	 * @return processingSuccess - boolean.
	 */
	public boolean accepts(final String commandLine) {
		this.setCommandLine(commandLine);
		if (this.recognizes() && this.process())
			return true;
		else
			return false;
	}

	/**
	 * Return the command string that the command is currently manipulating.
	 *
	 * @return commandLine - String.
	 */
	public String getCommandLine() {
		return this.cmdLine;
	}

	/**
	 * Sets the command's string to be manipulated, logically the first method
	 * in the processing chain.
	 *
	 * @param commandLine
	 *            String.
	 */
	public void setCommandLine(final String commandLine) {
		this.cmdLine = commandLine;
	}

	/**
	 * COMMANDCLI ABSTRACT METHODS ********************************************.
	 * 
	 * @return true, if successful
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Executes the processed command, logically follows {@link #process} when
	 * it succeeds and returns the success of the command's execution.
	 */
	@Override
	public abstract boolean execute();

	/**
	 * Determines if the command signals that the program should exit.
	 *
	 * @return shouldExit - boolean.
	 */
	public abstract boolean exits();

	/**
	 * Returns a string describing the processing failure that occured,
	 * logically follows {@link #process} when it returns success.
	 *
	 * @return processingFailure - String.
	 */
	public abstract String getProcessingFailure();

	/**
	 * Returns a string describing the acceptable command line for this command.
	 *
	 * @return acceptableCommandLine - String.
	 */
	public abstract String getUsage();

	/**
	 * Attempts to process the given command string to set the command's
	 * parameters from the information present in the command line. Logically
	 * follows either {@link #recognizes} or {@link #setCommandLine} and
	 * preceeds either {@link #execute} or {@link #getProcessingFailure}.
	 *
	 * @return processingSuccess - boolean.
	 */
	public abstract boolean process();

	/**
	 * Determines if the base command is correct, it does not provide an
	 * assessment of the appropriateness of the command string's parameters.
	 * Logically follows {@link #setCommandLine} and preceeds {@link #process}.
	 *
	 * @return recognitionSuccess - boolean.
	 */
	public abstract boolean recognizes();

}
