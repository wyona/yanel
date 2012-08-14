package org.wyona.yanel.impl.resources.contactform;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.log4j.Logger;

/**
 * @deprecated Use org.wyona.yanel.core.util.MailUtil instead
 */
public class SendMail {
    private static Logger log = Logger.getLogger(SendMail.class);

    /**
     * @deprecated Use org.wyona.yanel.core.util.MailUtil instead
     */
    public static void send(String smtpHost, int smtpPort, String from, String to, String subject, String content) throws AddressException, MessagingException {
        log.warn("DEPRECATED");
        org.wyona.yanel.core.util.MailUtil.send(smtpHost, smtpPort, from, to, subject, content);
    }
}
