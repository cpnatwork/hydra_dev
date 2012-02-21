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
package org.hydra.ui.commands;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.hydra.TH;
import org.hydra.core.Artifact;
import org.hydra.core.InvalidElementException;
import org.hydra.core.LogicalUnit;
import org.hydra.core.Path;
import org.hydra.core.Stage;
import org.hydra.ui.UIWriter;
import org.hydra.utilities.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * The Class LogicalUnitCommandsTest.
 */
public class LogicalUnitCommandsTest {

	/** The cmd. */
	CommandCLI cmd;

	/** The stage. */
	Stage stage;

	/**
	 * Start class test.
	 * 
	 * @throws FileNotFoundException
	 *             the file not found exception
	 */
	@BeforeClass
	public static void startClassTest() throws FileNotFoundException {
		TH.setupLogging();
		Logger.getInstance().info("LOGICAL UNIT COMMANDS TEST");
		UIWriter.setOutStream(new PrintStream(new FileOutputStream(TH.clioFile)));
	}

	/**
	 * After class.
	 */
	@AfterClass
	public static void afterClass() {
		UIWriter.setOutStream(System.out);
	}

	/**
	 * Setup test.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Before
	public void setupTest() throws InvalidElementException {
		TH.setupTestingEnvironment(true, true);
		this.stage = new Stage();
		TH.setupLogging();
	}

	/**
	 * LOGICALUNIT COMMANDS ***************************************************.
	 */

	@Test
	public void cmd_LUStatus() {
		this.cmd = new LUCmdStatus(this.stage, "aCard1");
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		this.cmd.setVerbosity(1);
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		// Check Accepts
		Assert.assertTrue("Doesn't Accept 'lustatus'.",
				this.cmd.accepts("lustatus"));
		Assert.assertTrue("Doesn't Accept 'lustatus aCard1'.",
				this.cmd.accepts("lustatus aCard1"));
		// Check Rejects
		Assert.assertFalse("Accepts 'other'.", this.cmd.accepts("other"));
		Assert.assertFalse("Accepts 'other second'.",
				this.cmd.accepts("other second"));
		// Check Exit & Usage
		Assert.assertFalse("Exits.", this.cmd.exits());
		Assert.assertTrue("Usage.",
				this.cmd.getUsage().startsWith("luStatus(lus) {<luName>}"));
	}

	/**
	 * Cmd_ lu element add execution.
	 */
	@Test
	public void cmd_LUElementAddExecution() {
		this.cmd = new LUCmdElementAdd(this.stage, "aCard1", "c2File.txt");
		this.cmd.setVerbosity(1);
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		Assert.assertFalse("Twice Should Not Succeed.", this.cmd.execute());
		this.cmd.setVerbosity(0);
		Assert.assertFalse("Twice Should Not Succeed.", this.cmd.execute());
	}

	/**
	 * Cmd_ lu element add.
	 */
	@Test
	public void cmd_LUElementAdd() {
		this.cmd = new LUCmdElementAdd(this.stage);
		// Check Accept
		Assert.assertTrue("Doesn't Accept 'luAdd aCard1 -e c2File.txt'.",
				this.cmd.accepts("luAdd aCard1 -e c2File.txt"));
		Assert.assertTrue("Accepts 'luAdd -e aCard1'.",
				this.cmd.accepts("luAdd -e aCard1"));
		Assert.assertTrue("Doesn't Accept 'luAdd aCard1 -er w1Dir'.",
				this.cmd.accepts("luAdd aCard1 -er w1Dir"));
		Assert.assertTrue("Doesn't Accept 'luAdd -er w1Dir/'.",
				this.cmd.accepts("luAdd -er w1Dir/"));
		Assert.assertTrue("Doesn't Accept 'luAdd -er w1Dir/c1File.txt'.",
				this.cmd.accepts("luAdd -er w1Dir/c1File.txt"));
		Assert.assertTrue("Doesn't Accept 'luAdd -er w1Dir'.",
				this.cmd.accepts("luAdd -er w1Dir"));
		Assert.assertTrue("Doesn't Accept 'luAdd -re w1Dir'.",
				this.cmd.accepts("luAdd -re w1Dir"));
		Assert.assertTrue("Doesn't Accept 'luAdd a b c -er d e f'.",
				this.cmd.accepts("luAdd a b c -er d e f"));
		Assert.assertTrue("Doesn't Accept 'luAdd -e .'.",
				this.cmd.accepts("luAdd -e ."));
		Assert.assertTrue("Doesn't Accept 'luAdd -e ..'.",
				this.cmd.accepts("luAdd -e .."));
		// Check Reject
		Assert.assertFalse("Accepts 'other'.", this.cmd.accepts("other"));
		Assert.assertFalse("Accepts 'luAdd -r w1Dir'.",
				this.cmd.accepts("luAdd -r w1Dir"));
		// Check Exit & Usage
		Assert.assertFalse("Exits.", this.cmd.exits());
		Assert.assertTrue("Usage.",
				this.cmd.getUsage()
						.startsWith("luAdd {<luName>} -e{r} <eName>"));
	}

