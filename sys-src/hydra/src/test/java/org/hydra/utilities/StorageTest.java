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
package org.hydra.utilities;

import java.io.File;

import org.hydra.TH;
import org.hydra.core.Fingerprint;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * The Class StorageTest.
 */
public class StorageTest {

	/** The storage. */
	private Storage storage;

	/**
	 * Before class.
	 */
	@BeforeClass
	public static void beforeClass() {
		TH.setupLogging();
		Logger.getInstance().info("STORAGE TEST");
	}

	/**
	 * Before test.
	 */
	@Before
	public void beforeTest() {
		TH.setupTestingEnvironment(true, true);
		this.storage = new Storage();
	}

	/**
	 * Store_ creation.
	 */
	@Test
	public void store_Creation() {
		final Storage store = new Storage();
	}

	/**
	 * Store_ write content.
	 */
	@Test
	public void store_WriteContent() {
		final File testFile = new File(TH.workspace, "Non-ExistentFile");
		Assert.assertFalse("File Should Not Exist.", testFile.exists());
		final String content1 = "This is the first content.";
		Assert.assertTrue("Write Content 1 Failed.",
				this.storage.overwriteContents(testFile, content1));
		Assert.assertTrue("File Should Exist.", testFile.exists());
		final Fingerprint fp1 = new Fingerprint(content1);
		Assert.assertTrue("Incorrect Content Fingerprint.",
				fp1.checkFingerprint(testFile));
		final String content2 = "This is the second content.";
		Assert.assertTrue("Write Content 1 Failed.",
				this.storage.overwriteContents(testFile, content2));
		final Fingerprint fp2 = new Fingerprint(content2);
		Assert.assertTrue("Incorrect Content Fingerprint.",
				fp2.checkFingerprint(testFile));
		Assert.assertFalse("Fingerprints should not match.", fp1.equals(fp2));
	}

	/**
	 * Store_ read content.
	 */
	@Test
	public void store_ReadContent() {
		final File testFile = new File(TH.workspace, "TestFile");
		final String line1 = "This is the first line.";
		final String line2 = "This is the second line.";
		Assert.assertTrue("Overwrite Content Failed.",
				this.storage.overwriteContents(testFile, line1 + "\n" + line2));
		Assert.assertTrue("Always Successfully Close Location when None Open.",
				this.storage.closeLocation());
		// Open Location.
		Assert.assertFalse("Storage Should Not Have Open Location.",
				this.storage.hasOpenLocation());
		Assert.assertTrue("Storage Failed to Open File.",
				this.storage.openLocation(testFile));
		Assert.assertTrue("Storage Should Have Open Location.",
				this.storage.hasOpenLocation());
		Assert.assertTrue("Storage Should Have More Contents.",
				this.storage.hasNextMember());
		// Read First Content Member.
		Assert.assertEquals("Should Return First Line.", line1,
				this.storage.readNextMember());
		Assert.assertTrue("Storage Should Have More Contents.",
				this.storage.hasNextMember());
		// Read Second Line.
		Assert.assertEquals("Should Return Second Line.", line2,
				this.storage.readNextMember());
		Assert.assertFalse("Storage Should Not Have More Contents.",
				this.storage.hasNextMember());
		// Attempt to Read Beyond EndOfFile
		Assert.assertEquals("Should Return Empty String.", new String(),
				this.storage.readNextMember());
		// Close Locataion.
		Assert.assertTrue("Storage Should Close.", this.storage.closeLocation());
		Assert.assertFalse("Storage Should Not Have Open Location.",
				this.storage.hasOpenLocation());
		Assert.assertEquals("Should Return Empty String.", new String(),
				this.storage.readNextMember());
		Assert.assertFalse("Should Not Have More Contents.",
				this.storage.hasNextMember());
	}

	/**
	 * Store_transfer contents.
	 */
	@Test
	public void store_transferContents() {
		final File testFile1 = new File(TH.workspace, "TestFile1");
		final File testFile2 = new File(TH.workspace, "TestFile2");
		final String content = "This is the Content.";
		Assert.assertTrue("Unable to Store Content to File1.",
				this.storage.overwriteContents(testFile1, content));
		Assert.assertTrue("Unable to transfer content.",
				this.storage.transferContents(testFile1, testFile2));
		Assert.assertTrue("Should Have Same Fingerprints.", new Fingerprint(
				testFile1).checkFingerprint(testFile2));
	}

	/**
	 * Store_null files.
	 */
	@Test
	public void store_nullFiles() {
		Assert.assertFalse("Should Not Succeed Writing to Null.",
				this.storage.overwriteContents(null, "Test Content."));
		Assert.assertFalse("Should Not Succeed Openning Null.",
				this.storage.openLocation(null));
		Assert.assertFalse("Should Not Transfer Null Null.",
				this.storage.transferContents(null, null));
	}

	/**
	 * Store_read contents bytes.
	 */
	@Test
	public void store_readContentsBytes() {
		final byte[] bytes = this.storage.readContentsBytes(TH.w1File);
		Assert.assertNotNull("Null Bytes Returned.", bytes);
		Assert.assertEquals("Byte Array Should Not be Empty.", 13, bytes.length);
	}

	/**
	 * Store_read contents bytes non existent file.
	 */
	@Test
	public void store_readContentsBytesNonExistentFile() {
		final byte[] bytes = this.storage.readContentsBytes(TH.neFile);
		Assert.assertNotNull("Null Bytes Returned.", bytes);
		Assert.assertEquals("Byte Array Should Be Empty.", 0, bytes.length);
	}

	/**
	 * Store_count and append.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void store_countAndAppend() throws Exception {
		final File testFile = new File(TH.workspace, "TestFile");
		Assert.assertEquals("Non-Existent Location Returns -1.", -1,
				this.storage.countMembers(testFile));
		testFile.createNewFile();
		Assert.assertEquals("Should Start with No Members.", 0,
				this.storage.countMembers(testFile));
		Assert.assertTrue("Append First Member.", this.storage.appendMember(
				testFile, "Here is the First Member."));
		Assert.assertEquals("Should Have One Member.", 1,
				this.storage.countMembers(testFile));

	}

}
