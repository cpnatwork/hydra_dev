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
package org.hydra.utilities.diff;

import java.util.ArrayList;

/**
 * Encapsulates the set of changes that may be applied to a original file to
 * generate the resulting file.
 *
 * @since 0.2
 * @version 0.2
 * @author Scott A. Hady
 */
public class ChangeSet {

	/** The change list. */
	private final ArrayList<Change> changeList = new ArrayList<Change>();

	/**
	 * Add a given change to the set of changes.
	 *
	 * @param change
	 *            Change.
	 * @return success - boolean.
	 */
	public boolean add(final Change change) {
		return this.changeList.add(change);
	}

	/**
	 * Return an array of all of the changes within the set.
	 *
	 * @return changes - Change[].
	 */
	public Change[] listChanges() {
		return this.changeList.toArray(new Change[this.changeList.size()]);
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return a string describing the complete set of changes.
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Change Set:\n");
		for (final Change change : this.changeList) {
			sb.append(change.toString() + "\n");
		}
		return sb.toString();
	}

}
