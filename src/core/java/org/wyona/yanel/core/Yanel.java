/*
 * Copyright 2006 Wyona
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

import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.core.map.RealmManager;
import org.wyona.yarep.core.RepositoryFactory;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This class is a singleton.
 */
public class Yanel {
    private static Logger log = LogManager.getLogger(Yanel.class);

    private Map map = null;
    private ResourceTypeRegistry rtr = null;
    private ApplicationContext applicationContext;
    private RealmManager realmConfig;
    private ResourceManager resourceManager;
    private boolean isInitialized = false;
    
    private static final String SPRING_CONFIG_FILE = "spring-*-config.xml";

    public static final String DEFAULT_CONFIGURATION_FILE = "yanel.properties";
    public static final String DEFAULT_CONFIGURATION_FILE_XML = "yanel.xml";

    private static Yanel yanel = null;

    private String smtpHost = null;
    private int smtpPort = -1;

    private String version = null;
    private String revision = null;
    private String reservedPrefix = null;
    private String targetEnv = null;
    private String truststoreSrc = null;
    private String truststorePwd = null;
    private boolean schedulerEnabled;
    private boolean preAuthenticationEnabled;
    private String preAuthReqHeaderName = null;

    private String smtpUsername, smtpPassword;

    private String adminName, adminEmail;

    /**
     * Private constructor
     */
    private Yanel() throws Exception {
        log.info("Spring config file: " + SPRING_CONFIG_FILE);
        applicationContext = new ClassPathXmlApplicationContext(SPRING_CONFIG_FILE);
    } 

   /**
    * Initialize Yanel
    */
   public void init() throws Exception {
       if (isInitialized) {
           return;
       }

       File configFile = getConfigFile();
       DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
       Configuration config = builder.buildFromFile(configFile);
       
       if (config.getChild("target-environment", false) != null) {
           targetEnv = config.getChild("target-environment").getValue();
       } else {
           log.warn("Target environment not configured within configuration: " + configFile);
           targetEnv = null;
       }

       configureSMTP(config, configFile);
       configureSSLTruststore(config, configFile);
       
       map = (Map) applicationContext.getBean("map");
       realmConfig = new RealmManager();
       map.setRealmConfiguration(realmConfig);

       rtr = new ResourceTypeRegistry();
       resourceManager = new ResourceManager();
       resourceManager.setResourceTypeRegistry(rtr);
       
       Configuration versionConfig = config.getChild("version");
       version = versionConfig.getAttribute("version");
       revision = versionConfig.getAttribute("revision");

       reservedPrefix = config.getChild("reserved-prefix").getValue();

       if (config.getChild("pre-auth-request-header", false) != null) {
           preAuthenticationEnabled = config.getChild("pre-auth-request-header").getAttributeAsBoolean("enabled");
           preAuthReqHeaderName = config.getChild("pre-auth-request-header").getValue();
       } else {
           preAuthenticationEnabled = false;
           preAuthReqHeaderName = null;
       }

       if (config.getChild("scheduler", false) != null) {
           schedulerEnabled = config.getChild("scheduler").getAttributeAsBoolean("enabled");
       } else {
           log.warn("Scheduler not configured inside global Yanel configuration: " + configFile);
           schedulerEnabled = false;
       }

       if (config.getChild("administrator", false) != null) {
           adminName = config.getChild("administrator").getValue();
           adminEmail = config.getChild("administrator").getAttribute("email");
       } else {
           log.warn("Administrator name and email not configured inside global Yanel configuration: " + configFile);
           adminName = null;
           adminEmail = null;
       }

       isInitialized = true;
    }

    /**
     * Get 'yanel.xml' configuration file
     */
    private File getConfigFile() {
       // 1.) Getting yanel.xml from hidden yanel directory inside user home directory
       log.debug("User home directory: " + System.getProperty("user.home"));
       File userHomeDotYanelConfigFile = new File(System.getProperty("user.home") + "/.yanel", DEFAULT_CONFIGURATION_FILE_XML);
        if (userHomeDotYanelConfigFile.isFile()) {
            log.warn("DEBUG: Use hidden folder inside user home directory: " + userHomeDotYanelConfigFile.getParentFile().getAbsolutePath());
            return userHomeDotYanelConfigFile;
        } else {
            log.warn("No yanel configuration found inside hidden folder at user home directory: " + userHomeDotYanelConfigFile.getAbsolutePath());
        }

       // 2.) Getting yanel.xml from classpath
       return new File(Yanel.class.getClassLoader().getResource(DEFAULT_CONFIGURATION_FILE_XML).getFile());
    }

