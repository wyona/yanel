/*
 * Merge global resource-types config with realm specific resource-types configs
 */
package org.wyona.yanel.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

import java.io.File;

import org.wyona.yanel.core.ResourceTypeDefinition;
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
    private boolean isBinaryRelease;

    private String NAMESPACE = "http://www.wyona.org/yanel/1.0";

    /**
     *
     */
    public void execute() throws BuildException {

        log("INFO: Realms config file: " + realmsConfigFile);
        File realmsConfig = new File(realmsConfigFile.toString());

        log("INFO: Global resource-types config directory: " + globalResourceTypesConfigFile);
        File globalResourceTypesConfig = new File(globalResourceTypesConfigFile.toString());

        if (isBinaryRelease) {
            insertPackageAttribute(globalResourceTypesConfig);
        }

        RealmManagerConfig realmManagerConfig = new RealmManagerConfig();
        try {
            RealmContextConfig[] realmContextConfigs;
            if (realmsConfig.isFile()) {
                realmContextConfigs = realmManagerConfig.getRealmContextConfigs(realmsConfig);

                log.info("Merge ...");
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
     * Ant file task attribute realmsconfigfile
     */
    public void setRealmsConfigFile(Path realmsConfigFile) {
        this.realmsConfigFile = realmsConfigFile;
    }

    /**
     * Ant file task attribute globalresourcetypesconfigfile
     */
    public void setGlobalResourceTypesConfigFile(Path globalResourceTypesConfigFile) {
        this.globalResourceTypesConfigFile = globalResourceTypesConfigFile;
    }

    /**
     * Ant file task attribute isbinaryrelease
     */
    public void setIsBinaryRelease(boolean isBinaryRelease) {
        this.isBinaryRelease = isBinaryRelease;
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

                Element[] resourceTypeElements = XMLHelper.getChildElements(realmDoc.getDocumentElement(), "resource-type", NAMESPACE);
                for (int i = 0; i < resourceTypeElements.length; i++) {
                    String srcAttr = resourceTypeElements[i].getAttribute("src");
                    if (srcAttr != null) {
                        srcAttr = srcAttr.replace("@REALM_SRC_DIR@", resourceTypesConfigOfRealm.getParent().replace(File.separator, "/")); // NOTE: Enforce forward slashes in the case of Windows!
                    }

                    // TODO: Check for duplicated resource-types also based re package attribute!
                    if (!resourceTypeExists(srcAttr, rootElement)) {
                        rootElement.appendChild(globalDoc.createTextNode("\n  ")); // Only formatting
                        Element rtElement = globalDoc.createElementNS(NAMESPACE, "resource-type");
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
                            log("WARN: copy-dir-name attribute is deprecated (Resource '" + srcAttr + "')!");
                            log.warn("copy-dir-name attribute is deprecated!");
                        }
                        if (isBinaryRelease) {
                            //log("DEBUG: This is a binary release! (Resource: " + srcAttr + ")");
                            //log.warn("DEBUG: This is a binary release!");
                            if (copyDirNameAttr.equals("") && packageAttr.equals("")) {
                                log("INFO: Insert the package name of the resource '" + srcAttr + "' automatically.");
                                rtElement.setAttribute("package", getJavaPackageOfResourceType(srcAttr));
                            }
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

    /**
     * @param resourceHomePath Source directory of resource type
     */
    private String getJavaPackageOfResourceType(String resourceHomePath) throws Exception {
        String classname = new ResourceTypeDefinition(new File(resourceHomePath, "resource.xml")).getResourceTypeClassname();
        //return Class.forName(classname).getPackage().getName(); // NOTE: This doesn't work, because java classloader check
        return classname.substring(0, classname.lastIndexOf(".")); // NOTE: This doesn't work in the case of ...
    }

    /**
     * Insert package attributes automatically if binary release and no copy-dir-name attribute exists
     * @param globalResourceTypesConfig Copied resource types configuration file (see build/classes/resource-types.xml)
     */
    private void insertPackageAttribute(File globalResourceTypesConfig) {
        log.warn("DEBUG: Patch file: " + globalResourceTypesConfig);
        try {
            Document doc = XMLHelper.readDocument(new java.io.FileInputStream(globalResourceTypesConfig));
            Element[] resourceTypeElements = XMLHelper.getChildElements(doc.getDocumentElement(), "resource-type", NAMESPACE);
            for (int i = 0; i < resourceTypeElements.length; i++) {
                if (!resourceTypeElements[i].hasAttribute("copy-dir-name")) {
                    if (!resourceTypeElements[i].hasAttribute("package")) {
                        String srcAttr = resourceTypeElements[i].getAttribute("src");
                        log.warn("DEBUG: Set package automatically for resource type: " + srcAttr);
                        resourceTypeElements[i].setAttribute("package", getJavaPackageOfResourceType(srcAttr));
                    }
                }
            }
            log.warn("DEBUG: Overwrite file: " + globalResourceTypesConfig);
            XMLHelper.writeDocument(doc, new java.io.FileOutputStream(globalResourceTypesConfig));
        } catch(Exception e) {
            log.error(e, e);
        }
    }
}
