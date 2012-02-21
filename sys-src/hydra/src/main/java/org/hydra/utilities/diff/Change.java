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
 * Abstract base class for the more specialized change classes which are used
 * provide a canonical view of the different differential algorithm results.
 * Each change describes a sub transformation from the original to the resulting
 * file.
 *
 * @since 0.2
 * @version 0.2
 * @author Scott A. Hady
 */
public abstract class Change {

	/** The line number. */
	protected int lineNumber;

	/** The changed lines. */
	protected String[] changedLines;

	/**
	 * Specialized Constructor which receives the line number affected in the
	 * original file and the resulting set of changes.
	 *
	 * @param startPosition
	 *            int.
	 * @param changedMembers
	 *            String[].
	 */
	public Change(final int startPosition, final String[] changedMembers) {
		this.lineNumber = startPosition;
		this.changedLines = changedMembers;
	}

	/**
	 * Return the number of lines that are designated to be deleted by this
	 * transformation.
	 *
	 * @return numberLinesDeleted - int.
	 */
	public int countDeleted() {
		return 0;
	}

	/**
	 * Return the line number of the original file affected by this change.
	 *
	 * @return lineNumber - int.
	 */
	public int getLineNumber() {
		return this.lineNumber;
	}

	/**
	 * Return the set of changed lines that should be applied to accomplish the
	 * described transformation.
	 *
	 * @return changedLines - String[].
	 */
	public String[] getChangedLines() {
		return this.changedLines;
	}

}