	/**
	 * Cmd_ lu element remove execution.
	 */
	@Test
	public void cmd_LUElementRemoveExecution() {
		this.cmd = new LUCmdElementRemove(this.stage, "aCard1", "c1File.txt");
		this.cmd.setVerbosity(1);
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		Assert.assertFalse("Twice Should Not Succeed.", this.cmd.execute());
		this.cmd.setVerbosity(0);
		Assert.assertFalse("Twice Should Not Succeed.", this.cmd.execute());
		Assert.assertFalse("Should Not Succeed.", this.cmd.execute());
	}

	/**
	 * Cmd_ lu element remove.
	 */
	@Test
	public void cmd_LUElementRemove() {
		this.cmd = new LUCmdElementRemove(this.stage);
		// Check Accept
		Assert.assertTrue("Doesn't Accept 'luRemove aCard1 -e c2File.txt'.",
				this.cmd.accepts("luRemove aCard1 -e c2File.txt"));
		Assert.assertTrue("Doesn't Accept 'luRemove -e c2File.txt'.",
				this.cmd.accepts("luRemove -e c2File.txt"));
		Assert.assertTrue("Doesn't Accept 'luRemove -e w1Dir/'.",
				this.cmd.accepts("luRemove -e w1Dir/"));
		Assert.assertTrue("Doesn't Accept 'luRemove -e aCard1'.",
				this.cmd.accepts("luRemove -e aCard1"));
		Assert.assertTrue("Doesn't Accept 'luRemove a b c -e d e f'.",
				this.cmd.accepts("luRemove a b c -e d e f"));
		Assert.assertTrue("Doesn't Accept 'luRemove -e .'.",
				this.cmd.accepts("luRemove -e ."));
		Assert.assertTrue("Doesn't Accept 'luRemove -e ..'.",
				this.cmd.accepts("luRemove -e .."));
		// Check Reject
		Assert.assertFalse("Accepts 'luRemove -e'.",
				this.cmd.accepts("luRemove -e"));
		Assert.assertFalse("Accepts 'luRemove'.", this.cmd.accepts("luRemove"));
		Assert.assertFalse("Accepts 'other'.", this.cmd.accepts("other"));
		// Check Exit & Usage
		Assert.assertFalse("Exits.", this.cmd.exits());
		Assert.assertTrue("Usage.",
				this.cmd.getUsage()
						.startsWith("luRemove {<luName>} -e <eName>"));
	}

