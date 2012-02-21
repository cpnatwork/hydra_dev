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
import org.hydra.core.Path;
import org.hydra.core.Stage;

/**
 * Revert a specified logical unit to a previously persisted state.
 *
 * @since 0.1
 * @version 0.2
 * @author Scott A. Hady
 */
public class LUCmdRevert extends CommandLogicalUnit {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The Constant DEFAULT_NAME. */
	public static final String DEFAULT_NAME = "LogicalUnit Revert";

	/** The Constant DEFAULT_ID. */
	public static final String DEFAULT_ID = "LUCmdRevert";

	// Constants
	/** The hash flag. */
	private final String hashFlag = "-h";

	/** The relative flag. */
	private final String relativeFlag = "-r";

	/** The path flag. */
	private final String pathFlag = "-p";
	// Parameters
	/** The target type. */
	private String targetType;

	/** The target hash. */
	private String targetHash;

	/** The target branch. */
	private int targetBranch;

	/** The target distance. */
	private int targetDistance;

	/** The target path. */
	private Path targetPath;

	/** The target description. */
	private String targetDescription;
	// Regular Expressions.
	/** The cmd reg ex. */
	private final String cmdRegEx = "^\\s*(?i:lurevert)\\b\\s*";

	/** The lu reg ex. */
	private final String luRegEx = "(\\s+(\\S+.*))?\\b";

	/** The tgt reg ex. */
	private final String tgtRegEx = "\\s+(?i:-(h|hash|p|path|r|relative))\\s+(\\S+.*)\\b\\s*$";

	/** The cmd pattern. */
	private final Pattern cmdPattern = Pattern.compile(this.cmdRegEx);

	/** The complete pattern. */
	private final Pattern completePattern = Pattern.compile(this.cmdRegEx
			+ this.luRegEx + this.tgtRegEx);
	// Extended Regular Expressions.
	/** The hash reg ex. */
	private final String hashRegEx = "^\\s*\\w{40}\\s*$";

	/** The path reg ex. */
	private final String pathRegEx = "^\\s*(\\*((-)?\\d)*\\+((-)?\\d)*)+\\s*$";

	/** The relative reg ex. */
	private final String relativeRegEx = "^\\s*(((-)?(\\d)*)\\s+((-)?(\\d)*))\\s*$";

	/** The hash pattern. */
	private final Pattern hashPattern = Pattern.compile(this.hashRegEx);

	/** The path pattern. */
	private final Pattern pathPattern = Pattern.compile(this.pathRegEx);

	/** The relative pattern. */
	private final Pattern relativePattern = Pattern.compile(this.relativeRegEx);

	/** The GROU p_ target. */
	private final int GROUP_TARGET = 3;

	/** The GROU p_ args. */
	private final int GROUP_ARGS = 4;

	/** The GROU p_ branch. */
	private final int GROUP_BRANCH = 2;

	/** The GROU p_ distance. */
	private final int GROUP_DISTANCE = 5;

	/**
	 * Specialized Constructor which designates which stage use to find the
	 * logical unit's to revert.
	 *
	 * @param stage
	 *            Stage.
	 */
	public LUCmdRevert(final Stage stage) {
		super(LUCmdRevert.DEFAULT_NAME, LUCmdRevert.DEFAULT_ID, stage);
	}

	/**
	 * Specialized constructor that specifies which logical unit's to revert and
	 * to which hash to revert.
	 *
	 * @param stage
	 *            Stage.
	 * @param luName
	 *            String.
	 * @param targetHash
	 *            String.
	 */
	public LUCmdRevert(final Stage stage, final String luName,
			final String targetHash) {
		super(LUCmdRevert.DEFAULT_NAME, LUCmdRevert.DEFAULT_ID, stage, luName);
		this.targetType = this.hashFlag;
		this.targetHash = targetHash;
		this.targetDescription = "Hash(" + this.targetHash + ")";
	}

	/**
	 * Specialized constructor that specifies which logical unit's to revert and
	 * the path along which to revert.
	 *
	 * @param stage
	 *            Stage.
	 * @param luName
	 *            String.
	 * @param targetPath
	 *            Path.
	 */
	public LUCmdRevert(final Stage stage, final String luName,
			final Path targetPath) {
		super(LUCmdRevert.DEFAULT_NAME, LUCmdRevert.DEFAULT_ID, stage, luName);
		this.targetType = this.pathFlag;
		this.targetPath = targetPath;
		this.targetDescription = "Path(" + this.targetPath + ")";
	}

