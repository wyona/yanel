/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources;

import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.transformation.I18nTransformer;
import org.wyona.yanel.core.util.HttpServletRequestHelper;
import org.wyona.yanel.core.util.PathUtil;

import org.apache.log4j.Category;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
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
