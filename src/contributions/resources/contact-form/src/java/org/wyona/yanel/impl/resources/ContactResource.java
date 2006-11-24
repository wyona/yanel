/*
 * Copyright 2006 Wyona
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

package org.wyona.yanel.impl.resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Category;
import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yarep.core.NoSuchNodeException;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryFactory;
import org.wyona.yarep.util.RepoPath;
import org.wyona.yarep.util.YarepUtil;

/**
 * 
 */
public class ContactResource extends Resource implements ViewableV1 {

    private static final String SMTP_HOST = "smtpHost:";

    private static final String SMTP_PORT = "smtpPort:";

    private static final String TO = "to:";

    private static Category log = Category.getInstance(ContactResource.class);

    private String smtpHost;

    private int smtpPort = 0;

    private String to;

    private String from;

    private String subject;

    private String content;

    /**
     * 
     */
    public ContactResource() {
    }

    /**
     * 
     */
    public ViewDescriptor[] getViewDescriptors() {
        return null;
    }
    
    /**
     * 
     */
    public View getView(Path path, String viewId) {
        View defaultView = new View();
        defaultView.setMimeType("application/xml");
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");
        defaultView.setInputStream(new java.io.StringBufferInputStream(sb
                .toString()));
        return defaultView;
    }

    /**
     * @throws Exception
     * 
     */
    public View getView(HttpServletRequest request, String viewId)
            throws Exception {
        Path path = new Path(request.getServletPath());
        Enumeration enumeration = request.getParameterNames();
        boolean submit = enumeration.hasMoreElements();
        // if submit null just return the file
        if (!submit) {
            View defaultView = new View();
            return plainRequest(path, defaultView);
        }
        String uri = request.getRequestURI();

        boolean success = false;
        Map status = null;
        if (!getProperties(path)) {
            log.warn("SetupException: ");
            status = new HashMap(3);
            status.put(SendMail.STATUS, "user-error");
            status.put(SendMail.MESSAGE, "Please verify your setup of the rti!");
            return computeForward(path, status);
        } else {
            // getting the form values
            ContactBean contact = new ContactBean(request);
            verifyContact(contact);
            try {
                SendMail.send(smtpHost, smtpPort, from, to, subject, content);
                success = true;
                status = new HashMap(3);
                status.put(SendMail.STATUS, "success");
            } catch (AddressException e) {
                log.warn("AddressException: ", e);
                status = new HashMap(3);
                status.put(SendMail.STATUS, "user-error");
                status.put(SendMail.MESSAGE, e.getMessage());
            } catch (MessagingException e) {
                log.warn("MessagingException: "
                        + "An error occured while sending email.", e);

                status = new HashMap(3);
                status.put(SendMail.STATUS, "server-error");
                status.put(SendMail.MESSAGE,
                        "An error occured while sending email: "
                                + e.getMessage());
            } finally {
                return computeForward(path, status);
            }
        }
    }

    private View plainRequest(Path path, View defaultView) throws Exception,
            TransformerConfigurationException,
            TransformerFactoryConfigurationError, NoSuchNodeException,
            TransformerException {
        Repository contentRepo;
        RepoPath rp = contentRepo(path);
        contentRepo = rp.getRepo();

        Transformer transformer = TransformerFactory.newInstance()
                .newTransformer(getXSLTStreamSource(path, contentRepo));
        transformer.setParameter("yanel.path.name", path.getName());
        transformer.setParameter("yanel.path", path.toString());
        transformer.setParameter("yanel.back2context", backToRoot(path, ""));
        transformer.setParameter("yarep.back2realm", backToRoot(new org.wyona.yanel.core.Path(rp.getPath().toString()), ""));
        // TODO: Is this the best way to generate an InputStream from an
        // OutputStream?
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        transformer.transform(new StreamSource(rp.getRepo().getInputStream(
                new org.wyona.yarep.core.Path(rp.getPath().toString()))),
                new StreamResult(baos));
        defaultView.setInputStream(new java.io.ByteArrayInputStream(baos
                .toByteArray()));
        defaultView.setMimeType(getMimeType(path));
        defaultView.setInputStream(new java.io.ByteArrayInputStream(baos
                .toByteArray()));

        return defaultView;
    }

