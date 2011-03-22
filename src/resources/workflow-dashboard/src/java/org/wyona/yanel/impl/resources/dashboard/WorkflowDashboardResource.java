/*
 * Copyright 2011 Wyona
 */
package org.wyona.yanel.impl.resources.dashboard;

import org.wyona.yanel.impl.resources.BasicXMLResource;

import org.wyona.yanel.core.workflow.Workflow;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * A resource in order to find resources by their workflow state
 */
public class WorkflowDashboardResource extends BasicXMLResource {
    
    private static Logger log = Logger.getLogger(WorkflowDashboardResource.class);
    
    /**
     * @see org.wyona.yanel.impl.resources.BasicXMLResource#getContentXML(String)
     */
    protected InputStream getContentXML(String viewId) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }

        String workflowState = null;
        if (getEnvironment().getRequest().getParameter("workflow-state") != null) {
            workflowState = getEnvironment().getRequest().getParameter("workflow-state");
        }

        String queryText = null;
        String mimeType = null;
        if (workflowState != null) {
            queryText = "workflow-state:" + workflowState;
            mimeType = getEnvironment().getRequest().getParameter("mime-type");
            if (mimeType != null && !mimeType.equals("none_selected")) {
                queryText = queryText + " AND " + "yarep_mimeType:" + mimeType;
            }
        }

/*
        if (org.wyona.commons.clazz.ClazzUtil.implementsInterface(getRealm().getRepository(), "org.wyona.yarep.core.attributes.LuceneSearchableV1")) {
            log.warn("DEBUG: Repo is lucene searchable ...");
            org.apache.lucene.search.Searcher searcher = ((org.wyona.yarep.core.attributes.LuceneSearchableV1)getRealm().getRepository()).getLuceneSearcher();

            String defaultField = "_FULLTEXT";
            org.apache.lucene.queryParser.QueryParser queryParser = new org.apache.lucene.queryParser.QueryParser(defaultField, new org.apache.lucene.analysis.standard.StandardAnalyzer());
            log.warn("DEBUG: Default field: " + defaultField);
            org.apache.lucene.search.Query query = queryParser.parse(queryText);

            // How to use StandardAnalyzer with TermQuery? http://stackoverflow.com/questions/1390088/how-do-i-use-standardanalyzer-with-termquery, http://today.java.net/pub/a/today/2003/07/30/LuceneIntro.html
            // What about MultiFieldQueryParser?
            //org.apache.lucene.search.Query queryF1 = new org.apache.lucene.search.TermQuery(new org.apache.lucene.index.Term("_FULLTEXT", "sugus"));
//org.apache.lucene.search.Query queryF1 = new org.apache.lucene.search.TermQuery(new org.apache.lucene.index.Term("workflow-state", workflowState));
            //org.apache.lucene.search.BooleanQuery query = new org.apache.lucene.search.BooleanQuery();
            //query.add(queryF1, org.apache.lucene.search.BooleanClause.Occur.MUST);

            //org.apache.lucene.search.Query query = new org.apache.lucene.queryParser.QueryParser("_FULLTEXT", (org.apache.lucene.analysis.Analyzer) Class.forName("org.apache.lucene.analysis.standard.StandardAnalyzer").newInstance()).parse("Sugus");

            log.warn("DEBUG: Query: " + query);
            org.apache.lucene.search.Hits hits = searcher.search(query);
            for (int i = 0; i < hits.length(); i++) {
                log.warn("DEBUG: Hit _PATH: " + hits.doc(i).getField("_PATH").stringValue());
            }
        }
*/

        org.wyona.yarep.core.Node[] nodes = null;
        if (queryText != null) {
            //nodes = getRealm().getRepository().getSearcher().searchProperty("workflow-state", workflowState, "/meetings");
            //nodes = getRealm().getRepository().getSearcher().searchProperty("workflow-state", workflowState, "/");
            nodes = getRealm().getRepository().getSearcher().searchProperty("workflow-state", queryText, "/");
        }

        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\"?>");
        sb.append("<workflow-dashboard");
        if (queryText != null) {
            sb.append(" query=\"" + queryText + "\"");
        }
        if (workflowState != null) {
            sb.append(" workflow-state=\"" + workflowState + "\"");
        }
        if (mimeType != null) {
            sb.append(" mime-type=\"" + mimeType + "\"");
        }
        sb.append(">");

        String[] mimeTypes = getMimeTypes();
        if (mimeTypes != null) {
            sb.append("<mime-types>");
            for(int i = 0; i < mimeTypes.length; i++) {
                sb.append("<mime-type>" + mimeTypes[i] + "</mime-type>");
            }
            sb.append("</mime-types>");
        } else {
            sb.append("<no-mime-types/>");
        }

        Workflow workflow = getWorkflow();
        if (workflow != null) {
            sb.append("<workflow-states>");
            String[] states = workflow.getStates();
            for(int i = 0; i < states.length; i++) {
                sb.append("<state>" + states[i] + "</state>");
            }
            sb.append("</workflow-states>");
        } else {
            sb.append("<no-workflow-states/>");
        }

        if (nodes != null && nodes.length > 0) {
            for (int i = 0; i < nodes.length; i++) {
                sb.append("<node path=\"" + nodes[i].getPath() + "\">");
                if (nodes[i] instanceof org.wyona.yarep.core.Revision) { // INFO: This only makes sense if revisions are being indexed actually
                    sb.append("<revision>" + ((org.wyona.yarep.core.Revision) nodes[i]).getRevisionName() + "</revision>");
                } else {
                    sb.append("<no-revision/>");
                }
                sb.append("</node>");
            }
        } else {
            sb.append("<no-nodes-found/>");
        }
        sb.append("</workflow-dashboard>");
        return new ByteArrayInputStream(sb.toString().getBytes());
    }

    /**
     * Get one particular workflow
     */
    private Workflow getWorkflow() throws Exception {
        String workflowPath = getResourceConfigProperty("one-particular-workflow-path");
        if (workflowPath != null && getRealm().getRepository().existsNode(workflowPath)) {
            return new org.wyona.yanel.core.workflow.WorkflowBuilder().buildWorkflow(getRealm().getRepository().getNode(workflowPath).getInputStream());
        }
        return null;
    }

    /**
     * Get list of mime types
     */
    private String[] getMimeTypes() throws Exception {
        String mimeTypesCSV = getResourceConfigProperty("mime-types");
        if (mimeTypesCSV != null) {
            String[] mimeTypes = mimeTypesCSV.split(",");
            for (int i = 0; i < mimeTypes.length; i++) {
                mimeTypes[i] = mimeTypes[i].trim();
            }
            return mimeTypes;
        }
        return null;
    }
}
