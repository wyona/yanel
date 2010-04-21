/*
 * Copyright 2007 Wyona
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
import java.util.regex.Pattern;
import java.util.regex.Matcher;
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
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yarep.core.NoSuchNodeException;
import org.wyona.yarep.core.RepositoryException;
import org.wyona.yarep.core.RepositoryFactory;
import org.wyona.yanel.core.serialization.SerializerFactory;
import org.wyona.yanel.core.source.ResourceResolver;
import org.wyona.yanel.core.source.SourceResolver;
import org.wyona.yanel.core.transformation.I18nTransformer2;
import org.wyona.yanel.core.transformation.XIncludeTransformer;
import org.wyona.yanel.core.util.PathUtil;

import org.wyona.yarep.util.RepoPath;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


//XXX: This resource-type should rather be implemented with BasicXMLResource really...
public class ContactResource extends Resource implements ViewableV1 {

    private static Logger log = Logger.getLogger(ContactResource.class);
    private static Logger logAccess = Logger.getLogger(org.wyona.yanel.servlet.AccessLog.CATEGORY);

    private static final String SMTP_HOST = "smtpHost";
    private static final String SMTP_PORT = "smtpPort";
    private static final String TO = "to";
    private static final String SUBJECT = "subject";
    private String smtpHost = "";
    private int smtpPort = 25;
    private String to = "";
    private ContactBean contact = null;
    private String defaultLanguage = "en";
    private String messageBundle = "contact-form";

    private Path path = null;

    private String defaultEmailRegEx = "(\\w+)@(\\w+\\.)(\\w+)(\\.\\w+)*";

    public ViewDescriptor[] getViewDescriptors() {
        return null;
    }

    public View getView(Path path, String viewId) {
        return null;
    }

    /**
     *
     */
    public View getView(HttpServletRequest request, String viewId) throws Exception {
        path = new Path(request.getServletPath());

        File xmlFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile().getParentFile().getAbsolutePath(), "xml" + File.separator + "contact-form.xml");

            // create reader:
            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            CatalogResolver catalogResolver = new CatalogResolver();
            xmlReader.setEntityResolver(catalogResolver);

            // create Body xslt transformer:
            SAXTransformerFactory tf = (SAXTransformerFactory)TransformerFactory.newInstance();
            SourceResolver uriResolver = new SourceResolver(this);
            tf.setURIResolver(uriResolver);
            java.io.StringWriter errorWriter = new java.io.StringWriter();
            ListingErrorHandler errorListener = new ListingErrorHandler(new java.io.PrintWriter(errorWriter));
            tf.setErrorListener(errorListener);

            TransformerHandler xsltHandler1 = tf.newTransformerHandler(getBodyXSLTStreamSource());
            Transformer transformer = xsltHandler1.getTransformer();

            boolean submit = request.getParameter("email") != null;
            if(submit) {
                if (request.getParameter("spamblock_hidden") == null || request.getParameter("spamblock_input") == null) {
                    throw new Exception("there is no spamblock implemented in the form.");
                }
                if (request.getParameter("spamblock_hidden").equals("TRyAg41n") && request.getParameter("spamblock_input").equals("8989890")) {    
                    logAccess.info(org.wyona.yanel.servlet.AccessLog.getLogMessage(request, getRealm().getID()) + " e-mail:" + request.getParameter("email"));
                    sendMail(transformer, org.wyona.yanel.servlet.AccessLog.getYanelAnalyticsCookie(request).getValue());


                    if (request.getParameter("company") != null) transformer.setParameter("company", request.getParameter("company"));
                    if (request.getParameter("firstName") != null) transformer.setParameter("firstName", request.getParameter("firstName"));
                    if (request.getParameter("lastName") != null) transformer.setParameter("lastName", request.getParameter("lastName"));
                    if (request.getParameter("email") != null) transformer.setParameter("email", request.getParameter("email"));
                    if (request.getParameter("address") != null) transformer.setParameter("address", request.getParameter("address"));
                    if (request.getParameter("zipCity") != null) transformer.setParameter("zipCity", request.getParameter("zipCity"));
                    if (request.getParameter("message") != null) transformer.setParameter("message", request.getParameter("message"));
                }
            }
            transformer.setParameter("content-language", getContentLanguage());

            // create xinclude transformer:
            XIncludeTransformer xIncludeTransformer = new XIncludeTransformer();
            ResourceResolver resolver = new ResourceResolver(this);
            xIncludeTransformer.setResolver(resolver);

            // create i18n transformer:
            String[] messageBundles = new String[2];
            messageBundles[0] = messageBundle;
            messageBundles[1] = "global";

            log.debug("Language: " + getRequestedLanguage());
            log.debug("Realm default language: " + getRealm().getDefaultLanguage());
            I18nTransformer2 i18nTransformer = new I18nTransformer2(messageBundles, getRequestedLanguage(), getRealm().getDefaultLanguage());
            i18nTransformer.setEntityResolver(catalogResolver);

            // create serializer:
            Serializer serializer = SerializerFactory.getSerializer(SerializerFactory.XHTML_STRICT);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // chain everything together (create a pipeline):
            xmlReader.setContentHandler(xsltHandler1);

            // create xslt transformer for global layout
            StreamSource globalStreamSource = getGlobalXSLTStreamSource();
            if (globalStreamSource != null) {
                TransformerHandler xsltHandlerGlobal = tf.newTransformerHandler(globalStreamSource);
                transformer = xsltHandlerGlobal.getTransformer();
                transformer.setParameter("yanel.path.name", org.wyona.commons.io.PathUtil.getName(getPath()));
                transformer.setParameter("yanel.path", getPath());
                transformer.setParameter("yanel.back2context", PathUtil.backToContext(realm, getPath()));
                transformer.setParameter("yarep.back2realm", PathUtil.backToRealm(getPath()));
                transformer.setParameter("content-language", getContentLanguage());
                transformer.setParameter("language", getRequestedLanguage());
                transformer.setParameter("yanel.reservedPrefix", getYanel().getReservedPrefix());
                xsltHandler1.setResult(new SAXResult(xsltHandlerGlobal));
                xsltHandlerGlobal.setResult(new SAXResult(xIncludeTransformer));
            } else {
                xsltHandler1.setResult(new SAXResult(xIncludeTransformer));
            }

            xIncludeTransformer.setResult(new SAXResult(i18nTransformer));
            i18nTransformer.setResult(new SAXResult(serializer.asContentHandler()));
            serializer.setOutputStream(baos);

            // execute pipeline:
            xmlReader.parse(new InputSource(new FileInputStream(xmlFile)));

            // create view:
            View defaultView = new View();
            defaultView.setMimeType(getMimeType());
            defaultView.setInputStream(new ByteArrayInputStream(baos.toByteArray()));
            return defaultView;
    }

    /**
     * Send E-Mail to administrator of this contact form
     *
     * @param transformer Transformer to generate HTTP response, whereas various messages might be added (errors, warnings, etc.)
     * @param cookieValue Yanel analytics cookie value in order to connect history with this e-mail
     */
    private void sendMail(Transformer transformer, String cookieValue) throws Exception {
        String email = getEnvironment().getRequest().getParameter("email");
        if(email == null || ("").equals(email)) {
            transformer.setParameter("error", "emailNotSet");
        } else if(!validateEmail(email)) {
            log.warn("Doesn't seem to be a valid email: " + email + " (according to the following regular expression: " + getEmailRegEx() + ")");
            transformer.setParameter("error", "emailNotValid");
        } else {
            contact = new ContactBean(request);

            String subject = getResourceConfigProperty(SUBJECT);
            if (subject == null) subject = "Yanel Contact Resource: No subject specified";
            to = getResourceConfigProperty(TO);

            String from = getResourceConfigProperty("from");
            if (from == null) {
                from = email;
            }

            String content = "";
            if (contact.getCompany() != null) content = content + "Company: " + contact.getCompany() + "\n";
            if (contact.getFirstName() != null) content = content + "Firstname: " + contact.getFirstName() + "\n";
            if (contact.getLastName() != null) content = content + "Lastname: " + contact.getLastName() + "\n";
            if (contact.getAddress() != null) content = content + "Address: " + contact.getAddress() + "\n";
            if (contact.getCity() != null) content = content + "City: " + contact.getCity() + "\n";
            if (contact.getEmail() != null) content = content + "E-Mail: " + contact.getEmail() + "\n\n";
            if (contact.message != null) content = content + "Message:\n" + contact.message + "\n\n";
            content = content + "Yanel-analytics-cookie: " + cookieValue;


            if(to != null) {
                try {
                    smtpHost = getResourceConfigProperty(SMTP_HOST);
                    String smtpPortAsString = getResourceConfigProperty(SMTP_PORT);
                    if(smtpHost != null && smtpPortAsString != null) {
                        try {
                            smtpPort = Integer.parseInt(smtpPortAsString);
                            SendMail.send(smtpHost, smtpPort, from, to, subject, content);
                            transformer.setParameter("sent", "true");
                        } catch(NumberFormatException nfe) {
                            log.error(nfe);
                            transformer.setParameter("error", "smtpPortNotCorrect");
                            smtpPort = 0;
                        }
                    } else {
                        // INFO: Use default settings of Yanel for smtp-host and smtp-port
                        SendMail.send(null, -1, from, to, subject, content);
                        transformer.setParameter("sent", "true");
                    }
                } catch(javax.mail.MessagingException e) {
                    log.error(e, e);
                    if(("" + e).startsWith("javax.mail.MessagingException: Unknown SMTP")) {
                        transformer.setParameter("error", "unknownHost");
                    } else if(("" + e).startsWith("javax.mail.SendFailedException: Invalid Addresses")) {
                        transformer.setParameter("error", "invalidAddress");
                    } else {
                        transformer.setParameter("error", "couldNotSendMail");
                    }
                }
            } else {
                transformer.setParameter("error", "smtpConfigError"); // INFO: Also see conf/contact-form_en.properties
            }
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

    /**
     * Get global XSLT
     */
    private StreamSource getGlobalXSLTStreamSource() throws NoSuchNodeException, RepositoryException, Exception {
        String xsltPath = getResourceConfigProperty("xslt");
        if (xsltPath != null) {
            return new StreamSource(getRealm().getRepository().getNode(xsltPath).getInputStream());
        }

        log.warn("No global XSLT has been set/configured.");
        return null;

/*
        File xsltFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile().getParentFile().getAbsolutePath(), "xslt" + File.separator + "global.xsl");
        log.error("DEBUG: XSLT file: " + xsltFile);
        return new StreamSource(xsltFile);
*/
    }

    /**
     * Get body XSLT
     */
    private StreamSource getBodyXSLTStreamSource() throws NoSuchNodeException, RepositoryException, Exception {
        String xsltPath = getResourceConfigProperty("xslt-body");
        if (xsltPath != null) {
            return new StreamSource(getRealm().getRepository().getNode(xsltPath).getInputStream());
        }
        return new StreamSource(org.wyona.commons.io.FileUtil.file(rtd.getConfigFile().getParentFile().getAbsolutePath(), "xslt" + File.separator + "contact-form.xsl"));
    }

    /**
     * Get mime type
     */
    private String getMimeType() throws Exception {
        String mimeType = getResourceConfigProperty("mime-type");
        if (mimeType != null) return mimeType;

        return "application/xhtml+xml";
    }

    private String getEmailRegEx() throws Exception {
        if (getResourceConfigProperty("email-validation-regex") != null) return getResourceConfigProperty("email-validation-regex");
        return defaultEmailRegEx;
    }
}
