package com.glenfordham.webserver.automation.handler;

import com.glenfordham.webserver.Log;
import com.glenfordham.webserver.automation.AutomationConfig;
import com.glenfordham.webserver.automation.Parameter;
import com.glenfordham.webserver.automation.jaxb.Config;
import com.glenfordham.webserver.servlet.parameter.ParameterException;
import com.glenfordham.webserver.servlet.parameter.ParameterMap;
import org.xml.sax.SAXException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

public class EmailHandler implements Handler {
    /**
     * Processes an Email type request. Matches request against configuration XML and triggers email action
     * on the mailbox configured against the request.
     *
     * @param parameterMap complete ParameterMap object, containing both parameter keys and values
     * @param clientOutput client OutputStream, for writing a response
     * @throws JAXBException      if unable to load configuration file
     * @throws ParameterException if unable to get request name from parameter
     */
    @Override
    public void start(ParameterMap parameterMap, OutputStream clientOutput) throws HandlerException, JAXBException, ParameterException, IOException, SAXException {
        String incomingRequestName = parameterMap.get(Parameter.REQUEST_NAME.get()).getFirst();

        // Load configuration file on every attempt to ensure server does not need restarting when modifying config
        Config config = AutomationConfig.load();

        // Get all Device and Request elements, then attempt to process the request
        List<Config.Email.Mailboxes.Mailbox> validMailboxList = config.getEmail().getMailboxes().getMailbox();
        List<Config.Email.Requests.Request> validRequestList = config.getEmail().getRequests().getRequest();

        // Check if the incoming request matches a configured request name
        Config.Email.Requests.Request request = validRequestList.stream()
                .filter(requestEntry -> incomingRequestName.equalsIgnoreCase(requestEntry.getName()))
                .findFirst()
                .orElse(null);

        if (request == null) {
            Log.error("Invalid request name: " + incomingRequestName);
            return;
        }

        // Check that the device associated with the validMailboxList name is configured
        Config.Email.Mailboxes.Mailbox mailbox = validMailboxList.stream()
                .filter(mailboxEntry -> request.getMailboxName().equalsIgnoreCase(mailboxEntry.getName()))
                .findFirst()
                .orElse(null);

        if (mailbox == null) {
            Log.error("Mailbox name not configured: " + request.getMailboxName());
            return;
        }
        sendEmail(mailbox, request);
    }

    private void sendEmail(Config.Email.Mailboxes.Mailbox mailbox, Config.Email.Requests.Request request) throws HandlerException {
        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        try {
            properties.put("mail.smtp.host", mailbox.getHost());
            properties.put("mail.smtp.port", mailbox.getPort());
            properties.put("mail.smtp.ssl.enable", mailbox.isTls().toString());
            properties.put("mail.smtp.auth", mailbox.isAuthenticate().toString());

            //TODO: handle no login
            // Get the Session object.// and pass username and password
            Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mailbox.getUsername(), mailbox.getPassword());
                }
            });
            // Used to debug SMTP issues
            session.setDebug(true);
            try {
                // Create a default MimeMessage object.
                MimeMessage message = new MimeMessage(session);
                // Set From: header field of the header.
                message.setFrom(new InternetAddress(request.getFrom()));
                // Set To: header field of the header.
                // TODO: handle multiple recipients in xsd and here
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(request.getTo()));
                // Set Subject: header field
                message.setSubject(request.getSubject());
                // Now set the actual message
                if (request.isHtml()) {
                    message.setContent(request.getMessage(), "text/html");
                } else {
                    message.setText(request.getMessage());
                }
                Log.debug("Attempting to send message...");
                Transport.send(message);
                Log.info("Email sent");
            } catch (MessagingException mE) {
                throw new HandlerException(mE.getMessage(), mE);
            }
        } finally {
            // Reset the system properties`
            properties.remove("mail.smtp.host");
            properties.remove("mail.smtp.port");
            properties.remove("mail.smtp.ssl.enable");
            properties.remove("mail.smtp.auth");
        }
    }
}
