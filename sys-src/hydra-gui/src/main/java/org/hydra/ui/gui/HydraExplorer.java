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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;

import org.hydra.core.CommittableElement;
import org.hydra.core.LogicalUnit;
import org.hydra.core.Stage;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;

/**
 * Display panel that provides a visual depiction of various aspects of the
 * hydra system.
 *
 * @since 0.2
 * @version 0.2
 * @author Scott A. Hady
 */
public class HydraExplorer extends JPanel implements ActionListener {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The stage. */
	private final Stage stage;

	/** The visualization dimension. */
	private Dimension visualizationDimension;

	/** The visualization target. */
	private CommittableElement visualizationTarget;

	/** The history builder. */
	private final HistoryVisualization historyBuilder;

	/** The history graph. */
	private VisualizationViewer historyGraph;

	/** The tab pane. */
	private JTabbedPane tabPane;

	/** The history panel. */
	private JPanel historyPanel;

	/** The status panel. */
	private JPanel statusPanel;

	/** The content panel. */
	private JPanel contentPanel;

	/** The control panel. */
	private JPanel controlPanel;

	/** The radio buttons. */
	private ButtonGroup radioButtons;

	/** The radio panel. */
	private JPanel radioPanel;

	/** The refresh button. */
	private JButton refreshButton;

	/**
	 * Specialized Constructor that designates which injects the stage to
	 * explore.
	 *
	 * @param targetStage
	 *            Stage.
	 */
	public HydraExplorer(final Stage targetStage) {
		super();
		this.stage = targetStage;
		this.visualizationTarget = targetStage;
		this.historyBuilder = new HistoryVisualization();
		this.initialize();
	}

	/**
	 * Delegation method that delegates the mouse mode to use to the history
	 * graph builder.
	 *
	 * @param mouseMode
	 *            ModalGraphMouse.Mode.
	 */
	public void setGraphMouseMode(final ModalGraphMouse.Mode mouseMode) {
		this.historyBuilder.setGraphMouseMode(mouseMode);
	}

	/**
	 * Return the current history graph.
	 *
	 * @return historyGraph - VisualizationViewer.
	 */
	public VisualizationViewer getHistoryGraph() {
		return this.historyGraph;
	}

	/**
	 * Return the current visualization target.
	 *
	 * @return visualizationTarget - CommittableElement.
	 */
	public CommittableElement getVisualizationTarget() {
		return this.visualizationTarget;
	}

	/**
	 * Initializes the explorer panel.
	 */
	protected void initialize() {
		this.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		this.setLayout(new BorderLayout());
		this.initializeVisualizationTabs();
		this.initializeControlPanel();
	}

	/**
	 * Initialize the Visualization Tabs.
	 */
	protected void initializeVisualizationTabs() {
		this.tabPane = new JTabbedPane();
		this.add(this.tabPane, BorderLayout.CENTER);
		this.historyPanel = new JPanel(new BorderLayout());
		this.loadHistoryVisualization();
		this.tabPane.addTab("History", this.historyPanel);
		this.statusPanel = new JPanel(new BorderLayout());
		this.loadStatusVisualization();
		this.tabPane.add("Status", this.statusPanel);
		this.contentPanel = new JPanel(new BorderLayout());
		this.loadContentVisualization();
		this.tabPane.add("Content", this.contentPanel);
	}

	/**
	 * Initialize the Control Panel.
	 */
	protected void initializeControlPanel() {
		this.controlPanel = new JPanel(new BorderLayout());
		this.controlPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		this.add(this.controlPanel, BorderLayout.LINE_END);
		this.controlPanel.add(new JLabel("Committable Elements"),
				BorderLayout.NORTH);
		this.radioPanel = new JPanel(new GridLayout(0, 1));
		this.radioPanel.setBorder(BorderFactory
				.createLineBorder(Color.LIGHT_GRAY));
		this.controlPanel.add(this.radioPanel, BorderLayout.CENTER);
		this.loadRadioButtons("Stage");
		this.refreshButton = new JButton("Refresh");
		this.refreshButton.addActionListener(this);
		this.controlPanel.add(this.refreshButton, BorderLayout.SOUTH);
	}