    private RepoPath contentRepo(Path path) throws Exception {
        return new YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(
                path.toString()), getRepositoryFactory());
    }

    private String getMimeType(Path path, String viewId) {
        // TODO Auto-generated method stub
        return null;
    }

    private View computeForward(Path path, Map status) throws Exception {
        View defaultView = new View();
        defaultView.setMimeType("application/xml");
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        sb.append("<head>");
        // here we need to test the status of the mail
        String statusCode = (String) status.get(SendMail.STATUS);
        if (statusCode.equals("success")) {
            sb.append("<title>Your message has been sent successfully.</title>");
            sb.append("</head>");
            sb.append("<body>");
            sb.append("<div id=\"contenBody\">");
            sb.append("<h1>Success</h1>");
            sb.append("<p>Following message has been sent:</p>");
            // decoding content of the message body
            StringReader stringReader = new StringReader(content);
            BufferedReader reader = new BufferedReader(stringReader);
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append("<p>" + line + "</p>");
            }
            // finishing
            sb.append("</div>");
            sb.append("</body>");
        } else {
            sb.append("<title>Your message has not been sent.</title>");
            sb.append("</head>");
            sb.append("<body>");
            sb.append("<div id=\"contenBody\">");
            sb.append("<h1>" + status.get(SendMail.STATUS) + "</h1>");
            sb.append("<p>Your message has not been sent.</p>");
            sb.append("<p>" + status.get(SendMail.MESSAGE) + "</p>");
            sb.append("</div>");
            sb.append("</body>");
        }
        sb.append("</html>");
        computeView(path, defaultView, sb);
        return defaultView;
    }

    private void computeView(Path path, View defaultView, StringBuffer sb)
            throws Exception, TransformerConfigurationException,
            TransformerFactoryConfigurationError, NoSuchNodeException,
            TransformerException {

        RepoPath rp = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()), getRepositoryFactory());
        
        Transformer transformer = prepareTransformer(path);
        transformer.setParameter("yanel.back2context", backToRoot(path, ""));
        transformer.setParameter("yarep.back2realm", backToRoot(new org.wyona.yanel.core.Path(rp.getPath().toString()), ""));
        // TODO: Is this the best way to generate an InputStream from an
        // OutputStream?
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        transformer.transform(new StreamSource(
                new java.io.StringBufferInputStream(sb.toString())),
                new StreamResult(baos));
        defaultView.setInputStream(new java.io.ByteArrayInputStream(baos
                .toByteArray()));
        defaultView.setMimeType(getMimeType(path));
        defaultView.setInputStream(new java.io.ByteArrayInputStream(baos
                .toByteArray()));
    }

    private Transformer prepareTransformer(Path path) throws Exception,
            TransformerConfigurationException,
            TransformerFactoryConfigurationError, NoSuchNodeException {
        Repository contentRepo;
        RepoPath rp = contentRepo(path);
        contentRepo = rp.getRepo();
        Transformer transformer = TransformerFactory.newInstance()
                .newTransformer(getXSLTStreamSource(path, contentRepo));
        transformer.setParameter("yanel.path.name", path.getName());
        transformer.setParameter("yanel.path", path.toString());
        return transformer;
    }

    /**
     * Here we need to verify whether we have all props that we need for the
     * SendMail class.
     */
    private boolean verifyContact(ContactBean contact) {
        boolean success = false;
        if (null != contact.email & null != contact.message) {

            from = contact.email;
            subject = "Yanel feedback";
            content = "Company: " + contact.getCompany() + "\n" + "Firstname: "
                    + contact.getFirstName() + "\n" + "Lastname: "
                    + contact.getLastName() + "\n" + "Address: "
                    + contact.getAddress() + "\n" + "City: " + contact.getCity()
                    + "\n" + "Email: " + contact.getEmail() + "\n" + "\n"
                    + "Message:\n" + contact.message;
            success = true;
        }
        return success;

    }

    /**
     * 
     */
    private StreamSource getXSLTStreamSource(Path path, Repository repo)
            throws NoSuchNodeException {
        Path xsltPath = getXSLTPath(path);
        if (xsltPath != null) {
            return new StreamSource(repo
                    .getInputStream(new org.wyona.yarep.core.Path(getXSLTPath(
                            path).toString())));
        } else {
            File xsltFile = org.wyona.commons.io.FileUtil.file(rtd
                    .getConfigFile().getParentFile().getAbsolutePath(), "xslt"
                    + File.separator + "dir2xhtml.xsl");
            log.error("DEBUG: XSLT file: " + xsltFile);
            return new StreamSource(xsltFile);
        }
    }

    /**
     * 
     */
    private Path getXSLTPath(Path path) {
        String xsltPath = null;
        try {
            // TODO: Get yanel RTI yarep properties file name from framework
            // resp. use MapFactory ...!
            RepoPath rpRTI = new org.wyona.yarep.util.YarepUtil()
                    .getRepositoryPath(new org.wyona.yarep.core.Path(path
                            .toString()), getRTIRepositoryFactory());
            java.io.BufferedReader br = new java.io.BufferedReader(rpRTI
                    .getRepo().getReader(
                            new org.wyona.yarep.core.Path(new Path(rpRTI
                                    .getPath().toString()).getRTIPath()
                                    .toString())));

            while ((xsltPath = br.readLine()) != null) {
                if (xsltPath.indexOf("xslt:") == 0) {
                    xsltPath = xsltPath.substring(6);
                    log.debug("XSLT Path: " + xsltPath);
                    return new Path(xsltPath);
                }
            }
            log.error("No XSLT Path within: " + rpRTI.getPath());
        } catch (Exception e) {
            log.warn(e);
        }

        return null;
    }

    /**
     * Here we need to verify that all default information like mail host, port,
     * etc. are set. If not we can directly abort the processing and report an
     * error.
     */
    private boolean getProperties(Path path) {
        boolean success = false;
        String line = null;
        try {
            // TODO: Get yanel RTI yarep properties file name from framework
            // resp. use MapFactory ...!
            RepoPath rpRTI = new org.wyona.yarep.util.YarepUtil()
                    .getRepositoryPath(new org.wyona.yarep.core.Path(path
                            .toString()), getRTIRepositoryFactory());
            java.io.BufferedReader br = new java.io.BufferedReader(rpRTI
                    .getRepo().getReader(
                            new org.wyona.yarep.core.Path(new Path(rpRTI
                                    .getPath().toString()).getRTIPath()
                                    .toString())));

            while ((line = br.readLine()) != null) {
                if (line.indexOf(SMTP_HOST) == 0) {
                    smtpHost = line.substring(SMTP_HOST.length() + 1);
                }
                if (line.indexOf(SMTP_PORT) == 0) {
                    smtpPort = Integer.parseInt(line.substring(SMTP_PORT
                            .length() + 1));
                }
                if (line.indexOf(TO) == 0) {
                    to = line.substring(TO.length() + 1);
                }
            }
        } catch (Exception e) {
            log.warn(e);
        }
        if (null != smtpHost & 0 < smtpPort & null != to) {
            success = true;
        }
        return success;
    }

    /**
     * 
     */
    private String getMimeType(Path path) {
        String mimeType = null;
        try {
            // TODO: Get yanel RTI yarep properties file name from framework
            // resp. use MapFactory ...!
            RepoPath rpRTI = new org.wyona.yarep.util.YarepUtil()
                    .getRepositoryPath(new org.wyona.yarep.core.Path(path
                            .toString()), getRTIRepositoryFactory());
            java.io.BufferedReader br = new java.io.BufferedReader(rpRTI
                    .getRepo().getReader(
                            new org.wyona.yarep.core.Path(new Path(rpRTI
                                    .getPath().toString()).getRTIPath()
                                    .toString())));

            while ((mimeType = br.readLine()) != null) {
                if (mimeType.indexOf("mime-type:") == 0) {
                    mimeType = mimeType.substring(11);
                    log.info("*" + mimeType + "*");
                    // TODO: Maybe validate mime-type ...
                    return mimeType;
                }
            }
        } catch (Exception e) {
            log.warn(e);
        }

        // NOTE: Assuming fallback re dir2xhtml.xsl ...
        return "application/xhtml+xml";
    }
    
    /**
     * 
     * @return
     */
    protected RepositoryFactory getRepositoryFactory() {
        return yanel.getRepositoryFactory("DefaultRepositoryFactory");
    }
    
    /**
     * 
     * @return
     */
    protected RepositoryFactory getRTIRepositoryFactory() {
        return yanel.getRepositoryFactory("RTIRepositoryFactory");
    }
    
   /**
    *
    */
   private String backToRoot(Path path, String backToRoot) {
       org.wyona.commons.io.Path parent = path.getParent();
       if (parent != null && !isRoot(parent)) {
           return backToRoot(new Path(parent.toString()), backToRoot + "../");
       }
       return backToRoot;
   }
   
  /**
   *
   */
  private boolean isRoot(org.wyona.commons.io.Path path) {
      if (path.toString().equals(File.separator)) return true;
      return false;
  }
}
