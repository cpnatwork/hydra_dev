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
package org.hydra.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.channels.FileChannel;
import java.util.Scanner;

/**
 * Provides the capability to store contents in and retrieve contents from
 * locations. The location provides the address where the contents may be found.
 * The contents may be retrieved in chunks which are commonly referred to as the
 * contents members.
 *
 * @since 0.2
 * @version 0.2
 * @author Scott A. Hady
 */
public class Storage {

	/** The Constant MEMBER_SEPARATOR. */
	public static final String MEMBER_SEPARATOR = "\n";

	/** The Constant TOKEN_SEPARATOR. */
	public static final String TOKEN_SEPARATOR = "::>>";

	/** The logger. */
	private Logger logger = null;

	/** The scanner. */
	private Scanner scanner = null;

	/** STORAGE READING OPERATIONS. *********************************************/

	/**
	 * Default constructor, which initializes the object's logger.
	 */
	public Storage() {
		this.logger = Logger.getInstance();
	}

	/**
	 * Close a location that has been opened for reading.
	 *
	 * @return success - boolean.
	 */
	public boolean closeLocation() {
		if (this.hasOpenLocation()) {
			this.scanner.close();
			this.scanner = null;
		}
		return true;
	}

	/**
	 * Determine how many members are contained within a location.
	 *
	 * @param targetLocation
	 *            File.
	 * @return numberOfMembers - int.
	 */
	public int countMembers(final File targetLocation) {
		int memberCount = 0;
		if (this.openLocation(targetLocation)) {
			while (this.hasNextMember()) {
				if (this.readNextMember() != "") {
					memberCount++;
				}
			}
		} else {
			this.logger.exception("Unable to count members in ["
					+ targetLocation + "].");
			memberCount = -1;
		}
		return memberCount;
	}

	/**
	 * Determine if there are more members of the content that have not been
	 * read.
	 *
	 * @return hasUnreadMembers - boolean.
	 */
	public boolean hasNextMember() {
		if (this.hasOpenLocation())
			return this.scanner.hasNextLine();
		else
			return false;
	}

	/**
	 * Determine if a location has been opened for reading.
	 *
	 * @return hasOpenLocation - boolean.
	 */
	public boolean hasOpenLocation() {
		return (this.scanner != null);
	}

	/**
	 * Open a given location for prior to reading contents. This operation must
	 * succeed before the other reading operations may be executed successfully.
	 *
	 * @param targetLocation
	 *            File.
	 * @return success - boolean.
	 */
	public boolean openLocation(final File targetLocation) {
		try {
			this.scanner = new Scanner(new FileInputStream(targetLocation),
					"UTF-8");
			return true;
		} catch (final Exception exception) {
			this.logger.exception("Unable to Open Location [" + targetLocation
					+ "].", exception);
			return false;
		}
	}

