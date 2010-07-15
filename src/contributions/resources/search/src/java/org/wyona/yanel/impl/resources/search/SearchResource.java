/*
 * Copyright 2009 Wyona
 */

package org.wyona.yanel.impl.resources.search;

import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.impl.resources.BasicXMLResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;

import org.wyona.meguni.parser.Parser;
import org.wyona.meguni.util.ResultSet;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationUtil;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * Search resource. The configuration parameters "max-entries-per-page" and "max-navigation-links"
 * can be used in the resource configuration file to set the number of search results per page
 * and the number of links that will be displayed to navigate between those pages (usually at the 
 * bottom of the page). The page numbers to be linked to will be passed to the XSLT so that they can 
 * be handled there accordingly. The current page number must be shared between java and XSLT
 * via the request parameter "page".
 */
public class SearchResource extends BasicXMLResource {
    
    private static Logger log = Logger.getLogger(SearchResource.class);

    private static String PROVIDER_NAME = "provider";
    private static String QUERY_NAME = "q";
    private static String DOMAIN_NAME = "domain";
    private static String DEFAULT_PROVIDER = "yanel";
    private static final String CURRENT_PAGE_NUMBER_REQUEST_PARAMETER_NAME = "page";
    private static final String MAX_ENTRIES_CONFIG_PARAMETER_NAME = "max-entries-per-page";
    private static final int MAX_ENTRIES_DEFAULT = 3;
    private static final String MAX_PAGINATION_LINKS_CONFIG_PARAMETER_NAME = "max-navigation-links";
    private static final int MAX_PAGINATION_LINKS_DEFAULT = 1;

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#getView(String)
     */
    public View getView(String viewId) throws Exception {
        String provider = getEnvironment().getRequest().getParameter(PROVIDER_NAME);
        if (provider != null && !provider.equals(DEFAULT_PROVIDER)) {
            ExternalSearchProvider esp = getExternalSearchProvider(provider);
            if (esp != null) {
                View view = new View();
                view.setResponse(false); // this resource writes the response itself in order to do a server side redirect

                javax.servlet.http.HttpServletResponse response = getEnvironment().getResponse();
                response.setStatus(307); // Temporary redirect (http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html)

                String query = getEnvironment().getRequest().getParameter(QUERY_NAME);
                String domain = getEnvironment().getRequest().getParameter(DOMAIN_NAME);
                String site="";
                if (domain != null) site = "+site:" + domain; // TODO: This will work for Google and bing, but is this true for all search engines?
                response.setHeader("Location", esp.getURL() + query + site);

                return view;
            }
        }
        return super.getView(viewId);
    }
    
