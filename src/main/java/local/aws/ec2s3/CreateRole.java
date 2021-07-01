/**
 * To run this Java code, ensure that you have setup your development environment, including your credentials.
 *
 * Region: AP_SOUTHEAST_2
 * Windows: C:\Users\<yourUserName>\.aws\credentials
 * Linux, macOS, Unix: ~/.aws/credentials
 *
 */
package local.aws.ec2s3;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import software.amazon.awssdk.services.iam.model.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;

import java.io.FileReader;

public class CreateRole {

	public static void main(String[] args) {

		final String USAGE = "\n" +
			"Usage:\n" +
			"    CreateRole <rolename> <fileLocation> \n\n" +
			"Where:\n" +
			"    rolename - the name of the role to create. \n\n" +
			"    fileLocation - the location of the JSON document that represents the trust policy. \n\n" ;

//		if (args.length != 2) {
//			System.out.println(USAGE);
//			System.exit(1);
//		}
		/**/

		String rolename = "xeroReadWrite";
		String fileLocation = "C:\\Users\\MF\\OneDrive\\security_testing\\Maven_demo\\AWS\\src\\main\\java\\local\\aws\\ec2s3\\roleReadWrite.json";
		Region region = Region.AWS_GLOBAL;
		IamClient iam = IamClient.builder()
			.region(region)
			.build();

		String result = createIAMRole(iam, rolename, fileLocation) ;
		System.out.println("Successfully created user: " +result);
		iam.close();
	}

	public static String createIAMRole(IamClient iam, String rolename, String fileLocation ) {

		try {

			JSONObject jsonObject = (JSONObject) readJsonSimpleDemo(fileLocation);

			CreateRoleRequest request = CreateRoleRequest.builder()
				.roleName(rolename)
				.assumeRolePolicyDocument(jsonObject.toJSONString())
				.description("Created using the AWS SDK for Java")
				.build();

			CreateRoleResponse response = iam.createRole(request);
			System.out.println("The ARN of the role is "+response.role().arn());

		} catch (IamException e) {
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static Object readJsonSimpleDemo(String filename) throws Exception {
		FileReader reader = new FileReader(filename);
		JSONParser jsonParser = new JSONParser();
		return jsonParser.parse(reader);
	}
}