package org.wyona.yanel.servlet;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceConfiguration;
import org.wyona.yanel.core.ResourceTypeDefinition;
import org.wyona.yanel.core.ResourceTypeIdentifier;
import org.wyona.yanel.core.ResourceTypeRegistry;
import org.wyona.yanel.core.Yanel;
import org.wyona.yanel.core.api.attributes.ModifiableV1;
import org.wyona.yanel.core.api.attributes.ModifiableV2;
import org.wyona.yanel.core.api.attributes.VersionableV2;
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.versionable.RevisionInformation;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.navigation.Node;
import org.wyona.yanel.core.navigation.Sitetree;
import org.wyona.yanel.core.util.DateUtil;
import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.core.map.Realm;

import org.wyona.yanel.servlet.CreateUsecaseHelper;
import org.wyona.yanel.servlet.communication.HttpRequest;
import org.wyona.yanel.servlet.communication.HttpResponse;
import org.wyona.yanel.util.ResourceAttributeHelper;

import org.wyona.security.core.AuthenticationException;
import org.wyona.security.core.api.Identity;
import org.wyona.security.core.api.IdentityManager;
import org.wyona.security.core.api.PolicyManager;
import org.wyona.security.core.api.Role;

import org.apache.log4j.Category;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 */
public class YanelServlet extends HttpServlet {

    private static Category log = Category.getInstance(YanelServlet.class);

    private ServletConfig config;

    ResourceTypeRegistry rtr;

    //PolicyManager pm;
    //IdentityManager im;
    Map map;
    Yanel yanel;
    Sitetree sitetree;

    File xsltInfoAndException;
    File xsltLoginScreen;

    private static String IDENTITY_KEY = "identity";
    private static String NAMESPACE = "http://www.wyona.org/yanel/1.0";

    private static final String METHOD_PROPFIND = "PROPFIND";
    private static final String METHOD_OPTIONS = "OPTIONS";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_DELETE = "DELETE";

    private String sslPort = null;
    
    public static final String DEFAULT_ENCODING = "UTF-8";

    /**
     *
     */
    public void init(ServletConfig config) throws ServletException {
        this.config = config;

        xsltInfoAndException = org.wyona.commons.io.FileUtil.file(config.getServletContext().getRealPath("/"), config.getInitParameter("exception-and-info-screen-xslt"));
        xsltLoginScreen = org.wyona.commons.io.FileUtil.file(config.getServletContext().getRealPath("/"), config.getInitParameter("login-screen-xslt"));
        try {
            yanel = Yanel.getInstance();
            yanel.init();
            
            rtr = yanel.getResourceTypeRegistry();

            map = (Map) yanel.getBeanFactory().getBean("map");

            sitetree = (Sitetree) yanel.getBeanFactory().getBean("nav-sitetree");

            sslPort = config.getInitParameter("ssl-port");
            
        } catch (Exception e) {
            log.error(e);
            throw new ServletException(e.getMessage(), e);
        }
    }

    /**
     *
     */
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String httpAcceptMediaTypes = request.getHeader("Accept");
        log.debug("HTTP Accept Media Types: " + httpAcceptMediaTypes);
        log.debug("HTTP User Agent: " + request.getHeader("User-Agent"));
        String httpAcceptLanguage = request.getHeader("Accept-Language");
        log.debug("HTTP Accept Language: " + httpAcceptLanguage);

        // Logout from Yanel
        String yanelUsecase = request.getParameter("yanel.usecase");
        if(yanelUsecase != null && yanelUsecase.equals("logout")) {
            if(doLogout(request, response) != null) return;
        }

        // Authentication
        if(doAuthenticate(request, response) != null) return;

        // Check authorization
        if(doAuthorize(request, response) != null) return;

