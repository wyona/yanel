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
import org.wyona.yarep.core.Repository;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 */
public class UsecaseResource extends Resource implements ViewableV2 {

    private static Category log = Category.getInstance(UsecaseResource.class);

    protected static final String VIEW_DEFAULT = "default";

    protected HashMap views;

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
        // reads views from configuration:
        try {
            this.views = new HashMap();
            Document customConfigDoc = getConfiguration().getCustomConfiguration();
            if (customConfigDoc != null) {
                Configuration config = ConfigurationUtil.toConfiguration(customConfigDoc.getDocumentElement());
                Configuration viewsConfig = config.getChild("views");
                Configuration[] viewConfigs = viewsConfig.getChildren("view");
                for (int i = 0; i < viewConfigs.length; i++) {
                    String id = viewConfigs[i].getAttribute("id");
                    String type = viewConfigs[i].getAttribute("type");
                    UsecaseView view = new UsecaseView(id, type);
                    view.configure(viewConfigs[i]);
                    this.views.put(id, view);
                }
            }
        } catch (ConfigurationException e) {
            String errorMsg = "Error configuring usecase: " + getName() + ": " + e.toString();
            log.error(errorMsg, e);
            throw new UsecaseException(errorMsg, e);
        }
    }

    protected UsecaseView processUsecase(String viewID) throws UsecaseException {
        return generateView(viewID);
    }

    protected UsecaseView generateView(String viewID) throws UsecaseException {
        if (viewID == null || viewID.length() == 0) {
            viewID = VIEW_DEFAULT;
        }
        UsecaseView view = (UsecaseView)this.views.get(viewID);

        if (view == null) {
            throw new UsecaseException("Usecase " + getName() + " has no view with id: " + viewID);
        }

        if (view.getType().equals(UsecaseView.TYPE_JELLY)) {
            String viewTemplate = view.getTemplate();
            renderJellyView(view, viewTemplate);
            return view;
        } else if (view.getType().equals(UsecaseView.TYPE_REDIRECT)) {
            String redirectURL = getRedirectURL(view);
            view.setRedirectURL(redirectURL);
            return view;
        } else if (view.getType().equals(UsecaseView.TYPE_CUSTOM)) {
            renderCustomView(view);
            return view;
        } else {
            throw new UsecaseException("Usecase " + getName() + " has invalid view type: " + view.getType());
        }
    }

    protected String getName() {
        return "name";
    }

    protected void renderJellyView(UsecaseView view, String viewTemplate) throws UsecaseException {
        try {
            String viewId = view.getID();
            Repository repo = this.getRealm().getRepository();

            JellyContext jellyContext = new JellyContext();
            jellyContext.setVariable("resource", this);
            jellyContext.setVariable("yanel.back2context", PathUtil.backToContext(realm, getPath()));
            jellyContext.setVariable("yanel.back2realm", PathUtil.backToRealm(getPath()));
            jellyContext.setVariable("yanel.globalHtdocsPath", PathUtil.getGlobalHtdocsPath(this));
            jellyContext.setVariable("yanel.resourcesHtdocsPath", PathUtil.getResourcesHtdocsPath(this));
            jellyContext.setVariable("yanel.reservedPrefix", this.getYanel().getReservedPrefix());
            //jellyContext.setVariable("request", request);

            // at first we write the jelly output to a stream,
            // instead of feeding it directly to the sax pipeline,
            // because otherwise there is an error: EmptyStackException
            ByteArrayOutputStream jellyResultStream = new ByteArrayOutputStream();
            XMLOutput jellyOutput = XMLOutput.createXMLOutput(jellyResultStream);

            //String viewTemplate = view.getTemplate();
            jellyContext.runScript(new InputSource(repo.getNode(viewTemplate).getInputStream()), jellyOutput);
            jellyOutput.flush();

            // create reader:
            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            CatalogResolver catalogResolver = new CatalogResolver();
            xmlReader.setEntityResolver(catalogResolver);

            // create xslt transformer:
            SAXTransformerFactory tf = (SAXTransformerFactory)TransformerFactory.newInstance();

            String[] xsltPath = getResourceConfigProperties("xslt");

            TransformerHandler[] xsltHandlers = new TransformerHandler[xsltPath.length];
            for (int i = 0; i < xsltPath.length; i++) {
                xsltHandlers[i] = tf.newTransformerHandler(new StreamSource(repo.getNode(xsltPath[i]).getInputStream()));
                xsltHandlers[i].getTransformer().setParameter("yanel.path.name", PathUtil.getName(getPath()));
                xsltHandlers[i].getTransformer().setParameter("yanel.path", getPath());
                xsltHandlers[i].getTransformer().setParameter("yanel.back2context", PathUtil.backToContext(realm, getPath()));
                xsltHandlers[i].getTransformer().setParameter("yanel.globalHtdocsPath", PathUtil.getGlobalHtdocsPath(this));
                xsltHandlers[i].getTransformer().setParameter("yanel.resourcesHtdocsPath", PathUtil.getResourcesHtdocsPath(this));
                xsltHandlers[i].getTransformer().setParameter("yanel.back2realm", PathUtil.backToRealm(getPath()));
                xsltHandlers[i].getTransformer().setParameter("yarep.back2realm", PathUtil.backToRealm(getPath())); // for backwards compatibility
                xsltHandlers[i].getTransformer().setParameter("language", getRequestedLanguage());
                xsltHandlers[i].getTransformer().setParameter("yanel.reservedPrefix", this.getYanel().getReservedPrefix());
            }

            // create i18n transformer:
            I18nTransformer2 i18nTransformer = new I18nTransformer2("global", getRequestedLanguage(), getRealm().getDefaultLanguage());
            i18nTransformer.setEntityResolver(catalogResolver);

            // create xinclude transformer:
            XIncludeTransformer xIncludeTransformer = new XIncludeTransformer();
            SourceResolver resolver = new SourceResolver(this);
            xIncludeTransformer.setResolver(resolver);

            // create serializer
            Serializer serializer = null;
            if (getMimeType(viewId).equals("text/html")) {
                serializer = SerializerFactory.getSerializer(SerializerFactory.HTML_TRANSITIONAL);
            } else if (getMimeType(viewId).equals("application/xhtml+xml")) {
                    serializer = SerializerFactory.getSerializer(SerializerFactory.XHTML_STRICT);
            } else {
                serializer = SerializerFactory.getSerializer(SerializerFactory.XML);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // chain everything together (create a pipeline):
            xmlReader.setContentHandler(xsltHandlers[0]);
            for (int i = 0; i < xsltHandlers.length - 1; i++) {
                xsltHandlers[i].setResult(new SAXResult(xsltHandlers[i+1]));
            }
            xsltHandlers[xsltHandlers.length - 1].setResult(new SAXResult(xIncludeTransformer));
            xIncludeTransformer.setResult(new SAXResult(i18nTransformer));
            i18nTransformer.setResult(new SAXResult(serializer.asContentHandler()));
            serializer.setOutputStream(baos);

            // execute pipeline:
            xmlReader.parse(new InputSource(new ByteArrayInputStream(jellyResultStream.toByteArray())));

            view.setInputStream(new ByteArrayInputStream(baos.toByteArray()));
            view.setMimeType(getMimeType(viewId));
        } catch (Exception e) {
            String errorMsg = "Error creating jelly view of usecase: " + getName() + ": " + e;
            log.error(errorMsg, e);
            throw new UsecaseException(errorMsg, e);
        }
    }

    protected String getRedirectURL(UsecaseView view) {
        return view.getRedirectURL();
    }

    protected void renderCustomView(UsecaseView view) throws UsecaseException {
        // implement in subclass
    }


    public boolean exists() throws Exception {
        return true;
    }

    public String getMimeType(String viewId) throws Exception {
        String mimeType = getResourceConfigProperty("mime-type");
        if (mimeType != null) return mimeType;
        return "application/xhtml+xml";
    }

    public long getSize() throws Exception {
        // TODO Auto-generated method stub
        return -1;
    }

    public ViewDescriptor[] getViewDescriptors() {
        // TODO: call init() instead of return null
        if (this.views != null) {
            ViewDescriptor[] descriptors = new ViewDescriptor[this.views.size()];

            Iterator iter = this.views.keySet().iterator();
            int i = 0;
            while (iter.hasNext()) {
                UsecaseView view = (UsecaseView)iter.next();
                descriptors[i] = new ViewDescriptor(view.getID());
                String mimeType = view.getMimeType();
                if (mimeType == null || mimeType.length() == 0) {
                    mimeType = "application/xhtml+xml";
                }
                descriptors[i].setMimeType(mimeType);
                i++;
            }
            return descriptors;
        }
        return null;
    }

}
