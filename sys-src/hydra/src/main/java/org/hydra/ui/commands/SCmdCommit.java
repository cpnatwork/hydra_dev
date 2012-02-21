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
 * Commits the state.
 *
 * @since 0.1
 * @version 0.2
 * @author Scott A. Hady
 */
public class SCmdCommit extends CommandStage {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The Constant DEFAULT_NAME. */
	public static final String DEFAULT_NAME = "Stage Commit";

	/** The Constant DEFAULT_ID. */
	public static final String DEFAULT_ID = "SCmdCommit";

	/** The commit msg. */
	private String commitMsg;

	/** The full commit. */
	private boolean fullCommit;
	// Regular Expressions
	/** The cmd reg ex. */
	private final String cmdRegEx = "^\\s*(?i:scommit)\\b";

	/** The tk reg ex. */
	private final String tkRegEx = "(\\s+-full\\b)?";

	/** The msg reg ex. */
	private final String msgRegEx = "\\s+-m\\b\\s+(.*)\\s*$";

	/** The cmd pattern. */
	private final Pattern cmdPattern = Pattern.compile(this.cmdRegEx);

	/** The complete pattern. */
	private final Pattern completePattern = Pattern.compile(this.cmdRegEx
			+ this.tkRegEx + this.msgRegEx);

	/** The GROU p_ full. */
	private final int GROUP_FULL = 1;

	/** The GROU p_ message. */
	private final int GROUP_MESSAGE = 2;

	/**
	 * Specialized Constructor which designates which stage to commit.
	 *
	 * @param stage
	 *            Stage.
	 */
	public SCmdCommit(final Stage stage) {
		super(SCmdCommit.DEFAULT_NAME, SCmdCommit.DEFAULT_ID, stage);
		this.fullCommit = false;
	}

	/**
	 * Specialized constructor that designates the stage and the commit message
	 * to use for the commit.
	 *
	 * @param stage
	 *            Stage.
	 * @param commitMsg
	 *            String.
	 */
	public SCmdCommit(final Stage stage, final String commitMsg) {
		super(SCmdCommit.DEFAULT_NAME, SCmdCommit.DEFAULT_ID, stage);
		this.commitMsg = commitMsg;
		this.fullCommit = false;
	}

	/**
	 * Specialized constructor that designates the stage, the commit message to
	 * use and if the commit should recursively commit the managed logical
	 * units.
	 *
	 * @param stage
	 *            Stage.
	 * @param commitMsg
	 *            String.
	 * @param fullCommit
	 *            boolean.
	 */
	public SCmdCommit(final Stage stage, final String commitMsg,
			final boolean fullCommit) {
		super(SCmdCommit.DEFAULT_NAME, SCmdCommit.DEFAULT_ID, stage);
		this.commitMsg = commitMsg;
		this.fullCommit = fullCommit;
	}

	/**
	 * COMMAND METHODS OVERRIDDEN *********************************************.
	 * 
	 * @return the command pattern
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Command Pattern accepts 'scommit' as the command.
	 */
	@Override
	public Pattern getCommandPattern() {
		return this.cmdPattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Complete Pattern accepts 'scommit {--full} -m [message].
	 */
	@Override
	public Pattern getCompletePattern() {
		return this.completePattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Template Method - Process the matcher to extract the logical unit and
	 * message parameters.
	 */
	@Override
	public boolean processMatcher(final Matcher matcher) {
		this.fullCommit = (matcher.group(this.GROUP_FULL) != null) ? true
				: false;
		this.commitMsg = matcher.group(this.GROUP_MESSAGE);
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Commit the designated logical unit.
	 */
	@Override
	public boolean execute() {
		boolean success = true;
		String exMsg = "FAILURE: Commit of Stage Unsuccessful.\n";
		try {
			if (this.fullCommit) {
				final String commitHash = this.stage
						.commitStageAndLogicalUnits(Configuration.getInstance()
								.getUserId(), this.commitMsg);
				exMsg = "Stage and Logical Units Successfully Committed. Hash ["
						+ commitHash + "].\n";
			} else {
				final String commitHash = this.stage
						.commitValidPath(Configuration.getInstance()
								.getUserId(), this.commitMsg);
				exMsg = "Stage Successfully Committed. Hash [" + commitHash
						+ "].\n";
			}
		} catch (final Exception e) {
			this.logger.exception("Unable to Commit Stage.", e);
			success = false;
		}
		this.writer.println(exMsg, this.cmdVerbosity);
		return success;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return a string describing the commands usage.
	 */
	@Override
	public String getUsage() {
		return "sCommit {-full} -m <message>\t\tCommit Stage (Commit Logical Units if Full).";
	}

}
