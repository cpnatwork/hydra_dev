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

import org.hydra.Hydra;
import org.hydra.core.InvalidConfigurationException;
import org.hydra.ui.UI;
import org.hydra.ui.UIWriter;
import org.hydra.ui.cli.CLI;
import org.hydra.ui.gui.GUI;

/**
 * Extends the Starter with GUI set-up.
 * 
 * Overrides StarterCLI methods.
 */
public class StarterExtByGUI extends StarterCLI {

	public StarterExtByGUI(final String[] args) {
		super(args);
	}

	/** The Constant FLAG_GUI. */
	public static final String FLAG_GUI = "--gui";

	/**
	 * Parse the command line arguments to derive the system verbosity, current
	 * working directory, whether to initialize a new repository and enter
	 * interactive mode. Additionally, collect any given command to be executed.
	 */
	@Override
	protected void parseArguments() {
		final StringBuilder sbParse = new StringBuilder(Hydra.getLongHeader()
				+ "\n\tParams: ");
		for (int i = 0; i < this.argList.size(); i++) {
			if (this.argList.get(i).equals(StarterCLI.FLAG_INITIALIZATION)) {
				sbParse.append(" " + i + ":[" + StarterCLI.FLAG_INITIALIZATION
						+ "]");
			} else if (this.argList.get(i).equals(StarterCLI.FLAG_CLI)) {
				sbParse.append(" " + i + ":[" + StarterCLI.FLAG_CLI + "]");
			} else if (this.argList.get(i).equals(StarterExtByGUI.FLAG_GUI)) {
				sbParse.append(" " + i + ":[" + StarterExtByGUI.FLAG_GUI + "]");
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
	 * Execute the command given on the command line and then enter interactive
	 * mode if desired.
	 * 
	 * @throws InvalidConfigurationException
	 *             - invalidConfig.
	 */
	@Override
	protected void startUI() throws InvalidConfigurationException {
		try {
			UI ui = new CLI();
			if (this.sbCmd.length() > 0) {
				((CLI) ui).executeCommand(this.sbCmd.toString().trim());
			}
			if (this.argList.contains(StarterExtByGUI.FLAG_GUI)) {
				ui = new GUI();
				ui.interact();
			} else if (this.argList.contains(StarterCLI.FLAG_CLI)
					|| (this.sbCmd.length() == 0)) {
				ui.interact();
			}
		} catch (final Exception e) {
			throw new InvalidConfigurationException("Unable to Start UI.", e);
		}
	}

}
