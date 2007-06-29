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


import org.wyona.yanel.core.attributes.viewable.View;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.log4j.Category;

/**
 *
 */
public class UsecaseView extends View {

    private static Category log = Category.getInstance(UsecaseView.class);
    
    public static final String TYPE_JELLY = "jelly";
    public static final String TYPE_REDIRECT = "redirect";
    public static final String TYPE_CUSTOM = "custom";
    
    protected String template;
    protected String type;
    protected String id;
    protected String redirectURL;

    /**
     *
     */
    public UsecaseView(String id, String type) {
        this.id = id;
        this.type = type;
    }
    
    /**
     * 
     */
    public void configure(Configuration config) throws ConfigurationException {
        if (getType().equals(TYPE_JELLY)) {
            setTemplate(config.getChild("template").getValue());
        }
        if (getType().equals(TYPE_REDIRECT)) {
            setRedirectURL(config.getChild("url").getValue());
        }
    }

    public String getRedirectURL() {
        return redirectURL;
    }

    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
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
    
    public String getID() {
        return this.id;
    }

}
