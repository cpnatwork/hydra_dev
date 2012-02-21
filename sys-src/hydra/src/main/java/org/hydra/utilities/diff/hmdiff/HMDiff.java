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

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeSet;

import org.hydra.utilities.Logger;
import org.hydra.utilities.diff.Change;
import org.hydra.utilities.diff.ChangeAdd;
import org.hydra.utilities.diff.ChangeDelete;
import org.hydra.utilities.diff.ChangeReplace;
import org.hydra.utilities.diff.ChangeSet;

/**
 * Calculates the diff between two files or two string arrays and provides the
 * resulting change set which may be applied to the original file or array in
 * order to produce the resulting file or array. The diff algorithm is based on
 * the paper An Algorithm for Differential File Comparison by JW Hunt and MD
 * McIlroy. If first calculates the longest common sequence as a trace and then
 * uses that to derive the change set.
 *
 * @since 0.2
 * @version 0.2
 * @author Scott A. Hady
 */
public class HMDiff {

	/** The from array. */
	private final String[] fromArray;

	/** The to array. */
	private final String[] toArray;

	/** The matches. */
	private final HashMap<String, ArrayList<Integer>> matches = new HashMap<String, ArrayList<Integer>>();

	/** The traces. */
	private final TreeSet<HMTrace> traces = new TreeSet<HMTrace>();

	/** The change set. */
	private final ChangeSet changeSet = new ChangeSet();

	/** The logger. */
	private final Logger logger;

	/**
	 * Specialized Constructor which manipulates arrays of strings.
	 *
	 * @param originalArray
	 *            String[].
	 * @param transformedArray
	 *            String[].
	 */
	public HMDiff(final String[] originalArray, final String[] transformedArray) {
		this.logger = Logger.getInstance();
		this.fromArray = originalArray;
		this.toArray = transformedArray;
		this.calculateDiff();
	}

	/**
	 * Specialized Constructor which recieves the original and the resulting
	 * file from which to determine the necessary differential describing the
	 * transformation.
	 *
	 * @param fromFile
	 *            File.
	 * @param toFile
	 *            File.
	 */
	public HMDiff(final File fromFile, final File toFile) {
		this.logger = Logger.getInstance();
		this.fromArray = this.loadFileToArray(fromFile);
		this.toArray = this.loadFileToArray(toFile);
		this.calculateDiff();
	}

