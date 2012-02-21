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

import org.hydra.core.Artifact;
import org.hydra.core.Configuration;
import org.hydra.core.Container;
import org.hydra.core.FingerprintedElement;
import org.hydra.core.Stage;

/**
 * Adds an element to the designated logical unit.
 *
 * @since 0.1
 * @version 0.2
 * @author Scott A. Hady
 */
public class LUCmdElementAdd extends CommandLogicalUnit {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The Constant DEFAULT_NAME. */
	public static final String DEFAULT_NAME = "LogicalUnit Add Element";

	/** The Constant DEFAULT_ID. */
	public static final String DEFAULT_ID = "LUCmdElementAdd";

	/** The e name. */
	private String eName;

	/** The recursive. */
	private boolean recursive;

	/** The config. */
	private final Configuration config = Configuration.getInstance();
	// Regular Expressions
	/** The cmd reg ex. */
	private final String cmdRegEx = "^\\s*(?i:luadd)\\b";

	/** The lu reg ex. */
	private final String luRegEx = "((\\s+)(\\S+.*))?\\s+-(e(r)?|(r)?e)\\b";

	/** The el reg ex. */
	private final String elRegEx = "\\s+(\\S+.*)(\\\\|\\/)?\\s*$";

	/** The cmd pattern. */
	private final Pattern cmdPattern = Pattern.compile(this.cmdRegEx);

	/** The complete pattern. */
	private final Pattern completePattern = Pattern.compile(this.cmdRegEx
			+ this.luRegEx + this.elRegEx);

	/** The GROU p_ logicalunit. */
	private final int GROUP_LOGICALUNIT = 3;

	/** The GROU p_ element. */
	private final int GROUP_ELEMENT = 7;

	/**
	 * Specialized Constructor which designates which stage use to find the
	 * logical unit's to add an element.
	 *
	 * @param stage
	 *            Stage.
	 */
	public LUCmdElementAdd(final Stage stage) {
		super(LUCmdElementAdd.DEFAULT_NAME, LUCmdElementAdd.DEFAULT_ID, stage);
	}

	/**
	 * Specialized constructor; which specifies the stage, logical unit's name
	 * and element's name to use.
	 *
	 * @param stage
	 *            Stage.
	 * @param luName
	 *            String.
	 * @param eName
	 *            String.
	 */
	public LUCmdElementAdd(final Stage stage, final String luName,
			final String eName) {
		super(LUCmdElementAdd.DEFAULT_NAME, LUCmdElementAdd.DEFAULT_ID, stage,
				luName);
		this.eName = eName;
		this.recursive = false;
	}

	/**
	 * Specialized constructor; which specifies the stage, logical unit's name,
	 * element's name to use and if the element's content should be recursively
	 * added.
	 *
	 * @param stage
	 *            Stage.
	 * @param luName
	 *            String.
	 * @param eName
	 *            String.
	 * @param recursive
	 *            boolean.
	 */
	public LUCmdElementAdd(final Stage stage, final String luName,
			final String eName, final boolean recursive) {
		super(LUCmdElementAdd.DEFAULT_NAME, LUCmdElementAdd.DEFAULT_ID, stage,
				luName);
		this.eName = eName;
		this.recursive = recursive;
	}

	/**
	 * COMMAND METHODS OVERRIDDEN *********************************************.
	 * 
	 * @return the command pattern
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Command Pattern accepts 'luadd' as the command.
	 */
	@Override
	public Pattern getCommandPattern() {
		return this.cmdPattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Factory Method - Complete Pattern accepts 'luadd {[luname]} -e{r}
	 * [ename]'.
	 */
	@Override
	public Pattern getCompletePattern() {
		return this.completePattern;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Template Method - Process the matcher to extract the logical unit,
	 * recursive flag and element name.
	 */
	@Override
	public boolean processMatcher(final Matcher matcher) {
		this.setRecursive(matcher);
		this.eName = matcher.group(this.GROUP_ELEMENT).trim();
		return this.processLogicalUnitName(matcher
				.group(this.GROUP_LOGICALUNIT));
	}

	/**
	 * Determine if element and its content should be added recursively.
	 * 
	 * @param matcher
	 *            Matcher.
	 */
	private void setRecursive(final Matcher matcher) {
		if ((matcher.group(5) != null) || (matcher.group(6) != null)) {
			this.recursive = true;
		} else {
			this.recursive = false;
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * Add the designated element to the designated logical unit.
	 */
	@Override
	public boolean execute() {
		boolean success = true;
		final File eFile = this.findElementFile(this.eName);
		FingerprintedElement element = null;
		try {
			if (eFile.isFile()) {
				element = new Artifact(eFile);
				success = this.stage.getLogicalUnit(this.luName).getContents()
						.addElement(element);
			} else {
				element = new Container(eFile);
				if (this.recursive) {
					success = this.stage.getLogicalUnit(this.luName)
							.getContents()
							.addContainerAndContents((Container) element);
				} else {
					success = this.stage.getLogicalUnit(this.luName)
							.getContents().addElement(element);
				}
			}
			this.stage.getLogicalUnit(this.luName).recordReferences();
		} catch (final Exception e) {
			this.logger.exception("Unable to Add Element.", e);
			success = false;
		}
		if (success) {
			this.writer.println("Element [" + this.eName
					+ "] added to Logical Unit [" + this.luName + "].\n",
					this.cmdVerbosity);
		} else {
			this.writer.println("Unable to Add Element [" + this.eName
					+ "] to Logical Unit [" + this.luName + "].",
					this.cmdVerbosity);
			this.writer.println("\tFile [" + eFile + "] "
					+ (eFile.exists() ? "Does" : "Does Not") + " Exist.\n",
					this.cmdVerbosity);
		}
		return success;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return a string describing the commands usage.
	 */
	@Override
	public String getUsage() {
		return "luAdd {<luName>} -e{r} <eName>\t\tAdds an Element to Logical Unit.";
	}

}
