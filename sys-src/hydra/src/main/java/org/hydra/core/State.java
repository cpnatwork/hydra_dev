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
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.UUID;

import org.hydra.persistence.DataAccessObject;

/**
 * Specialized fingerprinted element which maintains or records a state of all
 * the referred to elements within the workspace and is capable of returning the
 * workspace to that state. States are created by committing a historied
 * element, which maintains control over the state of the workspace.
 *
 * @author Scott A. Hady
 * @version 0.2
 * @since 0.1
 */
public class State extends FingerprintedElement {

	/** The Constant HEADER. */
	public static final String HEADER = "HH::>>State";

	/** The Constant TOKEN. */
	public static final String TOKEN = "CS";

	/** The Constant TOKEN_PREVIOUS. */
	public static final String TOKEN_PREVIOUS = "PS";

	/** The Constant TOKEN_VALIDPREVIOUS. */
	public static final String TOKEN_VALIDPREVIOUS = "VP";

	/** The previous list. */
	private final ArrayList<State> previousList = new ArrayList<State>();

	/** The valid previous. */
	private State validPrevious = null;

	/** The contents hash. */
	private String contentsHash = "null";

	/** The validity. */
	private boolean validity = true;

	/** The user id. */
	private String userId = null;

	/** The message. */
	private String message = null;

	/** The timestamp. */
	private Date timestamp = new Date();

	/** The uuid. */
	private String uuid = null;

	/** The path. */
	private Path path = new Path("*1+0");

	/**
	 * Specialized Constructor, that defines the previous state, contents, user
	 * name and message.
	 *
	 * @param previous
	 *            State.
	 * @param contents
	 *            Container.
	 * @param userId
	 *            String.
	 * @param message
	 *            String.
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public State(final State previous, final Container contents,
			final String userId, final String message)
			throws InvalidElementException {
		super();
		this.initialize(previous, contents, userId, message, UUID.randomUUID()
				.toString());
	}

	/**
	 * Specialized Constructor, that defines the previous state, contents,
	 * user's name, message and the Universally Unique ID.
	 *
	 * @param previous
	 *            State.
	 * @param contents
	 *            Container.
	 * @param userId
	 *            String.
	 * @param message
	 *            String.
	 * @param UUID
	 *            String.
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public State(final State previous, final Container contents,
			final String userId, final String message, final String UUID)
			throws InvalidElementException {
		super();
		this.initialize(previous, contents, userId, message, UUID);
	}

	/**
	 * Specialized Constructor, which initializes the state according to the
	 * content of the file stored within the fingerprinted storage.
	 *
	 * @param stateHash
	 *            String.
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public State(final String stateHash) throws InvalidElementException {
		super(stateHash);
		this.dao.load();
	}

	/**
	 * Initialize the primary fields of the state.
	 * 
	 * @param previous
	 *            State.
	 * @param contents
	 *            Container.
	 * @param userId
	 *            String.
	 * @param message
	 *            String.
	 * @param UUID
	 *            String.
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	private void initialize(final State previous, final Container contents,
			final String userId, final String message, final String UUID)
			throws InvalidElementException {
		this.validPrevious = previous;
		if (previous != null) {
			this.previousList.add(previous);
		}
		if (contents == null) {
			this.contentsHash = "null";
		} else {
			contents.refreshFingerprint();
			contents.store();
			this.contentsHash = contents.getHash();
		}
		this.userId = userId;
		if (this.userId == null) {
			final String logMessage = "Unable to Intialize State - Null User Name.";
			this.logger.exception(logMessage);
			throw new InvalidElementException(logMessage);
		}
		this.message = message;
		if (this.message == null) {
			final String logMessage = "Unable to Intialize State - Null Message.";
			this.logger.exception(logMessage);
			throw new InvalidElementException(logMessage);
		}
		this.uuid = UUID;
		if (this.uuid == null) {
			final String logMessage = "Unable to Intialize State - Null UUID.";
			this.logger.exception(logMessage);
			throw new InvalidElementException(logMessage);
		}
		this.timestamp = new Date();
		final String fpString = State.HEADER + DataAccessObject.SEP_MEMBER
				+ this.getDescriptor() + DataAccessObject.SEP_MEMBER;
		this.fingerprint = new Fingerprint(fpString);
		this.repositoryFile = new File(this.config.getFPStore(),
				this.fingerprint.getHash());
	}

	/**
	 * STATE METHODS *******************************************************.
	 * 
	 * @param previousState
	 *            the previous state
	 * @return true, if successful
	 */

	/**
	 * Add state as a previous state.
	 *
	 * @param previousState
	 *            State.
	 * @return success - boolean.
	 */
	public boolean addPrevious(final State previousState) {
		return this.previousList.add(previousState);
	}

