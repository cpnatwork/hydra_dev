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
import java.util.ArrayList;
import java.util.HashMap;

import org.hydra.persistence.DataAccessObject;
import org.hydra.utilities.FilterInFiles;
import org.hydra.utilities.FilterOutHidden;

/**
 * Starting point for the Hydra Versioning System and specialized historied
 * element which maintains references to all active logical units within the
 * system. Committing the stage creates a system state which is defined by the
 * state of all maintained logical units and the stage's system content.
 *
 * @author Scott A. Hady
 * @version 0.2
 * @since 0.1
 */
public class Stage extends CommittableElement {

	/** The Constant HEADER. */
	public static final String HEADER = "HH::>>Stage";

	/** The Constant TOKEN. */
	public static final String TOKEN = "STAGE";

	/** The stage name. */
	private final String stageName = "STAGE";

	/** The logical units. */
	private final HashMap<String, LogicalUnit> logicalUnits = new HashMap<String, LogicalUnit>();

	/** The focus. */
	private LogicalUnit focus = null;

	/**
	 * Default Constructor, initializes with the workspace set in the
	 * configuration.
	 *
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public Stage() throws InvalidElementException {
		super(new File("STAGE"));
		this.config.initializeSystem(this.config.getWorkspace());
		this.initializeStage();

	}

	/**
	 * Specialized Constructor, that set the workspace and repository in the
	 * configuration before initializing.
	 *
	 * @param workspace
	 *            File.
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public Stage(final File workspace) throws InvalidElementException {
		super(new File("STAGE"));
		this.config.initializeSystem(workspace);
		this.repositoryFile = new File(this.config.getRepository(),
				this.stageName);
		this.initializeStage();
	}

	/**
	 * Initializes the system to be use a specific workspace and ensure that the
	 * workspace contains a correctly configured repository.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	private void initializeStage() throws InvalidElementException {
		if (this.repositoryFile.exists()) {
			this.loadReferences();
		} else {
			this.initializeNewStage();
		}
	}

	/**
	 * Initializes a new stage for the system that maintains all logical units
	 * found within the repository and has an empty contents oriented at the
	 * system's workspace.
	 * 
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	private void initializeNewStage() throws InvalidElementException {
		// Load All Logical Units.
		for (final File f : this.config.getLUStore().listFiles(
				new FilterOutHidden())) {
			try {
				final LogicalUnit lu = new LogicalUnit(f.getName());
				this.logicalUnits
						.put(f.getName(), new LogicalUnit(f.getName()));
			} catch (final InvalidElementException e) {
				this.logger.exception("Unable to Initialize Stage.", e);
			}
		}
		// Create New Contents
		this.contents = new Container(this.config.getWorkspace());
		this.contents.store();
		// Record
		this.recordReferences();
	}

	/**
	 * STAGE METHODS **********************************************************.
	 * 
	 * @return the int
	 */

	/**
	 * Return the number of logical units currently being manipulated in the
	 * system.
	 *
	 * @return countLogicalUnits - int.
	 */
	public int countManaged() {
		return this.logicalUnits.size();
	}

	/**
	 * Create a new logical unit or initialize a logical unit if there is one
	 * with the given name already created and begin managing the logical unit.
	 *
	 * @param luName
	 *            String.
	 * @return logicalUnit - LogicalUnit.
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public LogicalUnit createLogicalUnit(final String luName)
			throws InvalidElementException {
		LogicalUnit lu;
		if (!this.logicalUnitExists(luName)) {
			lu = new LogicalUnit(luName);
			this.manage(lu);
			this.recordReferences();
		} else { // If Present Log Warning and Return.
			lu = new LogicalUnit(luName);
			this.logger.warning("LogicalUnit Name already Used [" + luName
					+ "]");
		}
		return lu;
	}

	/**
	 * Logical unit exists.
	 * 
	 * @param luName
	 *            the lu name
	 * @return true, if successful
	 */
	private boolean logicalUnitExists(final String luName) {
		return new File(this.config.getLUStore() + File.separator + luName)
				.exists();
	}

	/**
	 * Delete a given logical unit if it exists.
	 *
	 * @param name
	 *            String.
	 * @return success - boolean.
	 */
	public boolean deleteLogicalUnit(final String name) {
		this.logicalUnits.remove(name);
		this.recordReferences();
		return new File(this.config.getLUStore(), name).delete();
	}

	/**
	 * Get the logical unit that is currently the focus of the stage.
	 *
	 * @return focus - LogicalUnit.
	 */
	public LogicalUnit getFocus() {
		return this.focus;
	}

	/**
	 * Retrieve a logical unit by name. Returns null if not found.
	 *
	 * @param name
	 *            String.
	 * @return requestedLogicalUnit - LogicalUnit.
	 */
	public LogicalUnit getLogicalUnit(final String name) {
		return this.logicalUnits.get(name);
	}

	/**
	 * Determine if the stage is currently focused on a logical unit.
	 *
	 * @return isFocused - boolean.
	 */
	public boolean isFocused() {
		return (this.focus != null);
	}

