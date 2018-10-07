package org.wyona.yanel.core.util;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeBodyPart;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import org.bouncycastle.mail.smime.SMIMEEnvelopedGenerator;

import java.security.cert.X509Certificate;

/**
 * SMTP mail utility class
 */
public class MailUtil {
    private static Logger log = LogManager.getLogger(MailUtil.class);

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
     * @param from From address
     * @param replyTo email address (if null, then no reply-to will be set)
     * @param recipient Recipient (to which email will be sent)
     * @param subject Subject of email
     * @param content Body of email
     */
    public static void send(String from, String replyTo, Recipient recipient, String subject, String content) throws AddressException, MessagingException {
        if (recipient.getPGPPublicKey() != null) {
            try {
                // INFO: See http://fastpicket.com/blog/2012/05/14/easy-pgp-in-java-bouncy-castle/
                //log.debug("Encrypt email with PGP public key: " + recipient.getPGPPublicKey());
                // INFO: Add the bouncy castle security provider or have it installed in $JAVA_HOME/jre/lib/ext
                java.security.Security.addProvider(new BouncyCastleProvider());

                PGPPublicKeyRing keyRing = getKeyring(new java.io.ByteArrayInputStream(recipient.getPGPPublicKey().getBytes()));
                PGPPublicKey publicKey = getEncryptionKey(keyRing);
                //log.debug("Public Key: " + publicKey);
                //log.debug("ID: " + publicKey.getKeyID());
                // make an output stream connected to a file this also works with output streams in servlets
                File tmpFile = new File(System.getProperty("java.io.tmpdir"), "/openpgp-encrypted-email-" + new java.util.Date().getTime() + ".asc");
                java.io.FileOutputStream fileOutputStream = new java.io.FileOutputStream(tmpFile);
                // make one of our encryption utilities
                PGPEncryptionUtil util = new PGPEncryptionUtil(publicKey, "secrets.txt", fileOutputStream);
                // finally write something
                java.io.PrintWriter pw = new java.io.PrintWriter(util.getPayloadOutputStream());
                pw.println(content);
                // flush the stream and close up everything
                pw.flush();
                util.close();
                send(null, -1, from, replyTo, recipient.getEmail(), subject, org.apache.commons.io.IOUtils.toString(new java.io.FileInputStream(tmpFile)));
                tmpFile.delete();
            } catch(Exception e) {
                log.error(e, e);
                throw new MessagingException(e.getMessage());
            }
        } else if (recipient.getSMIMECertificate() != null) {
            try {
                log.warn("DEBUG: Encrypt email with S/MIME certificate: " + recipient.getSMIMECertificate());
                SMIMEEnvelopedGenerator  gen = new SMIMEEnvelopedGenerator();
                X509Certificate certX509 = convertPEMtoX509(recipient.getSMIMECertificate());
                gen.addKeyTransRecipient(certX509);
                MimeBodyPart msg = new MimeBodyPart();
                msg.setText(content);
                java.security.Security.addProvider(new BouncyCastleProvider());
                MimeBodyPart mp = gen.generate(msg, SMIMEEnvelopedGenerator.RC2_CBC, new BouncyCastleProvider());
                sendForGood(null, -1, from, null, replyTo, recipient.getEmail(), subject, mp, null, null, null);
            } catch(Exception e) {
                log.error(e, e);
                throw new MessagingException(e.getMessage());
            }
        } else {
            log.warn("DEBUG: E-Mail will not be encrypted.");
            send(null, -1, from, replyTo, recipient.getEmail(), subject, content);
        }
    }

    /**
     *
     */
    private static X509Certificate convertPEMtoX509(String pem) throws Exception {
        byte [] decoded = new org.apache.commons.codec.binary.Base64().decode(pem.replaceAll("-----BEGIN CERTIFICATE-----", "").replaceAll("-----END CERTIFICATE-----", ""));
        return (X509Certificate)java.security.cert.CertificateFactory.getInstance("X.509").generateCertificate(new java.io.ByteArrayInputStream(decoded));
    }

/**
 * Decode a PGP public key block and return the keyring it represents.
 * @param keyBlockStream Piblic key block as stream
 * @return TODO
 */
private static PGPPublicKeyRing getKeyring(InputStream keyBlockStream) throws IOException {
    // PGPUtil.getDecoderStream() will detect ASCII-armor automatically and decode it,
    // the PGPObject factory then knows how to read all the data in the encoded stream
    PGPObjectFactory factory = new PGPObjectFactory(PGPUtil.getDecoderStream(keyBlockStream));
 
    // these files should really just have one object in them,
    // and that object should be a PGPPublicKeyRing.
    Object o = factory.nextObject();
    if (o instanceof PGPPublicKeyRing) {
        return (PGPPublicKeyRing)o;
    }
    throw new IllegalArgumentException("Input text does not contain a PGP Public Key");
}

/**
 * Get the first encyption key off the given keyring.
 */
private static PGPPublicKey getEncryptionKey(PGPPublicKeyRing keyRing) {
    if (keyRing == null)
        return null;
 
    // iterate over the keys on the ring, look for one
    // which is suitable for encryption.
    Iterator keys = keyRing.getPublicKeys();
    PGPPublicKey key = null;
    while (keys.hasNext()) {
        key = (PGPPublicKey)keys.next();
        if (key.isEncryptionKey()) {
            return key;
        }
    }
    return null;
}

