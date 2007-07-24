/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources.updatefinder.utils;

import org.apache.log4j.Category;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 */
public class UpdateInfo {
    
    private static Category log = Category.getInstance(UpdateInfo.class);
    private ArrayList updateVersions = new ArrayList();
    private Model updateRdfModel;
    private InstallInfo installInfo;
    
    private String updateManagerNS = "http://www.wyona.org/update-manager/1.0#"; 

    private static String TARGET_APPLICATION_MIN_REVISION = "targetApplicationMinRevision";
    private static String TARGET_APPLICATION_MAX_REVISION = "targetApplicationMaxRevision";
    private static String TARGET_APPLICATION_ID = "targetApplicationId";
    
    public UpdateInfo(String updateRdfUrlString, InstallInfo installInfo) throws Exception{
        if (installInfo == null) {
            throw new Exception("InstallInfo should not be null");
        }
        if (updateRdfUrlString == null) {
            throw new Exception("InputStream should not be null");
        }
        
        URL updateRdfUrl = new URL(updateRdfUrlString);
        InputStream updateRdfIn = null;
        try {
            updateRdfIn = updateRdfUrl.openStream();
        } catch (Exception e) {
            throw new Exception("Could not get update information from: " + updateRdfUrlString);
        }
        
        Model model = ModelFactory.createDefaultModel();
        //read the RDF/XML file
        model.read(updateRdfIn, "");
        this.updateRdfModel = model;
        this.installInfo = installInfo;

        setUpdateVersions();
    }

    private void setUpdateVersions() {
        Resource type = updateRdfModel.getResource("urn:wyona:application");
        Property typeProperty = new PropertyImpl(updateManagerNS, "type");
        Property typesProperty = new PropertyImpl(updateManagerNS, "types");
        Property versionsProperty = new PropertyImpl(updateManagerNS, "versions");
        Seq types = type.getProperty(typesProperty).getSeq();
        NodeIterator typeIter = types.iterator();
        
        while (typeIter.hasNext()) {
            Resource typeResource = ((Resource) typeIter.next());
        
        //Resource update = updateRdfModel.getResource("urn:wyona:application:updates");
        
        Property versionProperty = new PropertyImpl(updateManagerNS, "version");
        Property revisionProperty = new PropertyImpl(updateManagerNS, "revision");
        //Seq versions = update.getProperty(versionsProperty).getSeq();
        Seq versionsSeq = typeResource.getProperty(versionsProperty).getSeq();
        
        NodeIterator iter2 = versionsSeq.iterator();
        Property idProperty = new PropertyImpl(updateManagerNS, "id");
        Property targetApplicationIdProperty = new PropertyImpl(updateManagerNS, TARGET_APPLICATION_ID);
        Property targetApplicationminVersionProperty = new PropertyImpl(updateManagerNS, "targetApplicationMinVersion");
        Property targetApplicationmaxVersionProperty = new PropertyImpl(updateManagerNS, "targetApplicationMaxVersion");
        Property targetApplicationminRevisionProperty = new PropertyImpl(updateManagerNS, "targetApplicationMinRevision");
        Property targetApplicationmaxRevisionProperty = new PropertyImpl(updateManagerNS, "targetApplicationMaxRevision");
        Property changeLogProperty = new PropertyImpl(updateManagerNS, "changelog");
        Property titleProperty = new PropertyImpl(updateManagerNS, "title");
        Property updateLinkProperty = new PropertyImpl(updateManagerNS, "updateLink");
        
        while (iter2.hasNext()) {
            Resource versionResource = ((Resource) iter2.next());
            
            HashMap updateVersionDetail = new HashMap();
            updateVersionDetail.put("type", typeResource.getProperty(typeProperty).getString());
            updateVersionDetail.put("title", versionResource.getProperty(titleProperty).getString());
            updateVersionDetail.put("id", versionResource.getProperty(idProperty).getString());
            updateVersionDetail.put("version", versionResource.getProperty(versionProperty).getString());
            updateVersionDetail.put("revision", versionResource.getProperty(revisionProperty).getString());
            updateVersionDetail.put("changeLog", versionResource.getProperty(changeLogProperty).getString());
            updateVersionDetail.put("updateLink", versionResource.getProperty(updateLinkProperty).getString());
            updateVersionDetail.put(TARGET_APPLICATION_ID, versionResource.getProperty(idProperty).getString());
            updateVersionDetail.put(TARGET_APPLICATION_MIN_REVISION, versionResource.getProperty(targetApplicationminRevisionProperty).getString());
            updateVersionDetail.put(TARGET_APPLICATION_MAX_REVISION, versionResource.getProperty(targetApplicationmaxRevisionProperty).getString());
            this.updateVersions.add(updateVersionDetail);
            }
        }
    }
    
