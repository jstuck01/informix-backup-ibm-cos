package com.ibm.hdm.informix.cos;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.cloud.objectstorage.ClientConfiguration;
import com.ibm.cloud.objectstorage.SDKGlobalConfiguration;
import com.ibm.cloud.objectstorage.auth.AWSCredentials;
import com.ibm.cloud.objectstorage.auth.AWSStaticCredentialsProvider;
import com.ibm.cloud.objectstorage.auth.BasicAWSCredentials;
import com.ibm.cloud.objectstorage.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.ibm.cloud.objectstorage.oauth.BasicIBMOAuthCredentials;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3ClientBuilder;
import com.ibm.cloud.objectstorage.services.s3.model.CompleteMultipartUploadRequest;
import com.ibm.cloud.objectstorage.services.s3.model.InitiateMultipartUploadRequest;
import com.ibm.cloud.objectstorage.services.s3.model.InitiateMultipartUploadResult;
import com.ibm.cloud.objectstorage.services.s3.model.ObjectMetadata;
import com.ibm.cloud.objectstorage.services.s3.model.PartETag;
import com.ibm.cloud.objectstorage.services.s3.model.S3Object;
import com.ibm.cloud.objectstorage.services.s3.model.S3ObjectInputStream;
import com.ibm.cloud.objectstorage.services.s3.model.UploadPartRequest;
import com.ibm.cloud.objectstorage.services.s3.model.UploadPartResult;

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
 * The class COSClient is a utility to move objects to and from IBM Cloud Object
 * storage using S3 interfaces. Movement of both large and small objects are
 * supported.
 */
public class COSClient {
	private static Log logger = LogFactory.getLog(COSClient.class);

	/**
	 * Utility configuration
	 */
	ConfigurationBean configurationBean = null;

	/**
	 * S3 client used to interact with the IBM Cloud Object store
	 */
	private static AmazonS3 _s3Client = null;

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
	 * Default constructor to initialize a new client
	 */
	public COSClient() {
		super();
		logger.info("COSClient initializing");
		this.configurationBean = new ConfigurationBean();
		SDKGlobalConfiguration.IAM_ENDPOINT = this.configurationBean.getIam_endpoint();
		this.bucketName = this.configurationBean.getBucketName();
		this.api_key = this.configurationBean.getApi_key();
		this.service_instance_id = this.configurationBean.getService_instance_id();
		this.service_endpoint = "https://" + this.configurationBean.getService_endpoint();
		this.geo_location = this.configurationBean.getGeo_location();
		_s3Client = createClient(this.api_key, this.service_instance_id, this.service_endpoint, this.geo_location);
	}

	/**
	 * Puts a file on IBM Cloud Object Storage
	 * 
	 * @param objectKey
	 *            is a name that is to be used as this object's key in the cloud
	 *            repository
	 * @param fileName
	 *            is the name of the file that is to be stored in the cloud
	 *            repository
	 */
	public void backupFile(String objectKey, String fileName) {
		_s3Client.putObject(this.bucketName, objectKey, new File(fileName));
	}

