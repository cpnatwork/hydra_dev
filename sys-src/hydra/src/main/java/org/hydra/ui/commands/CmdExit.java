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

import java.awt.event.ActionEvent;
import java.util.regex.Pattern;

/**
 * Informs the CLI that it should exit, however the command must be executed
 * before it returns true for {@link #exits} and once it has executed it will
 * always return true - indempotent.
 *
 * @since 0.1
 * @version 0.2
 * @author Scott A. Hady
 */
public class CmdExit extends CommandSystem {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The Constant DEFAULT_NAME. */
	public static final String DEFAULT_NAME = "Exit";

	/** The Constant DEFAULT_ID. */
	public static final String DEFAULT_ID = "CmdExit";

	/** The exit flag. */
	boolean exitFlag = false;
	// Regular Expressions
	/** The cmd reg ex. */
	private final String cmdRegEx = "^\\s*(?i:e|exit|q|quit)\\b\\s*";

	/** The cmd pattern. */
	private final Pattern cmdPattern = Pattern.compile(this.cmdRegEx);

	/**
	 * Default Constructor that initializes the name of the command that will
	 * appear in the menu item.
	 */
	public CmdExit() {
		super(CmdExit.DEFAULT_NAME, CmdExit.DEFAULT_ID);
	}

	/**
	 * COMMAND METHODS OVERRIDDEN *********************************************.
	 * 
	 * @return the command pattern
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Command pattern accepts e, exit, q or quit.
	 */
	@Override
	public Pattern getCommandPattern() {
		return this.cmdPattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Complete pattern accepts same as command pattern.
	 */
	@Override
	public Pattern getCompletePattern() {
		return this.getCommandPattern();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Notifies the System to exit.
	 */
	@Override
	public boolean execute() {
		this.exitFlag = true;
		this.writer.println("Auf Wiedersehen!", this.cmdVerbosity);
		this.writer.println(
				"Hydra Version Control System Exiting...\nGoodbye!",
				this.cmdVerbosity);
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Notifies the system that it should exit.
	 */
	@Override
	public boolean exits() {
		return this.exitFlag;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return a string describing the commands usage.
	 */
	@Override
	public String getUsage() {
		return "exit(e)|quit(q)\t\t\t\tExits from HVS CLI.";
	}

	/**
	 * ABSTRACTACTION METHODS (OVERRIDDEN) ************************************.
	 * 
	 * @param event
	 *            the event
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Respond to a given UI action being performed.
	 */
	@Override
	public void actionPerformed(final ActionEvent event) {
		this.execute();
		System.exit(0);
	}
}
