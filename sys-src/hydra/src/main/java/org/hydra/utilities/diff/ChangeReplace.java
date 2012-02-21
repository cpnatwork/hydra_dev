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
 * replacing the given set of delted lines in the original file with another set
 * of lines.
 *
 * @since 0.2
 * @version 0.2
 * @author Scott A. Hady
 */
public class ChangeReplace extends Change {

	/** The num deleted. */
	private final int numDeleted;

	/**
	 * Specialized constructor that accepts the line number of the original file
	 * affected, the set of lines to add to accomplish the transformation and
	 * the number of the lines that are to be deleted.
	 *
	 * @param lineNumber
	 *            int.
	 * @param changedLines
	 *            String[].
	 * @param numberDeleted
	 *            int.
	 */
	public ChangeReplace(final int lineNumber, final String[] changedLines,
			final int numberDeleted) {
		super(lineNumber, changedLines);
		this.numDeleted = numberDeleted;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return the number of lines that are designated to be deleted by this
	 * transformation.
	 */
	@Override
	public int countDeleted() {
		return this.numDeleted;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Returns a string describing the affects of the transformation.
	 */
	@Override
	public String toString() {
		final int numAdded = this.changedLines.length - this.numDeleted;
		final StringBuilder sb = new StringBuilder("Replacing ["
				+ this.numDeleted + "|" + numAdded + "] Lines.\n");
		for (int i = 0; i < this.numDeleted; i++) {
			sb.append("\t" + (this.lineNumber + i) + " << "
					+ this.changedLines[i] + "\n");
		}
		for (int i = this.numDeleted; i < this.changedLines.length; i++) {
			sb.append("\t" + (this.lineNumber + i) + " >>  "
					+ this.changedLines[i] + "\n");
		}
		return sb.toString();
	}

}
