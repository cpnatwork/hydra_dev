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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.hydra.persistence.DataAccessObject;

/**
 * Absract Unit which maintains and manipulates the history of other elements
 * and their state in the workspace. Each has a head state, current state and
 * contents that contains the fingerprinted elements that are directly tied to
 * this element. Primarily responsible for committing the workspace contents
 * state and reverting/returning the workspace to the previously committed
 * state.
 *
 * @author Scott A. Hady
 * @version 0.2
 * @since 0.2
 */
public abstract class CommittableElement extends Element {

	/** The HEADER. */
	public String HEADER;

	/** The TOKEN. */
	public String TOKEN;

	/** The Constant PROP_FORCECOMMIT. */
	public static final String PROP_FORCECOMMIT = "Core.forceCommit";

	/**
	 * Unique Token for identifying the historied element's HEAD State.
	 */
	public static final String TOKEN_HEAD = "HD";

	/**
	 * Unique Token for identifying the logical unit's CURRENT Committed State.
	 */
	public static final String TOKEN_CURRENT = "CU";

	/**
	 * Unique Token for identifying the logical unit's last recorded content.
	 */
	public static final String TOKEN_STASH = "ST";

	// CommittableElement Variables//
	/** The head. */
	protected State head;

	/** The current. */
	protected State current;

	/** The contents. */
	protected Container contents;

	/** The history crawler. */
	protected HistoryCrawler historyCrawler;

	/**
	 * Specialized Creator that loads its referencing representation from the
	 * designated repository file and initializes a history crawler for
	 * traversing the represented history.
	 *
	 * @param repositoryFile
	 *            File.
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public CommittableElement(final File repositoryFile)
			throws InvalidElementException {
		super(repositoryFile);
		this.historyCrawler = new HistoryCrawler(this);
	}

	/**
	 * ABSTRACT COMMITTABLEELEMENT METHODS ************************************.
	 * 
	 * @return true, if successful
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */

	/**
	 * Load the logical references from the repository.
	 *
	 * @return success - boolean.
	 * @throws org.hydra.core.InvalidElementException if any.
	 */
	public abstract boolean loadReferences() throws InvalidElementException;

	/**
	 * Persist the logical references in the repository.
	 *
	 * @return success - boolean.
	 */
	public abstract boolean recordReferences();

	/**
	 * COMMITTABLEELEMENT METHODS *********************************************.
	 * 
	 * @return the contents
	 */

	/**
	 * Retrieve the container that maintains the contents of this historied
	 * element.
	 *
	 * @return contents - Container.
	 */
	public Container getContents() {
		return this.contents;
	}

	/**
	 * Convience Method equal to getContents().getFingerprint().getHash().
	 *
	 * @return contentFingerprintHash - String.
	 */
	public String getContentsHash() {
		return (this.contents != null) ? this.contents.getFingerprint()
				.getHash() : "null";
	}

	/**
	 * Retrieve the historied element's current working state.
	 *
	 * @return current - State.
	 */
	public State getCurrent() {
		return this.current;
	}

	/**
	 * Convenience Method equal to getCurrent().getFingerprint().getHash().
	 *
	 * @return currentFingerprintHash - String.
	 */
	public String getCurrentHash() {
		return (this.current != null) ? this.current.getFingerprint().getHash()
				: "null";
	}

	/**
	 * Retrieve the historied element's head state.
	 *
	 * @return head - State.
	 */
	public State getHead() {
		return this.head;
	}

	/**
	 * Convenience Method equal to getCurrent().getFingerprint().getHash().
	 *
	 * @return headFingerprintHash - String.
	 */
	public String getHeadHash() {
		return (this.head != null) ? this.head.getFingerprint().getHash()
				: "null";
	}

	/**
	 * Retrieve the historied element's history crawler.
	 *
	 * @return historyCrawler - HistoryCrawler.
	 */
	public HistoryCrawler getHistoryCrawler() {
		return this.historyCrawler;
	}

	/**
	 * Set the current contents.
	 *
	 * @param contents
	 *            Container.
	 */
	public void setContents(final Container contents) {
		this.contents = contents;
	}

	/**
	 * Set the current contents.
	 *
	 * @param contentsHash
	 *            String.
	 * @throws org.hydra.core.InvalidElementException if any.
	 */
	public void setContents(final String contentsHash)
			throws InvalidElementException {
		this.contents = new Container(this.config.getWorkspace(), contentsHash);
	}

	/**
	 * Set the head state.
	 *
	 * @param headHash
	 *            String.
	 * @throws org.hydra.core.InvalidElementException if any.
	 */
	public void setHead(final String headHash) throws InvalidElementException {
		this.head = this.resolveStateFromHash(headHash);
	}

