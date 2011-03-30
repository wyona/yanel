/*
 * Adding a new (third party) realm to a Yanel instance
 */
package org.wyona.yanel.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wyona.commons.xml.XMLHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.w3c.dom.Document;

/**
 * Add a third party realm to the local realm configuration
 */
public class AddRealmTask extends Task {

	private Path newRealmConfig;
    private String newRealmMountPoint;
    private String newRealmId;
    private Path defaultRealmsXml;
    private Path localRealmsXml;
    
    public void execute() throws BuildException {
    	log("New (third party) realm configuration: " + newRealmConfig);
        log("New (third party) realm mount point: " + newRealmMountPoint);
        log("New (third party) realm id: " + newRealmId);
        
        log("defaultRealmsXml: " + defaultRealmsXml);
        log("localRealmsXml: " + localRealmsXml);
        
        if (!new File(localRealmsXml.toString()).exists()) {
        	try {
        		FileUtils.copyFile(new File(defaultRealmsXml.toString()), new File(localRealmsXml.toString()));
        	} catch (IOException e) {
        		log("Exception: " + e.getMessage());
        	}
        }
        
        try {
        	Document doc = XMLHelper.readDocument(new FileInputStream(new File(localRealmsXml.toString())));
        	Element rootElement = doc.getDocumentElement();
        	// TODO check if realm with this id already exists
              	
        	Element realm = doc.createElementNS(rootElement.getNamespaceURI(), "realm");
        	realm.setAttribute("id", newRealmId);
        	realm.setAttribute("mount-point", newRealmMountPoint);
        	realm.appendChild(doc.createTextNode("\n  ")); // formatting
        	
        	Element config = doc.createElementNS(rootElement.getNamespaceURI(), "config");
        	String configSrcAttribute = newRealmConfig.toString();
        	if (!configSrcAttribute.endsWith("/")) {
        		configSrcAttribute += "/";
        	}
        	config.setAttribute("src", configSrcAttribute);
        	      	
        	realm.appendChild(config);
        	realm.appendChild(doc.createTextNode("\n")); // formatting
        	rootElement.appendChild(doc.createComment("\nAnt Generated Entry Using:\n./build.sh add-realm -Drealm-config=" + newRealmConfig + " -Drealm-mount-point=" + newRealmMountPoint + " -Drealm-id=" + newRealmId + "\n"));
        	rootElement.appendChild(doc.createTextNode("\n")); // formatting
        	rootElement.appendChild(realm);
        	rootElement.appendChild(doc.createTextNode("\n\n")); // formatting

        	log("Write new file: " + localRealmsXml);
        	XMLHelper.writeDocument(doc, new FileOutputStream(localRealmsXml.toString()));
        	        	
        } catch (Exception e) {
        	log("Exception: " + e.getMessage());
        }

    }

    /**
     * Ant file task attribute new (third party) realm config
     */
    public void setNewRealmConfig(Path newRealmConfig) {
        this.newRealmConfig = newRealmConfig;
    }
    
    /**
     * Ant file task attribute new (third party) realm mount point
     */
    public void setNewRealmMountPoint(String newRealmMountPoint) {
        this.newRealmMountPoint = newRealmMountPoint;
    }
    
    /**
     * Ant file task attribute new (third party) realm id
     */
    public void setNewRealmId(String newRealmId) {
        this.newRealmId = newRealmId;
    }
    
    /**
     * Ant file task attribute default realms.xml path
     */
    public void setDefaultRealmsXml(Path defaultRealmsXml) {
        this.defaultRealmsXml = defaultRealmsXml;
    }
    
    /**
     * Ant file task attribute local realms.xml path
     */
    public void setLocalRealmsXml(Path localRealmsXml) {
        this.localRealmsXml = localRealmsXml;
    }
  
}
