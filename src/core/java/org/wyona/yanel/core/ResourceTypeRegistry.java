/*
 * Copyright 2007 Wyona
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

package org.wyona.yanel.core;

import java.io.File;
import java.lang.ClassNotFoundException;
import java.lang.IllegalAccessException;
import java.lang.InstantiationException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Properties;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFilter;
import org.apache.log4j.Category;

import org.wyona.commons.io.FileUtil;
import org.wyona.yanel.core.map.RealmManager;

/**
 *
 */
public class ResourceTypeRegistry {

    private static Category log = Category.getInstance(ResourceTypeRegistry.class);

    public static String CONFIGURATION_FILE = Yanel.DEFAULT_CONFIGURATION_FILE;

    public static String RESOURCE_DEFAULT_CONFIG_NAME = "resource.xml";

    private URL propertiesURL;
    private File configFile;
    private File resourceTypeConfigFile; 

    java.util.HashMap hm = new java.util.HashMap();

    /**
     *
     */
    public ResourceTypeRegistry() {
        this(Yanel.DEFAULT_CONFIGURATION_FILE_XML);
    }

    /**
     * 
     */
    public ResourceTypeRegistry(String configurationFile) {
        CONFIGURATION_FILE = configurationFile;

        if (RealmManager.class.getClassLoader().getResource(CONFIGURATION_FILE) == null) {
            CONFIGURATION_FILE = Yanel.DEFAULT_CONFIGURATION_FILE;
        }

        if (ResourceTypeRegistry.class.getClassLoader().getResource(CONFIGURATION_FILE) != null) {

            if (CONFIGURATION_FILE.endsWith(".xml")) {
                
                try {
                    // It seems like one can also use URI instead URLDecoder in order to avoid the Windows issue if a path contains spaces
                    URI configFileUri = new URI(RealmManager.class.getClassLoader().getResource(CONFIGURATION_FILE).toString());
                    configFile = new File(configFileUri.getPath());
                } catch (Exception e) {
                    log.error("Failure while reading configuration: " + e.getMessage(), e);
                }
                
                try {
                    DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
                    Configuration config;
                    config = builder.buildFromFile(configFile);

                    resourceTypeConfigFile = new File(config.getChild("resource-types-config").getAttribute("src"));
                    if (!resourceTypeConfigFile.isAbsolute()) {
                        resourceTypeConfigFile = FileUtil.file(configFile.getParentFile().getAbsolutePath(), resourceTypeConfigFile.toString());
                    }
                    log.debug("Resource types configuration: " + resourceTypeConfigFile);
                    readResourceTypes();
                } catch (Exception e) {
                    String errorMsg = "Failure while reading configuration: " + e.getMessage();
                    log.error(errorMsg, e);
                }


            } else if (CONFIGURATION_FILE.endsWith("properties")) {
                log.warn("DEPRECATED: " + CONFIGURATION_FILE);

                propertiesURL = ResourceTypeRegistry.class.getClassLoader().getResource(CONFIGURATION_FILE);

                Properties props = new Properties();
                try {
                    props.load(propertiesURL.openStream());
                    // use URLDecoder to avoid problems when the filename contains spaces, see
                    // http://bugzilla.wyona.com/cgi-bin/bugzilla/show_bug.cgi?id=5165
                    File propsFile = new File(URLDecoder.decode(propertiesURL.getFile()));
                    String separator = ",";
                    String[] tokens = props.getProperty("resources").split(separator);
                    for (int i = 0; i < tokens.length; i++) {
                        File resConfigFile = new File(tokens[i]);
                        if (!resConfigFile.isAbsolute()) {
                            resConfigFile = FileUtil.file(propsFile.getParentFile().getAbsolutePath(), tokens[i]);
                        }

                        if (resConfigFile.isDirectory()) {
                            resConfigFile = new File(resConfigFile, RESOURCE_DEFAULT_CONFIG_NAME);
                        }

                        if (resConfigFile.isFile()) {
                            ResourceTypeDefinition rtd = new ResourceTypeDefinition(resConfigFile);
                            log.debug("Universal Name: " + rtd.getResourceTypeUniversalName());
                            log.debug("Classname: " + rtd.getResourceTypeClassname());
                            hm.put(rtd.getResourceTypeUniversalName(), rtd);
                        } else {
                            log.error("No such file or directory: " + resConfigFile);
                        }
                    }
                } catch (Exception e) {
                    log.error(e);
                }
            } else {
                log.error(CONFIGURATION_FILE + "have to be either .xml or .properties");
            }
        } else {
            log.error("No such config file" + CONFIGURATION_FILE);
        }
    }

