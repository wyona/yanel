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

import org.wyona.security.core.api.IdentityManager;
import org.wyona.security.core.api.PolicyManager;
import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.core.map.RealmConfiguration;
import org.wyona.yarep.core.Repository;
import org.wyona.yarep.core.RepositoryFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * This class is a singleton.
 */
public class Yanel {

    private Map map = null;
    private ResourceTypeRegistry rtr = null;
    private ApplicationContext applicationContext;
    private RealmConfiguration realmConfig;
    private ResourceManager resourceManager;
    private boolean isInitialized = false;
    
    private static final String SPRING_CONFIG_FILE = "spring-*-config.xml"; 

    private static Yanel yanel = null;

    /**
    * Private constructor
    */
   private Yanel() throws Exception {
       applicationContext = new ClassPathXmlApplicationContext(SPRING_CONFIG_FILE);
   } 
   
   public void init() throws Exception {
       if (isInitialized) {
           return;
       } else {
           isInitialized = true;
       }
       
       map = (Map) applicationContext.getBean("map");
       realmConfig = new RealmConfiguration();
       map.setRealmConfiguration(realmConfig);

       rtr = new ResourceTypeRegistry();
       resourceManager = new ResourceManager();
       resourceManager.setResourceTypeRegistry(rtr);
       
       /*PolicyManager pm = (PolicyManager) yanel.getBeanFactory().getBean("policyManager");
       IdentityManager im = (IdentityManager) yanel.getBeanFactory().getBean("identityManager");
       
       Realm[] realms = realmConfig.getRealms();
       for (int i=0; i<realms.length; i++) {
           pm.addPoliciesRepository(realms[i].getPoliciesRepository());
           im.addIdentitiesRepository(realms[i].getIdentitiesRepository());
       }

       im = (IdentityManager) yanel.getBeanFactory().getBean("identityManager");*/

    }
   
    public static Yanel getInstance() throws Exception {
        if (yanel == null) {
            yanel = new Yanel();
        } 
        return yanel;
    }
   
    public BeanFactory getBeanFactory() {
        return applicationContext;
    }
    
    public RepositoryFactory getRepositoryFactory(String id) {
        return (RepositoryFactory)applicationContext.getBean(id);
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
    
    public ResourceManager getResourceManager() {
        return resourceManager;
    }
    
    public RealmConfiguration getRealmConfiguration() {
        return realmConfig;
    }
    
    /**
     * @deprecated
     * use getResourceManager().getResource() instead 
     */
    public Resource getResource(Realm realm, String path) throws Exception {
        return resourceManager.getResource(null, null, realm, path);
    }


}
