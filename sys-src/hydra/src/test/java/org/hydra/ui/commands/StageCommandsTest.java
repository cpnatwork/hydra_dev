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
import org.hydra.core.Container;
import org.hydra.core.InvalidElementException;
import org.hydra.core.Stage;
import org.hydra.ui.UIWriter;
import org.hydra.utilities.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * The Class StageCommandsTest.
 */
public class StageCommandsTest {

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
		Logger.getInstance().info("COMMANDS TEST");
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
	 * STAGE COMMANDS *********************************************************.
	 */

	@Test
	public void cmd_SCreateLU() {
		this.cmd = new SCmdCreateLU(this.stage, "luX");
		this.cmd.setVerbosity(1);
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		this.cmd.setVerbosity(0);
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		// Check Acceptable
		Assert.assertTrue("Doesn't Accept 'screate luX'.",
				this.cmd.accepts("screate luX"));
		Assert.assertTrue("Doesn't Accept 'screate lu master'.",
				this.cmd.accepts("screate lu master"));
		// Check Not Acceptable
		Assert.assertFalse("Accepts 'screate '.", this.cmd.accepts("screate "));
		Assert.assertFalse("Accepts 'screateX'.", this.cmd.accepts("screateX"));
		Assert.assertFalse("Accepts 'other'.", this.cmd.accepts("other"));
		// Check Exit & Usage
		Assert.assertFalse("Exits.", this.cmd.exits());
		Assert.assertTrue("Usage.",
				this.cmd.getUsage().startsWith("sCreate <luName>"));
	}

	/**
	 * Cmd_ s delete lu.
	 */
	@Test
	public void cmd_SDeleteLU() {
		this.cmd = new SCmdDeleteLU(this.stage, "aCard1");
		this.cmd.setVerbosity(1);
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		Assert.assertFalse("Should Fail.", this.cmd.execute());
		this.cmd.setVerbosity(0);
		Assert.assertFalse("Should Fail.", this.cmd.execute());
		// Check Acceptable
		Assert.assertTrue("Doesn't Accept 'sdelete aCard1'.",
				this.cmd.accepts("sdelete aCard1"));
		// Check Not Acceptable
		Assert.assertFalse("Accepts 'other'.", this.cmd.accepts("other"));
		// Check Exit & Usage
		Assert.assertFalse("Exits.", this.cmd.exits());
		Assert.assertTrue("Usage.",
				this.cmd.getUsage().startsWith("sDelete <luName>"));
	}

	/**
	 * Cmd_ s focus lu.
	 */
	@Test
	public void cmd_SFocusLU() {
		this.cmd = new SCmdFocusLU(this.stage, "aCard1");
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		this.cmd = new SCmdFocusLU(this.stage, "aCard1");
		this.cmd.setVerbosity(1);
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		this.cmd = new SCmdFocusLU(this.stage, "lux");
		this.cmd.setVerbosity(1);
		Assert.assertFalse("Should Fail.", this.cmd.execute());
		// Check Acceptance
		Assert.assertTrue("Should Accept 'sfocus aCard1'.",
				this.cmd.accepts("sfocus aCard1"));
		Assert.assertTrue("Should Accept 'sfocus      a b c'.",
				this.cmd.accepts("  sfocus     a b c"));
		// Check Rejection
		Assert.assertFalse("Doesn't Accept 'sfocus'.",
				this.cmd.accepts("sfocus"));
		Assert.assertFalse("Doesn't Accept 'sfocusX'.",
				this.cmd.accepts("sfocusX"));
		Assert.assertFalse("Doesn't Accept 'lufother'.",
				this.cmd.accepts("lufother"));
		// Check Exit & Usage.
		Assert.assertFalse("Exits.", this.cmd.exits());
		Assert.assertTrue("Usage.",
				this.cmd.getUsage().startsWith("sFocus <luName>"));
	}

