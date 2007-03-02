/*
 * Copyright 2006 Wyona
 */

package org.wyona.yanel.impl.resources;

import org.apache.log4j.Category;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
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
            throw new Exception("InputStraeam should not be null");
        }
        Model model = ModelFactory.createDefaultModel();
        //read the RDF/XML file
        model.read(in, "");
        this.updateRdfModel = model;
        this.installInfo = installInfo;

        setUpdateVersions();
    }

    private void setUpdateVersions() {
        Resource update = updateRdfModel.getResource("urn:wyona:application:updates");
        
        Property versionsProperty = new PropertyImpl(updateManagerNS, "versions");
        Seq versions = update.getProperty(versionsProperty).getSeq();
        
        NodeIterator iter2 = versions.iterator();
        Property versionProperty = new PropertyImpl(updateManagerNS, "version");
        Property idProperty = new PropertyImpl(updateManagerNS, "id");
        Property targetApplicationProperty = new PropertyImpl(updateManagerNS, "targetApplication");
        Property minVersionProperty = new PropertyImpl(updateManagerNS, "minVersion");
        Property maxVersionProperty = new PropertyImpl(updateManagerNS, "maxVersion");
        Property changeLogProperty = new PropertyImpl(updateManagerNS, "changelog");
        Property updateLinkProperty = new PropertyImpl(updateManagerNS, "updateLink");
        
        while (iter2.hasNext()) {
            Resource versionResource = ((Resource) iter2.next());
            
            //check id
            if (versionResource.getProperty(idProperty).getString().equals(installInfo.getId())) {
                //check for targetApplicationId
                Seq targetApplicationSeq = versionResource.getProperty(targetApplicationProperty).getSeq();
                NodeIterator targetApplicationIter = targetApplicationSeq.iterator();
                while (targetApplicationIter.hasNext()) {
                    Resource targetApplicationResource = ((Resource) targetApplicationIter.next());
                    String test1 = targetApplicationResource.getProperty(idProperty).getString();
                    String test2 = installInfo.getTargetApplicationId();
                    System.out.println("taid: "+test1 +" install: "+ test2);
                    
                    if (targetApplicationResource.getProperty(idProperty).getString().equals(installInfo.getTargetApplicationId())) {

                        //check for minorVersion
                        String minVersion = targetApplicationResource.getProperty(minVersionProperty).getString();
                        String installVersion = installInfo.getTargetApplicationVersion();
                        VersionComparator versionComparator = new VersionComparator(); 
                        if (versionComparator.compare(installVersion, minVersion) >= 0) {
                            //check for maxVersion
                            String maxVersion = targetApplicationResource.getProperty(maxVersionProperty).getString();
                            if (versionComparator.compare(maxVersion, installVersion) >= 0) {
                                HashMap updateVersions = new HashMap();
                                updateVersions.put("version", versionResource.getProperty(versionProperty).getString());
                                updateVersions.put("changeLog", versionResource.getProperty(changeLogProperty).getString());
                                updateVersions.put("updateLink", targetApplicationResource.getProperty(updateLinkProperty).getString());
                                this.updateVersions.add(updateVersions);
                            }
                        }
                    }
                }
            }
        }
    }

    public ArrayList getUpdateVersions() {
        return updateVersions;
    }
}
