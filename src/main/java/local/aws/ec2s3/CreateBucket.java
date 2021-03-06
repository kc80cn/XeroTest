/**
 * To run this Java code, ensure that you have setup your development environment, including your credentials.
 *
 * Region: AP_SOUTHEAST_2
 * Windows: C:\Users\<yourUserName>\.aws\credentials
 * Linux, macOS, Unix: ~/.aws/credentials
 *
 * this will create a S3 bucket only with bucket name "cicd-s3bucket01"
 *
 */
package local.aws.ec2s3;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;

import java.util.List;

public class CreateBucket {
	public static Bucket getBucket(String bucket_name) {
		final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.AP_SOUTHEAST_2).build();
		Bucket named_bucket = null;
		List<Bucket> buckets = s3.listBuckets();
		for (Bucket b : buckets) {
			if (b.getName().equals(bucket_name)) {
				named_bucket = b;
			}
		}
		return named_bucket;
	}

	public static Bucket createBucket(String bucket_name) {
		final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.AP_SOUTHEAST_2).build();
		Bucket b = null;
		if (s3.doesBucketExistV2(bucket_name)) {
			System.out.format("Bucket %s already exists.\n", bucket_name);
			b = getBucket(bucket_name);
		} else {
			try {
				b = s3.createBucket(bucket_name);
			} catch (AmazonS3Exception e) {
				System.err.println(e.getErrorMessage());
			}
		}
		return b;
	}

	public static void main(String[] args) {
		final String USAGE = "\n" +
			"CreateBucket - create an S3 bucket\n\n" +
			"Usage: CreateBucket <bucketname>\n\n" +
			"Where:\n" +
			"  bucketname - the name of the bucket to create.\n\n" +
			"The bucket name must be unique, or an error will result.\n";

//		if (args.length < 1) {
//			System.out.println(USAGE);
//			System.exit(1);
//		}

		String bucket_name = "cicd-s3bucket01";

		System.out.format("\nCreating S3 bucket: %s\n", bucket_name);
		Bucket b = createBucket(bucket_name);
		if (b == null) {
			System.out.println("Error creating bucket!\n");
		} else {
			System.out.println("Done!\n");
		}
	}
}