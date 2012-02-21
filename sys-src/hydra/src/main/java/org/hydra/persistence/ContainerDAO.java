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

import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

import org.hydra.core.Artifact;
import org.hydra.core.Container;
import org.hydra.core.FingerprintedElement;
import org.hydra.core.InvalidElementException;

/**
 * DAO implementation for the Container element.
 *
 * @author Scott A. Hady
 * @version 0.2
 * @since 0.2
 */
public class ContainerDAO extends DataAccessObject {

	/** The container. */
	private final Container container;

	/**
	 * Specialized Constructor that takes the Container on which the DAO will
	 * operate.
	 *
	 * @param container
	 *            Container.
	 */
	public ContainerDAO(final Container container) {
		this.container = container;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Load the container's sub-element references from the repository.
	 */
	@Override
	public boolean load() throws InvalidElementException {
		Scanner scanner = null;
		final File target = this.container.cloneRepositoryFile();
		try {
			scanner = new Scanner(new FileInputStream(target), "UTF-8");
			while (scanner.hasNextLine()) {
				final String[] splitLine = scanner.nextLine().split(
						DataAccessObject.SEP_TOKEN);
				if (splitLine[0].equals(Container.TOKEN)) {
					this.container.addElement(new Container(new File(
							this.container.cloneWorkspaceFile(), splitLine[1]),
							splitLine[2]));
				} else if (splitLine[0].equals(Artifact.TOKEN)) {
					this.container.addElement(new Artifact(new File(
							this.container.cloneWorkspaceFile(), splitLine[1]),
							splitLine[2]));
				}
			}
			return true;
		} catch (final Exception e) {
			this.logger.exception("Unable to Load Container [" + target + "].",
					e);
			return false;
		} finally {
			if (scanner != null) {
				try {
					scanner.close();
				} catch (final Exception e) {
					this.logger.exception("Unable to Close Scanner.", e);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * Record the current references to the repository.
	 */
	@Override
	public boolean record() {
		return this.storeContents(this.container.describe(),
				this.container.cloneRepositoryFile());
	}

	/**
	 * {@inheritDoc}
	 *
	 * Retrieve a persisted state of the container from the repository and
	 * restore it to the workspace. No operation, not relevant for the DAO, the
	 * Container holds references to the artifact's which must be retrieved.
	 */
	@Override
	public boolean retrieve() {
		boolean success = true;
		this.container.cloneWorkspaceFile().mkdir();
		for (final FingerprintedElement e : this.container.listElements()) {
			if (!e.retrieve()) {
				success = false;
			}
		}
		return success;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Store the current workspace state of the container into the repository.
	 */
	@Override
	public boolean store() {
		if (this.container.cloneRepositoryFile().exists())
			return true;
		else {
			boolean success = true;
			for (final FingerprintedElement subElement : this.container
					.listElements()) {
				if (!subElement.store()) {
					success = false;
				}
			}
			return this.record() && success;
		}
	}

}
