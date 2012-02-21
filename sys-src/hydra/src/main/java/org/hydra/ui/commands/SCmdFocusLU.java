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

import org.hydra.core.LogicalUnit;
import org.hydra.core.Stage;

/**
 * Focuses the stage on a specific logical unit.
 *
 * @since 0.1
 * @version 0.2
 * @author Scott A. Hady
 */
public class SCmdFocusLU extends CommandStage {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The Constant DEFAULT_NAME. */
	public static final String DEFAULT_NAME = "Stage Focus LogicalUnit";

	/** The Constant DEFAULT_ID. */
	public static final String DEFAULT_ID = "SCmdFocusLU";

	/** The lu name. */
	private String luName;
	// Regular Expression
	/** The cmd reg ex. */
	private final String cmdRegEx = "^\\s*(?i:sfocus)\\b";

	/** The lu reg ex. */
	private final String luRegEx = "\\s+(\\S.*)\\b\\s*$";

	/** The cmd pattern. */
	private final Pattern cmdPattern = Pattern.compile(this.cmdRegEx);

	/** The complete pattern. */
	private final Pattern completePattern = Pattern.compile(this.cmdRegEx
			+ this.luRegEx);

	/**
	 * Specialized constructor for a command prototype.
	 *
	 * @param stage
	 *            Stage.
	 */
	public SCmdFocusLU(final Stage stage) {
		super(SCmdFocusLU.DEFAULT_NAME, SCmdFocusLU.DEFAULT_ID, stage);
	}

	/**
	 * Specialized constructor for a complete command capable of being directly
	 * executed.
	 *
	 * @param stage
	 *            Stage.
	 * @param luName
	 *            String.
	 */
	public SCmdFocusLU(final Stage stage, final String luName) {
		super(SCmdFocusLU.DEFAULT_NAME, SCmdFocusLU.DEFAULT_ID, stage);
		this.luName = luName;
	}

	/**
	 * COMMAND METHODS OVERRIDDEN *********************************************.
	 * 
	 * @return the command pattern
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Command Pattern accepts 'sfocus' as the command.
	 */
	@Override
	public Pattern getCommandPattern() {
		return this.cmdPattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Complete Pattern accepts 'sfocus [luname]'.
	 */
	@Override
	public Pattern getCompletePattern() {
		return this.completePattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Template Method - Process the matcher to extract the logical unit.
	 */
	@Override
	public boolean processMatcher(final Matcher matcher) {
		this.luName = matcher.group(1);
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Notifies the System's stage to focus on a given logical unit.
	 */
	@Override
	public boolean execute() {
		boolean success = true;
		final LogicalUnit lu = this.stage.getLogicalUnit(this.luName);
		if (lu != null) {
			this.stage.setFocus(lu);
		} else {
			success = false;
		}
		if (success) {
			this.writer.println("Focusing on Logical Unit [" + this.luName
					+ "].\n", this.cmdVerbosity);
		} else {
			this.writer.println("Unable to Find/Focus on Logical Unit ["
					+ this.luName + "].\n", this.cmdVerbosity);
		}
		return success;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return a string describing the commands usage.
	 */
	@Override
	public String getUsage() {
		return "sFocus <luName>\t\t\t\tFocuses on Logical Unit.";
	}

}
