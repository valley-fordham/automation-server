package com.glenfordham.webserver.automation.handler.email;

import com.glenfordham.webserver.automation.Parameter;
import com.glenfordham.webserver.automation.config.AutomationConfig;
import com.glenfordham.webserver.automation.config.AutomationConfigException;
import com.glenfordham.webserver.automation.handler.Handler;
import com.glenfordham.webserver.automation.handler.HandlerException;
import com.glenfordham.webserver.automation.jaxb.Config;
import com.glenfordham.webserver.automation.jaxb.EmailHeader;
import com.glenfordham.webserver.automation.jaxb.EmailRequest;
import com.glenfordham.webserver.automation.jaxb.Mailbox;
import com.glenfordham.webserver.logging.Log;
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
     * @throws AutomationConfigException if unable to get configuration
     * @throws HandlerException          if a generic Exception occurs when handling the request
     * @throws ParameterException        if unable to get request name from parameter
     */
    @Override
    public void start(ParameterMap parameterMap, OutputStream clientOutput) throws AutomationConfigException, HandlerException, ParameterException {
        String incomingRequestName = parameterMap.get(Parameter.REQUEST_NAME.get()).getFirst();
        Config config = AutomationConfig.get();

        // Ensure Email element is present in config file
        if (config.getEmail() == null) {
            throw new HandlerException("No Email configuration in configuration XML");
        }

        // Check if the incoming request matches a configured request name
        EmailRequest request = config.getEmail().getRequests().stream()
                .filter(requestEntry -> incomingRequestName.equalsIgnoreCase(requestEntry.getName()))
                .findFirst()
                .orElse(null);

        if (request == null) {
            Log.error("Invalid request name");
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

    /**
     * Unpacks the email request and sends the email to the linked 'Mailbox' host
     * TODO: make thread-safe with something _better than_ synchronized
     *
     * @param mailbox the receiver of the message
     * @param request the email request to unpack and turn into a message to be sent
     * @throws HandlerException if an unexpected error occurs when handling the request
     */
    private synchronized void sendEmail(Mailbox mailbox, EmailRequest request) throws HandlerException {
        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put(Constant.MAIL_HOST.get(), mailbox.getHost());
        properties.put(Constant.MAIL_PORT.get(), mailbox.getPort());
        properties.put(Constant.MAIL_TLS.get(), mailbox.isTls() ? Boolean.TRUE.toString() : Boolean.FALSE.toString());
        properties.put(Constant.MAIL_SSL.get(), !mailbox.isTls() ? Boolean.TRUE.toString() : Boolean.FALSE.toString());
        properties.put(Constant.MAIL_AUTH.get(), (mailbox.isAuthenticate() ? Boolean.TRUE.toString() : Boolean.FALSE.toString()));

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
    }
}
