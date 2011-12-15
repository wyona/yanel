/*
 * Copyright 2011 Wyona
 */
package org.wyona.yanel.impl.resources.search;

import org.wyona.yarep.util.YarepUtil;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.impl.search.lucene.LuceneSearcher;

import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.impl.resources.BasicXMLResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * This resource can be used to clean the index of a repository.
 * If you configure this resource in your realm, make sure to protect it because you
 * most likely don't want your users to clean the index.
 */
public class CleanIndexResource extends BasicXMLResource {
    private static Logger log = Logger.getLogger(CleanIndexResource.class);

    private static String REPO_NAME = "repository";
    private static String REINDEX_XMLNS = "http://www.wyona.org/yanel/clean-index/1.0";
    
    /**
     * @see org.wyona.yanel.impl.resources.BasicXMLResource#getContentXML(String)
     */
    protected InputStream getContentXML(String viewId) throws Exception {
        // Build output document
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\"?>");
        sb.append("<r:clean-index xmlns:r=\"");
        sb.append(REINDEX_XMLNS);
        sb.append("\">");

        // Which repo needs to be cleaned?
        String selectedRepo = getEnvironment().getRequest().getParameter(REPO_NAME); 

        // Are we allowed to clean this repository?
        // Only default repositories and the ones listed in the resource configuration are allowed to be cleaned
        boolean allowed = false;

        // List default repositories
        // Only show them if we're allowed to clean them
        String defaultRepos = getResourceConfigProperty("allow-default-repos");
        if("true".equals(defaultRepos)) {
            sb.append("<r:repository id=\"yanel_data\">Realm's data repository</r:repository>");
            sb.append("<r:repository id=\"yanel_ac-identities\">Realm's ac-identities repository</r:repository>");
            sb.append("<r:repository id=\"yanel_ac-policies\">Realm's ac-policies repository</r:repository>");
            sb.append("<r:repository id=\"yanel_res-configs\">Realm's res-configs repository</r:repository>");
        }

        // List extra repositories from repo configuration
        String[] repoIds = getResourceConfigProperties("repository-id");
        for(String repoId : repoIds) {
            if(repoId != null && !"".equals(repoId)) {
                sb.append("<r:repository id=\"");
                sb.append(repoId);
                sb.append("\">Configured repository with id '");
                sb.append(repoId);
                sb.append("'</r:repository>");
                // Check  if repo that should be cleaned is listed in resource configuration (see property 'repository-id')
                if(!allowed && repoId.equals(selectedRepo)) allowed = true;
            }
        }

        // Check if repo that should be cleaned is default repo
        if(!allowed && "true".equals(defaultRepos) &&
          ("yanel_data".equals(selectedRepo) || 
           "yanel_ac-policies".equals(selectedRepo) || 
           "yanel_ac-identities".equals(selectedRepo) || 
           "yanel_res-configs".equals(selectedRepo))) {
                allowed = true;
        }

        // If it's an extra repo, allowed needs to be set to true
        Repository repo = null;
        if(allowed) {
            try {
                repo = getRealm().getRepository(selectedRepo);
            } catch(Exception e) {
                sb.append("<r:exception>Opening repo failed with exception: ");
                sb.append(e.getMessage());
                sb.append("</r:exception>");
            }
        }

        // INFO: Check what would/should be cleaned
        if(repo != null) {
            YarepUtil yu = new YarepUtil();

            try {
                log.warn("DEBUG: Check for nodes which still exist inside the search index, but do not exist anymore inside the repository: " + repo.getName());
                LuceneSearcher luceneSearcher = (LuceneSearcher) repo.getSearcher();
                boolean delete = false;
                if (getEnvironment().getRequest().getParameter("delete") != null) {
                    delete = new Boolean(getEnvironment().getRequest().getParameter("delete")).booleanValue();
                }
                String[] missingNodePaths = luceneSearcher.getMissingNodes(delete);
                sb.append("<r:message>Cleaning means that " + missingNodePaths.length + " index entries should be removed, because the corresponding content nodes do not seem to exist anymore inside the repository '" + repo.getName() + "'</r:message>");

                if (missingNodePaths.length > 0) {
                    sb.append("<r:missing-nodes>");
                    for (int i = 0; i < missingNodePaths.length; i++) {
                        sb.append("<r:path>" + missingNodePaths[i] + "</r:path>");
                    }
                    sb.append("</r:missing-nodes>");
                } else {
                    sb.append("<r:no-missing-nodes/>");
                }

                //sb.append("<r:message>Cleaning index was successful.</r:message>");
            } catch(Exception e) {
                log.error(e, e);
                sb.append("<r:exception>" + e.getMessage() + "</r:exception>");
            }
        } else if(selectedRepo != null) {
            sb.append("<r:exception>Repository '" + selectedRepo + "' does either not exist or is not configured in order to be cleaned.</r:exception>");
        }

        sb.append("</r:clean-index>");
        return new ByteArrayInputStream(sb.toString().getBytes());
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#exists()
     */
    public boolean exists() throws Exception {
        return true;
    }
}
