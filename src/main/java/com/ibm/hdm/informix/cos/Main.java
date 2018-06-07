package com.ibm.hdm.informix.cos;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Class to provide entry point into application.
 */
public final class Main {
	private static Log logger = LogFactory.getLog(Main.class);

	/**
	 * Default Constructor
	 */
	private Main() {

	}

	/**
	 * Entry point for Informix-Backup-IBM-COS
	 * 
	 * @param args
	 *            The arguments for the runtime. Arg1 is the requested operation to
	 *            be performed. BACKUP, RESTORE or DROP. Arg2 is the IBM Cloud
	 *            Object store object key to be used in the requested operation
	 */
	public static void main(String[] args) {
		logger.info("Informix COS Started");
		if (args.length < 2) {
			logger.info("Invalid usage!  Command line arguments must include <OPERATION> and <OBJECT-KEY>.  Exiting!");
			System.out.println("USAGE:  run <OPERATION> <OBJECT-KEY>");
			System.exit(0);
		}
		String operation = args[0];
		String filename = args[1];
		logger.info(String.format("Operation Requested: %s   Object Key: %s", operation, filename));
		COSClient cos = new COSClient();
		if (operation.toLowerCase().equals("backup")) {
			logger.info("Backup operation initiated");
			cos.backupStreamMultiPart(filename);
			logger.info("Backup operation completed");
		} else if (operation.toLowerCase().equals("restore")) {
			logger.info("Restore operation initiated");
			cos.restoreStream(filename);
			logger.info("Restore operation completed");
		} else if (operation.toLowerCase().equals("delete")) {
			logger.info("Drop operation initiated");
			cos.drop(filename);
			logger.info("Drop operation completed");
		}
		logger.info("Informix COS Completed");
	}
}