    /**
     * @param fromAddress From address, e.g. contact@wyona.org
     * @param fromName From name, e.g. Wyona
     * @param replyTo email address (if null, then no reply-to will be set)
     * @param to To address
     * @param subject Subject of email
     * @param content Body of email
     */
    public static void send(String fromAddress, String fromName, String replyTo, String to, String subject, String content) throws AddressException, MessagingException {
        send(null, -1, fromAddress, fromName, replyTo, to, subject, content, java.nio.charset.Charset.defaultCharset().name(), "plain");
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
     * @deprecated Because one cannot set a "from name". Use {@link #send(String, int, String, String, String, String, String, String, String, String)} instead.
     */
    public static void send(String smtpHost, int smtpPort, String from, String replyTo, String to, String subject, String content, String charset, String mimeSubType) throws AddressException, MessagingException {
        String fromName = null;
        send(smtpHost, smtpPort, from, fromName, replyTo, to, subject, content, charset, mimeSubType);
    }

    /**
     * Send email
     * @param smtpHost Alternative SMTP mail server host name, whereas if set to null, then the default/global configuration of Yanel will be used
     * @param smtpPort Alternative SMTP mail server port, whereas if set to smaller than 0, then the default/global configuration of Yanel will be used
     * @param fromEmailAddress E-Mail address of sender, e.g. contact@wyona.org
     * @param fromName Name of sender, e.g. Wyona
     * @param replyTo email address (if null, then no reply-to will be set)
     * @param to To address
     * @param subject Subject of email
     * @param content Body of email
     * @param charset Charset, e.g. utf-8
     * @param mimeSubType Mime sub-type, e.g. "html" or "plain"
     */
    public static void send(String smtpHost, int smtpPort, String fromEmailAddress, String fromName, String replyTo, String to, String subject, String content, String charset, String mimeSubType) throws AddressException, MessagingException {
        sendForGood(smtpHost, smtpPort, fromEmailAddress, fromName, replyTo, to, subject, null, content, charset, mimeSubType);
    }

    /**
     * Send email
     * @param smtpHost Alternative SMTP mail server host name, whereas if set to null, then the default/global configuration of Yanel will be used
     * @param smtpPort Alternative SMTP mail server port, whereas if set to smaller than 0, then the default/global configuration of Yanel will be used
     * @param fromEmailAddress E-Mail address of sender, e.g. contact@wyona.org
     * @param fromName Name of sender, e.g. Wyona
     * @param replyTo email address (if null, then no reply-to will be set)
     * @param to To address
     * @param subject Subject of email
     * @param mimeBodyPart When using S/MIME, then thhe mime body part is being encrypted instead just the content itself
     * @param content Body of email
     * @param charset Charset, e.g. utf-8
     * @param mimeSubType Mime sub-type, e.g. "html" or "plain"
     */
    private static void sendForGood(String smtpHost, int smtpPort, String fromEmailAddress, String fromName, String replyTo, String to, String subject, MimeBodyPart mimeBodyPart, String content, String charset, String mimeSubType) throws AddressException, MessagingException {
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
        if (fromEmailAddress != null) {
            if (fromName != null && fromName. length() > 0) {
                try {
                    msg.setFrom(new InternetAddress(fromEmailAddress, fromName));
                } catch (java.io.UnsupportedEncodingException e) {
                    log.error(e, e);
                    msg.setFrom(new InternetAddress(fromEmailAddress));
                }
            } else {
                msg.setFrom(new InternetAddress(fromEmailAddress));
            }
        } else {
            String errorMsg = "No 'from' email address configured! Check global yanel configuration http://www.yanel.org/en/documentation/configuration/yanel_xml.html (Element attribute 'administrator/@email') or custom realm resource configuration.";
            log.error(errorMsg);
            //msg.setFrom(new InternetAddress("no-reply@yanel.org"));
        }
        if (replyTo != null) {
            InternetAddress[] replyToAddresses = new InternetAddress[1];
            replyToAddresses[0] = new InternetAddress(replyTo);
            msg.setReplyTo(replyToAddresses);
        }
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        msg.setSubject(subject);
        if (mimeBodyPart != null) {
            try {
                msg.setContent(mimeBodyPart.getContent(), mimeBodyPart.getContentType());
            } catch(Exception e) {
                log.error(e, e);
                throw new MessagingException(e.getMessage());
            }
        } else {
            msg.setText(content, charset, mimeSubType);
        }

        // INFO: Send the message
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
