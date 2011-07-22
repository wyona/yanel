/*
 * Copyright 2011 Wyona
 */
package org.wyona.yanel.impl.resources.search;

import org.wyona.yarep.util.YarepUtil;
import org.wyona.yarep.core.Repository;

import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.impl.resources.BasicXMLResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * Re-indexing resource. This resource can be used to start the re-indexing
 * process of an arbitrary repository from within a browser session. If you
 * configure this resource in your realm, make sure to protect it because you
 * most likely don't want your users to start re-indexing processes.
 */
public class ReindexResource extends BasicXMLResource {
    private static Logger log = Logger.getLogger(ReindexResource.class);

    private static String REPO_NAME = "repository";
    private static String REINDEX_XMLNS = "http://www.wyona.org/yanel/reindex/1.0";
    
    /**
     * @see org.wyona.yanel.impl.resources.BasicXMLResource#getContentXML(String)
     */
    protected InputStream getContentXML(String viewId) throws Exception {
        // Build output document
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\"?>");
        sb.append("<r:reindex xmlns:r=\"");
        sb.append(REINDEX_XMLNS);
        sb.append("\">");

        // Which repo needs to be re-indexed?
        String reindexRepo = getEnvironment().getRequest().getParameter(REPO_NAME); 

        // Are we allowed to re-index this repository?
        // Only default repositories and the ones listed in the resource configuration are allowed to be re-indexed
        boolean allowed = false;

        // List default repositories
        // Only show them if we're allowed to re-index them
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
                // Check  if repo that should be re-indexed is listed in resource configuration (see property 'repository-id')
                if(!allowed && repoId.equals(reindexRepo)) allowed = true;
            }
        }

        // Check if repo that should be re-indexed is default repo
        if(!allowed && "true".equals(defaultRepos) &&
          ("yanel_data".equals(reindexRepo) || 
           "yanel_ac-policies".equals(reindexRepo) || 
           "yanel_ac-identities".equals(reindexRepo) || 
           "yanel_res-configs".equals(reindexRepo))) {
                allowed = true;
        }

        // If it's an extra repo, allowed needs to be set to true
        Repository repo = null;
        if(allowed) {
            try {
                repo = getRealm().getRepository(reindexRepo);
            } catch(Exception e) {
                sb.append("<r:exception>Opening repo failed with exception: ");
                sb.append(e.getMessage());
                sb.append("</r:exception>");
            }
        }

        // Perform re-indexing now
        if(repo != null) {
            YarepUtil yu = new YarepUtil();

            try {
                yu.indexRepository(repo);
                sb.append("<r:message>Re-indexing was successful.</r:message>");
            } catch(Exception e) {
                sb.append("<r:exception>Re-indexing failed with exception: ");
                sb.append(e.getMessage());
                sb.append("</r:exception>");
            }
        } else if(reindexRepo != null) {
            sb.append("<r:exception>Repository '" + reindexRepo + "' does either not exist or is not configured in order to be re-indexed.</r:exception>");
        }

        sb.append("</r:reindex>");
        return new ByteArrayInputStream(sb.toString().getBytes());
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#exists()
     */
    public boolean exists() throws Exception {
        return true;
    }
}
