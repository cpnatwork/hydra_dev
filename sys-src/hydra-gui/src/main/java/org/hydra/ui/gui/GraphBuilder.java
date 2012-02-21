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

import java.util.ArrayList;

import org.hydra.core.CommittableElement;

import edu.uci.ics.jung.graph.DirectedSparseGraph;

/**
 * Builds a graph representation given a committable target by first
 * representing the states as nodes and then joining the nodes.
 *
 * @since 0.2
 * @version 0.2
 * @author Scott A. Hady
 */
public class GraphBuilder {

	/** The target. */
	private CommittableElement target;

	/** The graph. */
	private DirectedSparseGraph<GraphNode, String> graph = null;

	/** The graph node. */
	private GraphNode graphNode;

	/**
	 * Builds a graph representation of the committable element's history.
	 *
	 * @param tgtCommittable
	 *            CommittableElement.
	 * @return graph - DirectedSparseGraph<GraphNode, String>.
	 */
	public DirectedSparseGraph<GraphNode, String> build(
			final CommittableElement tgtCommittable) {
		this.target = tgtCommittable;
		this.graph = new DirectedSparseGraph<GraphNode, String>();
		if ((this.target == null) || (this.target.getHead() == null))
			return this.graph;
		else {
			this.loadGraphNodes();
		}
		final ArrayList<GraphNode> searchList = new ArrayList<GraphNode>();
		final ArrayList<GraphNode> blackList = new ArrayList<GraphNode>();
		searchList.add(this.graphNode);
		blackList.add(this.graphNode);
		int depth = 0;
		while (!searchList.isEmpty()) {
			this.graphNode = searchList.remove(0);
			int branch = 0;
			for (GraphNode previous : this.graphNode.listPrevious()) {
				if (!blackList.contains(previous)) {
					searchList.add(previous);
					blackList.add(previous);
				} else {
					previous = blackList.get(blackList.indexOf(previous));
				}
				this.graph.addEdge(depth + "-" + branch, this.graphNode,
						previous);
				branch++;
			}
			searchList.remove(this.graphNode);
			depth++;
		}
		// Check For Single Commit - No Edges Produced
		if (this.graph.getEdgeCount() == 0) {
			this.graph.addVertex(this.graphNode);
		}
		// System.out.println("\n\nBuilt Graph ["+target+"]["+new
		// Date()+"]\n"+graph.toString());
		return this.graph;
	}

	/**
	 * Generates a representation of the committable element's history with
	 * nodes.
	 */
	protected void loadGraphNodes() {
		this.graphNode = new GraphNode(this.target.getHead());
		this.graphNode.setValidPath();
		this.graphNode.setCurrent(this.target.getCurrentHash());
	}

}
