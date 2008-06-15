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
import org.wyona.yanel.core.source.SourceResolver;

import org.wyona.yarep.core.RepositoryException;
import org.wyona.yarep.core.Repository;

import org.wyona.yanel.core.util.WildcardReplacerHelper;

import org.apache.log4j.Category;

import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.MimeConstants;

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

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
    public View getView(String viewId) throws Exception {
        if (!exists()) {
            log.warn("No such XML '" + getDataPath() + "' in order to generate PDF!");
            throw new org.wyona.yanel.core.ResourceNotFoundException("No such XML '" + getDataPath() + "' in order to generate PDF!");
        }

        View defaultView = new View();
        defaultView.setMimeType(getMimeType(viewId));
        defaultView.setResponse(false); // This resource writes directly into the response output stream

        try {
            String yanelPath = getDataPath();
            Repository repo = getRealm().getRepository();
            InputStream docSource = null;
            if (yanelPath.startsWith("yanelrepo:") || yanelPath.startsWith("yanelresource:")) {
                SourceResolver resolver = new SourceResolver(this);
                Source source = resolver.resolve(yanelPath, null);
                docSource = ((StreamSource) source).getInputStream();
            } else {
                docSource = repo.getNode(yanelPath).getInputStream();
            }

            // Step 1: Construct a FopFactory
            // (reuse if you plan to render multiple documents!)
            FopFactory fopFactory = FopFactory.newInstance();

            // Step 2: Construct fop with desired output format
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, getResponse().getOutputStream());

            SourceResolver srcResolver = new SourceResolver(this);
            fopFactory.setURIResolver(srcResolver);

            /* Only for debugging ...
            java.io.FileOutputStream fout = new java.io.FileOutputStream("/home/michi/Desktop/yanel.pdf");
            driver.setOutputStream(fout);
            */

            // TODO: This doesn't seem to work properly (see below)
            //java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            //driver.setOutputStream(baos);

            Transformer transformer = TransformerFactory.newInstance().newTransformer(getXSLTStreamSource(getPath(),repo));

            org.xml.sax.XMLReader xmlReader = org.xml.sax.helpers.XMLReaderFactory.createXMLReader();
            xmlReader.setEntityResolver(new org.apache.xml.resolver.tools.CatalogResolver());
            Source src = new SAXSource(xmlReader, new org.xml.sax.InputSource(docSource));

            Result res = new SAXResult(fop.getDefaultHandler());

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
     *  convenient method to allow easy overriding of how the dataPath is generated
     *  @return String datapath
     */
    public String getDataPath() throws Exception {
        WildcardReplacerHelper dataPath = new WildcardReplacerHelper(getResourceConfigProperty("yanel-path"), getResourceConfigProperty("yanel-path-matcher"));
        return dataPath.getReplacedString(getPath());
    }

    /**
    *
    */
    public boolean exists() throws Exception {
        String yanelPath = getDataPath();
        Repository repo = getRealm().getRepository();
        if (yanelPath.startsWith("yanelrepo:") || yanelPath.startsWith("yanelresource:")) {
            log.warn("Not implemented yet!");
            return true;
        } else {
            return repo.existsNode(yanelPath);
        }
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
