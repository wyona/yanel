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

package org.wyona.yanel.impl.resources;

import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.util.PathUtil;

import javax.servlet.http.HttpServletRequest;

import org.wyona.yarep.core.NoSuchNodeException;
import org.wyona.yarep.core.RepositoryException;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryFactory;
import org.wyona.yarep.util.RepoPath;

import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.Entry;
import org.apache.log4j.Category;

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.Vector;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;

/**
 *
 */
public class AtomFeedResource extends Resource implements ViewableV1 {

    private static Category log = Category.getInstance(AtomFeedResource.class);

    Date feedUpdated = null;

    /**
     *
     */
    public AtomFeedResource() {
    }

    /**
     *
     */
    public ViewDescriptor[] getViewDescriptors() {
        ViewDescriptor[] vd = new ViewDescriptor[4];

        vd[0] = new ViewDescriptor("default");
        vd[0].setMimeType(getMimeType(null));

        vd[1] = new ViewDescriptor("source");
        vd[1].setMimeType("application/xml");

        vd[2] = new ViewDescriptor("atom");
        vd[2].setMimeType("application/atom+xml");

        vd[3] = new ViewDescriptor("rss2.0");
        vd[3].setMimeType("text/xml");

        return vd;
    }

    /**
     *
     */
    public View getView(String viewId, String requestURL, String queryString) {
        View defaultView = new View();
	StringBuffer sb = new StringBuffer("<?xml version=\"1.0\"?>");

	//sb.append("<?xml-stylesheet type=\"text/xsl\" href=\"yanel/resources/directory/xslt/dir2xhtml.xsl\"?>");

        String path = getPath();
        String entriesPath = getEntriesPath(path);

        Repository contentRepo = null;
        try {
            contentRepo = getRealm().getRepository();

            // TODO: Do not show the children with suffix .yanel-rti resp. make this configurable!
	    // NOTE: Do not hardcode the .yanel-rti, but rather use Path.getRTIPath ...
            org.wyona.yarep.core.Path[] children = contentRepo.getChildren(new Path(entriesPath));

            Vector orderedEntries = new Vector();
            org.apache.abdera.Abdera abdera = new org.apache.abdera.Abdera();
            org.apache.abdera.parser.Parser parser = abdera.getParser();
            if (parser != null) {
                for (int i = 0; i < children.length; i++) {
                    if (contentRepo.isResource(children[i])) {
                        org.apache.abdera.model.Document doc = parser.parse(contentRepo.getInputStream(children[i]));
                        if (doc != null) {
                            try {
                                Entry entry = (Entry) doc.getRoot();
                                if (entry != null) {
                                    entry.addLink(children[i].getName(), "edit");
                                    entry.addLink(getEntriesURL(path) + children[i].getName());
                                    orderedEntries = addEntry(orderedEntries, entry);
                                } else {
                                    log.error("Atom entry is null!" + children[i]);
                                }
                            } catch(ClassCastException e) {
                                log.warn(e + ": Does not seem to be an atom entry: " + children[i]);
                            }
                        } else {
                            log.error("Atom doc is null!" + children[i]);
                        }
                    }
                }
            } else {
                log.error("Atom Parser is null!");
            }

            // TODO: Add realm prefix, e.g. realm-prefix="ulysses-demo"
            // NOTE: The schema is according to http://cocoon.apache.org/2.1/userdocs/directory-generator.html
	    //sb.append("<atom:feed yanel:path=\"" + path + "\" dir:name=\"" + PathUtil.getName(entriesPath) + "\" dir:path=\"" + entriesPath + "\" xmlns:dir=\"http://apache.org/cocoon/directory/2.0\" xmlns:yanel=\"http://www.wyona.org/yanel/resource/directory/1.0\" xmlns:atom=\"http://www.w3.org/2005/Atom\">");

	    sb.append("<atom:feed xmlns:atom=\"http://www.w3.org/2005/Atom\">");

            sb.append("<atom:title>" + getFeedTitle(path) + "</atom:title>");

            // Set self ...
            String selfURL = getProperty(path, "self-url", null);
            if (selfURL != null) {
                sb.append("<atom:link rel=\"self\" href=\"" + selfURL + "\"/>");
            } else {
            if (requestURL != null) {
            org.wyona.yanel.core.map.Realm realm = getYanel().getMap().getRealm(path);
            String proxyHostName = realm.getProxyHostName();
            if (proxyHostName != null) {
                URL url = new URL(requestURL);
                url = new URL(url.getProtocol(), proxyHostName, url.getPort(), url.getFile());
                int proxyPort = realm.getProxyPort();
                if (proxyPort >= 0) {
                    url = new URL(url.getProtocol(), url.getHost(), proxyPort, url.getFile());
                } else {
                    url = new URL(url.getProtocol(), url.getHost(), url.getDefaultPort(), url.getFile());
                }
                String proxyPrefix = realm.getProxyPrefix();
                if (proxyPrefix != null) {
                    url = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile().substring(proxyPrefix.length()));
                }
                if (queryString != null) {
                    sb.append("<atom:link rel=\"self\" href=\"" + url + "?" + queryString + "\"/>");
                } else {
                    sb.append("<atom:link rel=\"self\" href=\"" + url + "\"/>");
                }
            } else {
                // TODO: Add query string
                sb.append("<atom:link rel=\"self\" href=\"" + requestURL + "\"/>");
            }
            } else {
                sb.append("<atom:link rel=\"self\" href=\"" + path + "\"/>");
            }
            }


            sb.append("<atom:updated>" + AtomDate.format(feedUpdated) + "</atom:updated>");
            sb.append("<atom:author><atom:name>" + getProperty(path, "author", "WARNING: No author specified!") + "</atom:name></atom:author>");
            sb.append("<atom:id>urn:uuid:TODO</atom:id>");

            for (int i = 0; i < orderedEntries.size(); i++) {
                Entry entry = (Entry) orderedEntries.elementAt(i);

	        //sb.append("<dir:file name=\"" + entry.getLink("edit").getHref() + "\"/>");

                java.io.StringWriter sw = new java.io.StringWriter();
                //org.apache.abdera.writer.Writer writer = org.apache.abdera.writer.Writer.INSTANCE;
                //writer.writeTo(entry, sw);
                entry.writeTo(sw);
                sb.append(sw.toString());
            }
        } catch(Exception e) {
            log.error(e, e);
        }
	sb.append("</atom:feed>");

