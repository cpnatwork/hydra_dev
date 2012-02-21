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
 * The Class ElementComparatorTest.
 */
public class ElementComparatorTest {

	/** The ec. */
	private ElementComparator ec;

	/**
	 * Start class test.
	 */
	@BeforeClass
	public static void startClassTest() {
		Logger.getInstance().info("ELEMENT COMPARATOR TEST");
	}

	/**
	 * Setup test.
	 */
	@Before
	public void setupTest() {
		TH.setupTestingEnvironment(true, false);
	}

	/**
	 * Ec_creation.
	 */
	@Test
	public void ec_creation() {
		this.ec = new ElementComparator();
		Assert.assertNotNull("EC is Null.", this.ec);
	}

	/**
	 * Ec_ comparison.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void ec_Comparison() throws InvalidElementException {
		this.ec = new ElementComparator();
		final Artifact a1 = new Artifact(TH.w1File);
		final Artifact a2a = new Artifact(TH.w2File);
		final Artifact a2b = new Artifact(TH.w2File);
		final Container c1 = new Container(TH.workspace);
		final State s1 = new State(null, c1, "Scott", "Test Commit.");
		Assert.assertEquals("a1 Not Equals a1", 0, this.ec.compare(a1, a1));
		Assert.assertEquals("a1 Not Less Than a2a", -1,
				this.ec.compare(a1, a2a));
		Assert.assertEquals("a2a Not Greater Than a1", 1,
				this.ec.compare(a2a, a1));
		Assert.assertEquals("a2a Not Equals a2b", 0, this.ec.compare(a2a, a2b));
		Assert.assertEquals("c1 Not Equals c1", 0, this.ec.compare(c1, c1));
		Assert.assertEquals("c1 Not Less Than a1", -1, this.ec.compare(c1, a1));
		Assert.assertEquals("a1 Not Greater Than c1", 1,
				this.ec.compare(a1, c1));
		Assert.assertEquals("s1 Not Less Than c1", -1, this.ec.compare(s1, c1));
		Assert.assertEquals("c1 Not Greater Than s1", 1,
				this.ec.compare(c1, s1));
		Assert.assertEquals("s1 Not Equals s1", 0, this.ec.compare(s1, s1));
	}

}