	/**
	 * Cmd_ lu stash execution.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void cmd_LUStashExecution() throws InvalidElementException {
		this.cmd = new LUCmdStash(this.stage, "aCard1");
		final LogicalUnit lu1 = this.stage.getLogicalUnit("aCard1");
		LogicalUnit lu2 = new LogicalUnit("aCard1");
		Assert.assertEquals("1 Descriptions Are Same.", lu1.describe(),
				lu2.describe());
		// Add Element
		Assert.assertTrue("Artifact Should be Added.", lu1.getContents()
				.addElement(new Artifact(TH.w2File)));
		lu2 = new LogicalUnit("aCard1");
		Assert.assertFalse("2 Descriptions Are Different.", lu1.describe()
				.equals(lu2.describe()));
		// Content Hash is Not in Reference File.
		Assert.assertTrue("Stash Should Succeed.", this.cmd.execute());
		lu2 = new LogicalUnit("aCard1");
		Assert.assertEquals("3 Descriptions Are Same.", lu1.describe(),
				lu2.describe());
	}

	/**
	 * Cmd_ lu stash.
	 */
	@Test
	public void cmd_LUStash() {
		this.cmd = new LUCmdStash(this.stage);
		// Check Accepts
		Assert.assertTrue("Doesn't Accept 'lustash aCard1'.",
				this.cmd.accepts("lustash aCard1"));
		Assert.assertTrue("Doesn't Accept ' lustash   aCard1  '.",
				this.cmd.accepts("  lustash  aCard1  "));
		Assert.assertTrue("Doesn't Accept 'lustash'.",
				this.cmd.accepts("lustash"));
		// Check Rejects
		Assert.assertFalse("Accepts 'lus aCard1'.",
				this.cmd.accepts("lus aCard1"));
		// Check Exits & Usage
		Assert.assertFalse("Exits.", this.cmd.exits());
		Assert.assertTrue("Usage.",
				this.cmd.getUsage().startsWith("luStash {<luName>}"));
	}

	/**
	 * Cmd_ lu commit.
	 */
	@Test
	public void cmd_LUCommit() {
		this.cmd = new LUCmdCommit(this.stage, "aCard1", "Commit Command Test.");
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		this.cmd.setVerbosity(1);
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		// Check Accepts
		Assert.assertTrue(
				"Doesn't Accept 'lucommit aCard1 -m Commit Command Test.",
				this.cmd.accepts("lucommit aCard1 -m Commit Command Test"));
		Assert.assertTrue("Doesn't Accepts 'lucommit -m My Next Message.",
				this.cmd.accepts("lucommit -m My Next Message."));
		// Check Rejects
		Assert.assertFalse("Accepts 'lucommit aCard1 Unmarked Message.",
				this.cmd.accepts("lucommit aCard1 Unmarked Message"));
		Assert.assertFalse("Accepts 'other'.", this.cmd.accepts("other"));
		// Check Exits & Usage
		Assert.assertFalse("Exits.", this.cmd.exits());
		Assert.assertTrue(
				"Usage.",
				this.cmd.getUsage().startsWith(
						"luCommit {<luName>} -m <message>"));
	}

	/**
	 * Cmd_ lu revert relative.
	 */
	@Test
	public void cmd_LURevertRelative() {
		this.cmd = new LUCmdRevert(this.stage, "aCard1", 0, 0);
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		this.cmd.setVerbosity(1);
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		// Check Accepts
		Assert.assertTrue("Doesn't Accept 'lurevert aCard1 -relative 1 1.",
				this.cmd.accepts("lurevert aCard1 -relative 1 1"));
		Assert.assertTrue("Doesn't Accept 'lurevert aCard1 -r 1 1.",
				this.cmd.accepts("lurevert aCard1 -r 1 1"));
		Assert.assertTrue("Doesn't Accept 'lurevert -r 1 1.",
				this.cmd.accepts("lurevert -r 1 1"));
		Assert.assertTrue("Doesn't Accepts 'luRevert aCard1 -relative -1 1'.",
				this.cmd.accepts("lurevert aCard1 -relative -1 1"));
		Assert.assertTrue("Doesn't Accepts 'luRevert aCard1 -relative 1 -1'.",
				this.cmd.accepts("lurevert aCard1 -relative 1 -1"));
		Assert.assertTrue("Doesn't Accepts 'luRevert -r -1 -1'.",
				this.cmd.accepts("lurevert -r -1 -1"));
		// Check Rejects.
		Assert.assertFalse("Accepts 'lurevert aCard1 -r 1.",
				this.cmd.accepts("lurevert aCard1 -r 1"));
		Assert.assertFalse("Accepts 'other'.", this.cmd.accepts("other"));
		Assert.assertFalse("Accepts 'other two three four'.",
				this.cmd.accepts("other two three four"));
		// Checks Exits & Usage.
		Assert.assertFalse("Exits.", this.cmd.exits());
		Assert.assertTrue("Incorrect Usage.",
				this.cmd.getUsage().startsWith("luRevert {<luName>} -h <hash>"));
	}

