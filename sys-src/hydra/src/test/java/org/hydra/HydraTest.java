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
package org.hydra;

import org.hydra.utilities.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * The Class HydraTest.
 */
public class HydraTest {

	/** The hydra. */
	Hydra hydra;

	/**
	 * Start class test.
	 */
	@BeforeClass
	public static void startClassTest() {
		TH.setupLogging();
		Logger.getInstance().clearLog();
		Logger.getInstance().info("HYDRA TEST");
	}

	/**
	 * Setup test.
	 */
	@Before
	public void setupTest() {
		TH.setupLogging();
	}

	/**
	 * Hydra_ statics.
	 */
	@Test
	public void hydra_Statics() {
		Assert.assertEquals("Hydra Name.", "Hydra", Hydra.getName());
		Assert.assertEquals("Hydra Description.",
				"Multi-Headed Version Control System", Hydra.getDescription());
		Assert.assertEquals("Hydra Version.", "0.2", Hydra.getVersion());
		Assert.assertEquals("Hydra Name.", "201107-01", Hydra.getBuildNumber());
	}

}
