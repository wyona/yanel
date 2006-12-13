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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.Servlet;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.dom.DOMSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;

import org.apache.log4j.Category;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.taskdefs.optional.junit.*;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Tstamp;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.taskdefs.optional.junit.AggregateTransformer.Format;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;

import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceTypeDefinition;
import org.wyona.yanel.core.ResourceTypeRegistry;
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.core.util.ResourceAttributeHelper;
import org.wyona.yarep.core.NoSuchNodeException;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryFactory;
import org.wyona.yarep.util.RepoPath;
import org.wyona.yarep.util.YarepUtil;
import org.wyona.yanel.impl.resources.ResultAggregator;

/**
 * 
 */
public class TestingControlResource extends Resource implements ViewableV1 {

    private static final String DEFAULT_CONFIGURATION_FILE = "test.config.xml";
    private static Category log = Category.getInstance(TestingControlResource.class);
    private String YanelHomeDir;

    /**
     * 
     */
    public TestingControlResource() {
        URL propertiesURL = TestingControlResource.class.getClassLoader()
                .getResource(DEFAULT_CONFIGURATION_FILE);
        if (propertiesURL == null) {
            log.error("No such resource: " + DEFAULT_CONFIGURATION_FILE);
        }
        try {
            DefaultConfigurationBuilder configbuilder = new DefaultConfigurationBuilder();
            File configfile = new File(propertiesURL.getFile());
            Configuration config = configbuilder.buildFromFile(configfile);
            YanelHomeDir = config.getChild("yanel-home-dir").getValue();
        } catch (Exception e) {
            log.error(e);
        }
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
        defaultView.setInputStream(new java.io.StringBufferInputStream(sb.toString()));
        return defaultView;
    }

    /**
     * @throws Exception
     * 
     */
    public View getView(HttpServletRequest request, String viewId) throws Exception {
        Path path = new Path(request.getServletPath());
        String submit = request.getParameter("submit");
        String type = request.getParameter("type");
        String archive = request.getParameter("archive");
        String[] testnames = request.getParameterValues("testnames");
        View defaultView = new View();
        if (submit == null) {
            return plainRequest(path, defaultView);
        } else {
            if (type.equals("test")) {
                return executeTests(path, defaultView, testnames);
            }
            if (type.equals("show")) {
                return showArchive(path, defaultView, archive);
            }
        }
        return null;
    }

