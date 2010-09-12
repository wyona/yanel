package org.wyona.yanel.core.util;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

/**
 * SMTP mail utility class
 */
public class MailUtil {
    private static Logger log = Logger.getLogger(MailUtil.class);

    public static final String STATUS = "status";
    public static final String MESSAGE = "message";

    /**
     * @param from From address
     * @param to To address
     * @param subject Subject of email
     * @param content Body of email
     */
    public static void send(String from, String to, String subject, String content) throws AddressException, MessagingException {
        send(null, -1, from, null, to, subject, content);
    }

    /**
     * @param from From address
     * @param replyTo email address (if null, then no reply-to will be set)
     * @param to To address
     * @param subject Subject of email
     * @param content Body of email
     */
    public static void send(String from, String replyTo, String to, String subject, String content) throws AddressException, MessagingException {
        send(null, -1, from, replyTo, to, subject, content);
    }

    /**
     * Send an email without a reply-to address
     */
    public static void send(String smtpHost, int smtpPort, String from, String to, String subject, String content) throws AddressException, MessagingException {
        String replyTo = null;
        send(smtpHost, smtpPort, from, replyTo, to, subject, content);
    }

    /**
     * Send e-mail with a MIME type of "text/plain" and as encoding the platform's default charset
     * @param replyTo email address (if null, then no reply-to will be set)
     */
    public static void send(String smtpHost, int smtpPort, String from, String replyTo, String to, String subject, String content) throws AddressException, MessagingException {
        send(smtpHost, smtpPort, from, replyTo, to, subject, content, java.nio.charset.Charset.defaultCharset().name(), "plain");
    }

    /**
     * @param replyTo email address (if null, then no reply-to will be set)
     * @param charset Charset, e.g. utf-8
     * @param mimeSubType Mime sub-type, e.g. "html" or "plain"
     */
    public static void send(String smtpHost, int smtpPort, String from, String replyTo, String to, String subject, String content, String charset, String mimeSubType) throws AddressException, MessagingException {
        // Create a mail session
        Session session = null;
        if (smtpHost != null && smtpPort >= 0) {
            java.util.Properties props = new java.util.Properties();
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", "" + smtpPort);
            session = Session.getInstance(props, null);
            log.warn("Use specific mail session: " + session.getProperty("mail.smtp.host") + ":" + session.getProperty("mail.smtp.port"));
        } else { // INFO: Use the global configuration (default instance) of Yanel
            java.util.Properties props = new java.util.Properties();
            props.put("mail.smtp.host", "mail.foo.bar"); // Dummy value
            props.put("mail.smtp.port", "37"); // Dummy value
            boolean smtpAuthEnabled = false;
            try {
                smtpAuthEnabled = org.wyona.yanel.core.Yanel.getInstance().isSMTPAuthSet();
            } catch (Exception e) {
                log.error(e, e);
            } 
            if (smtpAuthEnabled) {
                log.info("SMTP authentication enabled.");
                session = Session.getDefaultInstance(props, new DummyAuthenticator()); // INFO: The dummy values will be ignored, because Yanel (org.wyona.yanel.core.Yanel) sets during initialization the default session!
            } else {
                session = Session.getDefaultInstance(props, null); // INFO: The dummy values will be ignored, because Yanel (org.wyona.yanel.core.Yanel) sets during initialization the default session!
            }
            log.info("Use default mail session: " + session.getProperty("mail.smtp.host") + ":" + session.getProperty("mail.smtp.port"));
        }

        // Construct the message
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        if (replyTo != null) {
            InternetAddress[] replyToAddresses = new InternetAddress[1];
            replyToAddresses[0] = new InternetAddress(replyTo);
            msg.setReplyTo(replyToAddresses);
        }
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        msg.setSubject(subject);
        msg.setText(content, charset, mimeSubType);

        // Send the message
        Transport.send(msg);
    }
}

/**
 * Dummy authenticator used for SMTP
 */
class DummyAuthenticator extends javax.mail.Authenticator {

    private String username, password;

    /**
     *
     */
    public DummyAuthenticator() {
        this.username = "foo";
        this.password = "bar";
    }

    /**
     * @see javax.mail.Authenticator#getPasswordAuthentication()
     */
    protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
        return new javax.mail.PasswordAuthentication(this.username, this.password);
    }
}