	/**
	 * Cmd_ s ignore lu.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void cmd_SIgnoreLU() throws InvalidElementException {
		final Stage stage = new Stage();
		this.cmd = new SCmdIgnoreLU(stage, "aCard1");
		final int preCount = stage.listManaged().length;
		this.cmd.setVerbosity(1);
		Assert.assertTrue("Execution Should Succeed.", this.cmd.execute());
		Assert.assertEquals("Should be One Less.", preCount - 1,
				stage.listManaged().length);
		this.cmd.setVerbosity(1);
		Assert.assertFalse("Execution Should Fail.", this.cmd.execute());
		this.cmd.setVerbosity(0);
		Assert.assertFalse("Execution Should Fail.", this.cmd.execute());
		// Check Acceptance.
		this.cmd = new SCmdIgnoreLU(stage);
		Assert.assertTrue("Doesn't Accept 'signore aCard1'.",
				this.cmd.accepts("signore aCard1"));
		Assert.assertTrue("Doesn't Accept 'SIGNORE a b c d   '.",
				this.cmd.accepts("SIGNORE a b c d   "));
		// Check Rejection.
		Assert.assertFalse("Accepts 'signorelu '.",
				this.cmd.accepts("signore "));
		Assert.assertFalse("Accepts 'other'.", this.cmd.accepts("other"));
		// Check Exit & Usage
		Assert.assertFalse("Exits.", this.cmd.exits());
		Assert.assertTrue("Usage Incorrect.",
				this.cmd.getUsage().startsWith("sIgnore <luName>"));
	}

	/**
	 * Cmd_ s manage lu.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void cmd_SManageLU() throws InvalidElementException {
		final Stage stage = new Stage();
		this.cmd = new SCmdManageLU(stage, "aCard1");
		stage.ignore("aCard1");
		final int preCount = stage.listManaged().length;
		Assert.assertTrue("Execution Should Succeed.", this.cmd.execute());
		Assert.assertEquals("Should be One More.", preCount + 1,
				stage.listManaged().length);
		this.cmd.setVerbosity(1);
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		// Check Acceptance.
		this.cmd = new SCmdManageLU(stage);
		Assert.assertTrue("Doesn't Accept 'sManage aCard1'.",
				this.cmd.accepts("sManage aCard1"));
		Assert.assertTrue("Doesn't Accept ' smanage aCard1'.",
				this.cmd.accepts("smanage aCard1"));
		Assert.assertTrue("Doesn't Accept 'SMANAGE a b c d   '.",
				this.cmd.accepts("SMANAGE a b c d   "));
		// Check Rejection.
		Assert.assertFalse("Accepts 'sManage'.", this.cmd.accepts("sManage"));
		Assert.assertFalse("Accepts 'smanage '.", this.cmd.accepts("smanage "));
		Assert.assertFalse("Accepts 'other'.", this.cmd.accepts("other"));
		// Check Exit & Usage
		Assert.assertFalse("Exits.", this.cmd.exits());
		Assert.assertTrue("Usage Incorrect.",
				this.cmd.getUsage().startsWith("sManage <luName>"));
	}

	/**
	 * Cmd_ s status.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void cmd_SStatus() throws InvalidElementException {
		final Stage stage = new Stage();
		stage.createLogicalUnit("lux");
		stage.createLogicalUnit("luy");
		stage.ignore("luy");
		this.cmd = new SCmdStatus(stage);
		this.cmd.setVerbosity(0);
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		this.cmd.setVerbosity(1);
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		// Full Coverage
		stage.commit("Scott", "Test Commit.");
		stage.getContents().addElement(new Artifact(TH.w1File));
		stage.getContents().addElement(new Container(TH.w1Dir));
		stage.setFocus(stage.getLogicalUnit("lux"));
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		// Check Acceptance
		Assert.assertTrue("Doesn't Accept 'sstatus'.",
				this.cmd.accepts("sstatus"));
		Assert.assertTrue("Doesn't Accept ' SS '.", this.cmd.accepts(" SS "));
		// Check Reject
		Assert.assertFalse("Accepts 'status'.", this.cmd.accepts("status"));
		Assert.assertFalse("Accepts 'sstatus x'.",
				this.cmd.accepts("sstatus x"));
		// Check Exit & Usage
		Assert.assertFalse("Exits.", this.cmd.exits());
		Assert.assertTrue("Usage Incorrect.",
				this.cmd.getUsage().startsWith("sStatus(ss)"));

	}

	/**
	 * Cmd_ s element add execute.
	 */
	@Test
	public void cmd_SElementAddExecute() {
		this.cmd = new SCmdElementAdd(this.stage, "c2File.txt");
		this.cmd.setVerbosity(1);
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		this.cmd.setVerbosity(1);
		Assert.assertFalse("Twice Should Not Succeed.", this.cmd.execute());
		// Try Container
		this.cmd = new SCmdElementAdd(this.stage, "w1Dir");
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		this.cmd = new SCmdElementAdd(this.stage, "w1Dir", true);
		Assert.assertFalse("Should Not Succeed Twice.", this.cmd.execute());
	}

