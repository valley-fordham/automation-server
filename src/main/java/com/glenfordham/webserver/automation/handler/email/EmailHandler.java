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
import com.glenfordham.webserver.servlet.parameter.ParameterException;
import com.glenfordham.webserver.servlet.parameter.ParameterMap;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.OutputStream;
import java.util.Properties;

/**
 * Email handler is used for processing requests of the email request type.
 */
public class EmailHandler implements Handler {

    private static final Logger logger = LogManager.getLogger();

    /**
     * Processes an Email type request. Matches request against configuration XML and triggers email action
     * on the mailbox configured against the request.
     *
     * @param parameterMap Complete ParameterMap object, containing both parameter keys and values.
     * @param clientOutput Client OutputStream, for writing a response.
     * @throws AutomationConfigException If unable to get configuration.
     * @throws HandlerException          If a generic Exception occurs when handling the request.
     * @throws ParameterException        If unable to get request name from parameter.
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
            logger.error("Invalid request name");
            return;
        }

        // Check that the device associated with the validMailboxList name is configured
        Mailbox mailbox = config.getEmail().getMailboxes().stream()
                .filter(mailboxEntry -> request.getMailboxName().equalsIgnoreCase(mailboxEntry.getName()))
                .findFirst()
                .orElse(null);

        if (mailbox == null) {
            logger.error("Mailbox name not configured: {}", request.getMailboxName());
            return;
        }
        sendEmail(mailbox, request);
    }

    /**
     * Unpacks the email request and sends the email to the linked 'Mailbox' host.
     *
     * @param mailbox Mail server to send the email with.
     * @param request Email request to unpack and turn into an email to be sent.
     * @throws HandlerException If an unexpected error occurs when handling the request.
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
        Session session = Session.getInstance(properties, new jakarta.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailbox.getUsername(), mailbox.getPassword());
            }
        });

        // If log level is debug then also print email debug lines
        session.setDebug(logger.isDebugEnabled());

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

            logger.debug("Attempting to send message...");
            Transport.send(message);
            logger.info("Email sent");
        } catch (Exception mE) {
            throw new HandlerException(mE.getMessage(), mE);
        }
    }
}
