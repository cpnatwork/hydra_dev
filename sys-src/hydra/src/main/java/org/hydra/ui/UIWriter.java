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
package org.hydra.ui;

import java.io.PrintStream;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JTextArea;

import org.hydra.core.Configuration;

/**
 * Provides printing functions to a designated output stream. Default is
 * System.out.
 *
 * @since 0.1
 * @version 0.2
 * @author Scott A. Hady
 */
public class UIWriter implements Observer {

	/** The Constant PROPERTY_VERBOSITY. */
	public static final String PROPERTY_VERBOSITY = "UI.verbosity";

	/** The Constant BREAK_LINE. */
	public static final String BREAK_LINE = "--------------------------------------------------\n";

	// Verbosity Level Constants.
	/** The Constant NONE. */
	public static final int NONE = 0;

	/** The Constant LOW. */
	public static final int LOW = 2;

	/** The Constant MEDIUM. */
	public static final int MEDIUM = 5;

	/** The Constant HIGH. */
	public static final int HIGH = 8;

	/** The Constant ALL. */
	public static final int ALL = 10;

	/** The writer. */
	private static UIWriter writer = null;

	/** The config. */
	private Configuration config = null;

	/** The writer verbosity. */
	private static int writerVerbosity = UIWriter.ALL;

	/** The gui flag. */
	private static boolean guiFlag = false;

	/** The gui console. */
	private static JTextArea guiConsole = null;

	/** The out. */
	private static PrintStream out = System.out;

	/**
	 * CREATIONAL (SINGLETON) *************************************************.
	 */
	protected UIWriter() {
		this.config = Configuration.getInstance();
		this.configureWriter();
	}

	/**
	 * Return system's singleton instance shared by all UIs.
	 *
	 * @return uiWriter - UIWriter.
	 */
	public static UIWriter getInstance() {
		if (UIWriter.writer == null) {
			UIWriter.writer = new UIWriter();
		}
		return UIWriter.writer;
	}

	/**
	 * Returns the writer's current output stream.
	 *
	 * @return outputStream - PrintStream.
	 */
	public static PrintStream getOutStream() {
		return UIWriter.out;
	}

	/**
	 * Returns the writer's current target JTextArea to write to.
	 *
	 * @return targetConsole - JTextArea.
	 */
	public static JTextArea getOutputConsole() {
		return UIWriter.guiConsole;
	}

	/**
	 * Specify if the writer should write to the console or the outputstream.
	 *
	 * @param useConsole
	 *            boolean.
	 */
	public static void useConsole(final boolean useConsole) {
		UIWriter.guiFlag = useConsole;
	}

	/**
	 * Determine if the writer is currently writing to a console.
	 *
	 * @return isWritingToConsole - boolean.
	 */
	public static boolean isConsole() {
		return UIWriter.guiFlag;
	}

	/**
	 * Get the writer's verbosity level.
	 *
	 * @return verbosityLevel - int.
	 */
	public static int getVerbosity() {
		return UIWriter.writerVerbosity;
	}

	/**
	 * Set where the data should be printed.
	 *
	 * @param outStream
	 *            PrintStream.
	 */
	public static void setOutStream(final PrintStream outStream) {
		UIWriter.guiFlag = false;
		UIWriter.out = outStream;
	}

	/**
	 * Sets the target output console.
	 *
	 * @param console
	 *            JTextArea.
	 */
	public static void setOutputConsole(final JTextArea console) {
		UIWriter.guiFlag = true;
		UIWriter.guiConsole = console;
	}

	/**
	 * Set the level of verbosity to output.
	 *
	 * @param verbosityLevel
	 *            int.
	 */
	public void setVerbosity(final int verbosityLevel) {
		UIWriter.writerVerbosity = verbosityLevel;
		this.config.setProperty(UIWriter.PROPERTY_VERBOSITY,
				Integer.toString(UIWriter.writerVerbosity));
	}

	/**
	 * Prints string to the designated output stream.
	 *
	 * @param outputStr
	 *            String.
	 */
	public void print(final String outputStr) {
		if (UIWriter.guiFlag) {
			UIWriter.guiConsole.append(outputStr);
		} else {
			UIWriter.out.print(outputStr);
		}
	}

	/**
	 * Prints string to designated output stream if the verbosity level is
	 * greater than 0.
	 *
	 * @param outputStr
	 *            String.
	 * @param outputVerbosity
	 *            int.
	 */
	public void print(final String outputStr, final int outputVerbosity) {
		if (outputVerbosity <= UIWriter.writerVerbosity) {
			this.print(outputStr);
		}
	}

	/**
	 * Prints string to the designated output stream with an additional eol
	 * character.
	 *
	 * @param outputStr
	 *            String.
	 */
	public void println(final String outputStr) {
		this.print(outputStr + "\n");
	}

	/**
	 * Prints string to designated output stream with additional eol charater
	 * only if the verbosity level provided is greater than 0.
	 *
	 * @param outputStr
	 *            String.
	 * @param outputVerbosity
	 *            int.
	 */
	public void println(final String outputStr, final int outputVerbosity) {
		if (outputVerbosity <= UIWriter.writerVerbosity) {
			this.println(outputStr);
		}
	}

	/**
	 * Prints a long dashed line which may be used to separate commands and
	 * results.
	 */
	public void printLineBreak() {
		this.println(UIWriter.BREAK_LINE);
	}

	/**
	 * Prints a long dashed line which may be used to separate commands and
	 * results, but only if the outputVerbosity is sufficient.
	 *
	 * @param outputVerbosity
	 *            int.
	 */
	public void printLineBreak(final int outputVerbosity) {
		this.println(UIWriter.BREAK_LINE, outputVerbosity);
	}

	/**
	 * {@inheritDoc}
	 *
	 * OBSEVER METHODS (IMPLEMENTATION) ***************************************.
	 */
	@Override
	public void update(final Observable observable, final Object notice) {
		this.configureWriter();
	}

	/**
	 * UIWRITER METHODS (PRIVATE) *********************************************.
	 */

	private void configureWriter() {
		if (this.config.hasProperty(UIWriter.PROPERTY_VERBOSITY)) {
			UIWriter.writerVerbosity = Integer.parseInt(this.config
					.getProperty(UIWriter.PROPERTY_VERBOSITY));
		}
	}

}
