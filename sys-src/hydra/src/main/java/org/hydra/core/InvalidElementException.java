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
package org.hydra.core;

/**
 * Exception thrown due to the invalid configuration of an element.
 *
 * @author Scott A. Hady
 * @version 0.1
 * @since 0.1
 */
public class InvalidElementException extends Exception {

	/** The Constant serialVersionUID. */
	static final long serialVersionUID = 01L;

	/**
	 * Specialized constructor that allows the thrower to define a descriptive
	 * message.
	 *
	 * @param message
	 *            String.
	 */
	public InvalidElementException(final String message) {
		super(message);
	}

	/**
	 * Specialized constructor that allows the thrower to define a descriptive
	 * message and the causal exception.
	 *
	 * @param message
	 *            String.
	 * @param exception
	 *            Exception.
	 */
	public InvalidElementException(final String message,
			final Exception exception) {
		super(message, exception);
	}

}
