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
package org.hydra.startup;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.hydra.core.Configuration;
import org.hydra.persistence.DataAccessObject;
import org.hydra.persistence.GZipStorageStrategyImpl;
import org.hydra.persistence.ZipStorageStrategyImpl;
import org.hydra.ui.UIWriter;
import org.hydra.utilities.Logger;
import org.hydra.utilities.LoggerLevel;

/**
 * The Class TH.
 */
public class TH {

	/** The config. */
	public static Configuration config = Configuration.getInstance();

	/** The logger. */
	public static Logger logger = Logger.getInstance();
	// Workspace & Repository
	/** The workspace. */
	public static File workspace = new File("target/autotest/");

	/** The workspace parent. */
	public static File workspaceParent = TH.workspace.getParentFile();

	/** The log file. */
	public static File logFile = new File(TH.workspaceParent, ".logdump");

	/** The clio file. */
	public static File clioFile = new File(TH.workspaceParent, ".cliodump");

	/** The repository. */
	public static File repository = new File(TH.workspace, ".hydra");

	/** The fp store. */
	public static File fpStore = new File(TH.repository, "fpStore");

	/** The lu store. */
	public static File luStore = new File(TH.repository, "luStore");
	// File Strings & Hashes
	/** The f1 string. */
	public static String f1String = "Hello World1\n";

	/** The f2 string. */
	public static String f2String = "L1: This is some Content.\n";

	/** The f3 string. */
	public static String f3String = "L1: This is some Content.\nL2: This is some more Content.\n";

	/** The f1 hash. */
	public static String f1Hash = "0aa4f38d72dc55ff0617bdce4796c9f9022a6bef";

	/** The f2 hash. */
	public static String f2Hash = "9d20ad9666cb73bcf4549a188920b24e613de2d3";

	/** The f3 hash. */
	public static String f3Hash = "caf7c324d1cf63b013ccc5eb6db3e56735433b8f";
	// Container String & Hashes
	/** The c header. */
	public static String cHeader = "HH::>>Container\n";

	/** The c1 string. */
	public static String c1String = TH.cHeader + "IT::>>c1File.txt::>>"
			+ TH.f1Hash + "\n";

	/** The c12 string. */
	public static String c12String = TH.c1String + "IT::>>c2File.txt::>>"
			+ TH.f2Hash + "\n";

	/** The c123 string. */
	public static String c123String = TH.c12String + "IT::>>c3File.txt::>>"
			+ TH.f3Hash + "\n";

	/** The c0 hash. */
	public static String c0Hash = "77bd0105ea6b73b506c6a656b8181c87d90cd5f4";

	/** The c1 hash. */
	public static String c1Hash = "fade2e04735742cdfcb634a70fa9b13dabffca57";

	/** The c12 hash. */
	public static String c12Hash = "f57e90475e4470c37bdcab4620d3761333e228a7";

	/** The c123 hash. */
	public static String c123Hash = "4ade434663cbd67b2a486d7181410115c570bf9d";
	// SubContainer String & Hashes
	/** The c123c header. */
	public static String c123cHeader = TH.cHeader + "CO::>>w1Dir::>>"
			+ TH.c0Hash + "\n" + "IT::>>c1File.txt::>>" + TH.f1Hash
			+ "\nIT::>>c2File.txt::>>" + TH.f2Hash + "\nIT::>>c3File.txt::>>"
			+ TH.f3Hash + "\n";

	/** The c123c1 string. */
	public static String c123c1String = TH.cHeader + "CO::>>w1Dir::>>"
			+ TH.c1Hash + "\n" + "IT::>>c1File.txt::>>" + TH.f1Hash
			+ "\nIT::>>c2File.txt::>>" + TH.f2Hash + "\nIT::>>c3File.txt::>>"
			+ TH.f3Hash + "\n";

	/** The c123c0 hash. */
	public static String c123c0Hash = "7e7e35c5f3501cdc916b131b4e1cf4e7e15f0154";

	/** The c123c1 hash. */
	public static String c123c1Hash = "bbef545a39285818de5e3db8d0b3693e2d88355e";
	// State String & Hashes
	/** The s header. */
	public static String sHeader = "HH::>>State\n";

	/** The u1s0 hash. */
	public static String u1s0Hash = "85044a2da164382f6a66d611d334ebbc57309c06";

	/** The u1s0 string. */
	public static String u1s0String = TH.sHeader
			+ "CS::>>NoUUID\n"
			+ "CO::>>null\n"
			+ "VP::>>null\n"
			+ "MD::>>Sat May 07 14:44:06 CEST 2011::>>Scott::>>true::>>Temporary Commit.\n";

	/** The u1s1 hash. */
	public static String u1s1Hash = "93325710fe4f40be55509db627d5f58a04e42ee3";

	/** The u1s1 string. */
	public static String u1s1String = TH.sHeader
			+ "CS::>>UUID\n"
			+ "CO::>>"
			+ TH.c0Hash
			+ "\n"
			+ "VP::>>null\n"
			+ "MD::>>Sun May 08 15:45:07 CEST 2011::>>Scott::>>true::>>Initial Commit.\n";

