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
 * The Class FingerprintTest.
 */
public class FingerprintTest {

	// Fingerprints
	/** The fp1. */
	private Fingerprint fp1 = null;

	/** The fp2. */
	private Fingerprint fp2 = null;

	/** The fp3. */
	private Fingerprint fp3 = null;

	/**
	 * Start class test.
	 */
	@BeforeClass
	public static void startClassTest() {
		TH.setupLogging();
		Logger.getInstance().info("FINGERPRINT TEST");
	}

	/**
	 * Setup.
	 */
	@Before
	public void setup() {
		TH.setupTestingEnvironment(true, false);
	}

	/**
	 * Fp_ creation string.
	 */
	@Test
	public void fp_CreationString() {
		this.fp1 = new Fingerprint(TH.f1String);
		this.fp2 = new Fingerprint(TH.f2String);
		this.fp3 = new Fingerprint(TH.f3String);
		Assert.assertEquals("String1 Hash.", TH.f1Hash, this.fp1.getHash());
		Assert.assertEquals("String2 Hash.", TH.f2Hash, this.fp2.getHash());
		Assert.assertEquals("String3 Hash.", TH.f3Hash, this.fp3.getHash());
		Assert.assertEquals("Incorrect toString.", "Fingerprint:" + TH.f1Hash,
				this.fp1.toString());
		// System.out.println("\n\tF1: "+fp1.getHash()+" F2: "+fp2.getHash()+" F3: "+fp3.getHash());
	}

	/**
	 * Fp_creation file.
	 */
	@Test
	public void fp_creationFile() {
		this.fp1 = new Fingerprint(TH.w1File);
		this.fp2 = new Fingerprint(TH.w2File);
		this.fp3 = new Fingerprint(TH.w3File);
		Assert.assertEquals("File1 Hash.", TH.f1Hash, this.fp1.getHash());
		Assert.assertEquals("File2 Hash.", TH.f2Hash, this.fp2.getHash());
		Assert.assertEquals("File3 Hash.", TH.f3Hash, this.fp3.getHash());
		// System.out.println("\n\tF1: "+fp1.getHash()+" F2: "+fp2.getHash()+" F3: "+fp3.getHash());
	}

	/**
	 * Fp_creation directory.
	 */
	@Test
	public void fp_creationDirectory() {
		// Should Only Have a Empty Directory - Do Not recursive add
		// Subcontents ---> Creates Unexceptable times for deep folders.
		this.fp1 = new Fingerprint(TH.workspace);
		// System.out.println("\n<<Hash:"+fp1.getHash());
		Assert.assertEquals("Not c0Hash.", TH.c0Hash, this.fp1.getHash());
	}

	/**
	 * Fp_creation set.
	 */
	@Test
	public void fp_creationSet() {
		this.fp1 = new Fingerprint();
		this.fp1.setHash(TH.f1Hash);
		Assert.assertEquals("Set Hash.", TH.f1Hash, this.fp1.getHash());
	}

	/**
	 * Fp_equals.
	 */
	@Test
	public void fp_equals() {
		this.fp1 = new Fingerprint(TH.f1String);
		this.fp2 = new Fingerprint(TH.w1File);
		Assert.assertTrue("FPs Equal.", this.fp1.equals(this.fp2));
		this.fp3 = new Fingerprint(TH.f2String);
		Assert.assertFalse("FPs Not Equal.", this.fp1.equals(this.fp3));
	}

	/**
	 * Fp_check fingerprint.
	 */
	@Test
	public void fp_checkFingerprint() {
		this.fp1 = new Fingerprint(TH.f1String);
		Assert.assertTrue("FPs Same.", this.fp1.checkFingerprint(TH.f1String));
		Assert.assertTrue("FPs Same.", this.fp1.checkFingerprint(TH.w1File));
		Assert.assertFalse("FPs Different.",
				this.fp1.checkFingerprint(TH.f2String));
		Assert.assertFalse("FPs Different.",
				this.fp1.checkFingerprint(TH.w3File));
	}

	// EXPECTED EXCEPTIONAL CONDITIONS

	/**
	 * Fp_create null file.
	 */
	@Test(expected = NullPointerException.class)
	public void fp_createNullFile() {
		final File nullFile = null;
		new Fingerprint(nullFile);
	}

	/**
	 * Fp_create null string.
	 */
	@Test(expected = NullPointerException.class)
	public void fp_createNullString() {
		final String nullString = null;
		new Fingerprint(nullString);
	}

	/**
	 * Fp_calculate null file.
	 */
	@Test(expected = NullPointerException.class)
	public void fp_calculateNullFile() {
		final File nullFile = null;
		Assert.assertNull(new Fingerprint().calculateHash(nullFile));
	}

	/**
	 * Fp_calculate with non existent file.
	 */
	@Test
	public void fp_calculateWithNonExistentFile() {
		Assert.assertNull("Should Return Null Fingerprint.",
				new Fingerprint().calculateHash(TH.neFile));
	}

}
