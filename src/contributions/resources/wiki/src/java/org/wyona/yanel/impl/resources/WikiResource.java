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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

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

import org.wyona.yarep.core.NoSuchNodeException;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryFactory;
import org.wyona.yarep.util.RepoPath;

/**
 * 
 */
public class WikiResource extends Resource implements ContinuableV1, ViewableV1 {

    private static Category log = Category.getInstance(WikiResource.class);

    private final String XML_MIME_TYPE = "application/xml";
    private final String NAME_SPACE = "http://apache.org/cocoon/wiki/1.0";

    private DocumentBuilderFactory dbf = null;
    //private Document document = null;

    private File input = null;

    private Repository repository  = null;
    
    private Path path = null;

    /**
     * 
     */
    public WikiResource() {
        dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
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
        this.path = path;
        try {
            RepoPath rp = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()),
                    new RepositoryFactory());
            repository = rp.getRepo();
            WikiParser wikiin = new WikiParser(rp.getRepo()
                    .getInputStream(new org.wyona.yarep.core.Path(rp.getPath().toString())));
            SimpleNode node = wikiin.WikiBody();

            DocumentBuilder parser = dbf.newDocumentBuilder();
            DOMImplementation impl = parser.getDOMImplementation();
            DocumentType doctype = null;
            Document document = impl.createDocument(NAME_SPACE, "wiki", doctype);
            Element rootElement = document.getDocumentElement();
            traverse(document, rootElement, node);

            StringBuffer sb = new StringBuffer("<?xml  version=\"1.0\"?>");
            int indent = 0;
            sb.append("<wiki xmlns=\"" + NAME_SPACE + "\">");
            traverse(sb, node, indent);
            sb.append("</wiki>");
            log.debug(sb);

            if(viewId != null && viewId.equals("source")) {
                defaultView.setInputStream(new java.io.StringBufferInputStream(sb.toString()));
                defaultView.setMimeType(XML_MIME_TYPE);
            } else {
                try {
                    Transformer transformer = TransformerFactory.newInstance().newTransformer(getXSLTStreamSource(path, repository));
                    transformer.setParameter("yanel.path.name", path.getName());
                    transformer.setParameter("yanel.path", path.toString());

                    // TODO: Is this the best way to generate an InputStream from an OutputStream?
                    java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                    transformer.transform(new StreamSource(new java.io.StringBufferInputStream(sb.toString())), new StreamResult(baos));
                    defaultView.setInputStream(new java.io.ByteArrayInputStream(baos.toByteArray()));
                    defaultView.setMimeType("application/xhtml+xml");
                    //defaultView.setMimeType(getMimeType(path));
                    defaultView.setInputStream(new java.io.ByteArrayInputStream(baos.toByteArray()));
                } catch (Exception e) {
                    log.error(e);
                }
            }

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

        for (int i = 0; i < indent; i++)
            sb.append(" ");

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
                    traverse(sb, (SimpleNode) node.jjtGetChild(i), indent + 1);
                for (int i = 0; i < indent; i++)
                    sb.append(" ");
                sb.append("</" + node.toString() + ">");
            } else {
                sb.append("/>");
            }
        }
    }

    /**
     * Traverse tree and output an "XML Document"
     */
    public void traverse(Document doc, Element element, SimpleNode node) {

        if (node.toString().equals("Text")) {
            if (!node.optionMap.isEmpty()) {
                Set keySet = node.optionMap.keySet();
                Iterator kit = keySet.iterator();
                while (kit.hasNext()) {
                    Object option = kit.next();
                    Object value = node.optionMap.get(option);
                    element.appendChild(doc.createTextNode(value.toString()));
                }
            }
        } else {
            Element child = (Element) element.appendChild(doc.createElement(node.toString()));

/*
            if (!node.optionMap.isEmpty()) {
                Set keySet = node.optionMap.keySet();
                Iterator kit = keySet.iterator();
                while (kit.hasNext()) {
                    Object option = kit.next();
                    Object value = node.optionMap.get(option);
                    sb.append(" " + option.toString() + "=" + "\"" + value.toString() + "\"");
                }
            }
*/

            if (node.jjtGetNumChildren() > 0) {
                for (int i = 0; i < node.jjtGetNumChildren(); i++)
                    traverse(doc, child, (SimpleNode) node.jjtGetChild(i));
            }
        }
    }

    /**
     * 
     */
    public View getView(HttpServletRequest request, String viewId) {
        return getView(new Path(request.getServletPath()), viewId);
    }

    /**
     * 
     */
    private StreamSource getXSLTStreamSource(Path path, Repository repo) throws NoSuchNodeException {
        Path xsltPath = getXSLTPath(path);
        if (xsltPath != null) {
            return new StreamSource(repo.getInputStream(new org.wyona.yarep.core.Path(getXSLTPath(path).toString())));
        } else {
            File xsltFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile()
                    .getParentFile()
                    .getAbsolutePath(), "xslt" + File.separator + "wiki2xhtml.xsl");
            log.debug("XSLT file: " + xsltFile);
            return new StreamSource(xsltFile);
        }
    }

    /**
     * 
     */
    private Path getXSLTPath(Path path) {
        String xsltPath = null;
        try {
            // TODO: Get yanel RTI yarep properties file name from framework resp. use MapFactory
            // ...!
            RepoPath rpRTI = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()),
                    new RepositoryFactory("yanel-rti-yarep.properties"));
            java.io.BufferedReader br = new java.io.BufferedReader(rpRTI.getRepo()
                    .getReader(new org.wyona.yarep.core.Path(new Path(rpRTI.getPath().toString()).getRTIPath()
                            .toString())));

            while ((xsltPath = br.readLine()) != null) {
                if (xsltPath.indexOf("xslt:") == 0) {
                    xsltPath = xsltPath.substring(6);
                    log.debug("XSLT Path: " + xsltPath);
                    return new Path(xsltPath);
                }
            }
            log.info("No XSLT Path within: " + rpRTI.getPath());
        } catch (Exception e) {
            log.warn(e);
        }

        return null;
    }




/*
    private String getMimeType(Path path) {
        String mimeType = null;
        try {
            // TODO: Get yanel RTI yarep properties file name from framework resp. use MapFactory
            // ...!
            RepoPath rpRTI = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()),
                    new RepositoryFactory("yanel-rti-yarep.properties"));
            java.io.BufferedReader br = new java.io.BufferedReader(rpRTI.getRepo()
                    .getReader(new org.wyona.yarep.core.Path(new Path(rpRTI.getPath().toString()).getRTIPath()
                            .toString())));

            while ((mimeType = br.readLine()) != null) {
                if (mimeType.indexOf("mime-type:") == 0) {
                    mimeType = mimeType.substring(11);
                    log.info("*" + mimeType + "*");
                    // TODO: Maybe validate mime-type ...
                    return mimeType;
                }
            }
        } catch (Exception e) {
            log.warn(e);
        }

        // NOTE: Assuming fallback re dir2xhtml.xsl ...
        return "application/xhtml+xml";
    }
*/
}
