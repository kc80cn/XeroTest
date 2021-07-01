/**
 * To run this Java code, ensure that you have setup your development environment, including your credentials.
 *
 * Region: AP_SOUTHEAST_2
 * Windows: C:\Users\<yourUserName>\.aws\credentials
 * Linux, macOS, Unix: ~/.aws/credentials
 *
 */
package local.aws.ec2s3;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutBucketPolicyRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.regions.Region;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SetBucketPolicy {

	public static void main(String[] args) {
		final String USAGE = "\n" +
			"Usage:\n" +
			"    SetBucketPolicy <bucketName> <polFile>\n\n" +
			"Where:\n" +
			"    bucketName - the Amazon S3 bucket to set the policy on.\n" +
			"    polFile - a JSON file containing the policy (see the Amazon S3 Readme for an example). \n" ;

//		if (args.length != 2) {
//			System.out.println(USAGE);
//			System.exit(1);
//		}

		String bucketName = "cicd-s3bucket03";
		String polFile = "accessOnlyFromVPC.json";
		String policyText = getBucketPolicyFromFile(polFile);

		Region region = Region.AP_SOUTHEAST_2;
		S3Client s3 = S3Client.builder()
			.region(region)
			.build();

		setPolicy(s3, bucketName, policyText);
		s3.close();
	}

	// snippet-start:[s3.java2.set_bucket_policy.main]
	public static void setPolicy(S3Client s3, String bucketName, String policyText) {

		System.out.println("Setting policy:");
		System.out.println("----");
		System.out.println(policyText);
		System.out.println("----");
		System.out.format("On Amazon S3 bucket: \"%s\"\n", bucketName);

		try {
			PutBucketPolicyRequest policyReq = PutBucketPolicyRequest.builder()
				.bucket(bucketName)
				.policy(policyText)
				.build();
			s3.putBucketPolicy(policyReq);
		} catch (S3Exception e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}
		System.out.println("Done!");
	}

	// Loads a JSON-formatted policy from a file
	public static String getBucketPolicyFromFile(String policyFile) {

		StringBuilder fileText = new StringBuilder();
		try {
			List<String> lines = Files.readAllLines(
				Paths.get(policyFile), Charset.forName("UTF-8"));
			for (String line : lines) {
				fileText.append(line);
			}
		} catch (IOException e) {
			System.out.format("Problem reading file: \"%s\"", policyFile);
			System.out.println(e.getMessage());
		}

		try {
			final JsonParser parser = new ObjectMapper().getFactory().createParser(fileText.toString());
			while (parser.nextToken() != null) {
			}

		} catch (JsonParseException jpe) {
			jpe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return fileText.toString();
	}
}