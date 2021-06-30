/**
 * To run this Java code, ensure that you have setup your development environment, including your credentials.
 *
 * Region: AP_SOUTHEAST_2
 * Windows: C:\Users\<yourUserName>\.aws\credentials
 * Linux, macOS, Unix: ~/.aws/credentials
 *
 */
package local.aws.ec2s3;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3control.S3ControlClient;
import software.amazon.awssdk.services.s3control.model.CreateAccessPointRequest;
import software.amazon.awssdk.services.s3control.model.S3ControlException;
import software.amazon.awssdk.services.s3control.model.DeleteAccessPointRequest;

public class CreateAccessPoint {

	public static void main(String[] args) {

		final String USAGE = "\n" +
			"Usage:\n" +
			"    CreateAccessPoint <accountId> <bucketName> <accessPointName>\n\n" +
			"Where:\n" +
			"    accountId - the account id that owns the Amazon S3 bucket. \n\n" +
			"    bucketName - the Amazon S3 bucket name. \n" +
			"    accessPointName - the access point name (for example, myaccesspoint). \n";

		if (args.length != 3) {
			System.out.println(USAGE);
			System.exit(1);
		}

		String accountId = args[0];
		String bucketName = args[1];
		String accessPointName = args[2];

		Region region = Region.AP_SOUTHEAST_2;
		S3ControlClient s3ControlClient = S3ControlClient.builder()
			.region(region)
			.build();

		createSpecificAccessPoint(s3ControlClient, accountId, bucketName, accessPointName );
		deleteSpecificAccessPoint(s3ControlClient, accountId, accessPointName);
		s3ControlClient.close();;
	}

	// snippet-start:[s3.java2.create_access_point.main]
	// This method creates an access point for the given Amazon S3 bucket.
	public static void createSpecificAccessPoint(S3ControlClient s3ControlClient,
	                                             String accountId,
	                                             String bucketName,
	                                             String accessPointName) {

		try {
			CreateAccessPointRequest accessPointRequest = CreateAccessPointRequest.builder()
				.accountId(accountId)
				.bucket(bucketName)
				.name(accessPointName)
				.build();

			s3ControlClient.createAccessPoint(accessPointRequest);
			System.out.println("The access point was created" );

		} catch (S3ControlException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}
	}
	// snippet-end:[s3.java2.create_access_point.main]

	public static void deleteSpecificAccessPoint(S3ControlClient s3ControlClient,
	                                             String accountId,
	                                             String accessPointName) {

		try {
			DeleteAccessPointRequest deleteAccessPointRequest = DeleteAccessPointRequest.builder()
				.name(accessPointName)
				.accountId(accountId)
				.build();

			s3ControlClient.deleteAccessPoint(deleteAccessPointRequest);

		} catch (S3ControlException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}


	}


}