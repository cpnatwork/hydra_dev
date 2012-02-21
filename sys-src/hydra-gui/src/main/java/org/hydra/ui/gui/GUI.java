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
package org.hydra.ui.gui;

import org.hydra.core.InvalidElementException;
import org.hydra.ui.UI;

/**
 * Graphical User Interface starter class responsible for initializing the
 * Hydra's GUI, initializing and managing the commands and starting the
 * GUIThread.
 *
 * @since 0.2
 * @version 0.2
 * @author Scott A. Hady
 */
public class GUI extends UI {

	/** The gui thread. */
	private GUIThread guiThread;

	/** The hydra frame. */
	private HydraFrame hydraFrame;

	/**
	 * Default constructor that initializes the user interface.
	 *
	 * @throws org.hydra.core.InvalidElementException
	 *             invalidElement.
	 */
	public GUI() throws InvalidElementException {
		super();
	}

	/**
	 * GUI METHODS ***********************************************************.
	 */

	/**
	 * Creates and shows the Hydra GUI.
	 */
	protected void createAndShowHydraFrame() {
		this.hydraFrame = new HydraFrame(this.stage, this.commands);
		this.hydraFrame.setVisible(true);
	}

	/**
	 * UI METHODS (OVERRIDDEN) ************************************************.
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Initializes Hydra GUI interactive mode by launching a GUI Thread, which
	 * in-turn initializes the GUI and waits for it's exit.
	 */
	@Override
	public void interact() {
		this.guiThread = new GUIThread(this);
		try {
			this.guiThread.start();
			this.guiThread.join();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

}
