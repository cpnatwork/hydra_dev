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

import org.hydra.TH;
import org.hydra.utilities.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * The Class PathTest.
 */
public class PathTest {

	/** The p1. */
	private Path p1;

	/**
	 * Before class.
	 */
	@BeforeClass
	public static void beforeClass() {
		Logger.getInstance().info("PATH TEST");
	}

	/**
	 * Before test.
	 */
	@Before
	public void beforeTest() {
		TH.setupTestingEnvironment(false, false);
	}

	/**
	 * P_ creation.
	 */
	@Test
	public void p_Creation() {
		this.p1 = new Path();
		Assert.assertEquals("1 Incorrect Path String Returned.", "*1+0",
				this.p1.toString());
		Assert.assertTrue("1 Is Valid.", this.p1.isValid());
		this.p1 = new Path("*1+0");
		Assert.assertEquals("2 Incorrect Path String Returned.", "*1+0",
				this.p1.toString());
		Assert.assertTrue("2 Is Valid.", this.p1.isValid());
		this.p1 = new Path("*0+0");
		Assert.assertEquals("3 Incorrect Path String Returned.", "*0+0",
				this.p1.toString());
		Assert.assertTrue("3 Is Valid.", this.p1.isValid());
		this.p1 = new Path("x;laksdjf;asdkjf;askdsda");
		Assert.assertEquals("4 Incorrect Path String Returned.", "",
				this.p1.toString());
		Assert.assertFalse("4 Is Not Valid.", this.p1.isValid());
	}

	/**
	 * P_ steps.
	 */
	@Test
	public void p_Steps() {
		final Step s0 = new Step("*0+0");
		final Step s1 = new Step(0, 0);
		Assert.assertEquals("S0 and S1 Not Same.", s0.toString(), s1.toString());
		Assert.assertTrue("Is Empty Step.", s1.isEmpty());
		Assert.assertEquals("Incorrect Step String Returned.", "*0+0",
				s1.toString());
		Assert.assertEquals("0 (1).", 0, s1.shiftDistance(1));
		Assert.assertFalse("Is Not Empty Step.", s1.isEmpty());
		Assert.assertEquals("Incorrect Step String Returned.", "*0+1",
				s1.toString());
		Assert.assertEquals("0 (2).", 0, s1.shiftBranch(1));
		Assert.assertEquals("Incorrect Step String Returned.", "*1+1",
				s1.toString());
		Assert.assertEquals("-4 (3).", -4, s1.shiftDistance(-5));
		Assert.assertEquals("Incorrect Step String Returned.", "*1+0",
				s1.toString());
		Assert.assertEquals("-2 (4).", -2, s1.shiftBranch(-3));
		Assert.assertEquals("Incorrect Step String Returned.", "*0+0",
				s1.toString());
		final Step s2 = new Step(2, 2);
		Assert.assertEquals("Incorrect Step String Returned.", "*0+0", s1
				.shift(s2).toString());
		Assert.assertEquals("Incorrect Step String Returned.", "*2+2",
				s1.toString());
	}

	/**
	 * P_ creation multiple steps.
	 */
	@Test
	public void p_CreationMultipleSteps() {
		// Multiple Steps
		this.p1 = new Path("*1+1*2+5");
		Assert.assertEquals("1 Incorrect Path String.", "*1+1*2+5",
				this.p1.toString());
		Assert.assertEquals("1 Two Steps.", 2, this.p1.countSteps());
		// Compressed Steps
		this.p1 = new Path("*1+1*1+3");
		Assert.assertEquals("2 Incorrect Path String.", "*1+1*1+3",
				this.p1.toString());
		Assert.assertEquals("2 Two Steps.", 2, this.p1.countSteps());
		// Negative Steps Compressed
		this.p1 = new Path("*1+1*1+-3");
		Assert.assertEquals("3 Incorrect Path String.", "*1+1*1+-3",
				this.p1.toString());
		Assert.assertEquals("3 Two Steps.", 2, this.p1.countSteps());
		// Negative Steps Compressed - Loses Branch
		this.p1 = new Path("*2+1*1+-1*1+1");
		Assert.assertEquals("4 Incorrect Path String.", "*2+1*1+-1*1+1",
				this.p1.toString());
		Assert.assertEquals("4 Three Steps.", 3, this.p1.countSteps());
	}