	/**
	 * Return a clone of the state's contents.
	 *
	 * @return contents - Container.
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public Container cloneContents() throws InvalidElementException {
		if (this.contentsHash.equals("null"))
			return null;
		else
			return new Container(this.config.getWorkspace(), this.contentsHash);
	}

	/**
	 * Return a clone of the current path from the HEAD to this state, may not
	 * be unique.
	 *
	 * @return path - Path.
	 */
	public Path clonePath() {
		return new Path(this.path.toString());
	}

	/**
	 * Return a string formatted as a log entry describing the state's creation.
	 *
	 * @return logEntry - String.
	 */
	public String getLogEntry() {
		final StringBuilder sb = new StringBuilder(this.userId).append(" - ")
				.append(this.message).append("\n\tTimestamp: ")
				.append(this.timestamp).append("\n\tCommit Hash: ")
				.append(this.getHash()).append("\n\tContent Hash: ")
				.append(this.contentsHash);
		for (int i = 0; i < this.previousList.size(); i++) {
			sb.append("\n\tPrev[").append(i).append("]: ")
					.append(this.previousList.get(i).getHash());
		}
		return sb.toString();
	}

	/**
	 * Return the message associated with the creation of this state.
	 *
	 * @return message - String.
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * Return the timestamp associated with the creation of this state.
	 *
	 * @return creationalTimestamp - Date.
	 */
	public Date getTimestamp() {
		return this.timestamp;
	}

	/**
	 * Return the user associated with the creation of this state.
	 *
	 * @return userId - String.
	 */
	public String getUserId() {
		return this.userId;
	}

	/**
	 * Retrieve the UUID associated with this state.
	 *
	 * @return UUID - String.
	 */
	public String getUUID() {
		return this.uuid;
	}

	/**
	 * Retrieve the state's valid path previous state.
	 *
	 * @return validPrevious - State.
	 */
	public State getValidPathPrevious() {
		return this.validPrevious;
	}

	/**
	 * Determine if the state is a temporary state.
	 *
	 * @return tempState - boolean.
	 */
	public boolean isTemporary() {
		return this.contentsHash.equals("null");
	}

	/**
	 * Determine if the state is a valid state.
	 *
	 * @return validity - boolean.
	 */
	public boolean isValid() {
		return this.validity;
	}

	/**
	 * List the previous states.
	 *
	 * @return previousStates - State[].
	 */
	public State[] listPrevious() {
		return this.previousList.toArray(new State[this.previousList.size()]);
	}

	/**
	 * Adjust the state's dynamic path by one step.
	 *
	 * @param step
	 *            Step.
	 * @return success - boolean.
	 */
	public boolean movePath(final Step step) {
		return this.path.move(step);
	}

	/**
	 * Remove a previous state from the list.
	 *
	 * @param previousState
	 *            State.
	 * @return success - boolean.
	 */
	public boolean removePrevious(final State previousState) {
		return this.previousList.remove(previousState);
	}

	/**
	 * Set the state's contents.
	 *
	 * @param contentsHash
	 *            String.
	 */
	public void setContentsHash(final String contentsHash) {
		this.contentsHash = contentsHash;
	}

	/**
	 * Set the state's message.
	 *
	 * @param message
	 *            String.
	 */
	public void setMessage(final String message) {
		this.message = message;
	}

