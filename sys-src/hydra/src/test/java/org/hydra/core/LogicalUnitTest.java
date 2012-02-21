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
 * The Class LogicalUnitTest.
 */
public class LogicalUnitTest {

	/** The lu1. */
	LogicalUnit lu1;

	/** The lu2. */
	LogicalUnit lu2;

	/**
	 * Start class test.
	 */
	@BeforeClass
	public static void startClassTest() {
		TH.setupLogging();
		Logger.getInstance().info("LOGICAL UNIT TESTS");
	}

	/**
	 * Setup test.
	 */
	@Before
	public void setupTest() {
		TH.setupTestingEnvironment(true, true);
	}

	/**
	 * Lu_creation.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void lu_creation() throws InvalidElementException {
		final File newluFile = new File(TH.luStore, "NewLU");
		Assert.assertFalse("LU1 File Exists.", newluFile.exists());
		this.lu1 = new LogicalUnit("NewLU");
		Assert.assertNotNull("LU1 is Null.", this.lu1);
		Assert.assertTrue("LU1 File Not Exists.", newluFile.exists());
		Assert.assertNull("HEAD is not Null.", this.lu1.getHead());
		Assert.assertNull("Current is not Null.", this.lu1.getCurrent());
		Assert.assertEquals("Incorrect Container.", TH.c0Hash, this.lu1
				.getContents().getFingerprint().getHash());
		Assert.assertEquals("Incorrect toString.", "LogicalUnit:NewLU",
				this.lu1.toString());
		final String expectedDescriptor = "LS::>>NewLU::>>null::>>" + TH.c0Hash;
		Assert.assertEquals("Incorrect Descriptor.", expectedDescriptor,
				this.lu1.getDescriptor());
		Assert.assertEquals("Should Return Questioning Status.", "? ?",
				this.lu1.getStatus(true, true));
	}

	/**
	 * Lu_creation2.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void lu_creation2() throws InvalidElementException {
		this.lu1 = new LogicalUnit("aCard1");
		final LogicalUnit lu2 = new LogicalUnit("aCard1",
				this.lu1.getCurrentHash(), this.lu1.getContentsHash());
		Assert.assertEquals("Should Have Same Descriptions.",
				this.lu1.describe(), lu2.describe());
	}

	/**
	 * Lu_creation2 invalid name.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test(expected = InvalidElementException.class)
	public void lu_creation2InvalidName() throws InvalidElementException {
		this.lu1 = new LogicalUnit("unknown", "null", TH.c0Hash);
	}

	/**
	 * Lu_creation2 bad info2.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test(expected = InvalidElementException.class)
	public void lu_creation2BadInfo2() throws InvalidElementException {
		this.lu1 = new LogicalUnit("aCard1", "null", "null");
	}

	/**
	 * Lu_load.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void lu_load() throws InvalidElementException {
		this.lu1 = new LogicalUnit("aCard1");
		Assert.assertEquals("Head Hash Incorrect.", TH.u1s2Hash, this.lu1
				.getHead().getFingerprint().getHash());
		Assert.assertEquals("Current Hash Incorrect.", TH.u1s1Hash, this.lu1
				.getCurrent().getFingerprint().getHash());
		Assert.assertEquals("Container Hash Incorrect.", TH.c1Hash, this.lu1
				.getContents().getFingerprint().getHash());
		Assert.assertEquals("Incorrect Description.", TH.lu1String,
				this.lu1.describe());
		final String expectedDescriptor = "LS::>>" + this.lu1.getName()
				+ "::>>" + this.lu1.getCurrentHash() + "::>>"
				+ this.lu1.getContentsHash();
		Assert.assertEquals("Incorrect Descriptor.", expectedDescriptor,
				this.lu1.getDescriptor());
	}

	/**
	 * Lu_get status.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void lu_getStatus() throws InvalidElementException {
		this.lu1 = new LogicalUnit("aCard1");
		Assert.assertEquals("Status should be 'v v'.", "v v",
				this.lu1.getStatus(true, true));
		Assert.assertEquals("Status should be 'v'.", "v",
				this.lu1.getStatus(true, false));
		Assert.assertEquals("Status should be 'v'.", "v",
				this.lu1.getStatus(false, true));
	}

	/**
	 * Lu_commit.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void lu_commit() throws InvalidElementException {
		this.lu1 = new LogicalUnit("aCard1");
		Assert.assertFalse("Head Not Equals Current.", this.lu1.getHead()
				.equals(this.lu1.getCurrent()));
		final String commitHash = this.lu1.commitValidPath("Scott",
				"This is a Logical Unit Commit.");
		Assert.assertTrue("Head Equals Current.",
				this.lu1.getHead().equals(this.lu1.getCurrent()));
		Assert.assertEquals("Head Incorrect Previous Count.", 2, this.lu1
				.getHead().listPrevious().length);
		Assert.assertEquals("LU Head Hash Methods Different Results.", this.lu1
				.getHead().getFingerprint().getHash(), this.lu1.getHeadHash());
		Assert.assertEquals("LU Current Hash Methods Different Results.",
				this.lu1.getCurrent().getFingerprint().getHash(),
				this.lu1.getCurrentHash());
	}

	/**
	 * Lu_commit prevented null addition.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void lu_commitPreventedNullAddition()
			throws InvalidElementException, Exception {
		this.lu1 = new LogicalUnit("emptyLogicalUnit");
		Assert.assertEquals("Incorrect Log History.", "", this.lu1
				.getHistoryCrawler().getHistoryLog(true));
		Assert.assertNotNull(this.lu1.commitValidPath("Scott",
				"Committing an Empty Hash."));
		// Testing State Description Failure.
		Assert.assertNotNull("Head Incorrectly Described.", this.lu1.getHead()
				.describe());
		Assert.assertNotNull("Current Incorrectly Described.", this.lu1
				.getCurrent().describe());
		Assert.assertNotNull("History Incorrectly Described.", this.lu1
				.getHistoryCrawler().getHistoryLog(true));
	}

	/**
	 * Lu_commit temporary.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void lu_commitTemporary() throws InvalidElementException {
		this.lu1 = new LogicalUnit("aCard1");
		final String tempCommitHash = this.lu1.commitTemporary("Scott",
				"Temporary Message.");
		Assert.assertEquals("Head Commit Hash Not Correct.", tempCommitHash,
				this.lu1.getHead().getFingerprint().getHash());
		Assert.assertEquals("Current Commit Hash Not Correct.", tempCommitHash,
				this.lu1.getCurrent().getFingerprint().getHash());
		Assert.assertNull("HEAD Content Not Null.", this.lu1.getHead()
				.cloneContents());
		Assert.assertNull("CURRENT Content Not Null.", this.lu1.getCurrent()
				.cloneContents());
	}

	/**
	 * Lu_update commit.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void lu_updateCommit() throws InvalidElementException {
		this.lu1 = new LogicalUnit("aCard1");
		final String targetHash = this.lu1.getCurrent().getFingerprint()
				.getHash();
		final String targetId = this.lu1.getCurrent().getDescriptor();
		Assert.assertTrue("Update Failed.",
				this.lu1.commitUpdate(targetHash, "Scott", "Updating Commit."));
		Assert.assertEquals("IDs Changed.", targetId, this.lu1.getCurrent()
				.getDescriptor());
		final State sX = new State(targetHash);
		Assert.assertEquals("Different Descriptions.", this.lu1.getCurrent()
				.describe(), sX.describe());
		Assert.assertEquals("Different Contents.", this.lu1.getContents()
				.getFingerprint().getHash(), sX.cloneContents()
				.getFingerprint().getHash());
		// System.out.println("\n"+lu1.getCurrent().describe()+"\n"+sX.describe());
	}

	/**
	 * Lu_update commit head.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void lu_updateCommitHead() throws InvalidElementException {
		this.lu1 = new LogicalUnit("aCard1");
		this.lu1.commitUpdate(TH.u1s2Hash, "Updated User", "Updated Message");
		Assert.assertEquals("Should Have 'Updated User'.", "Updated User",
				this.lu1.getHead().getUserId());
		Assert.assertEquals("Should Have 'Updated Message'.",
				"Updated Message", this.lu1.getHead().getMessage());
	}

	/**
	 * Lu_commit insert.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void lu_commitInsert() throws InvalidElementException {
		this.lu1 = new LogicalUnit("aCard1");
		final String initCurrentHash = this.lu1.getCurrent().getFingerprint()
				.getHash();
		final int initHeadPrev = this.lu1.getHead().listPrevious().length;
		final String insertedHash = this.lu1.commitInsert("Scott",
				"Inserted Commit.", this.lu1.getCurrent().getFingerprint()
						.getHash(), this.lu1.getHead().getFingerprint()
						.getHash());
		Assert.assertNotNull("Inserted Hash is Null.", insertedHash);
		// CHECK CURRENT.
		Assert.assertEquals("Current Hash Not Inserted Hash.", insertedHash,
				this.lu1.getCurrent().getFingerprint().getHash());
		Assert.assertFalse(
				"Current Hash Not Changed.",
				initCurrentHash.equals(this.lu1.getCurrent().getFingerprint()
						.getHash()));
		State sX = new State(this.lu1.getCurrent().getFingerprint().getHash());
		Assert.assertEquals("Current Info Not Correctly Stored.", this.lu1
				.getCurrent().describe(), sX.describe());
		// CHECK HEAD.
		Assert.assertEquals("Inserted Not Added to Head's Previous.",
				initHeadPrev + 1, this.lu1.getHead().listPrevious().length);
		Assert.assertTrue("Inserted Commit Not Stored.", new File(TH.fpStore,
				insertedHash).exists());
		sX = new State(this.lu1.getHead().getFingerprint().getHash());
		Assert.assertEquals("Head Info Not Correctly Stored.", this.lu1
				.getHead().describe(), sX.describe());
	}

	/**
	 * Lu_commit insert before head.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void lu_commitInsertBeforeHead() throws InvalidElementException {
		this.lu1 = new LogicalUnit("aCard1");
		this.lu1.commit("Scott", "Third Commit.");
		final String insertedHash = this.lu1.commitInsert("Scott",
				"Inserted Commit.", TH.u1s1Hash, TH.u1s2Hash);
		// CHECK CURRENT
		Assert.assertEquals("Should Have Inserted Hash.", insertedHash,
				this.lu1.getCurrentHash());
		Assert.assertEquals("Should be Correct Path.", "*1+1*2+1", this.lu1
				.getCurrent().clonePath().toString());
		final State tgtState = this.lu1.getHistoryCrawler().findCommitHash(
				this.lu1.getHead(), insertedHash, null, true);
		Assert.assertNotNull("State Should be Found.", tgtState);
		Assert.assertTrue(
				"Log Should Contain Log Message.",
				this.lu1.getHistoryCrawler().getHistoryLog(true)
						.contains("Scott - Inserted Commit."));
		// CHECK NEXT
		Assert.assertEquals("Should Have 2 Previous.", 2,
				new State(TH.u1s2Hash).listPrevious().length);
	}

	/**
	 * Lu_revert step.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void lu_revertStep() throws InvalidElementException {
		// System.out.println("\nrevertRelative(Step) - Success");
		this.lu1 = new LogicalUnit("aCard1");
		final String S3 = this.lu1.commitValidPath("Auto", "New Commit.");
		// 1 - Revert S2 (DF)
		// System.out.print("\n1 - S1 (DF) S2");
		Assert.assertTrue("1 Not Reverted to (S2)",
				this.lu1.revertHash(TH.u1s2Hash, true));
		Assert.assertEquals("1 Incorrect Element Count (S2).", 1, this.lu1
				.getContents().countElements());
		Assert.assertEquals("1 Current Not u1s2Hash.", TH.u1s2Hash, this.lu1
				.getCurrent().getFingerprint().getHash());
		Assert.assertEquals("1 Incorrect Current Path.", "*2+1", this.lu1
				.getCurrent().clonePath().toString());
		// System.out.println("\t*2+1 <=> "+lu1.getCurrent().clonePath().toString());
		this.lu2 = new LogicalUnit("aCard1");
		Assert.assertEquals(" 1 Current(1) is Not Equal Current(2).", this.lu1
				.getCurrent().getFingerprint().getHash(), this.lu2.getCurrent()
				.getFingerprint().getHash());
		// 2 - Null Revert S2 (1,0)
		// System.out.print("2 - S2 (1,0) S2");
		Assert.assertTrue("2 Not Stay At (S2).", this.lu1.revertRelative(1, 0));
		Assert.assertEquals("2 Incorrect Element Count (S2).", 1, this.lu1
				.getContents().countElements());
		Assert.assertEquals("2 Current Not u1s2Hash (S2).", TH.u1s2Hash,
				this.lu1.getCurrent().getFingerprint().getHash());
		Assert.assertEquals("2 Head Not Equals Head (S3).", S3, this.lu1
				.getHead().getFingerprint().getHash());
		Assert.assertEquals("2 Incorrect Current Path.", "*2+1", this.lu1
				.getCurrent().clonePath().toString());
		// System.out.println("\t*2+1 <=> "+lu1.getCurrent().clonePath().toString());
		// 3 - Revert S1 (1,1).
		// System.out.print("3 - S2 (1,1) S1");
		Assert.assertTrue("3 Not Reverted to First (S1).",
				this.lu1.revertRelative(1, 1));
		Assert.assertEquals("3 Incorrect Element Count (S1).", 0, this.lu1
				.getContents().countElements());
		Assert.assertEquals("3 Current Not u1s1Hash (S1).", TH.u1s1Hash,
				this.lu1.getCurrent().getFingerprint().getHash());
		Assert.assertEquals("3 Incorrect Current Path.", "*2+2", this.lu1
				.getCurrent().clonePath().toString());
		// System.out.println("\t*2+2 <=> "+lu1.getCurrent().clonePath().toString());
		// 4 - Revert NONEXISTENT (Return to Previous) (1,1).
		// System.out.print("4 - S1 (1,1) S1");
		Assert.assertFalse("4 Reverted to Non-Existent (S0).",
				this.lu1.revertRelative(1, 1));
		Assert.assertEquals("4 Incorrect Element Count (S0).", 0, this.lu1
				.getContents().countElements());
		Assert.assertEquals("4 Current Not u1s1Hash (S0).", TH.u1s1Hash,
				this.lu1.getCurrent().getFingerprint().getHash());
		Assert.assertEquals("4 Incorrect Current Path.", "*2+2", this.lu1
				.getCurrent().clonePath().toString());
		// System.out.println("\t*2+2 <=> "+lu1.getCurrent().clonePath().toString());
		// 5 - Revert Forward S2 (1,-1).
		// System.out.print("5 - S1 (1,-1) S2");
		Assert.assertTrue("5 Not Reverted Forward to Head.",
				this.lu1.revertRelative(1, -1));
		Assert.assertEquals("5 Current Not u1s2Hash (S2).", TH.u1s2Hash,
				this.lu1.getCurrent().getFingerprint().getHash());
		Assert.assertEquals("5 Incorrect Element Count (S2).", 1, this.lu1
				.getContents().countElements());
		Assert.assertEquals("5 Incorrect Current Path.", "*2+1", this.lu1
				.getCurrent().clonePath().toString());
		// System.out.println("\t*2+1 <=> "+lu1.getCurrent().clonePath().toString());
		// 6 - Revert Forward S3 (1,-1).
		// System.out.print("6 - S2 (1,-1) S3");
		Assert.assertTrue("6 Not Reverted Forward to Head.",
				this.lu1.revertRelative(1, -1));
		Assert.assertEquals("6 Current Not (S3).", S3, this.lu1.getCurrent()
				.getFingerprint().getHash());
		Assert.assertEquals("6 Incorrect Element Count (S3).", 1, this.lu1
				.getContents().countElements());
		Assert.assertEquals("6 Incorrect Current Path.", "*1+0", this.lu1
				.getCurrent().clonePath().toString());
		// System.out.println("\t*1+0 <=> "+lu1.getCurrent().clonePath().toString());
		// 7 - Revert S2 (1,1).
		// System.out.print("7 - S3 (1,1) S1 ");
		Assert.assertTrue("7 Not Reverted Forward to Head.",
				this.lu1.revertRelative(1, 1));
		Assert.assertEquals("7 Current Not (S1).", TH.u1s1Hash, this.lu1
				.getCurrent().getFingerprint().getHash());
		Assert.assertEquals("7 Incorrect Element Count (S1).", 0, this.lu1
				.getContents().countElements());
		Assert.assertEquals("7 Incorrect Current Path.", "*1+1", this.lu1
				.getCurrent().clonePath().toString());
		// System.out.println("\t*1+1 <=> "+lu1.getCurrent().clonePath().toString());
		// System.out.println("\n\tTestEnd");
	}

	/**
	 * Lu_revert hash.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void lu_revertHash() throws InvalidElementException {
		this.lu1 = new LogicalUnit("aCard1");
		// 1a - Revert S2 Head (DF)
		// System.out.print("\n1a - S2 HEAD (DF)");
		Assert.assertTrue("Not Reverted to Head.",
				this.lu1.revertHash(TH.u1s2Hash, true));
		Assert.assertEquals("Current Not u1s2Hash.", TH.u1s2Hash, this.lu1
				.getCurrent().getFingerprint().getHash());
		// System.out.println("\t >> "+lu1.getCurrent().clonePath().toString());
		// 1b - Revert S2 Head (BF)
		// System.out.print("1b - S2 HEAD (BF)");
		Assert.assertTrue("Not Reverted Back to Head.",
				this.lu1.revertHash(TH.u1s2Hash, false));
		Assert.assertEquals("Current Not u1s2Hash.", TH.u1s2Hash, this.lu1
				.getCurrent().getFingerprint().getHash());
		// System.out.println("\t >> "+lu1.getCurrent().clonePath().toString());
		// 2a - Revert S1 Previous (DF)
		// System.out.print("2a - S1 PREVIOUS (DF)");
		Assert.assertTrue("Not Reverted to First Previous.",
				this.lu1.revertHash(TH.u1s1Hash, true));
		Assert.assertEquals("Current Not u1s1Hash.", TH.u1s1Hash, this.lu1
				.getCurrent().getFingerprint().getHash());
		// System.out.println("\t >> "+lu1.getCurrent().clonePath().toString());
		// 2b - Revert S1 Previous (BF)
		// System.out.print("2b - S1 PREVIOUS (BF)");
		Assert.assertTrue("Not Reverted to First Previous.",
				this.lu1.revertHash(TH.u1s1Hash, false));
		Assert.assertEquals("Current Not u1s1Hash.", TH.u1s1Hash, this.lu1
				.getCurrent().getFingerprint().getHash());
		// System.out.println("\t >> "+lu1.getCurrent().clonePath().toString());
		// 3a - Revert NonExistent (Return to Previous)
		// System.out.print("3a - NONEXISTENT (Return to S1 PREVIOUS) (DF)");
		Assert.assertFalse("Reverted to Non Existent Hash.",
				this.lu1.revertHash("NonExistentHash", true));
		Assert.assertEquals("Current Not u1s1Hash.", TH.u1s1Hash, this.lu1
				.getCurrent().getFingerprint().getHash());
		// System.out.println("\t >> "+lu1.getCurrent().clonePath().toString());
		// 3b - Revert NonExistent (Return to Previous)
		// System.out.print("3b - NONEXISTENT (Return to S1 PREVIOUS) (BF)");
		Assert.assertFalse("Reverted to Non Existent Hash.",
				this.lu1.revertHash("NonExistentHash", false));
		Assert.assertEquals("Current Not u1s1Hash.", TH.u1s1Hash, this.lu1
				.getCurrent().getFingerprint().getHash());
		// System.out.println("\t >> "+lu1.getCurrent().clonePath().toString());
	}

	/**
	 * Lu_revert hash2.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void lu_revertHash2() throws InvalidElementException {
		this.lu1 = new LogicalUnit("aCard1");
		final String cXHash = this.lu1.commitValidPath("Scott",
				"Next Revert Hash Search.");
		// System.out.println("\n1 - COMMITX: "+lu1.getCurrent().clonePath().toString());
		// 1a - Revert s2 PREVIOUS (DF)
		// System.out.print("1a - S2 PREVIOUS (DF)");
		Assert.assertTrue(
				"Not Reverted to Previous Head (Depth-First-Search).",
				this.lu1.revertHash(TH.u1s2Hash, true));
		Assert.assertEquals("Current Not u1s2Hash.", TH.u1s2Hash, this.lu1
				.getCurrent().getFingerprint().getHash());
		// System.out.println("\t >> "+lu1.getCurrent().clonePath().toString());
		// 1b - Revert s2 PREVIOUS (BF)
		// System.out.print("1b - S2 PREVIOUS (BF)");
		Assert.assertTrue(
				"Not Reverted to Previous Head (Depth-First-Search).",
				this.lu1.revertHash(TH.u1s2Hash, false));
		Assert.assertEquals("Current Not u1s2Hash.", TH.u1s2Hash, this.lu1
				.getCurrent().getFingerprint().getHash());
		// System.out.println("\t >> "+lu1.getCurrent().clonePath().toString());
		// 2a - Revert s1 PREVIOUS*2 (DF)
		// System.out.print("2a - S1 PREVIOUS (DF)");
		Assert.assertTrue(
				"Not Reverted to Previous Head (Breadth-First-Search).",
				this.lu1.revertHash(TH.u1s1Hash, true));
		Assert.assertEquals("Current Not u1s1Hash.", TH.u1s1Hash, this.lu1
				.getCurrent().getFingerprint().getHash());
		// System.out.println("\t >> "+lu1.getCurrent().clonePath().toString());
		// 2b - Revert s1 PREVIOUS*2 (BF)
		// System.out.print("2b - S1 PREVIOUS (BF)");
		Assert.assertTrue(
				"Not Reverted to Previous Head (Breadth-First-Search).",
				this.lu1.revertHash(TH.u1s1Hash, false));
		Assert.assertEquals("Current Not u1s1Hash.", TH.u1s1Hash, this.lu1
				.getCurrent().getFingerprint().getHash());
		// System.out.println("\t >> "+lu1.getCurrent().clonePath().toString());
	}

	/**
	 * Lu_revert path.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void lu_revertPath() throws InvalidElementException {
		this.lu1 = new LogicalUnit("aCard1");
		Assert.assertTrue("Not Reverted to Head.",
				this.lu1.revertPath(new Path("*1+0")));
		// System.out.println("*1+0 <=> "+lu1.getCurrent().clonePath().toString());
		Assert.assertEquals("Current Not u1s2Hash.", TH.u1s2Hash, this.lu1
				.getCurrent().getFingerprint().getHash());
		Assert.assertTrue("Not Reverted to Previous.",
				this.lu1.revertPath(new Path("*1+0*1+1")));
		// System.out.println("*1+0*1+1 <=> "+lu1.getCurrent().clonePath().toString());
		Assert.assertEquals("Current Not u1s1Hash.", TH.u1s1Hash, this.lu1
				.getCurrent().getFingerprint().getHash());
		Assert.assertTrue("Not Reverted to Head.",
				this.lu1.revertPath(new Path("*1+0*1+0*1+0")));
		// System.out.println("*1+0*1+0*1+0 <=> "+lu1.getCurrent().clonePath().toString());
		Assert.assertEquals("Current Not u1s2Hash.", TH.u1s2Hash, this.lu1
				.getCurrent().getFingerprint().getHash());
	}

	/**
	 * Lu_revert path hash.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void lu_revertPathHash() throws InvalidElementException {
		this.lu1 = new LogicalUnit("aCard1");
		Assert.assertFalse("Reverted to Unrecognized Path (Hash).",
				this.lu1.revertPath(new Path(TH.u1s1Hash)));
	}

	/**
	 * Lu_revert to temporary.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void lu_revertToTemporary() throws InvalidElementException {
		this.lu1 = new LogicalUnit("aCard1");
		final String tempHash = this.lu1.commitTemporary("Scott",
				"Temporary Commit.");
		Assert.assertTrue("Current is Not Temporary.", this.lu1.getCurrent()
				.isTemporary());
		Assert.assertNull("Current Contents is Not Null.", this.lu1
				.getCurrent().cloneContents());
		Assert.assertTrue("Head is Not Temporary.", this.lu1.getHead()
				.isTemporary());
		Assert.assertNull("Head Contents is Not Null.", this.lu1.getHead()
				.cloneContents());
		Assert.assertTrue("Revert to Temporary Failed.",
				this.lu1.revertHash(tempHash, true));
		Assert.assertTrue("Current is Not Temporary.", this.lu1.getCurrent()
				.isTemporary());
		Assert.assertTrue("Head is Not Temporary.", this.lu1.getHead()
				.isTemporary());
	}

	/**
	 * Lu_commit path validity.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void lu_commitPathValidity() throws InvalidElementException {
		this.lu1 = new LogicalUnit("aCard1");
		final State s1 = this.lu1.getCurrent();
		final String cX = this.lu1.commitValidPath("Scott",
				"Valid Path Commit.");
		final State sX = new State(cX);
		Assert.assertEquals(s1, sX.getValidPathPrevious());
	}

	/**
	 * EXCEPTIONAL CASES. ****************************************************
	 * 1). Revert Greater Branch, Greater Position or Unknown Hash. 2).
	 * Invalidly Formated Logical Unit.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */

