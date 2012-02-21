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
import org.hydra.utilities.FilterOutHidden;
import org.hydra.utilities.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * The Class ContainerTest.
 */
public class ContainerTest {

	/** The c1. */
	private Container c1;

	/**
	 * Start class test.
	 */
	@BeforeClass
	public static void startClassTest() {
		TH.setupLogging();
		Logger.getInstance().info("CONTAINER TEST");
	}

	/**
	 * Setup test.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Before
	public void setupTest() throws InvalidElementException {
		TH.setupTestingEnvironment(true, false);
		this.c1 = new Container(TH.workspace);
	}

	/**
	 * C_creation.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void c_creation() throws InvalidElementException {
		this.c1 = new Container(TH.workspace);
		Assert.assertNotNull("C1 is Null.", this.c1);
		Assert.assertEquals("FP Hash is wc0.", TH.c0Hash, this.c1
				.getFingerprint().getHash());
		Assert.assertEquals("Incorrect toString.", "Container:" + TH.c0Hash
				+ "[" + TH.workspace + "]", this.c1.toString());
		// System.out.print("[FP:"+c1.getFingerprint()+"|"+c1.getFingerprint().getHash()+"]");
	}

	/**
	 * C_store.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void c_store() throws InvalidElementException {
		Assert.assertFalse("R0Container Exists.", TH.r0Cont.exists());
		Assert.assertTrue("R0Con Not Stored.", this.c1.store());
		Assert.assertTrue("R0Container Does Not Exists.", TH.r0Cont.exists());
	}

	/**
	 * C_addremove files.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void c_addremoveFiles() throws InvalidElementException {
		Assert.assertEquals("Element Count is not 0.", 0,
				this.c1.countElements());
		Assert.assertTrue("w1File Not Added.",
				this.c1.addElement(new Artifact(TH.w1File)));
		Assert.assertEquals("Element Count is not 1.", 1,
				this.c1.countElements());
		Assert.assertEquals("Incorrect Hash.", TH.c1Hash, this.c1
				.getFingerprint().getHash());
		Assert.assertFalse("w1File Added Twice.",
				this.c1.addElement(new Artifact(TH.w1File)));
		Assert.assertEquals("Element Count is not 1.", 1,
				this.c1.countElements());
		Assert.assertTrue("w2File Not Added.",
				this.c1.addElement(new Artifact(TH.w2File)));
		Assert.assertEquals("Element Count is not 2.", 2,
				this.c1.countElements());
		Assert.assertEquals("Number of Elements Returned not 2.", 2,
				this.c1.listElements().length);
		Assert.assertEquals("Incorrect Hash.", TH.c12Hash, this.c1
				.getFingerprint().getHash());
		Assert.assertTrue("w2File Not Removed.",
				this.c1.removeElement(new Artifact(TH.w2File)));
		Assert.assertEquals("Element Count is not 1.", 1,
				this.c1.countElements());
		Assert.assertEquals("Incorrect Hash.", TH.c1Hash, this.c1
				.getFingerprint().getHash());
		Assert.assertFalse("w2File Removed Twice.",
				this.c1.removeElement(new Artifact(TH.w2File)));
		Assert.assertEquals("Element Count is not 1.", 1,
				this.c1.countElements());
		Assert.assertFalse("w3File Removed.",
				this.c1.removeElement(new Artifact(TH.w3File)));
		Assert.assertEquals("Element Count is not 1.", 1,
				this.c1.countElements());
		Assert.assertTrue("w1File Not Removed.",
				this.c1.removeElement(new Artifact(TH.w1File)));
		Assert.assertEquals("Element Count is not 0.", 0,
				this.c1.countElements());
		Assert.assertEquals("Number of Elements Returned not 0.", 0,
				this.c1.listElements().length);
		Assert.assertEquals("Incorrect Hash.", TH.c0Hash, this.c1
				.getFingerprint().getHash());
	}

	/**
	 * C_describe.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void c_describe() throws InvalidElementException {
		final Artifact a1 = new Artifact(TH.w1File);
		final Artifact a2 = new Artifact(TH.w2File);
		final StringBuilder sb = new StringBuilder(Container.HEADER + "\n");
		Assert.assertEquals("Not Header.", sb.toString(), this.c1.describe());
		Assert.assertTrue("w1File Not Added.", this.c1.addElement(a1));
		sb.append(a1.describe());
		Assert.assertEquals("Not Header and w1File.", sb.toString(),
				this.c1.describe());
		Assert.assertTrue("w2File Not Added.", this.c1.addElement(a2));
		sb.append(a2.describe());
		Assert.assertEquals("Not Header, w1File and w2File.", sb.toString(),
				this.c1.describe());
	}

	/**
	 * C_store files.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void c_storeFiles() throws InvalidElementException {
		// Check For Empty
		Assert.assertEquals("Repository Not Empty.", 0,
				TH.fpStore.listFiles(new FilterOutHidden()).length);
		Assert.assertTrue("Empty Storage Failed.", this.c1.store());
		final Artifact a1 = new Artifact(TH.w1File);
		this.c1.addElement(a1);
		Assert.assertTrue("Single Storage Failed.", this.c1.store());
		final Artifact a2 = new Artifact(TH.w2File);
		this.c1.addElement(a2);
		Assert.assertTrue("Double Storage Failed.", this.c1.store());
		Assert.assertEquals("Repository Doesn't Contain Correct Files.", 5,
				TH.fpStore.listFiles(new FilterOutHidden()).length);
	}

	/**
	 * C_retrieve files.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void c_retrieveFiles() throws InvalidElementException {
		TH.setupTestingEnvironment(false, true);
		Assert.assertEquals("Workspace Contains Files.", 0,
				TH.workspace.listFiles(new FilterOutHidden()).length);
		this.c1 = new Container(TH.workspace, TH.c12Hash);
		Assert.assertEquals("Incorrect Hash.", TH.c12Hash, this.c1
				.getFingerprint().getHash());
		Assert.assertEquals("Incorrect Number of Elements.", 2,
				this.c1.countElements());
		Assert.assertTrue("Container not retrieved.", this.c1.retrieve());
		Assert.assertEquals("Workspace Does not Contain Two Files.", 2,
				TH.workspace.listFiles(new FilterOutHidden()).length);
	}

	/**
	 * C_retrieve complex.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void c_retrieveComplex() throws InvalidElementException {
		TH.setupTestingEnvironment(false, true);
		Assert.assertEquals("Workspace Contains Files.", 0,
				TH.workspace.listFiles(new FilterOutHidden()).length);
		this.c1 = new Container(TH.workspace, TH.c123c1Hash);
		Assert.assertEquals("Incorrect Hash.", TH.c123c1Hash, this.c1
				.getFingerprint().getHash());
		Assert.assertEquals("Incorrect Number of Elements.", 5,
				this.c1.countElements());
		Assert.assertTrue("Container not retrieved.", this.c1.retrieve());
		Assert.assertEquals("Workspace Does not Contain Four Files.", 4,
				TH.workspace.listFiles(new FilterOutHidden()).length);
	}

	/**
	 * C_status.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void c_status() throws InvalidElementException {
		TH.setupTestingEnvironment(false, true);
		this.c1 = new Container(TH.w1Dir, TH.c1Hash);
		final Artifact a1 = new Artifact(TH.d1w2File, TH.f2Hash);
		// System.out.println("\nStart:\t\t"+c1.getStatus(true, true));
		Assert.assertEquals("Workspace Not Missing or Repository Not Valid.",
				"- v", this.c1.getStatus(true, true));
		Assert.assertTrue("Container Not Retrieved.", this.c1.retrieve());
		// System.out.println("Retrieve1:\t"+c1.getStatus(true, true));
		Assert.assertEquals("Workspace & Repository Not Valid.", "v v",
				this.c1.getStatus(true, true));
		// ADD ELEMENT.
		Assert.assertTrue("Artifact Not Added.", this.c1.addElement(a1));
		// System.out.println("Add:\t\t"+c1.getStatus(true,
		// true)+" | "+a1.getStatus(true, true));
		Assert.assertEquals("Workspace & Repository Not Changed.", "c c",
				this.c1.getStatus(true, true));
		Assert.assertTrue("Container Not Retrieved 2.", this.c1.retrieve());
		// System.out.println("Retrieve2:\t"+c1.getStatus(true,
		// true)+" | "+a1.getStatus(true, true));
		Assert.assertEquals("Workspace Not Valid & Repository Not Changed.",
				"v c", this.c1.getStatus(true, true));
		Assert.assertTrue("Container Not Stored.", this.c1.store());
		// System.out.println("Store:\t\t"+c1.getStatus(true,
		// true)+" | "+a1.getStatus(true, true));
		Assert.assertEquals("Workspace & Repository Not Valid.", "v v",
				this.c1.getStatus(true, true));
		final File rFile = new File(Configuration.getInstance().getFPStore(),
				this.c1.getFingerprint().getHash());
		rFile.delete();
		// System.out.println("Deleted:\t"+c1.getStatus(true,
		// true)+" | "+a1.getStatus(true, true));
		Assert.assertEquals("Repository Not Missing.", "-",
				this.c1.getStatus(false, true));
		Assert.assertEquals("Workspace Not Valid.", "v",
				this.c1.getStatus(true, false));
		// ADJUST ADDED ELEMENT.
		Assert.assertTrue("Container Not Stored.", this.c1.store());
		// System.out.println("Store2:\t\t"+c1.getStatus(true,
		// true)+" | "+a1.getStatus(true, true));
		Assert.assertEquals("Workspace & Repository Not Valid.", "v v",
				this.c1.getStatus(true, true));
		TH.writeFile(TH.d1w2File, "Adjusted Text.");
		// System.out.println("ChangeFile:\t"+c1.getStatus(true,
		// true)+" | "+a1.getStatus(true, true));
		Assert.assertEquals("Workspace Not Changed.", "c v",
				this.c1.getStatus(true, true));
		TH.d1w2File.delete();
		// System.out.println("DeleteFile:\t"+c1.getStatus(true,
		// true)+" | "+a1.getStatus(true, true));
		Assert.assertEquals("Workspace Not Changed.", "c v",
				this.c1.getStatus(true, true));
		Assert.assertTrue("Container Not Retrieved 3.", this.c1.retrieve());
		// System.out.println("Retrieve3:\t"+c1.getStatus(true,
		// true)+" | "+a1.getStatus(true, true));
		Assert.assertEquals("Workspace & Repository Not Valid.", "v v",
				this.c1.getStatus(true, true));
		// ADJUST EXTRA FILE.
		TH.writeFile(TH.d1w3File, "Another File Added.");
		// System.out.println("ExtraFile:\t"+c1.getStatus(true,
		// true)+" | "+a1.getStatus(true, true));
		Assert.assertEquals("Workspace & Repository Not Valid.", "v v",
				this.c1.getStatus(true, true));
	}

	/**
	 * EXCEPTIONAL CASES. **************************************************** 1
	 * - Invalid Hash. 2 - Store Deleted File. 3 - Retrieve Unwriteable File. 4
	 * - Add Deep Element. 5 - Add Microsoft Pathed Element
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */

