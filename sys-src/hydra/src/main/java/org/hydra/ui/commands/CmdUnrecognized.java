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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Notifies the user that the given command was not recognized.
 *
 * @since 0.1
 * @version 0.2
 * @author Scott A. Hady
 */
public class CmdUnrecognized extends CommandSystem {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The Constant DEFAULT_NAME. */
	public static final String DEFAULT_NAME = "Unrecognized Command";

	/** The Constant DEFAULT_ID. */
	public static final String DEFAULT_ID = "CmdUnrecognized";

	// Regular Expressions
	/** The cmd reg ex. */
	private final String cmdRegEx = "^.*$";

	/** The cmd pattern. */
	private final Pattern cmdPattern = Pattern.compile(this.cmdRegEx);

	/**
	 * Specialized Constructor, specifies the command string that was not
	 * recognized.
	 *
	 * @param commandLine
	 *            String.
	 */
	public CmdUnrecognized(final String commandLine) {
		super(CmdUnrecognized.DEFAULT_NAME, CmdUnrecognized.DEFAULT_ID);
		this.setCommandLine(commandLine);
	}

	/**
	 * COMMAND METHODS OVERRIDDEN *********************************************.
	 * 
	 * @return the command pattern
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Command Pattern recognizes any command string.
	 */
	@Override
	public Pattern getCommandPattern() {
		return this.cmdPattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Complete Pattern matches same as the command pattern.
	 */
	@Override
	public Pattern getCompletePattern() {
		return this.getCommandPattern();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Receive the entire command string.
	 */
	@Override
	public boolean processMatcher(final Matcher matcher) {
		this.setCommandLine(matcher.group(0));
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Print notice that the command string was not recognized.
	 */
	@Override
	public boolean execute() {
		this.logger.info("Unrecognized Command [" + this.getCommandLine()
				+ "].");
		this.writer.println("Unrecognized Command [" + this.getCommandLine()
				+ "].", this.cmdVerbosity);
		this.writer
				.println(
						"\tUse \"help\" to view list of possible commands and arguments.\n",
						this.cmdVerbosity);
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return a string describing the commands usage.
	 */
	@Override
	public String getUsage() {
		return "<unknown-cmdStr>\t\t\t\t\tNotifies User that Command String was not Recognized.";
	}

}
