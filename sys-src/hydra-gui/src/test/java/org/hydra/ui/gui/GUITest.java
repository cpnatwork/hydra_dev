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
import org.hydra.core.Stage;
import org.hydra.utilities.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * The Class GUITest.
 */
public class GUITest {

	/** The gui. */
	GUI gui;

	/**
	 * Start class test.
	 */
	@BeforeClass
	public static void startClassTest() {
		TH.setupLogging();
		Logger.getInstance().info("GUI TEST");
	}

	/**
	 * Setup test.
	 */
	@Before
	public void setupTest() {
		TH.setupTestingEnvironment(true, true);
	}

	/**
	 * Gui_creation.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void gui_creation() throws InvalidElementException {
		final Stage stage = new Stage();
		stage.commit("Scott", "Stage Commit.");
		stage.commit("Scott", "Second Stage Commit.");
		this.gui = new GUI();
		this.gui.createAndShowHydraFrame();
	}

}
