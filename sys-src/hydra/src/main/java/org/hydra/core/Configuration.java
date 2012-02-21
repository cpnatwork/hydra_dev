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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Observable;
import java.util.Properties;

/**
 * Singleton which maintains the system's configuration; to include the
 * workspace, repository, user id and other configurations. Also responsible for
 * initializing a default repository when required.
 *
 * @author Scott A. Hady
 * @version 0.2
 * @since 0.1
 */
public class Configuration extends Observable {

	/** The config. */
	private static Configuration config = null;
	// Workspace & Repository Directory Structure
	/** The workspace. */
	private File workspace = new File(".");

	/** The repository. */
	private File repository;

	/** The fp store. */
	private File fpStore;

	/** The lu store. */
	private File luStore;

	/** The current working directory. */
	private File currentWorkingDirectory = this.workspace;
	// Properties
	/** The properties file. */
	private File propertiesFile;

	/** The props. */
	private final Properties props = new Properties();

	/**
	 * Protected default constructor, retrieve singleton instance using the.
	 *
	 * {@link #getInstance} method.
	 */
	protected Configuration() {
	}

	/**
	 * Retrieve the singleton instance of the system's configuration.
	 *
	 * @return config - Configuration.
	 */
	public static Configuration getInstance() {
		if (Configuration.config == null) {
			Configuration.config = new Configuration();
		}
		return Configuration.config;
	}

	/**
	 * CONFIGURATION METHODS **************************************************.
	 * 
	 * @return the string
	 */

	/**
	 * Retrieve the path extension from the system's current working directory
	 * to the workspace.
	 *
	 * @return pathExtension - String.
	 */
	public String calculatePathExtension() {
		String pathExt = "";
		try {
			pathExt = this.currentWorkingDirectory.getCanonicalPath().replace(
					this.workspace.getCanonicalPath(), "");
		} catch (final Exception e) {
			this.debugOut("Unable to Calculate Path Extension from CWD ["
					+ this.currentWorkingDirectory + "] and WS ["
					+ this.workspace + "].");
		}
		return pathExt;

	}

	/**
	 * Retrieve the designated property's associated value.
	 *
	 * @param name
	 *            String.
	 * @return value - String.
	 */
	public String getProperty(final String name) {
		return this.props.getProperty(name);
	}

	/**
	 * Retrieve the system's current working directory.
	 *
	 * @return currentWorkingDirectory - File.
	 */
	public File getCurrentWorkingDirectory() {
		return this.currentWorkingDirectory;
	}

	/**
	 * Retrieve the system's fingerprinted content storage location.
	 *
	 * @return fpStore - File.
	 */
	public File getFPStore() {
		return this.fpStore;
	}

	/**
	 * Retrieve the system's logical unit storage location.
	 *
	 * @return luStore - File.
	 */
	public File getLUStore() {
		return this.luStore;
	}

	/**
	 * Retrieve the system's storage location.
	 *
	 * @return storageLocation - File.
	 */
	public File getRepository() {
		return this.repository;
	}

	/**
	 * Retrieve the system's user identification.
	 *
	 * @return userId - String.
	 */
	public String getUserId() {
		return this.props.getProperty("Core.userId");
	}

	/**
	 * Retrieve the system's working location.
	 *
	 * @return workingLocation - File.
	 */
	public File getWorkspace() {
		return this.workspace;
	}

	/**
	 * Determine if the designated property is present.
	 *
	 * @param name
	 *            String.
	 * @return isPresent - boolean.
	 */
	public boolean hasProperty(final String name) {
		return this.props.containsKey(name);
	}

	/**
	 * Set the associated value of a designated property.
	 *
	 * @param name
	 *            String.
	 * @param value
	 *            String.
	 */
	public void setProperty(final String name, final String value) {
		if (value == null) {
			this.props.remove(name);
		} else {
			this.props.setProperty(name, value);
		}
		this.storeProperties();
		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * Set the system's current working directory.
	 *
	 * @param currentWorkingDirectory
	 *            File.
	 */
	public void setCurrentWorkingDirectory(final File currentWorkingDirectory) {
		this.currentWorkingDirectory = currentWorkingDirectory;
	}

	/**
	 * Set the system's user identification.
	 *
	 * @param userId
	 *            String.
	 */
	public void setUserId(final String userId) {
		this.props.setProperty("Core.userId", userId);
		this.storeProperties();
	}

	/**
	 * Defines the workspace, repository strucure and properties. The current
	 * working directory is not set or updated.
	 *
	 * @param workspace
	 *            File.
	 */
	public void initializeSystem(final File workspace) {
		this.workspace = workspace;
		this.repository = new File(workspace, ".hydra");
		if (!this.repository.exists()) {
			this.repository.mkdir();
		}
		this.fpStore = new File(this.repository, "fpStore");
		if (!this.fpStore.exists()) {
			this.fpStore.mkdir();
		}
		this.luStore = new File(this.repository, "luStore");
		if (!this.luStore.exists()) {
			this.luStore.mkdir();
		}
		this.propertiesFile = new File(this.repository, "hydra.properties");
		if (!this.propertiesFile.exists()) {
			this.setDefaultProperties();
		} else {
			this.loadProperties();
		}
	}

	/**
	 * Configure the system's properties.
	 */
	public void configureSystem() {
		if (!this.propertiesFile.exists()) {
			this.setDefaultProperties();
		} else {
			this.loadProperties();
		}
	}

	/**
	 * CONFIGURATION METHODS (PRIVATE) ****************************************.
	 */

	/**
	 * Sets the system's properties to the defaults.
	 */
	private void setDefaultProperties() {
		// Set Core Properties
		this.props.setProperty("Core.userId", System.getProperty("user.name"));
		this.props.setProperty("Core.forceCommit", "true");
		// Store Properties
		this.storeProperties();
	}

	/**
	 * Load properties from properties file.
	 */
	private void loadProperties() {
		if (this.propertiesFile.exists()) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(this.propertiesFile);
				this.props.load(fis);
			} catch (final Exception e) {
				this.debugOut("Unable to Load Properties ["
						+ this.propertiesFile + "]." + e);
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (final Exception e) {
						this.debugOut("Unable to Close PropFile ["
								+ this.propertiesFile + "]." + e);
					}
				}
			}
		}
	}

	/**
	 * Store the configuration properties to properties file.
	 */
	private void storeProperties() {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(this.propertiesFile);
			this.props.store(fos, "Hydra Properties");
		} catch (final Exception e) {
			this.debugOut("Unable to Store Properties [" + this.propertiesFile
					+ "]." + e);
		} finally {
			if (fos != null) {
				try {
					fos.flush();
					fos.close();
				} catch (final Exception e) {
					this.debugOut("Unable to Close Properties File ["
							+ this.propertiesFile + "]." + e);
				}
			}
		}
	}

	/**
	 * Debug out.
	 * 
	 * @param message
	 *            the message
	 */
	private void debugOut(final String message) {
		// System.out.println(message);
	}
}