    /**
     * Shutdown Yanel
     */
    public void destroy() {
       Realm[] realms = realmConfig.getRealms();
       for (int i = 0; i < realms.length; i++) {
           log.warn("Try to destroy realm: " + realms[i].getName() + " (" + (i + 1) + " of " + realms.length + ")");
           try {
               realms[i].destroy();
           } catch(Exception e) {
               log.error(e, e);
           }
       }
       log.warn("All realms destroyed.");
    }

    /**
     *
     */
    public static Yanel getInstance() throws Exception {
        if (yanel == null) {
            yanel = new Yanel();
        } 
        return yanel;
    }
   
    /**
     * Some resources are still using this, such as for example src/contributions/resources/wiki/src/java/org/wyona/yanel/impl/resources/WikiResource.java
     * @deprecated
     */
    public BeanFactory getBeanFactory() {
        log.warn("DEPRECATED");
        return applicationContext;
    }
    
    /**
     * Get repository factory
     * @param id Repository factory bean ID
     */
    public RepositoryFactory getRepositoryFactory(String id) {
        //log.debug("Repository factory bean id: " + id);
        return (RepositoryFactory)applicationContext.getBean(id);
    }
    
    /**
     * Get policy manager factory
     * @param id Policy manager factory bean ID
     */
    public org.wyona.security.core.PolicyManagerFactory getPolicyManagerFactory(String id) {
        return (org.wyona.security.core.PolicyManagerFactory)applicationContext.getBean(id);
    }
    
    /**
     * Get identity manager factory
     * @param id Identity manager factory bean ID
     */
    public org.wyona.security.core.IdentityManagerFactory getIdentityManagerFactory(String id) {
        return (org.wyona.security.core.IdentityManagerFactory)applicationContext.getBean(id);
    }
    
    /**
     * Get sitetree implementation
     * @param id Sitetree implementation bean ID
     */
    public org.wyona.yanel.core.navigation.Sitetree getSitetreeImpl(String id) {
        return (org.wyona.yanel.core.navigation.Sitetree)applicationContext.getBean(id);
    }
    
    /**
     * Get map implementation
     * @param id Map implementation bean ID
     */
    public org.wyona.yanel.core.map.Map getMapImpl(String id) {
        return (org.wyona.yanel.core.map.Map)applicationContext.getBean(id);
    }
    
    /**
     *
     */
    public Map getMap() throws Exception {
        return map;
    }
    
    public ResourceTypeRegistry getResourceTypeRegistry() {
        return rtr;
    }
    
    /**
     * Get resource manager
     */
    public ResourceManager getResourceManager() {
        return resourceManager;
    }
    
    /**
     * Get realm manager
     */
    public RealmManager getRealmConfiguration() {
        return realmConfig;
    }
    
    /**
     * @deprecated Use {@link #getResourceManager()} and then the method getResource() of the resource manager instead 
     */
    public Resource getResource(Realm realm, String path) throws Exception {
        return resourceManager.getResource(null, realm, path);
    }

    /**
     * Get Yanel version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Get Yanel revision
     */
    public String getRevision() {
        return revision;
    }

    /**
     * Get SMTP host
     */
    public String getSMTPHost() {
        return smtpHost;
    }

    /**
     * Get SMTP port
     */
    public int getSMTPPort() {
        return smtpPort;
    }

    /**
     * Get Yanel reserved prefix
     */
    public String getReservedPrefix() {
        return reservedPrefix;
    }

    /**
     * Get target environment
     */
    public String getTargetEnvironment() {
        return targetEnv;
    }

    /**
     * Check whether pre-authentication is enabled
     * @return true when pre-authentication is enabled and false otherwise
     */
    public boolean isPreAuthenticationEnabled() {
        return preAuthenticationEnabled;
    }

    /**
     * Get pre-authentication request header name, e.g. "SM_USER"
     * @return request header name and null when not set
     */
    public String getPreAuthenticationRequestHeaderName() {
        return preAuthReqHeaderName;
    }

