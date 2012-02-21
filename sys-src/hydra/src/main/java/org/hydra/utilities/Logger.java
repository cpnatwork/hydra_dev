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

import java.io.File;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import org.hydra.core.Configuration;

/**
 * Provides a singleton logging mechanism to organize the system's logging.
 * 
 * @since 0.1
 * @version 0.2
 * @author Scott A. Hady
 */
public class Logger implements Observer {

	// PUBLIC PROPERTIES
	/** The Constant SEPARATOR. */
	public static final String SEPARATOR = "::>>";

	/** The Constant PROPERTY_LOGFILE. */
	public static final String PROPERTY_LOGFILE = "Logger.logFile";

	/** The Constant PROPERTY_USERLEVEL. */
	public static final String PROPERTY_USERLEVEL = "Logger.userLevel";

	/** The Constant PROPERTY_SYSTEMLEVEL. */
	public static final String PROPERTY_SYSTEMLEVEL = "Logger.systemLevel";
	// PRIVATE PROPERTIES
	/** The logger. */
	private static Logger logger = null;

	/** The config. */
	private Configuration config = null;

	/** The log file. */
	private File logFile = null;

	/** The log writer. */
	private LogWriter logWriter = null;

	/** The append. */
	private final boolean append = true;

	/** The user level. */
	private LoggerLevel userLevel = LoggerLevel.CRITICAL;

	/** The system level. */
	private LoggerLevel systemLevel = LoggerLevel.DEBUG;

	/**
	 * LOGGER CREATIONAL (SINGLETON) ******************************************.
	 */

	/**
	 * Protected Creator - Configures the Logging System.
	 */
	protected Logger() {
		this.config = Configuration.getInstance();
		this.config.addObserver(this);
		this.logFile = new File(this.config.getRepository(), "hydra.log");
		this.configureLogger();
		this.logWriter = new LogWriter(this.logFile);
	}

	/**
	 * Singleton Pattern for retrieving the lone instance of the logger.
	 * 
	 * @return logger - Logger.
	 */
	public static Logger getInstance() {
		if (Logger.logger == null) {
			Logger.logger = new Logger();
		}
		return Logger.logger;
	}

	/**
	 * LOGGER ADMINISTRATION **************************************************.
	 */

	/**
	 * Clear and reinitialize log.
	 */
	public void clearLog() {
		this.logWriter.clearLog();
	}

	/**
	 * Determine how many log entries are currently in the log file. It will
	 * always return zero if no valid logfile has been defined.
	 * 
	 * @return entryCount - int.
	 */
	public int countEntries() {
		if (this.hasLogFile()) {
			return this.logWriter.countEntries();
		} else {
			return 0;
		}
	}

	/**
	 * Return the logging file.
	 * 
	 * @return log - File.
	 */
	public File getLogFile() {
		return this.logFile;
	}

	/**
	 * Return the log writer.
	 * 
	 * @return logWriter - LogWriter.
	 */
	public LogWriter getLogWriter() {
		return this.logWriter;
	}

	/**
	 * Describe the current status of the logger.
	 * 
	 * @return statusDescription - String.
	 */
	public String getStatus() {
		final StringBuilder sb = new StringBuilder("")
				.append("  Log File:\t")
				.append(this.logFile)
				.append((this.logFile.exists() ? " (Found)\n"
						: " (Not Found)\n")).append("  Log Count:\t")
				.append(this.countEntries()).append("\n")
				.append("  System Level:\t").append(this.systemLevel)
				.append("\n").append("  User Level:\t").append(this.userLevel)
				.append("\n");
		return sb.toString();
	}

	/**
	 * Determine if the logger has a valid log file. If it does not have a valid
	 * log file than it will output all logging events to the standard output.
	 * 
	 * @return hasLogFile - boolean.
	 */
	public boolean hasLogFile() {
		return (this.logFile != null);
	}

	/**
	 * Set the file to log to.
	 * 
	 * @param log
	 *            File.
	 */
	public void setLogFile(final File log) {
		this.logFile = log;
		this.logWriter.setLogFile(log);
		this.config.setProperty(Logger.PROPERTY_LOGFILE,
				((this.logFile == null) ? null : this.logFile.getPath()));
	}

	/**
	 * Set user logging level.
	 * 
	 * @param userLvl
	 *            LoggerLevel.
	 */
	public void setUserLevel(final LoggerLevel userLvl) {
		this.userLevel = userLvl;
		this.config.setProperty(Logger.PROPERTY_USERLEVEL,
				this.userLevel.toString());
	}

	/**
	 * Set system logging level.
	 * 
	 * @param sysLvl
	 *            LoggerLevel.
	 */
	public void setSystemLevel(final LoggerLevel sysLvl) {
		this.systemLevel = sysLvl;
		this.config.setProperty(Logger.PROPERTY_SYSTEMLEVEL,
				this.systemLevel.toString());
	}

	/**
	 * LOGGING METHODS ********************************************************.
	 * 
	 * @param logMessage
	 *            the log message
	 * @param logLevel
	 *            the log level
	 * @return the string
	 */

	/**
	 * Log an entry into the designated log.
	 * 
	 * @param logMessage
	 *            String.
	 * @param logLevel
	 *            LoggerLevel.
	 * @return logEntry - String.
	 */
	public String log(final String logMessage, final LoggerLevel logLevel) {
		if (!this.systemLevel.greaterThan(logLevel)) {
			if (this.checkForSysOutOverSLF4J() || !this.hasLogFile()) {
				System.out.println(logMessage);
			} else if (this.logWriter.appendEntry(logMessage)) {
				// WARNING - DO NOT ATTEMPT TO LOG THIS EXCEPTION (CREATES
				// INFINITE LOOP).
				if (this.userLevel == LoggerLevel.DEBUG) {
					System.out.println("Unable to Append Log Entry ["
							+ this.logFile + "].");
				}
			}
		}
		if (!this.userLevel.greaterThan(logLevel) && this.hasLogFile()) {
			System.out.println(logMessage);
		}
		return logMessage;
	}

