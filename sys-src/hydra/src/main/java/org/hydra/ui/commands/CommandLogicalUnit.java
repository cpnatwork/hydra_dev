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

import org.hydra.core.Stage;

/**
 * Provides encapsulation of functionality common to all commands operating on a
 * specific logical unit.
 *
 * @since 0.2
 * @version 0.2
 * @author Scott A. Hady
 */
public class CommandLogicalUnit extends CommandRegex {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The stage. */
	protected Stage stage;

	/** The lu name. */
	protected String luName;

	/**
	 * Specialized Constructor which specifies which stage to manipulate.
	 *
	 * @param commandName
	 *            String.
	 * @param commandId
	 *            String.
	 * @param stage
	 *            Stage.
	 */
	public CommandLogicalUnit(final String commandName, final String commandId,
			final Stage stage) {
		super(commandName, commandId);
		this.stage = stage;
	}

	/**
	 * Specialized Constructor which specifies which stage and the name of the
	 * logical unit to manipulate.
	 *
	 * @param commandName
	 *            String.
	 * @param commandId
	 *            String.
	 * @param stage
	 *            Stage.
	 * @param luName
	 *            String.
	 */
	public CommandLogicalUnit(final String commandName, final String commandId,
			final Stage stage, final String luName) {
		super(commandName, commandId);
		this.stage = stage;
		this.luName = luName;
	}

	/**
	 * COMMANDLOGICALUNIT METHODS *********************************************.
	 * 
	 * @param targetName
	 *            the target name
	 * @return true, if successful
	 */

	/**
	 * Determine the appropriate logical unit name to use. If the luName given
	 * is equal to null than the stage's focus is used, if available.
	 *
	 * @param targetName
	 *            String.
	 * @return success - boolean.
	 */
	public boolean processLogicalUnitName(final String targetName) {
		if (targetName == null) {
			if (this.stage.isFocused()) {
				this.luName = this.stage.getFocus().getName();
				return true;
			} else
				return false;
		} else {
			this.luName = targetName;
			return true;
		}
	}

}