    /**
     * Load/register resource types (e.g. from local/apache-tomcat-5.5.20/webapps/yanel/WEB-INF/classes/resource-types.xml)
     */
    public void readResourceTypes() throws ConfigurationException {
        try {
            DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder(true);
            Configuration config;
            config = builder.buildFromFile(resourceTypeConfigFile);
            if (log.isDebugEnabled()) log.debug("Resource types config file: " + resourceTypeConfigFile);
            
            Configuration resourceTypes[] = config.getChildren("resource-type");
            
            for (int i = 0; i < resourceTypes.length; i++) {
                log.debug("Register resource type...");
                try {
                    String packageName = resourceTypes[i].getAttribute("package"); // INFO: This method will throw an exception if no 'package' attribute exists, and hence further down it will try to read the 'src' attribute...
                    log.debug("Package: " + packageName);
                    log.info("Package: " + packageName);

                    // TODO: Config itself, e.g. org/wyona/yanel/impl/resources/redirect/my-resource.xml

                    URL packageURL = ResourceTypeRegistry.class.getClassLoader().getResource(packageName.replace('.','/'));
                    log.debug("Package: " + packageURL.getFile());
                    //log.info("Package: " + packageURL.getFile());
                    File jarFile = null;
                    if (packageURL.getPath().indexOf("!") > 0) {
                        jarFile = new File(packageURL.getPath().substring(5, packageURL.getPath().indexOf("!")));
                    }
                    if (jarFile != null && jarFile.isFile()) {
                        log.debug("Jar file: " + jarFile);
                        java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(jarFile);
                        java.util.Enumeration entries = zipFile.entries();
                        while (entries.hasMoreElements()) {
                            String entryName = ((java.util.zip.ZipEntry) entries.nextElement()).getName();
                            //log.debug("Entry: " + entryName);
                            if (entryName.indexOf("resource.xml") >= 0 || entryName.indexOf("resource-") >= 0) {
                                log.info("Resource definition: " + entryName);
                                URL resourceURL = ResourceTypeRegistry.class.getClassLoader().getResource(entryName);
                                //URL resourceURL = ResourceTypeRegistry.class.getClassLoader().getResource(packageName.replace('.','/') + "/resource.xml");
                                log.info("Resource config URL: " + resourceURL);
                                try {
                                    ResourceTypeDefinition rtd = new ResourceTypeDefinition(resourceURL.openStream());
                                    log.debug("Universal Name: " + rtd.getResourceTypeUniversalName());
                                    log.debug("Classname: " + rtd.getResourceTypeClassname());
                                    hm.put(rtd.getResourceTypeUniversalName(), rtd);
                                } catch (Exception exception) {
                                    log.error("Exception re registring resource with package '" + packageName + "' and resource definition URL '" + resourceURL + "'!");
                                    log.error(exception, exception);
                                }
                            }
                        }
                    } else if (new File(packageURL.getPath()).isDirectory()) {
                        log.warn("TODO: ...");
                                try {
                                    ResourceTypeDefinition rtd = new ResourceTypeDefinition(new java.io.FileInputStream(new File(packageURL.getPath(), "resource.xml")));
                                    log.debug("Universal Name: " + rtd.getResourceTypeUniversalName());
                                    log.debug("Classname: " + rtd.getResourceTypeClassname());
                                    hm.put(rtd.getResourceTypeUniversalName(), rtd);
                                } catch (Exception exception) {
                                    log.error("Exception re registring resource with package '" + packageName + "'!");
                                    log.error(exception, exception);
                                }
                    } else {
                        log.error("No such file or directory: " + packageURL.getPath());
                    }
                } catch (Exception e) {
                    log.error(e.getMessage()); // INFO: Do not show this error message, because it is not really an error in most cases
                    log.debug("No package attribute, hence try src attribute...");
                    log.info("No package attribute, hence try src attribute...");
                    try {
                        File resConfigFile = new File(resourceTypes[i].getAttribute("src"));
                        log.info("Source: " + resConfigFile);
                        if (!resConfigFile.isAbsolute()) {
                            resConfigFile = FileUtil.file(resourceTypeConfigFile.getParentFile().getAbsolutePath(), resourceTypes[i].getAttribute("src"));
                        }

                    if (resConfigFile.isDirectory()) {
                        File resDir = resConfigFile;
                    
                        Iterator iter = FileUtils.listFiles(resDir, new WildcardFilter("resource*.xml"), null).iterator();
                        while (iter.hasNext()) {
                            resConfigFile = (File)iter.next();
                            log.debug("found resource config: " + resConfigFile);
                            ResourceTypeDefinition rtd = new ResourceTypeDefinition(resConfigFile);
                            if (log.isDebugEnabled()) {
                                log.debug("Universal Name: " + rtd.getResourceTypeUniversalName());
                                log.debug("Classname: " + rtd.getResourceTypeClassname());
                            }
                            hm.put(rtd.getResourceTypeUniversalName(), rtd);
                        }
                    } else if (resConfigFile.isFile()) {
                        ResourceTypeDefinition rtd = new ResourceTypeDefinition(resConfigFile);
                        if (log.isDebugEnabled()) {
                            log.debug("Universal Name: " + rtd.getResourceTypeUniversalName());
                            log.debug("Classname: " + rtd.getResourceTypeClassname());
                        }
                        hm.put(rtd.getResourceTypeUniversalName(), rtd);
                    } else {
                        log.error("No such file or directory: " + resConfigFile);
                    }
                    } catch (Exception exception) {
                        log.error(exception, exception);
                    }
                }
            }    
        } catch (Exception e) {
            log.error(e, e);
            String errorMsg = "Failure while reading configuration: " + e.getMessage(); 
            throw new ConfigurationException(errorMsg, e);
        }
    }
   
