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

import org.hydra.core.Container;
import org.hydra.core.Element;
import org.hydra.core.LogicalUnit;
import org.hydra.core.Stage;

/**
 * Displays the current status of the system's stage.
 *
 * @since 0.2
 * @version 0.2
 * @author Scott A. Hady
 */
public class SCmdStatus extends CommandStage {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The Constant DEFAULT_NAME. */
	public static final String DEFAULT_NAME = "Stage Status";

	/** The Constant DEFAULT_ID. */
	public static final String DEFAULT_ID = "SCmdStatus";

	// Regular Expressions
	/** The cmd reg ex. */
	private final String cmdRegEx = "^\\s*(?i:ss|sstatus)\\b\\s*";

	/** The cmd pattern. */
	private final Pattern cmdPattern = Pattern.compile(this.cmdRegEx);

	/**
	 * Specialized constructor which specifies the stage on which to operate.
	 *
	 * @param stage
	 *            Stage.
	 */
	public SCmdStatus(final Stage stage) {
		super(SCmdStatus.DEFAULT_NAME, SCmdStatus.DEFAULT_ID, stage);
	}

	/**
	 * COMMAND METHODS (OVERRIDDEN) *******************************************.
	 * 
	 * @return the command pattern
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Command Pattern accepts 'sstatus|ss' as the command.
	 */
	@Override
	public Pattern getCommandPattern() {
		return this.cmdPattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Complete Pattern accepts 'sstatus|ss' as the command.
	 */
	@Override
	public Pattern getCompletePattern() {
		return this.getCommandPattern();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Display the stage's status.
	 */
	@Override
	public boolean execute() {
		final StringBuilder sb = new StringBuilder("");
		sb.append("\nStage Status:\n");
		sb.append("--------------------------------------------------\n");
		if (this.stage.getHead() != null) {
			sb.append("  Head:    ["
					+ this.stage.getHead().getStatus(true, true) + "] "
					+ this.stage.getHeadHash() + "\n");
		} else {
			sb.append("  Head:    [- -] null\n");
		}
		if (this.stage.getCurrent() != null) {
			sb.append("  Current: ["
					+ this.stage.getCurrent().getStatus(true, true) + "] "
					+ this.stage.getCurrentHash() + "\n");
			sb.append("           Path: " + this.stage.getCurrent().clonePath()
					+ "\n");
		} else {
			sb.append("  Current: [- -] null\n");
			sb.append("                 Path: N/A\n");
		}
		sb.append("--------------------------------------------------\n");
		sb.append("  Contents:\n");
		sb.append("--------------------------------------------------\n");
		if (this.stage.getContents().countElements() > 0) {
			sb.append(this.listContentStatus(this.stage.getContents(), "  "));
		} else {
			sb.append("    No Contents\n");
		}
		sb.append("--------------------------------------------------\n");
		sb.append("  Logical Units:\n");
		sb.append("--------------------------------------------------\n");
		for (final LogicalUnit lu : this.stage.listManaged()) {
			sb.append((this.isFocusedLogicalUnit(lu) ? "    * " : "    + "));
			sb.append(lu.getName() + "\n");
		}
		for (final LogicalUnit lu : this.stage.listUnmanaged()) {
			sb.append("    - " + lu.getName() + "\n");
		}
		this.writer.println(sb.toString(), this.cmdVerbosity);
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return a string describing the command's usage.
	 */
	@Override
	public String getUsage() {
		return "sStatus(ss)\t\t\t\tPrints the Stage's Status.";
	}

	/**
	 * CMDSSTATUS PROTECTED METHODS *******************************************.
	 * 
	 * @param logicalUnit
	 *            the logical unit
	 * @return true, if is focused logical unit
	 */

	/**
	 * Determine if the logical unit is the stage's focused logical unit.
	 *
	 * @param logicalUnit
	 *            LogicalUnit.
	 * @return isFocus - boolean.
	 */
	protected boolean isFocusedLogicalUnit(final LogicalUnit logicalUnit) {
		return (this.stage.isFocused() && this.stage.getFocus().equals(
				logicalUnit));
	}

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
