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
 * Sets the System's verbosness level.
 *
 * @since 0.1
 * @version 0.2
 * @author Scott A. Hady
 */
public class CmdVerbose extends CommandSystem {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The Constant DEFAULT_NAME. */
	public static final String DEFAULT_NAME = "System Verbosity";

	/** The Constant DEFAULT_ID. */
	public static final String DEFAULT_ID = "CmdVerbose";

	/** The target verbosity. */
	private int targetVerbosity;

	/** The DEFAUL t_ verbosity. */
	private final int DEFAULT_VERBOSITY = 5;
	// Regular Expression
	/** The cmd reg ex. */
	private final String cmdRegEx = "^\\s*(?i:v|verbosity)\\b";

	/** The lu reg ex. */
	private final String luRegEx = "\\s+(\\d{1,2})\\b\\s*$";

	/** The cmd pattern. */
	private final Pattern cmdPattern = Pattern.compile(cmdRegEx);

	/** The complete pattern. */
	private final Pattern completePattern = Pattern.compile(cmdRegEx + luRegEx);

	/** The GROU p_ level. */
	private final int GROUP_LEVEL = 1;

	/**
	 * Default constructor which uses the default verbosity level.
	 */
	public CmdVerbose() {
		super(DEFAULT_NAME, DEFAULT_ID);
		targetVerbosity = DEFAULT_VERBOSITY;
	}

	/**
	 * Special Constructor for specifying the level of verbosity to set for the
	 * system.
	 *
	 * @param verbosityLevel
	 *            int.
	 */
	public CmdVerbose(int verbosityLevel) {
		super(DEFAULT_NAME, DEFAULT_ID);
		targetVerbosity = verbosityLevel;
	}

	/**
	 * COMMAND METHODS OVERRIDDEN *********************************************.
	 * 
	 * @return the command pattern
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Pattern accepts 'verbosity|v' as the command.
	 */
	@Override
	public Pattern getCommandPattern() {
		return cmdPattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Complete Pattern accepts 'verbosity|v [int]'.
	 */
	@Override
	public Pattern getCompletePattern() {
		return completePattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Process the matcher to extract the target verbosity.
	 */
	@Override
	public boolean processMatcher(Matcher matcher) {
		targetVerbosity = Integer.parseInt(matcher.group(GROUP_LEVEL));
		return (targetVerbosity >= 0 && targetVerbosity <= 10);
	}

	/**
	 * {@inheritDoc}
	 *
	 * Set the defined UI's verbosity level.
	 */
	@Override
	public boolean execute() {
		writer.println("Setting Verbosity Level: [" + targetVerbosity + "].\n",
				this.cmdVerbosity);
		writer.getInstance().setVerbosity(targetVerbosity);
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return a string describing the commands usage.
	 */
	@Override
	public String getUsage() {
		return "verbosity(v) <0-10>\t\t\tSets System's Verbosity Level.";
	}

}
