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
 * The Class AcceptanceTestCore.
 */
public class AcceptanceTestCore {

	/** The s1. */
	Stage s1 = null;

	/**
	 * Start class test.
	 */
	@BeforeClass
	public static void startClassTest() {
		TH.setupLogging();
		Logger.getInstance().info("CORE ACCEPTANCE TEST");
	}

	/**
	 * Setup test.
	 */
	@Before
	public void setupTest() {
		TH.setupTestingEnvironment(true, true);
	}

	/**
	 * Cat_ committing lu.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void cat_CommittingLU() throws InvalidElementException {
		this.s1 = new Stage(TH.workspace);
		// Create and Commit New Logical Unit.
		final LogicalUnit lu2 = this.s1.createLogicalUnit("aCard2");
		lu2.getContents().addElement(new Artifact(TH.w1File));
		final String commitHash = lu2.commit("Scott",
				"New Logical Unit Initial Commit.");
		Assert.assertTrue("Commit File Not Exists.", new File(TH.fpStore,
				commitHash).exists());
		// Compare New with Equivalent Logical Unit.
		final LogicalUnit lu2x = new LogicalUnit("aCard2");
		Assert.assertEquals("LU2 equals LU2X.", lu2.getName(), lu2x.getName());
		Assert.assertEquals("Should Still be Two Logical Units.", 2,
				this.s1.countManaged());
		Assert.assertEquals("LU2X has 1 Element.", 1, lu2x.getContents()
				.countElements());
		Assert.assertEquals("LU2 and LU2X Descriptions Different.",
				lu2.describe(), lu2x.describe());
	}

	/**
	 * Cat_ reverting l u1.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void cat_RevertingLU1() throws InvalidElementException {
		TH.setupTestingEnvironment(false, true);
		this.s1 = new Stage(TH.workspace);
		Assert.assertFalse("W1File Exists.", TH.w1File.exists());
		final String headHash = this.s1.getLogicalUnit("aCard1").getHead()
				.getFingerprint().getHash();
		Assert.assertTrue("Not Reverted.", this.s1.getLogicalUnit("aCard1")
				.revert(headHash));
		Assert.assertTrue("W1File Not Retrieved.", TH.w1File.exists());
	}

}