	/**
	 * Retrieve an array of all the maintained logical units.
	 *
	 * @return logicalUnits - LogicalUnit[].
	 */
	public LogicalUnit[] listManaged() {
		return this.logicalUnits.values().toArray(
				new LogicalUnit[this.logicalUnits.size()]);
	}

	/**
	 * Retrieve an array of all the unmanaged logical units, those that are in
	 * the system but are not currently managed by this stage and may be added.
	 *
	 * @return unmanagedLogicalUnits - LogicalUnit[].
	 */
	public LogicalUnit[] listUnmanaged() {
		final ArrayList<LogicalUnit> unmanaged = new ArrayList<LogicalUnit>();
		for (final File luFile : this.config.getLUStore().listFiles(
				new FilterInFiles())) {
			if (!this.isManaging(luFile.getName())) {
				try {
					unmanaged.add(new LogicalUnit(luFile.getName()));
				} catch (final Exception e) {
					this.logger.exception(
							"Unable to Initialize Unmanaged Logical Unit ["
									+ luFile + "].", e);
				}
			}
		}
		return unmanaged.toArray(new LogicalUnit[unmanaged.size()]);
	}

	/**
	 * Start managing a given logical unit in the system.
	 *
	 * @param logicalUnit
	 *            LogicalUnit.
	 * @return success - boolean.
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public boolean manage(final LogicalUnit logicalUnit)
			throws InvalidElementException {
		if (logicalUnit != null) {
			this.logicalUnits.put(logicalUnit.getName(), logicalUnit);
			return this.recordReferences();
		} else {
			this.logger.warning("Attempting to Manage a Null Pointer.");
			return false;
		}
	}

	/**
	 * Determine if the a logical unit with the given name is being managed on
	 * this stage.
	 *
	 * @param luName
	 *            String.
	 * @return isManaging - boolean.
	 */
	public boolean isManaging(final String luName) {
		return this.logicalUnits.containsKey(luName);
	}

	/**
	 * Stop managing the given logical unit on this stage.
	 *
	 * @param luName
	 *            String.
	 * @return logicalUnit - LogicalUnit.
	 */
	public LogicalUnit ignore(final String luName) {
		final LogicalUnit lu = this.logicalUnits.remove(luName);
		if (lu != null) {
			if (this.isFocused() && this.getFocus().equals(lu)) {
				this.setFocus(null);
			}
			this.recordReferences();
		}
		return lu;
	}

	/**
	 * Sets the logical units to the given set.
	 * 
	 * @param logicalUnits
	 *            HashMap<String, LogicalUnit>.
	 */
	private void setLogicalUnits(final HashMap<String, LogicalUnit> logicalUnits) {
		this.logicalUnits.clear();
		this.logicalUnits.putAll(logicalUnits);
	}

	/**
	 * Set the logical unit to be the stage's focus.
	 *
	 * @param focus
	 *            LogicalUnit.
	 */
	public void setFocus(final LogicalUnit focus) {
		this.focus = focus;
		this.recordReferences();
	}

	/**
	 * COMMIT METHODS *********************************************************.
	 * 
	 * @param userId
	 *            the user id
	 * @param commitMessage
	 *            the commit message
	 * @return the string
	 */