	/**
	 * Returns the contents of the given file as an array of strings.
	 * 
	 * @param targetFile
	 *            File.
	 * @return content - String[].
	 */
	private String[] loadFileToArray(final File targetFile) {
		final ArrayList<String> contentList = new ArrayList<String>();
		Scanner scanner = null;
		try {
			scanner = new Scanner(new FileInputStream(targetFile));
			while (scanner.hasNext()) {
				contentList.add(scanner.nextLine());
			}
		} catch (final Exception e) {
			this.logger.exception("Unable to Load File [" + targetFile
					+ "] to Array.", e);
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
		return contentList.toArray(new String[contentList.size()]);
	}

	/**
	 * Return the trace of the longest common sequence found in the diff
	 * calculations.
	 *
	 * @return longestCommonSequence - HMTrace.
	 */
	public HMTrace findLongestTrace() {
		HMTrace longest = this.traces.first();
		for (final HMTrace trace : this.traces) {
			if (trace.length() > longest.length()) {
				longest = trace;
			}
		}
		return longest;
	}

	/**
	 * Return the set of changed which describe a possible transformation from
	 * the original file into the resulting file.
	 *
	 * @return changes - ChangeSet.
	 */
	public ChangeSet getChangeSet() {
		return this.changeSet;
	}

	/**
	 * DESCRIBE METHODS
	 * ********************************************************.
	 * 
	 * @return the string
	 */

	/**
	 * Return a string describing the tranformation between the orignal and
	 * resulting file/array.
	 *
	 * @return transformationDescription - String.
	 */
	public String describeTransformation() {
		final StringBuilder sb = new StringBuilder("l  t   content\n");
		final Change[] changes = this.changeSet.listChanges();
		int changeIndex = 0;
		int diffIndex = 0;
		for (int fromLine = 0; fromLine < this.fromArray.length; fromLine++) {
			if ((changes.length > changeIndex)
					&& ((changes[changeIndex].getLineNumber() - 1) == fromLine)) {
				final Change change = changes[changeIndex];
				final String[] changedLines = changes[changeIndex]
						.getChangedLines();
				if (change instanceof ChangeAdd) {
					for (int i = 0; i < changedLines.length; i++) {
						sb.append(this.formatLine(
								(fromLine + diffIndex + i + 1), ">",
								changedLines[i])
								+ "\n");
					}
					diffIndex += changedLines.length;
				} else if (change instanceof ChangeDelete) {
					for (final String changedLine : changedLines) {
						sb.append(this.formatLine("", "<", changedLine) + "\n");
						fromLine++;
					}
					diffIndex -= changedLines.length;
				} else {
					for (int i = 0; i < changedLines.length; i++) {
						if (i < change.countDeleted()) {
							sb.append(this
									.formatLine("", "<<", changedLines[i])
									+ "\n");
							fromLine++;
							diffIndex--;
						} else {
							sb.append(this.formatLine(
									(fromLine + diffIndex + 1), ">>",
									changedLines[i])
									+ "\n");
							diffIndex++;
						}
					}
				}
				changeIndex++;
			}
			if ((fromLine < this.fromArray.length)) {
				sb.append(this.formatLine((fromLine + diffIndex + 1), " ",
						this.fromArray[fromLine]) + "\n");
			}
		}
		// Make Up Any Left Over Changes.
		for (int i = changeIndex; i < changes.length; i++) {
			final String[] changedLines = changes[changeIndex]
					.getChangedLines();
			for (int j = 0; j < changedLines.length; j++) {
				sb.append(this.formatLine((this.fromArray.length + diffIndex
						+ j + 1), ">", changedLines[j])
						+ "\n");
			}
			diffIndex += changedLines.length;
			changeIndex++;
		}
		return sb.toString();
	}

	/**
	 * Return a formatted string describing a lines transformation.
	 * 
	 * @param lineNumber
	 *            int.
	 * @param transformation
	 *            String.
	 * @param content
	 *            String.
	 * @return formattedTransformationLine - String.
	 */
	private String formatLine(final int lineNumber,
			final String transformation, final String content) {
		return this.formatLine(Integer.toString(lineNumber), transformation,
				content);
	}

	/**
	 * Return a formatted string describing a lines transformation.
	 * 
	 * @param lineNumber
	 *            int.
	 * @param transformation
	 *            String.
	 * @param content
	 *            String.
	 * @return formattedTransformationLine - String.
	 */
	private String formatLine(final String lineNumber,
			final String transformation, final String content) {
		final int lnTab = 2;
		final int trTab = 7;
		final StringBuilder sb = new StringBuilder(lineNumber);
		while (sb.length() < lnTab) {
			sb.append(" ");
		}
		sb.append(" " + transformation);
		while (sb.length() < trTab) {
			sb.append(" ");
		}
		sb.append(content);
		return sb.toString();
	}

	/**
	 * Return a string describing the set of matching lines found in the
	 * original and resulting file/array.
	 *
	 * @return matchingLineDescription - String.
	 */
	public String describeMatches() {
		final StringBuilder sb = new StringBuilder(
				"Describing Matching Lines...");
		for (final String key : this.matches.keySet()) {
			sb.append(key + ": ");
			for (final Integer lineNumber : this.matches.get(key)) {
				sb.append(lineNumber + " ");
			}
			sb.append("");
		}
		return sb.toString();
	}

	/**
	 * Return a string describing the set of traces that were calculated by
	 * analyzing the files/arrays.
	 *
	 * @return traceDescription - String.
	 */
	public String describeTraces() {
		final StringBuilder sb = new StringBuilder("Describing All Traces...");
		for (final HMTrace trace : this.traces) {
			sb.append(trace.length() + " : " + trace);
		}
		return sb.toString();
	}

	/**
	 * DIFF ALGORITHM
	 * **********************************************************.
	 */
	/**
	 * Basic steps of the Diff Algorithm.
	 */
	private void calculateDiff() {
		this.calculateMatches();
		this.calculateTraces();
		this.calculateChangeSet();
	}

	/**
	 * Determine the list of matching lines between the orignal and the result.
	 */
	private void calculateMatches() {
		for (final String element : this.fromArray) {
			if (this.matches.containsKey(element)) {
				continue;
			}
			final ArrayList<Integer> lineNumbers = new ArrayList<Integer>();
			for (int toLine = 0; toLine < this.toArray.length; toLine++) {
				if (element.equals(this.toArray[toLine])) {
					lineNumbers.add(toLine + 1);
				}
			}
			if (!lineNumbers.isEmpty()) {
				this.matches.put(element, lineNumbers);
			}
		}
	}

	/**
	 * Calculate all of the canidate traces, the longest of which is the longest
	 * common sequence between the original and the result.
	 */
	private void calculateTraces() {
		this.traces.add(new HMTrace(0, 0, null));
		this.traces.add(new HMTrace(this.fromArray.length + 1,
				this.toArray.length + 1, null));
		for (int fromLine = 0; fromLine < this.fromArray.length; fromLine++) {
			this.appendTraces(fromLine);
		}
	}

	/**
	 * Appends the current line to an appropriate trace.
	 * 
	 * @param fromLine
	 *            int.
	 */
	private void appendTraces(final int fromLine) {
		if (!this.matches.containsKey(this.fromArray[fromLine]))
			return;
		for (final Integer toLine : this.matches.get(this.fromArray[fromLine])) {
			HMTrace testTrace = new HMTrace(fromLine + 1, toLine, null);
			if (!this.traces.contains(testTrace)) {
				final HMTrace lowerTrace = this.traces.lower(testTrace);
				if (lowerTrace.getFromLine() != testTrace.getFromLine()) {
					testTrace = new HMTrace(fromLine + 1, toLine, lowerTrace);
					this.traces.add(testTrace);
				}
			}
		}
	}

	/**
	 * Use the longest common sequence calculated to derive the set of changes
	 * that may be applied to transform the original file/array into the
	 * resulting file/array.
	 */
	private void calculateChangeSet() {
		int fromDiff = 0;
		int toDiff = 0;
		HMTrace lastTrace = this.findLongestTrace();
		if ((lastTrace.getFromLine() < (this.fromArray.length + 1))
				|| (lastTrace.getToLine() < (this.toArray.length + 1))) {
			lastTrace = new HMTrace(this.fromArray.length + 1,
					this.toArray.length + 1, lastTrace);
		}
		lastTrace = lastTrace.invertTrace();
		HMTrace currTrace = lastTrace.getSubTrace();
		while (currTrace != null) {
			fromDiff = currTrace.getFromLine() - lastTrace.getFromLine();
			toDiff = currTrace.getToLine() - lastTrace.getToLine();
			if ((fromDiff > 1) && (toDiff > 1)) {
				this.replaceLinesInChangeSet(lastTrace, fromDiff, toDiff);
			} else if ((fromDiff > 1) || (toDiff < 1)) {
				this.deleteLinesFromChangeSet(lastTrace, fromDiff, toDiff);
			} else if (toDiff > 1) {
				this.addLinesToChangeSet(lastTrace, toDiff);
			}
			/* else {//No Change - Ignore} */
			lastTrace = currTrace;
			currTrace = currTrace.getSubTrace();
		}
	}

	/**
	 * Create and append a replacement change to the set of changes, called by
	 * the {@link calculateChangeSet} method.
	 * 
	 * @param lastTrace
	 *            HMTrace.
	 * @param fromDiff
	 *            int.
	 * @param toDiff
	 *            int.
	 */
	private void replaceLinesInChangeSet(final HMTrace lastTrace,
			final int fromDiff, final int toDiff) {
		final String[] changedLines = new String[(fromDiff + toDiff) - 2];
		for (int i = 0; i < (fromDiff - 1); i++) {
			changedLines[i] = this.fromArray[lastTrace.getFromLine() + i];
		}
		for (int i = 0; i < (toDiff - 1); i++) {
			changedLines[(fromDiff + i) - 1] = this.toArray[lastTrace
					.getToLine() + i];
		}
		this.changeSet.add(new ChangeReplace(lastTrace.getFromLine() + 1,
				changedLines, fromDiff - 1));
	}

	/**
	 * Create and append an line addition change to the set of changes, called
	 * by the {@link calculateChangeSet} method.
	 * 
	 * @param lastTrace
	 *            HMTrace.
	 * @param toDiff
	 *            int.
	 */
	private void addLinesToChangeSet(final HMTrace lastTrace, final int toDiff) {
		final String[] addedLines = new String[toDiff - 1];
		for (int i = 0; i < (toDiff - 1); i++) {
			addedLines[i] = this.toArray[lastTrace.getToLine() + i];
		}
		this.changeSet.add(new ChangeAdd(lastTrace.getSubTrace().getFromLine(),
				addedLines));
	}

	/**
	 * Create and append an line deletion change to the set of changes, called
	 * by the {@link calculateChangeSet} method.
	 * 
	 * @param lastTrace
	 *            HMTrace.
	 * @param fromDiff
	 *            int.
	 * @param toDiff
	 *            int.
	 */
	private void deleteLinesFromChangeSet(final HMTrace lastTrace,
			final int fromDiff, final int toDiff) {
		final int diff = fromDiff - toDiff;
		final String[] removedLines = new String[diff];
		for (int i = 0; i < diff; i++) {
			removedLines[i] = this.fromArray[lastTrace.getFromLine() + i];
		}
		this.changeSet.add(new ChangeDelete(lastTrace.getSubTrace()
				.getFromLine() - 1, removedLines));
	}

}