        if (viewId != null && viewId.equals("source")) {
            defaultView.setMimeType("application/xml");
	    defaultView.setInputStream(new java.io.StringBufferInputStream(sb.toString()));
            return defaultView;
        }

        if (viewId != null && viewId.equals("atom")) {
            defaultView.setMimeType("application/atom+xml");
	    defaultView.setInputStream(new java.io.StringBufferInputStream(sb.toString()));
            return defaultView;
        }
        
        if (viewId != null && viewId.equals("rss2.0")) {
        	try {
        		File xsltFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile().getParentFile().getAbsolutePath(), "xslt" + File.separator + "atomfeed2rss2.0.xsl");
        		Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(xsltFile));
        		java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                transformer.transform(new StreamSource(new java.io.StringBufferInputStream(sb.toString())), new StreamResult(baos));
                defaultView.setInputStream(new java.io.ByteArrayInputStream(baos.toByteArray()));
                defaultView.setMimeType("text/xml");
                defaultView.setInputStream(new java.io.ByteArrayInputStream(baos.toByteArray()));	            
        	} catch(Exception e) {
                log.error(e);
            }
            return defaultView;
        }


        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer(getXSLTStreamSource(path, contentRepo));
            transformer.setParameter("yanel.path.name", PathUtil.getName(path));
            transformer.setParameter("yanel.path", path.toString());
            //transformer.setParameter("yanel.back2context", backToRoot(path, ""));
            //transformer.setParameter("yarep.back2realm", backToRoot(new org.wyona.yanel.core.Path(rp.getPath().toString()), ""));
            // TODO: Is this the best way to generate an InputStream from an OutputStream?
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            transformer.transform(new StreamSource(new java.io.StringBufferInputStream(sb.toString())), new StreamResult(baos));
            defaultView.setInputStream(new java.io.ByteArrayInputStream(baos.toByteArray()));
            defaultView.setMimeType(getMimeType(path));
	        defaultView.setInputStream(new java.io.ByteArrayInputStream(baos.toByteArray()));
        } catch (Exception e) {
            log.error(e);
        }

        return defaultView;
    }

    /**
     *
     */
    public View getView(Path path, String viewId) {
        return getView(viewId, null, null);
    }

    /**
     *
     */
    public View getView(HttpServletRequest request, String viewId) {
        return getView(viewId, request.getRequestURL().toString(), request.getQueryString());
    }

    /**
     *
     */
    private StreamSource getXSLTStreamSource(String path, Repository repo) throws RepositoryException {
        String xsltPath = getXSLTPath(path);
        if(xsltPath != null) {
            return new StreamSource(repo.getInputStream(new org.wyona.yarep.core.Path(getXSLTPath(path))));
        } else {
            File xsltFile = org.wyona.commons.io.FileUtil.file(rtd.getConfigFile().getParentFile().getAbsolutePath(), "xslt" + File.separator + "atomfeed2xhtml.xsl");
            log.debug("XSLT file: " + xsltFile);
            return new StreamSource(xsltFile);
        }
    }

    /**
     *
     */
    private String getXSLTPath(String path) {
        String xsltPath = getProperty(path, "xslt", null);
        if (xsltPath != null) return xsltPath;
        log.info("No XSLT Path within: " + path);
        return null;
    }

    /**
     *
     */
    private String getMimeType(String path) {
        String mimeType = getProperty(path, "mime-type", null);
        if (mimeType != null) return mimeType;

        // NOTE: Assuming fallback re dir2xhtml.xsl ...
        return "application/xhtml+xml";
    }

    /**
     *
     */
    private String getFeedTitle(String path) {
        return getProperty(path, "feed-title", "WARNING: No feed title specified!");
    }

    /**
     * Get property from resource instance configuration
     */
    private String getProperty(String path, String name, String defaultValue) {
        String propertyValue = null;
	
        try {
	    propertyValue = getResourceConfigProperty(name);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        if (propertyValue == null) propertyValue = defaultValue;

        return propertyValue;
    }

    /**
     * Get path to entries
     */
    private String getEntriesPath(String feedPath) {
        String entriesPathString = getProperty(feedPath, "entries-path", null);

	if (entriesPathString != null) {
            return entriesPathString;
        } else {
            try {
                Repository repo = getRealm().getRepository();
                org.wyona.yarep.core.Path entriesPath = new org.wyona.yarep.core.Path(feedPath);

                // TODO: This doesn't seem to work ... (check on Yarep ...)
                if (repo.isResource(entriesPath)) {
                    log.warn("Path is a resource instead of a collection: " + entriesPath);
                    //entriesPath = entriesPath.getParent();
                }

                // TODO: Implement org.wyona.yarep.core.Path.getParent()
                if (!repo.isCollection(entriesPath)) {
                    log.warn("Path is not a collection: " + entriesPath);
                    entriesPath = new org.wyona.yarep.core.Path(new org.wyona.commons.io.Path(entriesPath.toString()).getParent().toString());
                    log.warn("Use parent of path: " + entriesPath);
                }
                return entriesPath.toString();
            } catch(Exception e) {
                log.error(e);
                return null;
            }
        }
    }

    /**
     * Is this method obsolete?
     * @param path feed path
     * @param in entry content
     * @return entry path
     */
    public String createEntry(String path, java.io.InputStream in) {
        try {
            org.wyona.yarep.core.Path entryPath = new org.wyona.yarep.core.Path(getEntriesPath(path) + "/" + new Date().getTime() + ".xml");
            java.io.OutputStream out = getRealm().getRepository().getOutputStream(entryPath);
            byte buffer[] = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
               out.write(buffer, 0, bytesRead);
            }
            log.info("Atom entry has been created: " + entryPath);

            return entryPath.toString();
        } catch(Exception e) {
            log.error(e);
        }
        return null;
    }

    /**
     * Order entries by published date and set feed updated
     */
    private Vector addEntry(Vector orderedEntries, Entry entry) throws Exception {
        Date datePublished = entry.getPublished();
        if (datePublished != null) {
            long timePublished = datePublished.getTime();
            int pos = 0;
            for (int i = 0; i < orderedEntries.size(); i++) {
                Entry current = (Entry) orderedEntries.elementAt(i);
                if (timePublished > current.getPublished().getTime()) {
                    break;
                }
                pos++;
            }
            orderedEntries.add(pos, entry);
        } else {
            log.error("Entry will be ignored because entry does not have a published date: " + entry.getLink("edit").getHref());
        }

        Date entryUpdated = entry.getUpdated();
        if (entryUpdated != null) {
            long timeUpdated = entryUpdated.getTime();
            if (feedUpdated == null) feedUpdated = entryUpdated;
            if (timeUpdated > feedUpdated.getTime()) {
                feedUpdated = entryUpdated;
            }
        }

        return orderedEntries;
    }

    /**
     *
     */
    String getEntriesURL(String path) {
        return getProperty(path, "entries-url", null);
    }
    
    protected RepositoryFactory getRepositoryFactory() {
        return yanel.getRepositoryFactory("DefaultRepositoryFactory");
    }
    
}
