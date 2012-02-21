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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.hydra.utilities.Logger;

/**
 * Uniquely defines the contents of a file or string based on its SHA1 hash
 * value. FingerprintedElements are persisted in the repository according to
 * their fingerprint hash, so the correct content may be later retrieved.
 *
 * @author Scott A. Hady
 * @version 0.2
 * @since 0.1
 */
public class Fingerprint {

	/** The logger. */
	private Logger logger = null;

	/** The md. */
	private final MessageDigest md;

	/** The fp hash. */
	private String fpHash;

	/**
	 * Default constructor.
	 */
	public Fingerprint() {
		this.logger = Logger.getInstance();
		this.md = this.getMessageDigest();
		this.fpHash = null;
	}

	/**
	 * Constructs fingerprint by hashing the given string's content.
	 *
	 * @param contentString
	 *            String.
	 */
	public Fingerprint(final String contentString) {
		this.logger = Logger.getInstance();
		this.md = this.getMessageDigest();
		this.fpHash = this.calculateHash(contentString);
	}

	/**
	 * Constructs the fingerprint by hashing the given file's content.
	 *
	 * @param contentFile
	 *            File.
	 */
	public Fingerprint(final File contentFile) {
		this.logger = Logger.getInstance();
		this.md = this.getMessageDigest();
		this.fpHash = this.calculateHash(contentFile);
	}

	/**
	 * Return a SHA1 message digest instance to use for the hash calculations.
	 * 
	 * @return messageDigest - MessageDigest.
	 */
	private MessageDigest getMessageDigest() {
		try {
			return MessageDigest.getInstance("SHA1");
		} catch (final NoSuchAlgorithmException e) {
			this.logger.exception("Unable to get Message Digest.", e);
			return null;
		}
	}

	/**
	 * Calculate a unique fingerprint hash for the content of the given string.
	 *
	 * @param contentString
	 *            String.
	 * @return hash - String.
	 */
	public String calculateHash(final String contentString) {
		this.md.update(contentString.getBytes());
		return this.convertBytesToHash(this.md.digest());
	}

	/**
	 * Calculate a unique fingerprint hash for the content of the given file.
	 *
	 * @param contentFile
	 *            File.
	 * @return hash - String.
	 */
	public String calculateHash(final File contentFile) {
		if (!contentFile.exists()) {
			this.logger.warning("Cannot calculate hash for non-existent file ["
					+ contentFile + "].");
			return null;
		} else if (contentFile.isFile()) {
			this.md.update(this.readContentBytes(contentFile));
			return this.convertBytesToHash(this.md.digest());
		} else {
			final StringBuilder contentString = new StringBuilder(
					Container.HEADER).append("\n");
			return this.calculateHash(contentString.toString());
		}
	}

	/**
	 * Read content bytes.
	 * 
	 * @param contentFile
	 *            the content file
	 * @return the byte[]
	 */
	private byte[] readContentBytes(final File contentFile) {
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(contentFile));
			final byte[] contentBytes = new byte[(int) contentFile.length()];
			bis.read(contentBytes);
			return contentBytes;
		} catch (final Exception e) {
			this.logger.exception("Unable to Read Contents Bytes ["
					+ contentFile + "].", e);
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
			} catch (final Exception e) {
				this.logger
						.exception(
								"Unable to Close Storage BufferedInputStream During readContentsBytes.",
								e);
			}
		}
		return new byte[0];

	}

	/**
	 * Convert the byte array returned by the message digest to a string of
	 * characters.
	 * 
	 * @param bytes
	 *            byte[].
	 * @return String - hashCode.
	 */
	private String convertBytesToHash(final byte[] bytes) {
		final StringBuffer sb = new StringBuffer("");
		for (final byte b : bytes) {
			sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

	/**
	 * Return the hash code string that represents the fingerprinted contents.
	 *
	 * @return hashCode - String.
	 */
	public String getHash() {
		return this.fpHash;
	}

	/**
	 * Set the hash code string that represents the contents.
	 *
	 * @param contentHash
	 *            String.
	 */
	public void setHash(final String contentHash) {
		this.fpHash = contentHash;
	}

	/**
	 * Determine if the content of the string matches the fingerprinted content.
	 *
	 * @param contentString
	 *            String.
	 * @return matches - boolean.
	 */
	public boolean checkFingerprint(final String contentString) {
		return this.fpHash.equals(this.calculateHash(contentString));
	}

	/**
	 * Determine if the content of the file matches the fingerprinted content.
	 *
	 * @param contentFile
	 *            String.
	 * @return matches - boolean.
	 */
	public boolean checkFingerprint(final File contentFile) {
		return this.fpHash.equals(this.calculateHash(contentFile));
	}

	/**
	 * OBJECT METHODS (OVERRIDDEN) ********************************************.
	 * 
	 * @param fp
	 *            the fp
	 * @return true, if successful
	 */

	/**
	 * Determine if this fingerprint is equal to another fingerprint.
	 *
	 * @param fp
	 *            Fingerprint.
	 * @return equals - Fingerprint.
	 */
	public boolean equals(final Fingerprint fp) {
		return this.fpHash.equals(fp.getHash());
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return a string representation of the fingerprint.
	 */
	@Override
	public String toString() {
		return "Fingerprint:" + this.fpHash;
	}

}
