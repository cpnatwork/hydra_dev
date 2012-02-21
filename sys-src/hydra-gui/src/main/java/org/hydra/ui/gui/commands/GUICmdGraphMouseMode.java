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
package org.hydra.ui.gui.commands;

import org.hydra.ui.commands.Command;
import org.hydra.ui.gui.HydraExplorer;

import edu.uci.ics.jung.visualization.control.ModalGraphMouse;

/**
 * Command that sets the graph mouse control mode either to translations or
 * selecting or picking.
 *
 * @author Scott A. Hady
 * @version 0.2
 * @since 0.2
 */
public class GUICmdGraphMouseMode extends Command {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The Constant DEFAULT_NAME. */
	public static final String DEFAULT_NAME = "Graph Mouse Mode";

	/** The Constant DEFAULT_ID. */
	public static final String DEFAULT_ID = "GUICmdGraphMouseMode";

	/** The explorer. */
	private final HydraExplorer explorer;

	/** The mouse mode. */
	private ModalGraphMouse.Mode mouseMode;

	/**
	 * Default constructor that sets the explorer on which to operate.
	 *
	 * @param tgtExplorer
	 *            HydraExplorer
	 */
	public GUICmdGraphMouseMode(final HydraExplorer tgtExplorer) {
		super(GUICmdGraphMouseMode.DEFAULT_NAME,
				GUICmdGraphMouseMode.DEFAULT_ID);
		this.explorer = tgtExplorer;
		this.mouseMode = ModalGraphMouse.Mode.TRANSFORMING;
	}

	/**
	 * Specialized constructor that sets the explorer on which to operate and
	 * the name and id of the command to use.
	 *
	 * @param cmdName
	 *            String
	 * @param cmdId
	 *            String
	 * @param tgtExplorer
	 *            HydraExplorer
	 */
	public GUICmdGraphMouseMode(final String cmdName, final String cmdId,
			final HydraExplorer tgtExplorer) {
		super(cmdName, cmdId);
		this.explorer = tgtExplorer;
		this.mouseMode = ModalGraphMouse.Mode.TRANSFORMING;
	}

	/**
	 * Specialized constructor that sets the explorer on which to operate and
	 * the name and id of the command to use.
	 *
	 * @param cmdName
	 *            String
	 * @param cmdId
	 *            String
	 * @param tgtExplorer
	 *            HydraExplorer
	 * @param mouseModus
	 *            ModalGraphMouse.Mode
	 */
	public GUICmdGraphMouseMode(final String cmdName, final String cmdId,
			final HydraExplorer tgtExplorer,
			final ModalGraphMouse.Mode mouseModus) {
		super(cmdName, cmdId);
		this.explorer = tgtExplorer;
		this.mouseMode = this.mouseMode;
	}

	/**
	 * Set the mode of the graphical mouse.
	 *
	 * @param mouseModus
	 *            ModalGraphMouse.Mode
	 */
	public void setMode(final ModalGraphMouse.Mode mouseModus) {
		this.mouseMode = this.mouseMode;
	}

	/**
	 * COMMAND METHODS (OVERRIDDEN). *******************************************
	 * 
	 * @return true, if successful
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Change the mode of the graphical mouse to either transforming or picking.
	 */
	@Override
	public boolean execute() {
		this.explorer.setGraphMouseMode(this.mouseMode);
		return true;
	}

}
