/*
 * Copyright 2007 Wyona
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

import org.wyona.yanel.core.serialization.SerializerFactory;
import org.wyona.yanel.core.source.ResourceResolver;
import org.wyona.yanel.core.transformation.I18nTransformer;
import org.wyona.yanel.core.transformation.I18nTransformer2;
import org.wyona.yanel.core.transformation.XIncludeTransformer;
import org.wyona.yanel.core.util.PathUtil;
import org.wyona.yanel.impl.resources.updatefinder.utils.*;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Collections;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import javax.servlet.http.HttpServletRequest;

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
        if (viewId != null && viewId.equals("source")) return "application/xml";
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
     * Generate screen
     */
    private String getScreen() {
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        sb.append("<head><title>Yanel Updater</title>");
        //sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + PathUtil.getResourcesHtdocsPath(this) + "css/resource-creator.css\"/>");
        sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\""
                + PathUtil.getGlobalHtdocsPath(this) + "yanel-css/progressBar.css\"/>");
        sb.append("<script src=\"" + PathUtil.getGlobalHtdocsPath(this)
                + "yanel-js/prototype.js\" type=\"text/javascript\"></script>");
        sb.append("<script src=\"" + PathUtil.getGlobalHtdocsPath(this)
                + "yanel-js/progressBar.js\" type=\"text/javascript\"></script>");
        sb.append("<script src=\"" + PathUtil.getGlobalHtdocsPath(this)
                + "yanel-js/sorttable.js\" type=\"text/javascript\"></script>");
        //sb.append("<script src=\"" + PathUtil.getResourcesHtdocsPath(this) + "js/ajaxlookup.js\" type=\"text/javascript\"></script>");

        if (request.getParameter("updateconfirmed") != null && request.getParameter("updateconfirmed").equals("updateconfirmed")) {    
            try {
                Map bestUpdater = getBestUpdater();
                String htmlHeadContent = "<meta http-equiv=\"refresh\" content=\"10; URL=" + "http://" + request.getServerName() + ":" + request.getServerPort() + "/" + bestUpdater.get("id") + "-v-" + bestUpdater.get("version") + "-r-" + bestUpdater.get("revision") + "/" + "?updatelink=" + request.getParameter("updatelink") + "&amp;requestingwebapp=" + request.getParameter("requestingwebapp") + "\"/>";
                sb.append(htmlHeadContent);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        sb.append("</head>");
        sb.append("<body>");
        sb.append("<h1>Yanel Updater</h1>");

        StringBuffer body = new StringBuffer();
        Enumeration parameters = request.getParameterNames();
        if (!parameters.hasMoreElements()) {
            body = plainRequest();
        } else {
            if (request.getParameter("save-as") != null) {
                body = plainRequest();
            } else if (request.getParameter("update") != null && request.getParameter("update").equals("update")) {
                body = getUpdateConfirmScreen();
            } else if (request.getParameter("updateconfirmed") != null && request.getParameter("updateconfirmed").equals("updateconfirmed")) {    
                body = getUpdateScreen();
            } else {
                log.info("Fallback ...");
                body = plainRequest();
            }
        }
        sb.append(body);

        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }

    /**
     *
     */
    private StringBuffer plainRequest() {

        InstallInfo installInfo = null;
        try {
            installInfo = getInstallInfo();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new StringBuffer("<p>Could not get install information. " + e.getMessage() + "</p>");
        }

        UpdateInfo updateInfo = null;
        try {
            updateInfo = getUpdateInfo();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new StringBuffer("<p>Could not get update information. " + e.getMessage() + "</p>");
        }

        if (!installInfo.getInstalltype().equals("bin-snapshot")) {
            return new StringBuffer("<p>This Yanel was not installed from binary. You can only use the updater if you installed yanel from binary. Please use Subversion or get another source snapshot.</p><p>NOTE: In order to enhance the Yanel Updater resource developers might want to modify " + installInfo.getInstallRdfFilename() + " by replacing the installtype \"source\" with \"bin-snapshot\" and also customize the version and revision!</p>");
        }

        String idVersionRevisionCurent = installInfo.getId() + "-v-" + installInfo.getVersion() + "-r-" + installInfo.getRevision();

        StringBuffer htmlBodyContent = new StringBuffer();
        // show installed version
        htmlBodyContent.append("<p>");
        htmlBodyContent.append("Your installed yanel is: " + installInfo.getId() + "-v-" + installInfo.getVersion() + "-r-" + installInfo.getRevision());
        htmlBodyContent.append("</p>");

        // TODO implement getBestYanelWebapp() to get all yanel-webapp version which has an
        // yanel-updater which fits the targetRevision requirement of the current yanel and is not
        // allready installed.

        ArrayList updatebleYanelVersions = null;
        try {
            updatebleYanelVersions = getSuiteableYanelUpdates();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            htmlBodyContent.append("<p>Could not get Updates. " + e.getMessage() + "</p>");
        }

        if (updatebleYanelVersions == null) {
            htmlBodyContent.append("<p>");
            htmlBodyContent.append("No updates found.");
            htmlBodyContent.append("</p>");
        } else {
        
        HashMap newestYanel = (HashMap) updatebleYanelVersions.get(updatebleYanelVersions.size() - 1);
        String newestYanelName = (String) newestYanel.get("id") + "-v-"
                + (String) newestYanel.get("version") + "-r-"
                + (String) newestYanel.get("revision");
        if (newestYanelName.equals(idVersionRevisionCurent)) {
            htmlBodyContent.append("<p>");
            htmlBodyContent.append("Your yanel is already the newest version.");
            htmlBodyContent.append("</p>");
        } else {
            htmlBodyContent.append("<p>");
            htmlBodyContent.append("Newest yanel is: " + newestYanelName);
            htmlBodyContent.append("<form method=\"post\"><input type=\"submit\" name=\"button\" value=\"update\"></input><input type=\"hidden\" name=\"update\" value=\"update\"></input><input type=\"hidden\" name=\"updatelink\" value=\"" + newestYanel.get("updateLink") + "\"/></form>");
            htmlBodyContent.append("</p>");
        }

        htmlBodyContent.append("<p>");
        htmlBodyContent.append("All versions you can get:");
        htmlBodyContent.append("</p>");
        htmlBodyContent.append("<ul>");

        for (int i = 0; i < updatebleYanelVersions.size(); i++) {
            HashMap versionDetails = (HashMap) updatebleYanelVersions.get(i);
            String idVersionRevisionItem = (String) versionDetails.get("id") + "-v-"
                    + (String) versionDetails.get("version") + "-r-"
                    + (String) versionDetails.get("revision");

                htmlBodyContent.append("<li>"
                        + versionDetails.get("title")
                        + "<ul>"
                        + "<li>Version: "
                        + idVersionRevisionItem
                        + "</li>"
                        + "<li>Type: "
                        + versionDetails.get("type")
                        + "</li>"
                        + "<li> ChangeLog: "
                        + versionDetails.get("changeLog")
                        + "</li>"
                        + "<li> <form method=\"post\"><input type=\"submit\" name=\"button\" value=\"update\"></input><input type=\"hidden\" name=\"update\" value=\"update\"/><input type=\"hidden\" name=\"updatelink\" value=\""
                        + versionDetails.get("updateLink") + "\"/></form></li>" + "</ul></li>");
        }
        htmlBodyContent.append("</ul>");
        }
        
        // show installed versions
        try {
            htmlBodyContent.append("<p>");
            htmlBodyContent.append("Installed versions:");
            htmlBodyContent.append("</p>");
            TomcatContextHandler tomcatContextHandler = new TomcatContextHandler(request);
            Map contextAndWebapp = tomcatContextHandler.getContextAndWebapp();

            htmlBodyContent.append("<table class=\"sortable\">");
            htmlBodyContent.append("<thead>");
            htmlBodyContent.append("<tr><th>Context</th><th>Webapp</th></tr>");
            htmlBodyContent.append("</thead>");
            htmlBodyContent.append("<tbody>");
            Iterator iterator = contextAndWebapp.keySet().iterator();

            while (iterator.hasNext()) {
                String context = (String) iterator.next();
                String webapp = (String) contextAndWebapp.get(context);
                htmlBodyContent.append("<tr><td><a href=\"" + "http://" + request.getServerName()
                        + ":" + request.getServerPort() + "/" + context.replaceAll("/", "") + "\">"
                        + context + "</a></td><td>" + webapp + "</td></tr>");
            }
            htmlBodyContent.append("</tbody>");
            htmlBodyContent.append("</table>");

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            htmlBodyContent.append("<p>Could not get installed versions. " + e.getMessage() + "</p>");
            //return;
        }
        return htmlBodyContent;
    }

    /**
     *
     */
    private StringBuffer getUpdateConfirmScreen() {
        StringBuffer htmlBodyContent = new StringBuffer();
        try {
            UpdateInfo updateInfo = getUpdateInfo();
            InstallInfo installInfo = getInstallInfo();
            Map bestUpdater = getBestUpdater();
            TomcatContextHandler tomcatContextHandler = new TomcatContextHandler(request);
            
            HashMap versionDetails = updateInfo.getUpdateVersionDetail("updateLink", request.getParameter("updatelink"));
            String version = (String) versionDetails.get("version");
            String revision = (String) versionDetails.get("revision");
            String id = (String) versionDetails.get("id");
            
            if (tomcatContextHandler.getWebappOfContext(bestUpdater.get("id") + "-v-" + bestUpdater.get("version") + "-r-" + bestUpdater.get("revision")) != null) {
                htmlBodyContent.append("<p>Yanel will redirect you to the update-manager which will download and install " + id + "-v-" + version + "-r-" + revision  + "</p>");
                htmlBodyContent.append("<p>Do you want to continue?</p>");
                htmlBodyContent.append("<p>");
                htmlBodyContent.append("<form method=\"post\" action=\"" + "http://" + request.getServerName() + ":" + request.getServerPort() + "/" + bestUpdater.get("id") + "-v-" + bestUpdater.get("version") + "-r-" + bestUpdater.get("revision") + "/\">");
                htmlBodyContent.append("<input type=\"submit\" name=\"button\" value=\"YES\"/>");
                htmlBodyContent.append("<input type=\"hidden\" name=\"updateconfirmed\" value=\"updateconfirmed\"/>");
                htmlBodyContent.append("<input type=\"hidden\" name=\"updatelink\" value=\"" + request.getParameter("updatelink") + "\"/>");
                htmlBodyContent.append("<input type=\"hidden\" name=\"requestingwebapp\" value=\"" + installInfo.getWebaName() + "\"/>");
                //TODO here it should ask for a password which shoudl be set in the new updater
                htmlBodyContent.append("</form>");
                htmlBodyContent.append("<form method=\"post\">");
                htmlBodyContent.append("<input type=\"submit\" name=\"button\" value=\"Cancel\"></input>");
                htmlBodyContent.append("</form>");
                htmlBodyContent.append("</p>");
            } else {
                htmlBodyContent.append("<p>Yanel will download the update-manager (" + bestUpdater.get("id") + "-v-" + bestUpdater.get("version") + "-r-" + bestUpdater.get("revision") + ") which will download and install " + id + "-v-" + version + "-r-" + revision  + "</p>");
                htmlBodyContent.append("<p>Do you want to continue?</p>");
                htmlBodyContent.append("<p>");
                htmlBodyContent.append("<form method=\"post\">");
                htmlBodyContent.append("<input type=\"submit\" name=\"button\" value=\"YES\"/>");
                htmlBodyContent.append("<input type=\"hidden\" name=\"updateconfirmed\" value=\"updateconfirmed\"/>");
                htmlBodyContent.append("<input type=\"hidden\" name=\"updatelink\" value=\"" + request.getParameter("updatelink") + "\"/>");
                htmlBodyContent.append("<input type=\"hidden\" name=\"requestingwebapp\" value=\"" + installInfo.getWebaName() + "\"/>");
                htmlBodyContent.append("</form>");
                htmlBodyContent.append("<form method=\"post\">");
                htmlBodyContent.append("<input type=\"submit\" name=\"button\" value=\"Cancel\"></input>");
                htmlBodyContent.append("</form>");
                htmlBodyContent.append("</p>");
            }
        }  catch (Exception e) {
            log.error(e.getMessage(), e);
            htmlBodyContent.append("<p>An error occoured. Exception: " + e.getMessage() + "</p>");
        }
        return htmlBodyContent;
    }
    
    /**
     *
     */
    private StringBuffer getUpdateScreen() {
        StringBuffer htmlBodyContent = new StringBuffer();
        try {
            String destDir = request.getSession().getServletContext().getRealPath(".") + File.separator + "..";
            Map bestUpdater = getBestUpdater();
            
            WarFetcher warFetcher = new WarFetcher(request, (String) bestUpdater.get("updateLink"), destDir);
            warFetcher.fetch();
            
            //TODO here it should set a password for the updater
            
            TomcatContextHandler tomcatContextHandler = new TomcatContextHandler(request);
            tomcatContextHandler.setContext(bestUpdater.get("id") + "-v-" + bestUpdater.get("version") + "-r-" + bestUpdater.get("revision"), bestUpdater.get("id") + "-v-" + bestUpdater.get("version") + "-r-" + bestUpdater.get("revision"));
            
            htmlBodyContent.append("<p>");
            htmlBodyContent.append("Update done.<br/>");
            htmlBodyContent.append("You will be redirected to the updater which will automaticaly download and install the requested yanel.");
            htmlBodyContent.append("</p>");
            
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            htmlBodyContent.append("<p>Update failed. Exception: " + e.getMessage() + "</p>");
        }
        return htmlBodyContent;
    }

    private InstallInfo getInstallInfo() throws Exception {
        return new InstallInfo(request);

    }

    private UpdateInfo getUpdateInfo() throws Exception {
        return new UpdateInfo(getInstallInfo().getUpdateURL(), getInstallInfo());
    }
    
    private HashMap getBestUpdater() throws Exception {
        InstallInfo installInfo = getInstallInfo();
        UpdateInfo updateInfo = getUpdateInfo();
        
        HashMap updateVersionDetails = updateInfo.getUpdateVersionDetail("updateLink", request.getParameter("updatelink"));
        VersionComparator versionComparator = new VersionComparator();
        String updateId = (String) updateVersionDetails.get("id");
        String updateVersion = (String) updateVersionDetails.get("version");
        String updateRevision = (String) updateVersionDetails.get("revision");
        ArrayList bestUpdater = updateInfo.getUpdateVersionsOf("type", "updater", installInfo.getRevision());
        for (int i = 0; i < bestUpdater.size(); i++) {
            HashMap versionDetail = (HashMap) bestUpdater.get(i);
            if (versionComparator.compare((String) versionDetail.get("targetApllicationMinRevision"), updateRevision) > 0 ) {
                bestUpdater.remove(i);
            }
            if (versionComparator.compare((String) versionDetail.get("targetApllicationMaxRevision"), updateRevision) < 0 ) {
                bestUpdater.remove(i);
            }
        }
        Collections.sort(bestUpdater, new UpdateInfoVersionComparator());
        if (bestUpdater.size() < 1) {
            throw new Exception("No updater found for updating your current version(" + installInfo.getId() + "-v-" + installInfo.getVersion() + "-r-" + installInfo.getRevision() + ") to your requested version (" + updateId + "-v-" + updateVersion + "-r-" + updateRevision + ")");
        }
        return (HashMap) bestUpdater.get(bestUpdater.size() - 1);
    }
    
    /**
     * @return ArrayList with all updates which are matching the revision requirement and are not installed yet. or null if none.
     * @throws Exception
     */
    private ArrayList getSuiteableYanelUpdates() throws Exception {
        InstallInfo installInfo = getInstallInfo();
        UpdateInfo updateInfo = getUpdateInfo();
        TomcatContextHandler tomcatContextHandler = new TomcatContextHandler(request);
        
        ArrayList updates = updateInfo.getYanelUpatesForYanelRevision(installInfo.getRevision());
        if (updates == null) return null;
        for (int i = 0; i < updates.size(); i++) {
            HashMap versionDetail = (HashMap) updates.get(i);
            for (int j = 0; j < tomcatContextHandler.getWebappNames().length; j++) {
                if (tomcatContextHandler.getWebappNames()[j].equals(versionDetail.get("id") + "-v-" + versionDetail.get("version") + "-r-" + versionDetail.get("revision"))) {
                    updates.remove(i);
                }
            }
        }
        if (updates.size() < 1) return null;  
        return updates;
    }

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
