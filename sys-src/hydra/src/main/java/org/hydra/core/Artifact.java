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
 * Specialized fingerprinted element that maintains content. Within a a file
 * system workspace it is a file, but is more generally considered a specific
 * versioned content element.
 *
 * @author Scott A. Hady
 * @version 0.2
 * @since 0.1
 */
public class Artifact extends FingerprintedElement {

	/**
	 * Unique Artifact Header.
	 */
	public static final String HEADER = "HVS::>>Item";

	/**
	 * Unique Artifact Token.
	 */
	public static final String TOKEN = "IT";

	/**
	 * Specialized Constructor, which uses the workspace file as its content.
	 *
	 * @param workspaceFile
	 *            File.
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public Artifact(final File workspaceFile) throws InvalidElementException {
		super(workspaceFile);
	}

	/**
	 * Specialized Constructor, which uses the workspace file as it working copy
	 * and the content from the content identified by the hash in the
	 * fingerprinted storage.
	 *
	 * @param workspaceFile
	 *            File.
	 * @param contentHash
	 *            String.
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public Artifact(final File workspaceFile, final String contentHash)
			throws InvalidElementException {
		super(workspaceFile, contentHash);
	}

	/**
	 * ELEMENT METHODS (OVERRIDDEN) ****************************************.
	 * 
	 * @return the string
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Return a description of this artifact.
	 */
	@Override
	public String describe() {
		return new StringBuilder(this.getDescriptor()).append(
				DataAccessObject.SEP_MEMBER).toString();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return this artifact's complete string id.
	 */
	@Override
	public String getDescriptor() {
		return new StringBuilder(Artifact.TOKEN)
				.append(DataAccessObject.SEP_TOKEN).append(this.getName())
				.append(DataAccessObject.SEP_TOKEN)
				.append(this.fingerprint.getHash()).toString();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return the artifact's name.
	 */
	@Override
	public String getName() {
		return this.workspaceFile.getName();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return the current status of the artifact.
	 */
	@Override
	public String getStatus(final boolean workspace, final boolean repository) {
		final StringBuilder sb = new StringBuilder("");
		if (workspace) {
			if (!this.workspaceFile.exists()) {
				sb.append("-");
			} else {
				sb.append(this.fingerprint.checkFingerprint(this.workspaceFile) ? "v"
						: "c");
			}
		}
		if (repository) {
			if (workspace) {
				sb.append(" ");
			}
			if (!this.repositoryFile.exists()) {
				sb.append("-");
			} else {
				sb.append((this.repositoryFile.exists()) ? "v" : "c");
			}
		}
		return sb.toString();
	}

	/**
	 * FINGERPRINTEDELEMENT METHODS (OVERRIDDEN) ******************************.
	 * 
	 * @return true, if successful
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Refresh the artifact's fingerprint to account for any changes to the
	 * content.
	 */
	@Override
	public boolean refreshFingerprint() {
		if (this.workspaceFile.exists()) {
			this.fingerprint.setHash(this.fingerprint
					.calculateHash(this.workspaceFile));
			return true;
		} else {
			this.logger.warning("Unable to refresh fingerprint because ["
					+ this.workspaceFile + "] does not exist.");
			this.fingerprint.setHash(null);
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return the artifact's designated persisted state.
	 */
	@Override
	public boolean retrieve() {
		return this.dao.retrieve();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Persist the artifact's current state.
	 */
	@Override
	public boolean store() {
		this.repositoryFile = new File(this.config.getFPStore(), this.getHash());
		return this.dao.store();
	}

}
