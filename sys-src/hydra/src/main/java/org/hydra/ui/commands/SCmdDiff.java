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

import org.hydra.core.FingerprintedElement;
import org.hydra.core.Stage;
import org.hydra.core.State;
import org.hydra.utilities.diff.hmdiff.HMDiff;

/**
 * Calculates the differential of element's of the designated logical unit.
 *
 * @since 0.1
 * @version 0.2
 * @author Scott A. Hady
 */
public class SCmdDiff extends CommandStage {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The Constant DEFAULT_NAME. */
	public static final String DEFAULT_NAME = "Stage Diff";

	/** The Constant DEFAULT_ID. */
	public static final String DEFAULT_ID = "SCmdDiff";

	/** The e name. */
	private String eName;
	// Regular Expressions
	/** The cmd reg ex. */
	private final String cmdRegEx = "^\\s*(?i:sdiff)\\b";

	/** The el reg ex. */
	private final String elRegEx = "\\s+-e\\s+(\\S+.*)(\\\\|\\/)?\\s*$";

	/** The cmd pattern. */
	private final Pattern cmdPattern = Pattern.compile(this.cmdRegEx);

	/** The complete pattern. */
	private final Pattern completePattern = Pattern.compile(this.cmdRegEx
			+ this.elRegEx);

	/** The GROU p_ element. */
	private final int GROUP_ELEMENT = 1;

	/**
	 * Specialized Constuctor which designates the stage to use to find the
	 * element to calculate differential's.
	 *
	 * @param stage
	 *            Stage.
	 */
	public SCmdDiff(final Stage stage) {
		super(SCmdDiff.DEFAULT_NAME, SCmdDiff.DEFAULT_ID, stage);
	}

	/**
	 * Specialized Constructor which specifies the stage and the element's name.
	 *
	 * @param stage
	 *            Stage.
	 * @param eName
	 *            String.
	 */
	public SCmdDiff(final Stage stage, final String eName) {
		super(SCmdDiff.DEFAULT_NAME, SCmdDiff.DEFAULT_ID, stage);
		this.eName = eName;
	}

	/**
	 * COMMAND METHODS OVERRIDDEN *********************************************.
	 * 
	 * @return the command pattern
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Command Pattern accepts 'sdiff' as the command.
	 */
	@Override
	public Pattern getCommandPattern() {
		return this.cmdPattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Complete Pattern accepts 'sdiff -e [ename]'.
	 */
	@Override
	public Pattern getCompletePattern() {
		return this.completePattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Template Method - Process the matcher to extract the logical unit and
	 * element name.
	 */
	@Override
	public boolean processMatcher(final Matcher matcher) {
		this.eName = matcher.group(this.GROUP_ELEMENT).trim();
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Remove the designated element to the designated logical unit.
	 */
	@Override
	public boolean execute() {
		boolean success = true;
		try {
			final String fullName = this.findElementName(this.eName);
			final FingerprintedElement activeElement = this.stage.getContents()
					.getElement(fullName.substring(1));
			if (activeElement == null) {
				this.writer.println("Unable to Find Element [" + this.eName
						+ "] in Stage Content's Workspace.\n");
				return false;
			}
			final State currentState = this.stage.getCurrent();
			if (currentState == null) {
				this.writer.println("Unable to Find Element [" + this.eName
						+ "] in Stage Current's Repository.");
				this.writer.println("\tStage Has Not Been Committed.\n");
				return false;
			}
			final FingerprintedElement committedElement = currentState
					.cloneContents().getElement(fullName.substring(1));
			if (committedElement == null) {
				this.writer.println("Unable to Find Element [" + this.eName
						+ "] in Stage Current's Repository.\n");
				return false;
			}

			final HMDiff diff = new HMDiff(
					committedElement.cloneRepositoryFile(),
					activeElement.cloneWorkspaceFile());
			this.writer.println(diff.describeTransformation(),
					this.cmdVerbosity);
		} catch (final Exception e) {
			this.logger
					.exception(
							"(CMD) Unable to Calculate the Differential of Stage Element.",
							e);
			success = false;
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
		return "sDiff -e <eName>\t\t\tProvides a Differential of the Stage's Element.";
	}

}
