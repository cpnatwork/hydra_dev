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
 * Command that removes a given logical unit from the targeted stage's managed
 * list.
 *
 * @since 0.2
 * @version 0.2
 * @author Scott A. Hady
 */
public class SCmdIgnoreLU extends CommandStage {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The Constant DEFAULT_NAME. */
	public static final String DEFAULT_NAME = "Stage Ignore LogicalUnit";

	/** The Constant DEFAULT_ID. */
	public static final String DEFAULT_ID = "SCmdIgnoreLU";

	/** The lu name. */
	private String luName;
	// Regular Expression
	/** The cmd reg ex. */
	private final String cmdRegEx = "^\\s*(?i:signore)\\b";

	/** The lu reg ex. */
	private final String luRegEx = "\\s+(\\S.*)\\b\\s*$";

	/** The cmd pattern. */
	private final Pattern cmdPattern = Pattern.compile(this.cmdRegEx);

	/** The complete pattern. */
	private final Pattern completePattern = Pattern.compile(this.cmdRegEx
			+ this.luRegEx);

	/**
	 * Specialized Constructor, which only specifies the stage to manipulate and
	 * is appropriate for usage through command strings.
	 *
	 * @param stage
	 *            Stage.
	 */
	public SCmdIgnoreLU(final Stage stage) {
		super(SCmdIgnoreLU.DEFAULT_NAME, SCmdIgnoreLU.DEFAULT_ID, stage);
	}

	/**
	 * Specialized Constructor which specifies the stage and the name of the
	 * logical unit to operate on. Appropriate for usage outside of the user
	 * interface.
	 *
	 * @param stage
	 *            Stage.
	 * @param luName
	 *            String.
	 */
	public SCmdIgnoreLU(final Stage stage, final String luName) {
		super(SCmdIgnoreLU.DEFAULT_NAME, SCmdIgnoreLU.DEFAULT_ID, stage);
		this.luName = luName;
	}

	/**
	 * COMMAND METHODS (OVERRIDDEN) *******************************************.
	 * 
	 * @return the command pattern
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Command Pattern accepts 'signore' as the command.
	 */
	@Override
	public Pattern getCommandPattern() {
		return this.cmdPattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Complete Pattern accepts 'signore [luname]'.
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
	 * Remove the designated logical unit from the stage's managed list.
	 */
	@Override
	public boolean execute() {
		final boolean success = (this.stage.ignore(this.luName) != null);
		if (success) {
			this.writer.println("Ignoring Logical Unit [" + this.luName
					+ "].\n", this.cmdVerbosity);
		} else {
			this.writer.println("Unable to find/ignore on Logical Unit ["
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
		return "sIgnore <luName>\t\t\tIgnores the Logical Unit.";
	}

}
