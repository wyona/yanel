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

package org.wyona.yanel.cmdl;

import org.wyona.yanel.cmdl.communication.CommandLineRequest;
import org.wyona.yanel.cmdl.communication.CommandLineResponse;
import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceTypeDefinition;
import org.wyona.yanel.core.ResourceTypeRegistry;
import org.wyona.yanel.core.Yanel;
import org.wyona.yanel.core.api.attributes.CreatableV1;
import org.wyona.yanel.core.api.attributes.CreatableV2;
import org.wyona.yanel.core.api.attributes.ModifiableV1;
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.core.map.Realm;

import org.wyona.yanel.core.util.ResourceAttributeHelper;

import org.wyona.security.core.api.Identity;
import org.wyona.security.core.api.PolicyManager;
import org.wyona.security.core.api.Role;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.log4j.Category;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Yanel command line interface, in order to execute requests/resources from the command line
 */
public class YanelCommandLine {

    private static Category log = Category.getInstance(YanelCommandLine.class);
    //private static Logger log = LogManager.getLogger(YanelCommandLine.class);

    /**
     *
     */
    static public void main(String[] args) throws Exception {
        System.out.println("Welcome to the Yanel command line interface!\n");

        System.out.println("Loading realms and resources/controllers... (please be patient)\n");

        Yanel yanel = Yanel.getInstance();
        yanel.init();
        Map map = yanel.getMapImpl("map");

        ResourceTypeRegistry rtr = new ResourceTypeRegistry();

        System.out.println("The following realms have been configured:");
        Realm[] realms = yanel.getRealmConfiguration().getRealms();
        for (int i = 0; i < realms.length; i++) {
            System.out.println("Realm: " + realms[i]);
        }

        System.out.println("\nThe following resource types have been configured:");
        ResourceTypeDefinition[] rtds = rtr.getResourceTypeDefinitions();
        for (int i = 0; i < rtds.length; i++) {
            System.out.println("Resource Type: " + rtds[i].getResourceTypeUniversalName());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String url = null;
        if (args.length != 1 || args[0].length() == 0) { // INFO: Check whether an argument has been set
            System.out.println("\nPlease enter a path (e.g. /index.html):");
            try {
                String value = br.readLine();
                if (value.equals("")) {
                   System.out.println("No path has been specified!");
                   return;
                }
                System.out.println("The following value has been entered: " + value);
                url = value;
            } catch (Exception e) {
                System.err.println(e);
            }
        } else {
            url = args[0];
        }

        String queryString = null;
        if (url.indexOf("?") >= 0) {
            queryString = url.substring(url.indexOf("?") + 1);
            url = url.substring(0, url.indexOf("?"));
        }

        String viewId = null;
        if (queryString != null) {
            System.out.println("The following URL has been entered: " + url + " (Query string: " + queryString + ")");
            if (queryString.startsWith("yanel.resource.viewid")) {
                viewId = queryString.split("=")[1];
            }
        } else {
            System.out.println("The following URL has been entered: " + url + " (no query string)");
        }
      
        Realm realm = map.getRealm(url);
        String path = map.getPath(realm, url);
        //PolicyManager pm = (PolicyManager) yanel.getBeanFactory().getBean("policyManager");
        PolicyManager pm = realm.getPolicyManager();
    
        Identity identity = null;
        if (pm.authorize(path, new Identity(), new Role("view"))) {
            identity = new Identity();
            System.out.println("Access granted for WORLD: " + path + " (Realm ID: " + realm.getID() + ")");
        } else {
            String[] groupnames = {"admin", "accounting"};
            identity = new Identity("lenya", groupnames, "lenya");
            System.out.println("Access denied for WORLD, hence let's try user '" + identity.getUsername() + "' ...");
            if (pm.authorize(path, identity, new Role("view"))) {
                System.out.println("Access granted for user '" + identity.getUsername() + "': " + path + " (Realm ID: " + realm.getID() + ")");
            } else {
                System.out.println("Access denied for user '" + identity.getUsername() + "' as well: " + path);

                // TODO: Abort or start login process!
                System.out.println("Let's continue anyway ...");
            }
        }

        Resource res = null;
        CommandLineRequest request = new CommandLineRequest(url);
        CommandLineResponse response = new CommandLineResponse();
        try {
            res  = yanel.getResourceManager().getResource(new org.wyona.yanel.core.Environment(request, response, identity, org.wyona.yanel.core.StateOfView.LIVE, null), realm, path);
            System.out.println("Resource Type Identifier: " + res.getResourceTypeUniversalName());
            System.out.println("Local name: " + res.getRTD().getResourceTypeLocalName());
            System.out.println("Namespace: " + res.getRTD().getResourceTypeNamespace());

/* INFO: Alternative way to init resource (without path in a sense)
            res = rtr.newResource(rti);
            res.setYanel(yanel);
            res.setRequest(request);
            res.setResponse(response);
*/
            System.out.println("Resource path: " + res.getPath());
        } catch(Exception e) {
            System.err.println("Exception (also see log4j: tail -f logs/log4j-cmdl.log): " + e);
            log.error(e.getMessage(), e);
            return;
        }

        if (ResourceAttributeHelper.hasAttributeImplemented(res, "Creatable", "1")) {
            System.out.println(res.getClass().getName() + " does implement creatable V1 interface.");
            System.out.println("Property names: " + ((CreatableV1) res).getPropertyNames());
        } else if (ResourceAttributeHelper.hasAttributeImplemented(res, "Creatable", "2")) {
            System.out.println(res.getClass().getName() + " does implement creatable V2 interface.");
            System.out.println("Property type (of 'name'): " + ((CreatableV2) res).getPropertyType("name"));
        } else {
            System.out.println(res.getClass().getName() + " does NOT implement creatable interface, neither V1 nor V2!");
        }

        if (ResourceAttributeHelper.hasAttributeImplemented(res, "Viewable", "1")) {
            System.out.println("View Descriptors: " + ((ViewableV1) res).getViewDescriptors());
            try {
                View view = ((ViewableV1) res).getView(new Path(url), viewId);
                System.out.println("mime-type: " + view.getMimeType());

                BufferedReader bReader = new BufferedReader(new java.io.InputStreamReader(view.getInputStream()));
                int k = 0;
                String line = null;
                while ((line = bReader.readLine()) != null) {
                    k++;
                    System.out.println("Line " + k + ": " + line);
                }
            } catch(Exception e) {
                System.err.println(e);
            }
        } else {
            System.out.println(res.getClass().getName() + " does NOT implement viewable V1 interface!");
        }

        if (ResourceAttributeHelper.hasAttributeImplemented(res, "Viewable", "2")) {
            System.out.println(res.getClass().getName() + " does implement viewable V2 interface!");
            ViewDescriptor[] vds = ((ViewableV2) res).getViewDescriptors();
            System.out.println("View Descriptors: ");
            for (int i = 0; i < vds.length; i++) {
                System.out.println(" - Id: " + vds[i].getId() + ", Mime-Type: " + vds[i].getMimeType());
            }
            if (viewId == null && vds.length > 0) {
                if (vds.length > 0) {
                    viewId = vds[0].getId();
                } else {
                    log.error("No view ID set!");
                }
            }
            System.out.println("View ID: " + viewId);
            try {
                View view = ((ViewableV2) res).getView(viewId);
                System.out.println("mime-type: " + view.getMimeType());

                BufferedReader bReader = new BufferedReader(new java.io.InputStreamReader(view.getInputStream()));
                int k = 0;
                String line = null;
                while ((line = bReader.readLine()) != null) {
                    k++;
                    System.out.println("Line " + k + ": " + line);
                }
            } catch(Exception e) {
                System.err.println(e);
            }
        } else {
            System.out.println(res.getClass().getName() + " does NOT implement viewable V2 interface!");
        }

        if (ResourceAttributeHelper.hasAttributeImplemented(res, "Modifiable", "1")) {
            try {
                java.io.Reader reader = ((ModifiableV1) res).getReader(new Path(url));
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        } else {
            System.out.println(res.getClass().getName() + " does NOT implement modifiable V1 interface!");
        }


/*
        Resource tapeRes = null;
        try {
            rti = "<{http://www.wyonapictures.com/yanel/resource/1.0}tape/>";
            tapeRes = rtr.newResource(rti);
            rtd = rtr.getResourceTypeDefinition(rti);
            if (tapeRes != null) {
                tapeRes.setRTD(rtd);
            } else {
                System.err.println("No such resource registered for rti: " + rti);
                return;
            }
        } catch(Exception e) {
            System.err.println(e);
            return;
        }
        if (ResourceAttributeHelper.hasAttributeImplemented(tapeRes, "Creatable", "1")) {
            String[] names = ((CreatableV1) tapeRes).getPropertyNames();
            String propNames = "";
            for (int i = 0; i < names.length; i++) {
                if (i == names.length -1) {
                    propNames = propNames + names[i];
                } else {
                    propNames = propNames + names[i] + ", ";
                }
            }
            System.out.println("Property Names: " + propNames);
        } else {
            System.out.println(tapeRes.getClass().getName() + " does NOT implement creatable interface!");
        }



        Resource invoiceRes = null;
        try {
            rti = "<{http://www.wyona.com/yanel/resource/1.0}invoice/>";
            invoiceRes = rtr.newResource(rti);
            rtd = rtr.getResourceTypeDefinition(rti);
            invoiceRes.setRTD(rtd);
        } catch(Exception e) {
            System.err.println(e);
            return;
        }

        if (ResourceAttributeHelper.hasAttributeImplemented(invoiceRes, "Creatable", "1")) {
            String[] names = ((CreatableV1) invoiceRes).getPropertyNames();
            String propNames = "";
            for (int i = 0; i < names.length; i++) {
                System.out.println("Please enter a value for property \"" + names[i] + "\":");
                try {
                    String value = br.readLine();
                    System.out.println("The following value has been entered: " + value);
                } catch (Exception e) {
                    System.err.println(e);
                }
                if (i == names.length -1) {
                    propNames = propNames + names[i];
                } else {
                    propNames = propNames + names[i] + ", ";
                }
            }
            System.out.println("Property Names: " + propNames);
        } else {
            System.out.println(invoiceRes.getClass().getName() + " does NOT implement creatable V1 interface!");
        }

        if (ResourceAttributeHelper.hasAttributeImplemented(invoiceRes, "Versionable", "1")) {
            System.out.println(invoiceRes.getClass().getName() + " does implement versionable interface!");
        } else {
            System.out.println(invoiceRes.getClass().getName() + " does NOT implement versionable V1 interface!");
        }

	try {
            Resource websearchRes = rtr.newResource("<{http://www.wyona.org/yanel/resource/1.0}websearch/>");
            websearchRes.setRTD(rtd);
            if (ResourceAttributeHelper.hasAttributeImplemented(websearchRes, "Continuable", "1")) System.out.println("yeah");
        } catch(Exception e) {
            System.err.println(e);
            return;
        }
*/
    }
}
