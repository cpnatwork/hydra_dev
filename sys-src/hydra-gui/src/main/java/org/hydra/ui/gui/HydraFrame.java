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
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import org.hydra.core.Stage;
import org.hydra.ui.commands.CommandSet;
import org.hydra.ui.gui.commands.GUICmdGraphMouseMode;
import org.hydra.ui.gui.commands.GUICmdGraphRevert;

import edu.uci.ics.jung.visualization.control.ModalGraphMouse;

/**
 * Base JFrame of the GUI.
 *
 * @since 0.2
 * @version 0.2
 * @author Scott A. Hady
 */
public class HydraFrame extends JFrame {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The Constant DEFAULT_TITLE. */
	public static final String DEFAULT_TITLE = "Hydra GUI";

	/** The stage. */
	private final Stage stage;

	/** The commands. */
	private final CommandSet commands;
	// Components
	/** The screen dimensions. */
	private Dimension screenDimensions;

	/** The split pane. */
	private JSplitPane splitPane;

	/** The hydra menu. */
	private HydraMenu hydraMenu;

	/** The hydra explorer. */
	private HydraExplorer hydraExplorer;

	/** The hydra console. */
	private HydraConsole hydraConsole;

	/**
	 * Specialized Constructor that receives the command set to execute.
	 *
	 * @param tgtStage
	 *            Stage.
	 * @param useCommands
	 *            CommandSet.
	 */
	public HydraFrame(final Stage tgtStage, final CommandSet useCommands) {
		super(HydraFrame.DEFAULT_TITLE);
		this.stage = tgtStage;
		this.commands = useCommands;
		this.initialize();
	}

	/**
	 * Intializes the frame and all of the sub components.
	 */
	private void initialize() {
		this.setExtendedState(Frame.MAXIMIZED_BOTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(true);
		this.screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
		this.getContentPane().setLayout(new BorderLayout());
		// Initialize SubComponents and GUI Commands
		this.initializeSplitPane();
		this.initializeExplorer();
		this.initializeConsole();
		this.initializeMenuBar();
		this.pack();
		this.hydraExplorer.refreshExplorer();
	}

	/**
	 * Initializes the split pane, explorer on top and console on bottom.
	 */
	private void initializeSplitPane() {
		this.splitPane = new JSplitPane();
		this.splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		this.splitPane.setDividerLocation(this.screenDimensions.height / 2);
		this.getContentPane().add(this.splitPane, BorderLayout.CENTER);
	}

	/**
	 * Initializes the hydra visualization panel, which provides a visual
	 * representation of the managed logical units.
	 */
	private void initializeExplorer() {
		this.hydraExplorer = new HydraExplorer(this.stage);
		this.splitPane.setTopComponent(this.hydraExplorer);
		this.initializeExplorerCommands();
	}

	/**
	 * Initializes the gui commands relevant for the hydra explorer.
	 */
	private void initializeExplorerCommands() {
		this.commands.add(new GUICmdGraphMouseMode("Transforming",
				"GUICmdGraphMouseMode.Transforming", this.hydraExplorer,
				ModalGraphMouse.Mode.TRANSFORMING));
		this.commands.add(new GUICmdGraphMouseMode("Picking",
				"GUICmdGraphMouseMode.Picking", this.hydraExplorer,
				ModalGraphMouse.Mode.PICKING));
		this.commands.add(new GUICmdGraphRevert("Revert to Selected",
				GUICmdGraphRevert.DEFAULT_ID, this.hydraExplorer));
	}

	/**
	 * Initialize the hydra console which is used to display output to the user
	 * and to receive command line input from the user.
	 */
	private void initializeConsole() {
		this.hydraConsole = new HydraConsole(this.stage, this.commands);
		this.splitPane.setBottomComponent(this.hydraConsole);
	}

	/**
	 * Initialize the menu bar which provides selectable actions for the user to
	 * execute.
	 */
	private void initializeMenuBar() {
		this.hydraMenu = new HydraMenu(this.commands);
		this.setJMenuBar(this.hydraMenu);
	}

	/**
	 * COMPONENT METHODS (OVERRIDDEN) *****************************************.
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Refresh the visualizations.
	 */
	@Override
	public void repaint() {
		this.hydraExplorer.refreshExplorer();
	}

}
