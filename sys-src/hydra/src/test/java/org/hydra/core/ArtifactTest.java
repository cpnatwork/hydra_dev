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
 * The Class ArtifactTest.
 */
public class ArtifactTest {

	/** The a1. */
	private Artifact a1;

	/** The a2. */
	private Artifact a2;

	/**
	 * Start class test.
	 */
	@BeforeClass
	public static void startClassTest() {
		TH.setupLogging();
		Logger.getInstance().info("ARTIFACT TEST");
	}

	/**
	 * Setup test.
	 */
	@Before
	public void setupTest() {
		TH.setupTestingEnvironment(true, false);
	}

	/**
	 * A_creation wsf.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void a_creationWSF() throws InvalidElementException {
		this.a1 = new Artifact(TH.w1File);
		Assert.assertNotNull("Not Null 1.", this.a1);
		Assert.assertTrue("Equals True 11.", this.a1.equals(this.a1));
		Assert.assertEquals("Incorrect toString.", "Artifact:" + TH.f1Hash
				+ "[" + TH.w1File + "]", this.a1.toString());
		this.a2 = new Artifact(TH.w1File);
		Assert.assertNotNull("Not Null 2.", this.a2);
		Assert.assertTrue("Equals True 12.", this.a1.equals(this.a2));
		this.a2 = new Artifact(TH.w2File);
		Assert.assertFalse("Equals False 12.", this.a1.equals(this.a2));
		Assert.assertEquals("Incorrect toString.", "Artifact:" + TH.f2Hash
				+ "[" + TH.w2File + "]", this.a2.toString());
	}

	/**
	 * A_creation rf.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void a_creationRF() throws InvalidElementException {
		TH.setupTestingEnvironment(false, true);
		this.a1 = new Artifact(TH.neFile, TH.f1Hash);
		Assert.assertFalse("NEFile exists.", TH.neFile.exists());
		Assert.assertEquals("a1 Wrong Fingerprint.", TH.f1Hash, this.a1
				.getFingerprint().getHash());
		Assert.assertEquals("a1 Wrong Hash.", TH.f1Hash, this.a1.getHash());
		Assert.assertEquals("a1 Hash Methods Different Results.", this.a1
				.getFingerprint().getHash(), this.a1.getHash());
	}

	/**
	 * A_store.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void a_store() throws InvalidElementException {
		this.a1 = new Artifact(TH.w2File);
		final File r2File = new File(TH.fpStore, TH.f2Hash);
		Assert.assertFalse("Not Exists (r2F).", r2File.exists());
		Assert.assertTrue("Store True (c2F).", this.a1.store());
		Assert.assertTrue("Exists (r2f).", r2File.exists());
	}

	/**
	 * A_retrieve.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void a_retrieve() throws InvalidElementException {
		TH.setupRepository(true);
		this.a1 = new Artifact(TH.neFile, TH.f1Hash);
		Assert.assertFalse("NE Not Exists.", TH.neFile.exists());
		Assert.assertTrue("Retrieve.", this.a1.retrieve());
		Assert.assertTrue("NE Exists.", TH.neFile.exists());
	}

	/**
	 * A_status.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void a_status() throws InvalidElementException {
		TH.setupRepository(true);
		this.a1 = new Artifact(TH.neFile, TH.f1Hash);
		Assert.assertEquals("Workspace Not Missing or Repository Not Valid.",
				"- v", this.a1.getStatus(true, true));
		Assert.assertTrue("Artifact should be Retrieved.", this.a1.retrieve());
		Assert.assertEquals("Workspace & Repository Not Valid.", "v v",
				this.a1.getStatus(true, true));
		TH.writeFile(TH.neFile, "Adjusted Text");
		Assert.assertEquals("Workspace Not Changed.", "c",
				this.a1.getStatus(true, false));
		TH.r1File.delete();
		Assert.assertEquals("Workspace Not Changed or Repository Not Missing.",
				"c -", this.a1.getStatus(true, true));
		Assert.assertTrue("Should Refresh Fingerprint.",
				this.a1.refreshFingerprint());
		Assert.assertTrue("Artifact Not Stored.", this.a1.store());
		Assert.assertEquals("Both Not Valid After Store (FP not Updated?).",
				"v v", this.a1.getStatus(true, true));
	}

	/*
	 * EXCEPTIONAL CASES. 1 - Non-Existent File 2 - Non-Existent Hash and File.
	 * 3 - Store Deleted File
	 */

	/**
	 * A_creation not existent file.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test(expected = InvalidElementException.class)
	public void a_creationNotExistentFile() throws InvalidElementException {
		this.a1 = new Artifact(TH.neFile);
	}

	/**
	 * A_creation non exsitent hash.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test(expected = InvalidElementException.class)
	public void a_creationNonExsitentHash() throws InvalidElementException {
		this.a1 = new Artifact(TH.w1File, "NonExistentHash");
	}

	/**
	 * A_store deleted file.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void a_storeDeletedFile() throws InvalidElementException {
		this.a1 = new Artifact(TH.w1File);
		TH.w1File.delete();
		Assert.assertFalse("Deleted File Stored.", this.a1.store());
	}

	/**
	 * A_retrieve un writeable.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void a_retrieveUnWriteable() throws InvalidElementException {
		TH.setupRepository(true);
		this.a1 = new Artifact(TH.w1File, TH.f1Hash);
		TH.w1File.setWritable(false);
		Assert.assertFalse(this.a1.retrieve());
	}

	/**
	 * A_invalid names member separator.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test(expected = InvalidElementException.class)
	public void a_invalidNamesMemberSeparator() throws Exception {
		final File testFile1 = new File(TH.workspace, "::>>");
		try {
			Assert.assertTrue(testFile1.createNewFile());
		} catch (final Exception exception) {
			throw new InvalidElementException("Unable to Create File.",
					exception);
		}
		new Artifact(testFile1);
	}
}
