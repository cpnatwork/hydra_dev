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
 * Revert the stage to a previously persisted state.
 *
 * @since 0.1
 * @version 0.2
 * @author Scott A. Hady
 */
public class SCmdRevert extends CommandStage {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The Constant DEFAULT_NAME. */
	public static final String DEFAULT_NAME = "Stage Revert";

	/** The Constant DEFAULT_ID. */
	public static final String DEFAULT_ID = "SCmdRevert";

	/** The hash flag. */
	private final String hashFlag = "-h";
	// Parameters
	/** The target type. */
	private String targetType;

	/** The target hash. */
	private String targetHash;

	/** The target description. */
	private String targetDescription;

	/** The full revert. */
	private boolean fullRevert;
	// Regular Expressions.
	/** The cmd reg ex. */
	private final String cmdRegEx = "^\\s*(?i:srevert)\\b\\s*";

	/** The full reg ex. */
	private final String fullRegEx = "(\\s+-full\\b)?";

	/** The tgt reg ex. */
	private final String tgtRegEx = "\\s+(?i:-(h|hash))\\s+(\\S+.*)\\b\\s*$";

	/** The cmd pattern. */
	private final Pattern cmdPattern = Pattern.compile(this.cmdRegEx);

	/** The complete pattern. */
	private final Pattern completePattern = Pattern.compile(this.cmdRegEx
			+ this.fullRegEx + this.tgtRegEx);
	// Extended Regular Expressions.
	/** The hash reg ex. */
	private final String hashRegEx = "^\\s*\\w{40}\\s*$";

	/** The hash pattern. */
	private final Pattern hashPattern = Pattern.compile(this.hashRegEx);

	/** The GROU p_ full. */
	private final int GROUP_FULL = 1;

	/** The GROU p_ target. */
	private final int GROUP_TARGET = 2;

	/** The GROU p_ args. */
	private final int GROUP_ARGS = 3;

	/**
	 * Specialized Constructor which designates which stage use to revert.
	 *
	 * @param stage
	 *            Stage.
	 */
	public SCmdRevert(final Stage stage) {
		super(SCmdRevert.DEFAULT_NAME, SCmdRevert.DEFAULT_ID, stage);
	}

	/**
	 * Specialized constructor that specifies which stage to revert and to which
	 * hash to revert.
	 *
	 * @param stage
	 *            Stage.
	 * @param targetHash
	 *            String.
	 */
	public SCmdRevert(final Stage stage, final String targetHash) {
		super(SCmdRevert.DEFAULT_NAME, SCmdRevert.DEFAULT_ID, stage);
		this.targetType = this.hashFlag;
		this.targetHash = targetHash;
		this.targetDescription = "Hash(" + this.targetHash + ")";
		this.fullRevert = false;
	}

	/**
	 * Specialized constructor that specifies which stage to revert, to which
	 * hash to revert and whether to revert all logical units as well.
	 *
	 * @param stage
	 *            Stage.
	 * @param targetHash
	 *            String.
	 * @param fullRevert
	 *            boolean.
	 */
	public SCmdRevert(final Stage stage, final String targetHash,
			final boolean fullRevert) {
		super(SCmdRevert.DEFAULT_NAME, SCmdRevert.DEFAULT_ID, stage);
		this.targetType = this.hashFlag;
		this.targetHash = targetHash;
		this.targetDescription = "Hash(" + this.targetHash + ")";
		this.fullRevert = fullRevert;
	}

	/**
	 * COMMAND METHODS OVERRIDDEN *********************************************.
	 * 
	 * @return the command pattern
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Pattern accepts 'srevert' as the command.
	 */
	@Override
	public Pattern getCommandPattern() {
		return this.cmdPattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Complete Pattern accepts 'srevert {--full} -r|p|h
	 * [args]'.
	 */
	@Override
	public Pattern getCompletePattern() {
		return this.completePattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Template Method - Process the matcher to extract the logical unit, target
	 * flag and arguments.
	 */
	@Override
	public boolean processMatcher(final Matcher matcher) {
		this.fullRevert = (matcher.group(this.GROUP_FULL) != null);
		return this.processTargetAndArgs(matcher);
	}

	/**
	 * Determines the type of revert and the arguments supporting the revert.
	 *
	 * @param matcher
	 *            Matcher.
	 * @return success - boolean.
	 */
	public boolean processTargetAndArgs(final Matcher matcher) {
		final String tgtFlag = "-"
				+ matcher.group(this.GROUP_TARGET).toLowerCase().charAt(0);
		final String args = matcher.group(this.GROUP_ARGS);
		if (tgtFlag.equals(this.hashFlag)) {
			if (this.hashPattern.matcher(args).matches()) {
				this.targetType = this.hashFlag;
				this.targetHash = args;
				this.targetDescription = "Hash(" + this.targetHash + ")";
				return true;
			} else
				return false;
		} else
			return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Revert logical unit to designated state.
	 */
	@Override
	public boolean execute() {
		boolean success = false;
		String resultingHash = "N/A";
		try {
			if (this.targetType.equals("-h")) {
				if (this.fullRevert) {
					success = this.stage.revertStage(this.targetHash, true);
				} else {
					success = this.stage.revert(this.targetHash);
				}
			}
			resultingHash = this.stage.getCurrentHash();
		} catch (final Exception e) {
			this.logger.exception("Unable to Revert Stage.", e);
			// Do Nothing - Success Starts As False.
		}
		if (success) {
			this.writer.print("Reverted Stage to Commit", this.cmdVerbosity);
			this.writer.println("[" + this.targetDescription + " to "
					+ resultingHash + "].\n", this.cmdVerbosity);
		} else {
			this.writer.println("FAILURE: Unable to Stage to Commit ["
					+ this.targetDescription + "].\n", this.cmdVerbosity);
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
		final StringBuilder sb = new StringBuilder("");
		sb.append("sRevert {-full} -h <hash>\t\tRevert Stage to Specified Commit (Revert Logical Units if Full).");
		return sb.toString();
	}

}
