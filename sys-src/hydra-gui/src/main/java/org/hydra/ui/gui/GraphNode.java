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
package org.hydra.ui.gui;

import org.hydra.core.State;

/**
 * Recursive structure that represents a committed state in the history graph.
 *
 * @since 0.2
 * @version 0.2
 * @author Scott A. Hady
 */
public class GraphNode {

	/** The state hash. */
	private String stateHash;

	/** The is current. */
	private boolean isCurrent = false;

	/** The is valid path. */
	private boolean isValidPath = false;

	/** The previous nodes. */
	private GraphNode[] previousNodes;

	/**
	 * Specialized Constructor which receives the committed state that the node
	 * should represent.
	 *
	 * @param committedState
	 *            State.
	 */
	public GraphNode(final State committedState) {
		if (committedState == null) {
			System.out.println("NULL STATE");
		} else {
			this.stateHash = committedState.getHash();
			final State[] previousStates = committedState.listPrevious();
			this.previousNodes = new GraphNode[previousStates.length];
			for (int i = 0; i < previousStates.length; i++) {
				this.previousNodes[i] = new GraphNode(previousStates[i]);
			}
		}
	}

	/**
	 * Returns the represented state's fingerprint hash.
	 *
	 * @return stateFingerprintHash - String.
	 */
	public String getStateHash() {
		return this.stateHash;
	}

	/**
	 * Return's if this state is identified as the current state.
	 *
	 * @return isCurrentState - boolean.
	 */
	public boolean isCurrent() {
		return this.isCurrent;
	}

	/**
	 * Returns if this state lies on the valid path.
	 *
	 * @return isValidPath - boolean.
	 */
	public boolean isValidPath() {
		return this.isValidPath;
	}

	/**
	 * Returns an array of nodes that represent the previous states, relative to
	 * the represented state.
	 *
	 * @return previousNodes - GraphNode[].
	 */
	public GraphNode[] listPrevious() {
		return this.previousNodes;
	}

	/**
	 * Sets the current flag if this node's state hash matches the current hash
	 * or forwards the request to its sub nodes.
	 *
	 * @param currentHash
	 *            String.
	 */
	public void setCurrent(final String currentHash) {
		if (this.stateHash.equals(currentHash)) {
			this.isCurrent = true;
		} else {
			if (this.previousNodes.length != 0) {
				for (final GraphNode subNode : this.previousNodes) {
					subNode.setCurrent(currentHash);
				}
			}
		}
	}

	/**
	 * Sets the valid path flag and forwards the request to the next valid path
	 * node.
	 */
	public void setValidPath() {
		this.isValidPath = true;
		if (this.previousNodes.length != 0) {
			this.previousNodes[0].setValidPath();
		}
	}

	/**
	 * OBJECT METHODS (OVERRIDDEN) ********************************************.
	 * 
	 * @return the string
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Returns a String representation of the node that will be depicted on the
	 * graph.
	 */
	@Override
	public String toString() {
		return this.stateHash.substring(0, 8) + "...";
	}

	/**
	 * {@inheritDoc}
	 *
	 * Takes an object and determines if it is equal to this node.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof GraphNode)
			return (this.stateHash.equals(((GraphNode) obj).getStateHash()));
		else
			return false;
	}

}
