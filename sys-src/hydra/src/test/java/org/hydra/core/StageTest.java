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
 * The Class StageTest.
 */
public class StageTest {

	/** The s1. */
	Stage s1 = null;

	/**
	 * Start class test.
	 */
	@BeforeClass
	public static void startClassTest() {
		TH.setupLogging();
		Logger.getInstance().info("STAGE TEST");
	}

	/**
	 * Setup test.
	 */
	@Before
	public void setupTest() {
		TH.setupTestingEnvironment(true, true);
	}

	/**
	 * Sg_creation.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void sg_creation() throws InvalidElementException {
		final Configuration config = Configuration.getInstance();
		this.s1 = new Stage(TH.workspaceParent);
		Assert.assertEquals("Incorrect Name.", "STAGE", this.s1.getName());
		Assert.assertEquals("Incorrect Workspace1.", TH.workspaceParent,
				config.getWorkspace());
		Assert.assertEquals("Incorrect Repository1.", new File(
				TH.workspaceParent, ".hydra"), config.getRepository());
		// assertFalse("Repository1 Exists.",
		// config.getRepository().exists());//BIN DIR Not Always Cleaned.
		this.s1 = new Stage();
		Assert.assertNotNull("Stage is Null.", this.s1);
		Assert.assertEquals("Incorrect Workspace2.", TH.workspaceParent,
				config.getWorkspace());
		Assert.assertTrue("Workspace2 Not Exists.", config.getWorkspace()
				.exists());
		Assert.assertEquals("Incorrect Repository2.", new File(
				TH.workspaceParent, ".hydra"), config.getRepository());
		Assert.assertTrue("Repository2 Not Exists.", config.getRepository()
				.exists());
		this.s1 = new Stage(TH.workspace);
		Assert.assertEquals("Incorrect Workspace3.", TH.workspace,
				config.getWorkspace());
		Assert.assertTrue("Workspace3 Not Exists.", config.getWorkspace()
				.exists());
		Assert.assertEquals("Incorrect Repository3.", TH.repository,
				config.getRepository());
		Assert.assertTrue("Repository3 Exists.", config.getRepository()
				.exists());
		Assert.assertEquals("Should Return Questioning Status.", "? ?",
				this.s1.getStatus(true, true));
	}

	/**
	 * Sg_create logical unit.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void sg_createLogicalUnit() throws InvalidElementException {
		TH.deleteDirectory(new File(TH.workspaceParent, ".hydra"));
		this.s1 = new Stage(TH.workspaceParent);
		final LogicalUnit lu1 = this.s1.createLogicalUnit("lu1");
		Assert.assertNotNull("LU1 is Null", lu1);
		final LogicalUnit lu2 = this.s1.getLogicalUnit("lu1");
		Assert.assertEquals("LU1 and LU2 Not Same.", lu1, lu2);
		final LogicalUnit[] lus = this.s1.listManaged();
		Assert.assertEquals("Incorrect Number of LUs", 1, lus.length);
	}

	/**
	 * Sg_describe descriptor.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void sg_describeDescriptor() throws InvalidElementException {
		TH.deleteDirectory(new File(TH.workspaceParent, ".hydra"));
		this.s1 = new Stage(TH.workspaceParent);
		final LogicalUnit lu1 = this.s1.createLogicalUnit("lu1");
		final String expectedDescription = Stage.HEADER + "\nST::>>"
				+ TH.c0Hash + "\nLS::>>" + lu1.getName() + "\n";
		Assert.assertEquals("Incorrect Description.", expectedDescription,
				this.s1.describe());
		final String expectedDescriptor = "STAGE::>>null::>>null::>>"
				+ TH.c0Hash;
		Assert.assertEquals("Incorrect Descriptor.", expectedDescriptor,
				this.s1.getDescriptor());
	}

	/**
	 * Sg_get status.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void sg_getStatus() throws InvalidElementException {
		this.s1 = new Stage();
		// Test Before Commit.
		Assert.assertEquals("Status should be '? ?'.", "? ?",
				this.s1.getStatus(true, true));
		Assert.assertEquals("Status should be '?'.", "?",
				this.s1.getStatus(true, false));
		Assert.assertEquals("Status should be '?'.", "?",
				this.s1.getStatus(false, true));
		// Test After Commit.
		this.s1.commit(Configuration.getInstance().getUserId(), "Test Commit.");
		Assert.assertEquals("Status should be 'v v'.", "v v",
				this.s1.getStatus(true, true));
		Assert.assertEquals("Status should be 'v'.", "v",
				this.s1.getStatus(true, false));
		Assert.assertEquals("Status should be 'v'.", "v",
				this.s1.getStatus(false, true));
		// Test Added.
		this.s1.getContents().addElement(new Artifact(TH.w1File));
		Assert.assertEquals("Status should be 'v c'.", "v c",
				this.s1.getStatus(true, true));
		// Test Changed.
		TH.writeFile(TH.w1File, "This is Some Other Content.");
		Assert.assertEquals("Status should be 'c c'.", "c c",
				this.s1.getStatus(true, true));
		// Test Remove Artifact.
		TH.deleteFile(TH.w1File);
		Assert.assertEquals("Status should be 'c c'.", "c c",
				this.s1.getStatus(true, true));
		// Test Remove Contents Directory.
		TH.deleteDirectory(TH.workspace);
		Assert.assertEquals("Status should be '? -'.", "? -",
				this.s1.getStatus(true, true));
	}

	/**
	 * Sg_create logical unit2.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void sg_createLogicalUnit2() throws InvalidElementException {
		this.s1 = new Stage(TH.workspace);
		// Create New Logical Unit & Add Artifact.
		final LogicalUnit lu2 = this.s1.createLogicalUnit("aCard2");
		Assert.assertEquals("Should be Two Logical Units.", 2,
				this.s1.listManaged().length);
		Assert.assertTrue("Artifact1 Add Failed.", lu2.getContents()
				.addElement(new Artifact(TH.w1File)));
		Assert.assertEquals("Should be One Element in Contents.", 1, lu2
				.getContents().countElements());
	}

	/**
	 * Sg_loading logical unit.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void sg_loadingLogicalUnit() throws InvalidElementException {
		// Create New Stage.
		this.s1 = new Stage();
		// Verify Stage Configuration & Logical Units.
		Assert.assertEquals("Incorrect Workspace.", TH.workspace, Configuration
				.getInstance().getWorkspace());
		Assert.assertEquals("Incorrect Repository.", TH.repository,
				Configuration.getInstance().getRepository());
		Assert.assertEquals("Should Have One LogicalUnit.", 1,
				this.s1.listManaged().length);
		Assert.assertEquals("Logical Unit should be aCard1.", "aCard1",
				this.s1.listManaged()[0].getName());
	}

	/**
	 * Sg_delete logical unit.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void sg_deleteLogicalUnit() throws InvalidElementException {
		this.s1 = new Stage();
		Assert.assertNotNull("aCard1 not exists.",
				this.s1.getLogicalUnit("aCard1"));
		final File luLocation = Configuration.getInstance().getLUStore();
		Assert.assertTrue("lu file not exists.",
				new File(luLocation, "aCard1").exists());
		Assert.assertTrue("aCard1 Not Deleted.",
				this.s1.deleteLogicalUnit("aCard1"));
		Assert.assertNull("aCard1 Still Exists.",
				this.s1.getLogicalUnit("aCard1"));
		Assert.assertFalse("LU File Still Exists.", new File(luLocation,
				"aCard1").exists());
	}

	/**
	 * Sg_focus.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void sg_focus() throws InvalidElementException {
		this.s1 = new Stage();
		final LogicalUnit luFocus = this.s1.getLogicalUnit("aCard1");
		this.s1.setFocus(luFocus);
		Assert.assertEquals("Correct Focus Not Returned.", luFocus.describe(),
				this.s1.getFocus().describe());
	}

	/**
	 * Sg_commit revert.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void sg_commitRevert() throws InvalidElementException {
		this.s1 = new Stage();
		final Stage s2 = new Stage();
		// Commit1
		final String c1Hash = this.s1.commit("Scott", "First Stage Commit.");
		Assert.assertNotNull("Null Commit 1 Hash Returned.", c1Hash);
		s2.loadReferences();
		Assert.assertEquals("Commit 1 Refs Not Same.", this.s1.describe(),
				s2.describe());
		// Commit2
		final String c2Hash = this.s1.commit("Scott", "Second Stage Commit.");
		Assert.assertNotNull("Null Commit 2 Hash Returned.", c2Hash);
		s2.loadReferences();
		Assert.assertEquals("Commit 2 Refs Not Same.", this.s1.describe(),
				s2.describe());
		// Revert1
		final boolean r1Success = this.s1.revert(c1Hash);
		Assert.assertTrue("Did Not Succeed Reverting to Commit 1.", r1Success);
		s2.loadReferences();
		Assert.assertEquals("Revert 1 Refs Not Same.", this.s1.describe(),
				s2.describe());
		// Revert2
		final boolean r2Success = this.s1.revert(c2Hash);
		Assert.assertTrue("Did Not Succeed Reverting to Commit 2.", r2Success);
		s2.loadReferences();
		Assert.assertEquals("Revert 2 Refs Not Same.", this.s1.describe(),
				s2.describe());
	}

	/**
	 * Sg_manage ignore logical units.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void sg_manageIgnoreLogicalUnits() throws InvalidElementException {
		// Initialize & Verify
		TH.setupTestingEnvironment(true, false);
		this.s1 = new Stage();
		this.s1.loadReferences();
		Assert.assertEquals("Should Be No Logical Units.", 0,
				this.s1.countManaged());
		// Create New Logical Unit
		this.s1.createLogicalUnit("lux");
		this.s1.loadReferences();
		Assert.assertEquals("Should Be One Logical Units.", 1,
				this.s1.countManaged());
		// Remove Logical Unit
		final LogicalUnit s1Remove = this.s1.ignore("lux");
		this.s1.loadReferences();
		Assert.assertNotNull("Should be Removed.", s1Remove);
		Assert.assertEquals("Should Be No Logical Units.", 0,
				this.s1.countManaged());
		// Add Logical Unit
		final boolean s1Add = this.s1.manage(new LogicalUnit("lux"));
		this.s1.loadReferences();
		Assert.assertTrue("Should be Added.", s1Add);
		Assert.assertEquals("Should Be One Logical Units.", 1,
				this.s1.countManaged());
	}

	/**
	 * Sg_manage ignore non existent logical units.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void sg_manageIgnoreNonExistentLogicalUnits()
			throws InvalidElementException {
		this.s1 = new Stage();
		final LogicalUnit lu = null;
		Assert.assertFalse("Should Not Add Non-Existent Logical Unit.",
				this.s1.manage(lu));
		Assert.assertNull("Should Not Remove Non-Existent Logical Unit.",
				this.s1.ignore("Unknown"));
	}

	/**
	 * Sg_ignore focused logical unit.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void sg_ignoreFocusedLogicalUnit() throws InvalidElementException {
		this.s1 = new Stage();
		Assert.assertTrue("Stage Has Focus.", this.s1.isFocused());
		Assert.assertNotNull("Should Ignore Focus.",
				this.s1.ignore(this.s1.getFocus().getName()));
		Assert.assertFalse("Stage Has No Focus.", this.s1.isFocused());
	}

	/**
	 * Sg_committing nulls.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void sg_committingNulls() throws InvalidElementException {
		this.s1 = new Stage();
		TH.setupLogging();
		Assert.assertEquals("Null Commit String.", "null",
				this.s1.commit(null, "Test Commit Message."));
		Assert.assertEquals("Null Commit String.", "null",
				this.s1.commit("Test User", null));
	}

	/**
	 * Sg_recreate stage.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void sg_recreateStage() throws InvalidElementException {
		TH.deleteFile(new File(TH.repository, "STAGE"));
		this.s1 = new Stage();
		Assert.assertEquals("Found Logical Unit.", 1, this.s1.countManaged());
		Assert.assertFalse("Is Not Focused.", this.s1.isFocused());
		this.s1.setFocus(this.s1.getLogicalUnit("aCard1"));
		Assert.assertTrue("Is Focused.", this.s1.isFocused());
	}

	/**
	 * Sg_load deleted reference.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void sg_loadDeletedReference() throws InvalidElementException {
		this.s1 = new Stage();
		TH.deleteFile(new File(TH.repository, "STAGE"));
		Assert.assertFalse("Cannot Load Deleted Reference.",
				this.s1.loadReferences());
	}

	/**
	 * Sg_list unmanaged.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void sg_listUnmanaged() throws InvalidElementException {
		this.s1 = new Stage();
		Assert.assertEquals("1 Should Have One Managed LUs.", 1,
				this.s1.listManaged().length);
		Assert.assertEquals("1 Should Have No Unmanaged LUs.", 0,
				this.s1.listUnmanaged().length);
		Assert.assertNotNull("Should Remove aCard1", this.s1.ignore("aCard1"));
		Assert.assertEquals("2 Should Have No Managed LUs.", 0,
				this.s1.listManaged().length);
		Assert.assertEquals("2 Should Have One Unmanaged LUs.", 1,
				this.s1.listUnmanaged().length);
		Assert.assertTrue("Should Add aCard1",
				this.s1.manage(new LogicalUnit("aCard1")));
		Assert.assertEquals("1 Should Have One Managed LUs.", 1,
				this.s1.listManaged().length);
		Assert.assertEquals("1 Should Have No Unmanaged LUs.", 0,
				this.s1.listUnmanaged().length);
	}

	/**
	 * Sg_revert stage.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void sg_revertStage() throws InvalidElementException {
		this.s1 = new Stage();
		// Commit Stage.
		final String s1Hash = this.s1.commitStageAndLogicalUnits("Scott",
				"Full Stage Commit 1.");
		final String l1Hash = this.s1.getLogicalUnit("aCard1").getCurrentHash();
		// Change Logical Unit
		this.s1.getLogicalUnit("aCard1").getContents()
				.addElement(new Artifact(TH.w2File));
		this.s1.getLogicalUnit("aCard1").commit("Scott",
				"Logical Unit Regular Commit.");
		final String l2aHash = this.s1.getLogicalUnit("aCard1")
				.getCurrentHash();
		Assert.assertFalse("2a LU Hash Should Have Changed.",
				l1Hash.equals(l2aHash));
		// Commit Stage.
		final String s2Hash = this.s1.commitStageAndLogicalUnits("Scott",
				"Full Stage Commit 2.");
		final String l2bHash = this.s1.getLogicalUnit("aCard1")
				.getCurrentHash();
		Assert.assertFalse("2b LU Hash Should Have Changed.",
				l2aHash.equals(l2bHash));
		// Revert to First Commit.
		Assert.assertTrue("Should Revert Stage.", this.s1.revert(s1Hash));
		Assert.assertEquals("Current Should Have s1Hash.", s1Hash,
				this.s1.getCurrentHash());
		Assert.assertEquals("Head Should Have s1Hash.", s2Hash,
				this.s1.getHeadHash());
		Assert.assertEquals("Should Not Have Changed Logical Unit.", l2bHash,
				this.s1.getLogicalUnit("aCard1").getCurrentHash());
		// Revert Stage to First Commit.
		Assert.assertTrue("Should Revert Stage.",
				this.s1.revertStage(s1Hash, true));
		Assert.assertEquals("Current Should Have s1Hash.", s1Hash,
				this.s1.getCurrentHash());
		Assert.assertEquals("Head Should Have s2Hash.", s2Hash,
				this.s1.getHeadHash());
		Assert.assertNotNull("Should Have Logical Unit.",
				this.s1.getLogicalUnit("aCard1"));
		Assert.assertEquals("Should Have Changed Logical Unit.", l1Hash,
				this.s1.getLogicalUnit("aCard1").getCurrentHash());
		// Revert Stage to Second Commit.
		Assert.assertTrue("Should Revert Stage.",
				this.s1.revertStage(s2Hash, true));
		Assert.assertEquals("Current Should Have s2Hash.", s2Hash,
				this.s1.getCurrentHash());
		Assert.assertEquals("Head Should Have s2Hash.", s2Hash,
				this.s1.getHeadHash());
		Assert.assertNotNull("Should Have Logical Unit.",
				this.s1.getLogicalUnit("aCard1"));
		Assert.assertEquals("Should Have Changed Logical Unit.", l2bHash,
				this.s1.getLogicalUnit("aCard1").getCurrentHash());
	}

	/*
	 * EXPECTED EXCEPTIONAL SITUATIONS 1. Use Same Logical Unit Name Twice -
	 * Return Already Created.
	 */

