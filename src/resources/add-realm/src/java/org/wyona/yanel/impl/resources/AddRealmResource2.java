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

import java.util.Calendar;
import java.util.HashMap;
import java.io.File;
import java.io.StringBufferInputStream;
import java.io.ByteArrayOutputStream;

import javax.servlet.http.HttpServletRequest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.log4j.Category;

import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

/**
 * 
 */
public class AddRealmResource2 extends Resource implements ViewableV1 {

    private static Category log = Category.getInstance(AddRealmResource2.class);

    String NAMESPACE = "http://www.wyona.org/yanel/1.0";

    /**
     *
     */
    class Parameter {
        public String name;
        public String sampleValue;
        public String setValue;
        public boolean required = false;
        public boolean hidden = false;

        /**
	 *
	 */
        public Parameter(String name) {
            this.name =  name;
        }
    }

    /**
     * 
     */
    public AddRealmResource2() {
    }

    /**
     * 
     */
    public ViewDescriptor[] getViewDescriptors() {
        log.warn("Not implemented yet!");
        return null;
    }
    
    /**
     * 
     */
    public View getView(Path path, String viewId) {
        View view = new View();
        StringBuffer sb = new StringBuffer("Hello Yanel");
        view.setInputStream(new StringBufferInputStream(sb.toString()));
        view.setMimeType("text/plain");
        return view;
    }

    /**
     * Get view
     */
    public View getView(HttpServletRequest request, String viewId) throws Exception {
        String addType = getConfiguration().getProperty("add-type");

        if (addType != null && addType.equals("from-scratch")) {
            return getFromScratchView(request, viewId);
        } else if (addType != null && addType.equals("from-existing-website")) {
            return getFromExistingWebsiteView(request, viewId);
        } else {
            return getExceptionView("No such type: " + addType);
        }
    }

    /**
     * Get DOM document
     */
    public Document getDocument() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document document = null;
        try {
            DocumentBuilder parser = dbf.newDocumentBuilder();
            document = parser.parse(new java.io.StringBufferInputStream("<yanel:add-realm xmlns:yanel=\""+NAMESPACE+"\" xmlns=\""+NAMESPACE+"\"/>"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return document;
    }

    /**
     * Get exception view
     */
    private View getExceptionView(String message) {
        View view = new View();
        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        sb.append("<head>");
        sb.append("<title>Add Realm Resource</title>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<div id=\"contenBody\">");
        sb.append("<h1>" + message + "</h1>");
        sb.append("</div>");
        sb.append("</body>");
        sb.append("</html>");

        view.setMimeType("application/xhtml+xml");
        view.setInputStream(new StringBufferInputStream(sb.toString()));
        return view;
    }

    /**
     * Get from scratch view
     */
    public View getFromScratchView(HttpServletRequest request, String viewId) throws Exception {
        Document document = getFromScratchInputDocument();

        View view = new View();
        Transformer transformer = null;

        if (viewId != null && viewId.equals("xml")) {
            view.setMimeType("application/xml");
            transformer = TransformerFactory.newInstance().newTransformer();
        } else {
            view.setMimeType("application/xhtml+xml");
            File xsltFile = org.wyona.commons.io.FileUtil.file(getRTD().getConfigFile().getParentFile().getAbsolutePath(), getConfiguration().getProperty("xslt"));
            transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xsltFile));
            //transformer.setParameter();
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(document), new StreamResult(byteArrayOutputStream));
        view.setInputStream(new java.io.ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        return view;
    }

    /**
     * Get from existing website view
     */
    public View getFromExistingWebsiteView(HttpServletRequest request, String viewId) throws Exception {
        return null;
    }

    /**
     *
     */
    private Document getFromScratchInputDocument() {
        Document doc = getDocument();
        Element rootElement = doc.getDocumentElement();
        Element fromScratchElement = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "from-scratch"));

        Element parameterElement = null;
        Parameter para = null;

        // Parameter "realmid"
        para = getParameterFromResourceConfig("realmid");
        parameterElement = (Element) fromScratchElement.appendChild(doc.createElementNS(NAMESPACE, "parameter"));
        parameterElement.setAttributeNS(NAMESPACE, "name", para.name);
        parameterElement.setAttributeNS(NAMESPACE, "sample-value", para.sampleValue);

        // Parameter "realmname"
        para = getParameterFromResourceConfig("realmname");
        parameterElement = (Element) fromScratchElement.appendChild(doc.createElementNS(NAMESPACE, "parameter"));
        parameterElement.setAttributeNS(NAMESPACE, "name", para.name);
        parameterElement.setAttributeNS(NAMESPACE, "sample-value", para.sampleValue);

        // Parameter "fslocation"
        para = getParameterFromResourceConfig("fslocation");
        parameterElement = (Element) fromScratchElement.appendChild(doc.createElementNS(NAMESPACE, "parameter"));
        parameterElement.setAttributeNS(NAMESPACE, "name", para.name);
        parameterElement.setAttributeNS(NAMESPACE, "sample-value", para.sampleValue);
        parameterElement.setAttributeNS(NAMESPACE, "required", "" + para.required);
        parameterElement.setAttributeNS(NAMESPACE, "hidden", "" + para.hidden);
        if (para.setValue != null) {
            if (para.setValue.length() == 0) {
                parameterElement.setAttributeNS(NAMESPACE, "configuration-value", getYanel().getRealmConfiguration().getRealm("from-scratch-realm-template").getRootDir().getParent());
            } else {
                parameterElement.setAttributeNS(NAMESPACE, "configuration-value", para.setValue);
            }
        }
        return doc;
    }

    /**
     * Get parameter from custom configuration
     */
    private Parameter getParameterFromResourceConfig(String name) {
        try {
            org.jdom.Document jdomDocument = new org.jdom.input.DOMBuilder().build(getConfiguration().getCustomConfiguration());

            org.jdom.xpath.XPath xpath = org.jdom.xpath.XPath.newInstance("/yanel:custom-config/arr:parameter[@name='" + name + "']/@samplevalue");
            xpath.addNamespace("yanel", "http://www.wyona.org/yanel/rti/1.0");
	    xpath.addNamespace("arr", "http://www.wyona.org/yanel/resource/add-realm-resource/1.0");

            Parameter para = new Parameter(name);

            org.jdom.Attribute sampleValue = (org.jdom.Attribute) xpath.selectSingleNode(jdomDocument);
            para.sampleValue = sampleValue.getValue();

            org.jdom.xpath.XPath xxpath = org.jdom.xpath.XPath.newInstance("/yanel:custom-config/arr:parameter[@name='" + name + "']/@value");
            xxpath.addNamespace("yanel", "http://www.wyona.org/yanel/rti/1.0");
	    xxpath.addNamespace("arr", "http://www.wyona.org/yanel/resource/add-realm-resource/1.0");
            org.jdom.Attribute value = (org.jdom.Attribute) xxpath.selectSingleNode(jdomDocument);
            if (value != null) {
                para.setValue = value.getValue();
            }

            org.jdom.xpath.XPath xxxpath = org.jdom.xpath.XPath.newInstance("/yanel:custom-config/arr:parameter[@name='" + name + "']/@required");
            xxxpath.addNamespace("yanel", "http://www.wyona.org/yanel/rti/1.0");
	    xxxpath.addNamespace("arr", "http://www.wyona.org/yanel/resource/add-realm-resource/1.0");
            org.jdom.Attribute required = (org.jdom.Attribute) xxxpath.selectSingleNode(jdomDocument);
            if (required != null) {
                para.required = required.getBooleanValue();
            }
            return para;
        } catch (Exception e) {
            return null;
        }
    }
}
