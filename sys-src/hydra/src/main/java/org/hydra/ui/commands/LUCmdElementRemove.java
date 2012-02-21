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

import org.hydra.core.Configuration;
import org.hydra.core.Container;
import org.hydra.core.Stage;

/**
 * Removes an element from the designated logical unit.
 *
 * @since 0.1
 * @version 0.2
 * @author Scott A. Hady
 */
public class LUCmdElementRemove extends CommandLogicalUnit {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The Constant DEFAULT_NAME. */
	public static final String DEFAULT_NAME = "LogicalUnit Remove Element";

	/** The Constant DEFAULT_ID. */
	public static final String DEFAULT_ID = "LUCmdElementRemove";

	/** The e name. */
	private String eName;

	/** The config. */
	private final Configuration config = Configuration.getInstance();
	// Regular Expressions
	/** The cmd reg ex. */
	private final String cmdRegEx = "^\\s*(?i:luremove)\\b";

	/** The lu reg ex. */
	private final String luRegEx = "((\\s+)(\\S+.*))?\\s+-e\\b";

	/** The el reg ex. */
	private final String elRegEx = "\\s+(\\S+.*)(\\\\|\\/)?\\s*$";

	/** The cmd pattern. */
	private final Pattern cmdPattern = Pattern.compile(this.cmdRegEx);

	/** The complete pattern. */
	private final Pattern completePattern = Pattern.compile(this.cmdRegEx
			+ this.luRegEx + this.elRegEx);

	/** The GROU p_ logicalunit. */
	private final int GROUP_LOGICALUNIT = 3;

	/** The GROU p_ element. */
	private final int GROUP_ELEMENT = 4;

	/**
	 * Specialized Constructor which designates which stage use to find the
	 * logical unit's to remove an element.
	 *
	 * @param stage
	 *            Stage.
	 */
	public LUCmdElementRemove(final Stage stage) {
		super(LUCmdElementRemove.DEFAULT_NAME, LUCmdElementRemove.DEFAULT_ID,
				stage);
	}

	/**
	 * Specialized constructor; which specifies the stage, logical unit's name
	 * and element's name to use.
	 *
	 * @param stage
	 *            Stage.
	 * @param luName
	 *            String.
	 * @param eName
	 *            String.
	 */
	public LUCmdElementRemove(final Stage stage, final String luName,
			final String eName) {
		super(LUCmdElementRemove.DEFAULT_NAME, LUCmdElementRemove.DEFAULT_ID,
				stage, luName);
		this.eName = eName;
	}

	/**
	 * COMMAND METHODS OVERRIDDEN *********************************************.
	 * 
	 * @return the command pattern
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Command Pattern accepts 'luremove' as the command.
	 */
	@Override
	public Pattern getCommandPattern() {
		return this.cmdPattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Complete Pattern accepts 'luremove {[luname]} -e
	 * [ename]'.
	 */
	@Override
	public Pattern getCompletePattern() {
		return this.completePattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Template Method - Process the matcher to extract the logical unit and
	 * element name.
	 */
	@Override
	public boolean processMatcher(final Matcher matcher) {
		this.eName = matcher.group(this.GROUP_ELEMENT).trim();
		return this.processLogicalUnitName(matcher
				.group(this.GROUP_LOGICALUNIT));
	}

	/**
	 * {@inheritDoc}
	 *
	 * Remove the designated element to the designated logical unit.
	 */
	@Override
	public boolean execute() {
		boolean success = true;
		String fullName = "";
		try {
			fullName = this.findElementName(this.eName);
			final Container luContents = this.stage.getLogicalUnit(this.luName)
					.getContents();
			success = luContents.removeElement(luContents.getElement(fullName
					.substring(1)));
			this.stage.getLogicalUnit(this.luName).recordReferences();
		} catch (final Exception e) {
			this.logger.exception("(CMD) Unable to Remove Element.", e);
			success = false;
		}
		if (success) {
			this.writer.println("Element [" + fullName
					+ "] removed from Logical Unit [" + this.luName + "].\n",
					this.cmdVerbosity);
		} else {
			this.writer.println("FAILURE: Removing Element [" + fullName
					+ "] from Logical Unit [" + this.luName + "].\n",
					this.cmdVerbosity);
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
		return "luRemove {<luName>} -e <eName>\t\tRemoves an Element from Logical Unit.";
	}

}
