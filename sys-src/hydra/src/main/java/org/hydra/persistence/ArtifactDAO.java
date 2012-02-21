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
package org.hydra.persistence;

import org.hydra.core.Artifact;

/**
 * DAO implementation for the versioning core's artifact.
 *
 * @author Scott A. Hady
 * @version 0.2
 * @since 0.2
 */
public class ArtifactDAO extends DataAccessObject {

	/** The artifact. */
	private final Artifact artifact;

	/** The storage strategy. */
	private StorageStrategy storageStrategy;

	/**
	 * Specialized Constructor that accepts the artifact on which it should
	 * operate.
	 *
	 * @param artifact
	 *            Artifact.
	 */
	public ArtifactDAO(final Artifact artifact) {
		this.artifact = artifact;
		if (DataAccessObject.STORAGE_STRATEGY
				.equals(ZipStorageStrategyImpl.COMPRESSION_TYPE)) {
			this.storageStrategy = new ZipStorageStrategyImpl();
		} else if (DataAccessObject.STORAGE_STRATEGY
				.equals(GZipStorageStrategyImpl.COMPRESSION_TYPE)) {
			this.storageStrategy = new GZipStorageStrategyImpl();
		} else {
			this.storageStrategy = new NIOStorageStrategyImpl();
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * Load the artifact's references from the repository. No operation, not
	 * relevant for an artifact, will always return false.
	 */
	@Override
	public boolean load() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Record the artifact's referenced into the repository. No operation, not
	 * relevant for an artifact, will always return false.
	 */
	@Override
	public boolean record() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Retrieve the fingerprint designated persisted content (version) of the
	 * artifact from the repository and restore it to the workspace.
	 */
	@Override
	public boolean retrieve() {
		if (this.artifact.cloneRepositoryFile().exists())
			return this.storageStrategy.transferFromRepository(
					this.artifact.cloneWorkspaceFile(),
					this.artifact.cloneRepositoryFile());
		else
			return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Persist the current state of the artifact into the repository.
	 */
	@Override
	public boolean store() {
		if (this.artifact.cloneRepositoryFile().exists())
			return true;
		else
			return this.storageStrategy.transferToRepository(
					this.artifact.cloneWorkspaceFile(),
					this.artifact.cloneRepositoryFile());
	}

}
