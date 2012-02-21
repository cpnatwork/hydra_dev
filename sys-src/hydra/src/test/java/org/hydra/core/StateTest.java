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

import java.util.Date;

import org.hydra.TH;
import org.hydra.utilities.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * The Class StateTest.
 */
public class StateTest {

	/** The s1. */
	State s1 = null;

	/** The s2. */
	State s2 = null;

	/**
	 * Start class test.
	 */
	@BeforeClass
	public static void startClassTest() {
		Logger.getInstance().info("STATE TEST");
	}

	/**
	 * Setup test.
	 */
	@Before
	public void setupTest() {
		TH.setupTestingEnvironment(true, false);
	}

	/**
	 * S_creation.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void s_creation() throws InvalidElementException {
		final Container c0 = new Container(TH.workspace);
		final Container c1 = new Container(TH.workspace);
		Assert.assertTrue(c1.addElement(new Artifact(TH.w1File)));
		this.s1 = new State(null, c0, "Scott", "Initial Commit.", "UUID");
		Assert.assertEquals("Incorrect toString.", "State:" + TH.u1s1Hash
				+ "[null]", this.s1.toString());
		// System.out.println("\n"+s1.describe());
		final Date tDate = new Date();
		Assert.assertNotNull("S1 is Null.", this.s1);
		String[] splitStr = TH.u1s1String.split("\n");
		Assert.assertTrue("S1 Incorrect Description.", this.s1.describe()
				.startsWith(splitStr[0] + "\n" + splitStr[1]));
		Assert.assertEquals("S1 Incorrect UUID.", "UUID", this.s1.getUUID());
		Assert.assertEquals("S1 Incorrect State Hash.", TH.u1s1Hash, this.s1
				.getFingerprint().getHash());
		Assert.assertEquals("S1 Incorrect Name.", "Scott", this.s1.getUserId());
		Assert.assertEquals("S1 Incorrect Message.", "Initial Commit.",
				this.s1.getMessage());
		Assert.assertTrue(
				"S1 Incorrect Date. Warning [Transient Failure! Retry Test.]",
				tDate.equals(this.s1.getTimestamp()));
		this.s1.setUserName("Another.User");
		Assert.assertEquals("S1 Incorrect UserName Set.", "Another.User",
				this.s1.getUserId());
		this.s1.setMessage("Another Message");
		Assert.assertEquals("S1 Incorrect Message Set.", "Another Message",
				this.s1.getMessage());
		final Date specDate = new Date(1235);
		this.s1.setTimestamp(specDate);
		Assert.assertEquals("S1 Incorrect Date Set.", specDate,
				this.s1.getTimestamp());
		Assert.assertTrue("S1 is Invalid.", this.s1.isValid());
		this.s1.setValidity(false);
		Assert.assertFalse("S1 is Valid.", this.s1.isValid());
		this.s2 = new State(this.s1, c1, "Scott", "Second Commit.", "UUID2");
		// System.out.println("\n"+s2.describe());
		splitStr = TH.u1s2String.split("\n");
		Assert.assertTrue("S2 Incorrect Description.", this.s2.describe()
				.startsWith(splitStr[0] + "\n" + splitStr[1]));
		Assert.assertEquals("S2 Incorrect Hash.", TH.u1s2Hash, this.s2
				.getFingerprint().getHash());
		// System.out.println("\n>State Description:\n"+s2.describe()+"\n>State Hash: "+s2.getFingerprint().getHash());
	}

	/**
	 * S_creation repository.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void s_creationRepository() throws InvalidElementException {
		TH.setupTestingEnvironment(false, true);
		this.s1 = new State(TH.u1s1Hash);
		Assert.assertEquals("S1 Hash Not Correct.", TH.u1s1Hash, this.s1
				.getFingerprint().getHash());
		Assert.assertEquals("S1 Description Not Correct.", TH.u1s1String,
				this.s1.describe());
		// System.out.println(s1.describe());
		this.s2 = new State(TH.u1s2Hash);
		Assert.assertEquals("S2 Hash Not Correct.", TH.u1s2Hash, this.s2
				.getFingerprint().getHash());
		Assert.assertEquals("S2 Description Not Correct.", TH.u1s2String,
				this.s2.describe());
		// System.out.println(s2.describe());
	}

	/**
	 * S_store.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void s_store() throws InvalidElementException {
		final Container c0 = new Container(TH.workspace);
		this.s1 = new State(null, c0, "Scott", "Initial Commit.", "UUID");
		Assert.assertFalse("S1 already in Repository.", TH.r1r1State.exists());
		Assert.assertTrue("S1 Failed Store.", this.s1.store());
		Assert.assertTrue("S1 not in Repository.", TH.r1r1State.exists());
		// assertEquals("S1 Stored Correct.", TH.u1s1Hash, new
		// Fingerprint().calculateHash(TH.r1r1State));
		final Container c1 = new Container(TH.workspace);
		c1.addElement(new Artifact(TH.w1File));
		this.s2 = new State(this.s1, c1, "Scott", "Second Commit.", "UUID2");
		Assert.assertFalse("S2 already in Repository.", TH.r1r2State.exists());
		Assert.assertTrue("S2 Failed Store.", this.s2.store());
		Assert.assertTrue("S2 not in Repository.", TH.r1r2State.exists());
		// assertEquals("S2 Stored Correct.", TH.u1s2FHash, new
		// Fingerprint().calculateHash(TH.r1r2State));
	}

	/**
	 * S_retrieve.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void s_retrieve() throws InvalidElementException {
		TH.setupTestingEnvironment(false, true);
		this.s2 = new State(TH.u1s2Hash);
		Assert.assertEquals("S2 Hash Not Correct.", TH.u1s2Hash, this.s2
				.getFingerprint().getHash());
		Assert.assertFalse("w1File Exists.", TH.w1File.exists());
		Assert.assertTrue("State Retrieved.", this.s2.retrieve());
		Assert.assertTrue("w1File Does Not Exist.", TH.w1File.exists());
	}

	/**
	 * S_list previous.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void s_listPrevious() throws InvalidElementException {
		TH.setupTestingEnvironment(false, true);
		this.s2 = new State(TH.u1s2Hash);
		final State[] pstates = this.s2.listPrevious();
		Assert.assertEquals("Not One Previous State.", 1, pstates.length);
	}

	/**
	 * S_is temporary.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void s_isTemporary() throws InvalidElementException {
		TH.setupTestingEnvironment(true, true);
		this.s1 = new State(TH.u1s1Hash);
		Assert.assertFalse("S1 is not Temporary.", this.s1.isTemporary());
		final State s0a = new State(null, null, "Scott", "Temporary Commit.",
				"NoUUID");
		Assert.assertEquals("S0A incorrect Hash.", TH.u1s0Hash, s0a
				.getFingerprint().getHash());
		Assert.assertTrue("S0A is Temporary.", s0a.isTemporary());
		Assert.assertTrue("S0A not Stored.", s0a.store());
		Assert.assertTrue("S0A Retrieved.", s0a.retrieve());
		final State s0b = new State(TH.u1s0Hash);
		Assert.assertTrue("S0B is Temporary.", s0b.isTemporary());
		Assert.assertEquals("S0A and S0B not Same.", s0a.describe(),
				s0b.describe());
	}

	/**
	 * S_add remove previous.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void s_addRemovePrevious() throws InvalidElementException {
		TH.setupTestingEnvironment(false, true);
		this.s1 = new State(TH.u1s1Hash);
		this.s2 = new State(TH.u1s2Hash);
		final State s3t = new State(this.s1, null, "Scott", "Temporary Commit.");
		Assert.assertTrue("S3T Not Stored.", s3t.store());
		Assert.assertEquals("S2 Not One Previous.", 1,
				this.s2.listPrevious().length);
		Assert.assertTrue("S3T Not Added to S2.", this.s2.addPrevious(s3t));
		Assert.assertEquals("S2 Not Two Previous.", 2,
				this.s2.listPrevious().length);
		Assert.assertTrue("S3T Not Removed from S2.",
				this.s2.removePrevious(s3t));
		Assert.assertEquals("S2 Not One Previous.", 1,
				this.s2.listPrevious().length);
	}

	/**
	 * S_status.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void s_status() throws InvalidElementException {
		TH.setupTestingEnvironment(false, true);
		this.s1 = new State(TH.u1s2Hash);
		// System.out.println("\nInitial:\t"+s1.getStatus(true, true));
		Assert.assertEquals("WS Not Valid & R Valid.", "c v",
				this.s1.getStatus(true, true));
		Assert.assertTrue("State Not Retrieved.", this.s1.retrieve());
		// System.out.println("Retrieved:\t"+s1.getStatus(true, true));
		Assert.assertEquals("WS & R Valid.", "v v",
				this.s1.getStatus(true, true));
		TH.writeFile(TH.r1r2State, "Adjusted Content.");
		// System.out.println("AlteredRFile1:\t"+s1.getStatus(true, true));
		Assert.assertEquals("WS Valid & R Exception.", "v ?",
				this.s1.getStatus(true, true));
		TH.writeFile(TH.r1r2State, "Adjusted Content.\nTwo Lines.");
		// System.out.println("AlteredRFile2:\t"+s1.getStatus(true, true));
		Assert.assertEquals("WS Valid & R Not Valid.", "v c",
				this.s1.getStatus(true, true));
		if (TH.deleteFile(TH.r1r2State)) {
			// System.out.println("DeletedRFile:\t"+s1.getStatus(true, true));
			Assert.assertEquals("R Missing.", "-",
					this.s1.getStatus(false, true));
		}
		if (TH.deleteFile(TH.w1File)) {
			// System.out.println("DeleteWSFile:\t"+s1.getStatus(true, true));
			Assert.assertEquals("WS Changed.", "c",
					this.s1.getStatus(true, false));
		}
	}

	/**
	 * S_get log entry.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void s_getLogEntry() throws InvalidElementException {
		TH.setupTestingEnvironment(true, true);
		this.s1 = new State(TH.u1s2Hash);
		Assert.assertEquals("Incorrect Log Entry.", "Scott - Second Commit."
				+ "\n\tTimestamp: Mon May 09 16:46:08 CEST 2011"
				+ "\n\tCommit Hash: " + TH.u1s2Hash + "\n\tContent Hash: "
				+ TH.c1Hash + "\n\tPrev[0]: " + TH.u1s1Hash,
				this.s1.getLogEntry());
	}

	/**
	 * S_store add remove previous.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void s_storeAddRemovePrevious() throws InvalidElementException {
		TH.setupTestingEnvironment(true, true);
		this.s1 = new State(TH.u1s1Hash);
		this.s2 = new State(TH.u1s2Hash);
		Assert.assertTrue("S1 Not Removed 1.", this.s2.removePrevious(this.s1));
		Assert.assertTrue("S2 Not Stored 1.", this.s2.store());
		State s2X = new State(TH.u1s2Hash);
		Assert.assertEquals("Different Descriptions 1.", this.s2.describe(),
				s2X.describe());
		Assert.assertTrue("S1 Not Added 2.", this.s2.addPrevious(this.s1));
		Assert.assertTrue("S2 Not Stored 2.", this.s2.store());
		s2X = new State(TH.u1s2Hash);
		Assert.assertEquals("Different Descriptions 2.", this.s2.describe(),
				s2X.describe());
	}

	/**
	 * S_validity.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void s_validity() throws InvalidElementException {
		TH.setupTestingEnvironment(true, true);
		this.s1 = new State(TH.u1s1Hash);
		Assert.assertTrue("S1 Should be Valid.", this.s1.isValid());
		this.s1.setValidity(false);
		Assert.assertFalse("S1 Should be Invalid.", this.s1.isValid());
		this.s2 = new State(TH.u1s2Hash);
		Assert.assertFalse("S2 is Initially Valid.", this.s2.isValid());
		this.s2.setValidity(true);
		Assert.assertTrue("S2 is Result Invalid.", this.s2.isValid());
	}

	/**
	 * S_valid previous.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void s_validPrevious() throws InvalidElementException {
		TH.setupTestingEnvironment(true, true);
		final State s0 = new State(TH.u1s0Hash);
		this.s1 = new State(TH.u1s1Hash);
		this.s2 = new State(TH.u1s2Hash);
		Assert.assertNull("S1 No Valid Previous.",
				this.s1.getValidPathPrevious());
		Assert.assertEquals("S2 Incorrect Valid Previous.", this.s1,
				this.s2.getValidPathPrevious());
		this.s2.setValidPathPrevious(s0);
		Assert.assertEquals("S2 Incorrect Valid Previous.", s0,
				this.s2.getValidPathPrevious());
		// Test Record and Load Functionality
		Assert.assertTrue("S2 Not Stored.", this.s2.store());
		final State s2X = new State(TH.u1s2Hash);
		Assert.assertEquals("S2X Incorrect Valid Previous.", s0,
				s2X.getValidPathPrevious());

	}

	/**
	 * EXCEPTIONAL CASES. ****************************************************
	 * 1. Incorrect State Hash. 2. Null => UserName, Message or UUID.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */

	@Test(expected = InvalidElementException.class)
	public void s_incorrectStateHash() throws InvalidElementException {
		this.s1 = new State("nonExistentHash");
	}

	/**
	 * S_invalid state data.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void s_invalidStateData() throws InvalidElementException {
		try {
			new State(null, null, null, "My Message");
			Assert.fail("Null Name Accepted.");
		} catch (final InvalidElementException e0) {
			try {
				new State(null, null, "Scott", null);
				Assert.fail("Null Message Accepted.");
			} catch (final InvalidElementException e1) {
				try {
					new State(null, null, "Scott", "My Message.", null);
					Assert.fail("Null UUID Accepted.");
				} catch (final InvalidElementException e2) {
				}
			}
		}
	}

}