	@Test
	public void lu_commitDoublePrevious() throws InvalidElementException {
		this.lu1 = new LogicalUnit("lux");
		Assert.assertNull("CURRENT is not null.", this.lu1.getCurrent());
		Assert.assertNull("HEAD is not null.", this.lu1.getHead());
		final String cHash1 = this.lu1
				.commitValidPath("Scott", "First Commit.");
		Assert.assertEquals("CURRENT No Previous.", 0, this.lu1.getCurrent()
				.listPrevious().length);
		Assert.assertEquals("HEAD No Previous.", 0, this.lu1.getHead()
				.listPrevious().length);
		final String cHash2 = this.lu1
				.commitValidPath("Scott", "First Commit.");
		Assert.assertEquals("CURRENT One Previous.", 1, this.lu1.getCurrent()
				.listPrevious().length);
		Assert.assertEquals("HEAD One Previous.", 1, this.lu1.getHead()
				.listPrevious().length);
	}

	/**
	 * Lu_commit temp double previous.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void lu_commitTempDoublePrevious() throws InvalidElementException {
		this.lu1 = new LogicalUnit("lux");
		Assert.assertNull("CURRENT is not null.", this.lu1.getCurrent());
		Assert.assertNull("HEAD is not null.", this.lu1.getHead());
		final String cHash1 = this.lu1
				.commitTemporary("Scott", "First Commit.");
		Assert.assertEquals("CURRENT No Previous.", 0, this.lu1.getCurrent()
				.listPrevious().length);
		Assert.assertEquals("HEAD No Previous.", 0, this.lu1.getHead()
				.listPrevious().length);
		final String cHash2 = this.lu1
				.commitTemporary("Scott", "First Commit.");
		Assert.assertEquals("CURRENT One Previous.", 1, this.lu1.getCurrent()
				.listPrevious().length);
		Assert.assertEquals("HEAD One Previous.", 1, this.lu1.getHead()
				.listPrevious().length);
	}

	/**
	 * Lu_revert exceptional cases.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void lu_revertExceptionalCases() throws InvalidElementException {
		// Set Up for Coverage.
		this.lu1 = new LogicalUnit("aCard1");
		final String commit1x = this.lu1.commitValidPath("Scott",
				"Another Commit.");
		this.lu1.revertRelative(1, 2);
		final String commit2x = this.lu1.commitValidPath("Scott",
				"Another Commit 2.");
		// Test Exceptional Cases.
		Assert.assertEquals("Head Not commit2x.", commit2x, this.lu1.getHead()
				.getFingerprint().getHash());
		Assert.assertFalse("Reverted with too large branch",
				this.lu1.revertRelative(100, 1));
		Assert.assertEquals("1 Current Not commit2x.", commit2x, this.lu1
				.getCurrent().getFingerprint().getHash());
		Assert.assertFalse("Reverted with too large position",
				this.lu1.revertRelative(1, 100));
		Assert.assertEquals("2 Current Not commit2x.", commit2x, this.lu1
				.getCurrent().getFingerprint().getHash());
		Assert.assertFalse("Reverted to unknown hash (DF).",
				this.lu1.revertHash("unknownHash", true));
		Assert.assertEquals("3 Current Not commit2x.", commit2x, this.lu1
				.getCurrent().getFingerprint().getHash());
		Assert.assertFalse("Reverted to unknown hash (BF).",
				this.lu1.revertHash("unknownHash", false));
		Assert.assertEquals("4 Current Not commit2x.", commit2x, this.lu1
				.getCurrent().getFingerprint().getHash());
		Assert.assertFalse("Reverted to Path too Long.",
				this.lu1.revertPath(new Path("*1+0*1+1*1+1*1+1")));
		Assert.assertEquals("5 Current Not commit2x.", commit2x, this.lu1
				.getCurrent().getFingerprint().getHash());
	}

	/**
	 * Lu_update commit not changing lu loaded state.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void lu_updateCommitNotChangingLULoadedState()
			throws InvalidElementException {
		/**
		 * Doesn't Update the InMemory State Maintained if the updated state is
		 * not either the current or head.
		 */
		this.lu1 = new LogicalUnit("aCard1");
		// Revert to S1
		Assert.assertTrue("Failed to Revert to Updated Commit.",
				this.lu1.revertHash(TH.u1s1Hash, true));
		// Temp & Regular Commit
		final String cX3Hash = this.lu1.commitTemporary("Scott",
				"Third Temp Commit");
		final String cX4Hash = this.lu1.commitValidPath("Scott",
				"Fourth Commit");
		// Attempt to Update S1
		Assert.assertTrue("Commit Update Failed.",
				this.lu1.commitUpdate(TH.u1s1Hash, "Upater", "Updated Message"));
		Assert.assertTrue("Failed to Revert to Updated Commit.",
				this.lu1.revertHash(TH.u1s1Hash, false));
		Assert.assertTrue(
				"Incorrect Last Log Entry.",
				this.lu1.getHistoryCrawler().getHistoryLog(true)
						.endsWith(new State(TH.u1s1Hash).getLogEntry() + "\n"));
	}

	/**
	 * Lu_no access find commit contents.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void lu_noAccessFindCommitContents() throws InvalidElementException {
		TH.setupTestingEnvironment(true, false);
		this.lu1 = new LogicalUnit("lux");
		// First Commit - Regular Contents.
		Assert.assertTrue("Add w1File.",
				this.lu1.getContents().addElement(new Artifact(TH.w1File)));
		final String c1Hash = this.lu1
				.commitValidPath("Scott", "First Commit.");
		final String cc1Hash = this.lu1.getCurrent().cloneContents().getHash();
		final File r1File = this.lu1.getCurrent().cloneContents()
				.getElement("c1File.txt").cloneRepositoryFile();
		final String r1Content = TH.gatherContent(r1File);
		// Second Commit - Different Contents.
		TH.writeFile(TH.w1File, "This is the Second Content.");
		final String c2Hash = this.lu1.commitValidPath("Scott",
				"Second Commit.");
		final String cc2Hash = this.lu1.getCurrent().cloneContents().getHash();
		final File r2File = this.lu1.getCurrent().cloneContents()
				.getElement("c1File.txt").cloneRepositoryFile();
		final String r2Content = TH.gatherContent(r2File);
		// Third Commit - Different Contents.
		TH.writeFile(TH.w1File, "This is the Third Content.");
		final String c3Hash = this.lu1
				.commitValidPath("Scott", "Third Commit.");
		final String cc3Hash = this.lu1.getCurrent().cloneContents().getHash();
		final File r3File = this.lu1.getCurrent().cloneContents()
				.getElement("c1File.txt").cloneRepositoryFile();
		final String r3Content = TH.gatherContent(r3File);
		// Find First Commit - Compare to First Commit Hash and Contents
		final State s1 = this.lu1.getHistoryCrawler().findCommitHash(
				this.lu1.getHead(), c1Hash, null, true);
		Assert.assertEquals("Incorrect Commit1 Content Hash.", cc1Hash, s1
				.cloneContents().getHash());
		final File rr1File = s1.cloneContents().getElement("c1File.txt")
				.cloneRepositoryFile();
		final String rr1Content = TH.gatherContent(rr1File);
		Assert.assertEquals("Commit 1 Repository Files are Different.",
				r1Content, rr1Content);
		// Find Second Commit - Compare to First Commit Hash and Contents
		final State s2 = this.lu1.getHistoryCrawler().findCommitHash(
				this.lu1.getHead(), c2Hash, null, true);
		Assert.assertEquals("Incorrect Commit2 Content Hash.", cc2Hash, s2
				.cloneContents().getHash());
		final File rr2File = s2.cloneContents().getElement("c1File.txt")
				.cloneRepositoryFile();
		final String rr2Content = TH.gatherContent(rr2File);
		Assert.assertEquals("Commit 2 Repository Files are Different.",
				r2Content, rr2Content);
		// Find Third Commit - Compare to First Commit Hash and Contents
		final State s3 = this.lu1.getHistoryCrawler().findCommitHash(
				this.lu1.getHead(), c3Hash, null, true);
		Assert.assertEquals("Incorrect Commit3 Content Hash.", cc3Hash, s3
				.cloneContents().getHash());
		final File rr3File = s3.cloneContents().getElement("c1File.txt")
				.cloneRepositoryFile();
		final String rr3Content = TH.gatherContent(rr3File);
		Assert.assertEquals("Commit 2 Repository Files are Different.",
				r3Content, rr3Content);
	}

	/**
	 * Lu_remove deleted file when commit.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void lu_removeDeletedFileWhenCommit() throws InvalidElementException {
		TH.setupTestingEnvironment(true, false);
		this.lu1 = new LogicalUnit("lux");
		Assert.assertEquals("No Elements in Contents.", 0, this.lu1
				.getContents().countElements());
		// Add and Commit Element.
		Assert.assertTrue("Artifact One Not Added.", this.lu1.getContents()
				.addElement(new Artifact(TH.w1File)));
		final String c1Hash = this.lu1.commitValidPath("Scott", "Good Commit.");
		Assert.assertEquals("One Element in Contents.", 1, this.lu1
				.getContents().countElements());
		// Delete and Commit (Should Remove Element).
		TH.deleteFile(TH.w1File);
		Assert.assertFalse("W1File should NOT exist.", TH.w1File.exists());
		final String c2Hash = this.lu1.commitValidPath("Scott",
				"Remove Element Commit.");
		Assert.assertEquals("No Elements in Contents.", 0, this.lu1
				.getContents().countElements());
	}

	/**
	 * Lu_invalid reference file.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void lu_invalidReferenceFile() throws InvalidElementException {
		TH.writeFile(TH.lu1File, "HD::>>\nCU::>>\nST::>>\n");
		this.lu1 = new LogicalUnit("aCard1");
		Assert.assertEquals("Should Have Recreated Contents.", TH.c0Hash,
				this.lu1.getContentsHash());
		Assert.assertEquals("Head Should be Null.", "null",
				this.lu1.getHeadHash());
		Assert.assertEquals("Current Should be Null.", "null",
				this.lu1.getCurrentHash());
	}

	/**
	 * Lu_insert commit check contents.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void lu_insertCommitCheckContents() throws InvalidElementException {
		this.lu1 = new LogicalUnit("aCard1");
		final String iCurrentHash = this.lu1.getCurrent().cloneContents()
				.getHash();
		final String iHeadHash = this.lu1.getHead().cloneContents().getHash();
		TH.writeFile(TH.w1File, "Altered Contents");
		this.lu1.getContents().addElement(new Artifact(TH.w1File));
		final String uContentsHash = this.lu1.getContents().getHash();
		this.lu1.commitInsert("Scott", "Inserted Commit", this.lu1.getCurrent()
				.getHash(), this.lu1.getHead().getHash());
		Assert.assertEquals("Head Should Still Have Same Content Hash.",
				iHeadHash, this.lu1.getHead().cloneContents().getHash());
	}

	/**
	 * Lu_update commit check contents.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void lu_updateCommitCheckContents() throws InvalidElementException {
		this.lu1 = new LogicalUnit("aCard1");
		final String c1Hash = this.lu1.commitTemporary("Scott",
				"Temporary Commit");
		State testState = new State(c1Hash);
		Assert.assertNull("Should be Null Contents.", this.lu1.getCurrent()
				.cloneContents());
		Assert.assertNull("Should be Null Contents.", testState.cloneContents());
		TH.writeFile(TH.w1File,
				"Changed Content to File One for Update Commits.");
		Assert.assertTrue("Not Updated.",
				this.lu1.commitUpdate(c1Hash, "Scott", "Updated Commit"));
		testState = new State(c1Hash);
		Assert.assertNotNull("Should be Not Null Contents.", this.lu1
				.getCurrent().cloneContents());
		Assert.assertNotNull("Should be Not Null Contents.",
				testState.cloneContents());
		Assert.assertEquals("Should be The Same.", this.lu1.getCurrent()
				.cloneContents().describe(), testState.cloneContents()
				.describe());
	}

}