	/**
	 * Cmd_ s element add.
	 */
	@Test
	public void cmd_SElementAdd() {
		this.cmd = new SCmdElementAdd(this.stage);
		// Check Accept
		Assert.assertTrue("Doesn't Accept 'sAdd -e c2File.txt'.",
				this.cmd.accepts("sAdd -e c2File.txt"));
		Assert.assertTrue("Doesn't Accept 'sAdd -e c3File.txt'.",
				this.cmd.accepts("sAdd -e c3File.txt"));
		Assert.assertTrue("Accepts 'sAdd -e aCard1'.",
				this.cmd.accepts("sAdd -e aCard1"));
		Assert.assertTrue("Doesn't Accept 'sAdd -er w1Dir'.",
				this.cmd.accepts("sAdd -er w1Dir"));
		Assert.assertTrue("Doesn't Accept 'sAdd -er w1Dir/'.",
				this.cmd.accepts("sAdd -er w1Dir/"));
		Assert.assertTrue("Doesn't Accept 'sAdd -re w1Dir'.",
				this.cmd.accepts("sAdd -re w1Dir"));
		Assert.assertTrue("Doesn't Accept 'sAdd -er d e f'.",
				this.cmd.accepts("sAdd -er d e f"));
		Assert.assertTrue("Doesn't Accept 'sAdd -e .'.",
				this.cmd.accepts("sAdd -e ."));
		Assert.assertTrue("Doesn't Accept 'sAdd -e ..'.",
				this.cmd.accepts("sAdd -e .."));
		// Check Reject
		Assert.assertFalse("Accepts 'other'.", this.cmd.accepts("other"));
		Assert.assertFalse("Accepts 'sAdd -r w1Dir'.",
				this.cmd.accepts("sAdd -r w1Dir"));
		// Check Exit & Usage
		Assert.assertFalse("Exits.", this.cmd.exits());
		Assert.assertTrue("Usage.",
				this.cmd.getUsage().startsWith("sAdd -e{r} <eName>"));
	}

	/**
	 * Cmd_ s element remove execute.
	 */
	@Test
	public void cmd_SElementRemoveExecute() {
		this.cmd = new SCmdElementAdd(this.stage, "c1File.txt");
		this.cmd.execute();
		this.cmd = new SCmdElementRemove(this.stage, "c1File.txt");
		this.cmd.setVerbosity(1);
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		Assert.assertFalse("Twice Should Not Succeed.", this.cmd.execute());
		this.cmd.setVerbosity(0);
		Assert.assertFalse("Twice Should Not Succeed.", this.cmd.execute());
	}

