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
import org.hydra.persistence.DataAccessObject;

/**
 * Sets the system's user identification that will be recorded when either a
 * logical unit or the stage are committed.
 *
 * @since 0.1
 * @version 0.2
 * @author Scott A. Hady
 */
public class CmdSetUser extends CommandSystem {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The Constant DEFAULT_NAME. */
	public static final String DEFAULT_NAME = "Set User";

	/** The Constant DEFAULT_ID. */
	public static final String DEFAULT_ID = "CmdSetUser";

	/** The user id. */
	private String userId;
	// Regular Expressions
	/** The cmd reg ex. */
	private final String cmdRegEx = "^\\s*(?i:setuser)\\b";

	/** The usr reg ex. */
	private final String usrRegEx = "\\s+(\\S.*)\\b\\s*$";

	/** The cmd pattern. */
	private final Pattern cmdPattern = Pattern.compile(this.cmdRegEx);

	/** The complete pattern. */
	private final Pattern completePattern = Pattern.compile(this.cmdRegEx
			+ this.usrRegEx);

	/** The GROU p_ userid. */
	private final int GROUP_USERID = 1;

	/**
	 * Default Constructor, defines the system user as "unknown".
	 */
	public CmdSetUser() {
		super(CmdSetUser.DEFAULT_NAME, CmdSetUser.DEFAULT_ID);
		this.userId = "unknown";
	}

	/**
	 * Specialized Constructor, defines what to set the system's user
	 * identification.
	 *
	 * @param userId
	 *            String.
	 */
	public CmdSetUser(final String userId) {
		super(CmdSetUser.DEFAULT_NAME, CmdSetUser.DEFAULT_ID);
		this.userId = userId;
	}

	/**
	 * COMMAND METHODS (OVERRIDDEN) *******************************************.
	 * 
	 * @return the command pattern
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Pattern accepts 'setuser' as the command.
	 */
	@Override
	public Pattern getCommandPattern() {
		return this.cmdPattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Complete Pattern accepts 'setuser [userid]'.
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
		if (!matcher.group(this.GROUP_USERID).contains(
				DataAccessObject.SEP_TOKEN)) {
			this.userId = matcher.group(this.GROUP_USERID);
			return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Set's the system's user identification in the configuration.
	 */
	@Override
	public boolean execute() {
		Configuration.getInstance().setUserId(this.userId);
		this.writer.println("Set User ID to [" + this.userId + "].\n",
				this.cmdVerbosity);
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return a string describing the commands usage.
	 */
	@Override
	public String getUsage() {
		return "setUser <userId>\t\t\tSets the System's User ID.";
	}

}
