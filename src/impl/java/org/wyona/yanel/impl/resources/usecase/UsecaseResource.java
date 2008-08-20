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

package org.wyona.yanel.impl.resources.usecase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.ConfigurationUtil;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.XMLOutput;
import org.apache.log4j.Category;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.apache.xml.serializer.Serializer;
import org.w3c.dom.Document;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceConfiguration;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.serialization.SerializerFactory;
import org.wyona.yanel.core.source.SourceResolver;
import org.wyona.yanel.core.transformation.I18nTransformer2;
import org.wyona.yanel.core.transformation.XIncludeTransformer;
import org.wyona.yanel.core.util.PathUtil;
import org.wyona.yanel.impl.resources.xml.ConfigurableViewDescriptor;
import org.wyona.yanel.impl.resources.BasicXMLResource;
import org.wyona.yarep.core.Repository;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 */
public class UsecaseResource extends BasicXMLResource {

    private static Category log = Category.getInstance(UsecaseResource.class);

    /**
     *
     */
    public UsecaseResource() {
    }

    public View getView(String viewID) throws Exception {
        init();
        return processUsecase(viewID);
    }
    
    protected void init() throws UsecaseException {
        // implement in subclass
    }
    
    protected View processUsecase(String viewID) throws UsecaseException {
        return generateView(viewID);
    }
    
    protected View generateView(String viewID) throws UsecaseException {
        if (viewID == null || viewID.length() == 0) {
            viewID = DEFAULT_VIEW_ID;
        }
        try {
            ConfigurableViewDescriptor viewDescriptor = (ConfigurableViewDescriptor)getViewDescriptor(viewID);
            View view = null;
           
            if (viewDescriptor == null) {
                throw new UsecaseException("Usecase " + getName() + " has no view with id: " + viewID);
            }
            
            if (viewDescriptor.getType().equals(ConfigurableViewDescriptor.TYPE_JELLY)) {
                InputStream xmlInputStream = getJellyXML(viewDescriptor);
                view = getXMLView(viewID, xmlInputStream);
            /*} else if (viewDescriptor.getType().equals(ViewDescriptor.TYPE_REDIRECT)) {
                String redirectURL = getRedirectURL(viewDescriptor);
                UsecaseView view = new UsecaseView(viewDescriptor.getId(), UsecaseView.TYPE_REDIRECT);
                view.setRedirectURL(redirectURL);
                return view;*/
            } else if (viewDescriptor.getType().equals(ConfigurableViewDescriptor.TYPE_CUSTOM)) {
                view = renderCustomView(viewDescriptor);
            } else {
                throw new UsecaseException("Usecase " + getName() + " has invalid view type: " + viewDescriptor.getType());
            }
            view.setHttpHeaders(viewDescriptor.getHttpHeaders());
            return view;
        } catch (Exception e) {
            String errorMsg = "Error generating view of usecase: " + getName() + ": " + e;
            log.error(errorMsg, e);
            throw new UsecaseException(errorMsg, e);
        }
    }
    
    protected String getName() {
        return "name";
    }

    protected InputStream getJellyXML(ConfigurableViewDescriptor viewDescriptor) throws UsecaseException {
        try {
            String viewTemplate = viewDescriptor.getTemplate();
            Repository repo = this.getRealm().getRepository();
            
            JellyContext jellyContext = new JellyContext();
            jellyContext.setVariable("resource", this);
            jellyContext.setVariable("yanel.back2context", PathUtil.backToContext(realm, getPath()));
            jellyContext.setVariable("yanel.back2realm", PathUtil.backToRealm(getPath()));
            jellyContext.setVariable("yanel.globalHtdocsPath", PathUtil.getGlobalHtdocsPath(this));
            jellyContext.setVariable("yanel.resourcesHtdocsPath", PathUtil.getResourcesHtdocsPathURLencoded(this));
            jellyContext.setVariable("yanel.reservedPrefix", this.getYanel().getReservedPrefix());
            //jellyContext.setVariable("request", request);

            ByteArrayOutputStream jellyResultStream = new ByteArrayOutputStream();
            // TODO: should enable xml escaping, see bug:
            // http://bugzilla.wyona.com/cgi-bin/bugzilla/show_bug.cgi?id=5964
            // problem: it breaks backwards compatibility
            //XMLOutput jellyOutput = XMLOutput.createXMLOutput(jellyResultStream, true);
            XMLOutput jellyOutput = XMLOutput.createXMLOutput(jellyResultStream);
            
            //String viewTemplate = view.getTemplate();
            jellyContext.runScript(new InputSource(repo.getNode(viewTemplate).getInputStream()), jellyOutput);
            jellyOutput.flush();
            byte[] result = jellyResultStream.toByteArray();
            //System.out.println(new String(result, "utf-8"));
            return new ByteArrayInputStream(result);
        } catch (Exception e) {
            String errorMsg = "Error creating jelly view of usecase: " + getName() + ": " + e;
            log.error(errorMsg, e);
            throw new UsecaseException(errorMsg, e);
        }
    }
        
    protected String getRedirectURL(ConfigurableViewDescriptor viewDescriptor) {
        return viewDescriptor.getRedirectURL();
    }
    
    /**
     * Allows to implement subclasses a custom view, which is executed if TYPE_CUSTOM
     */
    protected View renderCustomView(ConfigurableViewDescriptor viewDescriptor) throws UsecaseException {
        log.error("Not implemented! Please make sure to implement this method on your subclass when using TYPE_CUSTOM, because type '" + viewDescriptor.getType() + "' seems to be a custom type!");
        return null;
    }
    
    public boolean exists() throws Exception {
        return true;
    }
}