	/**
	 * Cmd_ lu revert hash.
	 */
	@Test
	public void cmd_LURevertHash() {
		this.cmd = new LUCmdRevert(this.stage, "aCard1", TH.u1s2Hash);
		Assert.assertTrue("Hash Should Succeed.", this.cmd.execute());
		this.cmd.setVerbosity(1);
		Assert.assertTrue("Hash Should Succeed.", this.cmd.execute());
		// Check Accepts
		Assert.assertTrue("Doesn't Accept 'lurevert aCard1 -hash "
				+ TH.u1s2Hash,
				this.cmd.accepts("lurevert aCard1 -hash " + TH.u1s2Hash));
		Assert.assertTrue("Doesn't Accept 'lurevert aCard1 -h " + TH.u1s2Hash,
				this.cmd.accepts("lurevert aCard1 -h " + TH.u1s2Hash));
		// Check Rejects
		Assert.assertFalse("Accepts 'lurevert aCard1 -h 1 1.",
				this.cmd.accepts("lurevert aCard1 -h 1 1"));
		Assert.assertFalse("Accepts 'lurevert -HASH XAS12342341",
				this.cmd.accepts("lurevert -HASH XAS12342341"));
		Assert.assertFalse("Accepts 'lurevert -H " + TH.u1s2Hash
				+ "1 << 41 Chars",
				this.cmd.accepts("lurevert -H " + TH.u1s2Hash + "1"));
		Assert.assertFalse("Hash Accepts 'other'.", this.cmd.accepts("other"));
	}

	/**
	 * Cmd_ lu revert path.
	 */
	@Test
	public void cmd_LURevertPath() {
		this.cmd = new LUCmdRevert(this.stage, "aCard1", new Path("*1+0"));
		Assert.assertTrue("Path Should Succeed.", this.cmd.execute());
		// Check Accepts
		Assert.assertTrue("Doesn't Accept 'lurevert -p *1+0",
				this.cmd.accepts("lurevert -p *1+0"));
		Assert.assertTrue("Doesn't Accept 'lurevert -p *-1+0",
				this.cmd.accepts("lurevert -p *-1+0"));
		Assert.assertTrue("Doesn't Accept 'lurevert -p *1+-0",
				this.cmd.accepts("lurevert -p *1+-0"));
		Assert.assertTrue("Doesn't Accept 'lurevert -p *-1+-0",
				this.cmd.accepts("lurevert -p *-1+-0"));
		Assert.assertTrue("Doesn't Accept 'lurevert -path *1+0*2+-3",
				this.cmd.accepts("lurevert -path *1+0*2+-3"));
		// Check Rejects
		Assert.assertFalse("Accepts 'lurevert -patj *1+0*2+-3",
				this.cmd.accepts("lurevert -patj *1+0*2+-3"));
		Assert.assertFalse("Accepts 'lurevert *1+0",
				this.cmd.accepts("lurevert *1+0"));
	}

