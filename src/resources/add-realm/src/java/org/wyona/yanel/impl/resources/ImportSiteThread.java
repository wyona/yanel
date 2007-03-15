/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.lenya.search.crawler.DumpingCrawler;
import org.apache.log4j.Category;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yarep.core.Node;
import org.wyona.yarep.core.NodeType;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryException;

/**
 * 
 */
public class ImportSiteThread extends Thread {

    private static Category log = Category.getInstance(ImportSiteThread.class);
    private final static int INSIDE_TAG = 0;
    private final static int OUTSIDE_TAG = 1;
    
    private DumpingCrawler crawler;
    private String dumpDir;
    private String crawlStartURL;
    private Realm realm;
    
    public ImportSiteThread(DumpingCrawler crawler, Realm realm, String dumpDir, String crawlStartURL) {
        this.crawler = crawler;
        this.realm = realm;
        this.dumpDir = dumpDir;
        this.crawlStartURL = crawlStartURL;
    }
    
    public void run() {
        try {
            // create dump:
            crawler.run();
            crawler.close();
            
            // import dump into realm:
            deleteRepositoryContent(realm.getRepository());
            deleteRepositoryContent(realm.getRTIRepository());
            Node root = realm.getRepository().getRootNode();
            importContent(new File(dumpDir), root);
            
            // remove temp dump dir
            FileUtils.deleteDirectory(new File(dumpDir));
            
            fixRootNode(crawlStartURL, root);
            addResourceConfiguration(realm.getRTIRepository());
        } catch (Exception e) {
            log.error(e, e);
            throw new RuntimeException(e.toString(), e);
        }
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
     * TODO: remove existing introspection link if imported page already has one 
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
    

}
