/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources.updatefinder;

import org.apache.log4j.Category;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.apache.xml.serializer.Serializer;
import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceConfiguration;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import javax.servlet.http.HttpServletRequest;

import org.wyona.yanel.core.serialization.SerializerFactory;
import org.wyona.yanel.core.source.ResourceResolver;
import org.wyona.yanel.core.transformation.I18nTransformer;
import org.wyona.yanel.core.transformation.I18nTransformer2;
import org.wyona.yanel.core.transformation.XIncludeTransformer;
import org.wyona.yanel.core.util.PathUtil;
import org.wyona.yanel.impl.resources.updatefinder.utils.*;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;

import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.net.URL;
import java.io.InputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.hp.hpl.jena.rdf.model.*;

/**
 * 
 */
public class UpdateFinder extends Resource implements ViewableV2 {

    private static Category log = Category.getInstance(UpdateFinder.class);
    private String defaultLanguage;
    private String language = null;

    /**
     * 
     */
    public UpdateFinder() {
    }

    /**
     * 
     */
    public boolean exists() {
        return true;
    }

    /**
     * 
     */
    public long getSize() {
        return -1;
    }

    /**
     * 
     */
    public String getMimeType(String viewId) {
        if (viewId != null && viewId.equals("source"))
            return "application/xml";
        return "application/xhtml+xml";
    }

    /**
     * 
     */
    public View getView(String viewId) {
        View view = new View();
        String mimeType = getMimeType(viewId);
        view.setMimeType(mimeType);

        try {
            org.wyona.yarep.core.Repository repo = getRealm().getRepository();

            if (viewId != null && viewId.equals("source")) {
                view.setInputStream(new java.io.StringBufferInputStream(getScreen()));
                view.setMimeType("application/xml");
                return view;
            }

            String[] xsltPath = getXSLTPath(getPath());
            if (xsltPath != null) {

                // create reader:
                XMLReader xmlReader = XMLReaderFactory.createXMLReader();
                CatalogResolver catalogResolver = new CatalogResolver();
                xmlReader.setEntityResolver(catalogResolver);

                // create xslt transformer:
                SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory.newInstance();

                TransformerHandler[] xsltHandlers = new TransformerHandler[xsltPath.length];
                for (int i = 0; i < xsltPath.length; i++) {
                    xsltHandlers[i] = tf.newTransformerHandler(new StreamSource(repo.getNode(xsltPath[i])
                            .getInputStream()));
                    xsltHandlers[i].getTransformer().setParameter("yanel.path.name",
                            PathUtil.getName(getPath()));
                    xsltHandlers[i].getTransformer().setParameter("yanel.path", getPath());
                    xsltHandlers[i].getTransformer().setParameter("yanel.back2context",
                            PathUtil.backToContext(realm, getPath()));
                    xsltHandlers[i].getTransformer().setParameter("yarep.back2realm",
                            PathUtil.backToRealm(getPath()));

                    xsltHandlers[i].getTransformer().setParameter("language",
                            getRequestedLanguage());
                }

                // create i18n transformer:
                I18nTransformer2 i18nTransformer = new I18nTransformer2("global",
                        getRequestedLanguage(),
                        getRealm().getDefaultLanguage());
                i18nTransformer.setEntityResolver(catalogResolver);

                // create xinclude transformer:
                XIncludeTransformer xIncludeTransformer = new XIncludeTransformer();
                ResourceResolver resolver = new ResourceResolver(this);
                xIncludeTransformer.setResolver(resolver);

                // create serializer:
                Serializer serializer = SerializerFactory.getSerializer(SerializerFactory.XHTML_STRICT);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                // chain everything together (create a pipeline):
                xmlReader.setContentHandler(xsltHandlers[0]);
                for (int i = 0; i < xsltHandlers.length - 1; i++) {
                    xsltHandlers[i].setResult(new SAXResult(xsltHandlers[i + 1]));
                }
                xsltHandlers[xsltHandlers.length - 1].setResult(new SAXResult(xIncludeTransformer));
                xIncludeTransformer.setResult(new SAXResult(i18nTransformer));
                i18nTransformer.setResult(new SAXResult(serializer.asContentHandler()));
                serializer.setOutputStream(baos);

                // execute pipeline:
                xmlReader.parse(new InputSource(new java.io.StringBufferInputStream(getScreen())));

                // write result into view:
                view.setInputStream(new ByteArrayInputStream(baos.toByteArray()));
                return view;
            } else {
                log.debug("Mime-Type: " + mimeType);
                view.setInputStream(new java.io.StringBufferInputStream(getScreen()));
                return view;
            }
        } catch (Exception e) {
            log.error(e + " (" + getPath() + ", " + getRealm() + ")", e);
        }

        view.setInputStream(new java.io.StringBufferInputStream(getScreen()));
        return view;
    }

