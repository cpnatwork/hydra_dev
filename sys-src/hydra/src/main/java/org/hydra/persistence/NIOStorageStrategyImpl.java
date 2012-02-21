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
import java.nio.channels.FileChannel;

import org.hydra.utilities.Logger;

/**
 * Implements the DAOs storage functionality using java's NIO API. Emphasizes
 * the response time and data transfer rate at the cost of the size of the
 * repository.
 *
 * @author Scott A. Hady
 * @version 0.2
 * @since 0.2
 */
public class NIOStorageStrategyImpl implements StorageStrategy {

	/** The Constant COMPRESSION_TYPE. */
	public static final String COMPRESSION_TYPE = "NIOTransfer";

	/** The logger. */
	private final Logger logger;

	/**
	 * Default Constructor with no parameters.
	 */
	public NIOStorageStrategyImpl() {
		this.logger = Logger.getInstance();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Transfer the content of a file in the workspace to a file in the
	 * repository.
	 */
	@Override
	public boolean transferToRepository(final File workspaceFile,
			final File repositoryFile) {
		return this.transferContents(workspaceFile, repositoryFile);
	}

	/**
	 * {@inheritDoc}
	 *
	 * Transfer the content of a file in the repository to a file in the
	 * workspace.
	 */
	@Override
	public boolean transferFromRepository(final File workspaceFile,
			final File repositoryFile) {
		return this.transferContents(repositoryFile, workspaceFile);
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
