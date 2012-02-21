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

import org.hydra.core.Container;
import org.hydra.core.Element;
import org.hydra.core.LogicalUnit;
import org.hydra.core.Stage;

/**
 * Displays the current status of the a logical unit.
 *
 * @since 0.1
 * @version 0.2
 * @author Scott A. Hady
 */
public class LUCmdStatus extends CommandLogicalUnit {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The Constant DEFAULT_NAME. */
	public static final String DEFAULT_NAME = "LogicalUnit Status";

	/** The Constant DEFAULT_ID. */
	public static final String DEFAULT_ID = "LUCmdStatus";

	// Regular Expressions
	/** The cmd reg ex. */
	private final String cmdRegEx = "^\\s*(?i:lustatus|lus)\\b";

	/** The lu reg ex. */
	private final String luRegEx = "(\\s+(\\S+.*))?\\b\\s*$";

	/** The cmd pattern. */
	private final Pattern cmdPattern = Pattern.compile(this.cmdRegEx);

	/** The complete pattern. */
	private final Pattern completePattern = Pattern.compile(this.cmdRegEx
			+ this.luRegEx);

	/** The GROU p_ logicalunit. */
	private final int GROUP_LOGICALUNIT = 2;

	/**
	 * Specialized Constructor designated which stage's logical unit's to query.
	 *
	 * @param stage
	 *            Stage.
	 */
	public LUCmdStatus(final Stage stage) {
		super(LUCmdStatus.DEFAULT_NAME, LUCmdStatus.DEFAULT_ID, stage);
	}

	/**
	 * Specialized Constructor; which takes the stage and name of the logical
	 * unit to describe.
	 *
	 * @param stage
	 *            Stage.
	 * @param luName
	 *            String.
	 */
	public LUCmdStatus(final Stage stage, final String luName) {
		super(LUCmdStatus.DEFAULT_NAME, LUCmdStatus.DEFAULT_ID, stage, luName);
	}

	/**
	 * COMMAND METHODS OVERRIDDEN *********************************************.
	 * 
	 * @return the command pattern
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Command Pattern accepts 'lustatus' as the command.
	 */
	@Override
	public Pattern getCommandPattern() {
		return this.cmdPattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Complete Pattern accepts 'lustatus {[luname]}'.
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
		return this.processLogicalUnitName(matcher
				.group(this.GROUP_LOGICALUNIT));
	}

	/**
	 * {@inheritDoc}
	 *
	 * Print a description of the logical unit's status.
	 */
	@Override
	public boolean execute() {
		boolean success = true;
		final StringBuilder sb = new StringBuilder("");
		final LogicalUnit lu = this.stage.getLogicalUnit(this.luName);
		if (lu == null) {
			sb.append("FAILURE: Logical Unit [" + this.luName
					+ "] Not Found.\n");
			success = false;
		} else {
			sb.append("Logical Unit [" + this.luName + "] Status:\n");
			sb.append("--------------------------------------------------\n");
			if (lu.getHead() != null) {
				sb.append("  Head:    [" + lu.getHead().getStatus(true, true)
						+ "] " + lu.getHead().getFingerprint().getHash() + "\n");
			} else {
				sb.append("  Head:    [- -] null\n");
			}
			if (lu.getHead() != null) {
				sb.append("  Current: ["
						+ lu.getCurrent().getStatus(true, true) + "] "
						+ lu.getCurrent().getFingerprint().getHash() + "\n");
				sb.append("                 Path: "
						+ lu.getCurrent().clonePath() + "\n");
			} else {
				sb.append("  Current: [- -] null\n");
				sb.append("                 Path: N/A\n");
			}
			sb.append("--------------------------------------------------\n");
			sb.append("  Contents:\n");
			sb.append("--------------------------------------------------\n");
			sb.append(this.listContentStatus(
					this.stage.getLogicalUnit(this.luName).getContents(), ""));
		}
		this.writer.println(sb.toString(), this.cmdVerbosity);
		return success;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return a string describing the commands usage.
	 */
	@Override
	public String getUsage() {
		return "luStatus(lus) {<luName>}\t\tPrints the Status of a Logical Unit.";
	}

	/**
	 * CMDLUSTATUS PROTECTED METHODS ******************************************.
	 * 
	 * @param container
	 *            the container
	 * @param prefix
	 *            the prefix
	 * @return the string
	 */

	/**
	 * Return a string describing the content of a container's status.
	 *
	 * @param container
	 *            Container.
	 * @param prefix
	 *            String.
	 * @return contentStatus - String.
	 */
	protected String listContentStatus(final Container container,
			final String prefix) {
		final StringBuilder sb = new StringBuilder("");
		for (final Element e : container.listElements()) {
			if (e instanceof Container) {
				sb.append("    [" + e.getStatus(true, true) + "]  " + prefix
						+ e.getName() + "/\n");
				sb.append(this.listContentStatus((Container) e,
						prefix + e.getName() + "/"));
			} else {
				sb.append("    [" + e.getStatus(true, true) + "] -" + prefix
						+ e.getName() + "\n");
			}
		}
		return sb.toString();
	}

}
