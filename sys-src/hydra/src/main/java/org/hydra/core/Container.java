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
import java.util.TreeSet;

import org.hydra.persistence.DataAccessObject;
import org.hydra.utilities.FileUtilities;
import org.hydra.utilities.FilterInDirectories;
import org.hydra.utilities.FilterInFiles;

/**
 * Specialized fingerprinted element capable of containing other elements;
 * similar in function to a folder in the file system.
 *
 * @author Scott A. Hady
 * @version 0.2
 * @since 0.1
 */
public class Container extends FingerprintedElement {

	/**
	 * Unique Container Header.
	 */
	public static final String HEADER = "HH::>>Container";

	/**
	 * Unique Container Token.
	 */
	public static final String TOKEN = "CO";

	/** The elements. */
	@SuppressWarnings("unchecked")
	private final TreeSet<FingerprintedElement> elements = new TreeSet<FingerprintedElement>(
			new ElementComparator());

	/**
	 * Specialized Constructor, which uses the given directory as its base.
	 *
	 * @param directory
	 *            File.
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public Container(final File directory) throws InvalidElementException {
		super(directory);
		this.fingerprint.setHash(this.fingerprint
				.calculateHash(new StringBuilder(Container.HEADER).append(
						DataAccessObject.SEP_MEMBER).toString()));
	}

	/**
	 * Specialized Constructor, which uses the given directory as its workspace
	 * base and defines the content according to the content's hash found in the
	 * fingerprinted store in the repository.
	 *
	 * @param directory
	 *            File.
	 * @param contentHash
	 *            String.
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public Container(final File directory, final String contentHash)
			throws InvalidElementException {
		super(directory, contentHash);
		this.dao.load();
	}

	/**
	 * Specialized Constructor, which creates a clone of the given container.
	 *
	 * @param clonedContainer
	 *            Container.
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public Container(final Container clonedContainer)
			throws InvalidElementException {
		this.fingerprint.setHash(clonedContainer.getFingerprint().getHash());
		this.repositoryFile = new File(this.config.getFPStore(),
				this.fingerprint.getHash());
		this.workspaceFile = new File(clonedContainer.cloneWorkspaceFile()
				.getPath());
		for (final FingerprintedElement e : clonedContainer.listElements()) {
			if (e instanceof Artifact) {
				this.elements.add(e);
			} else {
				this.elements.add(new Container((Container) e));
			}
		}

	}

	/**
	 * CONTAINER METHODS ***************************************************.
	 * 
	 * @param element
	 *            the element
	 * @return true, if successful
	 */

	/**
	 * Add an element to this container. Can only add elements that are direct
	 * subelements of the container.
	 *
	 * @param element
	 *            FingerprintedElement.
	 * @return success - boolean.
	 */
	public boolean addElement(final FingerprintedElement element) {
		if (this.isDirectSubElement(element))
			return this.addDirectSubElement(element);
		else
			return this.addDeepSubElement(element);
	}

	/**
	 * Checks if is direct sub element.
	 * 
	 * @param element
	 *            the element
	 * @return true, if is direct sub element
	 */
	private boolean isDirectSubElement(final FingerprintedElement element) {
		final File eFile = FileUtilities.findCanonicalLocation(element
				.cloneWorkspaceFile());
		return eFile.getParentFile().equals(
				FileUtilities.findCanonicalLocation(this.workspaceFile));
	}

	/**
	 * Adds the direct sub element.
	 * 
	 * @param element
	 *            the element
	 * @return true, if successful
	 */
	private boolean addDirectSubElement(final FingerprintedElement element) {
		if (this.elements.add(element)) {
			this.fingerprint = new Fingerprint(this.describe());
			return true;
		}
		this.logger.exception("Unable to Add Element [" + element.toString()
				+ "].");
		return false;
	}

	/**
	 * Adds the deep sub element.
	 * 
	 * @param element
	 *            the element
	 * @return true, if successful
	 */
	private boolean addDeepSubElement(final FingerprintedElement element) {
		final String SEPARATOR = File.separator;
		final File eFile = FileUtilities.findCanonicalLocation(element
				.cloneWorkspaceFile());
		final String[] splitPath = FileUtilities
				.findSubPath(this.workspaceFile, eFile).substring(1)
				.split("\\\\|\\/");
		final StringBuilder sbPath = new StringBuilder(this.workspaceFile
				+ SEPARATOR);
		Container ptr = this;
		for (int i = 0; i < (splitPath.length - 1); i++) {
			if (!splitPath[i].equals("")) {
				sbPath.append(splitPath[i] + SEPARATOR);
				if (ptr.getElement(splitPath[i]) == null) {
					try {
						ptr.addElement(new Container(
								new File(sbPath.toString())));
					} catch (final Exception e) {
						this.logger.exception("Unable to Add Container ["
								+ sbPath.toString() + "].", e);
						return false;
					}
				}
				ptr = (Container) ptr.getElement(splitPath[i]);
			}
		}
		return ptr.addElement(element);
	}

	/**
	 * Add a container and all if its contents found while searching recursively
	 * through the directory.
	 *
	 * @param container
	 *            Container.
	 * @return success - boolean.
	 */
	public boolean addContainerAndContents(final Container container) {
		boolean success = this.addElement(container);
		for (final File subFile : container.cloneWorkspaceFile().listFiles(
				new FilterInFiles())) {
			try {
				if (!this.addElement(new Artifact(subFile))) {
					success = false;
				}
			} catch (final Exception e) {
				success = false;
			}
		}
		for (final File subDirectory : container.cloneWorkspaceFile()
				.listFiles(new FilterInDirectories())) {
			try {
				if (!this.addContainerAndContents(new Container(subDirectory))) {
					success = false;
				}
			} catch (final Exception e) {
				success = false;
			}
		}
		return success;
	}

