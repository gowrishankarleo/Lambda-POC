package mwatch.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KinesisHandler {
	public String handleRequest(KinesisEvent event, Context context) {
		LambdaLogger logger = context.getLogger();
		logger.log("Received " + event.getRecords().size()
				+ " raw Event Records.");

		// Stream the User Records from the Lambda Event
		ObjectMapper mapper = new ObjectMapper();

		KinesisRecordDeaggregator.stream(event.getRecords().stream(),
				userRecord -> {
					// Your User Record Processing Code Here!
				String jsonString = new String(userRecord.getData().array());

				try {
					HttpLogData httpData = mapper.readValue(jsonString,
							HttpLogData.class);
					String subject = "EC2 Http Error - " + httpData.getCode()
							+ " error while accessing path - "
							+ httpData.getPath();
					String message = httpData.toString();
					EmailNotification.sendEmail(subject, message, context);
					logger.log(message);
				} catch (Exception e) {
					logger.log(e.toString());
				}

				logger.log(new String(userRecord.getData().array()));
			});

		return "handleKinesisEvent completed";

	}

}