	/**
	 * Loads a graphical representation of the targeted committable element's
	 * committed history.
	 */
	protected void loadHistoryVisualization() {
		this.historyPanel.removeAll();
		this.visualizationDimension = new Dimension(
				(this.getSize().width / 3) * 2, this.getSize().height);
		this.historyBuilder.build(this.visualizationTarget,
				this.visualizationDimension);
		this.historyGraph = this.historyBuilder.getResult();
		this.historyPanel.add(this.historyGraph, BorderLayout.CENTER);
	}

	/**
	 * Loads a graphical representation of the targetted committable element's
	 * status.
	 */
	protected void loadStatusVisualization() {
		this.statusPanel.removeAll();
		final JLabel statusLabel = new JLabel(
				"Status Visualization Not Yet Implemented");
		this.statusPanel.add(statusLabel, BorderLayout.CENTER);
	}

	/**
	 * Loads a graphical representaiton of the targetted committable element's
	 * contents.
	 */
	protected void loadContentVisualization() {
		this.contentPanel.removeAll();
		final JLabel contentLabel = new JLabel(
				"Content Visualization Not Yet Implemented");
		this.contentPanel.add(contentLabel, BorderLayout.CENTER);

	}

	/**
	 * Builds a set of radio buttons that represent the committable elements.
	 *
	 * @param selection
	 *            String.
	 */
	protected void loadRadioButtons(final String selection) {
		this.radioButtons = new ButtonGroup();
		JRadioButton radioButton = new JRadioButton("Stage");
		radioButton.setActionCommand("Stage");
		if (radioButton.getActionCommand().equals(selection)) {
			radioButton.setSelected(true);
		} else {
			radioButton.setSelected(false);
		}
		this.radioButtons.add(radioButton);
		this.radioPanel.add(radioButton);
		final LogicalUnit[] sorted = this.stage.listManaged();
		Arrays.sort(sorted);
		for (final LogicalUnit logicalUnit : sorted) {
			radioButton = new JRadioButton(logicalUnit.getName());
			radioButton.setActionCommand(logicalUnit.getName());
			if (radioButton.getActionCommand().equals(selection)) {
				radioButton.setSelected(true);
			} else {
				radioButton.setSelected(false);
			}
			this.radioButtons.add(radioButton);
			this.radioPanel.add(radioButton);
		}
	}

	/**
	 * Refresh the explorer's visualization and control panel.
	 */
	public void refreshExplorer() {
		this.refreshVisualization();
		this.refreshControlPanel();
	}

	/**
	 * Refreshes the targeted visualization.
	 */
	public void refreshVisualization() {
		this.loadHistoryVisualization();
		this.revalidate();
		this.repaint();
	}

	/**
	 * Refreshes the control panel.
	 */
	public void refreshControlPanel() {
		final String selection = this.radioButtons.getSelection()
				.getActionCommand();
		final Enumeration<AbstractButton> buttons = this.radioButtons
				.getElements();
		while (buttons.hasMoreElements()) {
			this.radioPanel.remove(buttons.nextElement());
		}
		this.loadRadioButtons(selection);
		this.revalidate();
		this.repaint();
	}

	/**
	 * ACTIONLISTENER (IMPLEMENATION) *****************************************.
	 * 
	 * @param actionEvent
	 *            the action event
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Updates the visualizaition target.
	 */
	@Override
	public void actionPerformed(final ActionEvent actionEvent) {
		if (this.radioButtons.getSelection().getActionCommand().equals("Stage")) {
			this.visualizationTarget = this.stage;
		} else {
			this.visualizationTarget = this.stage
					.getLogicalUnit(this.radioButtons.getSelection()
							.getActionCommand());
		}
		this.refreshVisualization();
		this.refreshControlPanel();
	}

}