	/**
	 * Cmd_ s element remove.
	 */
	@Test
	public void cmd_SElementRemove() {
		this.cmd = new SCmdElementRemove(this.stage);
		// Check Accept
		Assert.assertTrue("Doesn't Accept 'sRemove -e c2File.txt'.",
				this.cmd.accepts("sRemove -e c2File.txt"));
		Assert.assertTrue("Doesn't Accept 'sRemove -e aCard1'.",
				this.cmd.accepts("sRemove -e aCard1"));
		Assert.assertTrue("Doesn't Accept 'sRemove -e w1Dir/'.",
				this.cmd.accepts("sRemove -e w1Dir/"));
		Assert.assertTrue("Doesn't Accept 'sRemove -e d e f'.",
				this.cmd.accepts("sRemove -e d e f"));
		Assert.assertTrue("Doesn't Accept 'sRemove -e .'.",
				this.cmd.accepts("sRemove -e ."));
		Assert.assertTrue("Doesn't Accept 'sRemove -e ..'.",
				this.cmd.accepts("sRemove -e .."));
		// Check Reject
		Assert.assertFalse("Accepts 'sRemove -e'.",
				this.cmd.accepts("sRemove -e"));
		Assert.assertFalse("Accepts 'sRemove'.", this.cmd.accepts("sRemove"));
		Assert.assertFalse("Accepts 'other'.", this.cmd.accepts("other"));
		// Check Exit & Usage
		Assert.assertFalse("Exits.", this.cmd.exits());
		Assert.assertTrue("Usage.",
				this.cmd.getUsage().startsWith("sRemove -e <eName>"));
	}

	/**
	 * Cmd_ s stash execution.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void cmd_SStashExecution() throws InvalidElementException {
		this.cmd = new SCmdStash(this.stage);
		Stage stage2 = new Stage();
		Assert.assertEquals("1 Descriptions Are Same.", this.stage.describe(),
				stage2.describe());
		// Add Element
		Assert.assertTrue("Artifact Should be Added.", this.stage.getContents()
				.addElement(new Artifact(TH.w2File)));
		stage2 = new Stage();
		Assert.assertFalse("2 Descriptions Are Different.", this.stage
				.describe().equals(stage2.describe()));
		// Content Hash is Not in Reference File.
		Assert.assertTrue("Stash Should Succeed.", this.cmd.execute());
		stage2 = new Stage();
		Assert.assertEquals("3 Descriptions Are Same.", this.stage.describe(),
				stage2.describe());
	}

	/**
	 * Cmd_ s stash.
	 */
	@Test
	public void cmd_SStash() {
		this.cmd = new SCmdStash(this.stage);
		// Check Accepts
		Assert.assertTrue("Doesn't Accept 'sstash'.",
				this.cmd.accepts("sstash"));
		Assert.assertTrue("Doesn't Accept ' lustash  '.",
				this.cmd.accepts("  sstash  "));
		// Check Rejects
		Assert.assertFalse("Accepts 'ss'.", this.cmd.accepts("ss"));
		Assert.assertFalse("Accepts 'lustash'.", this.cmd.accepts("lustash"));
		// Check Exits & Usage
		Assert.assertFalse("Exits.", this.cmd.exits());
		Assert.assertTrue("Usage.", this.cmd.getUsage().startsWith("sStash"));
	}

	/**
	 * Cmd_ s commit.
	 */
	@Test
	public void cmd_SCommit() {
		this.cmd = new SCmdCommit(this.stage, "Commit Command Test.");
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		this.cmd.setVerbosity(1);
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		this.cmd = new SCmdCommit(this.stage, "Commit Command Test.", true);
		Assert.assertTrue("Should Succedd.", this.cmd.execute());
		// Check Accepts
		Assert.assertTrue("Doesn't Accept 'scommit -m Commit Command Test.",
				this.cmd.accepts("scommit -m Commit Command Test"));
		Assert.assertTrue("Doesn't Accept 'scommit -full -m Commit Full.",
				this.cmd.accepts("scommit -full -m Commit Full."));
		// Check Rejects
		Assert.assertFalse("Accepts 'scommit Unmarked Message.",
				this.cmd.accepts("scommit Unmarked Message"));
		Assert.assertFalse("Accepts 'other'.", this.cmd.accepts("other"));
		// Check Exits & Usage
		Assert.assertFalse("Exits.", this.cmd.exits());
		Assert.assertTrue("Usage.",
				this.cmd.getUsage().startsWith("sCommit {-full} -m <message>"));
	}

