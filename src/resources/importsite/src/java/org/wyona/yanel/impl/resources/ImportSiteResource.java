/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
//import java.io.UnsupportedEncodingException;
//import java.io.StringReader;
import java.util.Enumeration;
//import java.util.HashMap;
//import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Category;
import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceTypeDefinition;
import org.wyona.yanel.core.ResourceTypeRegistry;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.core.util.ResourceAttributeHelper;
import org.wyona.yarep.core.NoSuchNodeException;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryException;
import org.wyona.yarep.core.RepositoryFactory;
import org.wyona.yanel.core.transformation.I18nTransformer;
import org.wyona.yanel.core.Yanel;
import org.wyona.yarep.util.RepoPath;
import org.wyona.yarep.util.YarepUtil;
import org.wyona.yanel.core.util.HttpServletRequestHelper;

/**
 *
 */
public class ImportSiteResource extends Resource implements ViewableV2 {

    private static Category log = Category.getInstance(ImportSiteResource.class);
    private Repository repository  = null;
    private RepoPath rp = null;
    private Path path = null;
    private String defaultLanguage = "en";
    private String language = null;

    /**
     *
     */
    public ImportSiteResource() {
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
        return null;
    }

    /**
     * 
     */
    public View getView(String viewId) throws Exception {

        /*
        path = new Path(request.getServletPath());
        rp = new org.wyona.yarep.util.YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(path.toString()), getRepositoryFactory());
        repository = rp.getRepo();
        */
        
        // Get language
        try {
            language = request.getParameter("yanel.meta.language");
        } catch(Exception e) {
            log.debug("language param is not found will use default : " + language);
            language = defaultLanguage;
        }
        if(language == null || ("").equals(language)) {
            log.debug("language param is empty or null : " + language);
            language = defaultLanguage;
        }

        Transformer transformer = null;
        I18nTransformer i18nTransformer = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        View defaultView = new View();

        try {

            // Check if data was sumbitted (realm ID, realm Name, URL to be dumped, depth of crawling, max number of pages)
            boolean submit = false;
            Enumeration enumeration = request.getParameterNames();
            while(enumeration.hasMoreElements()){
                if(enumeration.nextElement().toString().equals("submit")) 
                    submit = true;
            }
            if(submit) {
                File statusXSLTFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile().getParentFile().getAbsolutePath(), "xslt" + File.separator + "importsite.xsl");
                File statusXMLFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile().getParentFile().getAbsolutePath(), "xml" + File.separator + "input-screen.xml");
                transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(statusXSLTFile));
                transformer.setParameter("realmid", HttpServletRequestHelper.getParameter(request, "realmid"));
                transformer.setParameter("realmname", HttpServletRequestHelper.getParameter(request, "realmname"));
                transformer.setParameter("url", HttpServletRequestHelper.getParameter(request, "url"));
                transformer.setParameter("crawldepth", HttpServletRequestHelper.getParameter(request, "crawldepth"));
                transformer.setParameter("maxpages", HttpServletRequestHelper.getParameter(request, "maxpages"));
            } else {
                File inputXSLTFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile().getParentFile().getAbsolutePath(), "xslt" + File.separator + "importsite.xsl");
                File inputXMLFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile().getParentFile().getAbsolutePath(), "xml" + File.separator + "input-screen.xml");
                transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(inputXSLTFile));

                transformer.transform(new javax.xml.transform.stream.StreamSource(inputXMLFile), new StreamResult(byteArrayOutputStream));
            }

            defaultView.setMimeType("application/xhtml+xml");

