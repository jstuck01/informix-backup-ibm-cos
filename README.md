# informix-backup-ibm-cos
Informix-Backup-IBM-COS is a utility that works with Informix onbar backups that leverage STDIO.  This utility communicates with Informix STDIO to stream backup data to the IBM Cloud Object Store.  In addition, restore operations will result in the object being retrieved and streamed to Informix STDIO.  

## Getting Started
Pull the repository and execute maven to build the project.  (e,g,:  mvn package)  This will result in the distribution being created as a compresses file.  Decompress the file, and un-tar the artifact.

## Configuration
Config Variable | Example Value | Description | Required
----------------|---------------|-------------|----------
BUCKET_NAME  |  mybucket  |  The name of the IBM Cloud Object Storage bucket  | YES
API_KEY  | cMJD73pkjfglb3skum96SxlrrZYlnEGSn-tE3i_cr7xY  |  The value of "apikey" from the service credentials  |  YES
SERVICE_INSTANCE_ID  |  crn:v1:bluemix:public:cloud-object-storage:global:a/a676d937c:a160-4583-4d7f-8bd5-e6a93b::  |  The value of "resource_instance_id" from the service credentials  |  YES
SERVICE_ENDPOINT  |  s3-api.us-geo.objectstorage.softlayer.net  |  Default value should not need to be changed  |  YES
LOCATION  |  US  | The geo location for the cloud object storage location  |  YES
IAM_ENDPOINT  | https://iam.bluemix.net/oidc/token  |  Default value should not have to be changed  |  YES
CLIENT_TIMEOUT  |  50000  |  The S3 client timeout value.  |  YES



