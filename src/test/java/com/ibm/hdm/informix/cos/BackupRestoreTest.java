package com.ibm.hdm.informix.cos;

import java.io.BufferedOutputStream;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
 * Convenience class for stand alone testing of Informix-Backup-IBM-COS. Class
 * not intended to be instantiated.
 */
public class BackupRestoreTest {

	/**
	 * Default Constructor
	 */
	private BackupRestoreTest() {

	}

	/**
	 * Entry point for testing round trip backup and restore of a file.
	 * 
	 * @param args
	 *            are the command line arguments arg1 is the name of the file that
	 *            will be put and retrieved from object storage arg2 is the full or
	 *            relative path to the InfomixCOS.jar file. Relative path is
	 *            determined from the location where BackupRestoreTest is executed
	 *            from.
	 */
	public static void main(String[] args) {
		String fileName = "uploadTest.txt";
		String jarFile = "target/InformixCOS/InformixCOS.jar";
		System.out.println("BackupRestore test starting....");
		if (args.length > 0) {
			fileName = args[0];
			jarFile = args[1];
		}
		System.out.println("File Name: " + fileName);
		System.out.println("JAR File: " + jarFile);
		System.out.println("Upload started...");
		putObject(fileName, jarFile);
		System.out.println("Upload Complete!");
		try {
			System.out.println("Pausing for 4 seconds before retrieving object...");
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Down started...");
		restoreObject(fileName, jarFile);
		System.out
				.println("Download Completed!  See file " + getRestoreFilename(fileName) + " for the restored object.");
	}

	/**
	 * Test method. Streams the contents of a given file to an InformixCOS process
	 * to test backing up file to IBM Cloud Object Storage.
	 * 
	 * @param fileName
	 *            is the name of the file to be processed
	 */
	public static void putObject(String fileName, String jarFile) {
		System.out.println("putObject called");
		try {
			Process process = Runtime.getRuntime().exec("java -jar " + jarFile + " BACKUP " + fileName);
			DataStreamer dataStreamer = new DataStreamer(fileName);
			dataStreamer.stream(process.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test method. Convenience method for simply putting a file on IBM Cloud Object
	 * Storage.
	 * 
	 * @param fileName
	 */
	public static void putDirect(String fileName) {
		COSClient cosClient = new COSClient();
		cosClient.backupStreamMultiPart(fileName);
	}

	/**
	 * Test Method. Gets a stream from an InformixCOS standard out and writes the
	 * stream to a file.
	 * 
	 * @param fileName
	 */
	public static void restoreObject(String fileName, String jarFile) {
		System.out.println("restoreObject called....");
		try {
			Process process2 = Runtime.getRuntime().exec("java -jar " + jarFile + " RESTORE " + fileName);
			InputStream is = process2.getInputStream();
			FileOutputStream fileOutputStream = new FileOutputStream(getRestoreFilename(fileName));
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
			byte[] readBuffArr = new byte[50];
			int readBytes = 0;
			while ((readBytes = is.read(readBuffArr)) >= 0) {
				bufferedOutputStream.write(readBuffArr, 0, readBytes);
			}
			bufferedOutputStream.close();
			fileOutputStream.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Formats the file name of an object that has been restored to include the
	 * string _RESTORED
	 * 
	 * @param inFilename
	 *            is the original file name.
	 * @return the modified file name.
	 */
	private static String getRestoreFilename(String inFilename) {
		int index = inFilename.lastIndexOf(".");
		String filename = inFilename.substring(0, index) + "_RESTORED"
				+ inFilename.substring(index, inFilename.length());
		return filename;
	}
}
