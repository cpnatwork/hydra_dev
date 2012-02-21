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

import java.util.ArrayList;

/**
 * Path that dynamically describes the route from a historied element's HEAD
 * state that may be traced to arrive at the given state. These routes are not
 * unique. More than a single route may be traced to the same state and they are
 * only valid when manipulating the logical unit's CURRENT state or searching
 * with the history crawler.
 * <p>
 * The path is made up of a sequence of Steps which may be considered a
 * combining of a single movement's initial branch and distance.
 *
 * @author Scott A. Hady
 * @version 0.1
 * @since 0.1
 */
public class Path {

	/** The steps. */
	private final ArrayList<Step> steps = new ArrayList<Step>();

	/** The valid. */
	private boolean valid = true;

	/**
	 * Default Constructor, initializes the path as +0*0.
	 */
	public Path() {
		this.steps.add(new Step(1, 0));
	}

	/**
	 * Specialized Constructor, which initializes the path according to the
	 * given path string.
	 *
	 * @param pathStr
	 *            String.
	 */
	public Path(final String pathStr) {
		// Split Path and Step Steps
		final String[] splitPath = pathStr.split("\\*");
		for (int i = 1; i < splitPath.length; i++) {
			this.steps.add(new Step("*" + splitPath[i]));
		}
		if (splitPath.length < 2) {
			this.valid = false;
		}
	}

	/**
	 * PATH METHODS ***********************************************************.
	 * 
	 * @return the int
	 */

	/**
	 * Returns the number of steps defined in the path.
	 *
	 * @return stepCount - int.
	 */
	public int countSteps() {
		return this.steps.size();
	}

	/**
	 * Return but not remove the first Step.
	 *
	 * @return firstStep - Step.
	 */
	public Step getFirstStep() {
		return this.steps.get(0);
	}

	/**
	 * Return but not remove the last Step.
	 *
	 * @return lastStep - Step.
	 */
	public Step getLastStep() {
		return this.steps.get(this.steps.size() - 1);
	}

	/**
	 * Return the step with the designated index.
	 *
	 * @param stepIndex
	 *            int.
	 * @return indexedStep - Step.
	 */
	public Step getStep(final int stepIndex) {
		return this.steps.get(stepIndex);
	}

	/**
	 * Determine if path will result in a move.
	 *
	 * @return isEmpty - boolean.
	 */
	public boolean isEmpty() {
		for (final Step s : this.steps) {
			if (!s.isEmpty())
				return false;
		}
		return true;
	}

	/**
	 * Determine if the path was generated with a valid path string.
	 *
	 * @return isValidPath - boolean.
	 */
	public boolean isValid() {
		return this.valid;
	}

	/**
	 * Remove and Return the first Step.
	 *
	 * @return firstStep - Step.
	 */
	public Step removeFirstStep() {
		return this.steps.remove(0);
	}

	/**
	 * Remove and Return the last step.
	 *
	 * @return lastStep - Step.
	 */
	public Step removeLastStep() {
		return this.steps.remove(this.steps.size() - 1);
	}

	/**
	 * Move along a given branch for a given distance beyond the current path.
	 *
	 * @param branch
	 *            int.
	 * @param distance
	 *            int.
	 * @return stepSuccessful - boolean.
	 */
	public boolean move(final int branch, final int distance) {
		return this.move(new Step(branch, distance));
	}

	/**
	 * Move according to the given step further along the path.
	 *
	 * @param step
	 *            Step.
	 * @return stepSuccessful - boolean.
	 */
	public boolean move(final Step step) {
		// No Movement
		if (step.getDistance() == 0) {
			if (step.getBranch() == 1) { // Only Change Branch if LastStep is
											// Empty.
				if (this.getLastStep().isEmpty()) {
					this.removeLastStep();
					this.steps.add(step.clone());
				}
				return true;
			} else if (step.getBranch() > 1) { // Change Branch - Add New Step
												// to Path.
				if (this.getLastStep().isEmpty()) {
					this.removeLastStep();
				}
				this.steps.add(step.clone());
				return true;
			} else
				return false;
		} else if (step.getDistance() >= 0) {
			if (step.getBranch() == 1) { // Continue Down Same Branch.
				this.getLastStep().shiftDistance(step.getDistance());
				return true;
			} else if (step.getBranch() > 1) { // Change Branch - Add New Step
												// to Path
				if (this.getLastStep().isEmpty()) {
					this.removeLastStep();
				}
				this.steps.add(step.clone());
				return true;
			} else
				return false;
		} else { // Move Backwards Ignoring Branch Change.
			if (step.getBranch() >= 1) {
				int remainder = this.steps.get(this.steps.size() - 1)
						.shiftDistance(step.getDistance());
				while ((remainder < 0) && (this.steps.size() > 1)) {
					this.steps.remove(this.steps.size() - 1);
					remainder = this.steps.get(this.steps.size() - 1)
							.shiftDistance(remainder);
				}
				if (this.getLastStep().isEmpty()) {
					if (this.steps.size() == 1) {
						this.getLastStep().setBranch(1);
					} else {
						this.removeLastStep();
					}
				}
				return (remainder == 0) ? true : false;
			} else
				return false;
		}
	}

	/**
	 * OBJECT METHODS (OVERRIDDEN) ********************************************.
	 * 
	 * @return the path
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Return a Clone of the given path.
	 */
	@Override
	public Path clone() {
		return new Path(this.toString());
	}

	/**
	 * {@inheritDoc}
	 *
	 * Returns a string describing the path's steps.
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("");
		for (final Step s : this.steps) {
			sb.append(s.toString());
		}
		return sb.toString();
	}

}
