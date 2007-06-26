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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.dom.DOMSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Category;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.ZipFileSet;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTask;
import org.apache.tools.ant.taskdefs.optional.junit.BatchTest;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.apache.xml.serializer.Serializer;

import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceConfiguration;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yarep.core.NoSuchNodeException;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryFactory;
import org.wyona.yanel.core.serialization.SerializerFactory;
import org.wyona.yanel.core.source.ResourceResolver;
import org.wyona.yanel.core.transformation.I18nTransformer2;
import org.wyona.yanel.core.transformation.XIncludeTransformer;
import org.wyona.yanel.core.util.PathUtil;
import org.wyona.yarep.util.RepoPath;
import org.wyona.yarep.util.YarepUtil;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * 
 */
public class TestingControlResource extends Resource implements ViewableV2 {

    private static final String JUNIT_JAR = "yanel-JunitTests.jar";
    private static final String HTMLUNIT_JAR = "yanel-HtmlUnitTests.jar";
    private static Category log = Category.getInstance(TestingControlResource.class);
    private boolean ajaxBrowser = false;
    private File JunitJarLocation;
    private File HtmlunitJarLocation;
    private File tmpResultDir;

    public TestingControlResource() {
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
        if (request.getHeader("User-Agent").indexOf("rv:1.7") < 0) {
            ajaxBrowser = true;
        }
        try {
            setLocations();
        } catch (Exception e) {
            // sb.append("<p>Could not get the Locations: " + e + "</p>");
            log.error(e.getMessage(), e);
        }
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
     * Flow
     */
    private String getScreen() {
        StringBuffer sbContent = new StringBuffer();
        Enumeration parameters = request.getParameterNames();
        if (request.getSession().getAttribute("tmpResultDir") != null) {
            if (request.getParameterValues("ajaxshowprogress") != null) {
                return showProgress().toString();
            } else {
                sbContent.append(showProgress());
            }
        } else if (!parameters.hasMoreElements()) {
            sbContent.append(getPlainRequest());
        } else {
            if (request.getParameterValues("testnames") != null) {
                if (request.getParameterValues("ajaxexecutetest") != null) {
                    return executeTests().toString();
                }else {
                    sbContent.append(executeTests());
                }
            } else {
                log.info("Fallback ...");
                sbContent.append(getPlainRequest());
            }
        }

        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        sb.append("<head><title>Testing Control</title>");
        if (request.getSession().getAttribute("tmpResultDir") != null && !ajaxBrowser) {
            sb.append("<meta http-equiv=\"refresh\" content=\"5; URL=\"/>");
        }
        sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\""
                + PathUtil.getGlobalHtdocsPath(this) + "yanel-css/progressBar.css\"/>");
        sb.append("<script src=\"" + PathUtil.getGlobalHtdocsPath(this)
                + "yanel-js/prototype.js\" type=\"text/javascript\"></script>");
        sb.append("<script src=\"" + PathUtil.getGlobalHtdocsPath(this)
                + "yanel-js/progressBar.js\" type=\"text/javascript\"></script>");
        sb.append("<script src=\"" + PathUtil.getResourcesHtdocsPath(this)
                + "js/ajaxexecutetests.js\" type=\"text/javascript\"></script>");
        sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\""
                + PathUtil.getResourcesHtdocsPath(this) + "css/testingcontroler.css\"/>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<span id=\"yanelprogressbarph\"/>");
        sb.append("<div id=\"ajaxreplace\">");
        sb.append(sbContent);
        sb.append("</div>");
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }

    private StringBuffer getPlainRequest() {
        StringBuffer sb = new StringBuffer();
        sb.append("<form method=\"post\">");
        sb.append("<h3>HtmlUnit Tests</h3>");
        sb.append("<ul id=\"htmlunit\">");
        String[] allHtmlUnitTestNames = getAllTestNames("htmlunit");
        for (int i = 0; i < allHtmlUnitTestNames.length; i++) {
            String title = allHtmlUnitTestNames[i].substring(allHtmlUnitTestNames[i].lastIndexOf("/") + 1)
                    .replaceAll(".class", "");
            sb.append("<li title=\"" + "\">");
            sb.append(title);
            sb.append("<input type=\"checkbox\" name=\"testnames\" value=\""
                    + allHtmlUnitTestNames[i] + "\"/>");
            sb.append("</li>");
        }
        sb.append("</ul>");
        sb.append("<hr/>");
        sb.append("<h3>JUnit Tests</h3>");
        sb.append("<ul id=\"junit\">");
        String[] allJUnitTestNames = getAllTestNames("junit");
        for (int i = 0; i < allJUnitTestNames.length; i++) {
            String title = allJUnitTestNames[i].substring(allJUnitTestNames[i].lastIndexOf("/") + 1)
                    .replaceAll(".class", "");
            sb.append("<li title=\"" + title + "\">");
            sb.append(title);
            sb.append("<input type=\"checkbox\" name=\"testnames\" value=\"" + allJUnitTestNames[i]
                    + "\"/>");
            sb.append("</li>");
        }
        sb.append("</ul>");
        if (ajaxBrowser) {
            sb.append("<input type=\"hidden\" name=\"yanel.resource.viewid\" value=\"source\"/>");
            sb.append("<input type=\"hidden\" name=\"ajaxexecutetest\" value=\"true\"/>");
            sb.append("<input type=\"button\" name=\"submit\" value=\"Test\" onclick=\"ajaxexecutetests();\" />");
        } else {
            sb.append("<input type=\"submit\" name=\"submit\" value=\"Test\"/>");
        }
        sb.append("</form>");
        return sb;
    }

    private StringBuffer executeTests() {
        StringBuffer sb = new StringBuffer();
        String[] testnames = request.getParameterValues("testnames");
        // prepare tmpResultDir
        if (request.getSession().getAttribute("tmpResultDir") == null) {
            String uuid = new java.rmi.server.UID().toString().replaceAll(":", "");
            tmpResultDir = new File(request.getSession().getServletContext().getRealPath("tmp"
                    + File.separator + "test-results-" + uuid));
            request.getSession().setAttribute("tmpResultDir", tmpResultDir);
        } else {
            tmpResultDir = (File) request.getSession().getAttribute("tmpResultDir");
        }
        request.getSession().setAttribute("exectime", getTime());
        request.getSession().setAttribute("numberOfTests", "" + testnames.length);
        // delete the resultdir before making new tests
        tmpResultDir.mkdir();
        Runnable runtest = new ExecuteTests(testnames,
                JunitJarLocation,
                HtmlunitJarLocation,
                tmpResultDir);
        new Thread(runtest).start();
        sb.append(showProgress());
        return sb;
    }

    private StringBuffer showProgress() {
        StringBuffer sb = new StringBuffer();
        // get tmpResultDir from session
        tmpResultDir = (File) request.getSession().getAttribute("tmpResultDir");
        // number of executed tests
        int numberOfTests = Integer.parseInt((String) request.getSession()
                .getAttribute("numberOfTests"));
        String resultName = request.getSession().getAttribute("exectime") + "-tests.xml";
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (tmpResultDir.list().length < numberOfTests) {
            // geting the test results
            // aggregate all tests in the tmp dir
            try {
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document result = builder.newDocument();
                Project project = new Project();

                ResultAggregator junitreport = new ResultAggregator();
                junitreport.setTaskName("JUnitReport");
                junitreport.setProject(project);

                FileSet fs_report = new FileSet();
                fs_report.setDir(tmpResultDir);
                fs_report.setProject(project);

                PatternSet.NameEntry ne = fs_report.createInclude();
                ne.setName("**/TEST-*.xml");
                junitreport.addFileSet(fs_report);

                junitreport.init();
                // get the result to show for this request
                result = junitreport.getDocument();

                File result2htmlXsltFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile()
                        .getParentFile()
                        .getAbsolutePath(), "xslt" + File.separator + "result2html.xsl");
                Transformer transResult2html = TransformerFactory.newInstance()
                        .newTransformer(new StreamSource(result2htmlXsltFile));
                transResult2html.setParameter("testing.result.title", "stillTesting");
                transResult2html.setParameter("testing.number.requested.tests", "" + numberOfTests);
                transResult2html.transform(new DOMSource(result),
                        new StreamResult(byteArrayOutputStream));
            } catch (Exception e) {
                sb.append("<p>Could not create folder. Exception: " + e + "</p>");
                log.error(e.getMessage(), e);
            }

        } else {
            request.getSession().removeAttribute("tmpResultDir");
            request.getSession().removeAttribute("exectime");
            request.getSession().removeAttribute("numberOfTests");

            try {
                // geting the test results
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document result = builder.newDocument();
                Project aggregatorproject = new Project();
                // aggregate all tests in the tmp dir
                ResultAggregator junitreport = new ResultAggregator();
                junitreport.setTaskName("JUnitReport");
                junitreport.setProject(aggregatorproject);

                FileSet fs_report = new FileSet();
                fs_report.setDir(tmpResultDir);
                fs_report.setProject(aggregatorproject);

                PatternSet.NameEntry ne = fs_report.createInclude();
                ne.setName("**/TEST-*.xml");
                junitreport.addFileSet(fs_report);

                junitreport.init();
                // get the result to show for this request
                result = junitreport.getDocument();

                // write test result to repo
                org.wyona.yarep.core.Repository Repo = this.getRealm().getRepository();
                org.wyona.commons.io.Path newPath = new org.wyona.commons.io.Path("/test-results-archive/"
                        + resultName);
                log.error("DEBUG: " + newPath);
                org.wyona.yanel.core.util.YarepUtil.addNodes(Repo,
                        newPath.toString(),
                        org.wyona.yarep.core.NodeType.RESOURCE);

                OutputStream out = Repo.getNode(newPath.toString()).getOutputStream();
                javax.xml.transform.TransformerFactory.newInstance()
                        .newTransformer()
                        .transform(new javax.xml.transform.dom.DOMSource(result),
                                new javax.xml.transform.stream.StreamResult(out));
                out.close();

                // delete the test dir
                FileUtils.deleteDirectory(tmpResultDir);

                File result2htmlXsltFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile()
                        .getParentFile()
                        .getAbsolutePath(), "xslt" + File.separator + "result2html.xsl");
                Transformer transResult2html = TransformerFactory.newInstance()
                        .newTransformer(new StreamSource(result2htmlXsltFile));
                transResult2html.setParameter("testing.result.title", "testDone");
                transResult2html.transform(new DOMSource(result),
                        new StreamResult(byteArrayOutputStream));
            } catch (Exception e) {
                sb.append("<p>Could not create folder. Exception: " + e + "</p>");
                log.error(e.getMessage(), e);
            }
        }
        sb.append(byteArrayOutputStream);
        return sb;
    }

    private void setLocations() throws Exception {
        String WEBINFPath = request.getSession().getServletContext().getRealPath("WEB-INF");
        HtmlunitJarLocation = new File(WEBINFPath + File.separator + "lib" + File.separator
                + HTMLUNIT_JAR);
        JunitJarLocation = new File(WEBINFPath + File.separator + "lib" + File.separator
                + JUNIT_JAR);

        if (!HtmlunitJarLocation.exists()) {
            throw new Exception("HtmlUnit-Tests not found");
        }
        if (!JunitJarLocation.exists()) {
            throw new Exception("JUnit-Tests not found");
        }

        // create tmp-directory to write the tests
        if (!new File(request.getSession().getServletContext().getRealPath("tmp")).exists()) {
            if (!new File(request.getSession().getServletContext().getRealPath("tmp")).mkdir()) {
                throw new Exception("Creation of tmp directory faild.");
            }
        }
    }

    /**
     * get Tests.
     * @param htmlOrJunit type of tests should be selected. can be htmlunit or junit.
     * @return an array with the aviable tests.
     */
    private String[] getAllTestNames(String htmlOrJunit) {
        Project project = new Project();
        try {
            JUnitTask junit = new JUnitTask();

            ZipFileSet zipfileset = new ZipFileSet();
            zipfileset.setProject(project);
            if (htmlOrJunit.equals("htmlunit")) {
                zipfileset.setSrc(HtmlunitJarLocation);
            } else {
                zipfileset.setSrc(JunitJarLocation);
            }
            zipfileset.setIncludes("**/*Test.class");
            zipfileset.setExcludes("**/Abstract*.class");

            BatchTest batchTest = junit.createBatchTest();
            batchTest.addFileSet(zipfileset);

            DirectoryScanner directoryscanner = zipfileset.getDirectoryScanner(project);

            return directoryscanner.getIncludedFiles();
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    /**
     * get time as string
     * @return timestamp (yyyy-MM-dd-HH-mm-ss)
     */
    private String getTime() {
        Calendar cal = Calendar.getInstance(java.util.TimeZone.getDefault());
        String dateFormat = "yyyy-MM-dd-HH-mm-ss";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(dateFormat);
        sdf.setTimeZone(java.util.TimeZone.getDefault());
        return sdf.format(cal.getTime());
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
