# informix-backup-ibm-cos
The Informix-Backup-IBM-COS project enables backing up Informix databases to IBM Cloud Object Storage leveraging S3 storage protocols.  The utility is simple to use, and once configured, requires only knowledge of Informix backup and restore operations.  It leverages the Backup and Restore (BAR) function with STDIO features of Informix and provides a seamless bridge to IBM Cloud Object Storage.  Both log files and data files can be configured for backup and restore operations.

Full support for Informix Backup and Restore function is available as follows:
* full level 0 backups 
* incremental level 1 and 2 backups
* full restore operations from the last backup
* point in time restore 
* point in log restore
* Informix based encrypted backup and restore

Simply use Backup and Restore (BAR) functions as you would with any other registered device with the Primary Storage Manager.   

## Getting Started
Pull the repository and execute maven to build the project.  (e,g,:  mvn package)  This will result in the distribution being created as a compressed file.  Decompress the file to access the artifacts.  Distribution packages are created as TAR and ZIP archives. 

NOTE:  Primary testing has been completed on Linux.

**Building for Linux**
Pull the repository and execute a maven build.  (e.g.:  mvn package).  This will result in a compressed tar file.  Simply decompress the file (e.g.:  gunzip InformixCOS-bin.tar.gz) and then extract the contents of the archive.  (e.g.:  tar -xvf InformixCOS-bin.tar).  Once expanded, you will see the following files:

* config.props (Required configuration file)
* InformixCOS.jar (Executable jar file)
* run.sh (bash shell script)
* run.bat (Windows batch file)

Ensure that the run.sh file has an executable attribute set.  (chmod +x run.sh) and the config.props file is edited with your specific runtime information.  See the configuration section below for details.



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

## Informix Configuration
InformixCOS needs to be registered with the Informix Primary Storage Manager (PSM).  After placing InformixCOS on the server, use the following command to register it with PSM.

**DBSPOOL Registration**
	onpsm -D add /home/informix/demo/BAR/PSM/STDIO/InformixCOS/run.sh -t STDIO -g DBSPOOL --write_arg "BACKUP @obj_name1@.@obj_id@.@obj_part@" --read_arg "RESTORE @obj_name1@.@obj_id@.@obj_part@" --drop_arg "DELETE @obj_name1@.@obj_id@.@obj_part@"

**LOGPOOL Registration**
	onpsm -D add /home/informix/demo/BAR/PSM/STDIO/InformixCOS/run.sh -t STDIO -g LOGPOOL -p HIGHEST  --write_arg "BACKUP @obj_name1@.@obj_id@.@obj_part@" --read_arg "RESTORE @obj_name1@.@obj_id@.@obj_part@" --drop_arg "DELETE @obj_name1@.@obj_id@.@obj_part@"
	
After PSM registration is completed, use onbar to execute Informix backup and restore operations and InformixCOS will automatically store and retrieve your backups to and from IBM Cloud Object Storage. 

## Encryption Information ##
IBM Cloud Object Storage encrypts all data in motion and at rest.  In addition, the encryption features of Informix can be leveraged to store backup files directly as Informix encrypted objects, offering an additional level of security for data at rest.   
 


