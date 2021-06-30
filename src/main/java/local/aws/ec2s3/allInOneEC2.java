package local.aws.ec2s3;
import java.util.Arrays;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeKeyPairsRequest;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.IpRange;
import com.amazonaws.services.ec2.model.MonitorInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.UnmonitorInstancesRequest;

public class allInOneEC2 {

	private static final AWSCredentials credentials;

	static {
		// put your accesskey and secretkey here
		credentials = new BasicAWSCredentials(
			"AKIA3N7V674T3FOA4XEE",
			"H6gWF+Selr/MlFqp1OzKTGoWdK+HQfRY1a3Nnsij"
		);
	}

	public static void main(String[] args) {

		// Set up the client
		AmazonEC2 ec2Client = AmazonEC2ClientBuilder.standard()
			.withCredentials(new AWSStaticCredentialsProvider(credentials))
			.withRegion(Regions.AP_SOUTHEAST_2)
			.build();

		// Create a security group
		CreateSecurityGroupRequest createSecurityGroupRequest = new CreateSecurityGroupRequest().withGroupName("CICDSecurityGroup")
			.withDescription("CICD Security Group");
		ec2Client.createSecurityGroup(createSecurityGroupRequest);

		// Allow HTTP and SSH traffic
		IpRange ipRange1 = new IpRange().withCidrIp("0.0.0.0/0");

/*		IpPermission ipPermission1 = new IpPermission().withIpv4Ranges(Arrays.asList(new IpRange[] { ipRange1 }))
			.withIpProtocol("tcp")
			.withFromPort(80)
			.withToPort(80);*/

		IpPermission ipPermission2 = new IpPermission().withIpv4Ranges(Arrays.asList(new IpRange[] { ipRange1 }))
			.withIpProtocol("tcp")
			.withFromPort(22)
			.withToPort(22);

		AuthorizeSecurityGroupIngressRequest authorizeSecurityGroupIngressRequest = new AuthorizeSecurityGroupIngressRequest()
			.withGroupName("CICDSecurityGroup")
			.withIpPermissions(ipPermission2);

		ec2Client.authorizeSecurityGroupIngress(authorizeSecurityGroupIngressRequest);

		// Create KeyPair
		CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest()
			.withKeyName("CICD-key-pair");
		CreateKeyPairResult createKeyPairResult = ec2Client.createKeyPair(createKeyPairRequest);
		String privateKey = createKeyPairResult
			.getKeyPair()
			.getKeyMaterial(); // make sure you keep it, the private key, Amazon doesn't store the private key

		// See what key-pairs you've got
		DescribeKeyPairsRequest describeKeyPairsRequest = new DescribeKeyPairsRequest();
		DescribeKeyPairsResult describeKeyPairsResult = ec2Client.describeKeyPairs(describeKeyPairsRequest);

		// Launch an Amazon Instance
		RunInstancesRequest runInstancesRequest = new RunInstancesRequest().withImageId("ami-078d5c7afa60d8d1b") // https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/AMIs.html | https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/usingsharedamis-finding.html
			.withInstanceType("t2.micro") // https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/instance-types.html
			.withMinCount(1)
			.withMaxCount(1)
			.withKeyName("CICD-key-pair") // optional - if not present, can't connect to instance
			.withSecurityGroups("CICDSecurityGroup");

		String yourInstanceId = ec2Client.runInstances(runInstancesRequest).getReservation().getInstances().get(0).getInstanceId();

		// Start an Instance
		StartInstancesRequest startInstancesRequest = new StartInstancesRequest()
			.withInstanceIds(yourInstanceId);

		ec2Client.startInstances(startInstancesRequest);

		// Monitor Instances
		MonitorInstancesRequest monitorInstancesRequest = new MonitorInstancesRequest()
			.withInstanceIds(yourInstanceId);

		ec2Client.monitorInstances(monitorInstancesRequest);

		UnmonitorInstancesRequest unmonitorInstancesRequest = new UnmonitorInstancesRequest()
			.withInstanceIds(yourInstanceId);

		ec2Client.unmonitorInstances(unmonitorInstancesRequest);

	}
}