	/**
	 * Specialized constructor that specifies which logical unit's to revert and
	 * which relative branch and position.
	 *
	 * @param stage
	 *            Stage.
	 * @param luName
	 *            String.
	 * @param targetBranch
	 *            int.
	 * @param targetDistance
	 *            int.
	 */
	public LUCmdRevert(final Stage stage, final String luName,
			final int targetBranch, final int targetDistance) {
		super(LUCmdRevert.DEFAULT_NAME, LUCmdRevert.DEFAULT_ID, stage, luName);
		this.targetType = this.relativeFlag;
		this.targetBranch = targetBranch;
		this.targetDistance = targetDistance;
		this.targetDescription = "Relative(" + this.targetBranch + ","
				+ this.targetDistance + ")";
	}

	/**
	 * COMMAND METHODS OVERRIDDEN *********************************************.
	 * 
	 * @return the command pattern
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Pattern accepts 'lurevert' as the command.
	 */
	@Override
	public Pattern getCommandPattern() {
		return this.cmdPattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Complete Pattern accepts 'lurevert {[luname]} -r|p|h
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
		if (this.processLogicalUnitName(matcher.group(2)))
			return this.processTargetAndArgs(matcher);
		return false;
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
		try {
			if (tgtFlag.equals(this.hashFlag)) {
				if (this.hashPattern.matcher(args).matches()) {
					this.targetType = this.hashFlag;
					this.targetHash = args;
					this.targetDescription = "Hash(" + this.targetHash + ")";
					return true;
				} else
					return false;
			} else if (tgtFlag.equals(this.pathFlag)) {
				if (this.pathPattern.matcher(args).matches()) {
					this.targetType = this.pathFlag;
					this.targetPath = new Path(args);
					this.targetDescription = "Path(" + this.targetPath + ")";
					return true;
				} else
					return false;
			} else if (tgtFlag.equals(this.relativeFlag)) {
				final Matcher relativeMatcher = this.relativePattern
						.matcher(args);
				if (relativeMatcher.matches()) {
					this.targetType = this.relativeFlag;
					this.targetBranch = Integer.parseInt(relativeMatcher
							.group(this.GROUP_BRANCH));
					this.targetDistance = Integer.parseInt(relativeMatcher
							.group(this.GROUP_DISTANCE));
					this.targetDescription = "Relative(" + this.targetBranch
							+ "," + this.targetDistance + ")";
					return true;
				} else
					return false;
			} else
				return false;
		} catch (final Exception e) {
			return false;
		}
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
			final LogicalUnit lu = this.stage.getLogicalUnit(this.luName);
			if (this.targetType.equals("-h")) {
				success = lu.revert(this.targetHash);
			} else if (this.targetType.equals("-r")) {
				success = lu.revertRelative(this.targetBranch,
						this.targetDistance);
			} else if (this.targetType.equals("-p")) {
				success = lu.revertPath(this.targetPath);
			}
			if (success) {
				resultingHash = lu.getCurrentHash();
			}
		} catch (final Exception e) {
			this.logger.exception("Unable to Revert Logical Unit.", e);
			// Do Nothing - Success Starts As False.
		}
		if (success) {
			this.writer.print("Reverted Logical Unit " + this.luName
					+ " to Commit", this.cmdVerbosity);
			this.writer.println("[" + this.targetDescription + " to "
					+ resultingHash + "].\n", this.cmdVerbosity);
		} else {
			this.writer.print("FAILURE: Unable to Revert [" + this.luName
					+ "] to Commit", this.cmdVerbosity);
			this.writer.println("[" + this.targetDescription + "].\n",
					this.cmdVerbosity);
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
		sb.append("luRevert {<luName>} -h <hash>\t\tRevert to (Hash) Specified Logical Unit's Commit.\n");
		sb.append("\tluRevert {<luName>} -r <br> <dis>\tRevert to Commit Relative to CURRENT.\n");
		sb.append("\tluRevert {<luName>} -p <path>\t\tRevert to Commit Identified by Path from HEAD.");
		return sb.toString();
	}

}
