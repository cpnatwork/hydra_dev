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
import org.hydra.core.Configuration;
import org.hydra.ui.UI;

/**
 * Displays the UI's status.
 *
 * @since 0.1
 * @version 0.2
 * @author Scott A. Hady
 */
public class CmdStatus extends CommandSystem {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The Constant DEFAULT_NAME. */
	public static final String DEFAULT_NAME = "System Status";

	/** The Constant DEFAULT_ID. */
	public static final String DEFAULT_ID = "CmdStatus";

	/** The ui. */
	private UI ui;
	// Regular Expressions
	/** The cmd reg ex. */
	private final String cmdRegEx = "^\\s*(?i:s|status)\\b\\s*";

	/** The cmd pattern. */
	private final Pattern cmdPattern = Pattern.compile(cmdRegEx);

	/**
	 * Specialized Constructor for specifying which UI to affect.
	 *
	 * @param ui
	 *            UI.
	 */
	public CmdStatus(UI ui) {
		super(DEFAULT_NAME, DEFAULT_ID);
		this.ui = ui;
	}

	/**
	 * COMMAND METHODS OVERRIDDEN *********************************************.
	 * 
	 * @return the command pattern
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Command Pattern recognizes either 's' or 'status' as a command string.
	 */
	@Override
	public Pattern getCommandPattern() {
		return cmdPattern;
	}

	/**
	 * Complete Pattern matches same as the command pattern.
	 *
	 * @return completePattern - Pattern.
	 */
	public Pattern getCompletePattern() {
		return getCommandPattern();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Dispaly System's status.
	 */
	@Override
	public boolean execute() {
		StringBuilder sb = new StringBuilder("Hydra System Status:\n");
		sb.append("--------------------------------------------------\n");
		sb.append("  Version:\t" + Hydra.getVersion() + "\n");
		sb.append("  Build Nr:\t" + Hydra.getBuildNumber() + "\n");
		sb.append("  Verbosity:\t" + writer.getVerbosity() + "\n");
		sb.append("  User ID:\t" + Configuration.getInstance().getUserId()
				+ "\n");
		sb.append("--------------------------------------------------\n");
		sb.append(logger.getStatus());
		sb.append("--------------------------------------------------\n");
		if (ui.getStage().isFocused()) {
			sb.append("  Stage Focus:\t" + ui.getStage().getFocus().getName()
					+ "\n");
		} else {
			sb.append("  Stage Focus:\tnull\n");
		}
		writer.println(sb.toString(), this.cmdVerbosity);
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return a string describing the commands usage.
	 */
	@Override
	public String getUsage() {
		return "status(s)\t\t\t\tDisplay System's Status.";
	}

}
