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
package org.hydra.core;

import java.io.File;

import org.hydra.TH;
import org.hydra.utilities.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * The Class StageStateTest.
 */
public class StageStateTest {

	/** The s1. */
	Stage s1;

	/** The ss1. */
	StageState ss1;

	/**
	 * Start class test.
	 */
	@BeforeClass
	public static void startClassTest() {
		TH.setupLogging();
		Logger.getInstance().info("STAGESTATE TEST");
	}

	/**
	 * Setup test.
	 */
	@Before
	public void setupTest() {
		TH.setupTestingEnvironment(true, true);
	}

	/**
	 * Ss_create.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void ss_create() throws InvalidElementException {
		this.s1 = new Stage();
		this.ss1 = new StageState(this.s1, "Scott", "StageState Test.");
		final File rFile = new File(TH.fpStore, this.ss1.getHash());
		Assert.assertFalse("Should Not Exist.", rFile.exists());
		Assert.assertTrue("Should Store.", this.ss1.store());
		Assert.assertTrue("Should Exist.", rFile.exists());
		// System.out.println("\nStage Description:\n"+s1.describe());
		// System.out.println("\nStageState Description:\n"+ss1.describe());
		Assert.assertTrue("Description Should Contain LogicalUnit.", this.ss1
				.describe().contains("LS::>>aCard1::>>"));
	}

}
