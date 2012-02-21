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
package org.hydra.utilities.diff.hmdiff;

import org.hydra.TH;
import org.hydra.utilities.Logger;
import org.hydra.utilities.diff.ChangeAdd;
import org.hydra.utilities.diff.ChangeDelete;
import org.hydra.utilities.diff.ChangeReplace;
import org.hydra.utilities.diff.ChangeSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * The Class HMDiffTest.
 */
public class HMDiffTest {

	/** The from array. */
	String[] fromArray;

	/** The to array. */
	String[] toArray;

	/** The hm diff. */
	HMDiff hmDiff;

	/** The hm change set. */
	ChangeSet hmChangeSet;

	/** The change set. */
	ChangeSet changeSet;

	/**
	 * Before class.
	 */
	@BeforeClass
	public static void beforeClass() {
		TH.setupLogging();
		Logger.getInstance().info("HMDIFF TEST");
	}

	/**
	 * Before.
	 */
	@Before
	public void before() {
		this.changeSet = new ChangeSet();
	}

	/**
	 * Hm small.
	 */
	@Test
	public void hmSmall() {
		// Compared Arrays and Expect Change Set.
		this.fromArray = "a".split(" ");
		this.toArray = "a".split(" ");
		// Calculate Difference and Change Set.
		this.hmDiff = new HMDiff(this.fromArray, this.toArray);
		// display();
		this.hmChangeSet = this.hmDiff.getChangeSet();
		Assert.assertEquals(this.changeSet.toString(),
				this.hmChangeSet.toString());
		final HMTrace hmTrace = this.hmDiff.findLongestTrace();
		final HMTrace[] hmTraceArray = hmTrace.toArray();
		Assert.assertEquals("Should Have Same Length.", hmTrace.length(),
				hmTraceArray.length);
		Assert.assertEquals("1->1[0->0[null]]", hmTrace.toString());
	}

	/**
	 * Hm same.
	 */
	@Test
	public void hmSame() {
		// Compared Arrays and Expect Change Set.
		this.fromArray = "a b b c".split(" ");
		this.toArray = "a b b c".split(" ");
		// Calculate Difference and Change Set.
		this.hmDiff = new HMDiff(this.fromArray, this.toArray);
		// display();
		this.hmChangeSet = this.hmDiff.getChangeSet();
		Assert.assertEquals(this.changeSet.toString(),
				this.hmChangeSet.toString());
	}

	// @Ignore
	/**
	 * Hm_small add front.
	 */
	@Test
	public void hm_smallAddFront() {
		// Compared Arrays and Expect Change Set.
		this.fromArray = "".split(" ");
		this.toArray = "a".split(" ");
		this.changeSet.add(new ChangeReplace(1, " a".split(" "), 1));
		// Calculate Difference and Change Set.
		this.hmDiff = new HMDiff(this.fromArray, this.toArray);
		// display();
		this.hmChangeSet = this.hmDiff.getChangeSet();
		Assert.assertEquals(this.changeSet.toString(),
				this.hmChangeSet.toString());
	}

	// @Ignore
	/**
	 * Hm add front.
	 */
	@Test
	public void hmAddFront() {
		// Compared Arrays and Expect Change Set.
		this.fromArray = "a b b c".split(" ");
		this.toArray = "b a b b c".split(" ");
		this.changeSet.add(new ChangeAdd(1, new String[] { "b" }));
		// Calculate Difference and Change Set.
		this.hmDiff = new HMDiff(this.fromArray, this.toArray);
		// display();
		this.hmChangeSet = this.hmDiff.getChangeSet();
		Assert.assertEquals(this.changeSet.toString(),
				this.hmChangeSet.toString());
	}

	// @Ignore
	/**
	 * Hm add end.
	 */
	@Test
	public void hmAddEnd() {
		// Compared Arrays and Expect Change Set.
		this.fromArray = "a b b c".split(" ");
		this.toArray = "a b b c b".split(" ");
		this.changeSet.add(new ChangeAdd(5, new String[] { "b" }));
		// Calculate Difference and Change Set.
		this.hmDiff = new HMDiff(this.fromArray, this.toArray);
		// display();
		this.hmChangeSet = this.hmDiff.getChangeSet();
		Assert.assertEquals(this.changeSet.toString(),
				this.hmChangeSet.toString());

	}

