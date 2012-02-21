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
package org.hydra.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

import org.hydra.core.Container;
import org.hydra.core.Element;
import org.hydra.core.InvalidElementException;
import org.hydra.core.StageState;
import org.hydra.core.State;

/**
 * DAO Implementation for a state.
 *
 * @author Scott A. Hady
 * @version 0.2
 * @since 0.2
 */
public class StateDAO extends DataAccessObject {

	/** The state. */
	private final State state;

	/** The date format. */
	private final DateFormat dateFormat = new SimpleDateFormat(
			"E MMM dd HH:mm:ss z yyyy", new Locale("en", "US"));

	/**
	 * Specialized constructor which takes a state on which to operate.
	 *
	 * @param state
	 *            State.
	 */
	public StateDAO(final State state) {
		this.state = state;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Load's the state's metadata and references from the repository into the
	 * state object.
	 */
	@Override
	public boolean load() {
		try {
			return this.loadScanner();
		} catch (final Exception e) {
			this.logger.exception("Unable to Load State [" + this.state + "].",
					e);
			return false;
		}
	}

	/**
	 * Load scanner.
	 * 
	 * @return true, if successful
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	private boolean loadScanner() throws InvalidElementException {
		Scanner scanner = null;
		final File target = this.state.cloneRepositoryFile();
		try {
			scanner = new Scanner(new FileInputStream(target), "UTF-8");
			while (scanner.hasNextLine()) {
				this.parseMember(scanner.nextLine());
			}
			return true;
		} catch (final Exception e) {
			this.logger.exception("Unable to Parse State File [" + target
					+ "].", e);
			return false;
		} finally {
			if (scanner != null) {
				try {
					scanner.close();
				} catch (final Exception e) {
					this.logger.exception("Unable to Close Scanner.", e);
				}
			}
		}
	}

	/**
	 * Parse the separate members of the states repository location.
	 *
	 * @param memberString
	 *            String.
	 * @throws org.hydra.core.InvalidElementException
	 *             the invalid element exception
	 */
	protected void parseMember(final String memberString)
			throws InvalidElementException {
		final String[] splitStr = memberString
				.split(DataAccessObject.SEP_TOKEN);
		if (memberString.equals(State.HEADER)) { // HEADER
			// Do Nothing (Ignore)
		} else if (splitStr[0].equals(State.TOKEN)) { // UUID
			this.state.setUUID(splitStr[1]);
		} else if (splitStr[0].equals(Container.TOKEN)) { // CONTENTS
			this.state.setContentsHash(splitStr[1]);
		} else if (splitStr[0].equals(Element.TOKEN_METADATA)) { // METADATA
			try {
				this.state.setTimestamp(this.dateFormat.parse(splitStr[1]));
			} catch (final Exception exception) {
				this.logger.exception("Unable to Parse State Timestamp ["
						+ splitStr[1] + "].", exception);
				this.state.setTimestamp(new Date());
			}
			this.state.setUserName(splitStr[2]);
			this.state.setValidity(Boolean.parseBoolean(splitStr[3]));
			this.state.setMessage(splitStr[4]);
		} else if (splitStr[0].equals(State.TOKEN_PREVIOUS)) { // PREVIOUS LIST
			if (this.state instanceof StageState) {
				this.state.addPrevious(new StageState(splitStr[1]));
			} else {
				this.state.addPrevious(new State(splitStr[1]));
			}
		} else if (splitStr[0].equals(State.TOKEN_VALIDPREVIOUS)) { // VALID_PREVIOUS
			if (splitStr[1].equals("null")) {
				this.state.setValidPathPrevious(null);
			} else {
				if (this.state instanceof StageState) {
					this.state
							.setValidPathPrevious(new StageState(splitStr[1]));
				} else {
					this.state.setValidPathPrevious(new State(splitStr[1]));
				}
			}
		} else {
			this.parseMemberExtension(memberString);
		}
	}

	/**
	 * Template Method - Null implementation to be extended for the specialized
	 * StageState.
	 *
	 * @param memberString
	 *            String.
	 */
	protected void parseMemberExtension(final String memberString) {
		this.logger
				.warning("Found Unexcepted Member While Parsing State File ["
						+ memberString + "].");
	}

	/**
	 * {@inheritDoc}
	 *
	 * Record the state's metadata and referential information into the state's
	 * repository location.
	 */
	@Override
	public boolean record() {
		return this.storeContents(this.state.describe(),
				this.state.cloneRepositoryFile());
	}

	/**
	 * {@inheritDoc}
	 *
	 * Restore's the state's content to the workspace.
	 */
	@Override
	public boolean retrieve() {
		try {
			return (this.state.isTemporary() ? true : this.state
					.cloneContents().retrieve());
		} catch (final Exception e) {
			this.logger.exception("Unable to Retrieve State [" + this.state
					+ "]", e);
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * Persist the state's workspace content, if not temporary, and then record
	 * the state's metadata.
	 */
	@Override
	public boolean store() {
		try {
			return (this.state.isTemporary() ? true : this.state
					.cloneContents().store()) && this.record();
		} catch (final Exception e) {
			this.logger.exception("Unable to Store State [" + this.state + "]",
					e);
			return false;
		}
	}

}