	/**
	 * Return a the contents of the location as bytes. Typically used for
	 * fingerprinting purposes - However, may cause a problem with large files.
	 *
	 * @param targetLocation
	 *            File.
	 * @return contentBytes - byte[].
	 */
	public byte[] readContentsBytes(final File targetLocation) {
		byte[] contentBytes = new byte[0];
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(targetLocation));
			contentBytes = new byte[(int) targetLocation.length()];
			bis.read(contentBytes);
		} catch (final Exception e) {
			this.logger.exception("Unable to Read Contents Bytes ["
					+ targetLocation + "].", e);
		} finally {
			try {
				bis.close();
			} catch (final Exception e) {
				this.logger
						.exception(
								"Unable to Close Storage BufferedInputStream During readContentsBytes.",
								e);
			}
		}
		return contentBytes;
	}

	/**
	 * Read the next member of the contents. Within files members are considered
	 * lines and this command will return the next line.
	 *
	 * @return nextMember - String.
	 */
	public String readNextMember() {
		if (!this.hasOpenLocation()) {
			this.logger.warning("Attempting to Read Without Closed Location.");
			return new String();
		} else if (!this.hasNextMember()) {
			this.logger.warning("Reading While Has No More Content.");
			return new String();
		} else
			return this.scanner.nextLine();
	}

	/**
	 * STORAGE WRITING OPERATIONS *********************************************.
	 * 
	 * @param targetLocation
	 *            the target location
	 * @param member
	 *            the member
	 * @return true, if successful
	 */

	/**
	 * Append a log entry to log - suppresses logging which may produce an
	 * infinite loop.
	 *
	 * @param targetLocation
	 *            File.
	 * @param member
	 *            String.
	 * @return success - boolean.
	 */
	public boolean appendLogMember(final File targetLocation,
			final String member) {
		return this.writeContents(targetLocation, member
				+ Storage.MEMBER_SEPARATOR, true, false);
	}

	/**
	 * Append a member - automatically adds member separator to end of given
	 * member.
	 *
	 * @param targetLocation
	 *            File.
	 * @param member
	 *            String.
	 * @return success - boolean.
	 */
	public boolean appendMember(final File targetLocation, final String member) {
		return this.writeContents(targetLocation, member
				+ Storage.MEMBER_SEPARATOR, false, true);
	}

	/**
	 * Overwrite the contents of a target location with the given contents.
	 *
	 * @param targetLocation
	 *            File.
	 * @param contents
	 *            String.
	 * @return success - boolean.
	 */
	public boolean overwriteContents(final File targetLocation,
			final String contents) {
		return this.writeContents(targetLocation, contents, false, true);
	}

	/**
	 * Write desingated content to designated file. This is the workhorse method
	 *
	 * @param targetLocation
	 *            File
	 * @param contents
	 *            String
	 * @param append
	 *            boolean
	 * @param logExceptions
	 *            boolean
	 * @return success - boolean
	 */
	protected boolean writeContents(final File targetLocation,
			final String contents, final boolean append,
			final boolean logExceptions) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(targetLocation, append));
			bw.write(contents);
			return true;
		} catch (final Exception e) {
			if (logExceptions) {
				this.logger.exception("Unable to Store Contents ["
						+ targetLocation + "]", e);
			}
			return false;
		} finally {
			try {
				if (bw != null) {
					bw.flush();
					bw.close();
				}
			} catch (final Exception e) {
				if (logExceptions) {
					this.logger
							.exception("Unable to Flush and Close Writer", e);
				}
			}
		}
	}

	/**
	 * Transfer the contents of one location.
	 *
	 * @param sourceLocation
	 *            File.
	 * @param destinationLocation
	 *            File.
	 * @return success - boolean.
	 */
	public boolean transferContents(final File sourceLocation,
			final File destinationLocation) {
		FileChannel srcFC = null;
		FileChannel destFC = null;
		try {
			srcFC = new FileInputStream(sourceLocation).getChannel();
			destFC = new FileOutputStream(destinationLocation).getChannel();
			srcFC.transferTo(0, srcFC.size(), destFC);
			return true;
		} catch (final Exception exception) {
			this.logger.exception("Unable to Transfer Content From ["
					+ sourceLocation + "] to [" + destinationLocation + "].",
					exception);
			return false;
		} finally {
			try {
				srcFC.close();
			} catch (final Exception e) {
				this.logger.exception("Unable to Close Source FileChannel.", e);
			}
			try {
				destFC.close();
			} catch (final Exception e) {
				this.logger.exception(
						"Unable to Close Destination FileChannel.", e);
			}
		}
	}

	/**
	 * FILE MANIPULATION METHODS **********************************************.
	 * 
	 * @param targetLocation
	 *            the target location
	 * @return the string
	 */

	/**
	 * Find a canonical path for a given location.
	 *
	 * @param targetLocation
	 *            File.
	 * @return canonicalPath - String.
	 */
	public String findCanonicalPath(final File targetLocation) {
		try {
			return targetLocation.getCanonicalPath();
		} catch (final Exception e) {
			this.logger.exception(
					"Unable to Determine Canonical Path for Target ["
							+ targetLocation + "].", e);
			return null;
		}
	}

	/**
	 * Find the canonical location for a given location.
	 *
	 * @param targetLocation
	 *            File.
	 * @return canonicalLocation - File.
	 */
	public File findCanonicalLocation(final File targetLocation) {
		try {
			return targetLocation.getCanonicalFile();
		} catch (final Exception e) {
			this.logger.exception(
					"Unable to Determine Canonical Location for Target ["
							+ targetLocation + "].", e);
			return null;
		}
	}

	/**
	 * Find the subpath from a parent path to a child path.
	 *
	 * @param targetParentPath
	 *            String.
	 * @param targetChildPath
	 *            String.
	 * @return subPath String.
	 */
	public String findSubPath(final String targetParentPath,
			final String targetChildPath) {
		return targetChildPath.replace(targetParentPath, "");
	}

	/**
	 * Find the subpath from a parent location to a child location.
	 *
	 * @param targetParentLocation
	 *            File.
	 * @param targetChildLocation
	 *            File.
	 * @return subPath - String.
	 */
	public String findSubPath(final File targetParentLocation,
			final File targetChildLocation) {
		return this.findSubPath(this.findCanonicalPath(targetParentLocation),
				this.findCanonicalPath(targetChildLocation));
	}
}
