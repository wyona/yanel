/*-
 * Copyright 2012 Wyona
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.wyona.org/licenses/APACHE-LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wyona.yanel.impl.resources.contactform;

import java.io.File;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.mail.MessagingException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import org.apache.xml.resolver.tools.CatalogResolver;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.utils.ListingErrorHandler;

import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.TrackableV1;
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.attributes.tracking.TrackingInformationV1;
import org.wyona.yanel.core.serialization.SerializerFactory;
import org.wyona.yanel.core.source.ResourceResolver;
import org.wyona.yanel.core.source.SourceResolver;
import org.wyona.yanel.core.util.MailUtil;
import org.wyona.yanel.core.util.PathUtil;
import org.wyona.yanel.impl.resources.BasicXMLResource;
import org.wyona.yanel.servlet.AccessLog;
import org.wyona.yanel.core.attributes.tracking.TrackingInformationV1;

import org.wyona.yarep.core.NoSuchNodeException;
import org.wyona.yarep.core.RepositoryException;
import org.wyona.yarep.core.RepositoryFactory;
import org.wyona.yarep.util.RepoPath;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Simple contact form resource.
 */
public class ContactResourceV2 extends BasicXMLResource implements TrackableV1 {

    private static Logger log = Logger.getLogger(ContactResourceV2.class);

    // Constants
    private static final String SMTP_HOST = "smtpHost";
    private static final String SMTP_PORT = "smtpPort";
    private static final String TO = "to";
    private static final String SUBJECT = "subject";

    // Values for sending email
    private String smtpHost = "";
    private int smtpPort = 25;
    private String to = "";
    private ContactBean contact = null;
    //private Path path = null;

    // Email validation
    private String defaultEmailRegEx = "(\\w+)@(\\w+\\.)(\\w+)(\\.\\w+)*";

    // Tracking information
    private TrackingInformationV1 trackInfo;

    // Parameters passed to transformer
    private Map<String, String> params = new HashMap<String, String>();

    /**
     * Get content XML for a given request.
     * @return The content XML data.
     */
    protected InputStream getContentXML(String viewId) throws Exception {
        // Set up tracking info
        if(trackInfo != null) {
            trackInfo.addTag("contact");
            trackInfo.setPageType("contact");
        } else {
            log.warn("Tracking information bean is null! Check life cycle of resource!");
        }

        //path = new Path(request.getServletPath());

        String email = request.getParameter("email");
        // Checking if form was submitted
        if(email == null || "".equals(email)) {
            // The form has not yet been submitted - no email is provided.
            log.debug("Form not submitted yet!");

            // Print back message
            if(request.getParameter("message") != null) {
                setParameter("message", request.getParameter("message"));
            }

            String[] tags = new String[1];
            tags[0] = "contact";

            if (trackInfo != null) {
                trackInfo.setRequestAction("view");
            } else {
                log.warn("Tracking information bean is null! Check life cycle of resource!");
            }

            // Abort
            return getXMLDocument();
        }

        // Checking if spamblock is implemented
        if(request.getParameter("spamblock_hidden") == null ||
           request.getParameter("spamblock_input") == null) {
            // Spamblock is missing, aborting.
            throw new Exception("There is no spamblock implemented in the form.");
        }

        // Verifying that spamblock matches
        if(!request.getParameter("spamblock_hidden").equals("TRyAg41n") ||
           !request.getParameter("spamblock_input").equals("8989890")) {
            // Spamblock does not match - abort.
            return getXMLDocument();
        }

        // Spamblock is verified, process form.
        String message = request.getParameter("message");

        // Set tags
        // TODO: Maybe add contact subject, etc to tags?
        String[] tags = new String[1];
        tags[0] = "contact";

        if(trackInfo != null) {
            if(message != null) {
                trackInfo.addTag(message);
            }
            trackInfo.addCustomField("e-mail", email);
            trackInfo.setRequestAction("submit");
        } else {
            log.warn("Tracking information bean is null! Check life cycle of resource!");
        }

        Cookie cookie = AccessLog.getYanelAnalyticsCookie(request);
        String cookieValue = null;
        if (cookie != null) {
            cookieValue = cookie.getValue();
        } else {
            log.warn("No Yanel analytics cookie set yet!");
        }

        // Now send email
        sendMail(cookieValue);

        // Pass transformer paramters for output
        if(request.getParameter("company") != null) {
            setParameter("company", request.getParameter("company"));
        }
        if(request.getParameter("firstName") != null) {
            setParameter("firstName", request.getParameter("firstName"));
        }
        if(request.getParameter("lastName") != null) {
            setParameter("lastName", request.getParameter("lastName"));
        }
        if(request.getParameter("email") != null) {
            setParameter("email", email);
        }
        if(request.getParameter("address") != null) {
            setParameter("address", request.getParameter("address"));
        }
        if(request.getParameter("zipCity") != null) {
            setParameter("zipCity", request.getParameter("zipCity"));
        }
        if(request.getParameter("message") != null) {
            setParameter("message", message);
        }

        return getXMLDocument();
    }

    /**
     * Add transformer paramter.
     */
    private void setParameter(String key, String value) {
        params.put(key, value);
    }