	/** The u1s2 hash. */
	public static String u1s2Hash = "a1af901a9591cc8b471d291516cfb68e3eadb08b";

	/** The u1s2 string. */
	public static String u1s2String = TH.sHeader
			+ "CS::>>UUID2\n"
			+ "CO::>>"
			+ TH.c1Hash
			+ "\n"
			+ "VP::>>"
			+ TH.u1s1Hash
			+ "\n"
			+ "PS::>>"
			+ TH.u1s1Hash
			+ "\n"
			+ "MD::>>Mon May 09 16:46:08 CEST 2011::>>Scott::>>false::>>Second Commit.\n";
	// Workspace Files
	/** The w1 file. */
	public static File w1File = new File(TH.workspace, "c1File.txt");

	/** The w2 file. */
	public static File w2File = new File(TH.workspace, "c2File.txt");

	/** The w3 file. */
	public static File w3File = new File(TH.workspace, "c3File.txt");

	/** The ne file. */
	public static File neFile = new File(TH.workspace, "NotExistent.txt");

	/** The w1 dir. */
	public static File w1Dir = new File(TH.workspace, "w1Dir");

	/** The d1w1 file. */
	public static File d1w1File = new File(TH.w1Dir, "c1File.txt");

	/** The d1w2 file. */
	public static File d1w2File = new File(TH.w1Dir, "c2File.txt");

	/** The d1w3 file. */
	public static File d1w3File = new File(TH.w1Dir, "c3File.txt");
	// Repository Files
	/** The r1 file. */
	public static File r1File = new File(TH.fpStore, TH.f1Hash);

	/** The r2 file. */
	public static File r2File = new File(TH.fpStore, TH.f2Hash);

	/** The r3 file. */
	public static File r3File = new File(TH.fpStore, TH.f3Hash);

	/** The r0 cont. */
	public static File r0Cont = new File(TH.fpStore, TH.c0Hash);

	/** The r1 cont. */
	public static File r1Cont = new File(TH.fpStore, TH.c1Hash);

	/** The r12 cont. */
	public static File r12Cont = new File(TH.fpStore, TH.c12Hash);

	/** The r123 cont. */
	public static File r123Cont = new File(TH.fpStore, TH.c123Hash);

	/** The r123r0 cont. */
	public static File r123r0Cont = new File(TH.fpStore, TH.c123c0Hash);

	/** The r123r1 cont. */
	public static File r123r1Cont = new File(TH.fpStore, TH.c123c1Hash);

	/** The r1r0 state. */
	public static File r1r0State = new File(TH.fpStore, TH.u1s0Hash);

	/** The r1r1 state. */
	public static File r1r1State = new File(TH.fpStore, TH.u1s1Hash);

	/** The r1r2 state. */
	public static File r1r2State = new File(TH.fpStore, TH.u1s2Hash);
	// Logical Units.
	/** The lu header. */
	public static String luHeader = "HH::>>LogicalUnit\n";

	/** The lu1 string. */
	public static String lu1String = TH.luHeader + "HD::>>" + TH.u1s2Hash
			+ "\nCU::>>" + TH.u1s1Hash + "\n" + "ST::>>" + TH.c1Hash + "\n";

	/** The lu1 file. */
	public static File lu1File = new File(TH.luStore, "aCard1");
	// Stage.
	/** The stage header. */
	public static String stageHeader = "HH::>>Stage\n";

	/** The stage string. */
	public static String stageString = TH.stageHeader + "ST::>>" + TH.c0Hash
			+ "\n" + "LS::>>aCard1::>>" + TH.u1s1Hash + "::>>FOCUS\n";

	/** The stage file. */
	public static File stageFile = new File(TH.repository, "STAGE");

	/**
	 * Setup testing environment.
	 * 
	 * @param loadWorkspace
	 *            the load workspace
	 * @param loadRepository
	 *            the load repository
	 */
	public static void setupTestingEnvironment(final boolean loadWorkspace,
			final boolean loadRepository) {
		TH.setupWorkspace(loadWorkspace);
		TH.setupRepository(loadRepository);
		TH.config.initializeSystem(TH.workspace);
		TH.config.setCurrentWorkingDirectory(TH.workspace);
		UIWriter.getInstance().setVerbosity(0);
		TH.setupLogging();
	}

	/**
	 * Setup logging.
	 */
	public static void setupLogging() {
		TH.logger.setLogFile(TH.logFile);
		TH.logger.setSystemLevel(LoggerLevel.DEBUG);
		TH.logger.setUserLevel(LoggerLevel.NO_LOG);
	}

