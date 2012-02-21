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

/**
 * A differential change that desribes the transformation of the original source
 * into the result through the deletion of a given set of lines or members.
 *
 * @since 0.2
 * @version 0.2
 * @author Scott A. Hady
 */
public class ChangeDelete extends Change {

	/**
	 * Specialized constructor that accept the line number of the file to affect
	 * and the lines to delete.
	 *
	 * @param lineNumber
	 *            int.
	 * @param changedLines
	 *            String[].
	 */
	public ChangeDelete(final int lineNumber, final String[] changedLines) {
		super(lineNumber, changedLines);
	}

	/**
	 * {@inheritDoc}
	 *
	 * Returns the number of lines to be deleted.
	 */
	@Override
	public int countDeleted() {
		return this.changedLines.length;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Returns a string describing the changes actions or transformation.
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Remove ["
				+ this.changedLines.length + "] Lines.\n");
		for (int i = 0; i < this.changedLines.length; i++) {
			sb.append("\t" + (this.lineNumber + i) + " > "
					+ this.changedLines[i] + "\n");
		}
		return sb.toString();
	}

}