	/**
	 * Captures a data stream from standard input and puts the bytes onto IBM Cloud
	 * Object Storage. Supports data streams up to 5MB in size. For larger objects,
	 * see the method backupStreamMultiPart.
	 * 
	 * @param objectKey
	 *            is the name that will be used as the object store key.
	 */
	public void backupStream(String objectKey) {
		logger.info("COS Client backup stream starting....");
		logger.info("Object Key: " + objectKey);
		try {
			logger.info("Reading stream...");
			byte[] contentBytes = IOUtils.toByteArray(System.in);
			Long contentLength = Long.valueOf(contentBytes.length);
			logger.info("Bytes Read: " + contentLength);
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(contentLength);
			metadata.setContentType("binary/octet-stream");
			InputStream stream = new ByteArrayInputStream(contentBytes);
			logger.info("Uploading object...");
			_s3Client.putObject(this.bucketName, objectKey, stream, metadata);
			logger.info("Upload complete!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Captures a data stream from standard input and puts the bytes onto IBM Cloud
	 * Object Storage. Data streams are separated into 5MB buffers which are
	 * transfered to the cloud.
	 * 
	 * @param objectKey
	 *            is the name that will be used as the object store key.
	 */
	public void backupStreamMultiPart(String objectKey) {
		logger.info("COS Client backup stream starting using backupStreamMultiPart");
		logger.info("Object Key: " + objectKey);
		try {
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType("binary/octet-stream");
			logger.info("Initiating multi part upload");
			InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(this.bucketName, objectKey,
					metadata);
			InitiateMultipartUploadResult initResult = _s3Client.initiateMultipartUpload(initRequest);
			ArrayList<PartETag> partETags = new ArrayList<PartETag>();
			long bytesRead = 0;
			long totalBytesRead = 0;
			int partSize = 100 * 1024 * 1024;
			byte[] part = new byte[partSize];
			int partNumber = 1;
			logger.info("Reading stream...");
			while ((bytesRead = IOUtils.read(System.in, part)) > 0) {
				logger.info("Bytes Read:  " + bytesRead);
				logger.info("Uploading part: " + partNumber);
				ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(part);
				UploadPartRequest uploadRequest = new UploadPartRequest();
				uploadRequest.setUploadId(initResult.getUploadId());
				uploadRequest.setBucketName(this.bucketName);
				uploadRequest.setKey(objectKey);
				uploadRequest.setPartNumber(partNumber);
				uploadRequest.setPartSize(bytesRead);
				uploadRequest.setInputStream(byteArrayInputStream);
				UploadPartResult uploadPartResult = _s3Client.uploadPart(uploadRequest);
				partETags.add(uploadPartResult.getPartETag());
				logger.info("Uploading part complete!");
				totalBytesRead = totalBytesRead + bytesRead;
				partNumber++;
				bytesRead = 0;
			}
			logger.info("Done reading stream.");
			logger.info("Total Bytes Uploaded: " + totalBytesRead);
			logger.info("Completing multipart upload...");
			CompleteMultipartUploadRequest completeRequest = new CompleteMultipartUploadRequest(this.bucketName,
					objectKey, initResult.getUploadId(), partETags);
			_s3Client.completeMultipartUpload(completeRequest);
			logger.info("Upload complete!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initiates a download stream from IBM object storage and redirects to standard
	 * output.
	 * 
	 * @param objectKey
	 *            is the key of the object to retrieve and stream.
	 */
	public void restoreStream(String objectKey) {
		logger.info("COS Client restore stream starting using restoreSream");
		logger.info("Object Key: " + objectKey);
		logger.info("Bucket Name: " + this.configurationBean.getBucketName());
		S3Object s3Response = _s3Client.getObject(this.bucketName, objectKey);
		S3ObjectInputStream s3Input = s3Response.getObjectContent();
		try {
			logger.info("Streaming object to standard output...");
			IOUtils.copy(s3Input, System.out);
			s3Input.close();
			logger.info("Completed streaming object!");
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		logger.info("Restore stream complete!");
	}

	/**
	 * Removes an object from IBM Cloud Object Storage
	 * 
	 * @param objectKey
	 *            is the key of the object to be removed
	 */
	public void drop(String objectKey) {
		_s3Client.deleteObject(this.bucketName, objectKey);
	}

	/**
	 * Create a new S3 client for interacting with the IBM Cloud Object Store
	 * 
	 * @param api_key
	 *            is the key to the object store
	 * @param service_instance_id
	 *            is the IBM Cloud service instance
	 * @param endpoint_url
	 *            is the service endpoint
	 * @param location
	 *            is the geo location
	 * @return AmazonS3
	 */
	public AmazonS3 createClient(String api_key, String service_instance_id, String endpoint_url, String location) {
		AWSCredentials credentials;
		if (endpoint_url.contains("objectstorage.softlayer.net")) {
			credentials = new BasicIBMOAuthCredentials(api_key, service_instance_id);
		} else {
			String access_key = api_key;
			String secret_key = service_instance_id;
			credentials = new BasicAWSCredentials(access_key, secret_key);
		}
		ClientConfiguration clientConfig = new ClientConfiguration()
				.withRequestTimeout(this.configurationBean.getClientTimeOut());
		clientConfig.setUseTcpKeepAlive(true);
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withEndpointConfiguration(new EndpointConfiguration(endpoint_url, location))
				.withPathStyleAccessEnabled(true).withClientConfiguration(clientConfig).build();
		return s3Client;
	}
}