	/**
	 * Cmd_ s revert hash.
	 */
	@Test
	public void cmd_SRevertHash() {
		final String cHash = this.stage.commit("Scott", "Stage Commit.");
		this.cmd = new SCmdRevert(this.stage, cHash);
		this.cmd.setVerbosity(0);
		Assert.assertTrue("Hash Should Succeed.", this.cmd.execute());
		this.cmd = new SCmdRevert(this.stage, cHash, true);
		this.cmd.setVerbosity(1);
		Assert.assertTrue("Hash Should Succeed.", this.cmd.execute());
		// Check Accepts
		Assert.assertTrue("Doesn't Accept 'srevert -hash " + cHash,
				this.cmd.accepts("srevert -hash " + cHash));
		Assert.assertTrue("Doesn't Accept 'srevert -h " + cHash,
				this.cmd.accepts("srevert -h " + cHash));
		Assert.assertTrue("Doesn't Accept 'srevert -full -h " + cHash,
				this.cmd.accepts("srevert -full -h " + cHash));
		// Check Rejects
		Assert.assertFalse("Accepts 'srevert aCard1 -h " + cHash,
				this.cmd.accepts("srevert aCard -h " + cHash));
		Assert.assertFalse("Accepts 'srevert -HASH XAS12342341",
				this.cmd.accepts("srevert -HASH XAS12342341"));
		Assert.assertFalse("Accepts 'srevert -H " + TH.u1s2Hash
				+ "1 << 41 Chars",
				this.cmd.accepts("srevert -H " + cHash + "1"));
		Assert.assertFalse("Hash Accepts 'other'.", this.cmd.accepts("other"));
		// Check Exits & Usage.
		Assert.assertFalse("Exits.", this.cmd.exits());
		Assert.assertTrue("Usage.",
				this.cmd.getUsage().startsWith("sRevert {-full} -h <hash>"));
	}

	/**
	 * Cmd_ s reset.
	 */
	@Test
	public void cmd_SReset() {
		this.stage.commit("Scott", "Stage Commit.");
		this.cmd = new SCmdReset(this.stage, "-HEAD");
		Assert.assertTrue("Head Should Succeed.", this.cmd.execute());
		this.cmd = new SCmdReset(this.stage, "-CURRENT");
		this.cmd.setVerbosity(1);
		Assert.assertTrue("Current Should Succeed.", this.cmd.execute());
		this.cmd = new SCmdReset(this.stage, "unknownTarget");
		// Check Accepts
		Assert.assertTrue("Doesn't Accept 'sreset'.",
				this.cmd.accepts("sreset"));
		Assert.assertTrue("Doesn't Accept 'sreset -C'.",
				this.cmd.accepts("sreset -C"));
		Assert.assertTrue("Doesn't Accept 'sreset -h'.",
				this.cmd.accepts("sreset -h"));
		Assert.assertTrue("Doesn't Accept 'sreset'.",
				this.cmd.accepts("sreset"));
		Assert.assertTrue("Doesn't Accept 'sreset -C'.",
				this.cmd.accepts("sreset -C"));
		Assert.assertTrue("Doesn't Accept 'sreset -h'.",
				this.cmd.accepts("sreset -h"));
		Assert.assertTrue("Doesn't Accept 'sreset -head'.",
				this.cmd.accepts("sreset -head"));
		Assert.assertTrue("Doesn't Accept 'sreset -CURRENT'.",
				this.cmd.accepts("sreset -CURRENT"));
		Assert.assertTrue("Doesn't Accept 'sreset -a -b -d -CURRENT'.",
				this.cmd.accepts("sreset -a -b -d -CURRENT"));
		Assert.assertTrue("Doesn't Accept 'sreset two three four'.",
				this.cmd.accepts("sreset two three four"));
		// Check Reject
		Assert.assertFalse("Doesn't Accept 'other two'.",
				this.cmd.accepts("other two"));
		// Check Exit and Usage.
		Assert.assertFalse("Exits.", this.cmd.exits());
		Assert.assertTrue("Usage.",
				this.cmd.getUsage().startsWith("sReset {-C|-H}"));
	}

