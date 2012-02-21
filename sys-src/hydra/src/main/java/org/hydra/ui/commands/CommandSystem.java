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

/**
 * Provides an encapsulation of common functionality used by all commands
 * operating on the overall hydra system.
 *
 * @since 0.2
 * @version 0.2
 * @author Scott A. Hady
 */
public class CommandSystem extends CommandRegex {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/**
	 * Instantiates a new command system.
	 *
	 * @param commandName
	 *            the command name
	 * @param commandId
	 *            the command id
	 */
	public CommandSystem(final String commandName, final String commandId) {
		super(commandName, commandId);
	}

}
