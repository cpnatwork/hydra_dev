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
package org.hydra.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

/**
 * Responsible for managing the log file.
 *
 * @author Scott A. Hady
 * @version 0.2
 * @since 0.2
 */
public class LogWriter {

	/** The log file. */
	private File logFile;

	/**
	 * Instantiates a new log writer.
	 *
	 * @param logFile
	 *            the log file
	 */
	public LogWriter(final File logFile) {
		this.logFile = logFile;
	}

	/**
	 * Append entry.
	 *
	 * @param logEntry
	 *            the log entry
	 * @return true, if successful
	 */
	public boolean appendEntry(final String logEntry) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(this.logFile, true));
			bw.write(logEntry + "\n");
			return true;
		} catch (final Exception e) {
			return false;
		} finally {
			try {
				if (bw != null) {
					bw.flush();
					bw.close();
				}
			} catch (final Exception e) {
				return false;
			}
		}
	}

	/**
	 * Clear log.
	 *
	 * @return true, if successful
	 */
	public boolean clearLog() {
		this.logFile.delete();
		this.appendEntry("Log Cleared And Initialized: " + new Date());
		return false;
	}

	/**
	 * Count entries.
	 *
	 * @return the int
	 */
	public int countEntries() {
		int entryCount = 0;
		Scanner scanner = null;
		try {
			scanner = new Scanner(new FileInputStream(this.logFile));
			while (scanner.hasNext()) {
				scanner.nextLine();
				entryCount++;
			}
		} catch (final Exception e) {
			entryCount = -1;
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
		return entryCount;
	}

	/**
	 * Gets the entries.
	 *
	 * @return the entries
	 */
	public String[] getEntries() {
		final ArrayList<String> entries = new ArrayList<String>();
		Scanner scanner = null;
		try {
			scanner = new Scanner(new FileInputStream(this.logFile));
			while (scanner.hasNext()) {
				entries.add(scanner.nextLine());
			}
		} catch (final Exception e) {
			// Do Nothing
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
		return entries.toArray(new String[entries.size()]);
	}

	/**
	 * Gets the last entries.
	 *
	 * @param numEntries
	 *            the num entries
	 * @return the last entries
	 */
	public String[] getLastEntries(final int numEntries) {
		final int entryStart = this.countEntries() - numEntries;
		int entryCount = 0;
		final ArrayList<String> entries = new ArrayList<String>();
		Scanner scanner = null;
		try {
			scanner = new Scanner(new FileInputStream(this.logFile));
			while (scanner.hasNext() && (entryCount < entryStart)) {
				entryCount++;
			}
			while (scanner.hasNext()) {
				entries.add(scanner.nextLine());
			}
		} catch (final Exception e) {
			// Do Nothing
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
		return entries.toArray(new String[entries.size()]);
	}

	/**
	 * Gets the log file.
	 *
	 * @return the log file
	 */
	public File getLogFile() {
		return this.logFile;
	}

	/**
	 * Sets the log file.
	 *
	 * @param logFile
	 *            the new log file
	 */
	public void setLogFile(final File logFile) {
		this.logFile = logFile;
	}

}
