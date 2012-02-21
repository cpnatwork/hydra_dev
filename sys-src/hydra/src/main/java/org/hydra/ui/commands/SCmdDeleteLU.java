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

import org.hydra.core.Stage;

/**
 * Deletes a new logical unit, but not the content.
 *
 * @since 0.1
 * @version 0.2
 * @author Scott A. Hady
 */
public class SCmdDeleteLU extends CommandStage {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The Constant DEFAULT_NAME. */
	public static final String DEFAULT_NAME = "Stage Delete LogicalUnit";

	/** The Constant DEFAULT_ID. */
	public static final String DEFAULT_ID = "SCmdDeleteLU";

	/** The lu name. */
	private String luName;
	// Regular Expressions
	/** The cmd reg ex. */
	private final String cmdRegEx = "^\\s*(?i:sdelete)\\b";

	/** The lu reg ex. */
	private final String luRegEx = "\\s+(\\S+.*)\\b\\s*$";

	/** The cmd pattern. */
	private final Pattern cmdPattern = Pattern.compile(this.cmdRegEx);

	/** The complete pattern. */
	private final Pattern completePattern = Pattern.compile(this.cmdRegEx
			+ this.luRegEx);

	/**
	 * Specialized Constructor which designates which stage's logical units to
	 * delete.
	 *
	 * @param stage
	 *            Stage.
	 */
	public SCmdDeleteLU(final Stage stage) {
		super(SCmdDeleteLU.DEFAULT_NAME, SCmdDeleteLU.DEFAULT_ID, stage);
	}

	/**
	 * Specialized constructor which specifies the stage and the name of the
	 * logical unit to delete.
	 *
	 * @param stage
	 *            Stage.
	 * @param luName
	 *            String.
	 */
	public SCmdDeleteLU(final Stage stage, final String luName) {
		super(SCmdDeleteLU.DEFAULT_NAME, SCmdDeleteLU.DEFAULT_ID, stage);
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
	 * Factory Method - Command Pattern accepts 'sdelete' as the command.
	 */
	@Override
	public Pattern getCommandPattern() {
		return this.cmdPattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Complete Pattern accepts 'sdelete [luname]'.
	 */
	@Override
	public Pattern getCompletePattern() {
		return this.completePattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Template Method - Process the matcher to extract the logical unit
	 * parameter.
	 */
	@Override
	public boolean processMatcher(final Matcher matcher) {
		this.luName = matcher.group(1);
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Delete the logical unit from the designed stage.
	 */
	@Override
	public boolean execute() {
		boolean success = true;
		if (!this.stage.deleteLogicalUnit(this.luName)) {
			success = false;
		}
		if (success) {
			this.writer.println(
					"Logical Unit [" + this.luName + "] Deleted.\n",
					this.cmdVerbosity);
		} else {
			this.writer.println("FAILURE: Deleting Logical Unit ["
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
		return "sDelete <luName>\t\t\tDeletes a Logical Unit.";
	}

}