	/**
	 * Set the current state.
	 *
	 * @param currentHash
	 *            String.
	 * @throws org.hydra.core.InvalidElementException if any.
	 */
	public void setCurrent(final String currentHash)
			throws InvalidElementException {
		this.current = this.historyCrawler.findCommitHash(this.head,
				currentHash, null, true);
	}

	/**
	 * Set the logical unit's current state to a given target state.
	 *
	 * @param target
	 *            State.
	 * @return success - boolean.
	 */
	protected boolean setCurrent(State target) {
		try {
			if (this instanceof Stage) {
				target = this.stateToStageState(target);
			}
			target.retrieve();
			this.contents = target.cloneContents();
			this.current = target;
			return this.recordReferences();
		} catch (final Exception e) {
			this.logger.exception(
					"Unable to Set Current. Attempting Recovery.", e);
			this.current.retrieve();
			return false;
		}
	}

	/**
	 * Stash or persist the element's content so it may be later retrieved with
	 * the state of the historied element.
	 *
	 * @return success - boolean.
	 */
	public boolean stashContents() {
		if (this.contents != null) {
			this.contents.refreshFingerprint();
		}
		return (this.contents != null) ? this.contents.store() : false;
	}

	/**
	 * STATE RESOLVERS ********************************************************.
	 * 
	 * @param previous
	 *            the previous
	 * @param userId
	 *            the user id
	 * @param message
	 *            the message
	 * @return the state
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */

	/**
	 * Creates and returns an appropriate state based on the type of committable
	 * element that the this object represents.
	 *
	 * @param previous
	 *            State.
	 * @param userId
	 *            String.
	 * @param message
	 *            String.
	 * @return appropriateState - State.
	 * @throws org.hydra.core.InvalidElementException if any.
	 */
	public State resolveNewState(final State previous, final String userId,
			final String message) throws InvalidElementException {
		State state = null;
		if (this instanceof Stage) {
			state = new StageState((Stage) this, (StageState) previous, userId,
					message);
		} else {
			state = new State(previous, this.contents, userId, message);
		}
		return state;
	}

	/**
	 * Returns an appropriate state designated by the given hash based on the
	 * type of committable element this object represents.
	 *
	 * @param stateHash
	 *            String.
	 * @return appropriateState - State.
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public State resolveStateFromHash(final String stateHash)
			throws InvalidElementException {
		State state = null;
		if (this instanceof Stage) {
			state = new StageState(stateHash);
		} else {
			state = new State(stateHash);
		}
		return state;
	}

	/**
	 * Transform a state to a stage state.
	 *
	 * @param state
	 *            State.
	 * @return stageState StageState.
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	public StageState stateToStageState(final State state)
			throws InvalidElementException {
		final StageState stageState = new StageState(state.getHash());
		stageState.updatePath(state.clonePath());
		return stageState;
	}

	/**
	 * COMMIT METHODS *********************************************************.
	 * 
	 * @return true, if successful
	 */

	/**
	 * Determine if the committable element is ready to be committed. Either the
	 * content has changed or commits are being forced.
	 *
	 * @return readyToCommit - boolean.
	 */
	public boolean readyToCommit() {
		if (this.config.getProperty(CommittableElement.PROP_FORCECOMMIT)
				.equals("true"))
			return true;
		else {
			this.contents.refreshFingerprint();
			try {
				if (this.contents.getHash().equals(
						this.current.cloneContents().getHash()))
					return false;
				else
					return true;
			} catch (final Exception e) {
				this.logger.exception(
						"Unable to Determine if Committable Element ["
								+ this.getName() + "] is Ready to Commit.", e);
				return true;
			}
		}
	}

	/**
	 * Commit the state of the contents and return a commit hash that may be
	 * used to uniquely identify the state.
	 *
	 * @param userId
	 *            String.
	 * @param commitMessage
	 *            String.
	 * @return commitHash - String.
	 */
	public String commit(final String userId, final String commitMessage) {
		if (this.readyToCommit()) {
			try {
				final State originalHead = this.head;
				this.head = this.resolveNewState(this.head, userId,
						commitMessage);
				this.head.store();
				this.current = this.head;
				this.recordReferences();
				return this.getHeadHash();
			} catch (final InvalidElementException e) {
				this.logger
						.exception("Unable to Commit Historical Element.", e);
				return "null";
			}
		} else {
			this.logger.warning("Element is Not Ready to Commit.");
			return "null";
		}
	}

