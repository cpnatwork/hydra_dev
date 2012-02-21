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
package org.hydra.ui;

import org.hydra.core.InvalidElementException;
import org.hydra.core.Stage;
import org.hydra.ui.commands.CmdExit;
import org.hydra.ui.commands.CmdHelp;
import org.hydra.ui.commands.CmdList;
import org.hydra.ui.commands.CmdLog;
import org.hydra.ui.commands.CmdSetUser;
import org.hydra.ui.commands.CmdStatus;
import org.hydra.ui.commands.CmdVerbose;
import org.hydra.ui.commands.CommandSet;
import org.hydra.ui.commands.LUCmdCommit;
import org.hydra.ui.commands.LUCmdDiff;
import org.hydra.ui.commands.LUCmdElementAdd;
import org.hydra.ui.commands.LUCmdElementRemove;
import org.hydra.ui.commands.LUCmdLog;
import org.hydra.ui.commands.LUCmdReset;
import org.hydra.ui.commands.LUCmdRevert;
import org.hydra.ui.commands.LUCmdStash;
import org.hydra.ui.commands.LUCmdStatus;
import org.hydra.ui.commands.SCmdCommit;
import org.hydra.ui.commands.SCmdCreateLU;
import org.hydra.ui.commands.SCmdDeleteLU;
import org.hydra.ui.commands.SCmdDiff;
import org.hydra.ui.commands.SCmdElementAdd;
import org.hydra.ui.commands.SCmdElementRemove;
import org.hydra.ui.commands.SCmdFocusLU;
import org.hydra.ui.commands.SCmdIgnoreLU;
import org.hydra.ui.commands.SCmdLog;
import org.hydra.ui.commands.SCmdManageLU;
import org.hydra.ui.commands.SCmdReset;
import org.hydra.ui.commands.SCmdRevert;
import org.hydra.ui.commands.SCmdStash;
import org.hydra.ui.commands.SCmdStatus;
import org.hydra.utilities.Logger;

/**
 * Abstract base class representing a hydra user interface and manages the hydra
 * stage and command set to manipulate.
 * 
 * @since 0.2
 * @version 0.2
 * @author Scott A. Hady
 */
public abstract class UI {

	/** The verbosity. */
	protected int verbosity;

	/** The stage. */
	protected Stage stage;

	/** The commands. */
	protected CommandSet commands;

	/** The logger. */
	protected Logger logger = null;

	/**
	 * Default constructor that logs a notice, creates operational stage and
	 * initializes the command list.
	 * 
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public UI() throws InvalidElementException {
		this.logger = Logger.getInstance();
		this.logger.info("Starting Hydra UI.");

		// this.logger.info("Create Stage");
		this.stage = new Stage();
		// this.logger.info("Create CommandSet");
		this.commands = new CommandSet();
		// this.logger.info("Initialize Commands");
		this.initializeCommands();
	}

	/**
	 * Specialized constructor that logs the notice but uses the injected stage
	 * and set of commands.
	 * 
	 * @param stage
	 *            Stage.
	 * @param commands
	 *            CommandSet.
	 */
	public UI(final Stage stage, final CommandSet commands) {
		this.logger = Logger.getInstance();
		this.logger.info("Starting Hydra UI.");
		this.stage = stage;
		this.commands = commands;
	}

	/**
	 * UI METHODS *************************************************************.
	 * 
	 * @return the commands
	 */

	/**
	 * Retrieve the ui's command set.
	 * 
	 * @return CommandSet commands.
	 */
	public CommandSet getCommands() {
		return this.commands;
	}

	/**
	 * Retrieve the ui's working stage.
	 * 
	 * @return stage Stage.
	 */
	public Stage getStage() {
		return this.stage;
	}

	/**
	 * Retrieve the ui's current verbosity level.
	 * 
	 * @return verbosityLevel - int.
	 */
	public int getVerbosity() {
		return this.verbosity;
	}

	/**
	 * Set the amount of executional information to be displayed.
	 * 
	 * @param verbosityLevel
	 *            int.
	 */
	public void setVerbosity(final int verbosityLevel) {
		this.verbosity = verbosityLevel;
	}

	/**
	 * UI ABSTRACT/OVERRIDABLE METHODS ****************************************.
	 */

	/**
	 * Intialize all of the commands and add them to the command list.
	 */
	protected void initializeCommands() {
		// General Commands
		this.commands.add(new CmdHelp(this.commands));
		this.commands.add(new CmdSetUser());
		this.commands.add(new CmdVerbose());
		this.commands.add(new CmdStatus(this));
		this.commands.add(new CmdLog());
		this.commands.add(new CmdList());
		this.commands.add(new CmdExit());
		// Stage Commands
		this.commands.add(new SCmdStatus(this.stage));
		this.commands.add(new SCmdCreateLU(this.stage));
		this.commands.add(new SCmdDeleteLU(this.stage));
		this.commands.add(new SCmdManageLU(this.stage));
		this.commands.add(new SCmdIgnoreLU(this.stage));
		this.commands.add(new SCmdFocusLU(this.stage));
		this.commands.add(new SCmdCommit(this.stage));
		this.commands.add(new SCmdRevert(this.stage));
		this.commands.add(new SCmdReset(this.stage));
		this.commands.add(new SCmdLog(this.stage));
		this.commands.add(new SCmdElementAdd(this.stage));
		this.commands.add(new SCmdElementRemove(this.stage));
		this.commands.add(new SCmdStash(this.stage));
		this.commands.add(new SCmdDiff(this.stage));
		// Logical Unit Commands
		this.commands.add(new LUCmdStatus(this.stage));
		this.commands.add(new LUCmdCommit(this.stage));
		this.commands.add(new LUCmdRevert(this.stage));
		this.commands.add(new LUCmdReset(this.stage));
		this.commands.add(new LUCmdLog(this.stage));
		this.commands.add(new LUCmdElementAdd(this.stage));
		this.commands.add(new LUCmdElementRemove(this.stage));
		this.commands.add(new LUCmdStash(this.stage));
		this.commands.add(new LUCmdDiff(this.stage));
	}

	/**
	 * Start interacting with user.
	 */
	public abstract void interact();

}
