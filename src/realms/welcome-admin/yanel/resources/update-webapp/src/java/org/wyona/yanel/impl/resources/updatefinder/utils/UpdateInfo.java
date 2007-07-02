/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources.updatefinder.utils;

import org.apache.log4j.Category;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl;
import java.util.HashMap;

/**
 * 
 */
public class UpdateInfo {
    
    private static Category log = Category.getInstance(UpdateInfo.class);
    private ArrayList updateVersions = new ArrayList();
    private Model updateRdfModel;
    private InstallInfo installInfo;
    
    private String updateManagerNS = "http://www.wyona.org/update-manager/1.0#"; 
    
    public UpdateInfo(InputStream in, InstallInfo installInfo) throws Exception{
        if (installInfo == null) {
            throw new Exception("InstallInfo should not be null");
        }
        if (in == null) {
            throw new Exception("InputStream should not be null");
        }
        Model model = ModelFactory.createDefaultModel();
        //read the RDF/XML file
        model.read(in, "");
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
        Property targetApplicationIdProperty = new PropertyImpl(updateManagerNS, "targetApplicationId");
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
            updateVersionDetail.put("targetApllicationId", versionResource.getProperty(idProperty).getString());
            updateVersionDetail.put("targetApllicationMinVersion", versionResource.getProperty(targetApplicationminVersionProperty).getString());
            updateVersionDetail.put("targetApllicationMaxVersion", versionResource.getProperty(targetApplicationmaxVersionProperty).getString());
            updateVersionDetail.put("targetApllicationMinRevision", versionResource.getProperty(targetApplicationminRevisionProperty).getString());
            updateVersionDetail.put("targetApllicationMaxRevision", versionResource.getProperty(targetApplicationmaxRevisionProperty).getString());
            this.updateVersions.add(updateVersionDetail);
            
            
            
            //check id
            //if (versionResource.getProperty(idProperty).getString().equals(installInfo.getId())) {
                //check for targetApplicationId
//                Seq targetApplicationSeq = versionResource.getProperty(targetApplicationProperty).getSeq();
//                NodeIterator targetApplicationIter = targetApplicationSeq.iterator();
//                while (targetApplicationIter.hasNext()) {
//                    Resource targetApplicationResource = ((Resource) targetApplicationIter.next());
//                    String test1 = targetApplicationResource.getProperty(idProperty).getString();
//                    String test2 = installInfo.getTargetApplicationId();
//                    System.out.println("taid: "+test1 +" install: "+ test2);
//                    
//                    //if (targetApplicationResource.getProperty(idProperty).getString().equals(installInfo.getTargetApplicationId())) {
//
//                        //check for minorVersion
//                        //String minVersion = targetApplicationResource.getProperty(minVersionProperty).getString();
//                        //String installVersion = installInfo.getTargetApplicationVersion();
//                        //VersionComparator versionComparator = new VersionComparator(); 
//                        //if (versionComparator.compare(installVersion, minVersion) >= 0) {
//                            //check for maxVersion
//                            //String maxVersion = targetApplicationResource.getProperty(maxVersionProperty).getString();
//                            //if (versionComparator.compare(maxVersion, installVersion) >= 0) {
//                                HashMap updateVersionDetail = new HashMap();
//                                updateVersionDetail.put("type", typeResource.getProperty(typeProperty).getString());
//                                updateVersionDetail.put("title", versionResource.getProperty(titleProperty).getString());
//                                updateVersionDetail.put("id", versionResource.getProperty(idProperty).getString());
//                                updateVersionDetail.put("version", versionResource.getProperty(versionProperty).getString());
//                                updateVersionDetail.put("revision", versionResource.getProperty(revisionProperty).getString());
//                                updateVersionDetail.put("changeLog", versionResource.getProperty(changeLogProperty).getString());
//                                updateVersionDetail.put("updateLink", targetApplicationResource.getProperty(updateLinkProperty).getString());
//                                updateVersionDetail.put("targetApllication", targetApplicationResource.getProperty(idProperty).getString());
//                                updateVersionDetail.put("targetApllicationMinVersion", targetApplicationResource.getProperty(minVersionProperty).getString());
//                                updateVersionDetail.put("targetApllicationMaxVersion", targetApplicationResource.getProperty(maxVersionProperty).getString());
//                                this.updateVersions.add(updateVersionDetail);
//                            //}
//                        //}
//                    //}
//                //}
//                }
            }
        }
    }

    public ArrayList getUpdateVersions() {
        return updateVersions;
    }
    
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
        ArrayList selectedUpdateVersions = updateVersions;
        for (int i = 0; i < selectedUpdateVersions.size(); i++) {
            HashMap versionDetail = (HashMap) selectedUpdateVersions.get(i);
            if (!versionDetail.get(key).equals(value)) {
                selectedUpdateVersions.remove(i);
            }
        }
        Collections.sort(selectedUpdateVersions, new UpdateInfoVersionComparator());
        return selectedUpdateVersions;
    }

    /**
     * @return ArrayList with version which are matching the value of the key, and fits in the version requirement
     * @param String key
     * @param String value 
     */
    public ArrayList getUpdateVersionsOf(String key, String value, String installInfoVersion) {
        ArrayList selectedUpdateVersions = getUpdateVersionsOf(key, value);
        VersionComparator versionComparator = new VersionComparator();  
        for (int i = 0; i < selectedUpdateVersions.size(); i++) {
            HashMap versionDetail = (HashMap) selectedUpdateVersions.get(i);
            if (versionComparator.compare((String) versionDetail.get("targetApllicationMinVersion"), installInfoVersion) > 0 ) {
                selectedUpdateVersions.remove(i);
            }
            if (versionComparator.compare((String) versionDetail.get("targetApllicationMaxVersion"), installInfoVersion) < 0 ) {
                selectedUpdateVersions.remove(i);
            }
        }
        Collections.sort(selectedUpdateVersions, new UpdateInfoVersionComparator());
        return selectedUpdateVersions;
    }
    
    /**
     * @return HashMap with the newest version which are matching the value of the key
     * @param String key
     * @param String value 
     */
    public HashMap getNewestUpdateVersionsOf(String key, String value) {
        ArrayList selectedUpdateVersions = getUpdateVersionsOf(key, value);
        for (int i = 0; i < selectedUpdateVersions.size(); i++) {
            HashMap versionDetail = (HashMap) selectedUpdateVersions.get(i);
            if (!versionDetail.get(key).equals(value)) {
                selectedUpdateVersions.remove(i);
            }
        }
        Collections.sort(selectedUpdateVersions, new UpdateInfoVersionComparator());
        return (HashMap) selectedUpdateVersions.get(0);
    }    

    /**
     * @return HashMap with the newest version which are matching the value of the key
     * @param String key
     * @param String value 
     */
    public HashMap getNewestUpdateVersionsOf(String key, String value, String installInfoVersion) {
        ArrayList selectedUpdateVersions = getUpdateVersionsOf(key, value);
        VersionComparator versionComparator = new VersionComparator();  
        for (int i = 0; i < selectedUpdateVersions.size(); i++) {
            HashMap versionDetail = (HashMap) selectedUpdateVersions.get(i);
            if (versionComparator.compare((String) versionDetail.get("targetApllicationMinVersion"), installInfoVersion) > 0 ) {
                selectedUpdateVersions.remove(i);
            }
            if (versionComparator.compare((String) versionDetail.get("targetApllicationMaxVersion"), installInfoVersion) < 0 ) {
                selectedUpdateVersions.remove(i);
            }
        }
        Collections.sort(selectedUpdateVersions, new UpdateInfoVersionComparator());
        return (HashMap) selectedUpdateVersions.get(0);
    }    
    
    
}
