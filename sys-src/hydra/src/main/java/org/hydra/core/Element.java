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
import java.util.regex.Pattern;

import org.hydra.persistence.DataAccessObject;
import org.hydra.utilities.Logger;

/**
 * Base abstract unit within the version control system, which is used to
 * represent any unit that is maintained within the repository. The current
 * state or references of the element may either be recorded into the repository
 * or loaded from the repository.
 *
 * @author Scott A. Hady
 * @version 0.2
 * @since 0.1
 */
public abstract class Element {

	/** The invalid names reg ex. */
	private final String invalidNamesRegEx = "::>>|\\\\|\\/";

	/** The invalid pattern. */
	private final Pattern invalidPattern = Pattern
			.compile(this.invalidNamesRegEx);

	/**
	 * Unique Meta Data Token.
	 */
	public static final String TOKEN_METADATA = "MD";

	/**
	 * System configuration data.
	 */
	protected Configuration config = null;

	/** The logger. */
	protected Logger logger = null;

	/**
	 * Repository storage location.
	 */
	protected File repositoryFile = null;

	/**
	 * Provides access to the storage component which encapsulates the
	 * functionality for reading and writing data.
	 */
	protected DataAccessObject dao;

	/**
	 * Default constructor.
	 */
	public Element() {
		this.config = Configuration.getInstance();
		this.dao = DataAccessObject.createDAO(this);
		this.logger = Logger.getInstance();
	}

	/**
	 * Construct element based on the repository's content hash.
	 *
	 * @param contentHash
	 *            String.
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public Element(final String contentHash) throws InvalidElementException {
		this.config = Configuration.getInstance();
		this.dao = DataAccessObject.createDAO(this);
		this.logger = Logger.getInstance();
		this.repositoryFile = new File(this.config.getFPStore(), contentHash);
		if (!this.repositoryFile.exists()) {
			final String message = "Content Hash Not Found. [" + contentHash
					+ "]";
			this.logger.exception(message);
			throw new InvalidElementException(message);
		}
	}

	/**
	 * Instantiates a new element.
	 *
	 * @param repositoryLocation
	 *            the repository location
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public Element(final File repositoryLocation)
			throws InvalidElementException {
		this.config = Configuration.getInstance();
		this.dao = DataAccessObject.createDAO(this);
		this.logger = Logger.getInstance();
		this.repositoryFile = new File(this.config.getRepository(),
				repositoryLocation.getPath());
	}

	/**
	 * ELEMENT METHODS *****************************************************.
	 * 
	 * @return the file
	 */

	/**
	 * Retrieve a clonethe element's associated repository file.
	 *
	 * @return repositoryFile - File.
	 */
	public File cloneRepositoryFile() {
		return new File(this.repositoryFile.getPath());
	}

	/**
	 * Determine if the name is valid. Names are not allowed to have path
	 * separators or any of the other critical tokens (::>> or \\n).
	 *
	 * @param name
	 *            String.
	 * @return isValid - boolean.
	 */
	public boolean isValidName(final String name) {
		return !(this.invalidPattern.matcher(name).find());
	}

	/**
	 * ELEMENT METHODS (ABSTRACT) ******************************************.
	 * 
	 * @return the string
	 */

	/**
	 * Return a string describing the element.
	 *
	 * @return description - String.
	 */
	public abstract String describe();

	/**
	 * Return this element's complete string id, includes element token, name
	 * and fingerprint hash.
	 *
	 * @return tokenizedDescriptor - String.
	 */
	public abstract String getDescriptor();

	/**
	 * Return the name of the element.
	 *
	 * @return elementName - String.
	 */
	public abstract String getName();

	/**
	 * Return the current status of the element.
	 *
	 * @param workspace
	 *            boolean.
	 * @param repository
	 *            boolean.
	 * @return status - String.
	 */
	public abstract String getStatus(boolean workspace, boolean repository);

	/**
	 * OBJECT METHODS (OVERRIDDEN) *****************************************.
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
	public abstract boolean equals(Object comparedElement);

	/**
	 * {@inheritDoc}
	 *
	 * Return a string representation of the element.
	 */
	@Override
	public abstract String toString();

}
