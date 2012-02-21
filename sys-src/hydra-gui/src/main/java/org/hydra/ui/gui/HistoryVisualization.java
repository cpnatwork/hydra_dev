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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;

import org.apache.commons.collections15.Transformer;
import org.hydra.core.CommittableElement;

import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;

/**
 * Encapsulation of the process necessary to build a graphical representation of
 * a committable element's history.
 *
 * @since 0.2
 * @version 0.2
 * @author Scott A. Hady
 */
public class HistoryVisualization {

	/** The target. */
	private CommittableElement target;

	/** The size. */
	private Dimension size;

	/** The graph. */
	private Graph<GraphNode, String> graph;

	/** The layout. */
	private Layout<GraphNode, String> layout;

	/** The visualization. */
	private VisualizationViewer<GraphNode, String> visualization;

	/** The graph mouse. */
	private final DefaultModalGraphMouse graphMouse;

	/**
	 * Default Constructor, which initializes the default mouse manipulation
	 * mode.
	 */
	public HistoryVisualization() {
		this.graphMouse = new DefaultModalGraphMouse();
		this.graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
	}

	/**
	 * Builds a visual graph representation of the target committable element
	 * and formats it to fit the specified dimensions.
	 *
	 * @param tgtCommittable
	 *            CommittableElement.
	 * @param tgtSize
	 *            Dimension.
	 */
	public void build(final CommittableElement tgtCommittable,
			final Dimension tgtSize) {
		this.target = tgtCommittable;
		this.size = tgtSize;
		this.buildVisualization(this.buildLayout(this.buildGraph()));
	}

	/**
	 * Returns the graph resulting from the build process.
	 *
	 * @return visualComponent VisualizationViewer.
	 */
	public VisualizationViewer getResult() {
		return this.visualization;
	}

	/**
	 * Sets the mouse mode to use.
	 *
	 * @param mouseMode
	 *            ModalGraphMouse.Mode.
	 */
	public void setGraphMouseMode(final ModalGraphMouse.Mode mouseMode) {
		this.graphMouse.setMode(mouseMode);
	}

	/**
	 * Subprocess that builds the graph.
	 * 
	 * @return graph - Graph<GraphNode,String>.
	 */
	private Graph<GraphNode, String> buildGraph() {
		final GraphBuilder graphBuilder = new GraphBuilder();
		this.graph = graphBuilder.build(this.target);
		return this.graph;
	}

	/**
	 * Subprocess that lays out the graph that has been built in the previous
	 * step.
	 * 
	 * @param tgtGraph
	 *            Graph<GraphNode,String>.
	 * @return layout - Layout<GraphNode,String>.
	 */
	private Layout<GraphNode, String> buildLayout(
			final Graph<GraphNode, String> tgtGraph) {
		this.layout = new KKLayout<GraphNode, String>(tgtGraph);
		this.layout.setSize(new Dimension(this.size.width - 100,
				this.size.height - 50));
		return this.layout;
	}

	/**
	 * Subprocess that generates a visual component from the laid out graph,
	 * which may be incorporated into a GUI.
	 * 
	 * @param tgtLayout
	 *            Layout<GraphNode,String>.
	 * @return visualComponent - VisualizationViewer.
	 */
	private VisualizationViewer buildVisualization(
			final Layout<GraphNode, String> tgtLayout) {
		this.visualization = new VisualizationViewer<GraphNode, String>(
				this.layout);
		this.visualization.setPreferredSize(this.size);
		this.setupRendering(this.visualization.getRenderContext(),
				this.visualization.getRenderer());
		this.setupMouseManipulation();
		return this.visualization;
	}

	/**
	 * Sets up the graphical rendering context which will be applied to the
	 * graphical represenation.
	 * 
	 * @param renderContext
	 *            RenderContext.
	 * @param renderer
	 *            Renderer.
	 */
	private void setupRendering(final RenderContext renderContext,
			final Renderer renderer) {
		// Label Vertexes
		renderContext.setVertexLabelTransformer(new ToStringLabeller());
		renderContext.setEdgeLabelTransformer(new ToStringLabeller());
		renderer.getVertexLabelRenderer().setPosition(
				Renderer.VertexLabel.Position.CNTR);
		// Color Nodes (System Path, Valid Path and Current)
		final Transformer<GraphNode, Paint> vertexPaintTransformer = new Transformer<GraphNode, Paint>() {
			@Override
			public Paint transform(final GraphNode graphNode) {
				if (graphNode.isCurrent())
					return Color.ORANGE;
				else if (graphNode.isValidPath())
					return Color.GREEN;
				else
					return Color.RED;
			}
		};
		renderContext.setVertexFillPaintTransformer(vertexPaintTransformer);
	}

	/**
	 * Sets the mouse manipulation mode and key listener.
	 */
	private void setupMouseManipulation() {
		this.visualization.setGraphMouse(this.graphMouse);
		this.visualization.addKeyListener(this.graphMouse.getModeKeyListener());
	}

}
