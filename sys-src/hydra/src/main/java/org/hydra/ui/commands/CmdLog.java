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

/**
 * Displays a summary of the system log.
 *
 * @since 0.2
 * @version 0.2
 * @author Scott A. Hady
 */
public class CmdLog extends CommandSystem {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The Constant DEFAULT_NAME. */
	public static final String DEFAULT_NAME = "System Log";

	/** The Constant DEFAULT_ID. */
	public static final String DEFAULT_ID = "CmdLog";

	/** The display count. */
	private int displayCount;

	/** The DEFAUL t_ displaycount. */
	private final int DEFAULT_DISPLAYCOUNT = 10;
	// Regular Expressions
	/** The cmd reg ex. */
	private final String cmdRegEx = "^\\s*(?i:log)\\b";

	/** The len reg ex. */
	private final String lenRegEx = "(\\s+(\\d+)\\b)?\\s*$";

	/** The cmd pattern. */
	private final Pattern cmdPattern = Pattern.compile(this.cmdRegEx);

	/** The complete pattern. */
	private final Pattern completePattern = Pattern.compile(this.cmdRegEx
			+ this.lenRegEx);

	/** The GROU p_ length. */
	private final int GROUP_LENGTH = 2;

	/**
	 * Default constructor, which initializes the number of entries to display
	 * to the default number (10).
	 */
	public CmdLog() {
		super(CmdLog.DEFAULT_NAME, CmdLog.DEFAULT_ID);
		this.displayCount = this.DEFAULT_DISPLAYCOUNT;
	}

	// ** COMMAND METHODS (OVERRIDDEN)
	// *******************************************/

	/**
	 * {@inheritDoc}
	 *
	 * Pattern accepts 'log' as the command.
	 */
	@Override
	public Pattern getCommandPattern() {
		return this.cmdPattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Complete Pattern accepts 'log [numEntries]'.
	 */
	@Override
	public Pattern getCompletePattern() {
		return this.completePattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Process the matcher to extract the user's id.
	 */
	@Override
	public boolean processMatcher(final Matcher matcher) {
		if (matcher.group(this.GROUP_LENGTH) != null) {
			this.displayCount = Integer.parseInt(matcher
					.group(this.GROUP_LENGTH));
		} else {
			this.displayCount = this.DEFAULT_DISPLAYCOUNT;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Display a summary and determined number of log entries.
	 */
	@Override
	public boolean execute() {
		final StringBuffer sb = new StringBuffer("Logger Status:\n");
		sb.append("--------------------------------------------------\n");
		sb.append(this.logger.getStatus());
		sb.append("--------------------------------------------------\n");
		for (final String entry : this.logger.getLogWriter().getLastEntries(
				this.displayCount)) {
			sb.append(entry);
		}
		this.writer.println(sb.toString(), this.cmdVerbosity);
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Display the command usage details.
	 */
	@Override
	public String getUsage() {
		return "log {<num>}\t\t\t\tDisplay the First <num> Log Entries.";
	}

}
