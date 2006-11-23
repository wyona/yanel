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
    
    private static final String SPRING_CONFIG_FILE = "spring-yanel-config.xml"; 

    private static Yanel yanel = null;

    /**
    * Private constructor
    */
   private Yanel() throws Exception {
      
	   applicationContext = new ClassPathXmlApplicationContext(SPRING_CONFIG_FILE);	     
       // example:
       //RepositoryFactory repoFactory = (RepositoryFactory)applicationContext.getBean("repositoryFactory");
	   
       map = (Map) applicationContext.getBean("map");
       rtr = new ResourceTypeRegistry();

      
  
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
    
    /**
     *
     */
    public Resource getResource(Path path) throws Exception {
        String rti = map.getResourceTypeIdentifier(path);
        Resource resource = rtr.newResource(rti);

        ResourceTypeDefinition rtd = rtr.getResourceTypeDefinition(rti);
        resource.setRTD(rtd);

        resource.setYanel(this);

        return resource;
    }

    /**
     *
     */
    public Map getMap() throws Exception {
        return map;
    }
}
