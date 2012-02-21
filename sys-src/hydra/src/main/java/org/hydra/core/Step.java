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
 * Describes a single movement through the combination of the initial branch and
 * the distance of the movement. The branch range is from 1 to the number of
 * previous states that the current state has. The distance range is from 0 to
 * the number of previous commits that were created to reach this state.
 * <p>
 * It may be described as a two part string. (*) Refers to the branch and (+)
 * refers to the distance of the step.
 *
 * @author Scott A. Hady
 * @version 0.1
 * @since 0.1
 */
public class Step {

	/** The branch. */
	private int branch;

	/** The distance. */
	private int distance;

	/**
	 * Default Constructor, sets the step's branch to 1 and distance to 0.
	 */
	public Step() {
		this.setBranch(1);
		this.setDistance(0);
	}

	/**
	 * Specialized Constructor, sets the step's branch and distance as
	 * designated.
	 *
	 * @param branch
	 *            int.
	 * @param distance
	 *            int.
	 */
	public Step(final int branch, final int distance) {
		this.setBranch(branch);
		this.setDistance(distance);
	}

	/**
	 * Specialized Constructor, parses a step string and sets the step
	 * accordingly.
	 *
	 * @param stepStr
	 *            String.
	 */
	public Step(final String stepStr) {
		final String[] stepSplit = stepStr.substring(1, stepStr.length())
				.split("\\+");
		this.setBranch(Integer.parseInt(stepSplit[0]));
		this.setDistance(Integer.parseInt(stepSplit[1]));
	}

	/**
	 * STEP METHODS ***********************************************************.
	 * 
	 * @return the branch
	 */

	/**
	 * Retrieve the step's branch.
	 *
	 * @return branch - int.
	 */
	public int getBranch() {
		return this.branch;
	}

	/**
	 * Retrieve the step's distance.
	 *
	 * @return distance - int.
	 */
	public int getDistance() {
		return this.distance;
	}

	/**
	 * Determine if the step will result in a move.
	 *
	 * @return noDistance - boolean.
	 */
	public boolean isEmpty() {
		return (this.distance == 0);
	}

	/**
	 * Set the step's branch.
	 *
	 * @param branch
	 *            int.
	 */
	public void setBranch(final int branch) {
		this.branch = branch;
	}

	/**
	 * Set the step's distance.
	 *
	 * @param distance
	 *            int.
	 */
	public void setDistance(final int distance) {
		this.distance = distance;
	}

	/**
	 * Set the step equal to the given branch and distance.
	 *
	 * @param branch
	 *            int.
	 * @param distance
	 *            int.
	 * @return thisResult Step.
	 */
	public Step setStep(final int branch, final int distance) {
		this.branch = branch;
		this.distance = distance;
		return this;
	}

	/**
	 * Set the step equal to the given step.
	 *
	 * @param step
	 *            Step.
	 * @return thisResult Step.
	 */
	public Step setStep(final Step step) {
		this.branch = step.getBranch();
		this.distance = step.getDistance();
		return this;
	}

	/**
	 * Shift the step according to the directives of another step and return a
	 * step that describes any remainder.
	 *
	 * @param stepShift
	 *            Step.
	 * @return remainder - Step.
	 */
	public Step shift(final Step stepShift) {
		return new Step(this.shiftBranch(stepShift.getBranch()),
				this.shiftDistance(stepShift.getDistance()));
	}

	/**
	 * Shift the step's branch and return any remainder.
	 *
	 * @param branchShift
	 *            int.
	 * @return remainder - int.
	 */
	public int shiftBranch(final int branchShift) {
		final int remainder = this.branch + branchShift;
		if (remainder >= 0) {
			this.branch = remainder;
			return 0;
		} else {
			this.branch = 0;
			return remainder;
		}
	}

	/**
	 * Shift the step's distance and return any remainder.
	 *
	 * @param distanceShift
	 *            int.
	 * @return remainder - int.
	 */
	public int shiftDistance(final int distanceShift) {
		final int remainder = this.distance + distanceShift;
		if (remainder >= 0) {
			this.distance = remainder;
			return 0;
		} else {
			this.distance = 0;
			return remainder;
		}
	}

	/**
	 * OBJECT METHODS (OVERRIDDEN) ********************************************.
	 * 
	 * @return the string
	 */

	/**
	 * {@inheritDoc}
	 *
	 * Return a string descibing the step.
	 */
	@Override
	public String toString() {
		return "*" + this.branch + "+" + this.distance;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Return a clone of this step.
	 */
	@Override
	public Step clone() {
		return new Step(this.branch, this.distance);
	}
}