	/**
	 * Persist the current content's state with the current and head state
	 * recorded both as the new state's previous. The current state is defined
	 * to be along the valid path.
	 *
	 * @param userId
	 *            String.
	 * @param message
	 *            String.
	 * @return commitHash - String.
	 */
	public String commitValidPath(final String userId, final String message) {
		if (this.readyToCommit()) {
			try {
				this.current = this.resolveNewState(this.current, userId,
						message);
				if ((this.head != null)
						&& !Arrays.asList(this.current.listPrevious())
								.contains(this.head)) {
					this.current.addPrevious(this.head);
				}
				this.head = this.current;
				this.current.store();
				this.recordReferences();
				return this.head.getFingerprint().getHash();
			} catch (final InvalidElementException e) {
				this.logger.exception("Unable to Commit Logical Unit.", e);
				return null;
			}
		} else {
			this.logger.warning("Element is Not Ready to Commit.");
			return "null";
		}
	}

	/**
	 * Inserts the persisted current content's state between the two designated
	 * states and sets the current to the new persisted state. The states given
	 * do not need to be consecutive.
	 *
	 * @param userId
	 *            String.
	 * @param message
	 *            String.
	 * @param prevHash
	 *            String.
	 * @param nextHash
	 *            String.
	 * @return commitHash - String.
	 */
	public String commitInsert(final String userId, final String message,
			final String prevHash, final String nextHash) {
		if (this.readyToCommit()) {
			try {
				this.current = this.resolveNewState(
						this.resolveStateFromHash(prevHash), userId, message);
				this.current.store();
				State nextState;
				if (nextHash.equals(this.head.getFingerprint().getHash())) {
					nextState = this.head;
				} else {
					nextState = this.resolveStateFromHash(nextHash);
				}
				nextState.addPrevious(this.current);
				nextState.store();
				// Reset Head and Current Histories
				this.head = this.resolveStateFromHash(this.getHeadHash());
				this.revert(this.getCurrentHash());
				return this.getCurrentHash();
			} catch (final Exception e) {
				this.logger.exception("Unable to Insert Logical Unit Commit.",
						e);
				return null;
			}
		} else {
			this.logger.warning("Element is Not Ready to Commit.");
			return "null";
		}
	}

	/**
	 * Persist a temporary commit placeholder that is expected to be later
	 * replaced or the content being updated. The temporary commit's content is
	 * null.
	 *
	 * @param user
	 *            String.
	 * @param message
	 *            String.
	 * @return commitHash - String.
	 */
	public String commitTemporary(final String user, final String message) {
		try {
			this.current = new State(this.current, null, user, message);
			if ((this.head != null)
					&& !Arrays.asList(this.current.listPrevious()).contains(
							this.head)) {
				this.current.addPrevious(this.head);
			}
			this.head = this.current;
			this.current.store();
			this.recordReferences();
			return this.head.getFingerprint().getHash();
		} catch (final InvalidElementException e) {
			this.logger.exception(
					"Unable to Create Temporary Logical Unit Commit.", e);
			return null;
		}
	}

	/**
	 * Update or replace a previously committed state with the current
	 * committable element's content.
	 *
	 * @param stateHash
	 *            String.
	 * @param user
	 *            String.
	 * @param message
	 *            String.
	 * @return success - boolean.
	 */
	public boolean commitUpdate(final String stateHash, final String user,
			final String message) {
		this.contents.refreshFingerprint();
		try {
			final State updatedState = this.resolveStateFromHash(stateHash);
			updatedState.setUserName(user);
			updatedState.setMessage(message);
			updatedState.setTimestamp(new Date());
			updatedState.updateContent(this.contents);
			updatedState.store();
			if (stateHash.equals(this.head.getFingerprint().getHash())) {
				this.head = updatedState;
			} else {
				this.head = this.resolveStateFromHash(this.getHeadHash());
			}
			return this.revert(this.getCurrent().getFingerprint().getHash());
		} catch (final InvalidElementException e) {
			this.logger.exception("Unable to Update Logical Unit Commit.", e);
			return false;
		}
	}

	/**
	 * REVERT METHODS *********************************************************.
	 * 
	 * @param commitHash
	 *            the commit hash
	 * @return true, if successful
	 */

	/**
	 * Revert the content to a previously commited state identified by the
	 * commit hash.
	 *
	 * @param commitHash
	 *            String.
	 * @return success - boolean.
	 */
	public boolean revert(final String commitHash) {
		final State target = this.historyCrawler.findCommitHash(this.head,
				commitHash, null, true);
		return this.setCurrent(target);
	}

