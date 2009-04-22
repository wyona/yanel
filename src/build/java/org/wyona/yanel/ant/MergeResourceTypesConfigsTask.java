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

import org.wyona.commons.xml.XMLHelper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
                Document globalDoc = XMLHelper.readDocument(new java.io.FileInputStream(globalResourceTypesConfig));
                Document realmDoc = XMLHelper.readDocument(new java.io.FileInputStream(resourceTypesConfigOfRealm));

                Element rootElement = globalDoc.getDocumentElement();
                rootElement.appendChild(globalDoc.createComment("Realm specific resource-types (" + resourceTypesConfigOfRealm + "):")); // Only formatting

                String namespace = "http://www.wyona.org/yanel/1.0";
                Element[] resourceTypeElements = XMLHelper.getChildElements(realmDoc.getDocumentElement(), "resource-type", namespace);
                for (int i = 0; i < resourceTypeElements.length; i++) {
                    String srcAttr = resourceTypeElements[i].getAttribute("src");
                    if (srcAttr != null) {
                        srcAttr = srcAttr.replace("@REALM_SRC_DIR@", resourceTypesConfigOfRealm.getParent());
                    }

                    // TODO: Check for duplicated resource-types also based re package attribute!
                    if (!resourceTypeExists(srcAttr, rootElement)) {
                        rootElement.appendChild(globalDoc.createTextNode("\n  ")); // Only formatting
                        Element rtElement = globalDoc.createElementNS(namespace, "resource-type");
                        //Element rtElement = globalDoc.createElementNS(namespace, "todo");

                        if (srcAttr != null && !srcAttr.equals("")) {
                            rtElement.setAttribute("src", srcAttr);
                        }
                        String packageAttr = resourceTypeElements[i].getAttribute("package");
                        if (packageAttr != null && !packageAttr.equals("")) {
                            rtElement.setAttribute("package", packageAttr);
                        }
                        String compileAttr = resourceTypeElements[i].getAttribute("compile");
                        if (compileAttr != null && !compileAttr.equals("")) {
                            rtElement.setAttribute("compile", compileAttr);
                        }
                        String copyDirNameAttr = resourceTypeElements[i].getAttribute("copy-dir-name");
                        if (copyDirNameAttr != null && !copyDirNameAttr.equals("")) {
                            rtElement.setAttribute("copy-dir-name", copyDirNameAttr);
                        }
                        rootElement.appendChild(rtElement);
                    }
                }

                rootElement.appendChild(globalDoc.createTextNode("\n\n")); // Only formatting
                XMLHelper.writeDocument(globalDoc, new java.io.FileOutputStream(globalResourceTypesConfig));
            } catch(Exception e) {
                log.error(e, e);
            }
        } else {
            log("INFO: Realm has no specific resource-types configured.");
        }
    }

    /**
     *
     */
    private boolean resourceTypeExists(String src, Element rootElement) throws Exception {
        Element[] elements = XMLHelper.getElements(rootElement, "resource-type", "src", src);
        if (elements.length > 0) return true;
        return false;
    }
}
