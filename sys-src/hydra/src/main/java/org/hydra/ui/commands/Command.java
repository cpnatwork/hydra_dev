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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.hydra.ui.UIWriter;
import org.hydra.utilities.Logger;

/**
 * Interface describing the basic hydra command pattern.
 *
 * @since 0.2
 * @version 0.2
 * @author Scott A. Hady
 */
public abstract class Command extends AbstractAction implements
		Comparable<Command> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6143653633215775755L;

	/** The Constant DEFAULT_VERBOSITY. */
	public static final int DEFAULT_VERBOSITY = 3;

	/** The cmd id. */
	protected String cmdId = "commandId";

	/** The cmd name. */
	protected String cmdName = "commandName";

	/** The cmd verbosity. */
	protected int cmdVerbosity = Command.DEFAULT_VERBOSITY;

	/** The writer. */
	protected UIWriter writer = null;

	/** The logger. */
	protected Logger logger = null;

	/**
	 * Specialized constructor which sets the commands name and id.
	 *
	 * @param commandName
	 *            String.
	 * @param commandId
	 *            String.
	 */
	public Command(final String commandName, final String commandId) {
		super(commandName);
		this.cmdId = commandId;
		this.cmdName = commandName;
		this.logger = Logger.getInstance();
		this.writer = UIWriter.getInstance();
	}

	/**
	 * Return the command's id, which is assumed to be unique within a
	 * collection.
	 *
	 * @return commandId - String.
	 */
	public String getId() {
		return this.cmdId;
	}

	/**
	 * Return the command's name, which does not need to be unique within a
	 * collection.
	 *
	 * @return commandName - String.
	 */
	public String getName() {
		return this.cmdName;
	}

	/**
	 * Return the command's verbosity level.
	 *
	 * @return verbosityLevel - int.
	 */
	public int getVerbosity() {
		return this.cmdVerbosity;
	}

	/**
	 * Execute the command's actions.
	 *
	 * @return success - boolean.
	 */
	public abstract boolean execute();

	/**
	 * Set the command's id, which is assumed to be unique within a collection.
	 *
	 * @param commandId
	 *            String.
	 */
	public void setId(final String commandId) {
		this.cmdId = commandId;
	}

	/**
	 * Set the command's name, which does not need to be unique within a
	 * collection.
	 *
	 * @param commandName
	 *            String.
	 */
	public void setName(final String commandName) {
		this.cmdName = commandName;
	}

	/**
	 * Set the command's verbosity level.
	 *
	 * @param verbosityLevel
	 *            int.
	 */
	public void setVerbosity(final int verbosityLevel) {
		this.cmdVerbosity = verbosityLevel;
	}

	/**
	 * ABSTRACTACTION METHODS (OVERRIDDEN) ************************************.
	 * 
	 * @param event
	 *            the event
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Redirects method to use the execute method.
	 */
	@Override
	public void actionPerformed(final ActionEvent event) {
		this.execute();
	}

	/**
	 * COMPARABLE METHODS (IMPLEMENTED) ***************************************.
	 * 
	 * @param comparedCommand
	 *            the compared command
	 * @return the int
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Compare this command to another command and determine which command's ID
	 * comes first.
	 */
	@Override
	public int compareTo(final Command comparedCommand) {
		return this.cmdId.compareTo(comparedCommand.getId());
	}

}
