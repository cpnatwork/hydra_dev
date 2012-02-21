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
package org.hydra.utilities.diff.hmdiff;

/**
 * Describes a route of a common sequence that may be traced between two files.
 * The class implements the Comparable interface which allows it to be sorted in
 * collections according to the corrisponding line in the resulting file.
 *
 * @see HMDiff
 * @since 0.2
 * @version 0.2
 * @author Scott A. Hady
 */
public class HMTrace implements Comparable<HMTrace> {

	/** The from line. */
	private final int fromLine;

	/** The to line. */
	private final int toLine;

	/** The sub trace. */
	private HMTrace subTrace = null;

	/**
	 * Specialized constructor that specifies the line of the original file, the
	 * line of the resulting file and the previous matching point.
	 *
	 * @param startLine
	 *            int.
	 * @param endLine
	 *            int.
	 * @param prevTrace
	 *            HMTrace.
	 */
	public HMTrace(final int startLine, final int endLine,
			final HMTrace prevTrace) {
		this.fromLine = startLine;
		this.toLine = endLine;
		this.subTrace = prevTrace;
	}

	/**
	 * Retrieve the line from which this change starts.
	 *
	 * @return fromLine - int.
	 */
	public int getFromLine() {
		return this.fromLine;
	}

	/**
	 * Retrieve the line to which this change runs.
	 *
	 * @return toLine - int.
	 */
	public int getToLine() {
		return this.toLine;
	}

	/**
	 * Retrieve the trace which it continues.
	 *
	 * @return subTrace - HMTrace.
	 */
	public HMTrace getSubTrace() {
		return this.subTrace;
	}

	/**
	 * Inverts the trace so that the deepest trace is the first trace and the
	 * first trace is now the deepest trace.
	 *
	 * @return invertedTrace - HMTrace.
	 */
	public HMTrace invertTrace() {
		HMTrace invertedTrace = null;
		HMTrace ptr = this;
		for (int i = this.length() - 1; i >= 0; i--) {
			invertedTrace = new HMTrace(ptr.fromLine, ptr.toLine, invertedTrace);
			ptr = ptr.subTrace;
		}
		return invertedTrace;
	}

	/**
	 * Determine the number of traces recursively included in this trace.
	 *
	 * @return numberOfTraces - int.
	 */
	public int length() {
		if (this.subTrace == null)
			return 1;
		else
			return this.subTrace.length() + 1;
	}

	/**
	 * Transforms the recursive structure of the trace into an array structure.
	 *
	 * @return traceArray - HMTrace[].
	 */
	public HMTrace[] toArray() {
		final HMTrace[] traceArray = new HMTrace[this.length()];
		HMTrace ptr = this;
		for (int i = 0; i < this.length(); i++) {
			traceArray[i] = new HMTrace(ptr.toLine, ptr.fromLine, null);
			ptr = ptr.subTrace;
		}
		return traceArray;
	}

	/**
	 * OBJECT METHODS (OVERRIDDEN) ********************************************.
	 * 
	 * @return the string
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Return a string describing the trace.
	 */
	@Override
	public String toString() {
		return this.fromLine + "->" + this.toLine + "[" + this.subTrace + "]";
	}

	/**
	 * COMPARABLE METHODS (IMPLEMENTED) ***************************************.
	 * 
	 * @param comparedTrace
	 *            the compared trace
	 * @return the int
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Compares the coresponding lines in the resulting files for each trace.
	 */
	@Override
	public int compareTo(final HMTrace comparedTrace) {
		return this.toLine - comparedTrace.toLine;
	}

}