    private View plainRequest(Path path, View defaultView) throws Exception,
            TransformerConfigurationException, TransformerFactoryConfigurationError,
            NoSuchNodeException, TransformerException {
        Repository contentRepo;
        RepoPath rp = contentRepo(path);
        contentRepo = rp.getRepo();

        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        sb.append("<head>");
        sb.append("<title>Testing Control</title>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<div id=\"contenBody\">");
        sb.append("<h1>Testing Control</h1>");
        sb.append("<form>");
        sb.append("<p>Following htmlunit tests are available:</p>");
        sb.append("<p>Number of Test: " + getAllTestNames("htmlunit").length + "</p>");

        for (int i = 0; i < this.getAllTestNames("htmlunit").length; i++) {
            sb.append("<p>"
                    + this.getAllTestNames("htmlunit")[i].substring(this.getAllTestNames("htmlunit")[i].lastIndexOf("/") + 1));
            sb.append("<input type=\"checkbox\" name=\"testnames\" value=\""
                    + this.getAllTestNames("htmlunit")[i] + "\"/></p>");
        }

        sb.append("<p>Following junit tests are available:</p>");
        sb.append("<p>Number of Test: " + getAllTestNames("junit").length + "</p>");

        for (int i = 0; i < this.getAllTestNames("junit").length; i++) {
            sb.append("<p>"
                    + this.getAllTestNames("junit")[i].substring(this.getAllTestNames("junit")[i].lastIndexOf("/") + 1));
            sb.append("<input type=\"checkbox\" name=\"testnames\" value=\""
                    + this.getAllTestNames("junit")[i] + "\"/></p>");
        }

        sb.append("<input type=\"hidden\" name=\"type\" value=\"test\"/>");
        sb.append("<input type=\"submit\" name=\"submit\" value=\"Test\"/>");
        sb.append("</form>");

        sb.append("<form>");
        sb.append("<br/>Archived Test-Results: <select name=\"archive\">");
        File testResultArchiveDir = new File(YanelHomeDir + "/src/test/test-results-archive");
        String[] archivedTests = testResultArchiveDir.list();
        for (int i = 0; i < archivedTests.length; i++) {
            if (archivedTests[i].matches("[\\d]{4,4}[-][\\d]{2,2}[-][\\d]{2,2}[-][\\d]{2,2}[-][\\d]{2,2}[-][\\d]{2,2}[-]tests\\.xml")) {
                sb.append("<option value=\"" + archivedTests[i] + "\">" + archivedTests[i]
                        + "</option>");
            }
        }
        sb.append("</select>");
        sb.append("<input type=\"hidden\" name=\"type\" value=\"show\"/>");
        sb.append("<input type=\"submit\" name=\"submit\" value=\"Show\"/>");
        sb.append("</form>");

        sb.append("</div>");
        sb.append("</body>");
        sb.append("</html>");

        Transformer transformer = TransformerFactory.newInstance()
                .newTransformer(getXSLTStreamSource(path, contentRepo));
        transformer.setParameter("yanel.path.name", path.getName());
        transformer.setParameter("yanel.path", path.toString());
        transformer.setParameter("yanel.back2context", backToRoot(path, ""));
        transformer.setParameter("yarep.back2realm",
                backToRoot(new org.wyona.yanel.core.Path(rp.getPath().toString()), ""));
        // TODO: Is this the best way to generate an InputStream from an
        // OutputStream?
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        transformer.transform(new StreamSource(new java.io.StringBufferInputStream(sb.toString())),
                new StreamResult(baos));
        defaultView.setMimeType("application/xhtml+xml");
        defaultView.setInputStream(new java.io.ByteArrayInputStream(baos.toByteArray()));
        return defaultView;
    }

