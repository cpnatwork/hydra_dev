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

import java.util.ArrayList;

import org.hydra.TH;
import org.hydra.utilities.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * The Class HistoryCrawlerTest.
 */
public class HistoryCrawlerTest {

	/** The lu. */
	private LogicalUnit lu;

	/** The hc. */
	private HistoryCrawler hc;

	/**
	 * Before class.
	 */
	@BeforeClass
	public static void beforeClass() {
		Logger.getInstance().info("HISTORY CRAWLER TEST");
	}

	/**
	 * Before.
	 */
	@Before
	public void before() {
		TH.setupTestingEnvironment(false, true);
	}

	/**
	 * Hc_ creation.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void hc_Creation() throws InvalidElementException {
		this.hc = new HistoryCrawler(new State(TH.u1s1Hash));
	}

	/**
	 * Hc_find commit relative.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void hc_findCommitRelative() throws InvalidElementException {
		// System.out.println("\nfindCommit(Step) - Success");
		this.lu = new LogicalUnit("aCard1");
		this.hc = new HistoryCrawler(this.lu.getHead());
		// Find (1,0) From HEAD --> HEAD.
		State foundCommit = this.hc.findCommitRelative(this.lu.getHead(),
				new Step(1, 0), null);
		Assert.assertEquals("1 U1S2Hash Not Found.", TH.u1s2Hash, foundCommit
				.getFingerprint().getHash());
		// System.out.println("S2 <=> "+foundCommit.clonePath().toString());
		// Find (1,1) From HEAD --> CURRENT.
		foundCommit = this.hc.findCommitRelative(this.lu.getHead(), new Step(1,
				1), null);
		Assert.assertEquals("2 U1S1Hash Not Found.", TH.u1s1Hash, foundCommit
				.getFingerprint().getHash());
		Assert.assertEquals("2 Head Hash Not Changed.", TH.u1s2Hash, this.lu
				.getHead().getFingerprint().getHash());
		// System.out.println("S1 <=> "+foundCommit.clonePath().toString());
		// Find (1,0) From Head (Prev Blacklisted) --> Head.
		final ArrayList<String> blackList = new ArrayList<String>();
		blackList.add(TH.u1s1Hash);
		foundCommit = this.hc.findCommitRelative(this.lu.getHead(), new Step(1,
				0), blackList);
		Assert.assertEquals("3 U1S2Hash Not Found.", TH.u1s2Hash, foundCommit
				.getFingerprint().getHash());
		// System.out.println("S2 <=> "+foundCommit.clonePath().toString());
		// Find (1,0) from CURRENT --> CURRENT.
		foundCommit = this.hc.findCommitRelative(this.lu.getCurrent(),
				new Step(1, 0), null);
		Assert.assertEquals("4 U1S1Hash Not Found.", TH.u1s1Hash, foundCommit
				.getFingerprint().getHash());
		// System.out.println("S1 <=> "+foundCommit.clonePath().toString());
		// Find (1,-1) from CURRENT --> HEAD.
		foundCommit = this.hc.findCommitRelative(this.lu.getCurrent(),
				new Step(1, -1), null);
		Assert.assertEquals("5 U1S2Hash Not Found.", TH.u1s2Hash, foundCommit
				.getFingerprint().getHash());
		Assert.assertEquals("5 Current Hash Not Changed.", TH.u1s1Hash, this.lu
				.getCurrent().getFingerprint().getHash());
		Assert.assertFalse(
				"5 Current and Found Hash Not Same.",
				this.lu.getCurrent().getFingerprint().getHash()
						.equals(foundCommit.getFingerprint().getHash()));
		// System.out.println("S2 <=> "+foundCommit.clonePath().toString());
		// Find (1,0) From Current (Head Blacklisted) --> Current.
		blackList.remove(TH.u1s1Hash);
		blackList.add(TH.u1s2Hash);
		foundCommit = this.hc.findCommitRelative(this.lu.getCurrent(),
				new Step(1, 0), blackList);
		Assert.assertEquals("6 U1S1Hash Not Found.", TH.u1s1Hash, foundCommit
				.getFingerprint().getHash());
		// System.out.println("S1 <=> "+foundCommit.clonePath().toString());
		// System.out.println("\n\tTestEnd");
	}

	/**
	 * Hc_find commit step failures.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void hc_findCommitStepFailures() throws InvalidElementException {
		// System.out.print("\nfindStateRelative(Step) - Failure");
		this.lu = new LogicalUnit("aCard1");
		this.hc = this.lu.getHistoryCrawler();
		final ArrayList<String> blackList = new ArrayList<String>();
		// Find (2,1) From Head --> Null.
		State foundCommit = this.hc.findCommitRelative(this.lu.getHead(),
				new Step(2, 1), null);
		Assert.assertNull("1 Head (2,1) Not Null.", foundCommit);
		// Find (1,2) From Head --> Null.
		foundCommit = this.hc.findCommitRelative(this.lu.getHead(), new Step(1,
				2), null);
		Assert.assertNull("2 Head (1,2) Not Null.", foundCommit);
		// Find (1,-1) From Head --> Null.
		foundCommit = this.hc.findCommitRelative(this.lu.getHead(), new Step(1,
				-1), null);
		Assert.assertNull("3 Head (1,-1) Not Null.", foundCommit);
		// Find (1,0) From Head (BlackLised) --> Null.
		blackList.add(TH.u1s2Hash);
		foundCommit = this.hc.findCommitRelative(this.lu.getHead(), new Step(1,
				0), blackList);
		Assert.assertNull("4 Head (1,-1) Not Null.", foundCommit);
		blackList.remove(TH.u1s2Hash);
		// Find (1,1) From Current --> Null.
		foundCommit = this.hc.findCommitRelative(this.lu.getCurrent(),
				new Step(1, 1), null);
		Assert.assertNull("5 Current (1,1) Not Null.", foundCommit);
		// Find (1,-2) From Current --> Null.
		foundCommit = this.hc.findCommitRelative(this.lu.getCurrent(),
				new Step(1, -2), null);
		Assert.assertNull("6 Current (1,-2) Not Null.", foundCommit);
		// Find (1,0) From Current (BlackListed) --> Null.
		blackList.add(TH.u1s1Hash);
		foundCommit = this.hc.findCommitRelative(this.lu.getCurrent(),
				new Step(1, 0), blackList);
		Assert.assertNull("7 Current BlackListed (1,0) Not Null.", foundCommit);
		blackList.remove(TH.u1s1Hash);
		// System.out.println("\n\tTestEnd");
	}

	/**
	 * Hc_find commit path success.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void hc_findCommitPathSuccess() throws InvalidElementException {
		// System.out.println("\nfindCommit(Path) - Success");
		this.lu = new LogicalUnit("aCard1");
		this.hc = this.lu.getHistoryCrawler();
		final ArrayList<String> blackList = new ArrayList<String>();
		// Find (*1+0) From Head --> Head.
		State foundCommit = this.hc.findCommitPath(this.lu.getHead(), new Path(
				"*1+0"), null);
		Assert.assertEquals("1 LU1S2Hash Not found.", TH.u1s2Hash, foundCommit
				.getFingerprint().getHash());
		// System.out.println("S2 <=> "+foundCommit.clonePath().toString());
		// Find (*1+1) From Head --> Current.
		foundCommit = this.hc.findCommitPath(this.lu.getHead(),
				new Path("*1+1"), null);
		Assert.assertEquals("2 LU1S1Hash Not found.", TH.u1s1Hash, foundCommit
				.getFingerprint().getHash());
		// System.out.println("S1 <=> "+foundCommit.clonePath().toString());
		// Find (*1+1*1+-1) From Head --> Head.
		foundCommit = this.hc.findCommitPath(this.lu.getHead(), new Path(
				"*1+1*1+-1"), null);
		Assert.assertEquals("3 LU1S2Hash Not found.", TH.u1s2Hash, foundCommit
				.getFingerprint().getHash());
		// System.out.println("S2 <=> "+foundCommit.clonePath().toString());
		// Find (*1+0) From Head (Current BlackListed) --> Head.
		blackList.add(TH.u1s1Hash);
		foundCommit = this.hc.findCommitPath(this.lu.getHead(),
				new Path("*1+0"), blackList);
		Assert.assertEquals("4 LU1S2Hash Not found.", TH.u1s2Hash, foundCommit
				.getFingerprint().getHash());
		blackList.remove(TH.u1s1Hash);
		// System.out.println("S2 <=> "+foundCommit.clonePath().toString());
		// Find (*1+0) From Current --> Current.
		foundCommit = this.hc.findCommitPath(this.lu.getCurrent(), new Path(
				"*1+0"), null);
		Assert.assertEquals("5 LU1S1Hash Not found.", TH.u1s1Hash, foundCommit
				.getFingerprint().getHash());
		// System.out.println("S1 <=> "+foundCommit.clonePath().toString());
		// Find (*1+-1) From Current --> Head.
		foundCommit = this.hc.findCommitPath(this.lu.getCurrent(), new Path(
				"*1+-1"), null);
		Assert.assertEquals("6 LU1S2Hash Not found.", TH.u1s2Hash, foundCommit
				.getFingerprint().getHash());
		// System.out.println("S2 <=> "+foundCommit.clonePath().toString());
		// Find (*1+-1*1+1) From Current --> Current.
		foundCommit = this.hc.findCommitPath(this.lu.getCurrent(), new Path(
				"*1+-1*1+1"), null);
		Assert.assertEquals("7 LU1S1Hash Not found.", TH.u1s1Hash, foundCommit
				.getFingerprint().getHash());
		// System.out.println("S1 <=> "+foundCommit.clonePath().toString());
		// Find (*1+0) From Current (Head BlackListed) --> Current.
		blackList.add(TH.u1s2Hash);
		foundCommit = this.hc.findCommitPath(this.lu.getCurrent(), new Path(
				"*1+0"), blackList);
		Assert.assertEquals("8 LU1S1Hash Not found.", TH.u1s1Hash, foundCommit
				.getFingerprint().getHash());
		blackList.remove(TH.u1s2Hash);
		// System.out.println("S1 <=> "+foundCommit.clonePath().toString());
		// System.out.println("\n\tTestEnd");
	}

	/**
	 * Hc_find commit path fail.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void hc_findCommitPathFail() throws InvalidElementException {
		// System.out.print("\nfindCommit(Path) - Failure");
		this.lu = new LogicalUnit("aCard1");
		this.hc = this.lu.getHistoryCrawler();
		final ArrayList<String> blackList = new ArrayList<String>();
		// Find (*2+1) From Head --> Null.
		State foundCommit = this.hc.findCommitPath(this.lu.getHead(), new Path(
				"*2+1"), null);
		Assert.assertNull("1 Invalid Path Followed.", foundCommit);
		// Find (*1+2) From Head --> Null.
		foundCommit = this.hc.findCommitPath(this.lu.getHead(),
				new Path("*1+2"), null);
		Assert.assertNull("2 Invalid Path Followed.", foundCommit);
		// Find (*1+-1) From Head --> Null.
		foundCommit = this.hc.findCommitPath(this.lu.getHead(), new Path(
				"*1+-1"), null);
		Assert.assertNull("3 Invalid Path Followed.", foundCommit);
		// Find (*1+2*1-1) From Head --> Null.
		foundCommit = this.hc.findCommitPath(this.lu.getHead(), new Path(
				"*1+2*1+-1"), null);
		Assert.assertNull("4 Invalid Path Followed.", foundCommit);
		// Find (*1+1) From Head (Current BlackListed) --> Null.
		blackList.add(TH.u1s1Hash);
		foundCommit = this.hc.findCommitPath(this.lu.getHead(),
				new Path("*1+1"), blackList);
		Assert.assertNull("5 BlackListed Found.", foundCommit);
		blackList.remove(TH.u1s2Hash);
		// Find (*1+1) From Current --> Null.
		foundCommit = this.hc.findCommitPath(this.lu.getCurrent(), new Path(
				"*1+1"), null);
		Assert.assertNull("6 Invalid Path Followed.", foundCommit);
		// Find (*1+-2) From Current --> Null.
		foundCommit = this.hc.findCommitPath(this.lu.getCurrent(), new Path(
				"*1+-2"), null);
		Assert.assertNull("7 Invalid Path Followed.", foundCommit);
		// Find (*1+-2*1+1) From Current --> Null.
		foundCommit = this.hc.findCommitPath(this.lu.getCurrent(), new Path(
				"*1+-2*1+1"), null);
		Assert.assertNull("8 Invalid Path Followed.", foundCommit);
		// Find (*1+-1) From Current (Head BlackListed) --> Null.
		blackList.add(TH.u1s2Hash);
		foundCommit = this.hc.findCommitPath(this.lu.getCurrent(), new Path(
				"*1+-1"), blackList);
		Assert.assertNull("9 BlackListed Found.", foundCommit);
		blackList.remove(TH.u1s2Hash);
		// System.out.println("\n\tTestEnd");
	}

	/**
	 * Hc_find commit hash success.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void hc_findCommitHashSuccess() throws InvalidElementException {
		// System.out.print("\nfindCommit(Hash) - Success");
		this.lu = new LogicalUnit("aCard1");
		this.hc = this.lu.getHistoryCrawler();
		final ArrayList<String> blackList = new ArrayList<String>();
		// Find S1 From HEAD --> Current
		State foundCommit = this.hc.findCommitHash(this.lu.getHead(),
				TH.u1s1Hash, null, true);
		Assert.assertEquals("1 LU1S1Hash Not Found.", TH.u1s1Hash, foundCommit
				.getFingerprint().getHash());
		// Find S2 From HEAD --> Head
		foundCommit = this.hc.findCommitHash(this.lu.getHead(), TH.u1s2Hash,
				null, false);
		Assert.assertEquals("2 LU1S2Hash Not Found.", TH.u1s2Hash, foundCommit
				.getFingerprint().getHash());
		// Find S2 From Head (Current BlackListed) --> Head
		blackList.add(TH.u1s1Hash);
		foundCommit = this.hc.findCommitHash(this.lu.getHead(), TH.u1s2Hash,
				null, true);
		Assert.assertEquals("3 LU1S2Hash Not Found.", TH.u1s2Hash, foundCommit
				.getFingerprint().getHash());
		blackList.add(TH.u1s1Hash);
		// Find S1 From Current --> Current
		foundCommit = this.hc.findCommitHash(this.lu.getCurrent(), TH.u1s1Hash,
				null, false);
		Assert.assertEquals("4 LU1S1Hash Not Found.", TH.u1s1Hash, foundCommit
				.getFingerprint().getHash());
		// Find S1 From Current (Head BlackListed) --> Current
		blackList.add(TH.u1s2Hash);
		foundCommit = this.hc.findCommitHash(this.lu.getCurrent(), TH.u1s1Hash,
				null, true);
		Assert.assertEquals("5 LU1S1Hash Not Found.", TH.u1s1Hash, foundCommit
				.getFingerprint().getHash());
		blackList.add(TH.u1s2Hash);
		// System.out.println("\n\tTestEnd");
	}

	/**
	 * Hc_find commit hash failure.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void hc_findCommitHashFailure() throws InvalidElementException {
		// System.out.print("\nfindCommit(Hash) - Success");
		this.lu = new LogicalUnit("aCard1");
		this.hc = this.lu.getHistoryCrawler();
		final ArrayList<String> blackList = new ArrayList<String>();
		// Find Unknown Commit From HEAD
		State foundCommit = this.hc.findCommitHash(this.lu.getHead(), "XXXXX",
				null, true);
		Assert.assertNull("1 XXXXX Found.", foundCommit);
		// Find S1 From HEAD (S1 BlackListed) --> Null.
		blackList.add(TH.u1s1Hash);
		foundCommit = this.hc.findCommitHash(this.lu.getHead(), TH.u1s1Hash,
				blackList, false);
		Assert.assertNull("2 S1 Found.", foundCommit);
		blackList.remove(TH.u1s1Hash);
		// Find S1 From HEAD (S2 BlackListed) --> Null.
		blackList.add(TH.u1s2Hash);
		foundCommit = this.hc.findCommitHash(this.lu.getHead(), TH.u1s1Hash,
				blackList, true);
		Assert.assertNull("3 S1 Found.", foundCommit);
		blackList.remove(TH.u1s2Hash);
		// Find S1 From Current (S1 BlackListed) --> Null.
		blackList.add(TH.u1s1Hash);
		foundCommit = this.hc.findCommitHash(this.lu.getCurrent(), TH.u1s1Hash,
				blackList, true);
		Assert.assertNull("4 S1 Found.", foundCommit);
		blackList.add(TH.u1s1Hash);
		// System.out.println("\n\tTestEnd");
	}

	/**
	 * Hc_get history log.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void hc_getHistoryLog() throws InvalidElementException {
		this.lu = new LogicalUnit("aCard1");
		this.hc = this.lu.getHistoryCrawler();
		final State s2 = new State(TH.u1s2Hash);
		final State s1 = new State(TH.u1s1Hash);
		// System.out.println("\nInitialSystemPath:\n"+hc.getHistoryLog(true));
		Assert.assertEquals("1 Incorrect Log.",
				s2.getLogEntry() + "\n\n" + s1.getLogEntry() + "\n",
				this.hc.getHistoryLog(true));
		final State s3 = new State(this.lu.commitValidPath("Scott",
				"Testing Log History."));
		// System.out.println("SystemPath:\n"+hc.getHistoryLog(true));
		Assert.assertEquals(
				"2 Incorrect Log.",
				s3.getLogEntry() + "\n\n" + s2.getLogEntry() + "\n\n"
						+ s1.getLogEntry() + "\n", this.hc.getHistoryLog(true));
		// System.out.println("ValidPath:\n"+hc.getHistoryLog(false));
		Assert.assertEquals("3 Incorrect Log.",
				s3.getLogEntry() + "\n\n" + s1.getLogEntry() + "\n",
				this.hc.getHistoryLog(false));
	}

	/**
	 * Hc_log history null head.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void hc_logHistoryNullHead() throws InvalidElementException {
		this.lu = new LogicalUnit("emptyLogicalUnit");
		this.hc = this.lu.getHistoryCrawler();
		Assert.assertEquals("Incorrect Log History.", "",
				this.hc.getHistoryLog(true));
	}

	/**
	 * Hc_list previous commits.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void hc_listPreviousCommits() throws InvalidElementException {
		this.lu = new LogicalUnit("aCard1");
		this.hc = this.lu.getHistoryCrawler();
		Assert.assertEquals("Incorrect Number of Previous Commits.", this.lu
				.getCurrent().listPrevious().length, this.hc
				.listPreviousCommits(this.lu.getCurrent().getFingerprint()
						.getHash()).length);
		Assert.assertEquals("No Commits prior to Unknown.", 0,
				this.hc.listPreviousCommits("XXXXXX").length);
	}

	/**
	 * Hc_list following commits.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void hc_listFollowingCommits() throws InvalidElementException {
		this.lu = new LogicalUnit("aCard1");
		this.hc = this.lu.getHistoryCrawler();
		Assert.assertEquals("Incorrect Number of Following Commits.", 1,
				this.hc.listFollowingCommits(TH.u1s1Hash).length);
		Assert.assertEquals("No Commits following HEAD.", 0,
				this.hc.listFollowingCommits(TH.u1s2Hash).length);
		Assert.assertEquals("No Commits following Unknown.", 0,
				this.hc.listFollowingCommits("XXXXXX").length);
		final String commitXHash = this.lu.commitValidPath("Scott",
				"List Following Commit.");
		Assert.assertEquals("Should be Two Commits Following S1.", 2,
				this.hc.listFollowingCommits(TH.u1s1Hash).length);
	}

}
