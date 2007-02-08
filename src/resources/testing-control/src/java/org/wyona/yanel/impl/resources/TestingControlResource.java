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
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.dom.DOMSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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

import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yarep.core.NoSuchNodeException;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryFactory;
import org.wyona.yanel.core.util.PathUtil;
import org.wyona.yarep.util.RepoPath;
import org.wyona.yarep.util.YarepUtil;

/**
 * 
 */
public class TestingControlResource extends Resource implements ViewableV1 {

    private static final String JUNIT_JAR = "yanel-JunitTests.jar";
    private static final String HTMLUNIT_JAR = "yanel-HtmlUnitTests.jar";
    private static Category log = Category.getInstance(TestingControlResource.class);

    private File JunitJarLocation;
    private File HtmlunitJarLocation;
    private File tmpResultDir;
    private String errorMessage;

    public TestingControlResource() {
    }

    public ViewDescriptor[] getViewDescriptors() {
        return null;
    }

    public View getView(Path path, String viewId) {
        View defaultView = new View();
        defaultView.setMimeType("application/xml");
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");
        defaultView.setInputStream(new java.io.StringBufferInputStream(sb.toString()));
        return defaultView;
    }

    public View getView(HttpServletRequest request, String viewId) throws Exception {
        Path path = new Path(request.getServletPath());

        // the junit and htmlunit jars
        String WEBINFPath = request.getSession().getServletContext().getRealPath("WEB-INF");
        HtmlunitJarLocation = new File(WEBINFPath + File.separator + "lib" + File.separator
                + HTMLUNIT_JAR);
        JunitJarLocation = new File(WEBINFPath + File.separator + "lib" + File.separator
                + JUNIT_JAR);

        if (!HtmlunitJarLocation.exists()) {
            errorMessage = errorMessage + "\n HtmlUnit-Tests not found.";
        }
        if (!JunitJarLocation.exists()) {
            errorMessage = errorMessage + "\n JUnit-Tests not found.";
        }

        // create tmp-directory to write the tests
        if (!new File(request.getSession().getServletContext().getRealPath("tmp")).exists()) {
            if (!new File(request.getSession().getServletContext().getRealPath("tmp")).mkdir()) {
                errorMessage = errorMessage + "\n Creation of tmp directory faild.";
            }
        }

        String submit = request.getParameter("submit");
        String type = request.getParameter("type");
        String archivedResults = request.getParameter("archived-results");
        String[] testnames = request.getParameterValues("testnames");

        View defaultView = new View();
        if (errorMessage != null) {
            return error();
        }
        if(request.getSession().getAttribute("tmpResultDir") != null){
            return showProgress(path, defaultView);
        }
        if (archivedResults != null) {
            return showArchive(path, defaultView, archivedResults);
        }
        if (submit != null) {
            if (testnames != null) {
                return executeTests(path, defaultView, testnames);
            }
            return plainRequest(path, defaultView);
        } else {

            return plainRequest(path, defaultView);
        }
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
        sb.append("<ul id=\"htmlunit\">");
        String[] allHtmlUnitTestNames = getAllTestNames("htmlunit");

        for (int i = 0; i < allHtmlUnitTestNames.length; i++) {
            sb.append("<li title=\""
                    + allHtmlUnitTestNames[i].substring(allHtmlUnitTestNames[i].lastIndexOf("/") + 1)
                    + "\">" + allHtmlUnitTestNames[i] + "</li>");
        }

        sb.append("</ul>");
        sb.append("<ul id=\"junit\">");
        String[] allJUnitTestNames = getAllTestNames("junit");
        for (int i = 0; i < allJUnitTestNames.length; i++) {
            sb.append("<li title=\""
                    + allJUnitTestNames[i].substring(allJUnitTestNames[i].lastIndexOf("/") + 1)
                    + "\">" + allJUnitTestNames[i] + "</li>");
        }
        sb.append("</ul>");
        sb.append("</body>");
        sb.append("</html>");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        File result2htmlXsltFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile()
                .getParentFile()
                .getAbsolutePath(), "xslt" + File.separator + "chooseTest.xsl");
        Transformer transResult2html = TransformerFactory.newInstance()
                .newTransformer(new StreamSource(result2htmlXsltFile));
        transResult2html.transform(new StreamSource(new java.io.StringBufferInputStream(sb.toString())),
                new StreamResult(byteArrayOutputStream));

