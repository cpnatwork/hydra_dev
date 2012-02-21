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
 * Displays a log of a logical unit's history.
 *
 * @since 0.1
 * @version 0.2
 * @author Scott A. Hady
 */
public class LUCmdLog extends CommandLogicalUnit {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The Constant DEFAULT_NAME. */
	public static final String DEFAULT_NAME = "LogicalUnit Log";

	/** The Constant DEFAULT_ID. */
	public static final String DEFAULT_ID = "LUCmdLog";

	/** The system path. */
	private boolean systemPath = true;
	// Regular Expressions
	/** The cmd reg ex. */
	private final String cmdRegEx = "^\\s*(?i:lulog)\\b";

	/** The lu reg ex. */
	private final String luRegEx = "(\\s+(\\S+.*(?<!\\s-[vVsS]))\\b)?";

	/** The pa reg ex. */
	private final String paRegEx = "(\\s+-([vVsS]))?\\s*$";

	/** The cmd pattern. */
	private final Pattern cmdPattern = Pattern.compile(this.cmdRegEx);

	/** The complete pattern. */
	private final Pattern completePattern = Pattern.compile(this.cmdRegEx
			+ this.luRegEx + this.paRegEx);

	/**
	 * Specialized Constructor which specifies which stage to use.
	 *
	 * @param stage
	 *            Stage.
	 */
	public LUCmdLog(final Stage stage) {
		super(LUCmdLog.DEFAULT_NAME, LUCmdLog.DEFAULT_ID, stage);
	}

	/**
	 * Specialized constructor that specifies which logical unit's history to
	 * display.
	 *
	 * @param stage
	 *            Stage.
	 * @param luName
	 *            String.
	 * @param systemPath
	 *            boolean.
	 */
	public LUCmdLog(final Stage stage, final String luName,
			final boolean systemPath) {
		super(LUCmdLog.DEFAULT_NAME, LUCmdLog.DEFAULT_ID, stage, luName);
		this.systemPath = systemPath;
	}

	/**
	 * COMMAND METHODS OVERRIDDEN *********************************************.
	 * 
	 * @return the command pattern
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Command Pattern accepts 'lulog' as the command.
	 */
	@Override
	public Pattern getCommandPattern() {
		return this.cmdPattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Complete Pattern accepts 'lulog {[luname]} {-s|v}'.
	 */
	@Override
	public Pattern getCompletePattern() {
		return this.completePattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Template Method - Process the matcher to extract the logical unit, path
	 * flag.
	 */
	@Override
	public boolean processMatcher(final Matcher matcher) {
		this.setSystemPath(matcher);
		return this.processLogicalUnitName(matcher.group(2));
	}

	/**
	 * Sets the system path.
	 * 
	 * @param matcher
	 *            the new system path
	 */
	private void setSystemPath(final Matcher matcher) {
		if (matcher.group(4) == null) {
			this.systemPath = true;
		} else {
			this.systemPath = matcher.group(4).toLowerCase().equals("s");
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * Displays the designated logical unit's commit history.
	 */
	@Override
	public boolean execute() {
		boolean success = true;
		final StringBuilder sb = new StringBuilder("");
		try {
			final LogicalUnit lu = this.stage.getLogicalUnit(this.luName);
			sb.append("Logical Unit Log: [" + this.luName + "]  -  ");
			sb.append("Following: "
					+ ((this.systemPath) ? "[System Path]" : "[Valid Path]")
					+ "\n");
			sb.append("--------------------------------------------------\n");
			sb.append(lu.getHistoryCrawler().getHistoryLog(this.systemPath));
		} catch (final Exception e) {
			sb.append("FAILURE: No Log for Logical Unit [" + this.luName
					+ "]\n");
			success = false;
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
		return "luLog {<luName>} {-S|-V}\t\tPrints Log of Commits along SYSTEM (default) or VALID Path.";
	}

}