	@Test(expected = InvalidElementException.class)
	public void c_invalidHash() throws InvalidElementException {
		this.c1 = new Container(TH.workspace, "Invalid Hash.");
	}

	/**
	 * C_store deleted file.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void c_storeDeletedFile() throws InvalidElementException {
		this.c1 = new Container(TH.workspace);
		this.c1.addElement(new Artifact(TH.w1File));
		final int initCount = this.c1.countElements();
		TH.w1File.delete();
		Assert.assertFalse("Refresh Fingerprint.", this.c1.refreshFingerprint());
		Assert.assertTrue("Deleted Container Stored.", this.c1.store());
		Assert.assertEquals("Deleted File Removed From Container.",
				initCount - 1, this.c1.countElements());
	}

	/**
	 * C_retrieve un writeable.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void c_retrieveUnWriteable() throws InvalidElementException {
		TH.setupRepository(true);
		this.c1 = new Container(TH.workspace);
		this.c1.addElement(new Artifact(TH.w1File, TH.f1Hash));
		TH.w1File.setWritable(false);
		Assert.assertFalse(this.c1.retrieve());
	}

	/**
	 * C_add remove deep element.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void c_addRemoveDeepElement() throws InvalidElementException {
		TH.setupTestingEnvironment(true, true);
		this.c1 = new Container(TH.workspace);
		Assert.assertEquals("Expected 0 Elements.", 0, this.c1.countElements());
		// System.out.println("\n"+c1.describe());
		// Add Deep File
		Assert.assertTrue("Should Add Deep File.",
				this.c1.addElement(new Artifact(TH.d1w1File)));
		Assert.assertEquals("Expected 2 Elements.", 2, this.c1.countElements());
		// System.out.println(c1.describe());
		// Add Deep Directory
		final File ssDir = new File(TH.w1Dir, "subSubDirectory");
		Assert.assertTrue("Making SSDir Failed.", ssDir.mkdir());
		Assert.assertTrue("SSDir Not Exists.", ssDir.exists());
		final Container ssC = new Container(ssDir);
		Assert.assertTrue("Should Add Deep Directory.", this.c1.addElement(ssC));
		Assert.assertEquals("Expected 3 Elements.", 3, this.c1.countElements());
		// System.out.println(c1.describe());
		// Remove Deep Directory
		Assert.assertTrue("Should Remove Deep Directory.",
				this.c1.removeElement(ssC));
		Assert.assertEquals("Expected 2 Elements.", 2, this.c1.countElements());
		// System.out.println(c1.describe());
		// Remove Deep File
		Assert.assertTrue("Should Remove Deep Directory.",
				this.c1.removeElement(new Artifact(TH.d1w1File)));
		Assert.assertEquals("Expected 1 Elements.", 1, this.c1.countElements());
		// System.out.println(c1.describe());
	}

	/**
	 * C_get deep element.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void c_getDeepElement() throws InvalidElementException {
		TH.setupTestingEnvironment(true, false);
		// Create Container and Add SubSubElements (Container & Artifact).
		this.c1 = new Container(TH.workspace);
		final File ssDir = new File(TH.w1Dir, "subSubDirectory");
		ssDir.mkdir();
		final Container ssContainer = new Container(ssDir);
		Assert.assertTrue("ssDir Not Added.", this.c1.addElement(ssContainer));
		final File ssFile = new File(TH.w1Dir, "subSubFile");
		TH.writeFile(ssFile, "Here is a file in a subdirectory.");
		final Artifact ssArtifact = new Artifact(ssFile);
		Assert.assertTrue("ssFile Not Added.", this.c1.addElement(ssArtifact));
		// Retrieve Deep Container
		final Container deepContainer = (Container) this.c1.getElement(ssDir
				.getPath());
		Assert.assertNotNull("Deep Container is Null.", deepContainer);
		Assert.assertEquals("Incorrect Container Returned.", ssContainer,
				deepContainer);
		// Retrieve Deep File
		final Artifact deepArtifact = (Artifact) this.c1.getElement(ssFile
				.getPath());
		Assert.assertNotNull("Deep File is Null.", deepArtifact);
		Assert.assertEquals("Incorrect Artifact Returned.", ssArtifact,
				deepArtifact);
	}

	/**
	 * C_add container and contents.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void c_addContainerAndContents() throws InvalidElementException {
		// Create Necessary Containers
		this.c1 = new Container(TH.workspace);
		final Container containerWithContents = new Container(TH.w1Dir);
		// Verify Containers are Empty
		Assert.assertEquals("Workspace Should have No Elements.", 0,
				this.c1.countElements());
		Assert.assertEquals("SubContainer Should have No Elements.", 0,
				containerWithContents.countElements());
		// Add Container and Contents Recursively
		Assert.assertTrue("Unable to Add Container and Contents.",
				this.c1.addContainerAndContents(containerWithContents));
		// Verify Container and Contents were Added
		Assert.assertEquals("Workspace Should have Two Elements.", 2,
				this.c1.countElements());
		Assert.assertEquals("SubContainer Should have One Element.", 1,
				containerWithContents.countElements());
	}

	/**
	 * C_add difficult file names.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void c_addDifficultFileNames() throws Exception {
		this.c1 = new Container(TH.workspace);
		// Check Parenthetical characters.
		final File testFile1 = new File(TH.workspace, "[({<>})]");
		Assert.assertTrue(testFile1.createNewFile());
		Assert.assertTrue("Did Not Add " + testFile1.getName(),
				this.c1.addElement(new Artifact(testFile1)));
		// Check Math characters.
		final File testFile2 = new File(TH.workspace, "+-*=~");
		Assert.assertTrue(testFile2.createNewFile());
		Assert.assertTrue("Did Not Add " + testFile2.getName(),
				this.c1.addElement(new Artifact(testFile2)));
		// Check Punctuation characters.
		final File testFile3 = new File(TH.workspace, ".,:;#\'\"$&§");
		Assert.assertTrue(testFile3.createNewFile());
		Assert.assertTrue("Did Not Add " + testFile3.getName(),
				this.c1.addElement(new Artifact(testFile3)));
		// Check <space> character.
		final File testFile4 = new File(TH.workspace, "a b c");
		Assert.assertTrue(testFile4.createNewFile());
		Assert.assertTrue("Did Not Add " + testFile4.getName(),
				this.c1.addElement(new Artifact(testFile4)));
		// Check äöüß character.
		final File testFile5 = new File(TH.workspace, "äöüß");
		Assert.assertTrue(testFile5.createNewFile());
		Assert.assertTrue("Did Not Add " + testFile5.getName(),
				this.c1.addElement(new Artifact(testFile5)));
		// Check Storage & Retrieval
		Assert.assertTrue("Did Not Store.", this.c1.store());
		Assert.assertTrue("Did Not Retrieve.", this.c1.retrieve());
		// Check Loading From Repository.
		final Container c2 = new Container(TH.workspace, this.c1.getHash());
		Assert.assertEquals("C1 and C2 Not Equal.", c2, this.c1);
		// Check Deleting Workspace Retrieval
		TH.deleteFile(testFile1);
		TH.deleteFile(testFile2);
		TH.deleteFile(testFile3);
		TH.deleteFile(testFile4);
		TH.deleteFile(testFile5);
		Assert.assertTrue("C2 Did Not Retrieve.", c2.retrieve());
		// Display Result
		// System.out.println("\n"+c1.describe()+c2.describe());
	}

	/**
	 * C_add ill formed element.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test(expected = InvalidElementException.class)
	public void c_addIllFormedElement() throws Exception {
		final File testFile1 = new File("\\");
		testFile1.createNewFile();
		final Artifact a1 = new Artifact(testFile1);
		this.c1 = new Container(TH.workspace);
		Assert.assertFalse("Should Not Add Element.", this.c1.addElement(a1));
	}
}