	/**
	 * Cmd_ s log.
	 */
	@Test
	public void cmd_SLog() {
		this.cmd = new SCmdLog(this.stage, true);
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		this.cmd.setVerbosity(1);
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		// Check Acceptable
		Assert.assertTrue("Doesn't Accept 'slog'.", this.cmd.accepts("slog"));
		Assert.assertTrue("Doesn't Accept 'slog -s'.",
				this.cmd.accepts("slog -s"));
		Assert.assertTrue("Doesn't Accept 'slog -V'.",
				this.cmd.accepts("slog -V"));
		// Check Reject
		Assert.assertFalse("Accepts 'slog aCard1 -s'.",
				this.cmd.accepts("slog aCard1 -s"));
		Assert.assertFalse("Accepts 'other'.", this.cmd.accepts("other"));
		Assert.assertFalse("Accepts 'other two'.",
				this.cmd.accepts("other two"));
		// Check Exit and Usage
		Assert.assertFalse("Exits.", this.cmd.exits());
		Assert.assertTrue("Usage.", this.cmd.getUsage().startsWith("sLog"));
	}

	/**
	 * Cmd_ s diff.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	@Test
	public void cmd_SDiff() throws InvalidElementException {
		// Setup Differential Calculation.
		this.stage.getContents().addElement(new Artifact(TH.w1File));
		this.stage.commit("Scott", "Differential Commit.");
		TH.writeFile(TH.w1File,
				"Line 1 Content.\nLine 2 Content.\n\nLine 4 Content.");
		this.cmd = new SCmdDiff(this.stage, "c1File.txt");
		Assert.assertTrue("Should Succeed.", this.cmd.execute());
		this.cmd = new SCmdDiff(this.stage);
		Assert.assertFalse("Should Not Succeed.", this.cmd.execute());
		// Check Acceptable
		Assert.assertTrue("Doesn't Accept 'sdiff -e c1File.txt'.",
				this.cmd.accepts("sdiff -e c1File.txt"));
		Assert.assertTrue("Doesn't Accept 'sdiff -e a b c'.",
				this.cmd.accepts("sdiff -e a b c"));
		Assert.assertTrue("Doesn't Accept 'sdiff -e a/b/c/'.",
				this.cmd.accepts("sdiff -e a/b/c/"));
		// Check Reject
		Assert.assertFalse("Accepts 'sdiff aCard1 -e c1File.txt'.",
				this.cmd.accepts("sdiff aCard1 -e c1File.txt"));
		Assert.assertTrue("Should Contain Usage.", this.cmd
				.getProcessingFailure().contains(this.cmd.getUsage()));
		// assertFalse("Accepts 'sdiff abc -e   /'.",
		// cmd.accepts("sdiff abc -e    /"));
		Assert.assertFalse("Accepts 'sdiff -e   '.",
				this.cmd.accepts("sdiff -e    "));
		Assert.assertFalse("Accepts 'ludiff -e xxx'.",
				this.cmd.accepts("ludiff -e xxx"));

		// Check Exit and Usage
		Assert.assertFalse("Exits.", this.cmd.exits());
		Assert.assertTrue("Usage.",
				this.cmd.getUsage().startsWith("sDiff -e <eName>"));
	}

}