	/**
	 * Sg_create logical unit same name.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void sg_createLogicalUnitSameName() throws InvalidElementException {
		this.s1 = new Stage();
		final LogicalUnit lu1 = this.s1.getLogicalUnit("aCard1");
		final LogicalUnit lu1X = this.s1.createLogicalUnit("aCard1");
		Assert.assertEquals("Different Logical Units.", lu1, lu1X);
	}

	/**
	 * Sg_commit.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void sg_commit() throws InvalidElementException {
		this.s1 = new Stage();
		final String luDescriptor = this.s1.getLogicalUnit("aCard1")
				.getDescriptor();
		// Commit Stage Once
		final String c1Hash = this.s1.commit("Scott", "Stage Commit 1.");
		Assert.assertNotNull("Stage Commit 1 Hash Is Null.", c1Hash);
		Assert.assertEquals("LU Should Not Change.", luDescriptor, this.s1
				.getLogicalUnit("aCard1").getDescriptor());
		// Commit Stage Twice
		final String c2Hash = this.s1.commit("Scott", "Stage Commit 2.");
		Assert.assertNotNull("Stage Commit 2 Hash Is Null.", c2Hash);
		Assert.assertEquals("LU Should Not Change.", luDescriptor, this.s1
				.getLogicalUnit("aCard1").getDescriptor());
	}

	/**
	 * Sg_commit logical units.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void sg_commitLogicalUnits() throws InvalidElementException {
		this.s1 = new Stage();
		final LogicalUnit lu1 = this.s1.getLogicalUnit("aCard1");
		final LogicalUnit lu2 = this.s1.createLogicalUnit("aCard2");
		final String lu1Descriptor = lu1.getDescriptor();
		final String lu2Descriptor = lu2.getDescriptor();
		final String nameHashPairs = this.s1.commitLogicalUnits("Scott",
				"Stage Commit Logical Units.");
		Assert.assertNotNull("Should Not Return Null", nameHashPairs);
		Assert.assertTrue(
				"Should Contain LU1",
				nameHashPairs.contains(lu1.getName() + "="
						+ lu1.getCurrentHash()));
		Assert.assertTrue(
				"Should Contain LU2",
				nameHashPairs.contains(lu2.getName() + "="
						+ lu2.getCurrentHash()));
		Assert.assertFalse("LU1 Descriptor Should Change.",
				lu1Descriptor.equals(lu1.getDescriptor()));
		Assert.assertFalse("LU2 Descriptor Should Change.",
				lu2Descriptor.equals(lu2.getDescriptor()));
	}

	/**
	 * Sg_commit stage and logical units.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void sg_commitStageAndLogicalUnits() throws InvalidElementException {
		this.s1 = new Stage();
		// Test Regular Commit.
		final String c1Hash = this.s1
				.commit("Scott", "Regular Stage Commit 1.");
		Assert.assertTrue("Log Should Contain Hash.", this.s1
				.getHistoryCrawler().getHistoryLog(true).contains(c1Hash));
		// Test System Commit.
		final String c2Hash = this.s1.commitStageAndLogicalUnits("Scott",
				"Full System Commit 1.");
		Assert.assertNotNull("Hash Should Not be Null.", c2Hash);
		Assert.assertTrue("Log Should Contain Hash.", this.s1
				.getHistoryCrawler().getHistoryLog(true).contains(c2Hash));
		// Test Recording References.
		final Stage s2 = new Stage();
		Assert.assertEquals("Should Have Same Description.",
				this.s1.describe(), s2.describe());
		final String c3Hash = this.s1.commitStageAndLogicalUnits("Scott",
				"Full System Commmit 2.");
		Assert.assertFalse("Should Have Different Descriptions.", this.s1
				.describe().equals(s2.describe()));
		Assert.assertTrue("Should be Refreshed.", s2.loadReferences());
		Assert.assertEquals("Should Have Same Description.",
				this.s1.describe(), s2.describe());
	}

	/**
	 * Sg_commit valid path logical units.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void sg_commitValidPathLogicalUnits() throws InvalidElementException {
		this.s1 = new Stage();
		final LogicalUnit lu1 = this.s1.getLogicalUnit("aCard1");
		final LogicalUnit lu2 = this.s1.createLogicalUnit("aCard2");
		lu2.commit("Scott", "First Commit.");
		final LogicalUnit lu3 = this.s1.createLogicalUnit("aCard3");
		final String nameHashPairs = this.s1.commitValidPathLogicalUnits(
				"Scott", "Stage Commit Logical Units.");
		Assert.assertNotNull("Should Not Return Null", nameHashPairs);
		Assert.assertTrue(
				"Should Contain LU1",
				nameHashPairs.contains(lu1.getName() + "="
						+ lu1.getCurrentHash()));
		Assert.assertTrue(
				"Should Contain LU2",
				nameHashPairs.contains(lu2.getName() + "="
						+ lu2.getCurrentHash()));
		Assert.assertTrue(
				"Should Contain LU3",
				nameHashPairs.contains(lu3.getName() + "="
						+ lu3.getCurrentHash()));
		Assert.assertEquals("LU1 Should Have 2 Previous.", 2, lu1.getHead()
				.listPrevious().length);
		Assert.assertEquals("LU2 Should Have 1 Previous.", 1, lu2.getHead()
				.listPrevious().length);
		Assert.assertEquals("LU3 Should Have 0 Previous.", 0, lu3.getHead()
				.listPrevious().length);
	}

	/**
	 * Sg_commit valid path stage and logical units.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void sg_commitValidPathStageAndLogicalUnits()
			throws InvalidElementException {
		// Setup Stage
		this.s1 = new Stage();
		this.s1.commit("Scott", "First Stage Commit");
		final String initialHeadHash = this.s1.getHeadHash();
		this.s1.commit("Scott", "Second Stage Commit");
		this.s1.revert(initialHeadHash);
		// Record LU Info
		final LogicalUnit lu1 = this.s1.getLogicalUnit("aCard1");
		final LogicalUnit lu2 = this.s1.createLogicalUnit("aCard2");
		lu2.commit("Scott", "First Commit.");
		final LogicalUnit lu3 = this.s1.createLogicalUnit("aCard3");
		// Commit ValidPath and Test Results
		final String commitHash = this.s1.commitValidPathStageAndLogicalUnits(
				"Scott", "Stage Commit Logical Units.");
		Assert.assertNotNull("Should Not Return Null", commitHash);
		Assert.assertEquals("Stage Should Have 2 Previous.", 2, this.s1
				.getHead().listPrevious().length);
		Assert.assertEquals("LU1 Should Have 2 Previous.", 2, lu1.getHead()
				.listPrevious().length);
		Assert.assertEquals("LU2 Should Have 1 Previous.", 1, lu2.getHead()
				.listPrevious().length);
		Assert.assertEquals("LU3 Should Have 0 Previous.", 0, lu3.getHead()
				.listPrevious().length);
	}
}
