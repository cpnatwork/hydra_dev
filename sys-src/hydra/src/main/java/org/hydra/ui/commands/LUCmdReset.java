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
 * Resets the workspace either to the state specified by the logical unit's head
 * or current state.
 *
 * @since 0.1
 * @version 0.2
 * @author Scott A. Hady
 */
public class LUCmdReset extends CommandLogicalUnit {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The Constant DEFAULT_NAME. */
	public static final String DEFAULT_NAME = "LogicalUnit Reset";

	/** The Constant DEFAULT_ID. */
	public static final String DEFAULT_ID = "LUCmdReset";

	/** The target. */
	private String target;

	/** The default target. */
	private final String defaultTarget = "-CURRENT";
	// Regular Expressions
	/** The cmd reg ex. */
	private final String cmdRegEx = "^\\s*(?i:lureset)\\b";

	/** The lu reg ex. */
	private final String luRegEx = "(\\s+(\\S+.*(?<!\\s-(?i:c|current|h|head)))\\b)?";

	/** The tgt reg ex. */
	private final String tgtRegEx = "(\\s+(\\S+.*)\\b)?\\s*$";

	/** The cmd pattern. */
	private final Pattern cmdPattern = Pattern.compile(this.cmdRegEx);

	/** The complete pattern. */
	private final Pattern completePattern = Pattern.compile(this.cmdRegEx
			+ this.luRegEx + this.tgtRegEx);

	/** The GROU p_ logicalunit. */
	private final int GROUP_LOGICALUNIT = 2;

	/**
	 * Specialized Constructor which designates which stage use to find the
	 * logical unit's to reset.
	 *
	 * @param stage
	 *            Stage.
	 */
	public LUCmdReset(final Stage stage) {
		super(LUCmdReset.DEFAULT_NAME, LUCmdReset.DEFAULT_ID, stage);
	}

	/**
	 * Specialized constructor that specifies which logical unit's to reset and
	 * which target (--HEAD or --CURRENT).
	 *
	 * @param stage
	 *            Stage.
	 * @param luName
	 *            String.
	 * @param target
	 *            String.
	 */
	public LUCmdReset(final Stage stage, final String luName,
			final String target) {
		super(LUCmdReset.DEFAULT_NAME, LUCmdReset.DEFAULT_ID, stage, luName);
		this.target = target;
	}

	/**
	 * COMMAND METHODS OVERRIDDEN *********************************************.
	 * 
	 * @return the command pattern
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Pattern accepts 'lureset' as the command.
	 */
	@Override
	public Pattern getCommandPattern() {
		return this.cmdPattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Complete Pattern accepts 'lureset {[luname]} {-h|c}'.
	 */
	@Override
	public Pattern getCompletePattern() {
		return this.completePattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Template Method - Process the matcher to extract the logical unit, target
	 * flag.
	 */
	@Override
	public boolean processMatcher(final Matcher matcher) {
		this.processTarget(matcher);
		return this.processLogicalUnitName(matcher
				.group(this.GROUP_LOGICALUNIT));
	}

	/**
	 * Determine the target to defining the state to which to reset the
	 * workspace.
	 * 
	 * @param matcher
	 *            Matcher.
	 */
	private void processTarget(final Matcher matcher) {
		if (matcher.group(4) == null) {
			this.target = this.defaultTarget;
		} else {
			this.target = matcher.group(4).toLowerCase();
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * Reset the workspace to the target commit (HEAD or CURRENT).
	 */
	@Override
	public boolean execute() {
		boolean success = false;
		try {
			final LogicalUnit lu = this.stage.getLogicalUnit(this.luName);
			if (this.isHead(this.target)) {
				lu.setContents(lu.getHead().cloneContents());
				success = lu.getHead().retrieve();
			} else if (this.isCurrent(this.target)) {
				lu.setContents(lu.getCurrent().cloneContents());
				success = this.stage.getLogicalUnit(this.luName).getCurrent()
						.retrieve();
			}
		} catch (final Exception e) {
			this.logger.exception(
					"[LUCmdReset.execute()] Unable to Reset Logical Unit.", e);
			// Do Nothing - Success Starts As False.
		}
		if (success) {
			this.writer.print("Reset Logical Unit [" + this.luName
					+ "] Workspace", this.cmdVerbosity);
			this.writer.println(" to ["
					+ (this.isCurrent(this.target) ? "CURRENT" : "HEAD")
					+ "].\n", this.cmdVerbosity);
		} else {
			this.writer.print("FAILURE: Unable to Reset Logical Unit ["
					+ this.luName + "] Workspace", this.cmdVerbosity);
			this.writer.println(" to [" + this.target + "].\n",
					this.cmdVerbosity);
		}
		return success;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return a string describing the command's usage.
	 */
	@Override
	public String getUsage() {
		return "luReset {<luName>} {-C|-H}\t\tReset Workspace's Content to CURRENT (default) or HEAD.";
	}

	/**
	 * CMDLURESET PRIVATE METHODS *********************************************.
	 * 
	 * @param target
	 *            the target
	 * @return true, if is current
	 */

	private boolean isCurrent(final String target) {
		return ((target.charAt(1) == 'c') || (target.charAt(1) == 'C'));
	}

	/**
	 * Checks if is head.
	 * 
	 * @param target
	 *            the target
	 * @return true, if is head
	 */
	private boolean isHead(final String target) {
		return ((target.charAt(1) == 'h') || (target.charAt(1) == 'H'));
	}
}
