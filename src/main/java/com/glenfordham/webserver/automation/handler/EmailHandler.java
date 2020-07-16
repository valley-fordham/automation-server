package com.glenfordham.webserver.automation.handler;

import com.glenfordham.webserver.automation.config.AutomationConfigException;
import com.glenfordham.webserver.automation.jaxb.EmailHeader;
import com.glenfordham.webserver.automation.jaxb.EmailRequest;
import com.glenfordham.webserver.automation.jaxb.Mailbox;
import com.glenfordham.webserver.logging.Log;
import com.glenfordham.webserver.automation.config.AutomationConfig;
import com.glenfordham.webserver.automation.Parameter;
import com.glenfordham.webserver.automation.jaxb.Config;
import com.glenfordham.webserver.logging.LogLevel;
import com.glenfordham.webserver.servlet.parameter.ParameterException;
import com.glenfordham.webserver.servlet.parameter.ParameterMap;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.OutputStream;
import java.util.Properties;

public class EmailHandler implements Handler {
    /**
     * Processes an Email type request. Matches request against configuration XML and triggers email action
     * on the mailbox configured against the request.
     *
     * @param parameterMap complete ParameterMap object, containing both parameter keys and values
     * @param clientOutput client OutputStream, for writing a response
     * @throws AutomationConfigException if unable to load configuration file
     * @throws HandlerException a generic Exception occurs when handling the request
     * @throws ParameterException if unable to get request name from parameter
     */
    @Override
    public void start(ParameterMap parameterMap, OutputStream clientOutput) throws AutomationConfigException, HandlerException, ParameterException {
        String incomingRequestName = parameterMap.get(Parameter.REQUEST_NAME.get()).getFirst();

        // Load configuration file on every attempt to ensure server does not need restarting when modifying config
        Config config = AutomationConfig.load();

        // Check if the incoming request matches a configured request name
        EmailRequest request = config.getEmail().getRequests().stream()
                .filter(requestEntry -> incomingRequestName.equalsIgnoreCase(requestEntry.getName()))
                .findFirst()
                .orElse(null);

        if (request == null) {
            Log.error("Invalid request name: " + incomingRequestName);
            return;
        }

        // Check that the device associated with the validMailboxList name is configured
        Mailbox mailbox = config.getEmail().getMailboxes().stream()
                .filter(mailboxEntry -> request.getMailboxName().equalsIgnoreCase(mailboxEntry.getName()))
                .findFirst()
                .orElse(null);

        if (mailbox == null) {
            Log.error("Mailbox name not configured: " + request.getMailboxName());
            return;
        }
        sendEmail(mailbox, request);
    }

    private void sendEmail(Mailbox mailbox, EmailRequest request) throws HandlerException {
        // Get system properties
        Properties properties = System.getProperties();

        try {
            // Setup mail server
            properties.put(Constant.MAIL_HOST.getText(), mailbox.getHost());
            properties.put(Constant.MAIL_PORT.getText(), mailbox.getPort());
            properties.put(Constant.MAIL_SSL.getText(), mailbox.isTls() ? "true" : "false");
            properties.put(Constant.MAIL_AUTH.getText(), (mailbox.isAuthenticate() ? "true" : "false"));

            // Setup the email session, including an authenticator (not used if authentication is off)
            Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mailbox.getUsername(), mailbox.getPassword());
                }
            });

            // If log level is debug then also print email debug lines
            if (Log.getLogLevel().equalsIgnoreCase(LogLevel.DEBUG.get())) {
                session.setDebug(true);
            }
            try {
                MimeMessage message = new MimeMessage(session);

                message.setFrom(new InternetAddress(request.getFrom()));

                for (String toEntry : request.getTo()) {
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEntry));
                }

                message.setSubject(request.getSubject());

                if (request.isHtml()) {
                    message.setContent(request.getMessage(), "text/html");
                } else {
                    message.setText(request.getMessage());
                }

                for (EmailHeader header : request.getHeaders()) {
                    message.setHeader(header.getName(), header.getText());
                }

                Log.debug("Attempting to send message...");
                Transport.send(message);
                Log.info("Email sent");
            } catch (Exception mE) {
                throw new HandlerException(mE.getMessage(), mE);
            }
        } finally {
            // Reset the system properties`
            properties.remove(Constant.MAIL_HOST.getText());
            properties.remove(Constant.MAIL_PORT.getText());
            properties.remove(Constant.MAIL_SSL.getText());
            properties.remove(Constant.MAIL_AUTH.getText());
        }
    }
}