        Transformer transformer = globalTransformer(path, contentRepo);
        // TODO: Is this the best way to generate an InputStream from an
        // OutputStream?
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        transformer.transform(new StreamSource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())),
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
        
        // prepare tmpResultDir
        if (request.getSession().getAttribute("tmpResultDir") == null) {
            String uuid = new java.rmi.server.UID().toString();
            tmpResultDir = new File(request.getSession().getServletContext().getRealPath("tmp"
                    + File.separator + "test-results-" + uuid));
            request.getSession().setAttribute("tmpResultDir", tmpResultDir);
        } else {
            tmpResultDir = (File) request.getSession().getAttribute("tmpResultDir");
        }        

        request.getSession().setAttribute("exectime", getTime());
        request.getSession().setAttribute("numberOfTests", ""+testnames.length);

        // delete the resultdir before making new tests
        tmpResultDir.mkdir();
        Runnable runtest = new ExecuteTests(testnames,
                JunitJarLocation,
                HtmlunitJarLocation,
                tmpResultDir);
        new Thread(runtest).start();
        return showProgress(path, defaultView);
    }

    private View showProgress(Path path, View defaultView) throws Exception,
    TransformerConfigurationException, TransformerFactoryConfigurationError,
    NoSuchNodeException, TransformerException{
        Repository contentRepo;
        RepoPath rp = contentRepo(path);
        contentRepo = rp.getRepo();
        
        //get tmpResultDir from session
        tmpResultDir = (File) request.getSession().getAttribute("tmpResultDir");
        
        //number of executed tests
        int numberOfTests = Integer.parseInt((String) request.getSession().getAttribute("numberOfTests"));
        
        String resultName = request.getSession().getAttribute("exectime") + "-tests.xml";

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        
        if (tmpResultDir.list().length < numberOfTests) {
            // geting the test results
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document result = builder.newDocument();

            Project project = new Project();

            // aggregate all tests in the tmp dir
            try {
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
            } catch (Exception e) {
                log.error(e);
            }

            File result2htmlXsltFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile()
                    .getParentFile()
                    .getAbsolutePath(), "xslt" + File.separator + "result2html.xsl");
            Transformer transResult2html = TransformerFactory.newInstance()
                    .newTransformer(new StreamSource(result2htmlXsltFile));
            transResult2html.setParameter("testing.result.title", "stillTesting");
            transResult2html.setParameter("testing.number.requested.tests", ""+numberOfTests);
            transResult2html.transform(new DOMSource(result),
                    new StreamResult(byteArrayOutputStream));

        } else {
            request.getSession().removeAttribute("tmpResultDir");
            request.getSession().removeAttribute("exectime");
            request.getSession().removeAttribute("numberOfTests");
            
            // geting the test results
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document result = builder.newDocument();
            Project aggregatorproject = new Project();
            // aggregate all tests in the tmp dir
            try {
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
            } catch (Exception e) {
                log.error(e);
            }

            // write test result to repo
            OutputStream out = contentRepo.getOutputStream(new Path("/test-results-archive/"
                    + resultName));
            javax.xml.transform.TransformerFactory.newInstance()
                    .newTransformer()
                    .transform(new javax.xml.transform.dom.DOMSource(result),
                            new javax.xml.transform.stream.StreamResult(out));
            out.close();
            
            //delete the test dir
            FileUtils.deleteDirectory(tmpResultDir);   
            
            File result2htmlXsltFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile()
                    .getParentFile()
                    .getAbsolutePath(), "xslt" + File.separator + "result2html.xsl");
            Transformer transResult2html = TransformerFactory.newInstance()
                    .newTransformer(new StreamSource(result2htmlXsltFile));
            transResult2html.setParameter("testing.result.title", "testDone");
            transResult2html.transform(new DOMSource(result),
                    new StreamResult(byteArrayOutputStream));
        }

        Transformer transformer = globalTransformer(path, contentRepo);
        // TODO: Is this the best way to generate an InputStream from an
        // OutputStream?
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        transformer.transform(new StreamSource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())),
                new StreamResult(baos));
        defaultView.setMimeType("application/xhtml+xml");
        defaultView.setInputStream(new java.io.ByteArrayInputStream(baos.toByteArray()));
        return defaultView;
    }

    private View showArchive(Path path, View defaultView, String archivedResults) throws Exception,
            TransformerConfigurationException, TransformerFactoryConfigurationError,
            NoSuchNodeException, TransformerException {
        Repository contentRepo;
        RepoPath rp = contentRepo(path);
        contentRepo = rp.getRepo();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        File result2htmlXsltFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile()
                .getParentFile()
                .getAbsolutePath(), "xslt" + File.separator + "result2html.xsl");
        Transformer transResult2html = TransformerFactory.newInstance()
                .newTransformer(new StreamSource(result2htmlXsltFile));
        transResult2html.setParameter("testing.result.title",
                archivedResults.substring(archivedResults.lastIndexOf("/") + 1,
                        archivedResults.length()));

        InputStream inputStream = rp.getRepo()
                .getInputStream(new org.wyona.yarep.core.Path(archivedResults));
        transResult2html.transform(new StreamSource(inputStream),
                new StreamResult(byteArrayOutputStream));

        Transformer transformer = globalTransformer(path, contentRepo);
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

    private Transformer globalTransformer(Path path, Repository repo) throws Exception,
            TransformerConfigurationException {
        RepoPath rp = contentRepo(path);
        if (getXSLTPath(path) != null) {
            Transformer transformer = TransformerFactory.newInstance()
                    .newTransformer(new StreamSource(repo.getInputStream(getXSLTPath(path))));
            transformer.setParameter("yanel.path.name", path.getName());
            transformer.setParameter("yanel.path", path.toString());
            transformer.setParameter("yanel.back2context", backToContext()+backToRoot());
            transformer.setParameter("yarep.back2realm", backToRoot());
            return transformer;
        } else {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            return transformer;
        }
    }

    /**
     * Get XSLT path
     */
    private Path getXSLTPath(Path path) {
        String xsltPath = getRTI().getProperty("xslt");
        if (xsltPath != null)
            return new Path(xsltPath);
        log.info("No XSLT Path within: " + path);
        return null;
    }

    protected RepositoryFactory getRepositoryFactory() {
        return yanel.getRepositoryFactory("DefaultRepositoryFactory");
    }

    protected RepositoryFactory getRTIRepositoryFactory() {
        return yanel.getRepositoryFactory("RTIRepositoryFactory");
    }

    /**
     * @return a String with as many ../ as it needs to go back to from current realm to context
     */
    private String backToContext() {
        String backToContext = "";
        int steps = realm.getMountPoint().split("/").length - 1;

        for (int i = 0; i < steps; i++) {
            backToContext = backToContext + "../";
        }
        return backToContext;
    }
     
    /**
     * @return a String with as many ../ as it needs to go back to from current resource to the realm-root
     */
    private String backToRoot() {
        String backToRoot = "";
        int steps;
        
        if (getPath().endsWith("/") && !getPath().equals("/")) {
            steps =  getPath().split("/").length - 1;
        } else {
            steps =  getPath().split("/").length - 2;
        }
        for (int i = 0; i < steps; i++) {
            backToRoot = backToRoot + "../";
        }
        return backToRoot;
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
     * get an error screen
     * @return a view with an error screen
     */
    private View error() {
        View defaultView = new View();
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        sb.append("<head>");
        sb.append("<title>Testing Control</title>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<div id=\"contenBody\">");
        sb.append("<h1>Testing Control, something is wrong:</h1>");
        sb.append(errorMessage);
        sb.append("</div>");
        sb.append("</body>");
        sb.append("</html>");

        defaultView.setMimeType("application/xhtml+xml");
        defaultView.setInputStream(new java.io.StringBufferInputStream(sb.toString()));
        return defaultView;
    }
}