	/**
	 * Set the timestamp.
	 *
	 * @param timestamp
	 *            Date.
	 */
	public void setTimestamp(final Date timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Set the state's UUID.
	 *
	 * @param uuid
	 *            String.
	 */
	public void setUUID(final String uuid) {
		this.uuid = uuid;
	}

	/**
	 * Set the user's name.
	 *
	 * @param userId
	 *            String.
	 */
	public void setUserName(final String userId) {
		this.userId = userId;
	}

	/**
	 * Set the state's validity.
	 *
	 * @param validity
	 *            boolean.
	 */
	public void setValidity(final boolean validity) {
		this.validity = validity;
	}

	/**
	 * Ste the state's previous valid path state.
	 *
	 * @param validPrevious
	 *            State.
	 */
	public void setValidPathPrevious(final State validPrevious) {
		this.validPrevious = validPrevious;
	}

	/**
	 * Update the state's content.
	 *
	 * @param updatedContent
	 *            Container.
	 */
	public void updateContent(final Container updatedContent) {
		try {
			updatedContent.refreshFingerprint();
			updatedContent.store();
			this.contentsHash = updatedContent.getHash();
			this.store();
		} catch (final Exception e) {
			this.logger.exception("Unable to Update Contents.", e);
		}
	}

	/**
	 * Update the state's dynamic path describing a non-unique route from the
	 * logical unit's HEAD state to this state.
	 *
	 * @param currentPath
	 *            Path.
	 */
	public void updatePath(final Path currentPath) {
		this.path = currentPath.clone();
	}

	/**
	 * ELEMENT METHODS (OVERRIDDEN) ****************************************.
	 * 
	 * @return the string
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Template Method that returns a string describing the state's descriptor,
	 * userId, message, content hash, uuid and previous states of the state.
	 * This describe method should be specialized through overriding the
	 * provided {@link #describeExtension} method.
	 */
	@Override
	public String describe() {
		// HEADER
		final StringBuilder sb = new StringBuilder(State.HEADER
				+ DataAccessObject.SEP_MEMBER);
		// UUID
		sb.append(this.getDescriptor()).append(DataAccessObject.SEP_MEMBER);
		// CONTENTS
		sb.append(Container.TOKEN).append(DataAccessObject.SEP_TOKEN)
				.append(this.contentsHash).append(DataAccessObject.SEP_MEMBER);
		// VALID PREVIOUS
		sb.append(State.TOKEN_VALIDPREVIOUS)
				.append(DataAccessObject.SEP_TOKEN)
				.append((this.validPrevious == null) ? "null"
						: this.validPrevious.getHash())
				.append(DataAccessObject.SEP_MEMBER);
		// PREVIOUS LIST
		for (final State p : this.previousList) {
			sb.append(State.TOKEN_PREVIOUS).append(DataAccessObject.SEP_TOKEN)
					.append(p.getHash()).append(DataAccessObject.SEP_MEMBER);
		}
		// TEMPLATE EXTENSION (i.e. Stage's Logical Units)
		sb.append(this.describeExtension());
		// METADATA
		sb.append(Element.TOKEN_METADATA).append(DataAccessObject.SEP_TOKEN)
				.append(this.timestamp).append(DataAccessObject.SEP_TOKEN)
				.append(this.userId).append(DataAccessObject.SEP_TOKEN)
				.append(new Boolean(this.validity).toString())
				.append(DataAccessObject.SEP_TOKEN).append(this.message)
				.append(DataAccessObject.SEP_MEMBER);
		return sb.toString();
	}

	/**
	 * Extends the {@link #describe} method as part of the template method that
	 * enables the subclasses to tailor the description to their specific needs.
	 *
	 * @return extendedDescription - String.
	 */
	protected String describeExtension() {
		return new String();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return a string that identifies this state.
	 */
	@Override
	public String getDescriptor() {
		return (new StringBuilder(State.TOKEN))
				.append(DataAccessObject.SEP_TOKEN).append(this.uuid)
				.toString();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return the user and message for this state.
	 */
	@Override
	public String getName() {
		return (new StringBuilder(this.userId))
				.append(DataAccessObject.SEP_TOKEN).append(this.message)
				.toString();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return the current status of the state.
	 */
	@Override
	public String getStatus(final boolean workspace, final boolean repository) {
		final StringBuilder sb = new StringBuilder("");
		if (workspace) {
			if (this.contentsHash.equals("null")) {
				sb.append("-");
			} else {
				try {
					sb.append((new Container(this.config.getWorkspace(),
							this.contentsHash)).getStatus(true, false));
				} catch (final Exception e) {
					sb.append("?");
				}
			}
		}
		if (repository) {
			if (workspace) {
				sb.append(" ");
			}
			if (!this.repositoryFile.exists()) {
				sb.append("-");
			} else {
				Scanner scanner = null;
				try {
					scanner = new Scanner(new FileInputStream(
							this.repositoryFile), "UTF-8");
					final StringBuffer stateSB = new StringBuffer("");
					for (int i = 0; i < 2; i++) {
						stateSB.append(scanner.nextLine()
								+ DataAccessObject.SEP_MEMBER);
					}
					if (this.fingerprint.checkFingerprint(stateSB.toString())
							&& (new Container(this.config.getWorkspace(),
									this.contentsHash)).getStatus(false, true)
									.equals("v")) {
						sb.append("v");
					} else {
						sb.append("c");
					}
				} catch (final Exception e) {
					this.logger
							.exception(
									"Unable to Determine State's Repository Status.",
									e);
					sb.append("?");
				}
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
	 * Refresh the fingerprint of the associated with the state, does nothing.
	 * The fingerprint of the state should not change.
	 */
	@Override
	public boolean refreshFingerprint() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Persist the current content and metatdata state of the logical unit.
	 */
	@Override
	public boolean store() {
		return this.dao.store();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return the logical unit workspace to the persisted state.
	 */
	@Override
	public boolean retrieve() {
		return this.dao.retrieve();
	}

}
