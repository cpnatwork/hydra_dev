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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.hydra.utilities.Logger;

/**
 * Implements the DAOs storage functionality using the GZIP compression format.
 * Emphasizes the reduction of the repository size at the cost of response time.
 *
 * @author Scott A. Hady
 * @version 0.2
 * @since 0.2
 */
public class GZipStorageStrategyImpl implements StorageStrategy {

	/** The Constant COMPRESSION_TYPE. */
	public static final String COMPRESSION_TYPE = "GZipCompression";

	/** The logger. */
	private final Logger logger;

	/**
	 * Default constructor with no parameters.
	 */
	public GZipStorageStrategyImpl() {
		this.logger = Logger.getInstance();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Transfer the uncompressed contents of a file in the workspace to an
	 * compressed version in the repository.
	 */
	@Override
	public boolean transferToRepository(final File workspaceFile,
			final File repositoryFile) {
		return this.compressContents(workspaceFile, repositoryFile);
	}

	/**
	 * {@inheritDoc}
	 *
	 * Transfer the compressed contents of a file in the repository to an
	 * uncompressed version in the workspace.
	 */
	@Override
	public boolean transferFromRepository(final File workspaceFile,
			final File repositoryFile) {
		return this.uncompressContents(repositoryFile, workspaceFile);
	}

	/**
	 * Generalized means to compress a file with the GZIP format.
	 *
	 * @param source
	 *            File.
	 * @param destination
	 *            File.
	 * @return success - boolean.
	 */
	public boolean compressContents(final File source, final File destination) {
		GZIPOutputStream zos = null;
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(source));
			zos = new GZIPOutputStream(new FileOutputStream(destination));
			final byte[] buffer = new byte[1024];
			int count;
			while ((count = bis.read(buffer)) >= 0) {
				zos.write(buffer, 0, count);
			}
			return true;
		} catch (final Exception e) {
			this.logger.exception("Unable to Compress Artifact [" + source
					+ "].", e);
			return false;
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (final Exception e) {
					this.logger
							.exception("Unable to Close BufferedInputStream ["
									+ bis + "]", e);
				}
			}
			if (zos != null) {
				try {
					zos.close();
				} catch (final Exception e) {
					this.logger.exception("Unable to Close GZIPOutputStream ["
							+ zos + "]", e);
				}
			}
		}
	}

	/**
	 * Generalized means to uncompress a file in the GZIP format.
	 *
	 * @param source
	 *            File.
	 * @param destination
	 *            File.
	 * @return success - boolean.
	 */
	public boolean uncompressContents(final File source, final File destination) {
		GZIPInputStream zis = null;
		BufferedOutputStream bos = null;
		try {
			zis = new GZIPInputStream(new FileInputStream(source));
			bos = new BufferedOutputStream(new FileOutputStream(destination));
			final byte[] buffer = new byte[1024];
			int count;
			while ((count = zis.read(buffer)) >= 0) {
				bos.write(buffer, 0, count);
			}
			return true;
		} catch (final Exception e) {
			this.logger.exception("Unable to Uncompress Artifact [" + source
					+ "].", e);
			return false;
		} finally {
			if (zis != null) {
				try {
					zis.close();
				} catch (final Exception e) {
					this.logger.exception("Unable to Close GZIPInputStream ["
							+ zis + "]", e);
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (final Exception e) {
					this.logger.exception(
							"Unable to Close BufferedOutputStream [" + bos
									+ "]", e);
				}
			}
		}
	}

}
