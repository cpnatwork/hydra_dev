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
package org.hydra.startup;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.hydra.Hydra;
import org.hydra.core.Configuration;
import org.hydra.core.InvalidConfigurationException;
import org.hydra.ui.UI;
import org.hydra.ui.UIWriter;
import org.hydra.ui.cli.CLI;
import org.hydra.utilities.Logger;

/**
 * Parses the command line arguments and initializes the key components of the
 * Hydra Version Control System.
 */
public class StarterCLI {

	// CommandLine Flags
	/** The Constant FLAG_INITIALIZATION. */
	public static final String FLAG_INITIALIZATION = "--initialize";

	/** The Constant FLAG_CLI. */
	public static final String FLAG_CLI = "--cli";

	/** The Constant FLAG_VERBOSITY. */
	public static final String FLAG_VERBOSITY = "--v";

	/** The Constant FLAG_CWD. */
	public static final String FLAG_CWD = "--cwd";
	// Variables
	/** The arg list. */
	protected final ArrayList<String> argList;

	/** The config. */
	protected final Configuration config;

	/** The writer. */
	protected final UIWriter writer;

	/** The sb cmd. */
	protected final StringBuilder sbCmd;

	/** The clio verbosity. */
	protected int clioVerbosity;

	/** The cwd. */
	protected File cwd;

	/**
	 * Specialized constructor that takes the command line arguments for the
	 * system.
	 * 
	 * @param args
	 *            String[].
	 */
	public StarterCLI(final String[] args) {
		this.argList = new ArrayList<String>(Arrays.asList(args));
		this.config = Configuration.getInstance();
		this.writer = UIWriter.getInstance();
		this.sbCmd = new StringBuilder("");
		this.clioVerbosity = UIWriter.MEDIUM;
		this.cwd = new File(".");
	}

	/**
	 * Start hydra versioning.
	 * 
	 * @throws org.hydra.core.InvalidConfigurationException
	 *             - invalidConfig.
	 */
	public void start() throws InvalidConfigurationException {
		this.parseArguments();
		this.configureHydra();
		this.configureUI();
		this.startUI();
	}

	/**
	 * Parse the command line arguments to derive the system verbosity, current
	 * working directory, whether to initialize a new repository and enter
	 * interactive mode. Additionally, collect any given command to be executed.
	 */
	protected void parseArguments() {
		final StringBuilder sbParse = new StringBuilder(Hydra.getLongHeader()
				+ "\n\tParams: ");
		for (int i = 0; i < this.argList.size(); i++) {
			if (this.argList.get(i).equals(StarterCLI.FLAG_INITIALIZATION)) {
				sbParse.append(" " + i + ":[" + StarterCLI.FLAG_INITIALIZATION
						+ "]");
			} else if (this.argList.get(i).equals(StarterCLI.FLAG_CLI)) {
				sbParse.append(" " + i + ":[" + StarterCLI.FLAG_CLI + "]");
			} else if (this.argList.get(i).equals(StarterCLI.FLAG_VERBOSITY)) {
				this.clioVerbosity = Integer.parseInt(this.argList.get(++i));
				sbParse.append(" " + (i - 1) + ":[" + StarterCLI.FLAG_VERBOSITY
						+ "=" + this.argList.get(i) + "]");
			} else if (this.argList.get(i).equals(StarterCLI.FLAG_CWD)) {
				this.cwd = new File(this.argList.get(++i));
				sbParse.append(" " + (i - 1) + ":[" + StarterCLI.FLAG_CWD + "="
						+ this.argList.get(i) + "]");
			} else {
				this.sbCmd.append(this.argList.get(i) + " ");
			}
		}
		if (this.sbCmd.length() == 0) {
			this.argList.add(StarterCLI.FLAG_CLI);
		}
		sbParse.append("\n\tInteractive: "
				+ (this.argList.contains(StarterCLI.FLAG_CLI)) + "\n");
		this.writer.setVerbosity(this.clioVerbosity);
		this.writer.println(sbParse.toString(), UIWriter.MEDIUM);
	}

	/**
	 * Configure hydra's repository and workspace.
	 * 
	 * @throws InvalidConfigurationException
	 *             - invalidConfig.
	 */
	protected void configureHydra() throws InvalidConfigurationException {
		try {
			this.cwd = this.cwd.getCanonicalFile();
			if (this.argList.contains(StarterCLI.FLAG_INITIALIZATION)) {
				this.createNewRepository();
			} else {
				this.useOldRepository();
			}
			this.config.setCurrentWorkingDirectory(this.cwd);
		} catch (final Exception e) {
			Logger.getInstance().exception("Unable to Configure Hydra.", e);
			throw new InvalidConfigurationException(
					"Unable to Configure Hydra.", e);
		}
	}

	/**
	 * Create a new repository in the given current working directory.
	 */
	protected void createNewRepository() {
		this.writer.print("Initializing New Repository...", UIWriter.MEDIUM);
		this.config.initializeSystem(this.cwd);
		this.writer.println("Complete.", UIWriter.MEDIUM);
	}

	/**
	 * Search for and use a preexisting repository.
	 * 
	 * @throws InvalidConfigurationException
	 *             - invalidConfig.
	 */
	protected void useOldRepository() throws InvalidConfigurationException {
		try {
			this.writer.println("Searching for Repository...", UIWriter.MEDIUM);
			final File workspace = this.findRepository(this.cwd)
					.getParentFile().getCanonicalFile();
			this.config.initializeSystem(workspace);
			this.writer.println("Repository Found.", UIWriter.MEDIUM);
		} catch (final Exception e) {
			throw new InvalidConfigurationException(
					"Unable to Use Old Repository.", e);
		}
	}

	/**
	 * Search the directory's ancestors for a repository and return it.
	 * 
	 * @param searchDirectory
	 *            File.
	 * @return repository - File.
	 * @throws InvalidConfigurationException
	 *             - invalidConfig.
	 */
	protected File findRepository(final File searchDirectory)
			throws InvalidConfigurationException {
		if (searchDirectory == null) {
			this.writer.println("Repository Not Found.\n", UIWriter.LOW);
			this.writer.println(
					"\tCreate New Repository with '--initialize' Parameter.\n",
					UIWriter.LOW);
			this.writer
					.println(
							"\tChange to Directory within Workspace with '--cwd <path>' Parameter.\n",
							UIWriter.LOW);
			throw new InvalidConfigurationException(
					"Unable to Find Repository.");
		}
		this.writer.println("\t>Searching [" + searchDirectory + "]...",
				UIWriter.MEDIUM);
		final File targetRepository = new File(searchDirectory, ".hydra");
		if (targetRepository.exists()) {
			return targetRepository;
		} else {
			return this.findRepository(searchDirectory.getParentFile());
		}
	}

	/**
	 * Configure the UI's verbosity.
	 */
	protected void configureUI() {
		this.writer.print("Configuring UI...", UIWriter.MEDIUM);
		this.writer.setVerbosity(this.clioVerbosity);
		this.writer.println("Complete.\n", UIWriter.MEDIUM);
	}

	/**
	 * Execute the command given on the command line and then enter interactive
	 * mode if desired.
	 * 
	 * @throws InvalidConfigurationException
	 *             - invalidConfig.
	 */
	protected void startUI() throws InvalidConfigurationException {
		try {
			final UI ui = new CLI();
			if (this.sbCmd.length() > 0) {
				((CLI) ui).executeCommand(this.sbCmd.toString().trim());
			} else {
				ui.interact();
			}
		} catch (final Exception e) {
			throw new InvalidConfigurationException("Unable to Start UI.", e);
		}
	}

}