    /*
     * @see org.wyona.yanel.impl.resources.BasicXMLResource#getContentXML(String)
     */
    protected InputStream getContentXML(String viewId) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\"?>");
        sb.append("<y:search xmlns:y=\"http://www.wyona.org/yanel/search/1.0\">");

        String provider = getEnvironment().getRequest().getParameter(PROVIDER_NAME);
        if (provider == null) {
            provider = DEFAULT_PROVIDER;
            log.warn("No search provider specified! Default provider will be used: " + provider);
        }
        sb.append("<y:provider id=\"" + provider + "\">" + provider + "</y:provider>");

        String query = getEnvironment().getRequest().getParameter(QUERY_NAME);
        if (query != null && query.length() > 0) {
            sb.append("<y:query>" + StringEscapeUtils.escapeXml(query) + "</y:query>");
            try {
                Result[] results;
                if (provider.equals(DEFAULT_PROVIDER)) {
                    results = getLocalResults(query);
                } else if (provider.equals("yanelproxy-google")) {
                    results = getProxyResults(query, "org.wyona.meguni.parser.impl.GoogleParser");
                } else if (provider.equals("yanelproxy-msn")) {
                    results = getProxyResults(query, "org.wyona.meguni.parser.impl.MSNParser");
                } else {
                    results = new Result[0];
                    String eMessage = "No such provider: " + provider;
                    log.warn(eMessage);
                    sb.append("<y:exception>" + eMessage + "</y:exception>");
                }

                if (results != null && results.length > 0) {

                    boolean paginationEnabled = false;
                    int currentPageNumber = 1;
                    int maxEntries = MAX_ENTRIES_DEFAULT;
                    String maxEntriesStr = getResourceConfigProperty(MAX_ENTRIES_CONFIG_PARAMETER_NAME);
                    if (maxEntriesStr != null && 0 != maxEntriesStr.length()){
                        maxEntries = Integer.parseInt(maxEntriesStr.trim());
                        paginationEnabled = true;
                    }
                    int maxLinks = MAX_PAGINATION_LINKS_DEFAULT;
                    String maxLinksStr = getResourceConfigProperty(MAX_PAGINATION_LINKS_CONFIG_PARAMETER_NAME);
                    if (maxLinksStr != null && 0 != maxLinksStr.length()){
                        maxLinks = Integer.parseInt(maxLinksStr.trim());
                        paginationEnabled = true;
                    }


                    if (paginationEnabled) {
                        if (log.isDebugEnabled()) {
                            log.debug("Max number of search results per page is set to " + maxEntries);
                            log.debug("Max number of results page links is set to " + maxLinks);
                            log.debug("Results will be paginated"); 
                        }

                        String currentPageNumberStr = getEnvironment().getRequest().getParameter(CURRENT_PAGE_NUMBER_REQUEST_PARAMETER_NAME);
                        if (currentPageNumberStr == null || 0 == currentPageNumberStr.length()) {
                            currentPageNumber = 1;
                        } else {
                            currentPageNumber = Integer.parseInt(currentPageNumberStr.trim());
                            if (log.isDebugEnabled()) {
                                log.debug("The currently diplayed results page number is " + currentPageNumber);
                            }
                        }
                    } else {
                        log.warn("Pagination is not configured within resource config, hence all results will be displayed.");
                    }


                    int totalPages = (results.length / maxEntries);
                    if (results.length % maxEntries > 0) {
                        totalPages = totalPages + 1;
                    }
                    int firstLink = currentPageNumber - (maxLinks / 2);
                    int lastLink = firstLink + maxLinks;
                    if (log.isDebugEnabled()) {
                        log.debug("Total number of results = " + results.length);
                        log.debug("Total number of pages = " + totalPages);
                        log.debug("First link page number = " + firstLink);
                        log.debug("Last link page number = " + lastLink);
                    }

                    if (paginationEnabled) {
                        sb.append("<y:pages max-entries-per-page=\"" + maxEntries + "\" max-page-links=\"" + maxLinks + "\">");
                        if (currentPageNumber > 1) {
                            sb.append("<y:previous>" + (currentPageNumber - 1) + "</y:previous>");
                        }
                        for (int pageNumber = firstLink; pageNumber < lastLink; pageNumber++) {
                            if (pageNumber <= 0) continue;
                            if (pageNumber > totalPages) break;
                            sb.append("<y:page");
                            if (pageNumber == currentPageNumber) {
                                sb.append(" selected='true'");
                            }
                            sb.append(">" + pageNumber + "</y:page>");
                        }
                        if (currentPageNumber < totalPages) {
                            sb.append("<y:next>" + (currentPageNumber + 1) + "</y:next>");
                        }
                        sb.append("</y:pages>");
                    }
                    
                    sb.append("<y:results total=\"" + results.length + "\" page-number='" + currentPageNumber + "'>");
                    int startingEntry = 0;
                    int numberOfEntries = results.length;
                    if (paginationEnabled) {
                        numberOfEntries = maxEntries;
                        startingEntry = (currentPageNumber - 1) * numberOfEntries;
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("Starting entry: " + startingEntry);
                        log.debug("Number of entries: " + numberOfEntries);
                    }
                    for (int i = startingEntry; i < (startingEntry + numberOfEntries); i++) {
                        if (i >= results.length) {
                            // INFO: Break in case number of entries on last page exceeds length of results
                            break;
                        }
                        sb.append("<y:result url=\"" + results[i].getURL() + "\">");
                        if (results[i].getTitle() != null) {
                            log.debug("Title: " + results[i].getTitle());
                            sb.append("  <y:title>" + StringEscapeUtils.escapeXml(results[i].getTitle()) + "</y:title>");
                        } else {
                            sb.append("  <y:no-title/>");
                        }
                        if (results[i].getDescription() != null) {
                            sb.append("  <y:excerpt><![CDATA[" + results[i].getDescription() + "]]></y:excerpt>");
                        } else {
                            sb.append("  <y:no-excerpt/>");
                        }
                        sb.append("</y:result>");
                    }
                    sb.append("</y:results>");
                }
            } catch(org.wyona.yarep.core.search.SearchException e) {
                log.error(e, e);
                if (e.getMessage().contains("no segments")) {
                    sb.append("<y:exception>It seems that no search index exists yet.</y:exception>"); // INFO: Generally a search index will be created automagically when editing content.
                } else {
                    sb.append("<y:exception>" + e.getMessage() + "</y:exception>");
                }
            }
        } else {
            sb.append("<y:no-query/>");
        }
        sb.append("</y:search>");
        return new ByteArrayInputStream(sb.toString().getBytes());
    }

    /**
     * @param query Search terms
     */
    private Result[] getLocalResults(String query) throws Exception {
        if (query != null && query.length() > 0) {

            org.wyona.yarep.core.Node[] nodes = null;
            String propertyName = getResourceConfigProperty("property-name");
            if (propertyName != null) {
                log.warn("DEBUG: Search property '" + propertyName + "': " + query);
                nodes = getRealm().getRepository().getSearcher().searchProperty(propertyName, query, "/");
            } else {
                log.warn("DEBUG: Search fulltext: " + query);
                nodes = getRealm().getRepository().getSearcher().search(query);
            }

            if (nodes != null && nodes.length > 0) {
                Result[] results = new Result[nodes.length];
                for (int i = 0; i < nodes.length; i++) {
                    // TODO: Check access policy if user is actually allowed to see this result
                    results[i] = new Result(nodes[i].getPath(), getTitle(nodes[i].getPath(), nodes[i].getInputStream(), nodes[i].getMimeType()), "TODO: excerpt", nodes[i].getMimeType(), null);
                }
                return results;
            } else {
               log.info("Nothing found for query: " + query);
                return new Result[0];
            }
        }
        log.warn("No query specified!");
        return new Result[0];
    }

    /**
     *
     */
    private Result[] getProxyResults(String query, String parserClassName) throws Exception {
        Parser parser = (Parser) Class.forName(parserClassName).newInstance();
        ResultSet rs = parser.parse(query);

        if (rs != null && rs.size() > 0) {
            Result[] results = new Result[rs.size()];
            for (int i = 0; i < rs.size(); i++) {
                //log.warn("DEBUG: Title: " + rs.get(i).title);
                //log.warn("DEBUG: Excerpt: " + rs.get(i).excerpt);
                results[i] = new Result(rs.get(i).url.toString(), null, null, null, null);
                //results[i] = new Result(rs.get(i).url.toString(), rs.get(i).title, rs.get(i).excerpt, null, null);
            }
            return results;
        } else {
            return new Result[0];
        }
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#exists()
     */
    public boolean exists() throws Exception {
        return true;
    }

    /**
     *
     */
    private ExternalSearchProvider getExternalSearchProvider(String providerId) throws Exception {
        org.w3c.dom.Document customConfigDoc = getConfiguration().getCustomConfiguration();
        if (customConfigDoc != null) {
            Configuration config = ConfigurationUtil.toConfiguration(customConfigDoc.getDocumentElement());
            Configuration externalSearchProvidersConfig = config.getChild("external-search-providers");
            Configuration[] searchProviders = externalSearchProvidersConfig.getChildren("provider");
            for (int i = 0; i < searchProviders.length; i++) {
                if (searchProviders[i].getAttribute("id").equals(providerId)) {
                    return new ExternalSearchProvider(providerId, searchProviders[i].getAttribute("url"), null);
                }
            }
        }
        return null;
    }

    /**
     * Get title of node
     * @param path Node path
     * @param in Node content as InputStream
     * @param mimeType Node content type
     */
    private String getTitle(String path, InputStream in, String mimeType) throws Exception {
        log.debug("Get title of node: " + path);
        if (mimeType != null) {

            // NOTE: Please also see src/impl/java/org/wyona/yarep/impl/search/lucene/LuceneConfig.java
            org.apache.tika.config.TikaConfig tikaConfig;
            java.io.File localTikaConfigFile = new java.io.File(getRealm().getRepository().getConfigFile().getParent(), "tika-config.xml");
            if (localTikaConfigFile.isFile()) {
                log.info("Use local tika config: " + localTikaConfigFile);
                tikaConfig = new org.apache.tika.config.TikaConfig(localTikaConfigFile);
            } else {
                log.info("Use default tika config.");
                tikaConfig = org.apache.tika.config.TikaConfig.getDefaultConfig();
            }

            org.apache.tika.parser.Parser parser = tikaConfig.getParser(mimeType);
            if (parser != null) {
                try {
                    // NOTE: The tika meta data must not be null, hence we just declare something
                    org.apache.tika.metadata.Metadata tikaMetaData = new org.apache.tika.metadata.Metadata();
                    tikaMetaData.set("yarep:path", path);

                    XHTMLBean xhtmlBean = new XHTMLBean();
                    parser.parse(in, new XHTMLBeanContentHandler(xhtmlBean), tikaMetaData);
                    //parser.parse(in, new DebugContentHandler(), tikaMetaData);

                    //java.io.StringWriter writer = new java.io.StringWriter();
                    //parser.parse(in, new TitleContentHandler(writer), tikaMetaData);
                    //parser.parse(in, new org.apache.tika.sax.BodyContentHandler(writer), tikaMetaData);
                    //parser.parse(in, new org.apache.tika.sax.WriteOutContentHandler(writer), tikaMetaData);
                    //String title = writer.toString().trim();

                    log.warn("DEBUG: Keywords: " + tikaMetaData.get(org.apache.tika.metadata.Metadata.KEYWORDS));

                    String title = xhtmlBean.getTitle();
                    if (title != null && title.length() > 0) {
                        return title;
                    }
                } catch (Exception e) {
                    log.error(e, e);
                }
            } else {
                log.warn("Tika parser is null! Mime type '" + mimeType + "' did probably not match any configured parser.");
            }
        } else {
            log.warn("Node '" + path + "' has no content type and hence will not be parsed re title");
        }
        return null;
    }
}

/**
 *
 */
class ExternalSearchProvider {

    private String url;
    
    /**
     *
     */
    public ExternalSearchProvider(String id, String url, String label) {
        this.url = url;
    }

    /**
     *
     */
    public String getURL() {
        return url;
    }
}
