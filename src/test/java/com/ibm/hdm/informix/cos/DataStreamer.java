package com.ibm.hdm.informix.cos;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;

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
 * DataStreamer streams the contents of a file as a binary stream to the output
 * stream passed to it.
 */
public class DataStreamer {
	/**
	 * The name of the file to stream
	 */
	String fileName = "";

	/**
	 * Default constructor.
	 * 
	 * @param fileName
	 *            is the name of the file to be streamed.
	 */
	public DataStreamer(String fileName) {
		super();
		this.fileName = fileName;
	}

	/**
	 * Streams the file to the output stream passed to it.
	 * 
	 * @param outputStream
	 *            is the stream used for output.
	 */
	public void stream(OutputStream outputStream) {
		try {
			FileInputStream fileInputStream = new FileInputStream(this.fileName);
			IOUtils.copy(fileInputStream, outputStream);
			fileInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}