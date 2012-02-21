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
import org.hydra.core.Stage;

/**
 * Removes an element from the stage.
 *
 * @since 0.1
 * @version 0.2
 * @author Scott A. Hady
 */
public class SCmdElementRemove extends CommandStage {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The Constant DEFAULT_NAME. */
	public static final String DEFAULT_NAME = "Stage Remove Element";

	/** The Constant DEFAULT_ID. */
	public static final String DEFAULT_ID = "SCmdElementRemove";

	/** The e name. */
	private String eName;

	/** The config. */
	private final Configuration config = Configuration.getInstance();
	// Regular Expressions
	/** The Constant cmdRegEx. */
	private static final String cmdRegEx = "^\\s*(?i:sremove)\\b";

	/** The Constant elRegEx. */
	private static final String elRegEx = "\\s+-e\\b\\s+(\\S+.*)(\\\\|\\/)?\\s*$";

	/** The Constant cmdPattern. */
	private static final Pattern cmdPattern = Pattern
			.compile(SCmdElementRemove.cmdRegEx);

	/** The Constant completePattern. */
	private static final Pattern completePattern = Pattern
			.compile(SCmdElementRemove.cmdRegEx + SCmdElementRemove.elRegEx);

	/** The Constant GROUP_ELEMENT. */
	private static final int GROUP_ELEMENT = 1;

	/**
	 * Specialized Constructor which designates which stage use to remove an
	 * element.
	 *
	 * @param stage
	 *            Stage.
	 */
	public SCmdElementRemove(final Stage stage) {
		super(SCmdElementRemove.DEFAULT_NAME, SCmdElementRemove.DEFAULT_ID,
				stage);
	}

	/**
	 * Specialized constructor; which specifies the stage and element's name to
	 * remove.
	 *
	 * @param stage
	 *            Stage.
	 * @param eName
	 *            String.
	 */
	public SCmdElementRemove(final Stage stage, final String eName) {
		super(SCmdElementRemove.DEFAULT_NAME, SCmdElementRemove.DEFAULT_ID,
				stage);
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
	 * Factory Method - Command Pattern accepts 'sremove' as the command.
	 */
	@Override
	public Pattern getCommandPattern() {
		return SCmdElementRemove.cmdPattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Complete Pattern accepts 'sremove -e [ename]'.
	 */
	@Override
	public Pattern getCompletePattern() {
		return SCmdElementRemove.completePattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Template Method - Process the matcher to extract the logical unit and
	 * element name.
	 */
	@Override
	public boolean processMatcher(final Matcher matcher) {
		this.eName = matcher.group(SCmdElementRemove.GROUP_ELEMENT).trim();
		return true;
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
			success = this.stage.getContents().removeElement(
					this.stage.getContents().getElement(fullName.substring(1)));
			this.stage.recordReferences();
		} catch (final Exception e) {
			this.logger
					.exception("Unable to Remove Element from the Stage.", e);
			success = false;
		}
		if (success) {
			this.writer.println("Element [" + fullName
					+ "] removed from the Stage.\n", this.cmdVerbosity);
		} else {
			this.writer.println("Unable to Remove Element [" + fullName
					+ "] from the Stage.\n", this.cmdVerbosity);
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
		return "sRemove -e <eName>\t\t\tRemoves an Element from the Stage.";
	}

}
