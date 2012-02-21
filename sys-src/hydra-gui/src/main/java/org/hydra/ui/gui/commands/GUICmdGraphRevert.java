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
package org.hydra.ui.gui.commands;

import java.util.Iterator;

import org.hydra.ui.commands.Command;
import org.hydra.ui.gui.GraphNode;
import org.hydra.ui.gui.HydraExplorer;

import edu.uci.ics.jung.visualization.control.ModalGraphMouse;

/**
 * Reverts the visualization target to the node selected in the commit history
 * graph.
 *
 * @since 0.2
 * @version 0.2
 * @author Scott A. Hady
 */
public class GUICmdGraphRevert extends Command {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The Constant DEFAULT_NAME. */
	public static final String DEFAULT_NAME = "Graph Revert";

	/** The Constant DEFAULT_ID. */
	public static final String DEFAULT_ID = "GUICmdGraphRevert";

	/** The explorer. */
	private final HydraExplorer explorer;

	/** The mouse mode. */
	private ModalGraphMouse.Mode mouseMode;

	/**
	 * Specialize constructor which specifies the explorer to use but uses the
	 * default command name and id.
	 *
	 * @param tgtExplorer
	 *            HydraExplorer.
	 */
	public GUICmdGraphRevert(final HydraExplorer tgtExplorer) {
		super(GUICmdGraphRevert.DEFAULT_NAME, GUICmdGraphRevert.DEFAULT_ID);
		this.explorer = tgtExplorer;
	}

	/**
	 * Specialized constructor which specifies the command name and id to use as
	 * well as the explorer.
	 *
	 * @param cmdName
	 *            String.
	 * @param cmdId
	 *            String.
	 * @param tgtExplorer
	 *            HydraExplorer.
	 */
	public GUICmdGraphRevert(final String cmdName, final String cmdId,
			final HydraExplorer tgtExplorer) {
		super(cmdName, cmdId);
		this.explorer = tgtExplorer;
	}

	/**
	 * COMMAND METHODS (OVERRIDDEN) *******************************************.
	 * 
	 * @return true, if successful
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Revert the currently visualization target to the selected state.
	 */
	@Override
	public boolean execute() {
		boolean success = false;
		String pickedStateHash = "null";
		final Iterator pickedStateIterator = this.explorer.getHistoryGraph()
				.getPickedVertexState().getPicked().iterator();
		if (pickedStateIterator.hasNext()) {
			final GraphNode pickedState = (GraphNode) pickedStateIterator
					.next();
			pickedStateHash = pickedState.getStateHash();
			success = this.explorer.getVisualizationTarget().revert(
					pickedStateHash);
		}
		this.printResult(pickedStateHash, success);
		this.explorer.refreshVisualization();
		return success;
	}

	/**
	 * Print the result through the UIWriter.
	 * 
	 * @param pickedStateHash
	 *            String.
	 * @param success
	 *            boolean.
	 */
	private void printResult(final String pickedStateHash, final boolean success) {
		if (success) {
			this.writer.print("Reverted ");
		} else {
			this.writer.print("FAILURE: Unable to Revert ");
		}
		this.writer.println("["
				+ this.explorer.getVisualizationTarget().getName()
				+ "] to Hash:[" + pickedStateHash + "].\n");
		this.writer.printLineBreak();

	}

}