/*
            //translate the form
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            saxParser = SAXParserFactory.newInstance().newSAXParser();
            saxParser.parse(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), i18nTransformer);
            transformer = TransformerFactory.newInstance().newTransformer(getXSLTStreamSource(path, repository));
            transformer.setParameter("yanel.path.name", path.getName());
            transformer.setParameter("yanel.path", path.toString());
            transformer.setParameter("yanel.back2context", backToRoot(path, ""));
            transformer.setParameter("yarep.back2realm", backToRoot(new org.wyona.yanel.core.Path(rp.getPath().toString()), ""));
 
            byteArrayOutputStream = new ByteArrayOutputStream();
            transformer.transform(new StreamSource(i18nTransformer.getInputStream()), new StreamResult(byteArrayOutputStream));
            //tranlate the page
            i18nTransformer = new I18nTransformer("global", language);
            saxParser = SAXParserFactory.newInstance().newSAXParser();
            saxParser.parse(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), i18nTransformer);
*/
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        defaultView.setInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        //defaultView.setInputStream(i18nTransformer.getInputStream());
        return defaultView;
    }

    /**
     * 
     * @param path
     * @return
     * @throws Exception
     */
    private RepoPath contentRepo(Path path) throws Exception {
        return new YarepUtil().getRepositoryPath(new org.wyona.yarep.core.Path(
                path.toString()), getRepositoryFactory());
    }

    /**
     * 
     * @param path
     * @param viewId
     * @return
     */
    private String getMimeType(Path path, String viewId) {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * 
     * @param path
     * @return
     */
    private String getMimeType(String viewId) {
        String mimeType = null;
        try {
            java.io.BufferedReader br = new java.io.BufferedReader(rp
                    .getRepo().getReader(
                            new org.wyona.yarep.core.Path(new Path(rp
                                    .getPath().toString()).getRTIPath()
                                    .toString())));

            while ((mimeType = br.readLine()) != null) {
                if (mimeType.indexOf("mime-type:") == 0) {
                    mimeType = mimeType.substring(11);
                    log.info("*" + mimeType + "*");
                    return mimeType;
                }
            }
        } catch (Exception e) {
            log.warn(e);
        }
        // NOTE: Assuming fallback re importsite.xsl ...
        return "application/xhtml+xml";
    }
    
    /**
     * 
     * @param path
     * @param repo
     * @return
     * @throws NoSuchNodeException
     */
    private StreamSource getXSLTStreamSource(Path path, Repository repo)
            throws NoSuchNodeException, RepositoryException {
        Path xsltPath = getXSLTPath(path);
        if (xsltPath != null) {
            return new StreamSource(repo
                    .getInputStream(new org.wyona.yarep.core.Path(getXSLTPath(
                            path).toString())));
        } else {
            File xsltFile = org.wyona.commons.io.FileUtil.file(rtd
                    .getConfigFile().getParentFile().getAbsolutePath(), "xslt"
                    + File.separator + "importsite.xsl");
            log.error("DEBUG: XSLT file: " + xsltFile);
            return new StreamSource(xsltFile);
        }
    }
    
    /**
     * 
     * @param path
     * @return
     */
    private Path getXSLTPath(Path path) {
        String xsltPath = null;
        try {
            java.io.BufferedReader br = new java.io.BufferedReader(rp
                    .getRepo().getReader(
                            new org.wyona.yarep.core.Path(new Path(rp
                                    .getPath().toString()).getRTIPath()
                                    .toString())));

            while ((xsltPath = br.readLine()) != null) {
                if (xsltPath.indexOf("xslt:") == 0) {
                    xsltPath = xsltPath.substring(6);
                    log.debug("XSLT Path: " + xsltPath);
                    return new Path(xsltPath);
                }
            }
            log.error("No XSLT Path within: " + rp.getPath());
        } catch (Exception e) {
            log.warn(e);
        }
        return null;
    }
    
    /**
     * this method reads out the specified key from rti file 
     * if key cannot be found return null
     * @param key
     * @return value
     */
    private String getProperty(String key) {
        try {
            java.io.BufferedReader bufferedReader = new java.io.BufferedReader(rp.getRepo().getReader(new org.wyona.yarep.core.Path(new Path(rp.getPath().toString()).getRTIPath().toString())));
            String line = null;
            while((line = bufferedReader.readLine()) != null) {
                if(line.indexOf(key) == 0) {
                    return line.substring(key.length() + 1);
                }
            }
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }
   
   /**
    * 
    * @return
    */
   protected RepositoryFactory getRepositoryFactory() {
       return yanel.getRepositoryFactory("DefaultRepositoryFactory");
   }
   
   public boolean exists() throws Exception {
       log.warn("Not implemented yet!");
       return true; 
   }

   /**
    * 
    */
    public long getSize() throws Exception {
        return getRealm().getRepository().getSize(getPath());
    }

    
}
