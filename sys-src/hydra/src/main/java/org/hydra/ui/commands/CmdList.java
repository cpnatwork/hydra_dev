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
import java.io.FileInputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hydra.core.Configuration;
import org.hydra.utilities.FilterInDirectories;
import org.hydra.utilities.FilterInFiles;

/**
 * Lists sub-directories and files in a directory or the contents of a file.
 *
 * @since 0.1
 * @version 0.2
 * @author Scott A. Hady
 */
public class CmdList extends CommandSystem {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The Constant DEFAULT_NAME. */
	public static final String DEFAULT_NAME = "System List";

	/** The Constant DEFAULT_ID. */
	public static final String DEFAULT_ID = "CmdList";

	/** The depth. */
	private int depth;

	/** The path. */
	private String path;

	/** The DEFAUL t_ depth. */
	private final int DEFAULT_DEPTH = 0;

	/** The DEFAUL t_ path. */
	private final String DEFAULT_PATH = ".";
	// Regular Expressions
	/** The cmd reg ex. */
	private final String cmdRegEx = "^\\s*(?i:list|ls)\\b\\s*";

	/** The depth reg ex. */
	private final String depthRegEx = "(\\s+-d(\\d+)\\b\\s*)?";

	/** The path reg ex. */
	private final String pathRegEx = "((\\s+)((?!\\s*-d).+)(\\\\|\\/)?)?\\s*$";

	/** The cmd pattern. */
	private final Pattern cmdPattern = Pattern.compile(this.cmdRegEx);

	/** The complete pattern. */
	private final Pattern completePattern = Pattern.compile(this.cmdRegEx
			+ this.depthRegEx + this.pathRegEx);

	/** The GROU p_ depth. */
	private final int GROUP_DEPTH = 2;

	/** The GROU p_ path. */
	private final int GROUP_PATH = 5;

	/**
	 * Default Constructor, uses a "." path extension and a depth of zero.
	 */
	public CmdList() {
		super(CmdList.DEFAULT_NAME, CmdList.DEFAULT_ID);
		this.depth = this.DEFAULT_DEPTH;
		this.path = this.DEFAULT_PATH;
	}

	/**
	 * Specialized Constructor, identifies the depth of directory recursion that
	 * will be included in the listing and the extension to the path to
	 * determine which directory to list.
	 *
	 * @param depth
	 *            int.
	 * @param path
	 *            String.
	 */
	public CmdList(final int depth, final String path) {
		super(CmdList.DEFAULT_NAME, CmdList.DEFAULT_ID);
		this.depth = depth;
		this.path = path;
	}

	/**
	 * COMMAND METHODS OVERRIDDEN *********************************************.
	 * 
	 * @return the command pattern
	 */

	/**
	 * {@inheritDoc}
	 *
	 * List Pattern accepts 'list' or 'ls' as the command.
	 */
	@Override
	public Pattern getCommandPattern() {
		return this.cmdPattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Complete List Pattern accepts 'list|ls| {-d[depth]} {[path]}.
	 */
	@Override
	public Pattern getCompletePattern() {
		return this.completePattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Process the matcher to extract the depth and path parameters if they
	 * exist.
	 */
	@Override
	public boolean processMatcher(final Matcher matcher) {
		this.depth = this.DEFAULT_DEPTH;
		this.path = this.DEFAULT_PATH;
		try {
			if (matcher.group(this.GROUP_DEPTH) != null) {
				this.depth = Integer.parseInt(matcher.group(this.GROUP_DEPTH));
			}
			if (matcher.group(this.GROUP_PATH) != null) {
				this.path = matcher.group(this.GROUP_PATH).trim();
			}
			return true;
		} catch (final Exception e) {
			this.logger.exception("Unable to Parse Matched Arguments.", e);
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * List the subdirectories and files in the current working directory.
	 */
	@Override
	public boolean execute() {
		final File target = new File(Configuration.getInstance()
				.getCurrentWorkingDirectory(), this.path);
		final StringBuilder sb = new StringBuilder("");
		if (target.exists()) {
			if (target.isDirectory()) {
				sb.append(this.listDirectoryContents(target, this.depth, 1));
			} else {
				sb.append(this.listFileContents(target));

			}
		} else {
			sb.append("Location [" + target + "] Not Found.\n");
		}
		this.writer.println(sb.toString(), this.cmdVerbosity);
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return a string describing the commands usage.
	 */
	@Override
	public String getUsage() {
		return "list(ls) {-d<depth>} {path}\t\tPrints List of Directories and Files in Workspace.";
	}

	/**
	 * CMDLIST PROTECTED METHODS **********************************************.
	 * 
	 * @param targetDirectory
	 *            the target directory
	 * @param depth
	 *            the depth
	 * @param spaces
	 *            the spaces
	 * @return the string
	 */

	/**
	 * Return a string listing the contents of a directory.
	 *
	 * @param targetDirectory
	 *            File.
	 * @param depth
	 *            int.
	 * @param spaces
	 *            int.
	 * @return directoryContent String.
	 */
	protected String listDirectoryContents(final File targetDirectory,
			final int depth, final int spaces) {
		final StringBuilder sb = new StringBuilder("");
		// Build Spacer
		final StringBuilder spacer = new StringBuilder("");
		for (int i = 0; i < spaces; i++) {
			spacer.append("   ");
		}
		// Include Directories - Recursive to Depth
		File[] files = targetDirectory.listFiles(new FilterInDirectories());
		for (final File f : files) {
			sb.append(spacer.toString() + f.getName() + "/\n");
			if (depth > 0) {
				sb.append(this.listDirectoryContents(f, depth - 1, spaces + 1));
			}
		}
		// Include Files
		files = targetDirectory.listFiles(new FilterInFiles());
		for (final File f : files) {
			sb.append(spacer.toString() + "-" + f.getName() + "\n");
		}
		return sb.toString();
	}

	/**
	 * Return a string listing the contents of a file.
	 *
	 * @param targetFile
	 *            File.
	 * @return fileContents - String.
	 */
	protected String listFileContents(final File targetFile) {
		Scanner scanner = null;
		final StringBuilder sb = new StringBuilder("");
		try {
			scanner = new Scanner(new FileInputStream(targetFile));
			while (scanner.hasNext()) {
				sb.append(scanner.nextLine() + "\n");
			}
		} catch (final Exception e) {
			sb.append("***EXCEPTION ENCOUNTERED: " + e);
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
		sb.append("\n");
		return sb.toString();
	}

}