	/**
	 * P_ path stepping.
	 */
	@Test
	public void p_PathStepping() {
		this.p1 = new Path();
		this.p1.move(1, 1);
		Assert.assertEquals("1 One Step.", 1, this.p1.countSteps());
		Assert.assertEquals("1 Incorrect Path String.", "*1+1",
				this.p1.toString());
		this.p1.move(5, 5);
		Assert.assertEquals("2 Two Steps.", 2, this.p1.countSteps());
		Assert.assertEquals("2 Incorrect Path String.", "*1+1*5+5",
				this.p1.toString());
		this.p1.move(new Step(1, -5));
		Assert.assertEquals("3 One Step.", 1, this.p1.countSteps());
		Assert.assertEquals("3 Incorrect Path String.", "*1+1",
				this.p1.toString());
		this.p1.move(new Step(1, -1));
		Assert.assertEquals("4 One Step.", 1, this.p1.countSteps());
		Assert.assertEquals("4 Incorrect Path String.", "*1+0",
				this.p1.toString());
		this.p1.move(new Step(1, -1));
		Assert.assertEquals("5 One Step.", 1, this.p1.countSteps());
		Assert.assertEquals("5 Incorrect Path String.", "*1+0",
				this.p1.toString());
		this.p1.move(2, 0);
		Assert.assertEquals("6 One Step.", 1, this.p1.countSteps());
		Assert.assertEquals("6 Incorrect Path String.", "*2+0",
				this.p1.toString());
		this.p1.move(1, 0);
		Assert.assertEquals("7 One Step.", 1, this.p1.countSteps());
		Assert.assertEquals("7 Incorrect Path String.", "*1+0",
				this.p1.toString());
		this.p1.move(1, 100);
		Assert.assertEquals("8 One Step.", 1, this.p1.countSteps());
		Assert.assertEquals("8 Incorrect Path String.", "*1+100",
				this.p1.toString());
	}

	/**
	 * P_ path stepping negatives.
	 */
	@Test
	public void p_PathSteppingNegatives() {
		this.p1 = new Path();
		this.p1.move(-1, 1);
		Assert.assertEquals("1 One Step.", 1, this.p1.countSteps());
		Assert.assertEquals("1 Incorrect Path String.", "*1+0",
				this.p1.toString());
		this.p1.move(1, -1);
		Assert.assertEquals("1 One Step.", 1, this.p1.countSteps());
		Assert.assertEquals("1 Incorrect Path String.", "*1+0",
				this.p1.toString());
	}

	/**
	 * P_ path step stepping.
	 */
	@Test
	public void p_PathStepStepping() {
		this.p1 = new Path();
		Assert.assertEquals("0 Path Incorrect.", "*1+0", this.p1.toString());
		final Step s1 = new Step();
		// System.out.println("\n1: ["+s1+"] --> "+p1);
		this.p1.move(s1.setStep(1, 0));
		Assert.assertEquals("1 Path Incorrect.", "*1+0", this.p1.toString());
		// System.out.println("2: ["+s1+"] --> "+p1);
		this.p1.move(s1.setStep(2, 0));
		Assert.assertEquals("2 Path Incorrect.", "*2+0", this.p1.toString());
		// System.out.println("3: ["+s1+"] --> "+p1);
		this.p1.move(s1.setStep(3, 2));
		Assert.assertEquals("3 Path Incorrect.", "*3+2", this.p1.toString());
		// System.out.println("4: ["+s1+"] --> "+p1);
		this.p1.move(s1.setStep(2, 0));
		Assert.assertEquals("4 Path Incorrect.", "*3+2*2+0", this.p1.toString());
		// System.out.println("5: ["+s1+"] --> "+p1);
		this.p1.move(s1.setStep(2, -1));
		Assert.assertEquals("5 Path Incorrect.", "*3+1", this.p1.toString());
		// System.out.println("6: ["+s1+"] --> "+p1);
		this.p1.move(s1.setStep(1, -1));
		Assert.assertEquals("6 Path Incorrect.", "*1+0", this.p1.toString());
		// System.out.println("7: ["+s1+"] --> "+p1);
		this.p1.move(s1.setStep(10, -1));
		Assert.assertEquals("7 Path Incorrect.", "*1+0", this.p1.toString());
		// System.out.println("8: ["+s1+"] --> "+p1);
		this.p1.move(s1.setStep(3, 5));
		Assert.assertEquals("8 Path Incorrect.", "*3+5", this.p1.toString());
		// System.out.println("7: ["+s1+"] --> "+p1);
		this.p1.move(s1.setStep(3, 5));
		Assert.assertEquals("9 Path Incorrect.", "*3+5*3+5", this.p1.toString());
		// System.out.println("7: ["+s1+"] --> "+p1);
		this.p1.move(s1.setStep(1, -6));
		Assert.assertEquals("10 Path Incorrect.", "*3+4", this.p1.toString());
		// System.out.println("7: ["+s1+"] --> "+p1);

	}

	/**
	 * EXCEPTIIONAL CASES ************************
	 */

}
