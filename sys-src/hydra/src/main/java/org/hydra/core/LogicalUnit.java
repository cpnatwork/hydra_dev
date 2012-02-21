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

import org.hydra.persistence.DataAccessObject;

/**
 * Specialized committable element which represents a logically coherent
 * collection of elements within the workspace and maintains its history and and
 * provides independent state control.
 *
 * @author Scott A. Hady
 * @version 0.2
 * @since 0.1
 */
public class LogicalUnit extends CommittableElement implements Comparable {

	/**
	 * Unique Logical Unit Header.
	 */
	public static final String HEADER = "HH::>>LogicalUnit";

	/**
	 * Unique Logical State Token.
	 */
	public static final String TOKEN = "LS";

	// Logical Unit Variables//
	/** The lu name. */
	private String luName = null;

	/**
	 * Specialized Constructor, creates a new logical unit with the given name.
	 *
	 * @param luName
	 *            String.
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public LogicalUnit(final String luName) throws InvalidElementException {
		super(new File("luStore/" + luName));
		this.luName = luName;
		this.contents = new Container(this.config.getWorkspace());
		if (!this.repositoryFile.exists() || !this.loadReferences()) {
			this.recordReferences();
		}
	}

	/**
	 * Specialized Constructor, which loads a logical unit that has already been
	 * created, reverts it to a given state and sets the contents.
	 *
	 * @param luName
	 *            String.
	 * @param currentHash
	 *            String.
	 * @param contentHash
	 *            String.
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public LogicalUnit(final String luName, final String currentHash,
			final String contentHash) throws InvalidElementException {
		super(new File("luStore/" + luName));
		this.luName = luName;
		if (!this.loadReferences()) {
			final String message = "Unable to Reinitialize Logical Unit ["
					+ luName + "].";
			this.logger.exception(message);
			throw new InvalidElementException(message);
		} else {
			if (!currentHash.equals("null")) {
				this.revertHash(currentHash, true);
			}
			this.contents = new Container(this.config.getWorkspace(),
					contentHash);
		}
	}

	/**
	 * ELEMENT METHODS (OVERRIDDEN) *******************************************.
	 * 
	 * @return the string
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Return a string description of the logical unit which defines the head,
	 * current and content states.
	 */
	@Override
	public String describe() {
		return (new StringBuffer(LogicalUnit.HEADER))
				.append(DataAccessObject.SEP_MEMBER).append(super.describe())
				.toString();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return a short string descriptor of logical unit that may be used to
	 * store necessary references to the current state of the logical unit.
	 */
	@Override
	public String getDescriptor() {
		return new StringBuilder(LogicalUnit.TOKEN)
				.append(DataAccessObject.SEP_TOKEN).append(this.getName())
				.append(DataAccessObject.SEP_TOKEN)
				.append(this.getCurrentHash())
				.append(DataAccessObject.SEP_TOKEN)
				.append(this.getContentsHash()).toString();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return the name of the logical unit.
	 */
	@Override
	public String getName() {
		return this.luName;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Load the in-memory references of the logical unit from the repository.
	 * Typically used for the initialization of the logical unit and refreshing
	 * the logical unit to reflect the most actual state in the repository.
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

	/**
	 * {@inheritDoc}
	 *
	 * COMPARABLE METHODS (IMPLEMENTATION) ************************************.
	 */
	@Override
	public int compareTo(final Object o) {
		if (o instanceof LogicalUnit)
			return this.getName().compareTo(((LogicalUnit) o).getName());
		else
			return -1;
	}
}
