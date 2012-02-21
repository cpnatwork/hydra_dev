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
 * Describes the tranformation of an original file into the resulting file by
 * adding the given set of lines to the original file.
 *
 * @since 0.2
 * @version 0.2
 * @author Scott A. Hady
 */
public class ChangeAdd extends Change {

	/**
	 * Specialized constructor that accepts the line number of the original file
	 * affected and the set of lines to add to accomplish the transformation.
	 *
	 * @param lineNumber
	 *            int.
	 * @param addedLines
	 *            String[].
	 */
	public ChangeAdd(final int lineNumber, final String[] addedLines) {
		super(lineNumber, addedLines);
	}

	/**
	 * {@inheritDoc}
	 *
	 * Returns a string describing the affects of the transformation.
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Add ["
				+ this.changedLines.length + "] Lines.\n");
		for (int i = 0; i < this.changedLines.length; i++) {
			sb.append("\t" + (this.lineNumber + i) + " > "
					+ this.changedLines[i] + "\n");
		}
		return sb.toString();
	}

}
