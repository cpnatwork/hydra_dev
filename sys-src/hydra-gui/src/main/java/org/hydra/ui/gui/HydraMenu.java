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

import java.awt.event.InputEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import org.hydra.ui.commands.CmdExit;
import org.hydra.ui.commands.CmdHelp;
import org.hydra.ui.commands.CmdLog;
import org.hydra.ui.commands.CmdStatus;
import org.hydra.ui.commands.CommandSet;
import org.hydra.ui.gui.commands.GUICmdGraphRevert;

/**
 * Encapsulates the menu of the Hydra GUI, which provides a user selectable
 * method for employing the commands.
 *
 * @since 0.2
 * @version 0.2
 * @author Scott A. Hady
 */
public class HydraMenu extends JMenuBar {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The commands. */
	private final CommandSet commands;

	/**
	 * Specialized constructor that injects the set of commands to use.
	 *
	 * @param useCommands
	 *            CommandSet.
	 */
	public HydraMenu(final CommandSet useCommands) {
		super();
		this.commands = useCommands;
		this.initialize();
	}

	/**
	 * Initialize each of the menu items and associate them to the appropriate
	 * command in the command set.
	 */
	protected void initialize() {
		this.initializeFileMenu();
		this.initializeEditMenu();
		this.initializeNavigationMenu();
		this.initializeHelpMenu();
	}

	/**
	 * Initialize the 'File' Menu.
	 */
	protected void initializeFileMenu() {
		final JMenu fileMenu = new JMenu("File");
		this.add(fileMenu);
		// Status
		final JMenuItem sysStatusItem = new JMenuItem();
		sysStatusItem.setAction(this.commands.findById(CmdStatus.DEFAULT_ID));
		sysStatusItem.setAccelerator(KeyStroke.getKeyStroke('S',
				InputEvent.CTRL_DOWN_MASK));
		fileMenu.add(sysStatusItem);
		// Log
		final JMenuItem sysLogItem = new JMenuItem();
		sysLogItem.setAction(this.commands.findById(CmdLog.DEFAULT_ID));
		sysLogItem.setAccelerator(KeyStroke.getKeyStroke('L',
				InputEvent.CTRL_DOWN_MASK));
		fileMenu.add(sysLogItem);
		// Separator
		fileMenu.add(new JSeparator());
		// Exit
		final JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.setAction(this.commands.findById(CmdExit.DEFAULT_ID));
		exitItem.setAccelerator(KeyStroke.getKeyStroke('Q',
				InputEvent.CTRL_DOWN_MASK));
		fileMenu.add(exitItem);
	}

	/**
	 * Initialize the 'Edit' Menu.
	 */
	protected void initializeEditMenu() {
		final JMenu editMenu = new JMenu("Edit");
		this.add(editMenu);
		// History Mouse Mode
		final JMenu mouseModeSubMenu = new JMenu("History Mouse Mode");
		editMenu.add(mouseModeSubMenu);
		// Transforming
		final JMenuItem transformingItem = new JMenuItem(
				this.commands.findById("GUICmdGraphMouseMode.Transforming"));
		transformingItem.setAccelerator(KeyStroke.getKeyStroke('T',
				InputEvent.ALT_DOWN_MASK));
		mouseModeSubMenu.add(transformingItem);
		// Picking
		final JMenuItem pickingItem = new JMenuItem(
				this.commands.findById("GUICmdGraphMouseMode.Picking"));
		pickingItem.setAccelerator(KeyStroke.getKeyStroke('P',
				InputEvent.ALT_DOWN_MASK));
		mouseModeSubMenu.add(pickingItem);
	}

	/**
	 * Initialize the 'Navigation' menu.
	 */
	protected void initializeNavigationMenu() {
		final JMenu navigationMenu = new JMenu("Navigation");
		this.add(navigationMenu);
		// Revert to Picked
		final JMenuItem revertPickedItem = new JMenuItem();
		revertPickedItem.setAction(this.commands
				.findById(GUICmdGraphRevert.DEFAULT_ID));
		revertPickedItem.setAccelerator(KeyStroke.getKeyStroke('R',
				InputEvent.ALT_DOWN_MASK));
		navigationMenu.add(revertPickedItem);
	}

	/**
	 * Initialize the 'Help' menu.
	 */
	protected void initializeHelpMenu() {
		final JMenu helpMenu = new JMenu("Help");
		this.add(helpMenu);
		// Help
		final JMenuItem sysHelpItem = new JMenuItem();
		sysHelpItem.setAction(this.commands.findById(CmdHelp.DEFAULT_ID));
		sysHelpItem.setAccelerator(KeyStroke.getKeyStroke('H',
				InputEvent.CTRL_DOWN_MASK));
		helpMenu.add(sysHelpItem);
	}

}
