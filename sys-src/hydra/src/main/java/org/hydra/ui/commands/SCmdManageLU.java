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
 * Manage a logical unit that has already been created.
 *
 * @since 0.1
 * @version 0.2
 * @author Scott A. Hady
 */
public class SCmdManageLU extends CommandStage {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The Constant DEFAULT_NAME. */
	public static final String DEFAULT_NAME = "Stage Manage LogicalUnit";

	/** The Constant DEFAULT_ID. */
	public static final String DEFAULT_ID = "SCmdManageLU";

	/** The lu name. */
	private String luName;
	// Regular Expressions
	/** The cmd reg ex. */
	private final String cmdRegEx = "^\\s*(?i:smanage)\\b";

	/** The lu reg ex. */
	private final String luRegEx = "\\s+(\\S+.*)\\b\\s*$";

	/** The cmd pattern. */
	private final Pattern cmdPattern = Pattern.compile(this.cmdRegEx);

	/** The complete pattern. */
	private final Pattern completePattern = Pattern.compile(this.cmdRegEx
			+ this.luRegEx);

	/** The GROU p_ luname. */
	private final int GROUP_LUNAME = 1;

	/**
	 * Specialized Constructor designating on which stage to manage the logical
	 * units.
	 *
	 * @param stage
	 *            Stage.
	 */
	public SCmdManageLU(final Stage stage) {
		super(SCmdManageLU.DEFAULT_NAME, SCmdManageLU.DEFAULT_ID, stage);
	}

	/**
	 * Specialized constructor which designates the stage and the logical unit's
	 * name.
	 *
	 * @param stage
	 *            Stage.
	 * @param luName
	 *            String.
	 */
	public SCmdManageLU(final Stage stage, final String luName) {
		super(SCmdManageLU.DEFAULT_NAME, SCmdManageLU.DEFAULT_ID, stage);
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
	 * Factory Method - Command Pattern accepts 'smanage' as the command.
	 */
	@Override
	public Pattern getCommandPattern() {
		return this.cmdPattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Complete Pattern accepts 'smanage [luname]'.
	 */
	@Override
	public Pattern getCompletePattern() {
		return this.completePattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Template Method - Process the matcher to extract the logical unit and
	 * message parameters.
	 */
	@Override
	public boolean processMatcher(final Matcher matcher) {
		this.luName = matcher.group(this.GROUP_LUNAME);
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Manage designated logical unit.
	 */
	@Override
	public boolean execute() {
		boolean success = false;
		try {
			success = this.stage.manage(new LogicalUnit(this.luName));
		} catch (final Exception e) {
			this.logger.exception("Unable to Manage Logical Unit.", e);
		}
		if (success) {
			this.writer.println(
					"Logical Unit [" + this.luName + "] Managed.\n",
					this.cmdVerbosity);
		} else {
			this.writer.println("Unable to Manage Logical Unit [" + this.luName
					+ "].\n", this.cmdVerbosity);
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
		return "sManage <luName>\t\t\tManage Logical Unit.";
	}

}
