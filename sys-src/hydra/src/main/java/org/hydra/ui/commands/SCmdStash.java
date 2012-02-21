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

import java.util.regex.Pattern;

import org.hydra.core.Stage;

/**
 * Stashes or saves the current contents of the designated stage.
 *
 * @since 0.2
 * @version 0.2
 * @author Scott A. Hady
 */
public class SCmdStash extends CommandLogicalUnit {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The Constant DEFAULT_NAME. */
	public static final String DEFAULT_NAME = "Stage Stash";

	/** The Constant DEFAULT_ID. */
	public static final String DEFAULT_ID = "SCmdStash";

	// Regular Expressions
	/** The cmd reg ex. */
	private final String cmdRegEx = "^\\s*(?i:sstash)\\b\\s*";

	/** The cmd pattern. */
	private final Pattern cmdPattern = Pattern.compile(this.cmdRegEx);

	/** The complete pattern. */
	private final Pattern completePattern = this.cmdPattern;

	/**
	 * Specialized Constructor designated which stage's contents to stash.
	 *
	 * @param stage
	 *            Stage.
	 */
	public SCmdStash(final Stage stage) {
		super(SCmdStash.DEFAULT_NAME, SCmdStash.DEFAULT_ID, stage);
	}

	/**
	 * COMMAND METHODS OVERRIDDEN *********************************************.
	 * 
	 * @return the command pattern
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Command Pattern accepts 'lustatus' as the command.
	 */
	@Override
	public Pattern getCommandPattern() {
		return this.cmdPattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Complete Pattern accepts 'lustatus {[luname]}'.
	 */
	@Override
	public Pattern getCompletePattern() {
		return this.completePattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Stash the stage's current contents.
	 */
	@Override
	public boolean execute() {
		boolean success = false;
		final StringBuilder sb = new StringBuilder("");
		success = this.stage.recordReferences();
		if (success) {
			sb.append("Stage's Content has been Stashed.\n");
		} else {
			sb.append("FAILURE: Unable to Stash the Stage's Content.\n");
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
		return "sStash\t\t\t\t\tStash the Stage's Content.";
	}

}