    /**
     * Pass transformer paramters.
     */
    protected void passTransformerParameters(Transformer transformer)
    throws Exception {
        super.passTransformerParameters(transformer);

        for(Map.Entry<String, String> entry : params.entrySet()) {
            transformer.setParameter(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Send e-mail to administrator of this contact form
     * @param cookieValue Yanel analytics cookie value (in order to connect
     * history with this e-mail).
     */
    private void sendMail(String cookieValue) throws Exception {
        String email = getEnvironment().getRequest().getParameter("email");

        if(email == null || "".equals(email)) {
            log.warn("No email set yet!");
            setParameter("error", "emailNotSet");
            return;
        }

        if(!validateEmail(email)) {
            log.debug(
                "Doesn't seem to be a valid email: " + email + " (according " +
                "to the following regular expression: " + getEmailRegEx() + ")");
            setParameter("error", "emailNotValid");
            return;
        }

        contact = new ContactBean(request);

        String subject = getResourceConfigProperty(SUBJECT);
        if (subject == null) {
            subject = "Yanel Contact Resource: No subject specified";
        }
        to = getResourceConfigProperty(TO);

        String from = getResourceConfigProperty("from");
        if (from == null) {
            from = email;
        }

        String content = getBody(contact, cookieValue);

        if(to == null) {
            // INFO: Also see conf/contact-form_en.properties
            setParameter("error", "smtpConfigError");
            return;
        }

        smtpHost = getResourceConfigProperty(SMTP_HOST);
        String smtpPortAsString = getResourceConfigProperty(SMTP_PORT);

        try {
            if(smtpHost != null && smtpPortAsString != null) {
                smtpPort = Integer.parseInt(smtpPortAsString);
                MailUtil.send(smtpHost, smtpPort, from, to, subject, content);
                setParameter("sent", "true");
            } else {
                // INFO: Use default settings of Yanel for smtp-host and smtp-port
                String replyTo = from;
                if(contact.getFirstName() != null || contact.getLastName() != null) {
                    String sender = contact.getFirstName() + " " + contact.getLastName();
                    MailUtil.send(from, sender, replyTo, to, subject, content);
                } else {
                    MailUtil.send(from, replyTo, to, subject, content);
                }
                setParameter("sent", "true");
            }
        } catch(MessagingException e) {
            // There as an error delivering the email
            log.error(e, e);
            String cause = e.toString();
            if(cause.contains("MessagingException: Unknown SMTP")) {
                setParameter("error", "unknownHost");
            } else if(cause.contains("SendFailedException: Invalid Addresses")) {
                setParameter("error", "invalidAddress");
            } else {
                setParameter("error", "couldNotSendMail");
            }
        } catch(NumberFormatException nfe) {
            log.error(nfe);
            setParameter("error", "smtpPortNotCorrect");
            smtpPort = 0;
        }
    }

    /**
     * this method checks if the specified email is valid against a regular expression
     * @param email
     * @return true if email is valid
     */
    private boolean validateEmail(String email) throws Exception {
        Pattern pattern = Pattern.compile(getEmailRegEx());
        Matcher matcher = pattern.matcher(email);
        return matcher.find();
    }

    private String getEmailRegEx() throws Exception {
        if(getResourceConfigProperty("email-validation-regex") != null) {
            return getResourceConfigProperty("email-validation-regex");
        }
        return defaultEmailRegEx;
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.TrackableV1#doTrack(TrackingInformationV1)
     */
    public void doTrack(TrackingInformationV1 trackInfo) {
        this.trackInfo = trackInfo;
    }

    /**
     * Get email body. Please overwrite this method in order to customize email body.
     * @param contact Contact information
     * @param cookieValue Yanel analytics cookie value
     */
    protected String getBody(ContactBean contact, String cookieValue){
        StringBuffer content = new StringBuffer("");
        if(contact.getCompany() != null) {
            content.append("Company: ");
            content.append(contact.getCompany());
            content.append("\n");
        }
        if(contact.getFirstName() != null) {
            content.append("Firstname: ");
            content.append(contact.getFirstName());
            content.append("\n");
        }
        if(contact.getLastName() != null) {
            content.append("Lastname: ");
            content.append(contact.getLastName());
            content.append("\n");
        }
        if(contact.getAddress() != null) {
            content.append("Address: ");
            content.append(contact.getAddress());
            content.append("\n");
        }
        if(contact.getCity() != null) {
            content.append("City: ");
            content.append(contact.getCity());
            content.append("\n");
        }
        if(contact.getEmail() != null) {
            content.append("E-Mail: ");
            content.append(contact.getEmail());
            content.append("\n\n");
        }
        if(contact.getMessage() != null) {
            content.append("Message:\n");
            content.append(contact.getMessage());
            content.append("\n\n");
        }
        content.append("Yanel-analytics-cookie: ");
        content.append(cookieValue);
        return content.toString();
    }

    /**
     * Get XML to start with
     * @return XML as InputStream
     */
    private InputStream getXMLDocument() throws Exception {
        File xmlFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile().getParentFile().getAbsolutePath(), "htdocs" + File.separator + "xml" + File.separator + "contact-form.xml");
        return new java.io.FileInputStream(xmlFile.getAbsolutePath());
        //return new ByteArrayInputStream("<root/>".getBytes());
    }
}
