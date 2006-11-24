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

import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Category;

import org.wyona.wikiparser.IWikiParser;
import org.wyona.wikiparser.IWikiParserFactory;
import org.wyona.wikiparser.WikiParserFactory;

import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.api.attributes.CreatableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import org.wyona.yarep.core.RepositoryException;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryFactory;
import org.wyona.yarep.util.RepoPath;

/**
 * 
 */
public class WikiResource extends Resource implements ViewableV1, CreatableV2 {

    private static Category log = Category.getInstance(WikiResource.class);
    private final String XML_MIME_TYPE = "application/xml";
    private DocumentBuilderFactory dbf = null;
    private Repository repository  = null;
    private String context = null;
    private HashMap properties = new HashMap();
    
    /**
     * 
     */
    public WikiResource() {
        dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        properties.put("title", "");
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
        try {
            RepoPath rp = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()), getRepositoryFactory());
            repository = rp.getRepo();
            
            
            String path2Resource = path.toString();
            path2Resource = path2Resource.substring(0, path2Resource.lastIndexOf("/") + 1);
            
            //these fields are specified via interface
            int type = 1;//is the jspWikiParser
            InputStream inputStream = rp.getRepo().getInputStream(new org.wyona.yarep.core.Path(rp.getPath().toString()));
            //instantiate wiki parser via factory
            
            IWikiParserFactory wikiparserFactory = new WikiParserFactory();
            IWikiParser wikiParser = wikiparserFactory.create(type, inputStream);
            //wikiParser does the transformation
            wikiParser.parse(inputStream);
            
            Transformer transformer = null;
            if(viewId != null && viewId.equals("source")) {
                transformer = TransformerFactory.newInstance().newTransformer();
                defaultView.setMimeType(XML_MIME_TYPE);
            } else {
                transformer = TransformerFactory.newInstance().newTransformer(getXSLTStreamSource(path, repository));
                transformer.setParameter("yanel.path.name", path.getName());
                transformer.setParameter("yanel.path", path.toString());
                defaultView.setMimeType("application/xhtml+xml");
            }
            
            LinkChecker linkChecker = new LinkChecker(path2Resource);
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            saxParser.parse(wikiParser.getInputStream(), linkChecker);
            //transformer.setParameter("yanel.contextPath", context);
            //transformer.setParameter("yanel.path2Resource", path2Resource);
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            transformer.transform(new StreamSource(linkChecker.getInputStream()), new StreamResult(baos));
            
            defaultView.setInputStream(new java.io.ByteArrayInputStream(baos.toByteArray()));
            
