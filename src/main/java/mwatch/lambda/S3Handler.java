package mwatch.lambda;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.ObjectMapper;

public class S3Handler {
	// Handle S3 Event
	public String handleRequest(S3Event s3event, Context context) {
		LambdaLogger logger = context.getLogger();
		try {
			for (S3EventNotificationRecord record : s3event.getRecords()) {
				String srcKey = record.getS3().getObject().getKey();
				String srcBucket = record.getS3().getBucket().getName();
				Matcher matcher = Pattern.compile(".*\\.([^\\.]*)").matcher(
						srcKey);
				if (!matcher.matches()) {
					logger.log("Unable to infer file type for key " + srcKey);
					return "";
				}
				String fileType = matcher.group(1);
				if (fileType.equals("json")) {
					ObjectMapper mapper = new ObjectMapper();
					AmazonS3 s3Client = new AmazonS3Client();
					S3Object s3Object = s3Client
							.getObject(new GetObjectRequest(srcBucket, srcKey));
					InputStream objectData = s3Object.getObjectContent();
					BufferedReader buffer = new BufferedReader(
							new InputStreamReader(objectData));
					String jsonString = null;
					while ((jsonString = buffer.readLine()) != null) {
						HttpLogData httpData = mapper.readValue(jsonString,
								HttpLogData.class);
						if(httpData.getCode()!= 200)
						{
						String subject = "EC2 Http Error - " + httpData.getCode()
								+ " error while accessing path - "
								+ httpData.getPath();
						String message = httpData.toString();
						EmailNotification.sendEmail(subject, message, context);
						logger.log(message);
						}
					}

				}
			}
		} catch (Exception e) {
			logger.log(e.toString());
		}
		return "handleS3Event completed";
	}

}
