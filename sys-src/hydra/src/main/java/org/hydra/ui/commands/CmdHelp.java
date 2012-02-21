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

import java.util.regex.Pattern;

import org.hydra.Hydra;

/**
 * Prints Help Menu and returns success.
 *
 * @since 0.1
 * @version 0.2
 * @author Scott A. Hady
 */
public class CmdHelp extends CommandSystem {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The Constant DEFAULT_NAME. */
	public static final String DEFAULT_NAME = "CLI Usage";

	/** The Constant DEFAULT_ID. */
	public static final String DEFAULT_ID = "CmdHelp";

	/** The commands. */
	private final CommandSet commands;
	// Regular Expressions
	/** The cmd reg ex. */
	private final String cmdRegEx = "^\\s*(?i:help|h)\\b\\s*";

	/** The cmd pattern. */
	private final Pattern cmdPattern = Pattern.compile(this.cmdRegEx);

	/**
	 * Default Constructor.
	 */
	public CmdHelp() {
		super(CmdHelp.DEFAULT_NAME, CmdHelp.DEFAULT_ID);
		this.commands = new CommandSet();
	}

	/**
	 * Specialized Constructor that accepts the command list to get usage
	 * information from.
	 *
	 * @param commands
	 *            CommandSet.
	 */
	public CmdHelp(final CommandSet commands) {
		super(CmdHelp.DEFAULT_NAME, CmdHelp.DEFAULT_ID);
		this.commands = commands;
	}

	/**
	 * COMMAND METHODS OVERRIDDEN *********************************************.
	 * 
	 * @return the command pattern
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Command Pattern recognizes either 'help' or 'h' as a command string.
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
	 * Prints help menu.
	 */
	@Override
	public boolean execute() {
		final String VER = Hydra.getVersion();
		final StringBuilder sb = new StringBuilder("");
		sb.append(Hydra.getLongHeader() + "\n\n");
		sb.append("Usage:\n");
		sb.append("\tjava -classpath <path>hydra-" + VER
				+ ".jar org.hydra.Hydra <params> <cmd>\n");
		sb.append("\tjava -jar hydra-" + VER + ".jar <params> <cmd>\n\n");
		sb.append("\tExample:\tjava -jar hydra-" + VER
				+ ".jar --cwd temp/manualtest --v 5 help\n\n");
		sb.append("Params:\n");
		sb.append("\t--cli\t\tCLI - Interactive Mode\n");
		sb.append("\t--gui\t\tGUI - Interatctive Mode\n");
		sb.append("\t\t\t\t*If No Command Is Provide CLI Interactive Mode is Assumed.\n");
		sb.append("\t--cwd <path>\tSet Current Workspace Directory\n");
		sb.append("\t--initialize\tIntialize New Repository\n");
		sb.append("\t--v <0-10>\tSystem Verbosity\n\n");
		sb.append("Commands:\n");
		for (final Command c : this.commands.toArray()) {
			if (c instanceof CommandCLI) {
				sb.append("\t" + ((CommandCLI) c).getUsage() + "\n");
			}
		}
		this.writer.println(sb.toString(), this.cmdVerbosity);
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return a string describing the commands usage.
	 */
	@Override
	public String getUsage() {
		return "help(h)\t\t\t\t\tPrints this Help Menu.";
	}

}
