package mwatch.lambda;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

public class PublishToSNS {

	public static void main(String args[]) {
		// create a new SNS client and set endpoint
		AmazonSNSClient snsClient = new AmazonSNSClient(
				new ClasspathPropertiesFileCredentialsProvider());
		snsClient.setRegion(Region.getRegion(Regions.US_WEST_2));
		String topicArn = "arn:aws:sns:us-west-2:684949458090:lambdatest";

		String message = "Test Message to AWS SNS lamdatest 6";
		String subject = "Sub - Test Message to AWS SNS lamdatest 6";

		PublishRequest publishRequest = new PublishRequest(topicArn, message,
				subject);

		PublishResult publishResult = snsClient.publish(publishRequest);
		// print MessageId of message published to SNS topic
		System.out.println("MessageId - " + publishResult.getMessageId());
	}

}
