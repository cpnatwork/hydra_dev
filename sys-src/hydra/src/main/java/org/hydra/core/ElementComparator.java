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
 * Compares two elements, first by type (states-containers-artifacts) then
 * alphabetically by name.
 *
 * @author Scott A. Hady
 * @version 0.1
 * @since 0.1
 */
public class ElementComparator implements java.util.Comparator {

	/**
	 * Default constructor with no parameters.
	 */
	public ElementComparator() {

	}

	/**
	 * {@inheritDoc}
	 *
	 * Returns an integer indicating the relative position of the first object
	 * to the second object. -1 means before, 0 means the same and 1 means
	 * after. Ordering is first prioritized by type
	 * (states-containers-artifacts) and then alphabetically by name.
	 */
	@Override
	public int compare(final Object o1, final Object o2) {
		final Element e1 = (Element) o1;
		final Element e2 = (Element) o2;

		if (e1 instanceof State) {
			if (e2 instanceof State)
				return e1.getName().compareTo(e2.getName());
			else
				return -1;
		} else if (e1 instanceof Container) {
			if (e2 instanceof State)
				return 1;
			else if (e2 instanceof Container)
				return e1.getName().compareTo(e2.getName());
			else
				return -1;
		} else {
			if (e2 instanceof Artifact)
				return e1.getName().compareTo(e2.getName());
			else
				return 1;
		}
	}

}