	/**
	 * Cmd_ lu reset.
	 */
	@Test
	public void cmd_LUReset() {
		this.cmd = new LUCmdReset(this.stage, "aCard1", "-HEAD");
		Assert.assertTrue("Head Should Succeed.", this.cmd.execute());
		this.cmd = new LUCmdReset(this.stage, "aCard1", "-CURRENT");
		this.cmd.setVerbosity(1);
		Assert.assertTrue("Current Should Succeed.", this.cmd.execute());
		this.cmd = new LUCmdReset(this.stage, "aCard1", "unknownTarget");
		this.cmd.setVerbosity(1);
		Assert.assertFalse("Unknown Should Fail.", this.cmd.execute());
		this.cmd = new LUCmdReset(this.stage, "unknownLU", "-C");
		Assert.assertFalse("Unknown LU should Fail.", this.cmd.execute());
		// Check Accepts
		Assert.assertTrue("Doesn't Accept 'luresert aCard1'.",
				this.cmd.accepts("lureset aCard1"));
		Assert.assertTrue("Doesn't Accept 'luresert aCard1 -C'.",
				this.cmd.accepts("lureset aCard1 -C"));
		Assert.assertTrue("Doesn't Accept 'luresert aCard1 -h'.",
				this.cmd.accepts("lureset aCard1 -h"));
		Assert.assertTrue("Doesn't Accept 'lureset'.",
				this.cmd.accepts("lureset"));
		Assert.assertTrue("Doesn't Accept 'lureset -C'.",
				this.cmd.accepts("lureset -C"));
		Assert.assertTrue("Doesn't Accept 'lureset -h'.",
				this.cmd.accepts("lureset -h"));
		Assert.assertTrue("Doesn't Accept 'lureset -head'.",
				this.cmd.accepts("lureset -head"));
		Assert.assertTrue("Doesn't Accept 'lureset -CURRENT'.",
				this.cmd.accepts("lureset -CURRENT"));
		Assert.assertTrue("Doesn't Accept 'lureset -a -b -d -CURRENT'.",
				this.cmd.accepts("lureset -a -b -d -CURRENT"));
		Assert.assertTrue("Doesn't Accept 'lureset two three four'.",
				this.cmd.accepts("lureset two three four"));
		// Check Reject
		Assert.assertFalse("Doesn't Accept 'other two'.",
				this.cmd.accepts("other two"));
		// Check Exit and Usage.
		Assert.assertFalse("Exits.", this.cmd.exits());
		Assert.assertTrue("Usage.",
				this.cmd.getUsage().startsWith("luReset {<luName>} {-C|-H}"));
	}

	/**
	 * Cmd_ lu log.
	 */
	@Test
	public void cmd_LULog() {
		this.cmd = new LUCmdLog(this.stage, "aCard1", true);
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		this.cmd.setVerbosity(1);
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		// Check Acceptable
		Assert.assertTrue("Doesn't Accept 'lulog aCard1'.",
				this.cmd.accepts("lulog aCard1"));
		Assert.assertTrue("Doesn't Accept 'lulog aCard1 -s'.",
				this.cmd.accepts("lulog aCard1 -s"));
		Assert.assertTrue("Doesn't Accept 'lulog aCard1 -V'.",
				this.cmd.accepts("lulog aCard1 -V"));
		Assert.assertTrue("Doesn't Accept 'lulog a-b-c -V'.",
				this.cmd.accepts("lulog a-b-c -V"));
		Assert.assertTrue("Doesn't Accept 'lulog a b c -V'.",
				this.cmd.accepts("lulog a b c -V"));
		Assert.assertTrue("Doesn't Accept 'lulog -a -b -c -V'.",
				this.cmd.accepts("lulog -a -b -c -V"));
		Assert.assertTrue("Doesn't Accept 'lulog'.", this.cmd.accepts("lulog"));
		// Check Reject
		Assert.assertFalse("Accepts 'other'.", this.cmd.accepts("other"));
		Assert.assertFalse("Accepts 'other two'.",
				this.cmd.accepts("other two"));
		// Check Exit and Usage
		Assert.assertFalse("Exits.", this.cmd.exits());
		Assert.assertTrue("Usage.",
				this.cmd.getUsage().startsWith("luLog {<luName>}"));
	}