    private View executeTests(Path path, View defaultView, String[] testnames) throws Exception,
            TransformerConfigurationException, TransformerFactoryConfigurationError,
            NoSuchNodeException, TransformerException {
        Repository contentRepo;
        RepoPath rp = contentRepo(path);
        contentRepo = rp.getRepo();

        // delete the resultdir before making new tests
        emptyResultDir();

        // executing the tests
        this.executeTests(testnames, "htmlunit");
        this.executeTests(testnames, "junit");

        // geting the test results
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document result = builder.newDocument();

        File testResultDir = new File(YanelHomeDir + "/src/test/test-results");
        File testResultArchiveDir = new File(YanelHomeDir + "/src/test/test-results-archive");

        Project project = new Project();

        try {
            ResultAggregator junitreport = new ResultAggregator();
            junitreport.setTaskName("JUnitReport");
            junitreport.setProject(project);

            FileSet fs_report = new FileSet();
            fs_report.setDir(testResultDir);
            fs_report.setProject(project);

            PatternSet.NameEntry ne = fs_report.createInclude();
            ne.setName("**/TEST-*.xml");
            junitreport.addFileSet(fs_report);

            junitreport.setTodir(testResultArchiveDir);
            junitreport.setTofile(getTime() + "-tests.xml");

            junitreport.init();
            // archives an aggregation of test-results in test-result-archives
            junitreport.execute();
            //get the result to show for this request
            result = junitreport.getDocument();
        } catch (Exception e) {
            log.error(e);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        File result2htmlXsltFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile()
                .getParentFile()
                .getAbsolutePath(), "xslt" + File.separator + "result2html.xsl");
        Transformer transResult2html = TransformerFactory.newInstance()
                .newTransformer(new StreamSource(result2htmlXsltFile));
        transResult2html.transform(new DOMSource(result), new StreamResult(byteArrayOutputStream));

        Transformer transformer = TransformerFactory.newInstance()
                .newTransformer(getXSLTStreamSource(path, contentRepo));
        transformer.setParameter("yanel.path.name", path.getName());
        transformer.setParameter("yanel.path", path.toString());
        transformer.setParameter("yanel.back2context", backToRoot(path, ""));
        transformer.setParameter("yarep.back2realm",
                backToRoot(new org.wyona.yanel.core.Path(rp.getPath().toString()), ""));
        // TODO: Is this the best way to generate an InputStream from an
        // OutputStream?
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        transformer.transform(new StreamSource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())),
                new StreamResult(baos));
        defaultView.setMimeType("application/xhtml+xml");
        defaultView.setInputStream(new java.io.ByteArrayInputStream(baos.toByteArray()));
        return defaultView;
    }

    private View showArchive(Path path, View defaultView, String archive) throws Exception,
            TransformerConfigurationException, TransformerFactoryConfigurationError,
            NoSuchNodeException, TransformerException {
        Repository contentRepo;
        RepoPath rp = contentRepo(path);
        contentRepo = rp.getRepo();

        File ResultArchive = new File(YanelHomeDir + "/src/test/test-results-archive/"+archive);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        File result2htmlXsltFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile()
                .getParentFile()
                .getAbsolutePath(), "xslt" + File.separator + "result2html.xsl");
        Transformer transResult2html = TransformerFactory.newInstance()
                .newTransformer(new StreamSource(result2htmlXsltFile));
        
        FileReader archfile = new FileReader(ResultArchive);
        transResult2html.transform(new StreamSource(archfile), new StreamResult(byteArrayOutputStream));

        Transformer transformer = TransformerFactory.newInstance()
                .newTransformer(getXSLTStreamSource(path, contentRepo));
        transformer.setParameter("yanel.path.name", path.getName());
        transformer.setParameter("yanel.path", path.toString());
        transformer.setParameter("yanel.back2context", backToRoot(path, ""));
        transformer.setParameter("yarep.back2realm",
                backToRoot(new org.wyona.yanel.core.Path(rp.getPath().toString()), ""));
        // TODO: Is this the best way to generate an InputStream from an
        // OutputStream?
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        transformer.transform(new StreamSource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())),
                new StreamResult(baos));
        defaultView.setMimeType("application/xhtml+xml");
        defaultView.setInputStream(new java.io.ByteArrayInputStream(baos.toByteArray()));
        return defaultView;
    }

    private RepoPath contentRepo(Path path) throws Exception {
        return new YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()),
                getRepositoryFactory());
    }

    private Transformer prepareTransformer(Path path) throws Exception,
            TransformerConfigurationException, TransformerFactoryConfigurationError,
            NoSuchNodeException {
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
     * 
     */
    private StreamSource getXSLTStreamSource(Path path, Repository repo) throws NoSuchNodeException {
        Path xsltPath = getXSLTPath(path);
        if (xsltPath != null) {
            return new StreamSource(repo.getInputStream(new org.wyona.yarep.core.Path(getXSLTPath(path).toString())));
        } else {
            File xsltFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile()
                    .getParentFile()
                    .getAbsolutePath(), "xslt" + File.separator + "copyOutput.xsl");
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
            RepoPath rpRTI = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()),
                    getRTIRepositoryFactory());
            java.io.BufferedReader br = new java.io.BufferedReader(rpRTI.getRepo()
                    .getReader(new org.wyona.yarep.core.Path(new Path(rpRTI.getPath().toString()).getRTIPath()
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
        if (path.toString().equals(File.separator))
            return true;
        return false;
    }

    /**
     * get Tests.
     * @param htmlOrJunit type of tests should be selected. can be htmlunit or junit.
     * @return an array with the aviable tests.
     */
    private String[] getAllTestNames(String htmlOrJunit) {

        File basedir = new File(YanelHomeDir + "/build/test/" + htmlOrJunit);
        File resultdir = new File(YanelHomeDir + "/src/test/test-result");

        Project project = new Project();

        try {
            JUnitTask junit = new JUnitTask();

            BatchTest batchTest = junit.createBatchTest();

            org.apache.tools.ant.types.FileSet fileset = new org.apache.tools.ant.types.FileSet();

            fileset.setProject(project);
            fileset.setDir(basedir);
            fileset.setIncludes("**/*Test.class");
            fileset.setExcludes("**/Abstract*.class");

            batchTest.setTodir(resultdir);
            batchTest.addFileSet(fileset);

            DirectoryScanner directoryscanner = fileset.getDirectoryScanner(project);

            String[] files = directoryscanner.getIncludedFiles();

            return files;
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    /**
     * Executes Tests.
     * @param testnames which should be executed.
     * @param htmlOrJunit type of tests which should be executed. can be htmlunit or junit.
     */
    private void executeTests(String[] testnames, String htmlOrJunit) throws Exception {

        File basedir = new File(YanelHomeDir + "/build/test/" + htmlOrJunit);
        File resultdir = new File(YanelHomeDir + "/src/test/test-results");

        Project project = new Project();
        project.init();

        try {
            JUnitTask junit = new JUnitTask();
            junit.setProject(project);

            FileSet fileset = new FileSet();
            fileset.setProject(project);
            fileset.setDir(basedir);

            String includes = "";
            for (int i = 0; i < testnames.length; i++) {
                includes = includes + testnames[i] + ",";
            }
            fileset.setIncludes(includes);

            BatchTest batchTest = junit.createBatchTest();
            batchTest.setTodir(resultdir);
            batchTest.addFileSet(fileset);

            DirectoryScanner directoryscanner = fileset.getDirectoryScanner(project);
            String[] files = directoryscanner.getIncludedFiles();

            FormatterElement formatter = new FormatterElement();
            formatter.setUseFile(true);
            FormatterElement.TypeAttribute typeattribute = new FormatterElement.TypeAttribute();
            typeattribute.setValue("xml");
            formatter.setType(typeattribute);

            junit.addFormatter(formatter);
            junit.setFork(false);

            JUnitTask.SummaryAttribute summaryattr = new JUnitTask.SummaryAttribute();
            summaryattr.setValue("true");
            junit.setPrintsummary(summaryattr);

            org.apache.tools.ant.types.Commandline.Argument cmdline = junit.createJvmarg();
            cmdline.setValue("-Djunit.base.dir=" + YanelHomeDir + "/build/test/" + htmlOrJunit);

            junit.setHaltonerror(false);
            junit.setHaltonfailure(false);

            junit.init();
            junit.execute();

        } catch (Exception e) {
            log.error(e);
        }
    }

    /**
     * remove old test from the result-dir
     */
    private void emptyResultDir() {
        File testResultDir = new File(YanelHomeDir + "/src/test/test-results");

        Project project = new Project();
        project.init();

        try {
            FileSet fileset = new FileSet();
            fileset.setProject(project);
            fileset.setDir(testResultDir);

            final Delete task = (Delete) project.createTask("delete");
            task.setTaskName("delete old tests");
            task.addFileset(fileset);
            task.init();
            task.execute();

        } catch (Exception e) {
            log.error(e);
        }
    }

    /**
     * get time as string
     * @return timestamp (yyyy-MM-dd-HH-mm-ss)
     */
    private String getTime() {
        Calendar cal = Calendar.getInstance(java.util.TimeZone.getDefault());
        String DATE_FORMAT = "yyyy-MM-dd-HH-mm-ss";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
        sdf.setTimeZone(java.util.TimeZone.getDefault());
        String time = sdf.format(cal.getTime());
        return time;
    }
}