            return defaultView;
        } catch (Exception e) {
            log.error(e, e);
        }
        return null;
    }
    
    /**
     * 
     */
    public View getView(HttpServletRequest request, String viewId) {
        context = request.getContextPath();
        return getView(new Path(request.getServletPath()), viewId);
    }

    /**
     * 
     */
    public View getView(HttpServletRequest request, OutputStream outputStream, String viewId) {
        return null;
    }
    /**
     * 
     */
    private StreamSource getXSLTStreamSource(Path path, Repository repo) throws RepositoryException {
        Path xsltPath = getXSLTPath(path);
        if (xsltPath != null) {
            return new StreamSource(repo.getInputStream(new org.wyona.yarep.core.Path(getXSLTPath(path).toString())));
        } else {
            File xsltFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile()
                    .getParentFile()
                    .getAbsolutePath(), "xslt" + File.separator + "wiki2xhtml.xsl");
            log.debug("XSLT file: " + xsltFile);
            return new StreamSource(xsltFile);
        }
    }

    /**
     * 
     */
    private Path getXSLTPath(Path path) {
        String xsltPath = null;
        try {
            // TODO: Get yanel RTI yarep properties file name from framework resp. use MapFactory
            // ...!
            RepoPath rpRTI = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()),
                    yanel.getRepositoryFactory("RTIRepositoryFactory"));
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
            log.info("No XSLT Path within: " + rpRTI.getPath());
        } catch (Exception e) {
            log.warn(e);
        }
        return null;
    }
    
    /**
     * @return the empty wiki resource as String 
     */
    private String getEmptyWikiXml() {
        StringBuffer emptyWikiXml = new StringBuffer();
        emptyWikiXml.append("<?xml version=\"1.0\"?>");
        emptyWikiXml.append("\n");
        emptyWikiXml.append("<wiki xmlns=\"http://www.wyona.org/neutron/1.0\">");
        emptyWikiXml.append("\n");
        emptyWikiXml.append("</wiki>");
        
        return emptyWikiXml.toString();
    }
    
    /**
     * @param newWikiPage
     * @return the empty wiki introspection as String
     */
    private String getEmptyWikiIntrospection(String newWikiPage) {
        StringBuffer emptyWikiPageContent = new StringBuffer();
        emptyWikiPageContent.append("<?xml version=\"1.0\"?>");
        emptyWikiPageContent.append("\n");
        emptyWikiPageContent.append("<introspection xmlns=\"http://www.wyona.org/neutron/1.0\">");
        emptyWikiPageContent.append("\n\t");
        emptyWikiPageContent.append("<edit mime-type=\"application/xml\" name=\"" + newWikiPage + "\">");
        emptyWikiPageContent.append("\n\t\t");
        emptyWikiPageContent.append("<checkout url=\"/wiki/" + newWikiPage + ".html\" method=\"GET\"/>");
        emptyWikiPageContent.append("\n\t\t");
        emptyWikiPageContent.append("<checkin url=\"/wiki/" + newWikiPage + ".html?yanel.resource.usecase=checkin\" method=\"PUT\"/>");
        emptyWikiPageContent.append("\n\t");
        emptyWikiPageContent.append("</edit>");
        emptyWikiPageContent.append("\n");
        emptyWikiPageContent.append("</introspection>");
        
        return emptyWikiPageContent.toString();
    }    
    
    protected RepositoryFactory getRepositoryFactory() {
        return yanel.getRepositoryFactory("DefaultRepositoryFactory");
    }
    
    
    public String[] getPropertyNames() {
        String[] propertyNames = (String[])properties.keySet().toArray(new String[properties.keySet().size()]);
        return propertyNames;
    }

    public void setProperty(String name, Object value){
        properties.put(name, value);
    }

    public Object getProperty(String name){
        Object property = properties.get(name);
        return property;
    }
    
    public String getPropertyType(String propertyName){
        //TODO not implemented yet
        return null;
    }
  
    public void create(HttpServletRequest request ,String createName){
        log.warn("TODO: Not implemented yet!");
        //yanel rti TODO does not work yet
        //writeRti(new Path(request.getServletPath()), createName, "rtd/wiki.rtd");
        
        
        //content
        writeContent(new Path(request.getServletPath()), createName, this.getEmptyWikiXml());

        //introspection
        //writeToRepo(newpath,content);
    }
 
    public void writeContent(Path path,String createName,  String content){
        
        try{
            
        RepoPath rp = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()+"/"+createName), new RepositoryFactory());
        repository = rp.getRepo();

        // Write content to repository
        System.out.println("\nWriting content to repository " + repository.getName() +"with content: "+content+"to path: "+path.toString()+"/"+createName);
        Writer writer = repository.getWriter(rp.getPath());
        writer.write(content);
        writer.close();
        }
        catch (Exception e) {
            log.warn(e);
        }
    }

    public void writeRti(Path path,String createName,  String content){
        
        try{
            RepoPath rpRTI = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()+"/"+createName), yanel.getRepositoryFactory("RTIRepositoryFactory"));
            
            repository = rpRTI.getRepo();

            // Write content to repository
            System.out.println("\nWriting content to repository " + repository.getName() +"with content: "+content+"to path: "+path.toString()+"/"+createName);
            Writer writer = repository.getWriter(rpRTI.getPath());
            writer.write(content);
            writer.close();
            
            //Writer br = rpRTI.getRepo().getWriter(new org.wyona.yarep.core.Path(new Path(rpRTI.getPath().toString()).getRTIPath().toString()));
            
            //PrintWriter br = new PrintWriter(rpRTI.getRepo().getWriter(new org.wyona.yarep.core.Path(new Path(rpRTI.getPath().toString()).getRTIPath().toString())));

            
            // Write the rti file
            //System.out.println("\nWriting RTI to repository " + repository.getName() +"with content: "+content+"to path: "+path.toString()+"/"+createName);
            //br.write(content);
            //br.close();
        }
        catch (Exception e) {
            log.error(e);
        }
    }
            
}
