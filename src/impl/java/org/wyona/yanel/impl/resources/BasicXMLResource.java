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
import java.io.InputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Category;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.apache.xml.serializer.Serializer;
import org.wyona.security.core.api.Identity;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.serialization.SerializerFactory;
import org.wyona.yanel.core.source.ResourceResolver;
import org.wyona.yanel.core.transformation.I18nTransformer2;
import org.wyona.yanel.core.transformation.XIncludeTransformer;
import org.wyona.yanel.core.util.PathUtil;
import org.wyona.yarep.core.Repository;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 */
public class BasicXMLResource extends Resource implements ViewableV2 {

    private static Category log = Category.getInstance(BasicXMLResource.class);

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#getViewDescriptors()
     */
    public ViewDescriptor[] getViewDescriptors() {
        ViewDescriptor[] vd = new ViewDescriptor[2];
        vd[0] = new ViewDescriptor("default");
        vd[0].setMimeType("application/xhtml+xml");
        vd[1] = new ViewDescriptor("source");
        vd[1].setMimeType("application/xml");
        return vd;
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#getView(java.lang.String)
     */
    public View getView(String viewId) throws Exception {
        InputStream xmlInputStream = getContentXML(viewId);
        return getXMLView(viewId, xmlInputStream);
    }
    
    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#getMimeType(java.lang.String)
     */
    public String getMimeType(String viewId) throws Exception {
        String mimeType = getResourceConfigProperty("mime-type");
        if (mimeType != null) return mimeType;
        mimeType = "application/xhtml+xml";
        return mimeType;
    }
    
    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#exists()
     */
    public boolean exists() throws Exception {
        return getRealm().getRepository().existsNode(getPath());
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#getSize()
     */
    public long getSize() throws Exception {
        return -1;
    }
    
    
    public View getXMLView(String viewId, InputStream xmlInputStream) throws Exception {
        View view = new View();
        String mimeType = getMimeType(viewId);
        view.setMimeType(mimeType);  

        try {
            Repository repo = getRealm().getRepository();

            if (viewId != null && viewId.equals("source")) {
                view.setInputStream(xmlInputStream);
                view.setMimeType("application/xml");
                return view;
            }

           
            // create reader:
            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            CatalogResolver catalogResolver = new CatalogResolver();
            xmlReader.setEntityResolver(catalogResolver);
            
            // create xslt transformer:
            SAXTransformerFactory tf = (SAXTransformerFactory)TransformerFactory.newInstance();
             
            String[] xsltPath = getXSLTPath(getPath());
            TransformerHandler[] xsltHandlers = new TransformerHandler[xsltPath.length];
            for (int i = 0; i < xsltPath.length; i++) {
                xsltHandlers[i] = tf.newTransformerHandler(new StreamSource(repo.getNode(xsltPath[i]).getInputStream()));
                passTransformerParameters(xsltHandlers[i].getTransformer());
            }
           
            // create i18n transformer:
            I18nTransformer2 i18nTransformer = new I18nTransformer2("global", getRequestedLanguage(), getRealm().getDefaultLanguage());
            i18nTransformer.setEntityResolver(catalogResolver);
           
            // create xinclude transformer:
            XIncludeTransformer xIncludeTransformer = new XIncludeTransformer();
            ResourceResolver resolver = new ResourceResolver(this);
            xIncludeTransformer.setResolver(resolver);
           
            // create serializer:
            Serializer serializer = null;
            // TODO: Should it also check for text/html?!
            if (getMimeType(viewId).equals("application/xhtml+xml")) {
                serializer = SerializerFactory.getSerializer(SerializerFactory.XHTML_STRICT);
            } else {
                serializer = SerializerFactory.getSerializer(SerializerFactory.XML);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
           
            // chain everything together (create a pipeline):
            if (xsltHandlers.length > 0) {
                xmlReader.setContentHandler(xsltHandlers[0]);
                for (int i=0; i<xsltHandlers.length-1; i++) {
                    xsltHandlers[i].setResult(new SAXResult(xsltHandlers[i+1]));
                }
                xsltHandlers[xsltHandlers.length-1].setResult(new SAXResult(xIncludeTransformer));
            } else {
                xmlReader.setContentHandler(new SAXResult(xIncludeTransformer).getHandler());
            }
            xIncludeTransformer.setResult(new SAXResult(i18nTransformer));
            i18nTransformer.setResult(new SAXResult(serializer.asContentHandler()));
            serializer.setOutputStream(baos);
           
            // execute pipeline:
            xmlReader.parse(new InputSource(xmlInputStream));
           
            // write result into view:
            view.setInputStream(new ByteArrayInputStream(baos.toByteArray()));
            return view;
        } catch(Exception e) {
            log.error(e + " (" + getPath() + ", " + getRealm() + ")", e);
            throw new Exception(e);
        }

    }
   
    /**
     * Pass parameters to xslt transformer.
     * @param transformer
     * @throws Exception
     */
    protected void passTransformerParameters(Transformer transformer) throws Exception {
        transformer.setParameter("yanel.path.name", PathUtil.getName(getPath()));
        transformer.setParameter("yanel.path", getPath());
        transformer.setParameter("yanel.back2context", PathUtil.backToContext(realm, getPath()));
        String backToRealm = PathUtil.backToRealm(getPath());
        transformer.setParameter("yanel.back2realm", backToRealm);
        transformer.setParameter("yarep.back2realm", backToRealm); // for backwards compatibility
        String userAgent = getEnvironment().getRequest().getHeader("User-Agent");
        transformer.setParameter("language", getRequestedLanguage());
        String os = getOS(userAgent);
        if (os != null) transformer.setParameter("os", os);
        String client = getClient(userAgent);
        if (client != null) transformer.setParameter("client", client);
        // localization
        transformer.setParameter("language", getRequestedLanguage());
        // language of this translation
        transformer.setParameter("content-language", getContentLanguage());
        // username
        String username = getUsername();
        if (username != null) transformer.setParameter("username", username);
    }
    
    /**
     * Gets the XML content which will be fed into the processing pipeline. 
     * @return xml stream
     * @throws Exception
     */
    protected InputStream getContentXML(String viewId) throws Exception {
        return getRealm().getRepository().getNode(getPath()).getInputStream();
    }

    /**
     * Get operating system
     */
    protected String getOS(String userAgent) {
        if (userAgent.indexOf("Linux") > 0) {
            return "unix";
        } else if (userAgent.indexOf("Mac OS X") > 0) {
            return "unix";
        } else if (userAgent.indexOf("Windows") > 0) {
            return "windows";
        } else {
            log.warn("Operating System could not be recognized: " + userAgent);
            return null;
        }
    }

    /**
     * Get client
     */
    protected String getClient(String userAgent) {
        if (userAgent.indexOf("Firefox") > 0) {
            return "firefox";
        } else if (userAgent.indexOf("MSIE") > 0) {
            return "msie";
        } else {
            log.warn("Client could not be recognized: " + userAgent);
            return null;
        }
    }

    /**
     * Get username from session
     */
    protected String getUsername() {
        Identity identity = getEnvironment().getIdentity();
        if (identity != null) return identity.getUsername();
        return null;
    }

    /**
     * Get XSLT path
     */
    protected String[] getXSLTPath(String path) throws Exception {
        String[] xsltPath = getResourceConfigProperties("xslt");
        if (xsltPath != null) return xsltPath;
        log.info("No XSLT Path within: " + path);
        return new String[0];
    }

}