	/**
	 * Commit all managed logical units and return a string of commit value
	 * pairs.
	 *
	 * @param userId
	 *            String.
	 * @param commitMessage
	 *            String.
	 * @return commitNameValuePairs - String.
	 */
	public String commitLogicalUnits(final String userId,
			final String commitMessage) {
		final StringBuilder sb = new StringBuilder("");
		for (final LogicalUnit lu : this.logicalUnits.values()) {
			final String commitHash = lu.commit(userId, commitMessage);
			sb.append(lu.getName() + "=" + commitHash + ",");
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - 1);
		}
		return sb.toString();
	}

	/**
	 * Commit all managed logical units and return a string of commit value
	 * pairs.
	 *
	 * @param userId
	 *            String.
	 * @param commitMessage
	 *            String.
	 * @return commitNameValuePairs - String.
	 */
	public String commitValidPathLogicalUnits(final String userId,
			final String commitMessage) {
		final StringBuilder sb = new StringBuilder("");
		for (final LogicalUnit lu : this.logicalUnits.values()) {
			final String commitHash = lu.commitValidPath(userId, commitMessage);
			sb.append(lu.getName() + "=" + commitHash + ",");
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - 1);
		}
		return sb.toString();
	}

	/**
	 * Convience Method that commits all managed logical units, which stage
	 * marker appended to the message and then commits the stage.
	 *
	 * @param userId
	 *            String.
	 * @param commitMessage
	 *            String.
	 * @return commitHash - String.
	 */
	public String commitStageAndLogicalUnits(final String userId,
			final String commitMessage) {
		this.commitLogicalUnits(userId, "(STAGE)" + commitMessage);
		return this.commit(userId, commitMessage);
	}

	/**
	 * Convience Method that commits all managed logical units validly, with
	 * stage marker appended to the message and then commits the stage validly.
	 *
	 * @param userId
	 *            String.
	 * @param commitMessage
	 *            String.
	 * @return commitHash - String.
	 */
	public String commitValidPathStageAndLogicalUnits(final String userId,
			final String commitMessage) {
		this.commitValidPathLogicalUnits(userId, "(STAGE)" + commitMessage);
		return this.commitValidPath(userId, commitMessage);
	}

	/**
	 * REVERT METHODS *********************************************************.
	 * 
	 * @return true, if successful
	 */

	/**
	 * Revert all of the logical units workspaces to their current hash.
	 *
	 * @return success - boolean.
	 */
	public boolean revertLogicalUnits() {
		boolean success = true;
		for (final LogicalUnit lu : this.logicalUnits.values()) {
			if (!lu.revertHash(lu.getCurrentHash(), true)) {
				success = false;
			}
		}
		return success;
	}

	/**
	 * Revert the stage's content and return the state's logical unit's but do
	 * not revert each of the logical units.
	 *
	 * @param commitHash
	 *            String.
	 * @param depthFirst
	 *            boolean
	 * @return success - boolean.
	 */
	public boolean revertStage(final String commitHash, final boolean depthFirst) {
		try {
			final State target = this.historyCrawler.findCommitHash(this.head,
					commitHash, null, depthFirst);
			final StageState targetStage = this.stateToStageState(target);
			this.setLogicalUnits(targetStage.revertLogicalUnits());
			return this.setCurrent(targetStage);
		} catch (final Exception e) {
			this.logger.exception("Unable to Revert Stage to [" + commitHash
					+ "].", e);
			return false;
		}
	}

	/**
	 * Convience method that first reverts the stage to a given hash and then
	 * then reverts all of the logical units to the stage's new designated
	 * current hashes.
	 *
	 * @param commitHash
	 *            String.
	 * @param depthFirst
	 *            boolean
	 * @return success - boolean.
	 */
	public boolean revertStageAndLogicalUnits(final String commitHash,
			final boolean depthFirst) {
		return this.revertHash(commitHash, depthFirst)
				&& this.revertLogicalUnits();
	}

	/**
	 * ELEMENT METHODS (OVERRIDDEN) *******************************************.
	 * 
	 * @return the string
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Return a string description of the stage which defines the head, current,
	 * contents and maintained logical units. The description is typically
	 * maintained in the reference file.
	 */
	@Override
	public String describe() {
		final StringBuilder sb = new StringBuilder(Stage.HEADER)
				.append(DataAccessObject.SEP_MEMBER);
		sb.append(super.describe());
		for (final LogicalUnit lu : this.logicalUnits.values()) {
			sb.append(LogicalUnit.TOKEN).append(DataAccessObject.SEP_TOKEN)
					.append(lu.getName());
			if (this.isFocused() && this.getFocus().equals(lu)) {
				sb.append(DataAccessObject.SEP_TOKEN).append("FOCUS");
			}
			sb.append(DataAccessObject.SEP_MEMBER);
		}
		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return a short string descriptor of the stage which may be used to store
	 * the state of the stage.
	 */
	@Override
	public String getDescriptor() {
		return new StringBuilder(Stage.TOKEN)
				.append(DataAccessObject.SEP_TOKEN).append(this.getHeadHash())
				.append(DataAccessObject.SEP_TOKEN)
				.append(this.getCurrentHash())
				.append(DataAccessObject.SEP_TOKEN)
				.append(this.getContentsHash()).toString();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return the name of the stage.
	 */
	@Override
	public String getName() {
		return "STAGE";
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return the status of the stage's state in the workspace and the
	 * repository.
	 */
	@Override
	public String getStatus(final boolean workspace, final boolean repository) {
		final ArrayList<String> stageStatusList = new ArrayList<String>();
		final StringBuilder sb = new StringBuilder("");
		// Workspace Status
		if (workspace) {
			stageStatusList.add(super.getStatus(true, false));
			for (final LogicalUnit lu : this.logicalUnits.values()) {
				stageStatusList.add(lu.getStatus(true, false));
			}
			sb.append(super.getWorstStatus(stageStatusList));
		}
		// Repository Status
		if (repository) {
			if (workspace) {
				sb.append(" ");
			}
			stageStatusList.clear();
			stageStatusList.add(super.getStatus(false, true));
			for (final LogicalUnit lu : this.logicalUnits.values()) {
				stageStatusList.add(lu.getStatus(false, true));
			}
			sb.append(super.getWorstStatus(stageStatusList));
		}
		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Load the in-memory reference of the stage from the repository. Typically
	 * used to initialize the stage or refresh the stage if the repository
	 * reference file has been altered either manually or by another stage
	 * operating on the same repository.
	 */
	@Override
	public boolean loadReferences() throws InvalidElementException {
		return this.dao.load();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Record/Overwrite the current references into the repository so that they
	 * may be retrieved at a later time.
	 */
	@Override
	public boolean recordReferences() {
		this.stashContents();
		return this.dao.record();
	}

}