    /**
     * @return ArrayList with all update version
     */
    public ArrayList getUpdateVersions() {
        
        return updateVersions;
    }
    
    /**
     * @return HashMap with version details which are matching the value of the key
     * @param String key
     * @param String value 
     */    
    public HashMap getUpdateVersionDetail(String key, String value) {
        for (int i = 0; i < updateVersions.size(); i++) {
            HashMap versionDetail = (HashMap) updateVersions.get(i);
            if (versionDetail.get(key).equals(value)) {
                return versionDetail;
            }
        }
        return null;
    }    


    /**
     * @return ArrayList with version which are matching the value of the key
     * @param String key
     * @param String value 
     */
    public ArrayList getUpdateVersionsOf(String key, String value) {
        ArrayList allUpdateVersions = getUpdateVersions();
        ArrayList selectedUpdateVersions = new ArrayList();
        if (allUpdateVersions == null) return null;
        for (int i = 0; i < allUpdateVersions.size(); i++) {
            HashMap versionDetail = (HashMap) allUpdateVersions.get(i);
            if (versionDetail.get(key).equals(value)) {
                selectedUpdateVersions.add(allUpdateVersions.get(i));
            }
        }
        if (selectedUpdateVersions.size() < 1) return null;
        Collections.sort(selectedUpdateVersions, new UpdateInfoVersionComparator());
        return selectedUpdateVersions;
    }

    /**
     * @return ArrayList with version which are matching the value of the key, and fits in the revision requirement. Or null if none.
     * @param String key
     * @param String value 
     * @param String installInfoRevision 
     */
    public ArrayList getUpdateVersionsOf(String key, String value, String InstallInfoRevision) {
        ArrayList selectedUpdateVersions = getUpdateVersionsOf(key, value);
        if (selectedUpdateVersions == null) return null;
        VersionComparator versionComparator = new VersionComparator();  
        for (int i = 0; i < selectedUpdateVersions.size(); i++) {
            HashMap versionDetail = (HashMap) selectedUpdateVersions.get(i);
            if (versionComparator.compare((String) versionDetail.get(TARGET_APPLICATION_MIN_REVISION), InstallInfoRevision) > 0 ) {
                selectedUpdateVersions.remove(i);
            }
            if (versionComparator.compare((String) versionDetail.get(TARGET_APPLICATION_MAX_REVISION), InstallInfoRevision) < 0 ) {
                selectedUpdateVersions.remove(i);
            }
        }
        if (selectedUpdateVersions.size() < 1) return null;           
        Collections.sort(selectedUpdateVersions, new UpdateInfoVersionComparator());
        return selectedUpdateVersions;
    }
    
    /**
     * @return HashMap with the newest version which are matching the value of the key
     * @param String key
     * @param String value 
     */
/*    public HashMap getNewestUpdateVersionsOf(String key, String value) {
        ArrayList selectedUpdateVersions = getUpdateVersionsOf(key, value);
        for (int i = 0; i < selectedUpdateVersions.size(); i++) {
            HashMap versionDetail = (HashMap) selectedUpdateVersions.get(i);
            if (!versionDetail.get(key).equals(value)) {
                selectedUpdateVersions.remove(i);
            }
        }
        if (selectedUpdateVersions.size() < 1) {
            throw new Exception("There are no newest update for key: " + key + ", value: " + value + ".");
        }             
        Collections.sort(selectedUpdateVersions, new UpdateInfoVersionComparator());
        return (HashMap) selectedUpdateVersions.get(selectedUpdateVersions.size() - 1);
    }   */ 