	// @Ignore
	/**
	 * Hm_ add2.
	 */
	@Test
	public void hm_Add2() {
		// Compared Arrays and Expect Change Set.
		this.fromArray = "a b b c".split(" ");
		this.toArray = "a b c b c c".split(" ");
		this.changeSet.add(new ChangeAdd(3, new String[] { "c" }));
		this.changeSet.add(new ChangeAdd(5, new String[] { "c" }));
		// Calculate Difference and Change Set.
		this.hmDiff = new HMDiff(this.fromArray, this.toArray);
		// display();
		this.hmChangeSet = this.hmDiff.getChangeSet();
		Assert.assertEquals(this.changeSet.toString(),
				this.hmChangeSet.toString());
	}

	// @Ignore
	/**
	 * Hm_ add4.
	 */
	@Test
	public void hm_Add4() {
		// Compared Arrays and Expected Change Set
		this.fromArray = "a b b c".split(" ");
		this.toArray = "a a b b b b c c".split(" ");
		this.changeSet.add(new ChangeAdd(2, new String[] { "a" }));
		this.changeSet.add(new ChangeAdd(4, "b b".split(" ")));
		this.changeSet.add(new ChangeAdd(5, new String[] { "c" }));
		// Calculate Difference and Change Set.
		this.hmDiff = new HMDiff(this.fromArray, this.toArray);
		// display();
		this.hmChangeSet = this.hmDiff.getChangeSet();
		Assert.assertEquals(this.changeSet.toString(),
				this.hmChangeSet.toString());
		final String trans = this.hmDiff.describeTransformation();
	}

	// @Ignore
	/**
	 * Hm_ remove1.
	 */
	@Test
	public void hm_Remove1() {
		// Compared Arrays and Expected Change Set.
		this.fromArray = "a b b c".split(" ");
		this.toArray = "a b c".split(" ");
		final ChangeDelete cDelete = new ChangeDelete(3, new String[] { "b" });
		Assert.assertEquals(1, cDelete.countDeleted());
		this.changeSet.add(cDelete);
		// Calculate Differnce and ChangeSet.
		this.hmDiff = new HMDiff(this.fromArray, this.toArray);
		// display();
		this.hmChangeSet = this.hmDiff.getChangeSet();
		Assert.assertEquals(this.changeSet.toString(),
				this.hmChangeSet.toString());
	}

	// @Ignore
	/**
	 * Hm remove front.
	 */
	@Test
	public void hmRemoveFront() {
		// Compared Arrays and Expected Change Set.
		this.fromArray = "a b b c".split(" ");
		this.toArray = "b b c".split(" ");
		this.changeSet.add(new ChangeDelete(1, new String[] { "a" }));
		// Calculate Difference and ChangeSet.
		this.hmDiff = new HMDiff(this.fromArray, this.toArray);
		// display();
		this.hmChangeSet = this.hmDiff.getChangeSet();
		Assert.assertEquals(this.changeSet.toString(),
				this.hmChangeSet.toString());
	}

	// @Ignore
	/**
	 * Hm remove end.
	 */
	@Test
	public void hmRemoveEnd() {
		// Compared Arrays and Expected Change Set.
		this.fromArray = "a b b c".split(" ");
		this.toArray = "a b b".split(" ");
		this.changeSet.add(new ChangeDelete(4, new String[] { "c" }));
		// Calculate Difference and ChangeSet.
		this.hmDiff = new HMDiff(this.fromArray, this.toArray);
		// display();
		this.hmChangeSet = this.hmDiff.getChangeSet();
		Assert.assertEquals(this.changeSet.toString(),
				this.hmChangeSet.toString());
	}

	// @Ignore
	/**
	 * Hm change front.
	 */
	@Test
	public void hmChangeFront() {
		// Compared Arrays and Expected Change Set.
		this.fromArray = "a b b c".split(" ");
		this.toArray = "c b b c".split(" ");
		this.changeSet.add(new ChangeReplace(1, new String[] { "a", "c" }, 1));
		// Calculate Difference and ChangeSet.
		this.hmDiff = new HMDiff(this.fromArray, this.toArray);
		// display();
		this.hmChangeSet = this.hmDiff.getChangeSet();
		Assert.assertEquals(this.changeSet.toString(),
				this.hmChangeSet.toString());
	}

