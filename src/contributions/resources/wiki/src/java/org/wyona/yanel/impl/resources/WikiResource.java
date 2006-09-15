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
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Category;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.wyona.wiki.SimpleNode;
import org.wyona.wiki.Wiki2XML;
import org.wyona.wiki.WikiParser;
import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ContinuableV1;
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import org.wyona.yarep.core.RepositoryFactory;
import org.wyona.yarep.util.RepoPath;

/**
 * 
 */
public class WikiResource extends Resource implements ContinuableV1, ViewableV1 {

    private static Category log = Category.getInstance(WikiResource.class);

    private final String XML_MIME_TYPE = "application/xml";

    private Document document = null;

    private File input = null;

    /**
     * 
     */
    public WikiResource() {
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
            RepoPath rp = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()), new RepositoryFactory());
            WikiParser wikiin = new WikiParser(rp.getRepo().getInputStream(new org.wyona.yarep.core.Path(rp.getPath().toString())));
            SimpleNode node = wikiin.WikiBody();
            
            StringBuffer sb = new StringBuffer("<?xml  version=\"1.0\"?>");
            int indent = 0;
            traverse(sb, node, indent);
            log.debug(sb);

            defaultView.setInputStream(new java.io.StringBufferInputStream(sb.toString()));                    
            defaultView.setMimeType(XML_MIME_TYPE);
            
            return defaultView;
        } catch (Exception e) {
            log.error(e, e);
        }
        return null;
    }

    /**
     * Traverse tree and output an "XML String"
     */
    public void traverse(StringBuffer sb, SimpleNode node, int indent) {

        for (int i = 0; i < indent; i++) sb.append(" ");

        if (node.toString().equals("Text")) {
            if (!node.optionMap.isEmpty()) {
                Set keySet = node.optionMap.keySet();
                Iterator kit = keySet.iterator();
                while (kit.hasNext()) {
                    Object option = kit.next();
                    Object value = node.optionMap.get(option);
                    sb.append(value.toString());
                }
            }       
        } else {
            sb.append("<" + node.toString());

        if (!node.optionMap.isEmpty()) {
            Set keySet = node.optionMap.keySet();
            Iterator kit = keySet.iterator();
            while (kit.hasNext()) {
                Object option = kit.next();
                Object value = node.optionMap.get(option);
                sb.append(" " + option.toString() + "=" + "\"" + value.toString() + "\"");
            }
        }       

        if (node.jjtGetNumChildren() > 0) {
            sb.append(">");
            for (int i = 0; i < node.jjtGetNumChildren(); i++) 
                traverse(sb, (SimpleNode)node.jjtGetChild(i), indent + 1);
            for (int i = 0; i < indent; i++)
                sb.append(" ");
            sb.append("</" + node.toString() + ">");
        } else {
            sb.append("/>"); 
        }
        }
    }
    
    /**
     * 
     */
    public View getView(HttpServletRequest request, String viewId) {
        return getView(new Path(request.getServletPath()), viewId);
    }

    private InputStream getInputStream(File wikifile) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder parser = dbf.newDocumentBuilder();
            DOMImplementation impl = parser.getDOMImplementation();
            DocumentType doctype = null;
            document = impl.createDocument("http://www.wyona.org/yanel/1.0",
                    "wiki", doctype);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        // create root element
        Element rootElement = document.getDocumentElement();
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            TransformerFactory.newInstance().newTransformer().transform(
                    new javax.xml.transform.dom.DOMSource(document),
                    new StreamResult(byteArrayOutputStream));
            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

}