    /**
     * @return ArrayList with all updaters which are installable within the given YanelRevision. return null if non.
     * @param String YanelRevision
     */
    public ArrayList getUpdatersForYanelRevision(String yanelRevision) {
        VersionComparator versionComparator = new VersionComparator();
        ArrayList bestUpdater = getUpdateVersionsOf("type", "updater");
        if (bestUpdater == null) return null;
        for (int i = 0; i < bestUpdater.size(); i++) {
            HashMap versionDetail = (HashMap) bestUpdater.get(i);
            log.error("DEBUG: Updater MinRevision: " + (String) versionDetail.get(TARGET_APPLICATION_MIN_REVISION));
            log.error("DEBUG: Updater MaxRevision: " + (String) versionDetail.get(TARGET_APPLICATION_MAX_REVISION));
            if (versionComparator.compare((String) versionDetail.get(TARGET_APPLICATION_MIN_REVISION), yanelRevision) > 0 ) {
                bestUpdater.remove(i);
            }
            if (versionComparator.compare((String) versionDetail.get(TARGET_APPLICATION_MAX_REVISION), yanelRevision) < 0 ) {
                bestUpdater.remove(i);
            }
        }
        Collections.sort(bestUpdater, new UpdateInfoVersionComparator());
        if (bestUpdater.size() < 1)  return null;
        return bestUpdater;
    }

    /**
     * @return ArrayList with all yanelUpdates which are installable within the given YanelRevision. return null if non.
     * @param String YanelRevision
     */
    public ArrayList getYanelUpatesForYanelRevision(String yanelRevision) {
        // Get all updaters which work for a  specific yanel revision
        ArrayList updaters = getUpdatersForYanelRevision(yanelRevision);
        if (updaters == null) return null;

        // Get all updates
        ArrayList allUpdates = getUpdateVersionsOf("type", "updates");
        if (allUpdates == null) return null;

        // Match updates with updaters
        VersionComparator versionComparator = new VersionComparator();
        for (int i = 0; i < allUpdates.size(); i++) {
            HashMap updatesVersionDetail = (HashMap) allUpdates.get(i);
            String revision = (String) updatesVersionDetail.get("revision");
            log.error("DEBUG: Update revision: " + revision);
            
            
            for (int j = 0; j < updaters.size(); j++) {
                HashMap updatersVersionDetail = (HashMap) updaters.get(j);
                log.error("DEBUG: Updater revision: " + (String) updatersVersionDetail.get("revision"));
                if (versionComparator.compare((String) updatersVersionDetail.get(TARGET_APPLICATION_MIN_REVISION), revision) > 0 ) {
                    allUpdates.remove(i);
                }
                if (versionComparator.compare((String) updatersVersionDetail.get(TARGET_APPLICATION_MAX_REVISION), revision) < 0 ) {
                    allUpdates.remove(i);
                }
            }
        }
        
        Collections.sort(allUpdates, new UpdateInfoVersionComparator());
        if (allUpdates.size() < 1) {
            return null;
        }
        return allUpdates;
    }
    
    /**
     * @return ArrayList with the versions which are matching the value of the key, and fits in the revision requirement and is not allready installed. returns null if now version fits.
     * @param String key
     * @param String value 
     * @param String installInfoRevision 
     */
/*    public ArrayList getUpdateVersionsOf(String key, String value, String installInfoRevision, HttpServletRequest request) throws Exception{
        ArrayList selectedUpdateVersions = getUpdateVersionsOf(key, value);
        VersionComparator versionComparator = new VersionComparator();
        TomcatContextHandler tomcatContextHandler = new TomcatContextHandler(request);
        for (int i = 0; i < selectedUpdateVersions.size(); i++) {
            HashMap versionDetail = (HashMap) selectedUpdateVersions.get(i);
            if (versionComparator.compare((String) versionDetail.get(TARGET_APPLICATION_MIN_REVISION), installInfoRevision) > 0 ) {
                selectedUpdateVersions.remove(i);
            }
            if (versionComparator.compare((String) versionDetail.get(TARGET_APPLICATION_MAX_REVISION), installInfoRevision) < 0 ) {
                selectedUpdateVersions.remove(i);
            }
            for (int j = 0; j < tomcatContextHandler.getWebappNames().length; j++) {
                if (tomcatContextHandler.getWebappNames()[j].equals(versionDetail.get("id") + "-v-" + versionDetail.get("version") + "-r-" + versionDetail.get("revision"))) {
                    selectedUpdateVersions.remove(i);
                }
            }
        }
        if (selectedUpdateVersions.size() < 1) {
            return null;
        }                    
        Collections.sort(selectedUpdateVersions, new UpdateInfoVersionComparator());
        return selectedUpdateVersions;
    }    */
    
}
