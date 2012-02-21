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

/**
 * Defines the various logging levels that may be set for the logger.
 *
 * @since 0.2
 * @version 0.2
 * @author Scott A. Hady
 */
public enum LoggerLevel {

	/** The DEBUG. */
	DEBUG(0), /** The INFO. */
	INFO(1), /** The WARNING. */
	WARNING(2), /** The EXCEPTION. */
	EXCEPTION(3), /** The CRITICAL. */
	CRITICAL(4), /** The N o_ log. */
	NO_LOG(5);

	/** The level value. */
	private int levelValue;

	/**
	 * Protected constructor that initializes the level to the designated value.
	 * 
	 * @param level
	 *            - int.
	 */
	private LoggerLevel(final int level) {
		this.levelValue = level;
	}

	/**
	 * Return the level's value as an int.
	 *
	 * @return levelValue - int.
	 */
	public int toInt() {
		return this.levelValue;
	}

	/**
	 * Static construction method that takes a given level value and returns the
	 * correct LoggerLevel Enum. If the given level value is not recognized then
	 * the default (NO_LOG) is returned.
	 *
	 * @param levelValue
	 *            int.
	 * @return loggerLevel - LoggerLevel.
	 */
	public static LoggerLevel toLoggerLevel(final int levelValue) {
		if (levelValue == 0)
			return LoggerLevel.DEBUG;
		else if (levelValue == 1)
			return LoggerLevel.INFO;
		else if (levelValue == 2)
			return LoggerLevel.WARNING;
		else if (levelValue == 3)
			return LoggerLevel.EXCEPTION;
		else if (levelValue == 4)
			return LoggerLevel.CRITICAL;
		else
			return LoggerLevel.NO_LOG;
	}

	/**
	 * Static construction method that takes a given level string and returns
	 * the correct LoggerLevel Enum. If the given level string is not recognized
	 * then the default (NO_LOG) is returned.
	 *
	 * @param levelString
	 *            String.
	 * @return loggerLevel - LoggerLevel.
	 */
	public static LoggerLevel toLoggerLevel(final String levelString) {
		final String upLevelString = levelString.toUpperCase();
		if (upLevelString.equals(LoggerLevel.DEBUG.toString()))
			return LoggerLevel.DEBUG;
		else if (upLevelString.equals(LoggerLevel.INFO.toString()))
			return LoggerLevel.INFO;
		else if (upLevelString.equals(LoggerLevel.WARNING.toString()))
			return LoggerLevel.WARNING;
		else if (upLevelString.equals(LoggerLevel.EXCEPTION.toString()))
			return LoggerLevel.EXCEPTION;
		else if (upLevelString.equals(LoggerLevel.CRITICAL.toString()))
			return LoggerLevel.CRITICAL;
		else
			return LoggerLevel.NO_LOG;
	}

	/**
	 * Convenience Method that compares the level values.
	 *
	 * @param comparedTo
	 *            LoggerLevel.
	 * @return isGreaterThan - boolean.
	 */
	public boolean greaterThan(final LoggerLevel comparedTo) {
		return (this.levelValue > comparedTo.toInt());
	}

	/**
	 * Convenience Method returns the same result as determining if the
	 * comparison result is equal to -1.
	 *
	 * @param comparedTo
	 *            LoggerLevel.
	 * @return isLessThan - boolean.
	 */
	public boolean lessThan(final LoggerLevel comparedTo) {
		return (this.levelValue < comparedTo.toInt());
	}

}
