/*
 * Copyright 2007 Wyona
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
package org.wyona.yanel.impl.resources.xml;

import java.util.Properties;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

public class ConfigurableViewDescriptor extends ViewDescriptor {
    
    public static final String TYPE_XML = "xml";
    public static final String TYPE_JELLY = "jelly";
    public static final String TYPE_REDIRECT = "redirect";
    public static final String TYPE_CUSTOM = "custom";
    
    protected String template;
    protected String type;
    protected String redirectURL;
    protected String[] xsltPaths;
    protected String serializerKey;
    protected Properties serializerProperties;

    public ConfigurableViewDescriptor(String id) {
        super(id);
    }
    
    /**
     * 
     */
    public void configure(Configuration config) throws ConfigurationException {
        
        type = config.getAttribute("type", TYPE_XML);
        
        Configuration[] xsltConfigs = config.getChildren("xslt");
        xsltPaths = new String[xsltConfigs.length];
        for (int i = 0; i < xsltConfigs.length; i++) {
            xsltPaths[i] = xsltConfigs[i].getValue();
        }
        
        Configuration mimeTypeConfig = config.getChild("mime-type", false);
        if (mimeTypeConfig != null) {
            setMimeType(mimeTypeConfig.getValue());
        }
        
        Configuration serializerConfig = config.getChild("serializer", false);
        if (serializerConfig != null) {
            serializerKey = serializerConfig.getAttribute("key");
            serializerProperties = new Properties();
            Configuration propertyConfig = serializerConfig.getChild("omit-xml-declaration", false);
            if (propertyConfig != null) {
                serializerProperties.setProperty("omit-xml-declaration", propertyConfig.getValue());
            }
            propertyConfig = serializerConfig.getChild("doctype-public", false);
            if (propertyConfig != null) {
                serializerProperties.setProperty("doctype-public", propertyConfig.getValue());
            }
            propertyConfig = serializerConfig.getChild("doctype-system", false);
            if (propertyConfig != null) {
                serializerProperties.setProperty("doctype-sytem", propertyConfig.getValue());
            }
        }
        
        if (type.equals(TYPE_JELLY)) {
            template = config.getChild("template").getValue();
        }
        if (type.equals(TYPE_REDIRECT)) {
            redirectURL = config.getChild("url").getValue();
        }
    }

    public String getRedirectURL() {
        return redirectURL;
    }

    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }

    public String getSerializerKey() {
        return serializerKey;
    }

    public void setSerializerKey(String serializerKey) {
        this.serializerKey = serializerKey;
    }

    public Properties getSerializerProperties() {
        return serializerProperties;
    }

    public void setSerializerProperties(Properties serializerProperties) {
        this.serializerProperties = serializerProperties;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getXSLTPaths() {
        return xsltPaths;
    }

    public void setXSLTPaths(String[] xsltPaths) {
        this.xsltPaths = xsltPaths;
    }
}
