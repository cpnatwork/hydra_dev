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
 * The Class HydraFacadeTest.
 */
public class HydraFacadeTest {

	/** The hydra. */
	HydraFacade hydra = null;

	/**
	 * Start class test.
	 */
	@BeforeClass
	public static void startClassTest() {
		Logger.getInstance().info("HYDRA FACADE TEST");
	}

	/**
	 * Setup test.
	 */
	@Before
	public void setupTest() {
		TH.setupTestingEnvironment(true, true);
	}

	/**
	 * Hf_creation default.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void hf_creationDefault() throws InvalidElementException {
		this.hydra = new HydraFacade();
		Assert.assertNotNull("Hydra is Null.", this.hydra);
		Assert.assertNotNull("Stage is Null.", this.hydra.getStage());
	}

	/**
	 * Hf_creation specified.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void hf_creationSpecified() throws InvalidElementException {
		this.hydra = new HydraFacade(TH.workspaceParent);
		Assert.assertNotNull("Hydra is Null.", this.hydra);
		Assert.assertEquals("Incorrect Workspace.", TH.workspaceParent,
				Configuration.getInstance().getWorkspace());
		Assert.assertEquals("Incorrect Repository.", new File(
				TH.workspaceParent, ".hydra"), Configuration.getInstance()
				.getRepository());
	}

	/**
	 * Hf_logical units.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void hf_logicalUnits() throws InvalidElementException {
		this.hydra = new HydraFacade();
		Assert.assertEquals("Should be One LogicalUnit.", 1, this.hydra
				.getStage().listManaged().length);
		final LogicalUnit lu2 = this.hydra.createLogicalUnit("aCard2");
		Assert.assertEquals("Should be 2 Logical Units.", 2, this.hydra
				.getStage().listManaged().length);
		Assert.assertTrue("aCard2 Not Deleted.",
				this.hydra.deleteLogicalUnit("aCard2"));
		Assert.assertEquals("Should be One LogicalUnit.", 1, this.hydra
				.getStage().listManaged().length);
	}

	/**
	 * Hf_add remove elements.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void hf_addRemoveElements() throws InvalidElementException {
		this.hydra = new HydraFacade();
		// Added Subdirectory
		Assert.assertEquals("Incorrect Num Elements.", 1, this.hydra.getStage()
				.getLogicalUnit("aCard1").getContents().countElements());
		Assert.assertTrue("SubDirectory Add Failed.",
				this.hydra.addElement("aCard1", TH.w1Dir));
		Assert.assertFalse("SubDirectory Added Twice.",
				this.hydra.addElement("aCard1", TH.w1Dir));
		Assert.assertEquals("SubDirectory Not Added.", 2, this.hydra.getStage()
				.getLogicalUnit("aCard1").getContents().countElements());
		// System.out.println(hydra.getStage().getLogicalUnit("aCard1").getContents().describe());
		// Add File
		Assert.assertTrue("File2 Add Failed.",
				this.hydra.addElement("aCard1", TH.w2File));
		Assert.assertFalse("File2 Added Twice.",
				this.hydra.addElement("aCard1", TH.w2File));
		Assert.assertEquals("File2 Not Added.", 3, this.hydra.getStage()
				.getLogicalUnit("aCard1").getContents().countElements());
		// System.out.println(hydra.getStage().getLogicalUnit("aCard1").getContents().describe());
		// Remove Subdirectory
		Assert.assertTrue("SubDirectory Remove Failed.",
				this.hydra.removeElement("aCard1", TH.w1Dir));
		Assert.assertFalse("SubDirectory Removed Twice.",
				this.hydra.removeElement("aCard1", TH.w1Dir));
		Assert.assertEquals("File2 Not Added.", 2, this.hydra.getStage()
				.getLogicalUnit("aCard1").getContents().countElements());
		// System.out.println(hydra.getStage().getLogicalUnit("aCard1").getContents().describe());
		// Remove File
		Assert.assertTrue("File2 Remove Failed.",
				this.hydra.removeElement("aCard1", TH.w2File));
		Assert.assertFalse("File2 Removed Twice.",
				this.hydra.removeElement("aCard1", TH.w2File));
		Assert.assertEquals("File2 Not Added.", 1, this.hydra.getStage()
				.getLogicalUnit("aCard1").getContents().countElements());
		// System.out.println(hydra.getStage().getLogicalUnit("aCard1").getContents().describe());
	}

	/**
	 * Hf_add remove deep elements.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void hf_addRemoveDeepElements() throws InvalidElementException {
		this.hydra = new HydraFacade();
		// System.out.println("\n"+hydra.getStage().getLogicalUnit("aCard1").getContents().describe());
		// Added Element in Subdirectory
		Assert.assertTrue("File in Subdirectory Does Not Exist.",
				TH.d1w1File.exists());
		Assert.assertTrue("Element(File) in Subdirectory Not Added.",
				this.hydra.addElement("aCard1", TH.d1w1File));
		Assert.assertEquals("Expected 3 Elements.", 3, this.hydra.getStage()
				.getLogicalUnit("aCard1").getContents().countElements());
		// System.out.println("\n"+hydra.getStage().getLogicalUnit("aCard1").getContents().describe());
		// Added Subdirectory in Subdirectory
		final File ssDir = new File(TH.w1Dir, "subsubDirectory");
		ssDir.mkdir();
		Assert.assertTrue("Directory in Subdirectory Does Not Exist.",
				ssDir.exists());
		Assert.assertTrue("Element(ssDir) Not Added.",
				this.hydra.addElement("aCard1", ssDir));
		Assert.assertEquals("Expected 4 Elements.", 4, this.hydra.getStage()
				.getLogicalUnit("aCard1").getContents().countElements());
		// System.out.println("\n"+hydra.getStage().getLogicalUnit("aCard1").getContents().describe());
		// Remove SubSubDirectory
		Assert.assertTrue("Element(ssDir) not Removed.",
				this.hydra.removeElement("aCard1", ssDir));
		Assert.assertEquals("Expected 3 Elements.", 3, this.hydra.getStage()
				.getLogicalUnit("aCard1").getContents().countElements());
		// System.out.println("\n"+hydra.getStage().getLogicalUnit("aCard1").getContents().describe());
		// Remove File in Subdirectory.
		Assert.assertTrue("Element(File) in SubDirectory not Removed.",
				this.hydra.removeElement("aCard1", TH.d1w1File));
		Assert.assertEquals("Expected 2 Elements.", 2, this.hydra.getStage()
				.getLogicalUnit("aCard1").getContents().countElements());
		// System.out.println("\n"+hydra.getStage().getLogicalUnit("aCard1").getContents().describe());
	}

	/**
	 * Hf_commit revert.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void hf_commitRevert() throws InvalidElementException {
		this.hydra = new HydraFacade();
		final String c0HHash = this.hydra.getStage().getLogicalUnit("aCard1")
				.getHead().getFingerprint().getHash();
		final String c0CHash = this.hydra.getStage().getLogicalUnit("aCard1")
				.getCurrent().getFingerprint().getHash();
		final String c1Hash = this.hydra.commit("aCard1",
				"Hydra Facade Commit.");
		// System.out.println("\nCurrent: "+c0CHash+" Head: "+c0HHash+"\n\tCommited: "+c1Hash);
		// System.out.println(hydra.getStage().getLogicalUnit("aCard1").getCurrent().describe());
		// Verify Correct Commit.
		final LogicalUnit lux = new LogicalUnit("aCard1");
		Assert.assertEquals("Incorrect Head Hash.", c1Hash, lux.getHead()
				.getFingerprint().getHash());
		Assert.assertEquals("Incorrect Current Hash.", c1Hash, lux.getCurrent()
				.getFingerprint().getHash());
		Assert.assertEquals("Incorrect Previous0.", c0CHash,
				this.hydra.getStage().getLogicalUnit("aCard1").getHead()
						.listPrevious()[0].getFingerprint().getHash());
		Assert.assertEquals("Incorrect Previous1.", c0HHash,
				this.hydra.getStage().getLogicalUnit("aCard1").getHead()
						.listPrevious()[1].getFingerprint().getHash());
		// Revert (Hash) to First Previous
		Assert.assertTrue("Revert Failed 1.",
				this.hydra.revertHash("aCard1", c0HHash));
		Assert.assertEquals("Incorrect Reverted Current Hash.", c0HHash,
				this.hydra.getStage().getLogicalUnit("aCard1").getCurrent()
						.getFingerprint().getHash());
		Assert.assertTrue("Revert Failed 2.",
				this.hydra.revertHash("aCard1", c0CHash));
		Assert.assertEquals("Incorrect Reverted Current Hash.", c0CHash,
				this.hydra.getStage().getLogicalUnit("aCard1").getCurrent()
						.getFingerprint().getHash());
		Assert.assertEquals("Incorrect Reverted Head Hash.", c1Hash, this.hydra
				.getStage().getLogicalUnit("aCard1").getHead().getFingerprint()
				.getHash());
		// Revert (Path) to Second Previous
		Assert.assertTrue("Revert Failed 3.",
				this.hydra.revertPath("aCard1", "*1+1"));
		Assert.assertEquals("Incorrect Reverted Current Hash.", c0CHash,
				this.hydra.getStage().getLogicalUnit("aCard1").getCurrent()
						.getFingerprint().getHash());
		Assert.assertEquals("Incorrect Reverted Head Hash.", c1Hash, this.hydra
				.getStage().getLogicalUnit("aCard1").getHead().getFingerprint()
				.getHash());
		// Revert (Relative) to First Previous
		Assert.assertTrue("Revert Failed 4.",
				this.hydra.revertRelative("aCard1", 1, -1));
		Assert.assertEquals("Incorrect Reverted Current Hash.", c1Hash,
				this.hydra.getStage().getLogicalUnit("aCard1").getCurrent()
						.getFingerprint().getHash());
	}

	/**
	 * H_log history.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void h_logHistory() throws InvalidElementException {
		this.hydra = new HydraFacade();
		// System.out.println("\n"+hydra.logHistory("aCard1"));
		final State s2 = new State(TH.u1s2Hash);
		final State s1 = new State(TH.u1s1Hash);
		final StringBuilder sb = new StringBuilder("\t\taCard1 Commit Log\n");
		sb.append("  --------------------------------------------------\n");
		sb.append(s2.getLogEntry() + "\n\n" + s1.getLogEntry() + "\n");
		Assert.assertEquals("Incorrect Log.", sb.toString(),
				this.hydra.getHistoryLog("aCard1", true));
		Assert.assertEquals("Incorrect Log.", sb.toString(),
				this.hydra.getHistoryLog("aCard1", false));
	}

	/**
	 * H_commit temporary.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void h_commitTemporary() throws InvalidElementException {
		this.hydra = new HydraFacade();
		final String tempCommitHash = this.hydra.commitTemporary("aCard1",
				"Temporary Message.");
		Assert.assertEquals("Head Commit Hash Not Correct.", tempCommitHash,
				this.hydra.getStage().getLogicalUnit("aCard1").getHeadHash());
		Assert.assertEquals("Current Commit Hash Not Correct.", tempCommitHash,
				this.hydra.getStage().getLogicalUnit("aCard1").getCurrentHash());
		Assert.assertNull("HEAD Content Not Null.", this.hydra.getStage()
				.getLogicalUnit("aCard1").getHead().cloneContents());
		Assert.assertNull("CURRENT Content Not Null.", this.hydra.getStage()
				.getLogicalUnit("aCard1").getCurrent().cloneContents());
	}

	/**
	 * Lu_update commit.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void lu_updateCommit() throws InvalidElementException {
		this.hydra = new HydraFacade();
		final String targetHash = this.hydra.getStage()
				.getLogicalUnit("aCard1").getCurrentHash();
		final String targetId = this.hydra.getStage().getLogicalUnit("aCard1")
				.getCurrent().getDescriptor();
		Assert.assertTrue("Update Failed.", this.hydra.commitUpdate("aCard1",
				targetHash, "Updating Commit."));
		Assert.assertEquals("IDs Changed.", targetId, this.hydra.getStage()
				.getLogicalUnit("aCard1").getCurrent().getDescriptor());
		final State sX = new State(targetHash);
		Assert.assertEquals("Different Descriptions.", this.hydra.getStage()
				.getLogicalUnit("aCard1").getCurrent().describe(),
				sX.describe());
		Assert.assertEquals("Different Contents.", this.hydra.getStage()
				.getLogicalUnit("aCard1").getContents().getFingerprint()
				.getHash(), sX.cloneContents().getFingerprint().getHash());
		// System.out.println("\n"+hydra.getStage().getLogicalUnit("aCard1").getCurrent().describe()+"\n"+sX.describe());
	}

	/**
	 * Lu_commit insert.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void lu_commitInsert() throws InvalidElementException {
		this.hydra = new HydraFacade();
		final String initCurrentHash = this.hydra.getStage()
				.getLogicalUnit("aCard1").getCurrentHash();
		final int initHeadPrev = this.hydra.getStage().getLogicalUnit("aCard1")
				.getHead().listPrevious().length;
		final String insertedHash = this.hydra
				.commitInsert("aCard1", "Inserted Commit.", this.hydra
						.getStage().getLogicalUnit("aCard1").getCurrentHash(),
						this.hydra.getStage().getLogicalUnit("aCard1")
								.getHeadHash());
		Assert.assertNotNull("Inserted Hash is Null.", insertedHash);
		// CHECK CURRENT.
		Assert.assertEquals("Current Hash Not Inserted Hash.", insertedHash,
				this.hydra.getStage().getLogicalUnit("aCard1").getCurrentHash());
		Assert.assertFalse(
				"Current Hash Not Changed.",
				initCurrentHash.equals(this.hydra.getStage()
						.getLogicalUnit("aCard1").getCurrentHash()));
		State sX = new State(this.hydra.getStage().getLogicalUnit("aCard1")
				.getCurrentHash());
		Assert.assertEquals("Current Info Not Correctly Stored.", this.hydra
				.getStage().getLogicalUnit("aCard1").getCurrent().describe(),
				sX.describe());
		// CHECK HEAD.
		Assert.assertEquals("Inserted Not Added to Head's Previous.",
				initHeadPrev + 1,
				this.hydra.getStage().getLogicalUnit("aCard1").getHead()
						.listPrevious().length);
		Assert.assertTrue("Inserted Commit Not Stored.", new File(TH.fpStore,
				insertedHash).exists());
		sX = new State(this.hydra.getStage().getLogicalUnit("aCard1")
				.getHeadHash());
		Assert.assertEquals("Head Info Not Correctly Stored.", this.hydra
				.getStage().getLogicalUnit("aCard1").getHead().describe(),
				sX.describe());
	}

}