        // Delegate ...
        String method = request.getMethod();
        if (method.equals(METHOD_PROPFIND)) {
            doPropfind(request, response);
        } else if (method.equals(METHOD_GET)) {
            doGet(request, response);
        } else if (method.equals(METHOD_POST)) {
            doPost(request, response);
        } else if (method.equals(METHOD_PUT)) {
            doPut(request, response);
        } else if (method.equals(METHOD_DELETE)) {
            doDelete(request, response);
        } else if (method.equals(METHOD_OPTIONS)) {
            doOptions(request, response);
        } else {
            log.error("No such method implemented: " + method);
            response.sendError(response.SC_NOT_IMPLEMENTED);
        }
    }

    /**
     *
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if a new resource shall be created ...
        String yanelUsecase = request.getParameter("yanel.usecase");
        if(yanelUsecase != null && yanelUsecase.equals("create")) {
            CreateUsecaseHelper creator = new CreateUsecaseHelper();
            creator.create(request, response, yanel);
            return;
        }

        String yanelWebDAV = request.getParameter("yanel.webdav");
        if(yanelWebDAV != null && yanelWebDAV.equals("edit")) {
            Resource resource = getResource(request, response);
            log.error("DEBUG: WebDAV client (" + request.getHeader("User-Agent") + ") requests to edit a resource: " + resource.getRealm() + ", " + resource.getPath());
            //return;
        }

        getContent(request, response);
    }

    /**
     * Get view of resource
     */
    private void getContent(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        View view = null;

        org.w3c.dom.Document doc = null;
        javax.xml.parsers.DocumentBuilderFactory dbf= javax.xml.parsers.DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        try {
            javax.xml.parsers.DocumentBuilder parser = dbf.newDocumentBuilder();
            org.w3c.dom.DOMImplementation impl = parser.getDOMImplementation();
            org.w3c.dom.DocumentType doctype = null;
            doc = impl.createDocument(NAMESPACE, "yanel", doctype);
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e.getMessage());
        }

        Element rootElement = doc.getDocumentElement();

        String servletContextRealPath = config.getServletContext().getRealPath("/");
        rootElement.setAttribute("servlet-context-real-path", servletContextRealPath);

        Element requestElement = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "request"));
        requestElement.setAttributeNS(NAMESPACE, "uri", request.getRequestURI());
        requestElement.setAttributeNS(NAMESPACE, "servlet-path", request.getServletPath());

        HttpSession session = request.getSession(true);
        Element sessionElement = (Element) rootElement.appendChild(doc.createElement("session"));
        sessionElement.setAttribute("id", session.getId());
        Enumeration attrNames = session.getAttributeNames();
        if (!attrNames.hasMoreElements()) {
            Element sessionNoAttributesElement = (Element) sessionElement.appendChild(doc.createElement("no-attributes"));
        }
        while (attrNames.hasMoreElements()) {
            String name = (String)attrNames.nextElement();
            String value = session.getAttribute(name).toString();
            Element sessionAttributeElement = (Element) sessionElement.appendChild(doc.createElement("attribute"));
            sessionAttributeElement.setAttribute("name", name);
            sessionAttributeElement.appendChild(doc.createTextNode(value));
        }

        String usecase = request.getParameter("yanel.resource.usecase");

        Resource res = null;
        long lastModified = -1;
        long size = -1;
            try {
                res = getResource(request, response);
                if (res != null) {

                    Element resourceElement = (Element) rootElement.appendChild(doc.createElement("resource"));
                    ResourceConfiguration resConfig = res.getConfiguration();
                    if (resConfig != null) {
                        Element resConfigElement = (Element) resourceElement.appendChild(doc.createElementNS(NAMESPACE, "config"));
                        resConfigElement.setAttributeNS(NAMESPACE, "rti-name", resConfig.getName());
                        resConfigElement.setAttributeNS(NAMESPACE, "rti-namespace", resConfig.getNamespace());
                    } else {
                        Element noResConfigElement = (Element) resourceElement.appendChild(doc.createElementNS(NAMESPACE, "no-config"));
                    }
                    if (ResourceAttributeHelper.hasAttributeImplemented(res, "Viewable", "1")) {
                        log.debug("Resource is viewable V1");
                        Element viewElement = (Element) resourceElement.appendChild(doc.createElement("view"));
                        viewElement.setAttributeNS(NAMESPACE, "version", "1");

                        // TODO: The same as for ViewableV2 ...
                        ViewDescriptor[] vd = ((ViewableV1) res).getViewDescriptors();
                        if (vd != null) {
                            for (int i = 0; i < vd.length; i++) {
                                Element descriptorElement = (Element) viewElement.appendChild(doc.createElement("descriptor"));
                                descriptorElement.appendChild(doc.createTextNode(vd[i].getMimeType()));
                                descriptorElement.setAttributeNS(NAMESPACE, "id", vd[i].getId());
                            }
                        } else {
                            viewElement.appendChild(doc.createTextNode("No View Descriptors!"));
                        }

                        String viewId = request.getParameter("yanel.resource.viewid");
                        try {
                            view = ((ViewableV1) res).getView(request, viewId);
                        } catch(org.wyona.yarep.core.NoSuchNodeException e) {
                            // TODO: Log all 404 within a dedicated file (with client info attached) such that an admin can react to it ...
                            String message = "No such node exception: " + e;
                            log.warn(e);
                            //log.error(e.getMessage(), e);
                            Element exceptionElement = (Element) rootElement.appendChild(doc.createElement("exception"));
                            exceptionElement.appendChild(doc.createTextNode(message));
                            exceptionElement.setAttribute("status", "404");
                            response.setStatus(javax.servlet.http.HttpServletResponse.SC_NOT_FOUND);
                            setYanelOutput(request, response, doc);
                            return;
                        } catch(Exception e) {
                            log.error(e.getMessage(), e);
                            String message = e.toString();
                            log.error(e.getMessage(), e);
                            Element exceptionElement = (Element) rootElement.appendChild(doc.createElement("exception"));
                            exceptionElement.appendChild(doc.createTextNode(message));
                            exceptionElement.setAttribute("status", "500");
                            response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            setYanelOutput(request, response, doc);
                            return;
                        }
                    } else if (ResourceAttributeHelper.hasAttributeImplemented(res, "Viewable", "2")) {
                        log.debug("Resource is viewable V2");
                        String viewId = request.getParameter("yanel.resource.viewid");
                        Element viewElement = (Element) resourceElement.appendChild(doc.createElement("view"));
                        viewElement.setAttributeNS(NAMESPACE, "version", "2");
                        ViewDescriptor[] vd = ((ViewableV2) res).getViewDescriptors();
                        if (vd != null) {
                            for (int i = 0; i < vd.length; i++) {
                                Element descriptorElement = (Element) viewElement.appendChild(doc.createElement("descriptor"));
                                descriptorElement.appendChild(doc.createTextNode(vd[i].getMimeType()));
                                descriptorElement.setAttributeNS(NAMESPACE, "id", vd[i].getId());
                            }
                        } else {
                            viewElement.appendChild(doc.createTextNode("No View Descriptors!"));
                        }

                        size = ((ViewableV2) res).getSize();
                        Element sizeElement = (Element) resourceElement.appendChild(doc.createElement("size"));
                        sizeElement.appendChild(doc.createTextNode(String.valueOf(size)));
                        try {
                            String revisionName = request.getParameter("yanel.resource.revision");
                            if (ResourceAttributeHelper.hasAttributeImplemented(res, "Versionable", "2") && revisionName != null) {
                                view = ((VersionableV2) res).getView(viewId, revisionName);
                            } else {
                                view = ((ViewableV2) res).getView(viewId);
                            }
                        } catch(org.wyona.yarep.core.NoSuchNodeException e) {
                            // TODO: Log all 404 within a dedicated file (with client info attached) such that an admin can react to it ...
                            String message = "No such node exception: " + e;
                            log.warn(e);
                            //log.error(e.getMessage(), e);
                            Element exceptionElement = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "exception"));
                            exceptionElement.appendChild(doc.createTextNode(message));
                            exceptionElement.setAttributeNS(NAMESPACE, "status", "404");
                            response.setStatus(javax.servlet.http.HttpServletResponse.SC_NOT_FOUND);
                            setYanelOutput(request, response, doc);
                            return;
                        }
                    } else {
                         Element noViewElement = (Element) resourceElement.appendChild(doc.createElement("not-viewable"));
                         noViewElement.appendChild(doc.createTextNode(res.getClass().getName() + " is not viewable!"));
                    }
                    if (ResourceAttributeHelper.hasAttributeImplemented(res, "Modifiable", "2")) {
                        lastModified = ((ModifiableV2) res).getLastModified();
                        Element lastModifiedElement = (Element) resourceElement.appendChild(doc.createElement("last-modified"));
                        lastModifiedElement.appendChild(doc.createTextNode(new java.util.Date(lastModified).toString()));
                    } else {
                        Element noLastModifiedElement = (Element) resourceElement.appendChild(doc.createElement("no-last-modified"));
                    }
                    if (ResourceAttributeHelper.hasAttributeImplemented(res, "Versionable", "2")) {
                        // retrieve the revisions, but only in the meta usecase (for performance reasons):
                        if (request.getParameter("yanel.resource.meta") != null) {
                            RevisionInformation[] revisions = ((VersionableV2)res).getRevisions();
                            Element revisionsElement = (Element) resourceElement.appendChild(doc.createElement("revisions"));
                            if (revisions != null) {
                                for (int i=0; i<revisions.length; i++) {
                                    Element revisionElement = (Element) revisionsElement.appendChild(doc.createElement("revision"));
                                    Element revisionNameElement = (Element) revisionElement.appendChild(doc.createElement("name"));
                                    revisionNameElement.appendChild(doc.createTextNode(revisions[i].getName()));
                                    Element revisionDateElement = (Element) revisionElement.appendChild(doc.createElement("date"));
                                    revisionDateElement.appendChild(doc.createTextNode(DateUtil.format(revisions[i].getDate())));
                                    Element revisionUserElement = (Element) revisionElement.appendChild(doc.createElement("user"));
                                    revisionUserElement.appendChild(doc.createTextNode(revisions[i].getUser()));
                                    Element revisionCommentElement = (Element) revisionElement.appendChild(doc.createElement("comment"));
                                    revisionCommentElement.appendChild(doc.createTextNode(revisions[i].getComment()));
                                }
                                
                            } else {
                                Element noRevisionsYetElement = (Element) resourceElement.appendChild(doc.createElement("no-revisions-yet"));
                            }
                        }
                    } else {
                        Element notVersionableElement = (Element) resourceElement.appendChild(doc.createElement("not-versionable"));
                    }
                    
                    
                    if (usecase != null && usecase.equals("checkout")) {
                        log.debug("Checkout data ...");
                        
                        if (ResourceAttributeHelper.hasAttributeImplemented(res, "Versionable", "2")) {
                            // note: this will throw an exception if the document is checked out already
                            // by another user.
                            Identity identity = (Identity) request.getSession().getAttribute("identity");
                            String userID = identity.getUsername();
                            VersionableV2 versionable = (VersionableV2)res;
                            if (versionable.isCheckedOut()) {
                                String checkoutUserID = versionable.getCheckoutUserID(); 
                                if (checkoutUserID.equals(userID)) {
                                    log.warn("Resource " + res.getPath() + " is already checked out by this user: " + checkoutUserID);
                                } else {
                                    throw new Exception("Resource is already checked out by another user: " + checkoutUserID);
                                }
                            } else {
                                versionable.checkout(userID);
                            }
                        }
                        
                        log.warn("Acquire lock has not been implemented yet ...!");
                        // acquireLock();
                    }


                } else {
                        Element resourceIsNullElement = (Element) rootElement.appendChild(doc.createElement("resource-is-null"));
                }
            } catch(Exception e) {
                log.error(e.getMessage(), e);
                String message = e.toString();
                Element exceptionElement = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "exception"));
                exceptionElement.appendChild(doc.createTextNode(message));
                setYanelOutput(request, response, doc);
                response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }



        String meta = request.getParameter("yanel.resource.meta");
        if (meta != null) {
            if (meta.length() > 0) {
                log.warn("TODO: meta: " + meta);
            } else {
                log.debug("Show all meta");
            }
            response.setStatus(javax.servlet.http.HttpServletResponse.SC_OK);
            setYanelOutput(request, response, doc);
            return;
        }


        if (view != null) {
            // Check if the view contains the response, otherwise assume that the resource wrote the response, and just return.
            if (!view.isResponse()) return;
            
            if (view.getEncoding() != null) {
                response.setContentType(patchMimeType(view.getMimeType(), request) + "; charset=" + view.getEncoding());
            } else if (res.getConfiguration() != null && res.getConfiguration().getEncoding() != null) {
                response.setContentType(patchMimeType(view.getMimeType(), request) + "; charset=" + res.getConfiguration().getEncoding());
            } else {
                // try to guess if we have to set the default encoding
                String mimeType = view.getMimeType();
                if (mimeType.startsWith("text") || 
                    mimeType.equals("application/xml") || 
                    mimeType.equals("application/xhtml+xml") || 
                    mimeType.equals("application/atom+xml") || 
                    mimeType.equals("application/x-javascript")) {
                    response.setContentType(patchMimeType(mimeType, request) + "; charset=" + DEFAULT_ENCODING);
                } else {
                    // probably binary mime-type, don't set encoding
                    response.setContentType(patchMimeType(mimeType, request));
                }
            }

            InputStream is = view.getInputStream();

            byte buffer[] = new byte[8192];
            int bytesRead;
           
            if (is != null) {
                // TODO: Yarep does not set returned Stream to null resp. is missing Exception Handling for the constructor. Exceptions should be handled here, but rather within Yarep or whatever repositary layer is being used ...
                bytesRead = is.read(buffer);
                if (bytesRead == -1) {
                    String message = "InputStream of view does not seem to contain any data!";

                    Element exceptionElement = (Element) rootElement.appendChild(doc.createElement("exception"));
                    exceptionElement.appendChild(doc.createTextNode(message));
                    setYanelOutput(request, response, doc);
                    response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return;
                }

                // TODO: Compare If-Modified-Since with lastModified and return 304 without content resp. check on ETag
                String ifModifiedSince = request.getHeader("If-Modified-Since");
                if (ifModifiedSince != null) {
                    log.warn("TODO: Implement 304 ...");
                }

                java.io.OutputStream os = response.getOutputStream();
                os.write(buffer, 0, bytesRead);
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                if(lastModified >= 0) response.setDateHeader("Last-Modified", lastModified);
                return;
            } else {
                String message = "InputStream of view is null!";
                Element exceptionElement = (Element) rootElement.appendChild(doc.createElement("exception"));
                exceptionElement.appendChild(doc.createTextNode(message));
            }
        } else {
            String message = "View is null!";
            Element exceptionElement = (Element) rootElement.appendChild(doc.createElement("exception"));
            exceptionElement.appendChild(doc.createTextNode(message));
        }

        setYanelOutput(request, response, doc);
        response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return;
    }

    /**
     *
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String value = request.getParameter("yanel.resource.usecase");

        if (value != null && value.equals("save")) {
            log.debug("Save data ...");
            save(request, response, false);
            return;
        } else if (value != null && value.equals("checkin")) {
            log.debug("Checkin data ...");
            save(request, response, true);

            log.warn("Release lock has not been implemented yet ...");
            // releaseLock();
            return;
        } else {
            log.info("No parameter yanel.resource.usecase!");

            String contentType = request.getContentType();
            if (contentType.indexOf("application/atom+xml") >= 0) {
                InputStream in = intercept(request.getInputStream());
                // Create new Atom entry
                try {
                    String atomEntryUniversalName = "<{http://www.wyona.org/yanel/resource/1.0}atom-entry/>";
                    org.wyona.yanel.core.map.Realm realm = yanel.getMap().getRealm(request.getServletPath());
                    String newEntryPath = yanel.getMap().getPath(realm, request.getServletPath() + "/" + new java.util.Date().getTime() + ".xml");

                    log.error("DEBUG: Realm and Path of new Atom entry: " + realm + " " + newEntryPath);
                    Resource atomEntryResource = yanel.getResourceManager().getResource(request, response, realm, newEntryPath, new ResourceTypeRegistry().getResourceTypeDefinition(atomEntryUniversalName), new org.wyona.yanel.core.ResourceTypeIdentifier(atomEntryUniversalName, null));
                    
                    ((ModifiableV2)atomEntryResource).write(in);

                    byte buffer[] = new byte[8192];
                    int bytesRead;
                    InputStream resourceIn = ((ModifiableV2)atomEntryResource).getInputStream();
                    OutputStream responseOut = response.getOutputStream();
                    while ((bytesRead = resourceIn.read(buffer)) != -1) {
                        responseOut.write(buffer, 0, bytesRead);
                    }

                    // TODO: Fix Location ...
                    response.setHeader("Location", "http://ulysses.wyona.org" + newEntryPath);
                    response.setStatus(javax.servlet.http.HttpServletResponse.SC_CREATED);
                    return;
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new IOException(e.getMessage());
                }
            }

            getContent(request, response);
        }
    }

    /**
     * HTTP PUT implementation
     */
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO: Reuse code doPost resp. share code with doPut

        String value = request.getParameter("yanel.resource.usecase");

        if (value != null && value.equals("save")) {
            log.debug("Save data ...");
            save(request, response, false);
            return;
        } else if (value != null && value.equals("checkin")) {
            log.debug("Checkin data ...");
            save(request, response, true);
            
            log.warn("Release lock has not been implemented yet ...!");
            // releaseLock();
            return;
        } else {
            log.warn("No parameter yanel.resource.usecase!");

            String contentType = request.getContentType();
            if (contentType != null && contentType.indexOf("application/atom+xml") >= 0) {
                InputStream in = intercept(request.getInputStream());
                // Overwrite existing atom entry
                try {
                    String atomEntryUniversalName = "<{http://www.wyona.org/yanel/resource/1.0}atom-entry/>";
                    org.wyona.yanel.core.map.Realm realm = yanel.getMap().getRealm(request.getServletPath());
                    String entryPath = yanel.getMap().getPath(realm, request.getServletPath());

                    log.error("DEBUG: Realm and Path of new Atom entry: " + realm + " " + entryPath);

                    Resource atomEntryResource = yanel.getResourceManager().getResource(request, response, realm, entryPath, new ResourceTypeRegistry().getResourceTypeDefinition(atomEntryUniversalName), new org.wyona.yanel.core.ResourceTypeIdentifier(atomEntryUniversalName, null));
                    
                    // TODO: There seems to be a problem ...
                    ((ModifiableV2)atomEntryResource).write(in);

                    // NOTE: This method does not update updated date
/*
                    OutputStream out = ((ModifiableV2)atomEntry).getOutputStream(entryPath);
                    byte buffer[] = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
*/

                    log.info("Atom entry has been saved: " + entryPath);

                    response.setStatus(javax.servlet.http.HttpServletResponse.SC_OK);
                    return;
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new IOException(e.getMessage());
                }
            } else {
                Resource resource = getResource(request, response);
                log.error("DEBUG: Client (" + request.getHeader("User-Agent") + ") requests to save a resource: " + resource.getRealm() + ", " + resource.getPath());
                save(request, response, false);
                return;
            }
        }
    }

    /**
     * HTTP DELETE implementation
     */
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Resource res = getResource(request, response);
            if (ResourceAttributeHelper.hasAttributeImplemented(res, "Modifiable", "2")) {
                if (((ModifiableV2) res).delete()) {
                    log.debug("Resource has been deleted: " + res);
                    response.setStatus(response.SC_OK);
                    return;
                } else {
                    log.warn("Resource could not be deleted: " + res);
                    response.setStatus(response.SC_FORBIDDEN);
                    return;
                }
            } else {
                log.error("Resource '" + res + "' has interface ModifiableV2 not implemented." );
                response.sendError(response.SC_NOT_IMPLEMENTED);
                return;
            }
        } catch (Exception e) {
            log.error("Could not delete resource with URL " + request.getRequestURL() + " " + e.getMessage(), e);
            throw new ServletException(e.getMessage(), e);
        }
    }

    /**
     *
     */
    private Resource getResource(HttpServletRequest request, HttpServletResponse response) {
        try {
            Realm realm = map.getRealm(request.getServletPath());
            String path = map.getPath(realm, request.getServletPath());
            HttpRequest httpRequest = new HttpRequest(request);
            HttpResponse httpResponse = new HttpResponse(response);
            Resource res = yanel.getResourceManager().getResource(httpRequest, httpResponse, realm, path);
            
            return res;
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Save data
     */
    private void save(HttpServletRequest request, HttpServletResponse response, boolean doCheckin) throws ServletException, IOException {
        log.debug("Save data ...");

        Resource resource = getResource(request, response);
        /*
         -> commented because the current default repo implementation does not support versioning yet.
        if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Versionable", "2")) {
            try {
                // check the resource state:
                Identity identity = (Identity) request.getSession().getAttribute("identity");
                String userID = identity.getUsername(); 
                VersionableV2 versionable  = (VersionableV2)resource;
                if (versionable.isCheckedOut()) {
                    String checkoutUserID = versionable.getCheckoutUserID(); 
                    if (!checkoutUserID.equals(userID)) {
                        throw new Exception("Resource is checked out by another user: " + checkoutUserID);
                    }
                } else {
                    throw new Exception("Resource is not checked out.");
                }

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new ServletException(e.getMessage(), e);
            }
        }
        */

        InputStream in = request.getInputStream();
        java.io.ByteArrayOutputStream baos  = new java.io.ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int bytesR;
        while ((bytesR = in.read(buf)) != -1) {
            baos.write(buf, 0, bytesR);
        }

        // Buffer within memory (TODO: Maybe replace with File-buffering ...)
        // http://www-128.ibm.com/developerworks/java/library/j-io1/
        byte[] memBuffer = baos.toByteArray();

            // TODO: Should be delegated to resource type, e.g. <{http://...}xml/>!
            // Check on well-formedness ...
            String contentType = request.getContentType();
            log.debug("Content-Type: " + contentType);
            if (contentType !=  null && (contentType.indexOf("application/xml") >= 0 || contentType.indexOf("application/xhtml+xml") >= 0)) {
                log.info("Check well-formedness ...");
                javax.xml.parsers.DocumentBuilderFactory dbf= javax.xml.parsers.DocumentBuilderFactory.newInstance();
                try {
                    javax.xml.parsers.DocumentBuilder parser = dbf.newDocumentBuilder();

                    // TODO: Get log messages into log4j ...
                    //parser.setErrorHandler(...);

                    // NOTE: DOCTYPE is being resolved/retrieved (e.g. xhtml schema from w3.org) also
                    //       if isValidating is set to false.
                    //       Hence, for performance and network reasons we use a local catalog ...
                    //       Also see http://www.xml.com/pub/a/2004/03/03/catalogs.html
                    //       resp. http://xml.apache.org/commons/components/resolver/
                    // TODO: What about a resolver factory?
                    parser.setEntityResolver(new org.apache.xml.resolver.tools.CatalogResolver());

                    parser.parse(new java.io.ByteArrayInputStream(memBuffer));
                    //org.w3c.dom.Document document = parser.parse(new ByteArrayInputStream(memBuffer));
                } catch (org.xml.sax.SAXException e) {
                    log.warn("Data is not well-formed: "+e.getMessage());

                    StringBuffer sb = new StringBuffer();
                    sb.append("<?xml version=\"1.0\"?>");
                    sb.append("<exception xmlns=\"http://www.wyona.org/neutron/1.0\" type=\"data-not-well-formed\">");
                    sb.append("<message>Data is not well-formed: "+e.getMessage()+"</message>");
                    sb.append("</exception>");
                    response.setContentType("application/xml; charset=" + DEFAULT_ENCODING);
                    response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    PrintWriter w = response.getWriter();
                    w.print(sb);
                    return;
                } catch (Exception e) {
                    log.error(e.getMessage(), e);

                    StringBuffer sb = new StringBuffer();
                    sb.append("<?xml version=\"1.0\"?>");
                    sb.append("<exception xmlns=\"http://www.wyona.org/neutron/1.0\" type=\"neutron\">");
                    //sb.append("<message>" + e.getStackTrace() + "</message>");
                    //sb.append("<message>" + e.getMessage() + "</message>");
                    sb.append("<message>" + e + "</message>");
                    sb.append("</exception>");
                    response.setContentType("application/xml; charset=" + DEFAULT_ENCODING);
                    response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    PrintWriter w = response.getWriter();
                    w.print(sb);
                    return;
                }

                log.info("Data seems to be well-formed :-)");
            } else {
                log.info("No well-formedness check required for content type: " + contentType);
            }

        java.io.ByteArrayInputStream memIn = new java.io.ByteArrayInputStream(memBuffer);


        // IMPORTANT TODO: Use ModifiableV2.write(InputStream in) such that resource can modify data during saving resp. check if getOutputStream is equals null and then use write ....
        OutputStream out = null;
        Resource res = getResource(request, response);
        if (ResourceAttributeHelper.hasAttributeImplemented(res, "Modifiable", "1")) {
            out = ((ModifiableV1) res).getOutputStream(new Path(request.getServletPath()));
            write(memIn, out, request, response);
        } else if (ResourceAttributeHelper.hasAttributeImplemented(res, "Modifiable", "2")) {
            try {
                out = ((ModifiableV2) res).getOutputStream();
                if (out != null) {
                    write(memIn, out, request, response);
                } else {
                    ((ModifiableV2) res).write(memIn);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new ServletException(e.getMessage(), e);
            }
        } else {
            String message = res.getClass().getName() + " is not modifiable (neither V1 nor V2)!";
            log.warn(message);
 
            StringBuffer sb = new StringBuffer();

            // TODO: Differentiate between Neutron based and other clients ...
            sb.append("<?xml version=\"1.0\"?>");
            sb.append("<exception xmlns=\"http://www.wyona.org/neutron/1.0\" type=\"neutron\">");
            sb.append("<message>" + message + "</message>");
            sb.append("</exception>");
            response.setContentType("application/xml; charset=" + DEFAULT_ENCODING);
            response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter w = response.getWriter();
            w.print(sb);
        }
        
        if (doCheckin) {
            if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Versionable", "2")) {
                VersionableV2 versionable  = (VersionableV2)resource;
                try {
                    versionable.checkin("updated");
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new ServletException("Could not check in resource: " + resource.getPath() 
                            + " " + e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Authorize request (and also authenticate for HTTP BASIC)
     */
    private HttpServletResponse doAuthorize(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Role role = null;

        // TODO: Replace hardcoded roles by mapping between roles amd query strings ...
        String value = request.getParameter("yanel.resource.usecase");
        String contentType = request.getContentType();
        String method = request.getMethod();
        if (value != null && value.equals("save")) {
            log.debug("Save data ...");
            role = new Role("write");
        } else if (value != null && value.equals("checkin")) {
            log.debug("Checkin data ...");
            role = new Role("write");
        } else if (value != null && value.equals("checkout")) {
            log.debug("Checkout data ...");
            role = new Role("open");
        } else if (contentType != null && contentType.indexOf("application/atom+xml") >= 0 && (method.equals(METHOD_PUT) || method.equals(METHOD_POST))) {
            // TODO: Is posting atom entries different from a general post (see below)?!
            log.error("DEBUG: Write/Checkin Atom entry ...");
            role = new Role("write");
        } else if (method.equals(METHOD_PUT) || method.equals(METHOD_POST)) {
            log.error("DEBUG: Upload data ...");
            role = new Role("write");
        } else if (method.equals(METHOD_DELETE)) {
            log.error("DEBUG: Delete resource ...");
            role = new Role("delete");
        } else {
            log.debug("Role will be 'view'!");
            role = new Role("view");
        }
        value = request.getParameter("yanel.usecase");
        if (value != null && value.equals("create")) {
            log.debug("Create new resource ...");
            role = new Role("create");
        }

        boolean authorized = false;
        Realm realm;
        String path;
        try {
            realm = map.getRealm(request.getServletPath());
            path = map.getPath(realm, request.getServletPath());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e.getMessage(), e);
        }


        // HTTP BASIC Authorization (For clients such as for instance Sunbird, OpenOffice or cadaver)
        // IMPORT NOTE: BASIC Authentication needs to be checked on every request, because clients often do not support session handling
        String authorization = request.getHeader("Authorization");
        log.debug("Checking for Authorization Header: " + authorization);
        if (authorization != null) {
            if (authorization.toUpperCase().startsWith("BASIC")) {
                log.debug("Using BASIC authorization ...");
                // Get encoded user and password, comes after "BASIC "
                String userpassEncoded = authorization.substring(6);
                // Decode it, using any base 64 decoder
                sun.misc.BASE64Decoder dec = new sun.misc.BASE64Decoder();
                String userpassDecoded = new String(dec.decodeBuffer(userpassEncoded));
                log.debug("Username and Password Decoded: " + userpassDecoded);
                String[] up = userpassDecoded.split(":");
                String username = up[0];
                String password = up[1];
                log.debug("username: " + username + ", password: " + password);
                try {
                if (realm.getIdentityManager().authenticate(username, password)) {
                    authorized = realm.getPolicyManager().authorize(path, new Identity(username, null), new Role("view"));
                    if(authorized) {
                        return null;
                    } else {
                        log.warn("HTTP BASIC Authorization failed for " + username + "!");
                        response.setHeader("WWW-Authenticate", "BASIC realm=\"" + realm.getName() + "\"");
                        response.sendError(response.SC_UNAUTHORIZED);
                        PrintWriter writer = response.getWriter();
                        writer.print("BASIC Authorization Failed!");
                        return response;
                    }
                } else {
                    log.warn("HTTP BASIC Authentication failed for " + username + "!");
                    response.setHeader("WWW-Authenticate", "BASIC realm=\"" + realm.getName() + "\"");
                    response.sendError(response.SC_UNAUTHORIZED);
                    PrintWriter writer = response.getWriter();
                    writer.print("BASIC Authentication Failed!");
                    return response;
                }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new ServletException(e.getMessage(), e);
                }
            } else if (authorization.toUpperCase().startsWith("DIGEST")) {
                log.error("DIGEST is not implemented");
                authorized = false;
                response.sendError(response.SC_UNAUTHORIZED);
                response.setHeader("WWW-Authenticate", "DIGEST realm=\"" + realm.getName() + "\"");
                PrintWriter writer = response.getWriter();
                writer.print("DIGEST is not implemented!");
                return response;
            } else {
                log.warn("No such authorization implemented resp. handled by session based authorization: " + authorization);
                authorized = false;
            }
        }


        // Custom Authorization
        log.debug("Do session based custom authorization");
        //String[] groupnames = {"null", "null"};
        HttpSession session = request.getSession(true);
        Identity identity = (Identity) session.getAttribute(IDENTITY_KEY);
        if (identity == null) {
            log.debug("Identity is WORLD");
            identity = new Identity();
        }
        
        
        //authorized = pm.authorize(new org.wyona.commons.io.Path(request.getServletPath()), identity, role);
        
        try {
            log.debug("Check authorization: realm: " + realm + ", path: " + path + ", identity: " + identity.getUsername() + ", role: " + role.getName());
            authorized = realm.getPolicyManager().authorize(path, identity, role);
            log.debug("Check authorization result: " + authorized);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e.getMessage(), e);
        }


        if(!authorized) {
            log.warn("Access denied: " + getRequestURLQS(request, null, false));

            if(!request.isSecure()) {
                if(sslPort != null) {
                    log.info("Redirect to SSL ...");
                    try {
                    URL url = new URL(getRequestURLQS(request, null, false).toString());
                        url = new URL("https", url.getHost(), new Integer(sslPort).intValue(), url.getFile());
                        response.setHeader("Location", url.toString());
                        // TODO: Yulup has a bug re TEMPORARY_REDIRECT
                        //response.setStatus(javax.servlet.http.HttpServletResponse.SC_TEMPORARY_REDIRECT);
                        response.setStatus(javax.servlet.http.HttpServletResponse.SC_MOVED_PERMANENTLY);
                        return response;
                    } catch (Exception e) {
                        log.error(e);
                    }
                } else {
                    log.warn("SSL does not seem to be configured!");
                }
            }

            // TODO: Shouldn't this be here instead at the beginning of service() ...?
            //if(doAuthenticate(request, response) != null) return response;


            // Check if this is a neutron request, a Sunbird/Calendar request or just a common GET request
            // Also see e-mail about recognizing a WebDAV request: http://lists.w3.org/Archives/Public/w3c-dist-auth/2006AprJun/0064.html
            StringBuffer sb = new StringBuffer("");
            String neutronVersions = request.getHeader("Neutron");
            String clientSupportedAuthScheme = request.getHeader("WWW-Authenticate");
            
            if (clientSupportedAuthScheme != null && clientSupportedAuthScheme.equals("Neutron-Auth")) {
                log.debug("Neutron Versions supported by client: " + neutronVersions);
                log.debug("Authentication Scheme supported by client: " + clientSupportedAuthScheme);
                sb.append("<?xml version=\"1.0\"?>");
                sb.append("<exception xmlns=\"http://www.wyona.org/neutron/1.0\" type=\"authorization\">");
                sb.append("<message>Authorization denied: " + getRequestURLQS(request, null, true) + "</message>");
                sb.append("<authentication>");
                sb.append("<original-request url=\"" + getRequestURLQS(request, null, true) + "\"/>");
                //TODO: Also support https ...
                sb.append("<login url=\"" + getRequestURLQS(request, "yanel.usecase=neutron-auth", true) + "\" method=\"POST\">");
                sb.append("<form>");
                sb.append("<message>Enter username and password for \"" + realm.getName() + "\" at \"" + realm.getMountPoint() + "\"</message>");
                sb.append("<param description=\"Username\" name=\"username\"/>");
                sb.append("<param description=\"Password\" name=\"password\"/>");
                sb.append("</form>");
                sb.append("</login>");
                // NOTE: Needs to be a full URL, because user might switch the server ...
                sb.append("<logout url=\"" + getRequestURLQS(request, "yanel.usecase=logout", true) + "\" realm=\"" + realm.getName() + "\"/>");
                sb.append("</authentication>");
                sb.append("</exception>");

                log.debug("Neutron-Auth response: " + sb);
                response.setContentType("application/xml; charset=" + DEFAULT_ENCODING);
                response.setStatus(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                response.setHeader("WWW-Authenticate", "NEUTRON-AUTH");
                PrintWriter w = response.getWriter();
                w.print(sb);
            } else if (request.getRequestURI().endsWith(".ics")) {
                log.warn("Somebody seems to ask for a Calendar (ICS) ...");
                response.setHeader("WWW-Authenticate", "BASIC realm=\"" + realm.getName() + "\"");
                response.sendError(response.SC_UNAUTHORIZED);
            } else {
                getXHTMLAuthenticationForm(request, response, realm, null);
            }
            return response;
        } else {
            log.info("Access granted: " + getRequestURLQS(request, null, false));
            return null;
        }
    }

    /**
     *
     */
    private String getRequestURLQS(HttpServletRequest request, String addQS, boolean xml) {
        //Realm realm = map.getRealm(new Path(request.getServletPath()));
        try {
            Realm realm = map.getRealm(request.getServletPath());
    
            // TODO: Handle this exception more gracefully!
            if (realm == null) log.error("No realm found for path " +request.getServletPath());
            String proxyHostName = realm.getProxyHostName();
            String proxyPort = realm.getProxyPort();
            String proxyPrefix = realm.getProxyPrefix();
    
            URL url = null;
        
            url = new URL(request.getRequestURL().toString());

            if (proxyHostName != null) {
                url = new URL(url.getProtocol(), proxyHostName, url.getPort(), url.getFile());
            }

            if (proxyPort != null) {
                if (proxyPort.length() > 0) {
                    url = new URL(url.getProtocol(), url.getHost(), new Integer(proxyPort).intValue(), url.getFile());
                } else {
                    url = new URL(url.getProtocol(), url.getHost(), url.getDefaultPort(), url.getFile());
                }
            }

            if (proxyPrefix != null) {
                url = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile().substring(proxyPrefix.length()));
            }

            if(proxyHostName != null || proxyPort != null || proxyPrefix != null) {
                log.debug("Proxy enabled request: " + url);
            }

            String urlQS = url.toString();
            if (request.getQueryString() != null) {
                urlQS = urlQS + "?" + request.getQueryString();
                if (addQS != null) urlQS = urlQS + "&" + addQS;
            } else {
                if (addQS != null) urlQS = urlQS + "?" + addQS;
            }
    
            if (xml) urlQS = urlQS.replaceAll("&", "&amp;");
    
            log.debug("Request: " + urlQS);

            return urlQS;
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    /**
     * Also see https://svn.apache.org/repos/asf/tomcat/container/branches/tc5.0.x/catalina/src/share/org/apache/catalina/servlets/WebdavServlet.java
     * Also maybe interesting http://sourceforge.net/projects/openharmonise
     */
    public void doPropfind(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Resource resource = getResource(request, response);
        //Node node = resource.getRealm().getSitetree().getNode(resource.getPath());
        Node node = sitetree.getNode(resource.getRealm(),resource.getPath());

        String depth = request.getHeader("Depth");

        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");
        sb.append("<multistatus xmlns=\"DAV:\">");
        if (depth.equals("0")) {
            if (node.isCollection()) {
                sb.append("  <response>");
                sb.append("    <href>"+request.getRequestURI()+"</href>");
                sb.append("    <propstat>");
                sb.append("      <prop>");
                sb.append("        <resourcetype><collection/></resourcetype>");
                sb.append("        <getcontenttype>http/unix-directory</getcontenttype>");
                sb.append("      </prop>");
                sb.append("      <status>HTTP/1.1 200 OK</status>");
                sb.append("    </propstat>");
                sb.append("  </response>");
            } else if (node.isResource()) {
                sb.append("  <response>");
                sb.append("    <href>"+request.getRequestURI()+"</href>");
                sb.append("    <propstat>");
                sb.append("      <prop>");
                sb.append("        <resourcetype/>");
                // TODO: Does getcontenttype also be set for resources?
                sb.append("        <getcontenttype>http/unix-directory</getcontenttype>");
                sb.append("      </prop>");
                sb.append("      <status>HTTP/1.1 200 OK</status>");
                sb.append("    </propstat>");
                sb.append("  </response>");
            } else {
                log.error("Neither collection nor resource!");
            }
        } else if (depth.equals("1")) {
            // TODO: Shouldn't one check with isCollection() first?!
            Node[] children = node.getChildren();
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    if (children[i].isCollection()) {
                        sb.append("  <response>\n");
                        sb.append("    <href>" + request.getRequestURI() + "/" + children[i].getName() + "/</href>\n");
                        sb.append("    <propstat>\n");
                        sb.append("      <prop>\n");
                        sb.append("        <displayname>C: " + children[i].getName() + "</displayname>\n");
                        sb.append("        <resourcetype><collection/></resourcetype>\n");
                        sb.append("        <getcontenttype>http/unix-directory</getcontenttype>\n");
                        sb.append("      </prop>\n");
                        sb.append("      <status>HTTP/1.1 200 OK</status>\n");
                        sb.append("    </propstat>\n");
                        sb.append("  </response>\n");
                    } else if(children[i].isResource()) {
                        sb.append("  <response>\n");
                        sb.append("    <href>" + request.getRequestURI() + "/" + children[i].getName() + "?yanel.webdav=edit</href>\n");
                        sb.append("    <propstat>\n");
                        sb.append("      <prop>\n");
                        sb.append("        <displayname>R: " + children[i].getName() + "</displayname>\n");
                        sb.append("        <resourcetype/>\n");
                        sb.append("        <getcontenttype>http/unix-directory</getcontenttype>\n");
                        sb.append("      </prop>\n");
                        sb.append("      <status>HTTP/1.1 200 OK</status>\n");
                        sb.append("    </propstat>\n");
  		        sb.append("  </response>\n");
                    } else {
                        log.error("Neither collection nor resource: " + children[i].getPath());
                    }
                }
            } else {
                log.warn("No children!");
            }
        } else if (depth.equals("infinity")) {
             log.warn("TODO: List children and their children and their children ...");
        } else {
             log.error("No such depth: " + depth);
        }
        sb.append("</multistatus>");

        //response.setStatus(javax.servlet.http.HttpServletResponse.SC_MULTI_STATUS);
        response.setStatus(207, "Multi-Status");

        PrintWriter w = response.getWriter();
        w.print(sb);
    }

    /**
     *
     */
    public void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("DAV", "1");
        // TODO: Is there anything else to do?!
    }

    /**
     * Authentication
     * @return null when authentication successful, otherwise return response
     */
    public HttpServletResponse doAuthenticate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            Realm realm = map.getRealm(request.getServletPath());
            String path = map.getPath(realm, request.getServletPath());
            //Realm realm = map.getRealm(new Path(request.getServletPath()));

            // HTML Form based authentication
            String loginUsername = request.getParameter("yanel.login.username");
            if(loginUsername != null) {
                HttpSession session = request.getSession(true);
                try {
                    if (realm.getIdentityManager().authenticate(loginUsername, request.getParameter("yanel.login.password"))) {
                        log.debug("Realm: " + realm);
                        session.setAttribute(IDENTITY_KEY, new Identity(loginUsername, null));
                        return null;
                    } else {
                        log.warn("Login failed: " + loginUsername);
                        getXHTMLAuthenticationForm(request, response, realm, "Login failed!");
                        return response;
                    }
                } catch (Exception e) {
                    log.warn("Login failed: " + loginUsername + " " + e);
                    getXHTMLAuthenticationForm(request, response, realm, "Login failed!");
                    return response;
                }
            }

            // Neutron-Auth based authentication
            String yanelUsecase = request.getParameter("yanel.usecase");
            if(yanelUsecase != null && yanelUsecase.equals("neutron-auth")) {
                log.debug("Neutron Authentication ...");

                String username = null;
                String password = null;
                String originalRequest = null;
                DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
                try {
                    Configuration config = builder.build(request.getInputStream());

                    Configuration originalRequestConfig = config.getChild("original-request");
                    originalRequest = originalRequestConfig.getAttribute("url", null);

                    Configuration[] paramConfig = config.getChildren("param");
                    for (int i = 0; i < paramConfig.length; i++) {
                        String paramName = paramConfig[i].getAttribute("name", null);
                        if (paramName != null) {
                            if (paramName.equals("username")) {
                                username = paramConfig[i].getValue();
                            } else if (paramName.equals("password")) {
                                password = paramConfig[i].getValue();
                            }
                        }
                    }
                } catch(Exception e) {
                    log.warn(e);
                }

                log.debug("Username: " + username);

                if (username != null) {
                    HttpSession session = request.getSession(true);
                    log.debug("Realm ID: " + realm.getID());
                    if (realm.getIdentityManager().authenticate(username, password)) {
                        log.info("Authentication successful: " + username);
                        session.setAttribute(IDENTITY_KEY, new Identity(username, null));

                        // TODO: send some XML content, e.g. <authentication-successful/>
                        response.setContentType("text/plain; charset=" + DEFAULT_ENCODING);
                        response.setStatus(response.SC_OK);

                        PrintWriter writer = response.getWriter();
                        writer.print("Neutron Authentication Successful!");
                        return response;
                    } else {
                        log.warn("Neutron Authentication failed: " + username);

                        // TODO: Refactor this code with the one from doAuthenticate ...
                        log.debug("Original Request: " + originalRequest);

                        StringBuffer sb = new StringBuffer("");
                        sb.append("<?xml version=\"1.0\"?>");
                        sb.append("<exception xmlns=\"http://www.wyona.org/neutron/1.0\" type=\"authentication\">");
                        sb.append("<message>Authentication failed!</message>");
                        sb.append("<authentication>");
                        // TODO: ...
                        sb.append("<original-request url=\"" + originalRequest + "\"/>");
                        //sb.append("<original-request url=\"" + getRequestURLQS(request, null, true) + "\"/>");
                        //TODO: Also support https ...
                        // TODO: ...
                        sb.append("<login url=\"" + originalRequest + "&amp;yanel.usecase=neutron-auth" + "\" method=\"POST\">");
                        //sb.append("<login url=\"" + getRequestURLQS(request, "yanel.usecase=neutron-auth", true) + "\" method=\"POST\">");
                        sb.append("<form>");
                        sb.append("<message>Enter username and password for \"" + realm.getName() + "\" at \"" + realm.getMountPoint() + "\"</message>");
                        sb.append("<param description=\"Username\" name=\"username\"/>");
                        sb.append("<param description=\"Password\" name=\"password\"/>");
                        sb.append("</form>");
                        sb.append("</login>");
                        // NOTE: Needs to be a full URL, because user might switch the server ...
                        // TODO: ...
                        sb.append("<logout url=\"" + originalRequest + "&amp;yanel.usecase=logout" + "\" realm=\"" + realm.getName() + "\"/>");
                        sb.append("</authentication>");
                        sb.append("</exception>");

                        log.debug("Neutron-Auth response: " + sb);

                        response.setContentType("application/xml; charset=" + DEFAULT_ENCODING);
                        response.setStatus(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                        response.setHeader("WWW-Authenticate", "NEUTRON-AUTH");

                        PrintWriter w = response.getWriter();
                        w.print(sb);
                        return response;
                    }
                } else {
                    // TODO: Refactor resp. reuse response from above ...
                    log.warn("Neutron Authentication failed because username is NULL!");

                    StringBuffer sb = new StringBuffer("");
                    sb.append("<?xml version=\"1.0\"?>");
                    sb.append("<exception xmlns=\"http://www.wyona.org/neutron/1.0\" type=\"authentication\">");
                    sb.append("<message>Authentication failed because no username was sent!</message>");
                    sb.append("<authentication>");
                    // TODO: ...
                    sb.append("<original-request url=\"" + originalRequest + "\"/>");
                    //sb.append("<original-request url=\"" + getRequestURLQS(request, null, true) + "\"/>");
                    //TODO: Also support https ...
                    // TODO: ...
                    sb.append("<login url=\"" + originalRequest + "&amp;yanel.usecase=neutron-auth" + "\" method=\"POST\">");
                    //sb.append("<login url=\"" + getRequestURLQS(request, "yanel.usecase=neutron-auth", true) + "\" method=\"POST\">");
                    sb.append("<form>");
                    sb.append("<message>Enter username and password for \"" + realm.getName() + "\" at \"" + realm.getMountPoint() + "\"</message>");
                    sb.append("<param description=\"Username\" name=\"username\"/>");
                    sb.append("<param description=\"Password\" name=\"password\"/>");
                    sb.append("</form>");
                    sb.append("</login>");
                    // NOTE: Needs to be a full URL, because user might switch the server ...
                    // TODO: ...
                    sb.append("<logout url=\"" + originalRequest + "&amp;yanel.usecase=logout" + "\" realm=\"" + realm.getName() + "\"/>");
                    sb.append("</authentication>");
                    sb.append("</exception>");

                    response.setContentType("application/xml; charset=" + DEFAULT_ENCODING);
                    response.setStatus(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                    response.setHeader("WWW-Authenticate", "NEUTRON-AUTH");

                    PrintWriter writer = response.getWriter();
                    writer.print(sb);
                    return response;
                }
            } else {
                log.debug("Neutron Authentication successful.");
                return null;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e.getMessage(), e);
        }
    }

    /**
     *
     */
    public HttpServletResponse doLogout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.info("Logout from Yanel ...");
        HttpSession session = request.getSession(true);
        session.setAttribute(IDENTITY_KEY, null);
        String clientSupportedAuthScheme = request.getHeader("WWW-Authenticate");
        if (clientSupportedAuthScheme != null && clientSupportedAuthScheme.equals("Neutron-Auth")) {
            // TODO: send some XML content, e.g. <logout-successful/>
            response.setContentType("text/plain; charset=" + DEFAULT_ENCODING);
            response.setStatus(response.SC_OK);
            PrintWriter writer = response.getWriter();
            writer.print("Neutron Logout Successful!");
            return response;
        }
        return null;
    }

    /**
     * Patches the mimetype of the Content-Type response field because
     * Microsoft Internet Explorer does not understand application/xhtml+xml
     * See http://en.wikipedia.org/wiki/Criticisms_of_Internet_Explorer#XHTML
     */
    public String patchMimeType(String mimeType, HttpServletRequest request) throws ServletException, IOException {
        String httpAcceptMediaTypes = request.getHeader("Accept");
        log.debug("HTTP Accept Media Types: " + httpAcceptMediaTypes);
        if (mimeType != null && mimeType.equals("application/xhtml+xml") && httpAcceptMediaTypes != null && httpAcceptMediaTypes.indexOf("application/xhtml+xml") < 0) {
            log.info("Patch contentType with text/html because client (" + request.getHeader("User-Agent") + ") does not seem to understand application/xhtml+xml");
            return "text/html";
        }
        return mimeType;
    }

    /**
     * Intercept InputStream and log content ...
     */
    public InputStream intercept(InputStream in) throws IOException {
        java.io.ByteArrayOutputStream baos  = new java.io.ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int bytesR;
        while ((bytesR = in.read(buf)) != -1) {
            baos.write(buf, 0, bytesR);
        }

        // Buffer within memory (TODO: Maybe replace with File-buffering ...)
        // http://www-128.ibm.com/developerworks/java/library/j-io1/
        byte[] memBuffer = baos.toByteArray();

        log.error("DEBUG: InputStream: " + baos);

        return new java.io.ByteArrayInputStream(memBuffer);
    }

    /**
     *
     */
    private void setYanelOutput(HttpServletRequest request, HttpServletResponse response, Document doc) throws ServletException {
        try {
            String yanelFormat = request.getParameter("yanel.format");
            if(yanelFormat != null && yanelFormat.equals("xml")) {
                response.setContentType("application/xml; charset=" + DEFAULT_ENCODING);
                OutputStream out = response.getOutputStream();
                javax.xml.transform.TransformerFactory.newInstance().newTransformer().transform(new javax.xml.transform.dom.DOMSource(doc), new javax.xml.transform.stream.StreamResult(out));
                out.close();
            } else {
                response.setContentType("application/xhtml+xml; charset=" + DEFAULT_ENCODING);
                Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xsltInfoAndException));
                transformer.transform(new javax.xml.transform.dom.DOMSource(doc), new javax.xml.transform.stream.StreamResult(response.getWriter()));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e.getMessage());
        }
    }

    /**
     * Custom XHTML Form for authentication
     */
    public void getXHTMLAuthenticationForm(HttpServletRequest request, HttpServletResponse response, Realm realm, String message) throws ServletException, IOException {
        
        org.w3c.dom.Document doc = null;
        javax.xml.parsers.DocumentBuilderFactory dbf= javax.xml.parsers.DocumentBuilderFactory.newInstance();
        try {
            javax.xml.parsers.DocumentBuilder parser = dbf.newDocumentBuilder();
            org.w3c.dom.DOMImplementation impl = parser.getDOMImplementation();
            org.w3c.dom.DocumentType doctype = null;
            doc = impl.createDocument(NAMESPACE, "yanel", doctype);            
            
            Element rootElement = doc.getDocumentElement();
            
            if (message != null) {
                Element messageElement = (Element) rootElement.appendChild(doc.createElement("message"));
                messageElement.appendChild(doc.createTextNode(message)); 
            }
            
            Element requestElement = (Element) rootElement.appendChild(doc.createElement("request"));
            requestElement.setAttribute("urlqs", getRequestURLQS(request, null, true));
            
            Element realmElement = (Element) rootElement.appendChild(doc.createElement("realm"));
            realmElement.setAttribute("name", realm.getName());
            realmElement.setAttribute("mount-point", realm.getMountPoint().toString());  
            
            Element sslElement = (Element) rootElement.appendChild(doc.createElement("ssl"));            
            if(sslPort != null) {
        	sslElement.setAttribute("status", "ON");   
            } else {
        	sslElement.setAttribute("status", "OFF");
            }
            
            String yanelFormat = request.getParameter("yanel.format");
            if(yanelFormat != null && yanelFormat.equals("xml")) {
                response.setContentType("application/xml; charset=" + DEFAULT_ENCODING);
                OutputStream out = response.getOutputStream();
                javax.xml.transform.TransformerFactory.newInstance().newTransformer().transform(new javax.xml.transform.dom.DOMSource(doc), new javax.xml.transform.stream.StreamResult(out));
                out.close();
            } else {
                response.setContentType("application/xhtml+xml; charset=" + DEFAULT_ENCODING);
                response.setStatus(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);            
                Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xsltLoginScreen));            
                transformer.transform(new javax.xml.transform.dom.DOMSource(doc), 
                        new javax.xml.transform.stream.StreamResult(response.getWriter()));
            }

            
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e.getMessage());
        }        
    }

    /**
     * Write to output stream of modifiable resource
     */
    private void write(InputStream in, OutputStream out, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (out != null) {
            log.debug("Content-Type: " + request.getContentType());
            // TODO: Compare mime-type from response with mime-type of resource
            //if (contentType.equals("text/xml")) { ... }

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
            out.close();

            StringBuffer sb = new StringBuffer();
            sb.append("<?xml version=\"1.0\"?>");
            sb.append("<html>");
            sb.append("<body>");
            sb.append("<p>Data has been saved ...</p>");
            sb.append("</body>");
            sb.append("</html>");


            response.setStatus(javax.servlet.http.HttpServletResponse.SC_OK);
            response.setContentType("application/xhtml+xml; charset=" + DEFAULT_ENCODING);
            PrintWriter w = response.getWriter();
            w.print(sb);

            log.info("Data has been saved ...");
            return;
        } else {
            log.error("OutputStream is null!");
 
            StringBuffer sb = new StringBuffer();
            sb.append("<?xml version=\"1.0\"?>");
            sb.append("<html>");
            sb.append("<body>");
            sb.append("<p>Exception: OutputStream is null!</p>");
            sb.append("</body>");
            sb.append("</html>");
            PrintWriter w = response.getWriter();
            w.print(sb);
            response.setContentType("application/xhtml+xml; charset=" + DEFAULT_ENCODING);
            response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
    }
}
