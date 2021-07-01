/**
 * To run this Java code, ensure that you have setup your development environment, including your credentials.
 *
 * Region: AP_SOUTHEAST_2
 * Windows: C:\Users\<yourUserName>\.aws\credentials
 * Linux, macOS, Unix: ~/.aws/credentials
 *
 */
package local.aws.ec2s3;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetBucketPolicyRequest;
import software.amazon.awssdk.services.s3.model.GetBucketPolicyResponse;


public class GetBucketPolicy {

	public static void main(String[] args) {

		final String USAGE = "\n" +
			"Usage:\n" +
			"    GetBucketPolicy <bucketName>\n\n" +
			"Where:\n" +
			"    bucketName - the Amazon S3 bucket to get the policy from.\n\n" ;

//		if (args.length != 1) {
//			System.out.println(USAGE);
//			System.exit(1);
//		}

		String bucketName = "cicd-s3bucket01";
		System.out.format("Getting policy for bucket: \"%s\"\n\n", bucketName);
		Region region = Region.AP_SOUTHEAST_2;
		S3Client s3 = S3Client.builder()
			.region(region)
			.build();

		String polText = getPolicy(s3, bucketName );
		System.out.println("Policy Text: "+polText);
		s3.close();
	}


	public static String getPolicy(S3Client s3, String bucketName) {

		String policyText = "";
		System.out.format("Getting policy for bucket: \"%s\"\n\n", bucketName);

		GetBucketPolicyRequest policyReq = GetBucketPolicyRequest.builder()
			.bucket(bucketName)
			.build();

		try {
			GetBucketPolicyResponse policyRes = s3.getBucketPolicy(policyReq);
			policyText = policyRes.policy();
			return policyText;
		} catch (S3Exception e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}
		return "";
	}

}