	/**
	 * Reset testing workspace.
	 * 
	 * @param load
	 *            boolean.
	 */
	public static void setupWorkspace(final boolean load) {
		TH.deleteDirectory(TH.workspace);
		TH.workspace.mkdir();
		if (load) {
			TH.writeFile(TH.w1File, TH.f1String);
			TH.writeFile(TH.w2File, TH.f2String);
			TH.writeFile(TH.w3File, TH.f3String);
			TH.w1Dir.mkdir();
			TH.writeFile(TH.d1w1File, TH.f1String);
		}
	}

	/**
	 * Reset testing repository.
	 * 
	 * @param load
	 *            boolean.
	 */
	public static void setupRepository(final boolean load) {
		TH.deleteDirectory(TH.repository);
		TH.repository.mkdir();
		TH.fpStore.mkdir();
		TH.luStore.mkdir();
		if (load) {
			TH.writeRepositoryArtifact(TH.r1File, TH.f1String);
			TH.writeRepositoryArtifact(TH.r2File, TH.f2String);
			TH.writeRepositoryArtifact(TH.r3File, TH.f3String);
			TH.writeFile(TH.r0Cont, TH.cHeader);
			TH.writeFile(TH.r1Cont, TH.c1String);
			TH.writeFile(TH.r12Cont, TH.c12String);
			TH.writeFile(TH.r123Cont, TH.c123String);
			TH.writeFile(TH.r123r0Cont, TH.c123cHeader);
			TH.writeFile(TH.r123r1Cont, TH.c123c1String);
			TH.writeFile(TH.r1r0State, TH.u1s0String);
			TH.writeFile(TH.r1r1State, TH.u1s1String);
			TH.writeFile(TH.r1r2State, TH.u1s2String);
			TH.writeFile(TH.lu1File, TH.lu1String);
			TH.writeFile(TH.stageFile, TH.stageString);
		}
	}

	/**
	 * Delete a directory and its content.
	 * 
	 * @param targetDirectory
	 *            File.
	 */
	public static void deleteDirectory(final File targetDirectory) {
		if (targetDirectory.isDirectory()) {
			for (final File child : targetDirectory.listFiles()) {
				TH.deleteDirectory(child);
			}
		}
		targetDirectory.delete();
	}

	/**
	 * Delete the specified File.
	 * 
	 * @param targetFile
	 *            File.
	 * @return success - boolean.
	 */
	public static boolean deleteFile(final File targetFile) {
		return targetFile.delete();
	}

	/**
	 * Write given content into specified file.
	 * 
	 * @param targetFile
	 *            File.
	 * @param content
	 *            String.
	 */
	public static void writeFile(final File targetFile, final String content) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(targetFile));
			bw.write(content);
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) {
					bw.flush();
					bw.close();
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Write repository artifact.
	 * 
	 * @param targetFile
	 *            the target file
	 * @param content
	 *            the content
	 */
	public static void writeRepositoryArtifact(final File targetFile,
			final String content) {
		if (DataAccessObject.STORAGE_STRATEGY
				.equals(ZipStorageStrategyImpl.COMPRESSION_TYPE)) {
			ZipOutputStream zos = null;
			try {
				zos = new ZipOutputStream(new FileOutputStream(targetFile));
				zos.putNextEntry(new ZipEntry(targetFile.getName()));
				final byte[] contentBytes = content.getBytes();
				zos.write(contentBytes, 0, contentBytes.length);
			} catch (final Exception e) {
				e.printStackTrace();
			} finally {
				if (zos != null) {
					try {
						zos.close();
					} catch (final Exception e) {
						e.printStackTrace();
					}
				}
			}
		} else if (DataAccessObject.STORAGE_STRATEGY
				.equals(GZipStorageStrategyImpl.COMPRESSION_TYPE)) {
			GZIPOutputStream zos = null;
			try {
				zos = new GZIPOutputStream(new FileOutputStream(targetFile));
				final byte[] contentBytes = content.getBytes();
				zos.write(contentBytes, 0, contentBytes.length);
			} catch (final Exception e) {
				e.printStackTrace();
			} finally {
				if (zos != null) {
					try {
						zos.close();
					} catch (final Exception e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			TH.writeFile(targetFile, content);
		}

	}

	/**
	 * Gathers the contents of the given file.
	 * 
	 * @param targetFile
	 *            File.
	 * @return the string
	 */
	public static String gatherContent(final File targetFile) {
		final StringBuilder sb = new StringBuilder("");
		Scanner scanner = null;
		try {
			scanner = new Scanner(new FileInputStream(targetFile), "UTF-8");
			while (scanner.hasNextLine()) {
				sb.append(scanner.nextLine() + "\n");
			}
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (scanner != null) {
					scanner.close();
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * Print the contents of the given file.
	 * 
	 * @param targetFile
	 *            File.
	 */
	public static void displayFile(final File targetFile) {
		System.out.println("File Content: [" + targetFile + "]");
		System.out
				.println("--------------------------------------------------");
		Scanner scanner = null;
		try {
			scanner = new Scanner(new FileInputStream(targetFile), "UTF-8");
			while (scanner.hasNextLine()) {
				System.out.println(scanner.nextLine());
			}
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (scanner != null) {
					scanner.close();
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

}
