package com.ibm.hdm.informix.cos;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
 * ConfigurationBean reads configuration values from a properties file and sets
 * configuration variables to used in the application.
 */
public class ConfigurationBean {

	/**
	 * The name of the bucket the client will connect to
	 */
	private String bucketName = "";

	/**
	 * The IBM Cloud Object Storage API key
	 */
	private String api_key = "";

	/**
	 * The IBM Cloud Service Instance ID
	 */
	private String service_instance_id = "";

	/**
	 * IBM Cloud service end point
	 */
	private String service_endpoint = "";

	/**
	 * The geo location of the object store
	 */
	private String geo_location = "";

	/**
	 * Agent end point
	 */
	private String iam_endpoint = "";

	/**
	 * S3Client time out value
	 */
	private int clientTimeOut = 5000;

	/**
	 * Default constructor
	 */
	public ConfigurationBean() {
		super();
		init();
	}

	/**
	 * Initialize variables with values from properties file.
	 */
	private void init() {
		Properties props = new Properties();
		try {
			InputStream inputStream = new FileInputStream("config.props");
			props.load(inputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.bucketName = props.getProperty("BUCKET_NAME");
		this.api_key = props.getProperty("API_KEY");
		this.service_instance_id = props.getProperty("SERVICE_INSTANCE_ID");
		this.service_endpoint = props.getProperty("SERVICE_ENDPOINT");
		this.geo_location = props.getProperty("LOCATION");
		this.iam_endpoint = props.getProperty("IAM_ENDPOINT");
		this.clientTimeOut = Integer.parseInt(props.getProperty("CLIENT_TIMEOUT"));
	}

	/**
	 * Gets the configured bucket name
	 * 
	 * @return the bucket name
	 */
	public String getBucketName() {
		return bucketName;
	}

	/**
	 * Sets the bucket name
	 * 
	 * @param bucketName
	 *            is the name of the bucket
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	/**
	 * Gets the configured API key
	 * 
	 * @return the API key
	 */
	public String getApi_key() {
		return api_key;
	}

	/**
	 * Sets the API key
	 * 
	 * @param api_key
	 *            is the key to set
	 */
	public void setApi_key(String api_key) {
		this.api_key = api_key;
	}

	/**
	 * Gets the configured service instance ID
	 * 
	 * @return the service instance ID
	 */
	public String getService_instance_id() {
		return service_instance_id;
	}

	/**
	 * Sets the service instance ID
	 * 
	 * @param service_instance_id
	 *            is the service instance id.
	 */
	public void setService_instance_id(String service_instance_id) {
		this.service_instance_id = service_instance_id;
	}

	/**
	 * Gets the configured service end point
	 * 
	 * @return the service end point
	 */
	public String getService_endpoint() {
		return service_endpoint;
	}

	/**
	 * Sets the service point
	 * 
	 * @param service_endpoint
	 *            is the service end point
	 */
	public void setService_endpoint(String service_endpoint) {
		this.service_endpoint = service_endpoint;
	}

	/**
	 * Gets the configured geo location
	 * 
	 * @return the geo location
	 */
	public String getGeo_location() {
		return geo_location;
	}

	/**
	 * Sets the go location
	 * 
	 * @param geo_location
	 *            is the geo location to set
	 */
	public void setGeo_location(String geo_location) {
		this.geo_location = geo_location;
	}

	/**
	 * Get the configured IAM end point
	 * 
	 * @return the IAM end point
	 */
	public String getIam_endpoint() {
		return iam_endpoint;
	}

	/**
	 * Sets the IAM end point
	 * 
	 * @param iam_endpoint
	 *            the IAM end point to set
	 */
	public void setIam_endpoint(String iam_endpoint) {
		this.iam_endpoint = iam_endpoint;
	}

	/**
	 * Gets the configured client timeout
	 * 
	 * @return the client timeout
	 */
	public int getClientTimeOut() {
		return clientTimeOut;
	}

	/**
	 * Sets the client timeout
	 * 
	 * @param clientTimeOut
	 *            is the client timeout to set
	 */
	public void setClientTimeOut(int clientTimeOut) {
		this.clientTimeOut = clientTimeOut;
	}
}
