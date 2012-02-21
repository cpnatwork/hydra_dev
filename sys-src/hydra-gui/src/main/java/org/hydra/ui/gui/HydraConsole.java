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
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.hydra.Hydra;
import org.hydra.core.Stage;
import org.hydra.ui.UIWriter;
import org.hydra.ui.cli.CLI;
import org.hydra.ui.commands.CommandSet;

/**
 * Console that allows the user to input commands as from the CLI and displays
 * the output to the user.
 *
 * @since 0.2
 * @version 0.2
 * @author Scott A. Hady
 */
public class HydraConsole extends JPanel implements KeyListener {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 02L;

	/** The cli. */
	private final CLI cli;

	/** The command prompt. */
	private JLabel commandPrompt;

	/** The console input. */
	private JTextArea consoleInput;

	/** The console output. */
	private JTextArea consoleOutput;

	/**
	 * Specialized Constructor which create creates a new console panel which
	 * executes command line commands through a cli using the given stage and
	 * commands.
	 *
	 * @param stage
	 *            Stage.
	 * @param commands
	 *            CommandSet.
	 */
	public HydraConsole(final Stage stage, final CommandSet commands) {
		super(new BorderLayout());
		this.cli = new CLI(stage, commands);
		this.initialize();
	}

	/**
	 * Inititializes the input and output subcomponents of the panel.
	 */
	protected void initialize() {
		this.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		// Add Input Console
		final JPanel inputPanel = new JPanel(new BorderLayout());
		this.add(inputPanel, BorderLayout.NORTH);
		this.commandPrompt = new JLabel();
		inputPanel.add(this.commandPrompt, BorderLayout.LINE_START);
		this.consoleInput = new JTextArea();
		this.consoleInput.setEditable(true);
		this.consoleInput.setColumns(50);
		this.consoleInput.addKeyListener(this);
		inputPanel.add(this.consoleInput, BorderLayout.CENTER);
		this.setCommandPrompt();
		// Add Output Console
		this.consoleOutput = new JTextArea();
		this.consoleOutput.setFont(new Font("Monospaced", Font.PLAIN, 12));
		this.consoleOutput.setEditable(false);
		this.consoleOutput.setRows(20);
		this.consoleOutput.setColumns(50);
		this.add(new JScrollPane(this.consoleOutput), BorderLayout.CENTER);
		this.cli.getWriter();
		// Reorient Command Writer to OutputConsole
		UIWriter.setOutputConsole(this.consoleOutput);
		this.cli.getWriter();
		UIWriter.useConsole(true);
		this.cli.getWriter().println(Hydra.getLongHeader());
		this.cli.getWriter().printLineBreak();
	}

	/**
	 * Sets the command prompt of the input to the designated command line from
	 * CLI.
	 */
	protected void setCommandPrompt() {
		this.consoleInput.setText("");
		this.consoleInput.setRows(1);
		this.commandPrompt.setText(this.cli.getCommandPrompt());
	}

	/**
	 * Executes the desired command inputted by the user.
	 */
	protected void processCommandLine() {
		final String commandLine = this.consoleInput.getText().trim();
		this.setCommandPrompt();
		this.cli.executeCommand(commandLine);
		this.cli.getWriter().printLineBreak();
		this.consoleOutput.selectAll();
		// Refresh Root
		SwingUtilities.getRoot(this).repaint();
	}

	/**
	 * KEYLISTENER (IMPLEMENATION) ********************************************.
	 * 
	 * @param keyEvent
	 *            the key event
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Reacts (Null Implementation to key typed events.
	 */
	@Override
	public void keyTyped(final KeyEvent keyEvent) {
		// Null Implementation.
	}

	/**
	 * {@inheritDoc}
	 *
	 * Reacts (Null Implementation) to key pressed events.
	 */
	@Override
	public void keyPressed(final KeyEvent keyEvent) {
		// Null Implementation.
	}

	/**
	 * {@inheritDoc}
	 *
	 * Reacts to key released events by processing the command line if the ENTER
	 * key is pressed.
	 */
	@Override
	public void keyReleased(final KeyEvent keyEvent) {
		if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
			this.processCommandLine();
		}
	}

}
