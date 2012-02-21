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
import java.util.Date;

import org.hydra.TH;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * The Class LoggerTest.
 */
public class LoggerTest {

	/** The SEP. */
	private final String SEP = Logger.SEPARATOR;

	/** The logger. */
	private Logger logger;

	/**
	 * Before class.
	 */
	@BeforeClass
	public static void beforeClass() {
		Logger.getInstance().info("LOGGER TEST");
	}

	/**
	 * Before test.
	 */
	@Before
	public void beforeTest() {
		TH.setupWorkspace(false);
		this.logger = Logger.getInstance();
		this.logger.setSystemLevel(LoggerLevel.INFO);
		this.logger.setUserLevel(LoggerLevel.NO_LOG);
		this.logger.setLogFile(new File(TH.workspaceParent, ".loggerdump"));
		this.logger.clearLog();
	}

	/**
	 * Logger_ creation.
	 */
	@Test
	public void logger_Creation() {
		final String status = "  Log File:\t" + TH.workspaceParent
				+ File.separator + ".loggerdump (Found)\n  Log Count:\t1\n"
				+ "  System Level:\tINFO\n  User Level:\tNO_LOG\n";
		Assert.assertEquals("Incorrect Status Returned.", status,
				this.logger.getStatus());
		// System.out.println(logger.getStatus());
		this.logger.setLogFile(null);
		Assert.assertNull("Should Have Null LogFile.", this.logger.getLogFile());
		this.logger.log("", LoggerLevel.INFO);
	}

	/**
	 * Logger_ read only log.
	 */
	@Test
	public void logger_ReadOnlyLog() {
		final File dir = new File(".");
		this.logger.setLogFile(dir);
		this.logger.log(" xxxx ", LoggerLevel.INFO);
	}

	/**
	 * Logger_ count entries.
	 */
	@Test
	public void logger_CountEntries() {
		this.logger.setLogFile(null);
		Assert.assertEquals("Return 0 Entries for Null Log.", 0,
				this.logger.countEntries());
		this.logger
				.setLogFile(new File(TH.workspace, "NonExistentLogFile.log"));
		Assert.assertEquals("Should Always Return -1 Entries.", -1,
				this.logger.countEntries());
	}

	/**
	 * Logger_info.
	 */
	@Test
	public void logger_info() {
		Assert.assertEquals("Should be One Entry.", 1,
				this.logger.countEntries());
		final String iMessage1 = "My First Info Message.";
		final String iEntry1 = "[INFO]" + this.SEP + new Date() + this.SEP
				+ iMessage1;
		Assert.assertEquals(
				"WARNING: Transient Time Error - Retry.First InfoEntry Incorrect.",
				iEntry1, this.logger.info(iMessage1));
		Assert.assertEquals("Should be Two Entries.", 2,
				this.logger.countEntries());
		final String iMessage2 = "My Second Info Message.";
		final String iEntry2 = "[INFO]" + this.SEP + new Date() + this.SEP
				+ iMessage2;
		Assert.assertEquals("Second InfoEntry Incorrect.", iEntry2,
				this.logger.info(iMessage2));
		Assert.assertEquals("Should be Three Entries.", 3,
				this.logger.countEntries());
	}

	/**
	 * Logger_warning.
	 */
	@Test
	public void logger_warning() {
		Assert.assertEquals("Should be One Entry.", 1,
				this.logger.countEntries());
		final String wMessage1 = "My First Warning Message.";
		final String wEntry1 = "[WARNING]" + this.SEP + new Date() + this.SEP
				+ wMessage1;
		Assert.assertEquals("First WarnEntry Incorrect.", wEntry1,
				this.logger.warning(wMessage1));
		Assert.assertEquals("Should be Two Entries.", 2,
				this.logger.countEntries());
		final String wMessage2 = "My Second Warning Message.";
		final String wEntry2 = "[WARNING]" + this.SEP + new Date() + this.SEP
				+ wMessage2;
		Assert.assertEquals("Second WarnEntry Incorrect.", wEntry2,
				this.logger.warning(wMessage2));
		Assert.assertEquals("Should be Three Entries.", 3,
				this.logger.countEntries());
	}

	/**
	 * Logger_exception.
	 */
	@Test
	public void logger_exception() {
		Assert.assertEquals("Should be One Entry.", 1,
				this.logger.countEntries());
		final String eMessage1 = "My First Exception Message.";
		final String eEntry1 = "[EXCEPTION]" + this.SEP + new Date() + this.SEP
				+ eMessage1;
		Assert.assertEquals("First ExceptionEntry Incorrect.", eEntry1,
				this.logger.exception(eMessage1));
		Assert.assertEquals("Should be Two Entries.", 2,
				this.logger.countEntries());
		final String eMessage2 = "My Second Exception Message.";
		final Exception ex = new Exception("Normal Exception");
		final int lineNr = ex.getStackTrace()[0].getLineNumber();
		final String eExceptionFormat = ex
				+ "[org.hydra.utilities.LoggerTest.logger_exception(LoggerTest.java:"
				+ lineNr + ")]";
		final String eEntry2 = "[EXCEPTION]" + this.SEP + new Date() + this.SEP
				+ eMessage2 + this.SEP + eExceptionFormat;
		Assert.assertEquals("Second ExceptionEntry Incorrect.", eEntry2,
				this.logger.exception(eMessage2, ex));
		Assert.assertEquals("Should be Three Entries.", 3,
				this.logger.countEntries());
	}