	/**
	 * Return the committable element's workspace to another state previously
	 * persisted, relative to the CURRENT state. The combination a branch and
	 * distance is generally the definition of a single step in a revert path
	 * ("*branch+distance").
	 *
	 * @param branch
	 *            int.
	 * @param distance
	 *            int.
	 * @return success - boolean.
	 */
	public boolean revertRelative(final int branch, final int distance) {
		final State target = this.historyCrawler.findCommitRelative(
				this.current, new Step(branch, distance), null);
		if (target == null) {
			this.logger.warning("Unable to Revert [" + this.getName()
					+ "] to Relative [" + branch + "," + distance + "].");
		}
		return this.setCurrent(target);
	}

	/**
	 * Return the logical unit's workspace to another state previously
	 * persisted, following the described step-wise path from the HEAD where
	 * each step is defined as the branch (*) and distance (+). An example is
	 * "*1+2*3+10" which represents a two step path. Step one takes the first
	 * branch and moves a distance of two. Step two takes the third branch and
	 * moves a distance of ten.
	 *
	 * @param revertPath
	 *            Path.
	 * @return success - boolean.
	 */
	public boolean revertPath(final Path revertPath) {
		final State target = this.historyCrawler.findCommitPath(this.head,
				revertPath, null);
		if (target == null) {
			this.logger.warning("Unable to Revert [" + this.getName()
					+ "] to Path [" + revertPath + "].");
		}
		return this.setCurrent(target);
	}

	/**
	 * Reurn the logical unit's workspace to another state previously persisted,
	 * defined by the given hash. Depending on the expected location of the
	 * commit hash, either a Depth-First or Breadth-First search may be used.
	 *
	 * @param revertHash
	 *            String.
	 * @param depthFirst
	 *            boolean.
	 * @return success - boolean.
	 */
	public boolean revertHash(final String revertHash, final boolean depthFirst) {
		final State target = this.historyCrawler.findCommitHash(this.head,
				revertHash, null, depthFirst);
		if (target == null) {
			this.logger.warning("Unable to Revert [" + this.getName()
					+ "] to Hash [" + revertHash + "].");
		}
		return this.setCurrent(target);
	}

	/**
	 * OVERRIDEN ELEMENT METHODS **********************************************.
	 * 
	 * @return the string
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Create a string description of a committable element.
	 */
	@Override
	public String describe() {
		final StringBuffer sb = new StringBuffer();
		if (this.head != null) {
			sb.append(CommittableElement.TOKEN_HEAD
					+ DataAccessObject.SEP_TOKEN + this.getHeadHash()
					+ DataAccessObject.SEP_MEMBER);
		}
		if (this.current != null) {
			sb.append(CommittableElement.TOKEN_CURRENT
					+ DataAccessObject.SEP_TOKEN + this.getCurrentHash()
					+ DataAccessObject.SEP_MEMBER);
		}
		if (this.contents != null) {
			this.contents.refreshFingerprint();
		}
		sb.append(CommittableElement.TOKEN_STASH + DataAccessObject.SEP_TOKEN
				+ this.getContentsHash() + DataAccessObject.SEP_MEMBER);
		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return the status of the historied element's state in the workspace and
	 * the repository.
	 */
	@Override
	public String getStatus(final boolean workspace, final boolean repository) {
		final ArrayList<String> statusList = new ArrayList<String>();
		final StringBuilder sb = new StringBuilder("");
		// Workspace Status
		if (workspace) {
			try {
				statusList.add(this.contents.getStatus(true, false));
				statusList.add(this.head.getStatus(true, false));
				statusList.add(this.current.getStatus(true, false));
				sb.append(this.getWorstStatus(statusList));
			} catch (final Exception e) {
				this.logger.warning("Unable to Determine Workspace Status.");
				sb.append("?");
			}
		}
		// Repository Status
		if (repository) {
			if (workspace) {
				sb.append(" ");
			}
			try {
				statusList.clear();
				statusList.add(this.contents.getStatus(false, true));
				statusList.add(this.head.getStatus(false, true));
				statusList.add(this.current.getStatus(false, true));
				sb.append(this.getWorstStatus(statusList));
			} catch (final Exception e) {
				this.logger.warning("Unable to Determine Repository Status.");
				sb.append("?");
			}
		}
		return sb.toString();
	}

	/**
	 * Determine the worst status of the contained elements.
	 *
	 * @param statusList
	 *            ArrayList<String>.
	 * @return worstStatus - String.
	 */
	protected String getWorstStatus(final ArrayList<String> statusList) {
		if (statusList.contains("?"))
			return "?";
		else if (statusList.contains("-"))
			return "-";
		else if (statusList.contains("c"))
			return "c";
		else
			return "v";
	}

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
		return this.getName().equals(
				((CommittableElement) comparedElement).getName());
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return a string representation of the element.
	 */
	@Override
	public String toString() {
		final String splitClassName[] = this.getClass().getName().split("\\.");
		return splitClassName[splitClassName.length - 1] + ":" + this.getName();
	}

}