	/**
	 * Cmd_ lu diff.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void cmd_LUDiff() throws InvalidElementException {
		// Setup Differential Calculation.
		final LogicalUnit lu = this.stage.getLogicalUnit("aCard1");
		lu.getContents().addElement(new Artifact(TH.w1File));
		lu.commit("Scott", "Differential Commit.");
		TH.writeFile(TH.w1File,
				"Line 1 Content.\nLine 2 Content.\n\nLine 4 Content.");
		this.cmd = new LUCmdDiff(this.stage, "aCard1", "c1File.txt");
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		this.cmd = new LUCmdDiff(this.stage);
		Assert.assertFalse("Should Not Succeed.", this.cmd.execute());
		// Check Acceptable
		Assert.assertTrue("Doesn't Accept 'ludiff aCard -e c1File.txt'.",
				this.cmd.accepts("ludiff aCard -e c1File.txt"));
		Assert.assertTrue("Doesn't Accept 'ludiff -e c1File.txt'.",
				this.cmd.accepts("ludiff -e c1File.txt"));
		Assert.assertTrue("Doesn't Accept 'ludiff -e a b c'.",
				this.cmd.accepts("ludiff -e a b c"));
		Assert.assertTrue("Doesn't Accept 'ludiff x y z -e a b c'.",
				this.cmd.accepts("ludiff x y z -e a b c"));
		Assert.assertTrue("Doesn't Accept 'ludiff x y z -e a/b/c/'.",
				this.cmd.accepts("ludiff x y z -e a/b/c/"));
		// Check Reject
		Assert.assertFalse("Accepts 'ludiff -er c1File.txt'.",
				this.cmd.accepts("ludiff -er c1File.txt"));
		Assert.assertTrue("Should Contain Usage.", this.cmd
				.getProcessingFailure().contains(this.cmd.getUsage()));
		// assertFalse("Accepts 'ludiff abc -e   /'.",
		// cmd.accepts("ludiff abc -e    /"));
		Assert.assertFalse("Accepts 'ludiff -e   '.",
				this.cmd.accepts("ludiff -e    "));

		// Check Exit and Usage
		Assert.assertFalse("Exits.", this.cmd.exits());
		Assert.assertTrue("Usage.",
				this.cmd.getUsage().startsWith("luDiff {<luName>} -e <eName>"));
	}

	/**************************************************************************
	 * EXCEPTIONAL CASES. 1). NonExistent Logical Units. 2). Double
	 * LUCreate/Delete and ElementAdd/Remove (see above). 3). Null HEAD, CURRENT
	 * and CONTENTS when new logical unit.
	 */

	@Test
	public void cmd_LUStatusNonExistent() {
		this.cmd = new LUCmdStatus(this.stage, "nonExistent");
		this.cmd.setVerbosity(0);
		Assert.assertFalse("LUCmdStatus Should Fail.", this.cmd.execute());
	}

	/**
	 * Cmd_ commit non existent lu.
	 */
	@Test
	public void cmd_CommitNonExistentLU() {
		this.cmd = new LUCmdCommit(this.stage, "NonExistent",
				"Commit Command Test Non-Existent.");
		this.cmd.setVerbosity(0);
		Assert.assertFalse("CmdCommit Should Fail.", this.cmd.execute());
	}

	/**
	 * Cmd_ revert non existent hash.
	 */
	@Test
	public void cmd_RevertNonExistentHash() {
		this.cmd = new LUCmdRevert(this.stage, "aCard1",
				";alskdflskadflsadkjfsk");
		this.cmd.setVerbosity(1);
		Assert.assertFalse("LUCmdRevert Should Fail - Invalid Hash.",
				this.cmd.execute());
	}

	/**
	 * Cmd_ revert non existent too far.
	 */
	@Test
	public void cmd_RevertNonExistentTooFar() {
		this.cmd = new LUCmdRevert(this.stage, "aCard1", 0, 10);
		this.cmd.setVerbosity(1);
		Assert.assertFalse("LUCmdRevert Should Fail - Invalid RelPos.",
				this.cmd.execute());
	}

	/**
	 * Cmd_ log invalid lu.
	 */
	@Test
	public void cmd_LogInvalidLU() {
		this.cmd = new LUCmdLog(this.stage, "NonExistent", false);
		this.cmd.setVerbosity(1);
		Assert.assertFalse("LUCmdLog Should Fail - Invalid LU.",
				this.cmd.execute());
	}

