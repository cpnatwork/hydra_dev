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

/**
 * Thread that provides gui execution to continue until the GUI closes.
 *
 * @since 0.2
 * @version 0.2
 * @author Scott A. Hady
 */
public class GUIThread extends Thread {

	/** The gui. */
	private final GUI gui;

	/**
	 * Specialized constructor that receives the GUI that should be observed.
	 *
	 * @param tgtGui
	 *            GUI.
	 */
	public GUIThread(final GUI tgtGui) {
		this.gui = tgtGui;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Run method that creates the GUI and waits until it closes.
	 */
	@Override
	public void run() {
		System.out.println("Launching GUI...");
		this.gui.createAndShowHydraFrame();
		while (this.isAlive()) {
			try {
				Thread.sleep(1000);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}
}