    /**
     *
     */
    public ResourceTypeDefinition getResourceTypeDefinition(String universalName) throws Exception {
        if (!hm.containsKey(universalName)) {
            throw new Exception("Unknown resource type: " + universalName);
        }
        return (ResourceTypeDefinition) hm.get(universalName);
    }

    /**
     *
     */
    public ResourceTypeDefinition[] getResourceTypeDefinitions() {
        java.util.Set keys = hm.keySet();
        java.util.Iterator keysIterator = keys.iterator();
        ResourceTypeDefinition[] rtds = new ResourceTypeDefinition[keys.size()];
        int i = 0;
        while (keysIterator.hasNext()) {
            String universalName = (String) keysIterator.next();
            rtds[i] = (ResourceTypeDefinition)hm.get(universalName);
            i++;
        }
        return rtds;
    }

    /**
     * @deprecated
     */
    public Resource newResource(String universalName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    ResourceTypeDefinition rtd = (ResourceTypeDefinition) hm.get(universalName);
        if (rtd != null) {
            Resource resource = (Resource) Class.forName(rtd.getResourceTypeClassname()).newInstance();

            resource.setRTD(rtd);
            // TODO: Set Yanel instance ... but Yanel should be a singleton, because it instantiates the Map ... see Cmdl and Servlet ...
            //resource.setYanel(...);

            return resource;
        }
        log.error("No resource registered for rti: " + universalName);
        return null;
    }


    /**
     *
     */
    public String getConfigurationFile() {
        return CONFIGURATION_FILE;
    }
}
