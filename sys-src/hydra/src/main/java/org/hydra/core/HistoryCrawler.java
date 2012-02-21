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

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.hydra.utilities.Logger;

/**
 * Searches through a history of states to find specified states or to produce a
 * log of the commits.
 *
 * @author Scott A. Hady
 * @version 0.2
 * @since 0.2
 */
public class HistoryCrawler {

	/** The history head. */
	private State historyHead = null;

	/** The committable element. */
	private CommittableElement committableElement = null;

	/** The logger. */
	private Logger logger = null;

	/**
	 * Specialized constructor, which uses a given static state as the head for
	 * its searching. It operates independently of any Committable Element and
	 * all generated state paths are created relative to the given head state
	 * and not any predetermined CommittableElement head.
	 *
	 * @param historyHead
	 *            State.
	 */
	public HistoryCrawler(final State historyHead) {
		this.logger = Logger.getInstance();
		this.historyHead = historyHead;
	}

	/**
	 * Specialized constructor, which uses the head of the given
	 * CommittableElement as the base for its searching. As the head of the
	 * element changes through commits, the used head is remains the most recent
	 * commit.
	 *
	 * @param committableElement
	 *            CommittableElement.
	 */
	public HistoryCrawler(final CommittableElement committableElement) {
		this.logger = Logger.getInstance();
		this.committableElement = committableElement;
	}

	/**
	 * Dynamically access the head of the CommittableElement or returns the
	 * static head to which it was initialized.
	 * 
	 * @return dynamicHead - State.
	 */
	private State getHistoryHead() {
		if (this.committableElement != null)
			return this.committableElement.getHead();
		else
			return this.historyHead;
	}

	/**
	 * HISTORYCRAWLER METHODS *************************************************.
	 * 
	 * @param systemPath
	 *            the system path
	 * @return the history log
	 */

	/**
	 * Return a log of this element's history either along the system path or
	 * along alternate branches created by pull commits.
	 *
	 * @param systemPath
	 *            boolean.
	 * @return elementHistoryLog - String.
	 */
	public String getHistoryLog(final boolean systemPath) {
		final StringBuilder sb = new StringBuilder("");
		State ptr = this.getHistoryHead();
		if (ptr != null) {
			State[] prevList = ptr.listPrevious();
			sb.append(ptr.getLogEntry() + "\n");
			while (prevList.length > 0) {
				if (systemPath && (prevList.length > 1)) {
					ptr = prevList[1];
				} else {
					ptr = prevList[0];
				}
				sb.append("\n" + ptr.getLogEntry() + "\n");
				prevList = ptr.listPrevious();
			}
		}
		return sb.toString();
	}

	/**
	 * Find a given commit by searching from the given state along a path
	 * described by a single given step while avoiding all commitst whose hash
	 * is equal to one of the black list hashes.
	 *
	 * @param startState
	 *            State.
	 * @param searchStep
	 *            Step.
	 * @param blackList
	 *            List<String>.
	 * @return targetState - State.
	 */
	public State findCommitRelative(final State startState,
			final Step searchStep, final List<String> blackList) {
		State ptrState;
		try {
			if (startState instanceof StageState) {
				ptrState = new StageState(startState.getHash());
			} else {
				ptrState = new State(startState.getHash());
			}
			ptrState.updatePath(startState.clonePath());
		} catch (final Exception e) {
			return null;
		}
		// Check BlackList for Start State
		if ((blackList != null)
				&& blackList.contains(ptrState.getFingerprint().getHash()))
			return null;
		// No Distance Ptr is State
		else if (searchStep.getDistance() == 0)
			return ptrState;
		// Check For Negative Distance.
		else if (searchStep.getDistance() < 0) {
			if (ptrState.movePath(searchStep))
				return this.findCommitPath(this.getHistoryHead(),
						ptrState.clonePath(), blackList);
			else
				return null;
		} else if (searchStep.getBranch() > ptrState.listPrevious().length)
			return null;
		// Move Stepwise Along Step and Search Any States on the Blacklist
		int branch = searchStep.getBranch();
		State[] prevList = ptrState.listPrevious();
		// Path targetPath = new Path(ptrState.clonePath());
		final Path targetPath = ptrState.clonePath();
		for (int i = 0; i < searchStep.getDistance(); i++) {
			if (prevList.length >= branch) {
				ptrState = prevList[branch - 1];
				if ((blackList != null)
						&& blackList.contains(ptrState.getFingerprint()
								.getHash()))
					return null;
				targetPath.move(new Step(branch, 1));
				ptrState.updatePath(targetPath);
				prevList = ptrState.listPrevious();
				branch = 1;
			} else
				return null;
		}
		return ptrState;
	}

