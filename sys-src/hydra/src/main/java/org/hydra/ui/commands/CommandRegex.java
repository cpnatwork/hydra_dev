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

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hydra.core.Configuration;

/**
 * Default implementation of the Command Pattern which does nothing and returns
 * failure. The primary work of the command is done by {@link #execute}. It
 * defines the basic processing operations {@link #recognizes} and
 * {@link #accepts} as template methods using the regular expression factory
 * methods and the processing method. All other commands are derived from this
 * base class.
 *
 * @since 0.1
 * @version 0.2
 * @author Scott A. Hady
 */
public class CommandRegex extends CommandCLI {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The Constant DEFAULT_NAME. */
	public static final String DEFAULT_NAME = "Regular Expression Command";

	/** The Constant DEFAULT_ID. */
	public static final String DEFAULT_ID = "CommandRegEx";

	// Regular Expressions
	/** The command reg ex. */
	private final String commandRegEx = "^\\s*$";

	/** The complete reg ex. */
	private final String completeRegEx = this.commandRegEx;

	/** The command pattern. */
	private final Pattern commandPattern = Pattern.compile(this.commandRegEx);

	/** The complete pattern. */
	private final Pattern completePattern = Pattern.compile(this.completeRegEx);

	/**
	 * Specialized constructor which specifies the command's name and id.
	 *
	 * @param commandName
	 *            String.
	 * @param commandId
	 *            String.
	 */
	public CommandRegex(final String commandName, final String commandId) {
		super(commandName, commandId);
	}

	/**
	 * COMMANDCLIREGEX METHODS ************************************************.
	 * 
	 * @return the command pattern
	 */

	/**
	 * Factory Method which returns a RegEx pattern that may be used to
	 * recognize if a command from the commandline, even if it is not completely
	 * correctly formatted.
	 *
	 * @return Pattern commandPattern.
	 */
	public Pattern getCommandPattern() {
		return this.commandPattern;
	}

	/**
	 * Factory Method which returns a RegEx pattern that may be matched to the
	 * commandline and finds only completely and correctly formatted commands.
	 * This is a stricter extension of the command pattern.
	 *
	 * @return Pattern completePattern.
	 */
	public Pattern getCompletePattern() {
		return this.completePattern;
	}

	/**
	 * Template Method which processes a matched commandline. Returns false if a
	 * processing exception occurs.
	 *
	 * @param matcher
	 *            Matcher.
	 * @return success - boolean.
	 */
	public boolean processMatcher(final Matcher matcher) {
		return true;
	}

	/**
	 * COMMANDCLI METHODS (OVERRIDDEN) ****************************************.
	 * 
	 * @return true, if successful
	 */

	/**
	 * {@inheritDoc}
	 *
	 * This is a template method that verifies a command string is correctly
	 * formatted and processes the matching arguments in preparation for the
	 * command's execution. To adjust the parsing functionality, the
	 * {@link #getCommandPattern}, {@link #getCompletePattern} and
	 * {@link #processMatcher} methods should be overridden.
	 */
	@Override
	public boolean process() {
		final Matcher matcher = this.getCompletePattern().matcher(this.cmdLine);
		if (matcher.matches())
			return this.processMatcher(matcher);
		else
			return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return a string describing the commands usage.
	 */
	@Override
	public String getUsage() {
		return "\t\t\t\tNull Command Functionality.";
	}

	/**
	 * {@inheritDoc}
	 *
	 * Execute command, does nothing and returns failure. Logically occurs after
	 * the {@link #process} method.
	 */
	@Override
	public boolean execute() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Determine if the system should exit after executing the command.
	 */
	@Override
	public boolean exits() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return a string describing the the command string that was not properly
	 * processed and the expected command's usage. Logically occurs after the
	 */
	@Override
	public String getProcessingFailure() {
		final StringBuilder sb = new StringBuilder("Parsing Failure!\n");
		sb.append("\tWhen [" + this.getClass().getName()
				+ "] attempted to parse [" + this.cmdLine + "]\n");
		sb.append("Expected Command Usage:\n\t" + this.getUsage() + "\n");
		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Recognizes if a given commandline string is intended to represent this
	 * command.
	 */
	@Override
	public boolean recognizes() {
		return this.getCommandPattern().matcher(this.cmdLine).find();
	}

	/**
	 * Determine the correct file from the CLI target.
	 *
	 * @param targetName
	 *            String.
	 * @return targetFile - File.
	 */
	public File findElementFile(final String targetName) {
		final String adjustedTarget = this.adjustPathSeparators(targetName);
		final Configuration config = Configuration.getInstance();
		File targetFile = new File(config.getWorkspace(),
				config.calculatePathExtension() + File.separator
						+ adjustedTarget);
		try {
			targetFile = targetFile.getCanonicalFile();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return targetFile;
	}

	/**
	 * Determine the correct name of the CLI element target.
	 *
	 * @param targetName
	 *            String.
	 * @return targetFullName - String.
	 */
	public String findElementName(final String targetName) {
		final File targetFile = this.findElementFile(targetName);
		try {
			final Configuration config = Configuration.getInstance();
			return targetFile.getCanonicalPath().replace(
					config.getWorkspace().getCanonicalPath(), "");
		} catch (final Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * Translate the path separators to match the operating system.
	 * 
	 * @param targetPath
	 *            String.
	 * @return resultPath - String.
	 */
	private String adjustPathSeparators(final String targetPath) {
		if (File.separator.equals("/"))
			return targetPath.replace("\\", "/");
		else
			return targetPath.replace("/", "\\");
	}

}
