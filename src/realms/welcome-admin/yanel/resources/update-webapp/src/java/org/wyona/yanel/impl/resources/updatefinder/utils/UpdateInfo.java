/*
 * Copyright 2007-2009 Wyona
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

package org.wyona.yanel.impl.resources.updatefinder.utils;

import org.apache.log4j.Logger;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import java.util.HashMap;

/**
 * 
 */
public class UpdateInfo {
    
    private static Logger log = Logger.getLogger(UpdateInfo.class);
    private List<Map<String, String>> updateVersions = new ArrayList<Map<String, String>>();
    private Model updateRdfModel;
//    private InstallInfo installInfo;
    
    private String updateManagerNS = "http://www.wyona.org/update-manager/1.0#"; 

    public static String TARGET_APPLICATION_MIN_REVISION = "targetApplicationMinRevision";
    public static String TARGET_APPLICATION_MAX_REVISION = "targetApplicationMaxRevision";
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
//        this.installInfo = installInfo;

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
//        Property targetApplicationIdProperty = new PropertyImpl(updateManagerNS, TARGET_APPLICATION_ID);
//        Property targetApplicationminVersionProperty = new PropertyImpl(updateManagerNS, "targetApplicationMinVersion");
//        Property targetApplicationmaxVersionProperty = new PropertyImpl(updateManagerNS, "targetApplicationMaxVersion");
        Property targetApplicationminRevisionProperty = new PropertyImpl(updateManagerNS, "targetApplicationMinRevision");
        Property targetApplicationmaxRevisionProperty = new PropertyImpl(updateManagerNS, "targetApplicationMaxRevision");
        Property changeLogProperty = new PropertyImpl(updateManagerNS, "changelog");
        Property titleProperty = new PropertyImpl(updateManagerNS, "title");
        Property updateLinkProperty = new PropertyImpl(updateManagerNS, "updateLink");
        
        while (iter2.hasNext()) {
            Resource versionResource = ((Resource) iter2.next());
            
            Map<String, String> updateVersionDetail = new HashMap<String, String>();
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
    public List<Map<String, String>> getUpdateVersions() {
        
        return updateVersions;
    }
    
    /**
     * @return map with version details which are matching the value of the key
     * @param String key
     * @param String value 
     */    
    public Map<String, String> getUpdateVersionDetail(String key, String value) {
        for (int i = 0; i < updateVersions.size(); i++) {
            Map<String, String> versionDetail = updateVersions.get(i);
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
    public List<Map<String, String>> getUpdateVersionsOf(String key, String value) {
        List<Map<String, String>> allUpdateVersions = getUpdateVersions();
        List<Map<String, String>> selectedUpdateVersions = new ArrayList<Map<String, String>>();
        if (allUpdateVersions == null) return null;
        for (int i = 0; i < allUpdateVersions.size(); i++) {
            Map<String, String> versionDetail = allUpdateVersions.get(i);
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
    public List<Map<String, String>> getUpdateVersionsOf(String key, String value, String InstallInfoRevision) {
        List<Map<String, String>> selectedUpdateVersions = getUpdateVersionsOf(key, value);
        if (selectedUpdateVersions == null) return null;
        VersionComparator versionComparator = new VersionComparator();  
        for (int i = 0; i < selectedUpdateVersions.size(); i++) {
            Map<String, String> versionDetail = selectedUpdateVersions.get(i);
            if (versionComparator.compare(versionDetail.get(TARGET_APPLICATION_MIN_REVISION), InstallInfoRevision) > 0 ) {
                selectedUpdateVersions.remove(i);
            }
            if (versionComparator.compare(versionDetail.get(TARGET_APPLICATION_MAX_REVISION), InstallInfoRevision) < 0 ) {
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
    public List<Map<String, String>> getUpdatersForYanelRevision(String yanelRevision) {
        VersionComparator versionComparator = new VersionComparator();
        List<Map<String, String>> bestUpdater = getUpdateVersionsOf("type", "updater");
        if (bestUpdater == null) return null;
        for (int i = 0; i < bestUpdater.size(); i++) {
            Map<String, String> versionDetail = bestUpdater.get(i);
            log.error("DEBUG: Updater MinRevision: " + versionDetail.get(TARGET_APPLICATION_MIN_REVISION));
            log.error("DEBUG: Updater MaxRevision: " +versionDetail.get(TARGET_APPLICATION_MAX_REVISION));
            if (versionComparator.compare( versionDetail.get(TARGET_APPLICATION_MIN_REVISION), yanelRevision) > 0 ) {
                bestUpdater.remove(i);
            }
            if (versionComparator.compare(versionDetail.get(TARGET_APPLICATION_MAX_REVISION), yanelRevision) < 0 ) {
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
    public List<Map<String, String>> getYanelUpdatesForYanelRevision(String yanelRevision) {
        // Get all updaters which work for a  specific yanel revision
        List<Map<String, String>> updaters = getUpdatersForYanelRevision(yanelRevision);
        if (updaters == null) return null;

        // Get all updates
        List<Map<String, String>> allUpdates = getUpdateVersionsOf("type", "updates");
        if (allUpdates == null) return null;

        // Match updates with updaters
        VersionComparator versionComparator = new VersionComparator();
        for (int i = 0; i < allUpdates.size(); i++) {
            Map<String, String> updatesVersionDetail = allUpdates.get(i);
            String revision = updatesVersionDetail.get("revision");
            log.error("DEBUG: Update revision: " + revision);
            
            
            for (int j = 0; j < updaters.size(); j++) {
                Map<String, String> updatersVersionDetail = updaters.get(j);
                log.error("DEBUG: Updater revision: " + updatersVersionDetail.get("revision"));
                if (versionComparator.compare(updatersVersionDetail.get(TARGET_APPLICATION_MIN_REVISION), revision) > 0 ) {
                    allUpdates.remove(i);
                }
                if (versionComparator.compare(updatersVersionDetail.get(TARGET_APPLICATION_MAX_REVISION), revision) < 0 ) {
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
