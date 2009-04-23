/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources.navigation.lookup;

import javax.xml.transform.Transformer;

import org.w3c.dom.Document;
import org.wyona.yanel.core.map.RealmConfigPathResolver;
import org.wyona.yanel.core.navigation.Node;
import org.wyona.yanel.core.navigation.Sitetree;
import org.wyona.yanel.core.source.ResourceResolver;
import org.wyona.yanel.core.source.SourceResolver;
import org.wyona.yanel.impl.resources.BasicXMLResource;
import org.wyona.yanel.impl.resources.usecase.ExecutableUsecaseResource;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.ConfigurationUtil;
import org.apache.log4j.Logger;

/**
 *
 */
public class LookupResource extends ExecutableUsecaseResource {

    private static Logger log = Logger.getLogger(LookupResource.class);
    
    private static final String REQUEST_PARAMETER_TYPE = "type";
    private Sitetree sitetree;

    /**
     * Get sitetree as XML
     */
    public String getSitetreeAsXML() throws Exception {
        String name4pathParameter = "path";
        if (getResourceConfigProperty("name4path-parameter") != null) {
            name4pathParameter = getResourceConfigProperty("name4path-parameter");
        }
        StringBuilder sb = new StringBuilder("<sitetree>");
        if (getEnvironment().getRequest().getParameter(name4pathParameter) != null) {
            sb.append(getNodeAsXML(request.getParameter(name4pathParameter)));
        } else {
            sb.append(getNodeAsXML("/"));
        }
        sb.append("</sitetree>");
        return sb.toString();
    }

    /**
     * Get node as XML
     */
    private String getNodeAsXML(String path) throws Exception {
        String collectionsOnly = "false";
        
        if (getResourceConfigProperty("show-collections-only") != null) {
            collectionsOnly = getResourceConfigProperty("show-collections-only");
        }
        if (getEnvironment().getRequest().getParameter("show-collections-only") != null) {
            collectionsOnly = getEnvironment().getRequest().getParameter("show-collections-only");
        }
        
        Sitetree sitetree = getSitetree();
        Node node = sitetree.getNode(getRealm(), path);
        StringBuilder sb = new StringBuilder();

        if (node != null) {
            if (node.isCollection()) {
                Node[] children = node.getChildren();
                for (int i = 0; i < children.length; i++) {
                    String childPath = path + "/" + children[i].getName();
                    if (path.equals("/")) {
                        childPath = path + children[i].getName();
                    }
                    String nodeName = children[i].getName();
                    if (children[i].isCollection()) {
                        if(Boolean.parseBoolean(collectionsOnly)) {
                            sb.append("<collection path=\"" + childPath + "\" name=\"" + children[i].getName() + "\">");
                            // TODO: ...
                            sb.append("<label><![CDATA[" +children[i].getName() + "]]></label>");
                            sb.append("</collection>");
                        } else if (filterMatch(nodeName)) {
                            sb.append("<collection path=\"" + childPath + "\" name=\"" + children[i].getName() + "\">");
                            // TODO: ...
                            sb.append("<label><![CDATA[" +children[i].getName() + "]]></label>");
                            sb.append("</collection>");
                        }
                    } else if (children[i].isResource() && !Boolean.parseBoolean(collectionsOnly)) {
                        if (filterMatch(nodeName)) {
                            sb.append("<resource path=\"" + childPath + "\" name=\"" + nodeName + "\">");
                            sb.append("<label><![CDATA[" + nodeName + "]]></label>");
                            sb.append("</resource>");
                        }
                    } else {
                        sb.append("<neither-resource-nor-collection path=\"" + childPath + "\" name=\"" + children[i].getName() + "\"/>");
                    }
                }
            } else if (!Boolean.parseBoolean(collectionsOnly)) {
                String nodeName = node.getName();
                if (filterMatch(nodeName)) {
                    sb.append("<resource path=\"" + path + "\" name=\"" + nodeName + "\">");
                    // TODO ...
                    sb.append("<label><![CDATA[" + nodeName + "]]></label>");
                    sb.append("</resource>");
                }
            }
        } else {
            String errorMessage = "node is null for path: " + path;
            sb.append("<exception>" + errorMessage + "</exception>");
            log.error(errorMessage);
        }
        return sb.toString();
    }
    
    private Sitetree getSitetree() {
        if (sitetree == null) {
            try {
                Document customConfigDoc = getConfiguration().getCustomConfiguration();
                Configuration config = ConfigurationUtil.toConfiguration(customConfigDoc.getDocumentElement());
                Configuration sitetreeConfig = config.getChild("sitetree", false);
                String sitetreeImplClassName = sitetreeConfig.getAttribute("class");
                sitetree = (Sitetree) Class.forName(sitetreeImplClassName).newInstance();
                sitetree.init(org.wyona.yanel.core.util.ConfigurationUtil.getCustomConfiguration(sitetreeConfig, "sitetree-config", "http://www.wyona.org/yanel/realm/1.0"), new SourceResolver(this));
            } catch (Exception e) {
                log.info("Sitree is not configured, falling back to realm-repo-navigation");
                log.info(e.getMessage(), e);
                sitetree = getRealm().getRepoNavigation();
            } 
        }
        return sitetree;
    }
    
    /**
     * filters allows to show only certain file-types within the lookup.
     * the filters are configured by resource-type-property and request-parameter.
     * define some filter-patterns in the rc by adding some resource-config-properties named filter-pattern-$TYPE. Type will be 
     * used as group-id. when requested with the request parameter type=$TYPE it will apply all the filters named filter-pattern-$TYPE.
     * 
     *  e.g. &lt;yanel:property name="filter-pattern-image" value=".*[.]gif"/>
     * @return String[] with filter-patterns.
     */
    private String[] getFilters() {
        try {
            String type = getParameterAsString(REQUEST_PARAMETER_TYPE);
            return getResourceConfigProperties("filter-pattern-" + type);
        } catch (Exception e) {
            log.error("Could not get filter: " + e.getMessage(),e);
            return null;
        }
    }

    /**
     * @return
     */
    private boolean filterMatch(String nodeName) {
        String[] filters = getFilters();
        if (filters != null && filters.length > 0) {
            for (int i = 0; i < filters.length; i++) {
                if (nodeName.matches(filters[i])) {
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
    }
}
