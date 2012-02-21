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

import java.util.HashMap;

import org.hydra.persistence.DataAccessObject;

/**
 * Memento of a specific state of a stage, which may be later restored or
 * reverted to. This is equivalent to a commit in most version control systems.
 *
 * @author Scott A. Hady
 * @version 0.2
 * @since 0.2
 */
public class StageState extends State {

	/** The lu references. */
	private HashMap<String, String> luReferences;

	/** The stage focus. */
	private String stageFocus;

	/** The stage. */
	private Stage stage;

	/**
	 * Specialized constructory that creates a fingerprinted stage state from a
	 * given stage which always maintains just the current has the previous
	 * state.
	 *
	 * @param stage
	 *            Stage.
	 * @param userId
	 *            String.
	 * @param commitMessage
	 *            String.
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public StageState(final Stage stage, final String userId,
			final String commitMessage) throws InvalidElementException {
		super(stage.getHead(), stage.getContents(), userId, commitMessage);
		this.stage = stage;
	}

	/**
	 * Specialized constructory that creates a fingerprinted stage state from a
	 * given stage and sets the specified state as the previous valid path
	 * state.
	 *
	 * @param stage
	 *            Stage.
	 * @param previousState
	 *            StageState.
	 * @param userId
	 *            String.
	 * @param commitMessage
	 *            String.
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public StageState(final Stage stage, final StageState previousState,
			final String userId, final String commitMessage)
			throws InvalidElementException {
		super(previousState, stage.getContents(), userId, commitMessage);
		this.stage = stage;
	}

	/**
	 * Specialized constructor that retrieved the identified stage state from
	 * the repository.
	 *
	 * @param commitHash
	 *            String.
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public StageState(final String commitHash) throws InvalidElementException {
		super(commitHash);
	}

	/**
	 * STAGESTATE METHODS *****************************************************.
	 * 
	 * @param luName
	 *            the lu name
	 * @param currentHash
	 *            the current hash
	 * @return true, if successful
	 */

	/**
	 * Adds a logical unit reference to the state.
	 *
	 * @param luName
	 *            String.
	 * @param currentHash
	 *            String.
	 * @return a boolean.
	 */
	public boolean loadLogicalUnitReference(final String luName,
			final String currentHash) {
		try {
			if (this.luReferences == null) {
				this.luReferences = new HashMap<String, String>();
			}
			this.luReferences.put(luName, currentHash);
			return true;
		} catch (final Exception e) {
			this.logger.exception(
					"Unable to Record Reference to Logical Unit [" + luName
							+ "].", e);
			return false;
		}
	}

	/**
	 * Loads the logical unit references derived from the stored state, reverts
	 * them to appropriate current state and returns them. This lazy loading
	 * reduces the overhead during reverts and searching through states.
	 *
	 * @return logicalUnits - HashMap<String, LogicalUnits>.
	 */
	public HashMap<String, LogicalUnit> revertLogicalUnits() {
		final HashMap<String, LogicalUnit> logicalUnits = new HashMap<String, LogicalUnit>();
		for (final String luName : this.luReferences.keySet()) {
			try {
				final LogicalUnit lu = new LogicalUnit(luName);
				lu.revertHash(this.luReferences.get(luName), true);
				logicalUnits.put(luName, lu);
			} catch (final Exception e) {
				this.logger.exception("Unable to Restore Logical Unit ["
						+ luName + "].", e);
			}
		}
		return logicalUnits;
	}

	/**
	 * STATE METHODS (OVERRIDDEN) *******************************************.
	 * 
	 * @return the string
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Returns a string describing the logical units maintained by the stage in
	 * this state. It fulfills the necessary extension of the template
	 * {@link #describe} method implemented in the {@link State} class.
	 */
	@Override
	protected String describeExtension() {
		final StringBuilder sb = new StringBuilder("");
		if (this.stage != null) {
			for (final LogicalUnit lu : this.stage.listManaged()) {
				sb.append(LogicalUnit.TOKEN).append(DataAccessObject.SEP_TOKEN)
						.append(lu.getName())
						.append(DataAccessObject.SEP_TOKEN)
						.append(lu.getCurrentHash())
						.append(DataAccessObject.SEP_MEMBER);
			}
		}
		return sb.toString();
	}

}
