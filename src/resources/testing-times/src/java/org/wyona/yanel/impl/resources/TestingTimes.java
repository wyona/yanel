/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources;

import java.awt.Color;
import java.awt.geom.Arc2D.Double;
import java.io.InputStream;

import org.apache.log4j.Category;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.util.PathUtil;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.wyona.yanel.core.Path;
import org.wyona.yarep.core.NoSuchNodeException;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryFactory;
import org.wyona.yarep.util.RepoPath;
import org.wyona.yarep.util.YarepUtil;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleInsets;
import org.jfree.chart.ChartUtilities;
import org.jfree.data.time.MovingAverage;

/**
 * 
 */
public class TestingTimes extends Resource implements ViewableV1 {

    private static Category log = Category.getInstance(TestingTimes.class);
    private static final String testResultsArchiveLocation = "/test-results-archive/";
    private  String numberOfResults = ""+20;
    private int chartWidth = 600;
    private int chartHeight = 450;
    private Path path;

    /**
     * 
     */
    public TestingTimes() {
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
        this.path = path;
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

        String showXML = request.getParameter("showXML");
        String showImage = request.getParameter("showImage");
        String showTest = request.getParameter("showTest");
        if(showTest == null){
            showTest = "all";
        }
        String setNumberOfResults = request.getParameter("setNumberOfResults");
        String configNumberOfResults = request.getParameter("configNumberOfResults");
        this.path = new Path(request.getServletPath());
        View defaultView = new View();
        
        if(setNumberOfResults != null){
            numberOfResults = setNumberOfResults;
        }
        //configuration
        if (configNumberOfResults != null ) {
            numberOfResults = configNumberOfResults;
            request.getSession().setAttribute("numberOfResults",configNumberOfResults);
        }
        if(request.getSession().getAttribute("numberOfResults") != null){
            numberOfResults = (String) request.getSession().getAttribute("numberOfResults");
        }
        if (showXML != null) {
            return showXML( defaultView, showTest);
        }
        if (showImage != null) {
            return showImage( defaultView, showImage);
        }
        return plainRequest(defaultView, showTest);
    }

    private View plainRequest(View defaultView, String testName) throws Exception,
    TransformerConfigurationException, TransformerFactoryConfigurationError,
    NoSuchNodeException, TransformerException {
        Repository contentRepo;
        RepoPath rp = contentRepo();
        contentRepo = rp.getRepo();
        
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        sb.append("<head>");
        sb.append("<title>Testing Control</title>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<div id=\"contenBody\">");
        sb.append("<ul id=\"testnames\">");
        for (Iterator iter = distinctChildNodesNames(getCombinedResults("all")).iterator(); iter.hasNext();) {
            String element = (String) iter.next();
            sb.append("<li>"+element+"</li>");
        }
        sb.append("</ul>");
        
        String[] configResultNumbers = {""+5,""+10,""+20,""+50,""+100,"all"};
        sb.append("<ul id=\"configResultNumbers\">");
        for (int i = 0; i < configResultNumbers.length; i++) {
            if(configResultNumbers[i].equals(numberOfResults)){
                sb.append("<li id=\"selected\">"+configResultNumbers[i]+"</li>");
            }else{
                sb.append("<li>"+configResultNumbers[i]+"</li>");
            }
        }
        sb.append("</ul>");
        
        sb.append("</div>");
        sb.append("</body>");
        sb.append("</html>");
        
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        File result2htmlXsltFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile()
                .getParentFile()
                .getAbsolutePath(),
                "xslt" + File.separator + "testNames2html.xsl");
        Transformer transdoc = TransformerFactory.newInstance()
        .newTransformer(new StreamSource(result2htmlXsltFile));
        
        transdoc.setParameter("testname", testName);
        
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        transdoc.transform(new StreamSource(new java.io.StringBufferInputStream(sb.toString())),
                new StreamResult(baos));
        
        Transformer transformer = globalTransformer(contentRepo);
        java.io.ByteArrayOutputStream ByteArrayOutputStream = new java.io.ByteArrayOutputStream();
        transformer.transform(new StreamSource(new ByteArrayInputStream(baos.toByteArray())),
                new StreamResult(ByteArrayOutputStream));
        
