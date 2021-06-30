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
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.CreateSecurityGroupRequest;
import software.amazon.awssdk.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import software.amazon.awssdk.services.ec2.model.AuthorizeSecurityGroupIngressResponse;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.awssdk.services.ec2.model.IpPermission;
import software.amazon.awssdk.services.ec2.model.CreateSecurityGroupResponse;
import software.amazon.awssdk.services.ec2.model.IpRange;

public class CreateSecurityGroup {

	public static void main(String[] args) {

		final String USAGE = "\n" +
			"Usage:\n" +
			"CreateSecurityGroup <groupName> <groupDesc> <vpcId> \n\n" +
			"Where:\n" +
			"    groupName - a group name (for example, TestKeyPair). \n\n"  +
			"    groupDesc - a group description  (for example, TestKeyPair). \n\n"  +
			"    vpc-id - a VPC ID that you can obtain from the AWS Management Console (for example, vpc-xxxxxf2f). \n\n"  ;

		if (args.length != 3) {
			System.out.println(USAGE);
			System.exit(1);
		}

		String groupName = args[0];
		String groupDesc = args[1];
		String vpcId = args[2];

		// snippet-start:[ec2.java2.create_security_group.client]
		Region region = Region.AP_SOUTHEAST_2;
		Ec2Client ec2 = Ec2Client.builder()
			.region(region)
			.build();
		// snippet-end:[ec2.java2.create_security_group.client]

		String id = createEC2SecurityGroup(ec2, groupName, groupDesc, vpcId);
		System.out.printf(
			"Successfully created Security Group with this ID %s",
			id);
		ec2.close();
	}

	// snippet-start:[ec2.java2.create_security_group.main]
	public static String createEC2SecurityGroup( Ec2Client ec2,String groupName, String groupDesc, String vpcId) {
		try {

			// snippet-start:[ec2.java2.create_security_group.create]
			CreateSecurityGroupRequest createRequest = CreateSecurityGroupRequest.builder()
				.groupName(groupName)
				.description(groupDesc)
				.vpcId(vpcId)
				.build();

			CreateSecurityGroupResponse resp= ec2.createSecurityGroup(createRequest);
			// snippet-end:[ec2.java2.create_security_group.create]

			// snippet-start:[ec2.java2.create_security_group.config]
			IpRange ipRange = IpRange.builder()
				.cidrIp("0.0.0.0/0").build();

/*			IpPermission ipPerm = IpPermission.builder()
				.ipProtocol("tcp")
				.toPort(80)
				.fromPort(80)
				.ipRanges(ipRange)
				.build();*/

			IpPermission ipPerm2 = IpPermission.builder()
				.ipProtocol("tcp")
				.toPort(22)
				.fromPort(22)
				.ipRanges(ipRange)
				.build();

			AuthorizeSecurityGroupIngressRequest authRequest =
				AuthorizeSecurityGroupIngressRequest.builder()
					.groupName(groupName)
					.ipPermissions(ipPerm2)
					.build();

			AuthorizeSecurityGroupIngressResponse authResponse =
				ec2.authorizeSecurityGroupIngress(authRequest);
			// snippet-end:[ec2.java2.create_security_group.config]

			// snippet-end:[ec2.java2.create_security_group.main]
			System.out.printf(
				"Successfully added ingress policy to Security Group %s",
				groupName);

			return resp.groupId();

		} catch (Ec2Exception e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}
		return "";
	}
}