	/**
	 * Check for sys out over sl f4 j.
	 * 
	 * @return true, if successful
	 */
	private boolean checkForSysOutOverSLF4J() {
		ClassNotFoundException classNotFound = null; // false (== null)
		try {
			Class.forName("uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J",
					false, ClassLoader.getSystemClassLoader());
		} catch (final ClassNotFoundException e) {
			classNotFound = e; // true (!= null)
		}
		return (classNotFound != null);
	}

	/**
	 * Log an informational message.
	 * 
	 * @param logMessage
	 *            String.
	 * @return infoEntry - String.
	 */
	public String info(final String logMessage) {
		return this.log(this.formatBody(LoggerLevel.INFO, logMessage),
				LoggerLevel.INFO);
	}

	/**
	 * Log a warning message.
	 * 
	 * @param logMessage
	 *            String.
	 * @return warningEntry - String.
	 */
	public String warning(final String logMessage) {
		return this.log(this.formatBody(LoggerLevel.WARNING, logMessage),
				LoggerLevel.WARNING);
	}

	/**
	 * Log a exception message.
	 * 
	 * @param logMessage
	 *            String.
	 * @return exceptionEntry - String.
	 */
	public String exception(final String logMessage) {
		return this.log(this.formatBody(LoggerLevel.EXCEPTION, logMessage),
				LoggerLevel.EXCEPTION);
	}

	/**
	 * Log a exception message.
	 * 
	 * @param logMessage
	 *            String.
	 * @param exception
	 *            Exception.
	 * @return exceptionEntry - String.
	 */
	public String exception(final String logMessage, final Exception exception) {
		return this.log(this.formatBody(LoggerLevel.EXCEPTION, logMessage)
				+ Logger.SEPARATOR + this.formatException(exception),
				LoggerLevel.EXCEPTION);
	}

	/**
	 * Log a critical message.
	 * 
	 * @param logMessage
	 *            String.
	 * @return criticalEntry - String.
	 */
	public String critical(final String logMessage) {
		return this.log(this.formatBody(LoggerLevel.CRITICAL, logMessage),
				LoggerLevel.CRITICAL);
	}

	/**
	 * Log a critical message.
	 * 
	 * @param logMessage
	 *            String.
	 * @param exception
	 *            Exception.
	 * @return criticalEntry - String.
	 */
	public String critical(final String logMessage, final Exception exception) {
		return this.log(this.formatBody(LoggerLevel.CRITICAL, logMessage)
				+ Logger.SEPARATOR + this.formatException(exception),
				LoggerLevel.CRITICAL);
	}

	/**
	 * LOGGER FORMATING METHODS (PRIVATE). ************************************
	 * 
	 * @param logLevel
	 *            the log level
	 * @param logMessage
	 *            the log message
	 * @return the string
	 */

	/**
	 * Returns a string with the basic log entry body formating.
	 * 
	 * @param logLevel
	 *            LoggerLevel.
	 * @param logMessage
	 *            String.
	 * @return formattedLogBody - String.
	 */
	private String formatBody(final LoggerLevel logLevel,
			final String logMessage) {
		return "[" + logLevel + "]" + Logger.SEPARATOR + new Date()
				+ Logger.SEPARATOR + logMessage;

	}

	/**
	 * Returns a formatted string describing the exception and its location,
	 * which can be appended to the formatted log body as needed.
	 * 
	 * @param exception
	 *            Exception.
	 * @return formattedExceptionLogExtension - String.
	 */
	private String formatException(final Exception exception) {
		final StringBuilder sb = new StringBuilder(exception.toString());
		sb.append("[" + exception.getStackTrace()[0] + "]");
		return sb.toString();
	}

	/**
	 * OBSERVER METHODS (IMPLEMENTATION). *************************************
	 * 
	 * @param observed
	 *            the observed
	 * @param notice
	 *            the notice
	 */

	/**
	 * {@inheritDoc}
	 * 
	 * Observer Pattern update method for observing changes made to the
	 * configuration.
	 */
	@Override
	public void update(final Observable observed, final Object notice) {
		this.configureLogger();
	}

	/** LOGGER METHODS (PRIVATE). ***********************************************/

	/**
	 * Gathers settings from configuration and adjusts the logger as designated.
	 */
	private void configureLogger() {
		if (this.config.hasProperty(Logger.PROPERTY_LOGFILE)) {
			final String logFilePath = this.config
					.getProperty(Logger.PROPERTY_LOGFILE);
			if (logFilePath.equals("null")) {
				this.logFile = null;
			} else {
				this.logFile = new File(
						this.config.getProperty(Logger.PROPERTY_LOGFILE));
			}
		}
		if (this.config.hasProperty(Logger.PROPERTY_SYSTEMLEVEL)) {
			this.systemLevel = LoggerLevel.toLoggerLevel(this.config
					.getProperty(Logger.PROPERTY_SYSTEMLEVEL));
		}
		if (this.config.hasProperty(Logger.PROPERTY_USERLEVEL)) {
			this.userLevel = LoggerLevel.toLoggerLevel(this.config
					.getProperty(Logger.PROPERTY_USERLEVEL));
		}
	}

}
