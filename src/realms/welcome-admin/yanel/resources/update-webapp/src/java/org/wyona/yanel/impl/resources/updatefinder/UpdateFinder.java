/*
 * Copyright 2007-2009 Wyona
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

package org.wyona.yanel.impl.resources.updatefinder;

import org.apache.log4j.Logger;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.apache.xml.serializer.Serializer;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import org.wyona.yanel.core.serialization.SerializerFactory;
import org.wyona.yanel.core.source.ResourceResolver;
import org.wyona.yanel.core.transformation.I18nTransformer2;
import org.wyona.yanel.core.transformation.XIncludeTransformer;
import org.wyona.yanel.core.util.PathUtil;
import org.wyona.yanel.impl.resources.updatefinder.utils.*;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.Collections;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

import javax.servlet.http.HttpSession;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


/**
 * Get updates for installed Yanel version
 */
public class UpdateFinder extends Resource implements ViewableV2 {

    private static Logger log = Logger.getLogger(UpdateFinder.class);

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
            if (xsltPath != null && xsltPath.length > 0) {

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
                            org.wyona.commons.io.PathUtil.getName(getPath()));
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
            }

            log.debug("Mime-Type: " + mimeType);
            view.setInputStream(new java.io.StringBufferInputStream(getScreen()));
            return view;
        } catch (Exception e) {
            log.error(e + " (" + getPath() + ", " + getRealm() + ")", e);
        }

        try {
            view.setInputStream(new java.io.StringBufferInputStream(getScreen()));
        } catch(Exception e) {
            view.setInputStream(new java.io.StringBufferInputStream("Exception: " + e.getMessage()));
            log.error(e, e);
        }
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
    private String getScreen() throws Exception {
        
        StringBuilder body = new StringBuilder();
        StringBuilder head = new StringBuilder();
        Enumeration<?> parameters = request.getParameterNames();
        if (!parameters.hasMoreElements()) {
            plainRequest(body);
        } else {
            if (request.getParameter("save-as") != null) {
                plainRequest(body);
            } else if (request.getParameter("usecase") != null && request.getParameter("usecase").equals("update")) {
                getUpdateConfirmScreen(body);
            } else if (request.getParameter("usecase") != null && request.getParameter("usecase").equals("updateconfirmed")) {    
                getUpdateScreen(body,head);
            } else {
                log.info("Fallback ...");
                plainRequest(body);
            }
        }
        
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        sb.append("<head>");
        sb.append("<title>Yanel Updater</title>");


        sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\""
                + PathUtil.getGlobalHtdocsPath(this) + "yanel-css/progressBarTerminated.css\"/>");
/*        
        sb.append("<script src=\"" + PathUtil.getGlobalHtdocsPath(this)
                + "yanel-js/prototype.js\" type=\"text/javascript\"></script>");
        sb.append("<script src=\"" + PathUtil.getGlobalHtdocsPath(this)
                + "yanel-js/progressBar.js\" type=\"text/javascript\"></script>");
        sb.append("<script src=\"" + PathUtil.getGlobalHtdocsPath(this)
                + "yanel-js/sorttable.js\" type=\"text/javascript\"></script>");
*/

