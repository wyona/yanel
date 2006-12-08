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
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.core.map.Realm;

import org.wyona.yanel.util.ResourceAttributeHelper;

import org.wyona.security.core.api.Identity;
import org.wyona.security.core.api.PolicyManager;
import org.wyona.security.core.api.Role;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 */
public class YanelCommandLine {

    /**
     *
     */
    static public void main(String[] args) throws Exception {
        System.out.println("Welcome to the Yanel command line interface!\n");

        Yanel yanel = Yanel.getInstance();
        yanel.init();
        Map map = (Map) yanel.getBeanFactory().getBean("map");

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

        Path path = null;
        if (args.length == 1 && args[0].length() > 0) {
            path = new Path(args[0]);
        } else {
            System.out.println("\nPlease enter a path (e.g. /index.html):");
            try {
                String value = br.readLine();
                if (value.equals("")) {
                   System.out.println("No path has been specified!");
                   return;
                }
                System.out.println("The following value has been entered: " + value);
                path = new Path(value);
            } catch (Exception e) {
                System.err.println(e);
            }
        }

      
        PolicyManager pm = (PolicyManager) yanel.getBeanFactory().getBean("policyManager");
    

        String[] groupnames = {"admin", "accounting"};
        if (pm.authorize(new org.wyona.commons.io.Path(path.toString()), new Identity("lenya", groupnames), new Role("view"))) {
            System.out.println("Access granted: " + path);
        } else {
            // TODO: Deny access resp. start login process!
            System.out.println("Access denied: " + path);
        }

        String rti = map.getResourceTypeIdentifier(path);
        System.out.println("Resource Type Identifier: " + rti);

        if (rti == null) {
            System.err.println("Abort, because resource type identifier is null!");
            return;
        }

        ResourceTypeDefinition rtd = rtr.getResourceTypeDefinition(rti);
        if (rtd == null) {
            System.err.println("Abort, because no such resource type registered: " + rti);
            System.err.println("Make sure resource type is registered within " + rtr.getConfigurationFile());
            return;
        }

        System.out.println("Local name: " + rtd.getResourceTypeLocalName());
        System.out.println("Namespace: " + rtd.getResourceTypeNamespace());


        Resource res = null;
        CommandLineRequest request = new CommandLineRequest(path.toString());
        CommandLineResponse response = new CommandLineResponse();
        try {
            res = rtr.newResource(rti);
            res.setYanel(yanel);
            res.setRequest(request);
            res.setResponse(response);
        } catch(Exception e) {
            System.err.println("Exception 435435: " + e);
            return;
        }

        if (ResourceAttributeHelper.hasAttributeImplemented(res, "Creatable", "1")) {
            System.out.println(((CreatableV1) res).getPropertyNames());
        } else if (ResourceAttributeHelper.hasAttributeImplemented(res, "Creatable", "2")) {
            System.out.println(((CreatableV2) res).getPropertyType("name"));
        } else {
            System.out.println(res.getClass().getName() + " does not implement creatable interface, neither V1 nor V2!");
        }

        if (ResourceAttributeHelper.hasAttributeImplemented(res, "Viewable", "1")) {
            System.out.println("View Descriptors: " + ((ViewableV1) res).getViewDescriptors());
            String viewId = null;
            try {
                View view = ((ViewableV1) res).getView(path, viewId);
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
            System.out.println(res.getClass().getName() + " does not implement viewable V1 interface!");
        }

        if (ResourceAttributeHelper.hasAttributeImplemented(res, "Modifiable", "1")) {
            try {
                java.io.Reader reader = ((ModifiableV1) res).getReader(path);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        } else {
            System.out.println(res.getClass().getName() + " does not implement modifiable V1 interface!");
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
            System.out.println(tapeRes.getClass().getName() + " does not implement creatable interface!");
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
            System.out.println(invoiceRes.getClass().getName() + " does not implement creatable interface!");
        }
        if (ResourceAttributeHelper.hasAttributeImplemented(invoiceRes, "Versionable", "1")) {
            System.out.println(invoiceRes.getClass().getName() + " does implement versionable interface!");
        } else {
            System.out.println(invoiceRes.getClass().getName() + " does not implement versionable interface!");
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
