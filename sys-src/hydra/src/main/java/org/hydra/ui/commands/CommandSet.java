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

import java.util.TreeMap;

/**
 * Encapsulates a collection of commands that may be accessed by their Id.
 *
 * @since 0.2
 * @version 0.2
 * @author Scott A. Hady
 */
public class CommandSet {

	/** The commands. */
	private final TreeMap<String, Command> commands = new TreeMap<String, Command>();

	/**
	 * Adds another command to this collection.
	 *
	 * @param command
	 *            Command.
	 * @return success - boolean.
	 */
	public boolean add(final Command command) {
		return (this.commands.put(command.getId(), command) == command);
	}

	/**
	 * Removes all commands from this collection.
	 */
	public void clear() {
		this.commands.clear();
	}

	/**
	 * Retrieves a command identified by its id.
	 *
	 * @param commandId
	 *            String.
	 * @return specifiedCommand - Command.
	 */
	public Command findById(final String commandId) {
		return this.commands.get(commandId);
	}

	/**
	 * Removes a command with the given id from the collection.
	 *
	 * @param commandId
	 *            String.
	 * @return success - boolean.
	 */
	public boolean remove(final String commandId) {
		return (this.commands.remove(commandId) != null);
	}

	/**
	 * Returns an array of all the commands currently stored in the collection.
	 *
	 * @return commandArray - Command[].
	 */
	public Command[] toArray() {
		return this.commands.values()
				.toArray(new Command[this.commands.size()]);
	}
}