	/**
	 * Cmd_ element add non existent lu.
	 */
	@Test
	public void cmd_ElementAddNonExistentLU() {
		this.cmd = new LUCmdElementAdd(this.stage, "NonExistentLU",
				"c2File.txt");
		Assert.assertFalse("CmdElementAdd Should Fail - Invalid LU & File.",
				this.cmd.execute());
	}

	/**
	 * Cmd_ element add non existent file.
	 */
	@Test
	public void cmd_ElementAddNonExistentFile() {
		this.cmd = new LUCmdElementAdd(this.stage, "aCard1", "NonFile");
		Assert.assertFalse("CmdElementAdd Should Fail - Invalid LU & File.",
				this.cmd.execute());
	}

	/**
	 * Cmd_ element remove non existent lu.
	 */
	@Test
	public void cmd_ElementRemoveNonExistentLU() {
		this.cmd = new LUCmdElementRemove(this.stage, "NonExistentLU",
				"c1File.txt");
		Assert.assertFalse("CMDElementRemove Should Succeed.",
				this.cmd.execute());
	}

	/**
	 * Cmd_ element remove non added element.
	 */
	@Test
	public void cmd_ElementRemoveNonAddedElement() {
		this.cmd = new LUCmdElementRemove(this.stage, "aCard1", "NonFile");
		Assert.assertFalse("CMDElementRemove Should Succeed.",
				this.cmd.execute());
	}

	/**
	 * Cmd_ elment remove missing element.
	 */
	@Test
	public void cmd_ElmentRemoveMissingElement() {
		this.cmd = new LUCmdElementRemove(this.stage, "aCard1", "c1File.txt");
		TH.deleteFile(TH.w1File);
		Assert.assertTrue("Remove Missing Should Succeed.", this.cmd.execute());
	}

	/**
	 * Cmd_ deep element ms path.
	 */
	@Test
	public void cmd_DeepElementMSPath() {
		this.cmd = new LUCmdElementAdd(this.stage, "aCard1",
				"w1Dir\\c1File.txt");
		Assert.assertTrue("Deep Element Should be Added.", this.cmd.execute());
		Assert.assertEquals("Should be Three Elements in Contents.", 3,
				this.stage.getLogicalUnit("aCard1").getContents()
						.countElements());
		this.cmd = new LUCmdElementRemove(this.stage, "aCard1",
				"w1Dir\\c1File.txt");
		Assert.assertTrue("Deep Element Should be Removed.", this.cmd.execute());
		Assert.assertEquals("Should be Two Elements in Contents.", 2,
				this.stage.getLogicalUnit("aCard1").getContents()
						.countElements());
	}

	/**
	 * Cmd_ recursive container add remove.
	 */
	@Test
	public void cmd_RecursiveContainerAddRemove() {
		// Add Container Recursively
		this.cmd = new LUCmdElementAdd(this.stage, "aCard1", "w1Dir", true);
		Assert.assertTrue("Container should be added recursively.",
				this.cmd.execute());
		Assert.assertEquals("Should be Three Elements in Contents.", 3,
				this.stage.getLogicalUnit("aCard1").getContents()
						.countElements());
		// Remove Container Recursively - Not Relevant.
		this.cmd = new LUCmdElementRemove(this.stage, "aCard1", "w1Dir");
		Assert.assertTrue("Container Should be Remove Recursively.",
				this.cmd.execute());
		Assert.assertEquals("Should be One Elements in Contents.", 1,
				this.stage.getLogicalUnit("aCard1").getContents()
						.countElements());
	}

	/**
	 * Cmd_ null headcurrent.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void cmd_NullHEADCURRENT() throws InvalidElementException {
		final Stage stage = new Stage();
		TH.setupLogging();
		stage.createLogicalUnit("lux");
		CommandCLI cmd = new LUCmdStatus(stage, "lux");
		Assert.assertTrue("CMDLUStatus Succeeds.", cmd.execute());
		cmd = new LUCmdRevert(stage, "lux", 1, 0);
		Assert.assertFalse("CMDLURevert Fails.", cmd.execute());
	}

}
