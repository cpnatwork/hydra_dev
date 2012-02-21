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
 * Displays a log of the stage's history.
 *
 * @since 0.1
 * @version 0.2
 * @author Scott A. Hady
 */
public class SCmdLog extends CommandLogicalUnit {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The Constant DEFAULT_NAME. */
	public static final String DEFAULT_NAME = "Stage Log";

	/** The Constant DEFAULT_ID. */
	public static final String DEFAULT_ID = "SCmdLog";

	/** The system path. */
	private boolean systemPath = true;
	// Regular Expressions
	/** The cmd reg ex. */
	private final String cmdRegEx = "^\\s*(?i:slog)\\b";

	/** The pa reg ex. */
	private final String paRegEx = "(\\s+-([vVsS]))?\\s*$";

	/** The cmd pattern. */
	private final Pattern cmdPattern = Pattern.compile(this.cmdRegEx);

	/** The complete pattern. */
	private final Pattern completePattern = Pattern.compile(this.cmdRegEx
			+ this.paRegEx);

	/** The GROU p_ target. */
	private final int GROUP_TARGET = 2;

	/**
	 * Specialized Constructor which specifies which stage to use.
	 *
	 * @param stage
	 *            Stage.
	 */
	public SCmdLog(final Stage stage) {
		super(SCmdLog.DEFAULT_NAME, SCmdLog.DEFAULT_ID, stage);
	}

	/**
	 * Specialized constructor that specifies which stage's history and which
	 * path to display.
	 *
	 * @param stage
	 *            Stage.
	 * @param systemPath
	 *            boolean.
	 */
	public SCmdLog(final Stage stage, final boolean systemPath) {
		super(SCmdLog.DEFAULT_NAME, SCmdLog.DEFAULT_ID, stage);
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
	 * Factory Method - Command Pattern accepts 'slog' as the command.
	 */
	@Override
	public Pattern getCommandPattern() {
		return this.cmdPattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Complete Pattern accepts 'slog {-s|v}'.
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
		return true;
	}

	/**
	 * Sets the system path.
	 * 
	 * @param matcher
	 *            the new system path
	 */
	private void setSystemPath(final Matcher matcher) {
		if (matcher.group(this.GROUP_TARGET) == null) {
			this.systemPath = true;
		} else {
			this.systemPath = matcher.group(this.GROUP_TARGET).toLowerCase()
					.equals("s");
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * Displays the designated logical unit's commit history.
	 */
	@Override
	public boolean execute() {
		boolean success = false;
		final StringBuilder sb = new StringBuilder("");
		try {
			sb.append("Stage Log  -  ");
			sb.append("Following: "
					+ ((this.systemPath) ? "[System Path]" : "[Valid Path]")
					+ "\n");
			sb.append("--------------------------------------------------\n");
			sb.append(this.stage.getHistoryCrawler().getHistoryLog(
					this.systemPath));
			success = true;
		} catch (final Exception e) {
			sb.append("\tNo Log for Stage.\n");
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
		return "sLog {-S|-V}\t\t\t\tPrints Log of Commits along SYSTEM (default) or VALID Path.";
	}

}
