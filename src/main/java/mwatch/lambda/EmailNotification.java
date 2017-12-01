package mwatch.lambda;

import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.Context;

public class EmailNotification {

	// Email from to
	static final String FROM = System.getenv("FROM_EMAIL");
	static final String TO = System.getenv("TO_EMAIL");

	// SMTP credentials
	static final String SMTP_USERNAME = System.getenv("SMTP_USER");
	static final String SMTP_PASSWORD = System.getenv("SMTP_PWD");

	// Amazon SES SMTP host name
	static final String HOST = System.getenv("SMTP_HOST"); // email-smtp.us-west-2.amazonaws.com

	// Amazon SMTP Port
	static final int PORT = Integer.parseInt(System.getenv("SMTP_PORT"));
	
	//Message Prefix
	static final String BODY_PREFIX = System.getenv("BODY_PREFIX");

	//Subject Prefix
	static final String SUBJECT_PREFIX = System.getenv("SUBJECT_PREFIX");

	public static void main(String[] args) throws Exception {
	}

	public static void sendEmail(String subject, String body, Context context)
			throws Exception {

		LambdaLogger logger = context.getLogger();

		// configuration information.
		Properties props = System.getProperties();
		props.put("mail.transport.protocol", "smtps");
		props.put("mail.smtp.port", PORT);

		// TLS configuration
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.starttls.required", "true");

		// Create Session
		Session session = Session.getDefaultInstance(props);

		// Create message.
		MimeMessage msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(FROM));
		msg.setRecipient(Message.RecipientType.TO, new InternetAddress(TO));
		msg.setSubject(SUBJECT_PREFIX + subject);
		msg.setContent(BODY_PREFIX + body, "text/plain");

		// Create transport.
		Transport transport = session.getTransport();

		// Send the message.
		try {
			logger.log("Attempting to send an email through the Amazon SES SMTP interface...");

			transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);
			// Send message
			transport.sendMessage(msg, msg.getAllRecipients());
			logger.log("Email sent!");
		} catch (Exception ex) {
			logger.log("The email was not sent.");
			logger.log("Error message: " + ex.getMessage());
		} finally {
			// Close and terminate the connection.
			transport.close();
		}
	}
}