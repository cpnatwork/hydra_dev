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

/**
 * Facade simplifying the usage of the hydra version control system.
 *
 * @author Scott A. Hady
 * @version 0.2
 * @since 0.1
 */
public class HydraFacade {

	/** The stage. */
	private final Stage stage;

	/** The config. */
	private final Configuration config;

	/**
	 * Default Constructor, initializes the stage.
	 *
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public HydraFacade() throws InvalidElementException {
		this.stage = new Stage();
		this.config = Configuration.getInstance();
	}

	/**
	 * Specialized Constructor, initializes the stage oriented towards a
	 * specific workspace.
	 *
	 * @param workspace
	 *            File.
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public HydraFacade(final File workspace) throws InvalidElementException {
		this.stage = new Stage(workspace);
		this.config = Configuration.getInstance();
	}

	/**
	 * Retrieve the stage being used by the facade.
	 *
	 * @return stage - Stage.
	 */
	public Stage getStage() {
		return this.stage;
	}

	/**
	 * Create a new logical unit with the given name.
	 *
	 * @param luName
	 *            String.
	 * @return logicalUnit - LogicalUnit.
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public LogicalUnit createLogicalUnit(final String luName)
			throws InvalidElementException {
		return this.stage.createLogicalUnit(luName);
	}

	/**
	 * Delete designated logical unit from the facade's stage.
	 *
	 * @param luName
	 *            String.
	 * @return success - boolean.
	 */
	public boolean deleteLogicalUnit(final String luName) {
		return this.stage.deleteLogicalUnit(luName);
	}

	/**
	 * Add an element to a designated logical unit.
	 *
	 * @param luName
	 *            String.
	 * @param targetElement
	 *            File.
	 * @return success - boolean.
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public boolean addElement(final String luName, final File targetElement)
			throws InvalidElementException {
		if (targetElement.isFile())
			return this.stage.getLogicalUnit(luName).getContents()
					.addElement(new Artifact(targetElement));
		else
			return this.stage.getLogicalUnit(luName).getContents()
					.addElement(new Container(targetElement));
	}

	/**
	 * Remove an element from a designated logical unit.
	 *
	 * @param luName
	 *            String.
	 * @param targetElement
	 *            File.
	 * @return success - boolean.
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public boolean removeElement(final String luName, final File targetElement)
			throws InvalidElementException {
		if (targetElement.isFile())
			return this.stage.getLogicalUnit(luName).getContents()
					.removeElement(new Artifact(targetElement));
		else
			return this.stage.getLogicalUnit(luName).getContents()
					.removeElement(new Container(targetElement));
	}

	/**
	 * Commit the designated logical unit and associate the given message to the
	 * committed state.
	 *
	 * @param luName
	 *            String.
	 * @param message
	 *            String.
	 * @return commitHash - String.
	 */
	public String commit(final String luName, final String message) {
		return this.stage.getLogicalUnit(luName).commitValidPath(
				this.config.getUserId(), message);
	}

	/**
	 * Inserts the current content's state between the two designated states and
	 * sets current to the new created states. The states given do not need to
	 * be consecutive.
	 *
	 * @param luName
	 *            String.
	 * @param message
	 *            String.
	 * @param prevHash
	 *            String.
	 * @param nextHash
	 *            String.
	 * @return commitHash - String.
	 */
	public String commitInsert(final String luName, final String message,
			final String prevHash, final String nextHash) {
		return this.stage.getLogicalUnit(luName).commitInsert(
				this.config.getUserId(), message, prevHash, nextHash);
	}

	/**
	 * Creates a temporary commit placeholder that is expected to be later
	 * replace or the content to be updated. The temporary commit's content is
	 * null.
	 *
	 * @param luName
	 *            String.
	 * @param message
	 *            String.
	 * @return commitHash - String.
	 */
	public String commitTemporary(final String luName, final String message) {
		return this.stage.getLogicalUnit(luName).commitTemporary(
				this.config.getUserId(), message);
	}

	/**
	 * Update a previously committed state with the current content of the
	 * logical unit.
	 *
	 * @param luName
	 *            String.
	 * @param commitHash
	 *            String.
	 * @param message
	 *            String.
	 * @return success - boolean.
	 */
	public boolean commitUpdate(final String luName, final String commitHash,
			final String message) {
		return this.stage.getLogicalUnit(luName).commitUpdate(commitHash,
				Configuration.getInstance().getUserId(), message);
	}

	/**
	 * Revert the designated logical unit to a specified commit hash. If the
	 * depthFirst boolean is true then it will search for the commit from the
	 * HEAD state in a depth first manner, otherwise it will search breadth
	 * first.
	 *
	 * @param luName
	 *            String.
	 * @param commitHash
	 *            String.
	 * @return success - boolean.
	 */
	public boolean revertHash(final String luName, final String commitHash) {
		return this.stage.getLogicalUnit(luName).revert(commitHash);
	}

	/**
	 * Revert the designated logical unit along the specified branch for a given
	 * distance, relative to the CURRENT state.
	 *
	 * @param luName
	 *            String.
	 * @param branch
	 *            int.
	 * @param distance
	 *            int.
	 * @return success - boolean.
	 */
	public boolean revertRelative(final String luName, final int branch,
			final int distance) {
		return this.stage.getLogicalUnit(luName).revertRelative(branch,
				distance);
	}

	/**
	 * Revert the designated logical unit along the specified path from the HEAD
	 * state.
	 *
	 * @param luName
	 *            String.
	 * @param revertPath
	 *            String.
	 * @return success - boolean.
	 */
	public boolean revertPath(final String luName, final String revertPath) {
		return this.stage.getLogicalUnit(luName).revertPath(
				new Path(revertPath));
	}

	/**
	 * Retrieve a string describing the logical unit's commit log history.
	 *
	 * @param luName
	 *            String.
	 * @param systemPath
	 *            boolean.
	 * @return log - String.
	 */
	public String getHistoryLog(final String luName, final boolean systemPath) {
		final StringBuilder sb = new StringBuilder("\t\t" + luName
				+ " Commit Log\n");
		sb.append("  --------------------------------------------------\n");
		sb.append(this.stage.getLogicalUnit(luName).getHistoryCrawler()
				.getHistoryLog(systemPath));
		return sb.toString();
	}

}
