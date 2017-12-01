package mwatch.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;

public class SNSHandler {
	public String myHandler(SNSEvent event, Context context) {
		LambdaLogger logger = context.getLogger();
		for (SNSEvent.SNSRecord record : event.getRecords()) {
			SNSEvent.SNS sns = record.getSNS();
			logger.log("handleSNSEvent received SNS message "
					+ sns.getMessage());
			try {
				EmailNotification.sendEmail(sns.getSubject(), sns.getMessage(),
						context);
			} catch (Exception e) {
				logger.log(e.toString());
			}
		}
		return "handleSNSEvent completed";
	}
}
