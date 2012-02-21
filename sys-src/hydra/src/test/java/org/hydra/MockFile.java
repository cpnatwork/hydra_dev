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
package org.hydra;

import java.io.File;

/**
 * The Class MockFile.
 */
public class MockFile extends File {

	/** The Constant serialVersionUID. */
	public static final long serialVersionUID = 01L;

	/**
	 * Instantiates a new mock file.
	 * 
	 * @param path
	 *            the path
	 */
	public MockFile(final String path) {
		super(path);
		this.setReadable(false);
	}

	/**
	 * Instantiates a new mock file.
	 * 
	 * @param file
	 *            the file
	 */
	public MockFile(final File file) {
		super(file.getPath());
		this.setReadable(false);
	}

	/**
	 * Instantiates a new mock file.
	 * 
	 * @param directory
	 *            the directory
	 * @param path
	 *            the path
	 */
	public MockFile(final File directory, final String path) {
		super(directory, path);
		this.setReadable(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.File#length()
	 */
	@Override
	public long length() {
		throw new RuntimeException("MockFile Runtime Exception.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.File#isFile()
	 */
	@Override
	public boolean isFile() {
		throw new RuntimeException("MockFile Runtime Exception.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.File#exists()
	 */
	@Override
	public boolean exists() {
		throw new RuntimeException("MockFile Runtime Exception.");
	}

}