	/**
	 * Find a given commit by searching from the given state to identify the
	 * state with the given target hash while avoiding all commits whose hash is
	 * equal to one of the black listed hashes.
	 *
	 * @param startState
	 *            State.
	 * @param searchPath
	 *            Path.
	 * @param blackList
	 *            List<String>.
	 * @return targetState - State.
	 */
	public State findCommitPath(final State startState, final Path searchPath,
			final List<String> blackList) {
		State ptrState;
		try {
			if (startState instanceof StageState) {
				ptrState = new StageState(startState.getHash());
			} else {
				ptrState = new State(startState.getHash());
			}
			ptrState.updatePath(startState.clonePath());
		} catch (final Exception e) {
			return null;
		}
		// Check BlackList for Start State
		if ((blackList != null)
				&& blackList.contains(ptrState.getFingerprint().getHash()))
			return null;
		// Check for Path Validity
		if (!searchPath.isValid())
			return null;
		// Step Along Path (When Not Empty).
		while (!searchPath.isEmpty() && (ptrState != null)) {
			ptrState = this.findCommitRelative(ptrState,
					searchPath.removeFirstStep(), blackList);
		}
		return ptrState;
	}

	/**
	 * Find a given commit by searching from the given state to identify a state
	 * with the given target hash while avoiding all commits whose hash is equal
	 * to one of the black list hashes.
	 *
	 * @param startState
	 *            State.
	 * @param targetHash
	 *            String.
	 * @param blackList
	 *            List<String>.
	 * @param depthFirst
	 *            boolean.
	 * @return targetState - State.
	 */
	public State findCommitHash(final State startState,
			final String targetHash, final List<String> blackList,
			final boolean depthFirst) {
		State ptrState;
		try {
			if (startState instanceof StageState) {
				ptrState = new StageState(startState.getHash());
			} else {
				ptrState = new State(startState.getHash());
			}
			ptrState.updatePath(startState.clonePath());
		} catch (final Exception e) {
			this.logger.exception("Unable to Initialize PtrState.", e);
			return null;
		}
		// FIFO is Breadth First and LIFO is Depth First
		final Deque<State> searchList = new LinkedList<State>();
		// Check if ptrState is on Blacklist
		if ((blackList != null)
				&& blackList.contains(ptrState.getFingerprint().getHash()))
			return null;
		// Search for targetHash
		while ((ptrState != null)
				&& !ptrState.getFingerprint().getHash().equals(targetHash)) {
			final State[] prevList = ptrState.listPrevious();
			for (int i = prevList.length - 1; i >= 0; i--) {
				if ((blackList == null)
						|| !blackList.contains(prevList[i].getFingerprint()
								.getHash())) {
					prevList[i].updatePath(ptrState.clonePath());
					prevList[i].movePath(new Step(i + 1, 1));
					if (depthFirst) {
						searchList.offerFirst(prevList[i]);
					} else {
						searchList.offerLast(prevList[i]);
					}
				}
			}
			ptrState = searchList.poll();
		}
		return ptrState;
	}

	/**
	 * List all commits immediately following the committed state identified by
	 * the given state/commit hash.
	 *
	 * @param commitHash
	 *            String.
	 * @return followingCommits - State[].
	 */
	public State[] listFollowingCommits(final String commitHash) {
		final ArrayList<State> following = new ArrayList<State>();
		final Deque<State> searchList = new LinkedList<State>();
		State ptrState = this.getHistoryHead();
		while (ptrState != null) {
			final State[] prevStates = ptrState.listPrevious();
			for (int i = 0; i < prevStates.length; i++) {
				prevStates[i].updatePath(ptrState.clonePath());
				prevStates[i].movePath(new Step(i + 1, 1));
				if (commitHash.equals(prevStates[i].getFingerprint().getHash())) {
					following.add(ptrState);
				}
				searchList.offer(prevStates[i]);
			}
			ptrState = searchList.poll();
		}
		return following.toArray(new State[following.size()]);
	}

	/**
	 * List all commits immediately previous to the committed state identified
	 * the by the given state/commit hash.
	 *
	 * @param commitHash
	 *            String.
	 * @return previousCommits - State[].
	 */
	public State[] listPreviousCommits(final String commitHash) {
		final State target = this.findCommitHash(this.getHistoryHead(),
				commitHash, null, true);
		if (target == null)
			return new State[0];
		else
			return target.listPrevious();
	}

}
