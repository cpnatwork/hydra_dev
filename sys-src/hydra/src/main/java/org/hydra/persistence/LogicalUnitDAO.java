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
import java.util.Scanner;

import org.hydra.core.CommittableElement;
import org.hydra.core.InvalidElementException;
import org.hydra.core.LogicalUnit;

/**
 * DAO implementation for the Logical Unit.
 *
 * @author Scott A. Hady
 * @version 0.2
 * @since 0.2
 */
public class LogicalUnitDAO extends DataAccessObject {

	/** The logical unit. */
	private final LogicalUnit logicalUnit;

	/**
	 * Specialized Constructor which take the Logical Unit on which to operate.
	 *
	 * @param logicalUnit
	 *            LogicalUnit.
	 */
	public LogicalUnitDAO(final LogicalUnit logicalUnit) {
		this.logicalUnit = logicalUnit;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Load the logical unit's references from repository.
	 */
	@Override
	public boolean load() throws InvalidElementException {
		Scanner scanner = null;
		final File target = this.logicalUnit.cloneRepositoryFile();
		try {
			scanner = new Scanner(new FileInputStream(target), "UTF-8");
			while (scanner.hasNextLine()) {
				this.parseMember(scanner.nextLine());
			}
			return true;
		} catch (final Exception e) {
			this.logger.exception("Unable to Parse Logical Unit File ["
					+ target + "].", e);
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
	 * Parses the member.
	 * 
	 * @param memberStr
	 *            the member str
	 * @throws InvalidElementException
	 *             the invalid element exception
	 */
	private void parseMember(final String memberStr)
			throws InvalidElementException {
		try {
			final String[] splitStr = memberStr
					.split(DataAccessObject.SEP_TOKEN);
			if (splitStr[0].equals(CommittableElement.TOKEN_HEAD)) {
				this.logicalUnit.setHead(splitStr[1]);
			} else if (splitStr[0].equals(CommittableElement.TOKEN_CURRENT)) {
				this.logicalUnit.setCurrent(splitStr[1]);
			} else if (splitStr[0].equals(CommittableElement.TOKEN_STASH)) {
				this.logicalUnit.setContents(splitStr[1]);
			}
		} catch (final Exception e) {
			this.logger.exception("Logical Unit Parsing Failure [" + memberStr
					+ "].", e);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * Record the logical unit's references into the repository.
	 */
	@Override
	public boolean record() {
		return this.storeContents(this.logicalUnit.describe(),
				this.logicalUnit.cloneRepositoryFile());
	}

	/**
	 * {@inheritDoc}
	 *
	 * Retrieve the logical unit from the repository and restore to the
	 * workspace. No Operation, not relevant for logical units, always returns
	 * false.
	 */
	@Override
	public boolean retrieve() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Store the logical unit's workspace content in the repository. No
	 * operation, not relevant for logical units, always returns false.
	 */
	@Override
	public boolean store() {
		return false;
	}

}