/*
        if (request.getParameter("usecase") != null && request.getParameter("usecase").equals("updateconfirmed")) {    
            try {
                Map bestUpdater = getBestUpdater();
                String htmlHeadContent = "<meta http-equiv=\"refresh\" content=\"10; URL=" + "http://" + request.getServerName() + ":" + request.getServerPort() + "/" + bestUpdater.get("id") + "-v-" + bestUpdater.get("version") + "-r-" + bestUpdater.get("revision") + "/" + "?updatelink=" + request.getParameter("updatelink") + "&amp;requestingwebapp=" + request.getParameter("requestingwebapp") + "\"/>";
                sb.append(htmlHeadContent);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
*/
        sb.append(head);

        sb.append("</head>");
        sb.append("<body>");
        sb.append("<h1>Yanel Updater</h1>");


        sb.append(body);

        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }

    /**
     *
     */
    private void plainRequest(StringBuilder htmlBodyContent) throws Exception {

        InstallInfo installInfo = null;
        try {
            installInfo = getInstallInfo();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            htmlBodyContent.append("<p>Could not get install information. " + e.getMessage() + "</p>");
            return;
        }

        UpdateInfo updateInfo = null;
        try {
            updateInfo = getUpdateInfo();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            htmlBodyContent.append("<p>Could not get update information. " + e.getMessage() + "</p>");
            return;
        }

        if (!installInfo.getInstalltype().equals("bin-snapshot")) {
            htmlBodyContent.append("<p>This Yanel was not installed from binary. You can only use the updater if you installed yanel from binary. Please use Subversion or get another source snapshot.</p><p><b>NOTE</b> for developers: In order to work on the Yanel Updater resource you might want to modify <a href=\"file://" + installInfo.getInstallRdfFilename() + "\">" + installInfo.getInstallRdfFilename() + "</a> by replacing the installtype \"source\" with \"bin-snapshot\" and also customize the version and revision!</p> <p>Also see the <a href=\"yanel-website/specification/update-dependency-manager.html\">Updater specification</a></p>");
            return;
        }

        String idVersionRevisionCurrent = installInfo.getId() + "-v-" + installInfo.getVersion() + "-r-" + installInfo.getRevision();

        // show productive version
        htmlBodyContent.append("<p>");
        htmlBodyContent.append("Your installed Yanel version is: <b>" + installInfo.getId() + "-v-" + installInfo.getVersion() + "-r-" + installInfo.getRevision() + "</b>");
        htmlBodyContent.append("</p>");

        try {
            int revisionAsInt = Integer.parseInt(installInfo.getRevision());
        } catch(NumberFormatException e) {
            htmlBodyContent.append("<p>Exception: Revision is not a number: " + installInfo.getRevision() + "</p>");
        }

        // TODO: implement getBestYanelWebapp() to get all yanel-webapp version which has an
        // yanel-updater which fits the targetRevision requirement of the current yanel and is not
        // already installed.

        List<Map<String, String>> updatebleYanelVersions = null;
        try {
            updatebleYanelVersions = getSuitableYanelUpdates(installInfo, updateInfo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            htmlBodyContent.append("<p>Could not get Updates because of exception: " + e.getMessage() + "</p>");
        }

        if (updatebleYanelVersions == null) {
            htmlBodyContent.append("<p>");
            htmlBodyContent.append("No updates found for your version within <a href=\"" + installInfo.getUpdateURL() + "\">" + installInfo.getUpdateURL() + "</a>");
            htmlBodyContent.append("</p>");
        } else {
            Map<String, String> newestYanel =updatebleYanelVersions.get(updatebleYanelVersions.size() - 1);
            String newestYanelName = newestYanel.get("id") + "-v-"
                    + newestYanel.get("version") + "-r-"
                    + newestYanel.get("revision");
            if (newestYanelName.equals(idVersionRevisionCurrent)) {
                htmlBodyContent.append("<p>");
                htmlBodyContent.append("Your yanel is already the newest version.");
                htmlBodyContent.append("</p>");
            } else {
                htmlBodyContent.append("<p>");
                htmlBodyContent.append("Newest yanel is: " + newestYanelName);
                htmlBodyContent.append("<form method=\"GET\"><input type=\"submit\" name=\"button\" value=\"update\"></input><input type=\"hidden\" name=\"usecase\" value=\"update\"></input><input type=\"hidden\" name=\"updatelink\" value=\"" + newestYanel.get("updateLink") + "\"/></form>");
                htmlBodyContent.append("</p>");
            }

        htmlBodyContent.append("<p>");
        htmlBodyContent.append("All versions you can get:");
        htmlBodyContent.append("</p>");
        htmlBodyContent.append("<ul>");

        for (int i = 0; i < updatebleYanelVersions.size(); i++) {
            Map<String, String> versionDetails = updatebleYanelVersions.get(i);
            String idVersionRevisionItem = versionDetails.get("id") + "-v-"
                    + versionDetails.get("version") + "-r-"
                    + versionDetails.get("revision");

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
                        + "<li> <form method=\"GET\"><input type=\"submit\" name=\"button\" value=\"update\"></input><input type=\"hidden\" name=\"usecase\" value=\"update\"/><input type=\"hidden\" name=\"updatelink\" value=\""
                        + versionDetails.get("updateLink") + "\"/></form></li>" + "</ul></li>");
        }
        htmlBodyContent.append("</ul>");
        }
        
        // show installed versions
        try {
            htmlBodyContent.append("<h3>Installed versions</h3>");
            TomcatContextHandler tomcatContextHandler = new TomcatContextHandler(request);
            Map<String, String> contextAndWebapp = tomcatContextHandler.getContextAndWebapp();

            htmlBodyContent.append("<table class=\"sortable\" border=\"1\">");
            htmlBodyContent.append("<thead>");
            htmlBodyContent.append("<tr><th>Context</th><th>Webapp</th></tr>");
            htmlBodyContent.append("</thead>");
            htmlBodyContent.append("<tbody>");
            Iterator<String> iterator = contextAndWebapp.keySet().iterator();

            while (iterator.hasNext()) {
                String context = iterator.next();
                String webapp = contextAndWebapp.get(context);
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
    }

    /**
     *
     */
    private void getUpdateConfirmScreen(StringBuilder htmlBodyContent) {
        try {
            UpdateInfo updateInfo = getUpdateInfo();
            InstallInfo installInfo = getInstallInfo();
            Map<String, String> bestUpdater = getBestUpdater();
            TomcatContextHandler tomcatContextHandler = new TomcatContextHandler(request);
            
            Map<String, String> versionDetails = updateInfo.getUpdateVersionDetail("updateLink", request.getParameter("updatelink"));
            String version = versionDetails.get("version");
            String revision = versionDetails.get("revision");
            String id = versionDetails.get("id");
            
            if (tomcatContextHandler.getWebappOfContext(bestUpdater.get("id") + "-v-" + bestUpdater.get("version") + "-r-" + bestUpdater.get("revision")) != null) {
                htmlBodyContent.append("<p>Yanel will redirect you to the update-manager which will download and install " + id + "-v-" + version + "-r-" + revision  + "</p>");
                htmlBodyContent.append("<p>Do you want to continue?</p>");
                htmlBodyContent.append("<p>");
                htmlBodyContent.append("<form method=\"post\" action=\"" + "http://" + request.getServerName() + ":" + request.getServerPort() + "/" + bestUpdater.get("id") + "-v-" + bestUpdater.get("version") + "-r-" + bestUpdater.get("revision") + "/\">");
                htmlBodyContent.append("<input type=\"submit\" name=\"button\" value=\"YES\"/>");
                htmlBodyContent.append("<input type=\"hidden\" name=\"usecase\" value=\"updateconfirmed\"/>");
                htmlBodyContent.append("<input type=\"hidden\" name=\"updatelink\" value=\"" + request.getParameter("updatelink") + "\"/>");
                htmlBodyContent.append("<input type=\"hidden\" name=\"requestingwebapp\" value=\"" + installInfo.getWebaName() + "\"/>");
                // TODO: here it should ask for a password which should be set in the new updater
		// NOTE: One can protect the URL itself and hence a specific authentication/authorization is not really necessary!
                htmlBodyContent.append("</form>");

                htmlBodyContent.append("<form method=\"GET\">");
                htmlBodyContent.append("<input type=\"submit\" name=\"button\" value=\"Cancel\"></input>");
                htmlBodyContent.append("</form>");

                htmlBodyContent.append("</p>");
            } else {
                htmlBodyContent.append("<p>In order to download and install the Yanel update \"" + id + "-v-" + version + "-r-" + revision  + "\" the update-manager \"" + bestUpdater.get("id") + "-v-" + bestUpdater.get("version") + "-r-" + bestUpdater.get("revision") + "\" needs to be downloaded first.</p>");
                htmlBodyContent.append("<p>Do you want to continue?</p>");
                htmlBodyContent.append("<p>");
                htmlBodyContent.append("<form method=\"GET\">");
                htmlBodyContent.append("<input type=\"submit\" name=\"button\" value=\"YES\"/>");
                htmlBodyContent.append("<input type=\"hidden\" name=\"usecase\" value=\"updateconfirmed\"/>");
                htmlBodyContent.append("<input type=\"hidden\" name=\"updatelink\" value=\"" + request.getParameter("updatelink") + "\"/>");
                htmlBodyContent.append("</form>");
                htmlBodyContent.append("<form method=\"GET\">");
                htmlBodyContent.append("<input type=\"submit\" name=\"button\" value=\"Cancel\"></input>");
                htmlBodyContent.append("</form>");
                htmlBodyContent.append("</p>");
            }
        }  catch (Exception e) {
            log.error(e.getMessage(), e);
            htmlBodyContent.append("<p>An error occoured. Exception: " + e.getMessage() + "</p>");
        }
    }
    
    /**
     *
     */
    private void getUpdateScreen(StringBuilder htmlBodyContent, StringBuilder head) {
        HttpSession session = request.getSession();
        try {
            String destDir = request.getSession().getServletContext().getRealPath(".") + File.separator + "..";
            Map<String, String> bestUpdater = getBestUpdater();
            InstallInfo installInfo = getInstallInfo();
            
            URL updaterURL = new URL("http://" + request.getServerName() + ":" + request.getServerPort() + "/" + bestUpdater.get("id") + "-v-" + bestUpdater.get("version") + "-r-" + bestUpdater.get("revision"));
            HttpURLConnection updaterURLConn = (HttpURLConnection)updaterURL.openConnection();
            if (updaterURLConn.getResponseCode() == 200) {
                session.removeAttribute(WarFetcher.SESSION_ATTR_TASK);
                session.removeAttribute(WarFetcher.SESSION_ATTR_PROGRESS);
                session.removeAttribute(WarFetcher.SESSION_ATTR_ITEMS_DONE);
                session.removeAttribute(WarFetcher.SESSION_ATTR_ITEMS_TO_BE_DONE);
                head.append("<meta http-equiv=\"refresh\" content=\"0; URL=http://" + request.getServerName() + ":" + request.getServerPort() + "/" + bestUpdater.get("id") + "-v-" + bestUpdater.get("version") + "-r-" + bestUpdater.get("revision") + "/?updatelink=" + request.getParameter("updatelink") + "&amp;requestingwebapp=" + installInfo.getWebaName() + "\"/>");
                htmlBodyContent.append("<p>Update-Manager has been downloaded and installed.</p>");
                htmlBodyContent.append("<p>You will be <a href=\""+"http://" + request.getServerName() + ":" + request.getServerPort() + "/" + bestUpdater.get("id") + "-v-" + bestUpdater.get("version") + "-r-" + bestUpdater.get("revision") + "/?updatelink=" + request.getParameter("updatelink") + "&amp;requestingwebapp=" + installInfo.getWebaName() + ""+"\">redirected</a> to the update-manager which will automatically download and install the requested yanel.</p>");
                return;
            }
            
            if (session.getAttribute(WarFetcher.SESSION_ATTR_TASK) == null ){
                Runnable runFetcher = new WarFetcher(request, bestUpdater.get("updateLink"), destDir);
                new Thread(runFetcher).start();
                session.setAttribute(WarFetcher.SESSION_ATTR_TASK, "started");
                session.setAttribute(WarFetcher.SESSION_ATTR_PROGRESS, "0");
            }
            
            //TODO here it should set a password for the updater
            
            if (session.getAttribute(WarFetcher.SESSION_ATTR_TASK) != null && session.getAttribute(WarFetcher.SESSION_ATTR_TASK).equals("downloaded")) {
                TomcatContextHandler tomcatContextHandler = new TomcatContextHandler(request);
                tomcatContextHandler.setContext(bestUpdater.get("id") + "-v-" + bestUpdater.get("version") + "-r-" + bestUpdater.get("revision"), bestUpdater.get("id") + "-v-" + bestUpdater.get("version") + "-r-" + bestUpdater.get("revision"));
                session.setAttribute(WarFetcher.SESSION_ATTR_TASK, "loading");
                //htmlBodyContent.append("<p>Tomcat is loading and startup the update-manager</p>");
                //head.append("<meta http-equiv=\"refresh\" content=\"2; URL=?usecase=updateconfirmed&amp;updatelink=" + request.getParameter("updatelink") + "\"/>");
            }

            if (session.getAttribute(WarFetcher.SESSION_ATTR_TASK) != null && session.getAttribute(WarFetcher.SESSION_ATTR_TASK).equals("loading")) {
                int pseudoprogress = Integer.valueOf((String) session.getAttribute(WarFetcher.SESSION_ATTR_PROGRESS)).intValue() + 1;
                session.setAttribute(WarFetcher.SESSION_ATTR_PROGRESS, "" + pseudoprogress);
            }
            
            if (session.getAttribute(WarFetcher.SESSION_ATTR_TASK) != null) {
                head.append("<meta http-equiv=\"refresh\" content=\"2; URL=?usecase=updateconfirmed&amp;updatelink=" + request.getParameter("updatelink") + "\"/>");
                htmlBodyContent.append("<p>Working: " + session.getAttribute(WarFetcher.SESSION_ATTR_TASK) + "</p><p>");
                if (session.getAttribute(WarFetcher.SESSION_ATTR_ITEMS_DONE) != null) {
                    if (session.getAttribute(WarFetcher.SESSION_ATTR_TASK).equals("download")) {
                        htmlBodyContent.append("Downloaded " + session.getAttribute(WarFetcher.SESSION_ATTR_ITEMS_DONE) + " bytes of " + session.getAttribute(WarFetcher.SESSION_ATTR_ITEMS_TO_BE_DONE) + " bytes");
                    }
                    if (session.getAttribute(WarFetcher.SESSION_ATTR_TASK).equals("extract")) {
                        htmlBodyContent.append("Extracted " + session.getAttribute(WarFetcher.SESSION_ATTR_ITEMS_DONE) + " items of " + session.getAttribute(WarFetcher.SESSION_ATTR_ITEMS_TO_BE_DONE) + " items");
                    }
                }
                htmlBodyContent.append("</p>");
                htmlBodyContent.append("<p>Progress: <div id=\"yanelprogressbarterminated\"><div id=\"yanelprogressbarindicatorterminated\" style=\"width:" + session.getAttribute(WarFetcher.SESSION_ATTR_PROGRESS) + "%\">" + session.getAttribute(WarFetcher.SESSION_ATTR_PROGRESS) +"%</div></div></p>");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            htmlBodyContent.append("<p>Update failed. Exception: " + e.getMessage() + "</p>");
        }
    }

    private InstallInfo getInstallInfo() throws Exception {
        return new InstallInfo(request);

    }

    private UpdateInfo getUpdateInfo() throws Exception {
        return new UpdateInfo(getInstallInfo().getUpdateURL(), getInstallInfo());
    }
    
    /**
     * Get Updater
     */
    private Map<String, String> getBestUpdater() throws Exception {
        InstallInfo installInfo = getInstallInfo();
        UpdateInfo updateInfo = getUpdateInfo();
        
        Map<String, String> updateVersionDetails = updateInfo.getUpdateVersionDetail("updateLink", request.getParameter("updatelink"));
        VersionComparator versionComparator = new VersionComparator();
        String updateId = updateVersionDetails.get("id");
        String updateVersion = updateVersionDetails.get("version");
        String updateRevision = updateVersionDetails.get("revision");
        List<Map<String, String>> bestUpdater = updateInfo.getUpdateVersionsOf("type", "updater", installInfo.getRevision());
        for (int i = 0; i < bestUpdater.size(); i++) {
            Map<String, String> versionDetail =bestUpdater.get(i);
            log.error("DEBUG: Updater details: " + versionDetail);
            if (versionComparator.compare(versionDetail.get(UpdateInfo.TARGET_APPLICATION_MIN_REVISION), updateRevision) > 0 ) {
                bestUpdater.remove(i);
            }
            if (versionComparator.compare(versionDetail.get(UpdateInfo.TARGET_APPLICATION_MAX_REVISION), updateRevision) < 0 ) {
                bestUpdater.remove(i);
            }
        }
        Collections.sort(bestUpdater, new UpdateInfoVersionComparator());
        if (bestUpdater.size() < 1) {
            throw new Exception("No updater found for updating your current version (" + installInfo.getId() + "-v-" + installInfo.getVersion() + "-r-" + installInfo.getRevision() + ") to your requested version (" + updateId + "-v-" + updateVersion + "-r-" + updateRevision + ")");
        }
        return bestUpdater.get(bestUpdater.size() - 1);
    }
    
    /**
     * @return ArrayList with all updates which are matching the revision requirement and are not installed yet. or null if none.
     * @throws Exception
     */
    private List<Map<String, String>> getSuitableYanelUpdates(InstallInfo installInfo, UpdateInfo updateInfo) throws Exception {
        TomcatContextHandler tomcatContextHandler = new TomcatContextHandler(request);
        
        List<Map<String, String>> updates = updateInfo.getYanelUpdatesForYanelRevision(installInfo.getRevision());
        if (updates == null) return null;

        for (int i = 0; i < updates.size(); i++) {
            Map<String, String> versionDetail = updates.get(i);
            log.error("DEBUG: Update: " + versionDetail.get("id") + "-v-" + versionDetail.get("version") + "-r-" + versionDetail.get("revision"));

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