	/**
	 * Logger_critical.
	 */
	@Test
	public void logger_critical() {
		Assert.assertEquals("Should be One Entry.", 1,
				this.logger.countEntries());
		final String cMessage1 = "My First Exception Message.";
		final String cEntry1 = "[CRITICAL]" + this.SEP + new Date() + this.SEP
				+ cMessage1;
		Assert.assertEquals("First CriticalEntry Incorrect.", cEntry1,
				this.logger.critical(cMessage1));
		Assert.assertEquals("Should be Two Entries.", 2,
				this.logger.countEntries());
		final String cMessage2 = "My Second Exception Message.";
		final Exception ex = new Exception("Critical Exception");
		final int lineNr = ex.getStackTrace()[0].getLineNumber();
		final String cExceptionFormat = ex
				+ "[org.hydra.utilities.LoggerTest.logger_critical(LoggerTest.java:"
				+ lineNr + ")]";
		final String cEntry2 = "[CRITICAL]" + this.SEP + new Date() + this.SEP
				+ cMessage2 + this.SEP + cExceptionFormat;
		Assert.assertEquals("Second CriticalEntry Incorrect.", cEntry2,
				this.logger.critical(cMessage2, ex));
		Assert.assertEquals("Should be Three Entries.", 3,
				this.logger.countEntries());
	}

	/**
	 * Logger_ levels creation int.
	 */
	@Test
	public void logger_LevelsCreationInt() {
		LoggerLevel test = LoggerLevel.toLoggerLevel(0);
		Assert.assertEquals("Should Be DEBUG.", LoggerLevel.DEBUG, test);
		test = LoggerLevel.toLoggerLevel(1);
		Assert.assertEquals("Should Be INFO.", LoggerLevel.INFO, test);
		test = LoggerLevel.toLoggerLevel(2);
		Assert.assertEquals("Should Be WARNING.", LoggerLevel.WARNING, test);
		test = LoggerLevel.toLoggerLevel(3);
		Assert.assertEquals("Should Be EXCEPTION.", LoggerLevel.EXCEPTION, test);
		test = LoggerLevel.toLoggerLevel(4);
		Assert.assertEquals("Should Be CRITICAL.", LoggerLevel.CRITICAL, test);
		test = LoggerLevel.toLoggerLevel(5);
		Assert.assertEquals("Should Be NO_LOG.", LoggerLevel.NO_LOG, test);
	}

	/**
	 * Logger_ levels creation int boundary.
	 */
	@Test
	public void logger_LevelsCreationIntBoundary() {
		LoggerLevel test = LoggerLevel.toLoggerLevel(-1);
		Assert.assertEquals("Should Be NO_LOG.", LoggerLevel.NO_LOG, test);
		test = LoggerLevel.toLoggerLevel(6);
		Assert.assertEquals("Should Be NO_LOG.", LoggerLevel.NO_LOG, test);
	}

	/**
	 * Logger_ levels creation string.
	 */
	@Test
	public void logger_LevelsCreationString() {
		LoggerLevel test = LoggerLevel.toLoggerLevel("DEBUG");
		Assert.assertEquals("Should Be DEBUG.", LoggerLevel.DEBUG, test);
		test = LoggerLevel.toLoggerLevel("INFO");
		Assert.assertEquals("Should Be INFO.", LoggerLevel.INFO, test);
		test = LoggerLevel.toLoggerLevel("WARNING");
		Assert.assertEquals("Should Be WARNING.", LoggerLevel.WARNING, test);
		test = LoggerLevel.toLoggerLevel("EXCEPTION");
		Assert.assertEquals("Should Be EXCEPTION.", LoggerLevel.EXCEPTION, test);
		test = LoggerLevel.toLoggerLevel("CRITICAL");
		Assert.assertEquals("Should Be CRITICAL.", LoggerLevel.CRITICAL, test);
		test = LoggerLevel.toLoggerLevel("NO_LOG");
		Assert.assertEquals("Should Be NO_LOG.", LoggerLevel.NO_LOG, test);
	}

	/**
	 * Logger_ levels creation string boundary.
	 */
	@Test
	public void logger_LevelsCreationStringBoundary() {
		LoggerLevel test = LoggerLevel.toLoggerLevel("debug");
		Assert.assertEquals("Should Be DEBUG.", LoggerLevel.DEBUG, test);
		test = LoggerLevel.toLoggerLevel("unknown");
		Assert.assertEquals("Should Be DEBUG.", LoggerLevel.NO_LOG, test);
	}

	/**
	 * Logger_ logger level comparision.
	 */
	@Test
	public void logger_LoggerLevelComparision() {
		final LoggerLevel debug = LoggerLevel.DEBUG;
		final LoggerLevel info = LoggerLevel.INFO;
		final LoggerLevel no_log = LoggerLevel.NO_LOG;
		Assert.assertTrue("Info is Greater Than Debug.",
				info.greaterThan(debug));
		Assert.assertFalse("Debug is Not Greater Than Info.",
				debug.greaterThan(info));
		Assert.assertTrue("Debug is Less Than Info.", debug.lessThan(info));
		Assert.assertFalse("Info is Not Less Than Debug.", info.lessThan(debug));
		Assert.assertTrue("NoLog is Not Greater Than Debug.",
				no_log.greaterThan(debug));
		Assert.assertFalse("NoLog is Less Than Debug.", no_log.lessThan(debug));

	}
}