	/**
	 * Return the number of subelements contained in this container.
	 *
	 * @return numElements - int.
	 */
	public int countElements() {
		int count = 0;
		for (final Element e : this.elements) {
			count++;
			if (e instanceof Container) {
				count += ((Container) e).countElements();
			}
		}
		return count;
	}

	/**
	 * Return a given named element.
	 *
	 * @param eName
	 *            String.
	 * @return element FingerprintedElement.
	 */
	public FingerprintedElement getElement(final String eName) {
		final String SEPARATOR = File.separator;
		final String searchPath = eName.replace(this.workspaceFile.getPath()
				+ SEPARATOR, "");
		final int nextSeparatorIndex = searchPath.indexOf(SEPARATOR);
		String subElementName = null;
		if (nextSeparatorIndex != -1) {
			subElementName = searchPath.substring(0, nextSeparatorIndex);
		} else {
			subElementName = searchPath;
		}
		for (final FingerprintedElement e : this.elements) {
			// Search For Direct SubElement
			if (subElementName.equals(e.getName())) {
				// If Direct SubElement is Target Return
				if (nextSeparatorIndex == -1)
					return e;
				// Else Continue Search Along Search Path
				else
					return ((Container) e).getElement(searchPath
							.substring(nextSeparatorIndex + 1));
			}
		}
		return null;
	}

	/**
	 * Return an array of elements contained.
	 *
	 * @return elements - FingerprintedElement[].
	 */
	public FingerprintedElement[] listElements() {
		return this.elements.toArray(new FingerprintedElement[this.elements
				.size()]);
	}

	/**
	 * Remove an element from this container.
	 *
	 * @param element
	 *            FingerprintedElement.
	 * @return success - boolean.
	 */
	public boolean removeElement(final FingerprintedElement element) {
		if (this.isDirectSubElement(element))
			return this.removeDirectSubElement(element);
		else
			return this.removeDeepSubElement(element);
	}

	/**
	 * Removes the direct sub element.
	 * 
	 * @param element
	 *            the element
	 * @return true, if successful
	 */
	private boolean removeDirectSubElement(final FingerprintedElement element) {
		if (this.elements.remove(element)) {
			this.fingerprint = new Fingerprint(this.describe());
			return true;
		}
		return false;
	}

	/**
	 * Removes the deep sub element.
	 * 
	 * @param element
	 *            the element
	 * @return true, if successful
	 */
	private boolean removeDeepSubElement(final FingerprintedElement element) {
		final File eFile = FileUtilities.findCanonicalLocation(element
				.cloneWorkspaceFile());
		final String[] splitPath = FileUtilities
				.findSubPath(this.workspaceFile, eFile).substring(1)
				.split("\\\\|\\/");
		Container ptr = this;
		for (int i = 0; i < (splitPath.length - 1); i++) {
			ptr = (Container) ptr.getElement(splitPath[i]);
			if (ptr == null)
				return false;
		}
		return ptr.removeElement(element);
	}

	/**
	 * ELEMENT METHODS (OVERRIDDEN) ****************************************.
	 * 
	 * @return the string
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Provide a string that describes the content of this container.
	 */
	@Override
	public String describe() {
		final StringBuilder sb = new StringBuilder(Container.HEADER)
				.append(DataAccessObject.SEP_MEMBER);
		for (final FingerprintedElement e : this.elements) {
			sb.append(e.getDescriptor()).append(DataAccessObject.SEP_MEMBER);
		}
		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return this container's complete string id.
	 */
	@Override
	public String getDescriptor() {
		return new StringBuilder(Container.TOKEN)
				.append(DataAccessObject.SEP_TOKEN).append(this.getName())
				.append(DataAccessObject.SEP_TOKEN)
				.append(this.fingerprint.getHash()).toString();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return the container's name.
	 */
	@Override
	public String getName() {
		return this.workspaceFile.getName();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return the current status of the container.
	 */
	@Override
	public String getStatus(final boolean workspace, final boolean repository) {
		final StringBuilder sb = new StringBuilder("");
		if (workspace) {
			if (!this.workspaceFile.exists()) {
				sb.append("-");
			} else {
				boolean valid = true;
				for (final Element e : this.listElements()) {
					if (!e.getStatus(true, false).equals("v")) {
						valid = false;
						break;
					}
				}
				sb.append(valid ? "v" : "c");
			}
		}
		if (repository) {
			if (workspace) {
				sb.append(" ");
			}
			if (!this.repositoryFile.exists()) {
				sb.append("-");
			} else {
				sb.append(this.fingerprint
						.checkFingerprint(this.repositoryFile) ? "v" : "c");
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
	 * Refresh the container's fingerprint to account for any changed content.
	 */
	@Override
	public boolean refreshFingerprint() {
		boolean success = true;
		for (final FingerprintedElement e : this.elements) {
			if (!e.refreshFingerprint()) {
				success = false;
			}
			if (e.getHash() == null) {
				this.logger
						.warning("Null Fingerprint Detected - Removing Element ["
								+ e + "].");
				this.removeElement(e);
			}
		}
		this.fingerprint
				.setHash(this.fingerprint.calculateHash(this.describe()));
		return success;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return the persisted container and its content from the repository to the
	 * workspace.
	 */
	@Override
	public boolean retrieve() {
		return this.dao.retrieve();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Persist the container and its content within the repository.
	 */
	@Override
	public boolean store() {
		this.repositoryFile = new File(this.config.getFPStore(),
				this.fingerprint.getHash());
		return this.dao.store();
	}

}
