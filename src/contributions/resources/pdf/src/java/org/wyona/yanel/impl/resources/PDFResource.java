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

import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import org.wyona.yarep.core.RepositoryException;
import org.wyona.yarep.core.Repository;

//import org.apache.avalon.framework.logger.Logger; 
//import org.apache.avalon.framework.logger.ConsoleLogger;

import org.apache.log4j.Category;

import org.apache.fop.apps.Driver;
//import org.apache.fop.configuration.Configuration;
//import org.apache.fop.messaging.MessageHandler;

import java.io.File;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

/**
 *
 */
public class PDFResource extends Resource implements ViewableV2 {
    
    private static Category log = Category.getInstance(PDFResource.class);
       
    
    /**
     *
     */
    public PDFResource() {
        
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
    public View getView(String viewId) {
        View defaultView = new View();
        defaultView.setMimeType(getMimeType(viewId));
        defaultView.setResponse(false); // This resource writes directly into the response output stream
       
        try {
            Repository repo = getRealm().getRepository();            
            
/*
            Configuration.put("version", "FOP 0.20.5");
            Configuration.put("stream-filter-list", "flate");
            Configuration.put("stream-filter-list", "ascii-85");
            java.util.List streamFilterList = new java.util.ArrayList();
            streamFilterList.add("flate");
            streamFilterList.add("ascii-85");
            Configuration.put("stream-filter-list", streamFilterList, Configuration.PDF);
*/
            
            Driver driver = new Driver();
/*
            Logger logger = new ConsoleLogger(ConsoleLogger.LEVEL_INFO);
            MessageHandler.setScreenLogger(logger);
            driver.setLogger(logger);
*/
            driver.setRenderer(Driver.RENDER_PDF);
        
            /* Only for debugging ...
            java.io.FileOutputStream fout = new java.io.FileOutputStream("/home/michi/Desktop/yanel.pdf");
            driver.setOutputStream(fout);
            */

            driver.setOutputStream(getResponse().getOutputStream());

            // TODO: This doesn't seem to work properly (see below)
            //java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            //driver.setOutputStream(baos);
            
            Transformer transformer = TransformerFactory.newInstance().newTransformer(getXSLTStreamSource(getPath(),repo));           
            
            org.xml.sax.XMLReader xmlReader = org.xml.sax.helpers.XMLReaderFactory.createXMLReader();
            xmlReader.setEntityResolver(new org.apache.xml.resolver.tools.CatalogResolver());
            Source src = new SAXSource(xmlReader, new org.xml.sax.InputSource(repo.getInputStream(new Path(getPath()))));
            
            Result res = new SAXResult(driver.getContentHandler());
            
            transformer.transform(src,res);
            
            // TODO: For some strange reason the stream seems to be truncated after a certain length ...!
            //log.debug("Result Size"+ baos.size());
            //defaultView.setInputStream(new java.io.ByteArrayInputStream(baos.toByteArray()));
        } catch(Exception e) {
            log.error(e, e);
        }        

        return defaultView;     
    }
    
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
 private StreamSource getXSLTStreamSource(String path, Repository repo) throws Exception {
     String xsltPath = getXSLTPath(path);
     if(xsltPath != null) {
         return new StreamSource(repo.getInputStream(new Path(xsltPath)));
     } else {
         File xsltFile = org.wyona.commons.io.FileUtil.file(
         rtd.getConfigFile().getParentFile().getAbsolutePath(), "xslt" + File.separator + "xml2fo.xsl");
         log.debug("XSLT file: " + xsltFile);
         return new StreamSource(xsltFile);
     }
 }

    /**
     * 
     */
    private String getXSLTPath(String path) throws Exception {
        String xsltPath = getResourceConfigProperty("xslt");
        if (xsltPath != null) return xsltPath;
        log.info("No XSLT Path within: " + path);
        return null;
    }

    /**
     *
     */
    public String getMimeType(String viewId) {
        return "application/pdf";
    }
}
