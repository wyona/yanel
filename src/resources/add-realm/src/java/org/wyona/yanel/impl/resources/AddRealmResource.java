/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources;

import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.core.transformation.I18nTransformer;
import org.wyona.yanel.core.util.HttpServletRequestHelper;
import org.wyona.yarep.core.Node;
import org.wyona.yarep.core.NodeType;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryException;
import org.wyona.yanel.core.util.PathUtil;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.lenya.search.crawler.DumpingCrawler;
import org.apache.log4j.Category;

import websphinx.EventLog;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


/**
 *
 */
public class AddRealmResource extends Resource implements ViewableV2 {

    private static final String CRAWLER_JAR = "yanel-crawler.jar";
    private static Category log = Category.getInstance(AddRealmResource.class);
    private final static int INSIDE_TAG = 0;
    private final static int OUTSIDE_TAG = 1;
    
    private String defaultLanguage = "en";
    private String language = null;
    private String parameterName = null;
    private String parameter = null;
    private String parameterErrorName = null;
    private String parameterError = null;
    
    private File CrawlerJarLocation;
    private File tmpResultDir;
    private String errorMessage;
    
    private HashMap parameters = new HashMap();

    /**
     *
     */
    public AddRealmResource() {
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
    public View getView(String viewId) throws Exception {
        
        String path = getPath();
        HttpServletRequest request = getRequest();
        
        // the crawler jar
        String WEBINFPath = request.getSession().getServletContext().getRealPath("WEB-INF");
        CrawlerJarLocation = new File(WEBINFPath + File.separator + "lib" + File.separator
                + CRAWLER_JAR);
        
        if (!CrawlerJarLocation.exists()) {
            errorMessage = errorMessage + "\n Crawler not found.";
        }
        
        // create tmp status file
        if (!new File(request.getSession().getServletContext().getRealPath("tmp")).exists()) {
            if (!new File(request.getSession().getServletContext().getRealPath("tmp")).mkdir()) {
                errorMessage = errorMessage + "\n Creation of tmp directory faild.";
            }
        }
        
        // Get language
        try {
            language = request.getParameter("yanel.meta.language");
        } catch(Exception e) {
            log.debug("language param is not found will use default : " + language);
            language = defaultLanguage;
        }
        if(language == null || ("").equals(language)) {
            log.debug("language param is empty or null : " + language);
            language = defaultLanguage;
        }

        Transformer transformer = null;
        I18nTransformer i18nTransformer = new I18nTransformer("add-realm", language);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        View defaultView = new View();

        try {
            
        	 // Check if data was sumbitted (realm ID, realm Name, URL to be dumped, depth of crawling, max number of pages)
            boolean submit = false;
            Enumeration enumeration = request.getParameterNames();
            while(enumeration.hasMoreElements()){
                if(enumeration.nextElement().toString().equals("submit")) 
                    submit = true;
            }
            
        	File XSLTFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile().getParentFile().getAbsolutePath(), "xslt" + File.separator + "add-realm.xsl");
        	File inputXMLFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile().getParentFile().getAbsolutePath(), "xml" + File.separator + "input-screen.xml");
        	File statusXMLFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile().getParentFile().getAbsolutePath(), "xml" + File.separator + "status-screen.xml");
            transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(XSLTFile));
            
            // Add HashMap keys with dummy values for form fields
            String[] parameterNames = { "realmid", "realmname", "url", "fslocation", "crawldepth", "crawlmaxpages" };
            for (int i=0; i<parameterNames.length; i++) {
            	String property = getConfiguration().getProperty(parameterNames[i]);
            	boolean propertyExists = getConfiguration().containsKey(parameterNames[i]);
            	
            	if (propertyExists == true) {
            		if (property == null || ("").equals(property)) {
            			parameters.put(parameterNames[i], "default");
            		} else {
            			parameters.put(parameterNames[i], property);
            		}
            	} else {
            		parameters.put(parameterNames[i], "");
        		}
            }
            
            Set keys = parameters.keySet();
            Iterator keysIterator = keys.iterator();
            
            if(submit) {
            	
                while (keysIterator.hasNext()) {
                    parameterName = (String) keysIterator.next();
                    parameter = HttpServletRequestHelper.getParameter(request, parameterName);
                    
                    if (("fslocation").equals(parameterName)) {
                    	parameters.put(parameterName, parameter);
                        transformer.setParameter(parameterName, parameters.get(parameterName).toString());
                    } else if (parameter == null || ("").equals(parameter)) {
                        parameterErrorName = "error-" + parameterName;
                        parameterError = "Please enter correct value for '" + parameterName + "'";
                        transformer.setParameter(parameterName, "ERROR:" + parameterError);
                    } else {
                        parameters.put(parameterName, parameter);
                        transformer.setParameter(parameterName, parameters.get(parameterName).toString());
                    }
                }
                
                if (parameterError == null || ("").equals(parameterError)) {
                	File fslocationValue = null;
                	if (parameters.get("fslocation").toString() == null || ("").equals(parameters.get("fslocation").toString())) {
                		fslocationValue = null;
                	} else if (parameters.get("fslocation").toString().equals("default")) {
                		fslocationValue = null;
                	} else {
                		fslocationValue = new File(parameters.get("fslocation").toString());
                	}
                    getYanel().getRealmConfiguration().copyRealm("from-scratch-realm-template", 
                            parameters.get("realmid").toString(), 
                            parameters.get("realmname").toString(),
                            "/" + parameters.get("realmid").toString() + "/", 
                            fslocationValue);
                    transformer.setParameter("submitted", "true");
                    transformer.setParameter("yanel.back2context", PathUtil.backToContext(realm, getPath()));
                    
                    String crawlStartURL = (String)parameters.get("url"); 
                    if (crawlStartURL != null && crawlStartURL.length() > 0) {
                        int maxPages = Integer.parseInt((String)parameters.get("crawlmaxpages"));
                        int maxDepth = Integer.parseInt((String)parameters.get("crawldepth"));
                        String realmID = parameters.get("realmid").toString();
                        
                        importSite(crawlStartURL, maxPages, maxDepth, realmID);
                    }
                }
                
                transformer.transform(new javax.xml.transform.stream.StreamSource(statusXMLFile), new StreamResult(byteArrayOutputStream));
                
            } else {
            	
                while (keysIterator.hasNext()) {
                    parameterName = (String) keysIterator.next();
                    transformer.setParameter(parameterName, parameters.get(parameterName).toString());
                }
                
                transformer.transform(new javax.xml.transform.stream.StreamSource(inputXMLFile), new StreamResult(byteArrayOutputStream));
                
            }
            
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            saxParser.parse(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), i18nTransformer);
            
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
        
        defaultView.setMimeType(getMimeType(viewId));
        defaultView.setInputStream(i18nTransformer.getInputStream());
        return defaultView;
    }
    
    /**
     * Crawls and external site and imports it into a realm.
     * @param crawlStartURL
     * @param maxPages
     * @param maxDepth
     * @param realmID
     * @throws Exception
     */
    protected void importSite(String crawlStartURL, int maxPages, int maxDepth, String realmID) throws Exception {
        String crawlScopeURL = crawlStartURL; 
        URL url = new URL(crawlStartURL);
        String path = url.getPath();
        if (path.length() != 0 && !path.endsWith("/") && path.indexOf("/") > -1) {
            crawlScopeURL = crawlStartURL.substring(0, crawlStartURL.lastIndexOf("/"));
        }
        
        String dumpDir = System.getProperty("java.io.tmpdir") + File.separator + "import_" + System.currentTimeMillis();
        DumpingCrawler crawler = new DumpingCrawler(crawlStartURL, crawlScopeURL, dumpDir);
        crawler.setMaxPages(maxPages);
        crawler.setMaxDepth(maxDepth);
        
        EventLog eventLog = new EventLog(System.out);
        crawler.addCrawlListener(eventLog);
        crawler.addLinkListener(eventLog);
       
        // create dump:
        // TODO: start crawler in thread and show progress
        crawler.run();
        crawler.close();
        
        // import dump into realm:
        Realm realm = getYanel().getRealmConfiguration().getRealm(realmID);
        deleteRepositoryContent(realm.getRepository());
        deleteRepositoryContent(realm.getRTIRepository());
        Node root = realm.getRepository().getRootNode();
        importContent(new File(dumpDir), root);
        
        // remove temp dump dir
        FileUtils.deleteDirectory(new File(dumpDir));
        
        fixRootNode(crawlStartURL, root);
        addResourceConfiguration(realm.getRTIRepository());
    }
    
    /**
     * Imports the content of the given directory into the repository as child nodes
     * of the given node. This will recursively add the complete subtree.
     * If a Node already exists in the repository, it will be overwritten.
     * @param dir
     * @param node
     * @throws IOException
     * @throws RepositoryException
     */
    protected void importContent(File dir, Node node) throws IOException, RepositoryException{
        File[] children = dir.listFiles();
        for (int i=0; i<children.length; i++) {
            File file = children[i];
            String name = file.getName();
            Node childNode;
            if (file.isDirectory()) {
                if (node.hasNode(name)) {
                    childNode = node.getNode(name);
                } else {
                    childNode = node.addNode(name, NodeType.COLLECTION);
                }
                // recursion:
                importContent(file, childNode);
            } else {
                if (node.hasNode(name)) {
                    childNode = node.getNode(name);
                } else {
                    childNode = node.addNode(name, NodeType.RESOURCE);
                }
                String mimeType = guessMimeType(FilenameUtils.getExtension(file.getName()));
                InputStream is = new FileInputStream(file);
                OutputStream os = childNode.getOutputStream();
                if (mimeType.equals("text/html")) {
                    addIntrospectionLink(is, os);
                } else {
                    byte[] buf = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = is.read(buf)) != -1) {
                        os.write(buf, 0, bytesRead);
                    }
                }
                os.flush();
                os.close();
                is.close();
                childNode.setMimeType(mimeType);
            }
        }
    }
    
    /**
     * Adds a yanel introspection link element to the head element of the current page.
     * Note: this method is stream based and does not consider character encoding, therefore
     * it works only for data with ascii-compatible encoding like utf-8 or iso-8859-1. 
     * @param is stream of the source html page
     * @param os stream of the result html page
     * @throws IOException
     */
    protected void addIntrospectionLink(InputStream is, OutputStream os) throws IOException {
        int b;
        int state = OUTSIDE_TAG;
        StringBuffer tagNameBuf = null;
        while ((b = is.read()) != -1) {
            switch (state) {
            case OUTSIDE_TAG:
                if (b == '<') {
                    tagNameBuf = new StringBuffer();
                    state = INSIDE_TAG;
                }
                os.write(b);
                break;
            case INSIDE_TAG:
                os.write(b);
                if (b == '>') {
                    state = OUTSIDE_TAG;
                    String tagName = tagNameBuf.toString();
                    if (tagName.startsWith("head")) {
                        String introspectionLink = "<link rel=\"neutron-introspection\" type=\"application/neutron+xml\" href=\"?yanel.resource.usecase=introspection\"/>";
                        os.write(introspectionLink.getBytes());
                    }
                } else {
                    tagNameBuf.append((char)b);
                }
                break;
            }
        }
    }
    
    /**
     * Creates a redirect from the repository root node to the crawl root page.
     * This is necessary to make the root page of the crawl 
     * accessible at root url of the new realm.
     * Example: 
     * crawlStartURL: http://foo.bar/start.html
     * new realm id:  foo-realm
     * -> /foo-realm/ will redirect to /foo-realm/start.html 
     * @param crawlStartURL
     * @param root
     * @throws RepositoryException 
     */
    protected void fixRootNode(String crawlStartURL, Node root) {
        try {
            URL url = new URL(crawlStartURL);
            String path = url.getPath();
            String crawlRoot = null;
            if (path.length() == 0 || path.endsWith("/")) {
                crawlRoot = "index.html";
            } else if (path.indexOf("/") > -1) {
                crawlRoot = path.substring(path.lastIndexOf("/") + 1);
            }
            log.debug("crawlRoot: " + crawlRoot);
            if (crawlRoot != null && root.hasNode(crawlRoot)) {
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(root.getOutputStream()));
                writer.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
                writer.println("<html>");
                writer.println("<head>");
                writer.println("<meta http-equiv=\"refresh\" content=\"0; url=" + crawlRoot + "\"/>");
                writer.println("</head>");
                writer.println("<body/>");
                writer.println("</html>");
                writer.flush();
                writer.close();
            }
        } catch (Exception e) {
            log.error(e, e);
            // ignore 
        }
    }
    
    /**
     * Adds a resource configuration file for the root node of the repository.
     * @param repository
     * @throws RepositoryException
     */
    protected void addResourceConfiguration(Repository repository) throws RepositoryException {
        Node node = repository.getRootNode().addNode(".yanel-rc", NodeType.RESOURCE);
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(node.getOutputStream()));
        writer.println("<?xml version=\"1.0\"?>");
        writer.println("<yanel:resource-config xmlns:yanel=\"http://www.wyona.org/yanel/rti/1.0\">");
        writer.println("<yanel:rti name=\"file\" namespace=\"http://www.wyona.org/yanel/resource/1.0\"/>");
        writer.println("<yanel:property name=\"mime-type\" value=\"text/html\"/>");
        writer.println("</yanel:resource-config>");
        writer.flush();
        writer.close();
    }
    
    /**
     * Delete all nodes from the repository except the root node.
     * @param repository
     * @throws RepositoryException
     */
    protected void deleteRepositoryContent(Repository repository) throws RepositoryException {
        Node[] children = repository.getRootNode().getNodes();
        for (int i=0; i<children.length; i++) {
            children[i].delete();
        }
    }
    
    /**
     * Returns the mime-type according to the given file extension.
     * Default is application/octet-stream.
     * @param extension
     * @return
     */
    protected String guessMimeType(String extension) {
        String ext = extension.toLowerCase();
        if (ext.equals("html") || ext.equals("htm")) return "text/html";
        if (ext.equals("css")) return "text/css";
        if (ext.equals("txt")) return "text/plain";
        if (ext.equals("js")) return "application/x-javascript";
        if (ext.equals("jpg") || ext.equals("jpg")) return "image/jpeg";
        if (ext.equals("gif")) return "image/gif";
        if (ext.equals("pdf")) return "application/pdf";
        if (ext.equals("zip")) return "application/zip";
        //TODO: add more
        return "application/octet-stream"; // default
    }
    
    /* TODO: add showProgress
    private View showProgress(Path path, View defaultView) throws Exception {
        
        //get tmpResultDir from session
        tmpResultDir = (File) request.getSession().getAttribute("tmpResultDir");
        
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        Transformer transformer = null;
        transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(inputXSLTFile));
        // TODO: Is this the best way to generate an InputStream from an
        // OutputStream?
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        transformer.transform(new StreamSource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())),
                new StreamResult(baos));
        defaultView.setMimeType(getMimeType(viewId));
        defaultView.setInputStream(new java.io.ByteArrayInputStream(baos.toByteArray()));
        return defaultView;
    }*/
   
    /**
     * 
     */
    public boolean exists() throws Exception {
       log.warn("Not implemented yet!");
       return true; 
    }

    /**
     * 
     */
    public long getSize() throws Exception {
        return getRealm().getRepository().getSize(new Path(getPath()));
    }

    /**
     *
     */
    public String getMimeType(String viewId) {
        return "application/xhtml+xml";
    }
}