	// @Ignore
	/**
	 * Hm change back.
	 */
	@Test
	public void hmChangeBack() {
		// Compared Arrays and Expected Change Set.
		this.fromArray = "a b b c".split(" ");
		this.toArray = "a b b a".split(" ");
		this.changeSet.add(new ChangeReplace(4, new String[] { "c", "a" }, 1));
		// Calculate Difference and ChangeSet.
		this.hmDiff = new HMDiff(this.fromArray, this.toArray);
		// display();
		this.hmChangeSet = this.hmDiff.getChangeSet();
		Assert.assertEquals(this.changeSet.toString(),
				this.hmChangeSet.toString());
	}

	// @Ignore
	/**
	 * Hm change multiple back.
	 */
	@Test
	public void hmChangeMultipleBack() {
		// Compared Arrays and Expected Change Set.
		this.fromArray = "a b b c".split(" ");
		this.toArray = "a b a a".split(" ");
		this.changeSet.add(new ChangeReplace(3, "b c a a".split(" "), 2));
		// Calculate Difference and ChangeSet.
		this.hmDiff = new HMDiff(this.fromArray, this.toArray);
		// display();
		this.hmChangeSet = this.hmDiff.getChangeSet();
		Assert.assertEquals(this.changeSet.toString(),
				this.hmChangeSet.toString());
	}

	// @Ignore
	/**
	 * Hm_ advanced1.
	 */
	@Test
	public void hm_Advanced1() {
		// Compared Arrays and Expected Change Set.
		this.fromArray = "a b c a b b a".split(" ");
		this.toArray = "c b a b a c".split(" ");
		this.changeSet.add(new ChangeAdd(1, "c b".split(" ")));
		this.changeSet.add(new ChangeDelete(3, "c".split(" ")));
		this.changeSet.add(new ChangeReplace(5, "b b a c".split(" "), 3));
		this.hmDiff = new HMDiff(this.fromArray, this.toArray);
		// display();
		this.hmChangeSet = this.hmDiff.getChangeSet();
		Assert.assertEquals(this.changeSet.toString(),
				this.hmChangeSet.toString());
		// Testing Descriptions
		final String transformation = this.hmDiff.describeTransformation();
		final String matches = this.hmDiff.describeMatches();
		final String traces = this.hmDiff.describeTraces();
	}

	/**
	 * Hm_ file test.
	 */
	@Test
	public void hm_FileTest() {
		this.changeSet.add(new ChangeAdd(2,
				new String[] { "L2: This is some more Content." }));
		TH.writeFile(TH.w2File, "L1: Initial Content.");
		TH.writeFile(TH.w3File,
				"L1: Initial Content.\nL2: This is some more Content.");
		this.hmDiff = new HMDiff(TH.w2File, TH.w3File);
		// System.out.println(hmDiff.describeTransformation());
		this.hmChangeSet = this.hmDiff.getChangeSet();
		Assert.assertEquals(this.changeSet.toString(),
				this.hmChangeSet.toString());
	}

	/**
	 * Hm_ check deleted.
	 */
	@Test
	public void hm_CheckDeleted() {
		final ChangeDelete cDelete = new ChangeDelete(3, new String[] { "b" });
		Assert.assertEquals(1, cDelete.countDeleted());
		final ChangeAdd cAdd = new ChangeAdd(1, new String[] { "a" });
		Assert.assertEquals(0, cAdd.countDeleted());

	}

	/**
	 * DISPLAY HELPERS ********************************************************.
	 */

	private void print() {
		this.printArrays();
		System.out.println(this.hmDiff.describeTransformation());
	}

	/**
	 * Prints the arrays.
	 */
	private void printArrays() {
		System.out.println("");
		this.printArray("fromArray", this.fromArray);
		this.printArray("toArray", this.toArray);
	}

	/**
	 * Prints the array.
	 * 
	 * @param name
	 *            the name
	 * @param array
	 *            the array
	 */
	private void printArray(final String name, final String[] array) {
		System.out.print(name + ":\t");
		for (final String s : array) {
			System.out.print(s + " ");
		}
		System.out.println("");
	}

}