    /**
     * 
     */
    public ViewDescriptor[] getViewDescriptors() {
        ViewDescriptor[] vd = new ViewDescriptor[2];
        vd[0] = new ViewDescriptor("default");
        vd[0].setMimeType(getMimeType(null));
        vd[1] = new ViewDescriptor("source");
        vd[1].setMimeType(getMimeType("source"));
        return vd;
    }

    /**
     * 
     */
    private String getScreen() {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        sb.append("<head><title>create resource</title>");
        sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\""
                + PathUtil.getResourcesHtdocsPath(this) + "css/resource-creator.css\"/>");
        sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\""
                + PathUtil.getGlobalHtdocsPath(this) + "yanel-css/progressBar.css\"/>");
        sb.append("<script src=\"" + PathUtil.getGlobalHtdocsPath(this)
                + "yanel-js/prototype.js\" type=\"text/javascript\"></script>");
        sb.append("<script src=\"" + PathUtil.getGlobalHtdocsPath(this)
                + "yanel-js/progressBar.js\" type=\"text/javascript\"></script>");
        sb.append("<script src=\"" + PathUtil.getResourcesHtdocsPath(this)
                + "js/ajaxlookup.js\" type=\"text/javascript\"></script>");
        sb.append("</head>");
        sb.append("<body>");

        HttpServletRequest request = getRequest();
        Enumeration parameters = request.getParameterNames();
        if (!parameters.hasMoreElements()) {
            plainRequest(sb);
        } else {
            if (request.getParameter("save-as") != null) {
                plainRequest(sb);
            } else if (request.getParameter("save") != null) {
                plainRequest(sb);
            } else {
                log.info("Fallback ...");
                plainRequest(sb);
            }
        }

        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }

    private void plainRequest(StringBuffer sb) {
        
        UpdateInfo updateInfo = null;
        InstallInfo installInfo = getInstallInfo(sb);
        // install.rdf
//        InstallInfo installInfo = null;
//        try {
//            installInfo = new InstallInfo(request);
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            sb.append("<p>Exception: " + e.getMessage() + "</p>");
//            return;
//        }


        if (!getInstallInfo(sb).getInstalltype().equals("bin-snapshot")) {

            sb.append("<p>");
            sb.append("This Yanel was not installed from binary. You can only use the updater if you installed yanel from binary. Please use svn up, build.sh");
            sb.append("</p>");

            // transformer = TransformerFactory.newInstance().newTransformer();

        } else {
            updateInfo = getUpdateInfo(sb);
//            try {
//                URL UpdateRdfUrl = new URL(installInfo.getUpdateURL());
//                InputStream updateRdfIn = UpdateRdfUrl.openStream();
//                updateInfo = new UpdateInfo(updateRdfIn, installInfo);
//            } catch (Exception e) {
//                log.error(e.getMessage(), e);
//                sb.append("<p>");
//                sb.append("Yanel could not get the Update information! " + e);
//                sb.append("</p>");
//            }
        }

        if (updateInfo != null) {
            
            if (request.getParameter("update") != null
                    && request.getParameter("update").equals("update")) {
                WarFetcher warFetcher = null;
                try {
                    String destDir = request.getSession().getServletContext().getRealPath(".") + File.separator + "..";
                    warFetcher = new WarFetcher(request, request.getParameter("updatelink"), destDir);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    sb.append("<p>Exception: " + e.getMessage() + "</p>");
                    return;
                }

                
                try {
                    HashMap versionDetails = updateInfo.getUpdateVersionDetail("updateLink", request.getParameter("updatelink"));
                    String version = (String) versionDetails.get("version");
                    String revision = (String) versionDetails.get("revision");
                    String id = (String) versionDetails.get("id");
                    
                    warFetcher.fetch();
                    
                    TomcatContextHandler tomcatContextHandler = new TomcatContextHandler(request);
                    tomcatContextHandler.setContext("updater", id + "-v-" + version + "-r-" + revision);
                    String pathToUpdater = "http://" + request.getServerName() + ":" + request.getServerPort() +"/updater/";
                    
                    sb.append("<p>");
                    sb.append("Update done.");
                    sb.append("<a href=\"" +pathToUpdater + "\">");
                    sb.append("go to the Updater!");
                    sb.append("</a>");
                    sb.append("</p>");
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    sb.append("<p>Update failed. Exception: " + e.getMessage() + "</p>");
                }
                

                
            } else {
                sb.append("<p>");
                sb.append("This are the updates which you can get:");
                sb.append("</p>");
                sb.append("<ul>");
                for (int i = 0; i < updateInfo.getUpdateVersions().size(); i++) {
                    HashMap versionDetails = (HashMap) updateInfo.getUpdateVersions().get(i);
                    if (versionDetails.get("version") != installInfo.getVersion()) {
                        sb.append("<li>"
                                + versionDetails.get("title")
                                + "<ul>"
                                + "<li>Version: "
                                + versionDetails.get("version")
                                + "</li>"
                                + "<li>Type: "
                                + versionDetails.get("type")
                                + "</li>"
                                + "<li> ChangeLog: "
                                + versionDetails.get("changeLog")
                                + "</li>"
                                + "<li> <form method=\"post\"><input type=\"submit\" name=\"update\" value=\"update\"></input><input type=\"hidden\" name=\"updatelink\" value=\""
                                + versionDetails.get("updateLink") + "\"/></form></li>"
                                + "</ul></li>");
                    }
                }
                sb.append("</ul>");
            }
        }
    }

    private InstallInfo getInstallInfo (StringBuffer sb) {
        InstallInfo installInfo = null;
        try {
            return installInfo = new InstallInfo(request);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            sb.append("<p>Exception: " + e.getMessage() + "</p>");
            return null;
        }
    }
    
    private UpdateInfo getUpdateInfo (StringBuffer sb) {
        UpdateInfo updateInfo = null;
        try {
            URL UpdateRdfUrl = new URL(getInstallInfo(sb).getUpdateURL());
            InputStream updateRdfIn = UpdateRdfUrl.openStream();
            return updateInfo = new UpdateInfo(updateRdfIn, getInstallInfo(sb));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            sb.append("<p>");
            sb.append("Yanel could not get the Update information! " + e);
            sb.append("</p>");
            return null;
        }
    }
    
//    /**
//     * Get property value from resource configuration
//     */
//    private String getResourceProperty(String name) throws Exception {
//        ResourceConfiguration rc = getConfiguration();
//        if (rc != null)
//            return rc.getProperty(name);
//        return getRTI().getProperty(name);
//    }

    /**
     * Get XSLT path
     */
    private String[] getXSLTPath(String path) throws Exception {
        String[] xsltPath = getResourceConfigProperties("xslt");
        if (xsltPath != null)
            return xsltPath;
        log.info("No XSLT Path within: " + path);
        return null;
    }
}