    /**
     * Get administrator name
     * @return administrator name and null when not set
     */
    public String getAdministratorName() {
        return adminName;
    }

    /**
     * Get administrator email
     * @return administrator email and null when not set
     */
    public String getAdministratorEmail() {
        return adminEmail;
    }

    /**
     * Check whether scheduler is enabled
     */
    public boolean isSchedulerEnabled() {
        return schedulerEnabled;
    }

    /**
     * Check whether SMTP authentication is set
     */
    public boolean isSMTPAuthSet() {
        if (smtpUsername != null && smtpPassword != null) {
            return true;
        }
        return false;
    }

    /**
     * Configure SMTP host and port
     */
    private void configureSMTP(Configuration config, File configFile) throws Exception {
       if (config.getChild("smtp", false) != null) {
           Configuration smtpConfig = config.getChild("smtp");
           String smtpPortSt = smtpConfig.getAttribute("port");
           try {
               smtpPort = Integer.parseInt(smtpPortSt);
           } catch(NumberFormatException e) {
               log.warn("Mail server not configured, because SMTP port '" + smtpPortSt + "' does not seem to be a number! Check within configuration: " + configFile);
           }

           smtpHost = smtpConfig.getAttribute("host");

           // INFO: SMTP Authentication (optional), which is normally necessary in order to relay messages to other hosts/domains
           smtpUsername = smtpConfig.getAttribute("username", null);
           smtpPassword = smtpConfig.getAttribute("password", null);

           java.util.Properties props = new java.util.Properties();
           props.put("mail.smtp.host", smtpHost);
           props.put("mail.smtp.port", smtpPortSt);

           // INFO: Set generic mail.smtp.* attributes, like for example 'mail.smtp.localhost'
           String[] mailSmtpAttrNames = smtpConfig.getAttributeNames();
           if (mailSmtpAttrNames != null && mailSmtpAttrNames.length > 0) {
               for (int i = 0; i < mailSmtpAttrNames.length; i++) {
                   if (mailSmtpAttrNames[i].startsWith("mail.smtp.")) {
                       props.put(mailSmtpAttrNames[i], smtpConfig.getAttribute(mailSmtpAttrNames[i]));
                   }
               } 
           }

           // http://java.sun.com/products/javamail/javadocs/javax/mail/Session.html
           javax.mail.Session session = null;
           if (smtpUsername != null && smtpPassword != null) {
               log.info("SMTP authentication enabled using username: " + smtpUsername);
               props.put("mail.smtp.auth", "true");
               session = javax.mail.Session.getDefaultInstance(props, new YanelMailAuthenticator(smtpUsername, smtpPassword));
           } else {
               log.info("No SMTP authentication configured.");
               session = javax.mail.Session.getDefaultInstance(props, null);
           }
           log.info("Mailserver default session (available to all code executing in the same JVM): " + session.getProperty("mail.smtp.host") + ":" + session.getProperty("mail.smtp.port"));
       } else {
           log.warn("Mail server not configured within configuration: " + configFile);
       }
    }

    /**
     * Configure trust-store location and password
     */
    private void configureSSLTruststore(Configuration config, File configFile) throws Exception {
       if (config.getChild("trust-store", false) != null) {

           truststoreSrc = config.getChild("trust-store").getAttribute("src");
           if (!new File(truststoreSrc).exists()) {
               log.error("No such trust-store file: " + truststoreSrc);
               return;
           }
           truststorePwd = config.getChild("trust-store").getAttribute("password");
           // TODO: Validate password, e.g. null check

           System.setProperty("javax.net.ssl.trustStore", truststoreSrc);
           System.setProperty("javax.net.ssl.keyStorePassword", truststorePwd);
       } else {
           log.warn("SSL trust-store not configured in configuration: " + configFile);
       }
    }
}

/**
 * Simple authenticator used for SMTP
 */
class YanelMailAuthenticator extends javax.mail.Authenticator {

    private String username, password;

    /**
     *
     */
    public YanelMailAuthenticator(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * @see javax.mail.Authenticator#getPasswordAuthentication()
     */
    protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
        return new javax.mail.PasswordAuthentication(this.username, this.password);
    }
}
