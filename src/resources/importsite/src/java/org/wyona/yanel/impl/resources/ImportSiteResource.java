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

import org.apache.log4j.Category;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;

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
public class ImportSiteResource extends Resource implements ViewableV2 {

    private static Category log = Category.getInstance(ImportSiteResource.class);
    private String defaultLanguage = "en";
    private String language = null;

    /**
     *
     */
    public ImportSiteResource() {
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
        
        /* create tmp status file
        if (!new File(request.getSession().getServletContext().getRealPath("tmp")).exists()) {
            if (!new File(request.getSession().getServletContext().getRealPath("tmp")).mkdir()) {
                errorMessage = errorMessage + "\n Creation of tmp directory faild.";
            }
        }*/

        Transformer transformer = null;
        I18nTransformer i18nTransformer = new I18nTransformer("importsite", language);
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
            if(submit) {
                File statusXSLTFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile().getParentFile().getAbsolutePath(), "xslt" + File.separator + "importsite.xsl");
                File statusXMLFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile().getParentFile().getAbsolutePath(), "xml" + File.separator + "status-screen.xml");
                transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(statusXSLTFile));
                
                String parameterName = "";
                String parameter = "";
                String parameterErrorName = "";
                String parameterError = "";
                String[] parameters = { "realmid", "realmname", "url", "fs-location", "crawldepth", "maxpages" };
                for (int i=0; i<parameters.length; i++){
                    parameterName = parameters[i];
                    parameter = HttpServletRequestHelper.getParameter(request, parameterName);
                    transformer.setParameter(parameterName, parameter);
                    
                    if (parameter == null || ("").equals(parameter)) {
                        parameterErrorName = "error-" + parameterName;
                        parameterError = "Please enter correct value for '" + parameterName + "'";
                        transformer.setParameter(parameterErrorName, parameterError);
                    }
                }
                
                if (parameterError == null || ("").equals(parameterError)) {
                    transformer.setParameter("submitted", "true");
                }
                
                transformer.transform(new javax.xml.transform.stream.StreamSource(statusXMLFile), new StreamResult(byteArrayOutputStream));
                
            } else {
                File inputXSLTFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile().getParentFile().getAbsolutePath(), "xslt" + File.separator + "importsite.xsl");
                File inputXMLFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile().getParentFile().getAbsolutePath(), "xml" + File.separator + "input-screen.xml");
                transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(inputXSLTFile));

                transformer.transform(new javax.xml.transform.stream.StreamSource(inputXMLFile), new StreamResult(byteArrayOutputStream));
            }
            
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            saxParser.parse(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), i18nTransformer);
            
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        
        defaultView.setMimeType("application/xhtml+xml");
        defaultView.setInputStream(i18nTransformer.getInputStream());
        return defaultView;
   }
   
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

}