        defaultView.setMimeType("application/xhtml+xml");
        defaultView.setInputStream(new java.io.ByteArrayInputStream(ByteArrayOutputStream.toByteArray()));
        return defaultView;
    }
    
    private View showXML(View defaultView, String name) throws Exception, TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException{
        Document doc = getCombinedResults(name);
        
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        
        Transformer transdoc = TransformerFactory.newInstance().newTransformer();
        transdoc.transform(new DOMSource(doc), new StreamResult(byteArrayOutputStream));

        defaultView.setMimeType("application/xhtml+xml");
        defaultView.setInputStream(new java.io.ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        return defaultView;
    }
    
    private View showImage(View defaultView, String name) throws Exception,
            TransformerConfigurationException, TransformerFactoryConfigurationError,
            TransformerException {
        Document doc = getCombinedResults(name);
        boolean showCases = false;
        if (!name.equals("all")) {
            showCases = true;
        }
        JFreeChart chart = createChart(createTestTimeDataset(doc, showCases), name);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ChartUtilities.writeChartAsPNG(byteArrayOutputStream, chart, chartWidth, chartHeight);

        defaultView.setMimeType("image/png");
        defaultView.setInputStream(new java.io.ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        return defaultView;
    }
    
    private Document getCombinedResults(String name) throws Exception,
    TransformerConfigurationException, TransformerFactoryConfigurationError,
    NoSuchNodeException, TransformerException {
        
        Repository contentRepo;
        RepoPath rp = contentRepo();
        contentRepo = rp.getRepo();
        
        // create the dom tree
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();
        Element rootElement = doc.createElement("test-results");
        doc.appendChild(rootElement);
        
        try {
            org.wyona.yarep.core.Path testResultArchivePath = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.getParent()
                    .toString()
                    + testResultsArchiveLocation),
                    getRepositoryFactory())
                    .getPath();
            org.wyona.yarep.core.Path[] children = contentRepo.getChildren(testResultArchivePath);
            
            //to limit the aggregation
            int omittedChildren = 1;
            if(!numberOfResults.equals("all") && children.length > Integer.parseInt(numberOfResults)){
                omittedChildren = children.length / Integer.parseInt(numberOfResults);
            }
            
            for (int i = 0; i < children.length; i = i + omittedChildren) {
                if (contentRepo.isResource(children[i])) {
                    //get date from filename
                    String date = children[i].getName().substring(0,
                            children[i].getName().indexOf("-tests"));
                    
                    InputStream result = rp.getRepo().getInputStream(children[i]);
                    
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    File result2htmlXsltFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile()
                            .getParentFile()
                            .getAbsolutePath(),
                            "xslt" + File.separator + "unitResult2time.xsl");
                    Transformer transResult2html = TransformerFactory.newInstance()
                    .newTransformer(new StreamSource(result2htmlXsltFile));
                    transResult2html.setParameter("testname", name);
                    transResult2html.setParameter("date", date);
                    
                    transResult2html.transform(new StreamSource(result),
                            new StreamResult(byteArrayOutputStream));
                    //TODO is there a better way to merge xml files, maybe with sax?
                    Document testsuiteDoc = builder.parse(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
                    NodeList testSuiteElem = testsuiteDoc.getDocumentElement().getChildNodes();
                    
                    for (int j = 0; j < testSuiteElem.getLength(); j++) {
                        Element elem = (Element) testSuiteElem.item(j);
                        if ("testsuite".equals(elem.getNodeName())) {
                            Node dup = doc.importNode(elem, true);
                            rootElement.appendChild(dup);
                        } else {
                            log.warn("the Document " + children[i]
                                                                + " is not a valid testresult XML document");
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e);
        }
        return doc;
    }

    private HashSet distinctChildNodesNames(Document doc) throws Exception{
        HashSet names = new HashSet();
        Element rootElement = doc.getDocumentElement();
        NodeList nodeList = rootElement.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            NamedNodeMap attributes = node.getAttributes();
            Node name = attributes.getNamedItem("name");
            names.add(name.getNodeValue());
        }
        return names;
    }
    
    /**
     * Creates a dataset, consisting all testsuite nodes in document
     *
     * @return The dataset.
     */
    private XYDataset createTestTimeDataset(Document doc, boolean showCases) throws Exception{
        
        HashSet distinctSuiteNames = new HashSet();
        HashSet distinctCaseNames = new HashSet();
        TimeSeriesCollection dataset = new TimeSeriesCollection();
            
        Element rootElement = doc.getDocumentElement();
        NodeList nodeList = rootElement.getElementsByTagName("testsuite");
            
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            NamedNodeMap testSuiteAttributes = node.getAttributes();
            String date = testSuiteAttributes.getNamedItem("date").getNodeValue();
            //parse date
            String[] dateFields = date.split("-");
            String time = testSuiteAttributes.getNamedItem("time").getNodeValue();
            String name = testSuiteAttributes.getNamedItem("name").getNodeValue()+" Suite";
              
            if(distinctSuiteNames.contains(name)){
                dataset.getSeries(name).add(new Second(Integer.parseInt(dateFields[5]),Integer.parseInt(dateFields[4]),Integer.parseInt(dateFields[3]),Integer.parseInt(dateFields[2]),Integer.parseInt(dateFields[1]),Integer.parseInt(dateFields[0])), java.lang.Double.parseDouble(time));
            } else {
                distinctSuiteNames.add(name);
                dataset.addSeries( new TimeSeries(name, Second.class));
                dataset.getSeries(name).add(new Second(Integer.parseInt(dateFields[5]),Integer.parseInt(dateFields[4]),Integer.parseInt(dateFields[3]),Integer.parseInt(dateFields[2]),Integer.parseInt(dateFields[1]),Integer.parseInt(dateFields[0])), java.lang.Double.parseDouble(time));
            }

            if(showCases){
                Node testcaseNode = node.getFirstChild();
                NamedNodeMap testCaseAttributes = testcaseNode.getAttributes();
                String caseDate = testCaseAttributes.getNamedItem("date").getNodeValue();
                //parse date
                String[] caseDateFields = date.split("-");
                String caseTime = testCaseAttributes.getNamedItem("time").getNodeValue();
                String caseName = name+": "+testCaseAttributes.getNamedItem("name").getNodeValue()+" Case";
                
                if(distinctCaseNames.contains(caseName)){
                    dataset.getSeries(caseName).add(new Second(Integer.parseInt(caseDateFields[5]),Integer.parseInt(caseDateFields[4]),Integer.parseInt(caseDateFields[3]),Integer.parseInt(caseDateFields[2]),Integer.parseInt(caseDateFields[1]),Integer.parseInt(caseDateFields[0])), java.lang.Double.parseDouble(caseTime));
                } else {
                    distinctCaseNames.add(caseName);
                    dataset.addSeries( new TimeSeries(caseName, Second.class));
                    dataset.getSeries(caseName).add(new Second(Integer.parseInt(caseDateFields[5]),Integer.parseInt(caseDateFields[4]),Integer.parseInt(caseDateFields[3]),Integer.parseInt(caseDateFields[2]),Integer.parseInt(caseDateFields[1]),Integer.parseInt(caseDateFields[0])), java.lang.Double.parseDouble(caseTime));
                }
            }
        }
        return dataset;
    }    
    
    /**
     * Creates the chart.
     * 
     * @param dataset  a dataset.
     * @param title  a title
     * 
     * @return A chart.
     */
    private static JFreeChart createChart(XYDataset dataset, String title) {

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            title,  // title
            "Date",             // x-axis label
            "Execution Time",   // y-axis label
            dataset,            // data
            true,               // create legend?
            true,               // generate tooltips?
            false               // generate URLs?
        );

        chart.setBackgroundPaint(Color.white);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(2.0, 2.0, 2.0, 2.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("yy/MM/dd"));
        return chart;

    }
    
    private Transformer globalTransformer(Repository repo) throws Exception,
            TransformerConfigurationException {
        RepoPath rp = contentRepo();
        if (getXSLTPath() != null) {
            Transformer transformer = TransformerFactory.newInstance()
                    .newTransformer(new StreamSource(repo.getInputStream(getXSLTPath())));
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
    private Path getXSLTPath() {
        String xsltPath = getRTI().getProperty("xslt");
        if (xsltPath != null)
            return new Path(xsltPath);
        log.info("No XSLT Path within: " + path);
        return null;
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
    
    private RepoPath contentRepo() throws Exception {
        return new YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()),
                getRepositoryFactory());
    }

    protected RepositoryFactory getRepositoryFactory() {
        return yanel.getRepositoryFactory("DefaultRepositoryFactory");
    }
}
