/*
 * Merge global resource-types config with realm specific resource-types configs
 */
package org.wyona.yanel.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

import java.io.File;

import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.core.map.RealmContextConfig;
import org.wyona.yanel.core.map.RealmManagerConfig;

import org.apache.log4j.Logger;

/**
 * Merge resource-types.xml config files of the various realms and core
 */
public class MergeResourceTypesConfigsTask extends Task {

    private static Logger log = Logger.getLogger(MergeResourceTypesConfigsTask.class);

    private Path realmsConfigFile;
    private Path globalResourceTypesConfigFile;

    /**
     *
     */
    public void execute() throws BuildException {
        log.info("Merge ...");
        log("INFO: Realms config file: " + realmsConfigFile);
        log("INFO: Global resource-types config directory: " + globalResourceTypesConfigFile);
        File realmsConfig = new File(realmsConfigFile.toString());
        File globalResourceTypesConfig = new File(globalResourceTypesConfigFile.toString());
        RealmManagerConfig realmManagerConfig = new RealmManagerConfig();
        try {
            RealmContextConfig[] realmContextConfigs;
            if (realmsConfig.isFile()) {
                realmContextConfigs = realmManagerConfig.getRealmContextConfigs(realmsConfig);

                log("Number of realms: " + realmContextConfigs.length);
                for (int i = 0; i < realmContextConfigs.length; i++) {
                    log("Realm context config: " + realmContextConfigs[i]);
                    mergeResourceTypesOfRealm(realmContextConfigs[i].getUnresolvedConfigurationFile(), globalResourceTypesConfig);
                }
            } else {
                log("ERROR: No such realms config '" + realmsConfig.getAbsolutePath() + "' exists!");
            }
        } catch (Exception e) {
            log("ERROR: " + e.getMessage());
            throw new BuildException(e.getMessage(), e);
        }
    }

    /**
     *
     */
    public void setRealmsConfigFile(Path realmsConfigFile) {
        this.realmsConfigFile = realmsConfigFile;
    }

    /**
     *
     */
    public void setGlobalResourceTypesConfigFile(Path globalResourceTypesConfigFile) {
        this.globalResourceTypesConfigFile = globalResourceTypesConfigFile;
    }

    /**
     *
     */
    private void mergeResourceTypesOfRealm(File unresolvedRealmConfig, File globalResourceTypesConfig) {
        File realmDir;
        if (unresolvedRealmConfig.isDirectory()) {
            realmDir = unresolvedRealmConfig;
        } else if (unresolvedRealmConfig.isFile()) {
            realmDir = new File(unresolvedRealmConfig.getParent());
        } else {
            log.error("Neither file nor directory: " + unresolvedRealmConfig);
            return;
        }
        log("INFO: Realm directory: " + realmDir);
        File resourceTypesConfigOfRealm = new File(realmDir, "resource-types.xml");
        if (resourceTypesConfigOfRealm.isFile()) {
            log("INFO: Realm has specific resource-types configured: " + resourceTypesConfigOfRealm);
            try {
                org.w3c.dom.Document globalDoc = org.wyona.commons.xml.XMLHelper.readDocument(new java.io.FileInputStream(globalResourceTypesConfig));
                org.w3c.dom.Document realmDoc = org.wyona.commons.xml.XMLHelper.readDocument(new java.io.FileInputStream(resourceTypesConfigOfRealm));

                org.w3c.dom.Element rootElement = globalDoc.getDocumentElement();
                rootElement.appendChild(globalDoc.createComment("Realm specific resource-types (" + resourceTypesConfigOfRealm + "):")); // Only formatting
                rootElement.appendChild(globalDoc.createTextNode("\n")); // Only formatting

                // TODO: Check for duplicated resource-types!
                rootElement.appendChild(globalDoc.createElement("todo"));
                //rootElement.appendChild(globalDoc.createElementNS(namespace, ""));

                rootElement.appendChild(globalDoc.createTextNode("\n")); // Only formatting
                org.wyona.commons.xml.XMLHelper.writeDocument(globalDoc, new java.io.FileOutputStream(globalResourceTypesConfig));
            } catch(Exception e) {
                log.error(e, e);
            }
        } else {
            log("INFO: Realm has no specific resource-types configured.");
        }
    }
}
