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

import org.hydra.core.LogicalUnit;
import org.hydra.core.StageState;

/**
 * DAO Implementation for the stage's state, which is extended to deal
 * specifically with the logical units.
 *
 * @author Scott A. Hady
 * @version 0.2
 * @since 0.2
 */
public class StageStateDAO extends StateDAO {

	/** The stage state. */
	private final StageState stageState;

	/**
	 * Instantiates a new stage state dao.
	 *
	 * @param stageState
	 *            the stage state
	 */
	public StageStateDAO(final StageState stageState) {
		super(stageState);
		this.stageState = stageState;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Template Method - Provides the parsing of the logical unit members which
	 * are only relevant for the stage states.
	 */
	@Override
	protected void parseMemberExtension(final String memberString) {
		final String[] splitStr = memberString
				.split(DataAccessObject.SEP_TOKEN);
		if (splitStr[0].equals(LogicalUnit.TOKEN)) {
			this.stageState.loadLogicalUnitReference(splitStr[1], splitStr[2]);
		} else {
			this.logger.warning("Unrecognized Parsing Member [" + memberString
					+ "].");
		}
	}

}
