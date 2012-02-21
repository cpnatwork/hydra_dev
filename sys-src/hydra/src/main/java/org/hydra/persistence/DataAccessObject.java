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
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.hydra.core.Artifact;
import org.hydra.core.Container;
import org.hydra.core.Element;
import org.hydra.core.InvalidElementException;
import org.hydra.core.LogicalUnit;
import org.hydra.core.Stage;
import org.hydra.core.StageState;
import org.hydra.core.State;
import org.hydra.utilities.Logger;

/**
 * Interface describing the functionality of the version core's data access
 * objects.
 *
 * @author Scott A. Hady
 * @version 0.2
 * @since 0.2
 */
public abstract class DataAccessObject {

	/** The Constant SEP_MEMBER. */
	public static final String SEP_MEMBER = "\n";

	/** The Constant SEP_TOKEN. */
	public static final String SEP_TOKEN = "::>>";

	/** The Constant STORAGE_STRATEGY. */
	public static final String STORAGE_STRATEGY = ZipStorageStrategyImpl.COMPRESSION_TYPE;

	/** The logger. */
	protected Logger logger = Logger.getInstance();

	/**
	 * Specialized Constructor that creates the appropriate DAOs based on the
	 * given versioning model element.
	 *
	 * @param element
	 *            Element.
	 * @return elementDAO - DataAccessObject.
	 */
	public static DataAccessObject createDAO(final Element element) {
		if (element instanceof Artifact)
			return new ArtifactDAO((Artifact) element);
		else if (element instanceof Container)
			return new ContainerDAO((Container) element);
		else if (element instanceof StageState)
			return new StageStateDAO((StageState) element);
		else if (element instanceof State)
			return new StateDAO((State) element);
		else if (element instanceof LogicalUnit)
			return new LogicalUnitDAO((LogicalUnit) element);
		else if (element instanceof Stage)
			return new StageDAO((Stage) element);
		else
			return null;
	}

	/**
	 * Load a persisted reference from the repository into the versioning
	 * element.
	 *
	 * @return success - boolean.
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public abstract boolean load() throws InvalidElementException;

	/**
	 * Record the current references of the versioning element into the
	 * repository.
	 *
	 * @return success - boolean.
	 */
	public abstract boolean record();

	/**
	 * Retrieve the persisted data from the repository and restore it to the
	 * workspace. Basic functionality for reverting an artifact's state.
	 *
	 * @return success - boolean.
	 */
	public abstract boolean retrieve();

	/**
	 * Store the current state of the referenced object in the repository. Basic
	 * functionality for committing an artifact's state.
	 *
	 * @return success - boolean.
	 */
	public abstract boolean store();

	/**
	 * DATAACCESSOBJECT METHODS (COMMON) **************************************.
	 * 
	 * @param contents
	 *            the contents
	 * @param destination
	 *            the destination
	 * @return true, if successful
	 */

	/**
	 * Store the designated contents to the designated file.
	 *
	 * @param contents
	 *            String.
	 * @param destination
	 *            File.
	 * @return success - boolean.
	 */
	protected boolean storeContents(final String contents,
			final File destination) {
		FileChannel fcDestination = null;
		try {
			final ByteBuffer bb = ByteBuffer
					.allocateDirect(contents.length() * 2);
			bb.put(contents.getBytes());
			bb.flip();
			fcDestination = new FileOutputStream(destination).getChannel();
			fcDestination.write(bb);
			return true;
		} catch (final Exception e) {
			this.logger.exception("Unable to Store Contents [" + destination
					+ "]", e);
			return false;
		} finally {
			if (fcDestination != null) {
				try {
					fcDestination.close();
				} catch (final Exception e) {
					this.logger.exception("Unable to Close File Channel ["
							+ fcDestination + "]", e);
				}
			}
		}

	}

	/**
	 * Transfer the content of one file to another file.
	 *
	 * @param source
	 *            File.
	 * @param destination
	 *            File.
	 * @return success - boolean.
	 */
	public boolean transferContents(final File source, final File destination) {
		FileChannel fcSource = null;
		FileChannel fcDestination = null;
		try {
			fcSource = new FileInputStream(source).getChannel();
			fcDestination = new FileOutputStream(destination).getChannel();
			fcSource.transferTo(0, fcSource.size(), fcDestination);
			return true;
		} catch (final Exception e) {
			this.logger.exception("Unable to Transfer Content from [" + source
					+ "] to [" + destination + "]", e);
			return false;
		} finally {
			if (fcSource != null) {
				try {
					fcSource.close();
				} catch (final Exception e) {
					this.logger.exception(
							"Unable to Close Source FileChannel.", e);
				}
			}
			if (fcDestination != null) {
				try {
					fcDestination.close();
				} catch (final Exception e) {
					this.logger.exception(
							"Unable to Close Destination FileChannel.", e);
				}
			}

		}
	}

}
