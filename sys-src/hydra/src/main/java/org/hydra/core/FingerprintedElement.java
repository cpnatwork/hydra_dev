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
 * Abstract unit that is used to represent elements or state of the workspace
 * where the current state of the element is defined by its assigned
 * fingerprint. These elements may be stored in or retrieved from the repository
 * according to their fingerprint. Two elements that have the same fingerprint
 * hash are assumed to be the equal or represent the same contents.
 *
 * @author Scott A. Hady
 * @version 0.2
 * @since 0.2
 */
public abstract class FingerprintedElement extends Element {

	/** The fingerprint. */
	protected Fingerprint fingerprint = new Fingerprint();

	/** The workspace file. */
	protected File workspaceFile = null;

	/**
	 * Default Constructor - Needed For States.
	 */
	public FingerprintedElement() {
		super();
	}

	/**
	 * Specialized Constructor that takes the fingerprint of the workspace file
	 * in order to define the repository file.
	 *
	 * @param workspaceFile
	 *            File.
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public FingerprintedElement(final File workspaceFile)
			throws InvalidElementException {
		super();
		if (!workspaceFile.exists()) {
			final String message = "Workspace File Not Found [" + workspaceFile
					+ "].";
			this.logger.exception(message);
			throw new InvalidElementException(message);
		} else if (!this.isValidName(workspaceFile.getName())) {
			final String message = "Invalid Workspace File Name ["
					+ workspaceFile + "].";
			this.logger.exception(message);
			throw new InvalidElementException(message);
		}
		this.fingerprint = new Fingerprint(workspaceFile);
		this.workspaceFile = workspaceFile;
		this.repositoryFile = new File(this.config.getFPStore(),
				this.fingerprint.getHash());
	}

	/**
	 * Specialized Constructor that maintains a reference to a given
	 * fingerprinted content in the repository.
	 *
	 * @param fingerprintHash
	 *            String.
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public FingerprintedElement(final String fingerprintHash)
			throws InvalidElementException {
		super(fingerprintHash);
		this.fingerprint.setHash(fingerprintHash);
	}

	/**
	 * Spcialized Constructor that links a given file in the workspace to a
	 * given fingerprinted content stored in the repository.
	 *
	 * @param workspaceFile
	 *            File.
	 * @param fingerprintHash
	 *            String.
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public FingerprintedElement(final File workspaceFile,
			final String fingerprintHash) throws InvalidElementException {
		super(fingerprintHash);
		if (!this.isValidName(workspaceFile.getName())) {
			final String message = "Invalid Workspace File Name ["
					+ workspaceFile + "].";
			this.logger.exception(message);
			throw new InvalidElementException(message);
		}
		this.fingerprint.setHash(fingerprintHash);
		this.workspaceFile = workspaceFile;
	}

	/**
	 * ABSTRACT FINGERPRINTED METHODS *****************************************.
	 * 
	 * @return true, if successful
	 */

	/**
	 * Persist the element's current state.
	 *
	 * @return success - boolean.
	 */
	public abstract boolean store();

	/**
	 * Return the element's designated persisted state.
	 *
	 * @return success - boolean.
	 */
	public abstract boolean retrieve();

	/**
	 * FINGERPRINTEDELEMENT METHODS *******************************************.
	 * 
	 * @return the file
	 */

	/**
	 * Retrieve a clone of the element's associated workspace file.
	 *
	 * @return workspaceFile - File.
	 */
	public File cloneWorkspaceFile() {
		return new File(this.workspaceFile.getPath());
	}

	/**
	 * Return the element's content fingerprint.
	 *
	 * @return fingerprint - Fingerprint.
	 */
	public Fingerprint getFingerprint() {
		return this.fingerprint;
	}

	/**
	 * Convenience Method equal to getFingerprint().getHash().
	 *
	 * @return fpHash - String.
	 */
	public String getHash() {
		return this.fingerprint.getHash();
	}

	/**
	 * Refresh the element's fingerprint to account for any changed content.
	 *
	 * @return success - boolean.
	 */
	public abstract boolean refreshFingerprint();

	/**
	 * OVERRIDDEN OBJECT METHODS **********************************************.
	 * 
	 * @param comparedElement
	 *            the compared element
	 * @return true, if successful
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Determine if the content of this element is equal to the content of a
	 * given element.
	 */
	@Override
	public boolean equals(final Object comparedElement) {
		if (comparedElement == null)
			return false;
		else
			return this.fingerprint
					.equals(((FingerprintedElement) comparedElement)
							.getFingerprint());
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return a string representation of the element.
	 */
	@Override
	public String toString() {
		final String splitClassName[] = this.getClass().getName().split("\\.");
		return splitClassName[splitClassName.length - 1] + ":"
				+ this.fingerprint.getHash() + "[" + this.workspaceFile + "]";
	}

}
