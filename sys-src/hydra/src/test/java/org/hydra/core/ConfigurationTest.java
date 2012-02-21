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
import org.hydra.ui.UIWriter;
import org.hydra.utilities.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * The Class ConfigurationTest.
 */
public class ConfigurationTest {

	/** The config. */
	Configuration config;

	/**
	 * Start class test.
	 */
	@BeforeClass
	public static void startClassTest() {
		TH.setupLogging();
		Logger.getInstance().info("CONFIGURAION TEST");
	}

	/**
	 * Setup tests.
	 */
	@Before
	public void setupTests() {
		this.config = Configuration.getInstance();
	}

	/**
	 * Cfg_get instance.
	 */
	@Test
	public void cfg_getInstance() {
		this.config = Configuration.getInstance();
	}

	/**
	 * Cfg_ configure system from property file.
	 */
	@Test
	public void cfg_ConfigureSystemFromPropertyFile() {
		this.config.configureSystem();
	}

	/**
	 * Cfg_ configure system missing property file.
	 */
	@Test
	public void cfg_ConfigureSystemMissingPropertyFile() {
		TH.deleteFile(new File(TH.repository, "hydra.properties"));
		this.config.configureSystem();
	}

	/**
	 * Cfg_ logging.
	 */
	@Test
	public void cfg_Logging() {
		Assert.assertEquals("Should Return Logger's LogFile.", Logger
				.getInstance().getLogFile().getPath(),
				this.config.getProperty(Logger.PROPERTY_LOGFILE));
		Assert.assertTrue("Property is Present.",
				this.config.hasProperty(Logger.PROPERTY_LOGFILE));
		this.config.setProperty(Logger.PROPERTY_LOGFILE, null);
		Assert.assertFalse("Property Has Been Removed.",
				this.config.hasProperty(Logger.PROPERTY_LOGFILE));
	}

	/**
	 * Cfg_ ui writer.
	 */
	@Test
	public void cfg_UIWriter() {
		Assert.assertEquals("Should be 10.", UIWriter.getInstance()
				.getVerbosity(), Integer.parseInt(this.config
				.getProperty(UIWriter.PROPERTY_VERBOSITY)));
	}

	/**
	 * Config_ cwd not set.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void config_CWDNotSet() throws Exception {
		final Configuration config = Configuration.getInstance();
		final File workspace = config.getWorkspace();
		final File cwd = config.getCurrentWorkingDirectory();
		Assert.assertEquals("WS and CWD Should be Same.", workspace, cwd);
	}

	/**
	 * Config_ cwd set.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void config_CWDSet() throws Exception {
		final Configuration config = Configuration.getInstance();
		final File workspace = config.getWorkspace();
		final File cwdSet = new File(workspace, "subDir");
		config.setCurrentWorkingDirectory(cwdSet);
		final File cwdGet = config.getCurrentWorkingDirectory();
		Assert.assertEquals("Get Should Be Set.", cwdGet, cwdSet);
		Assert.assertEquals("Path Ext Should be '/subDir'.", File.separator
				+ "subDir", config.calculatePathExtension());
	}
}
