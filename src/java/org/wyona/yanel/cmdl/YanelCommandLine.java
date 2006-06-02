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

import org.wyona.yanel.core.Path;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceDefaultImpl;
import org.wyona.yanel.core.ResourceTypeDefinition;
import org.wyona.yanel.core.ResourceTypeRegistry;
import org.wyona.yanel.core.ResourceDefaultImpl;
import org.wyona.yanel.core.attributes.CreatableResource;
import org.wyona.yanel.core.attributes.ViewableResource;
import org.wyona.yanel.core.attributes.WritableResource;
import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.core.map.MapFactory;
import org.wyona.yanel.util.ResourceAttributeHelper;

import com.wyonapictures.yanel.impl.resources.TapeResource;
import com.wyona.yanel.impl.resources.InvoiceResource;
import org.wyona.yanel.impl.resources.WebSearchResource;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 */
public class YanelCommandLine {

    /**
     *
     */
    static public void main(String[] args) {
        System.out.println("Welcome to the Yanel command line interface!\n");

        MapFactory mf = MapFactory.newInstance();
        Map map = mf.newMap();

        System.out.println("Please enter a path (e.g. /hello/world.html):");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Path path = null;
        try {
            String value = br.readLine();
            System.out.println("The following value has been entered: " + value);
            path = new Path(value);
        } catch (Exception e) {
            System.err.println(e);
        }

        // TODO: See YarepSource!
        //Path path = new Path("/hello/world.html");
        //Path path = new Path("/index.html");
        //Path path = new Path("/wyonapictures/index.html");

        String rti = map.getResourceTypeIdentifier(path);
        System.out.println("Resource Type Identifier: " + rti);

        ResourceTypeDefinition rtd = ResourceTypeRegistry.getResourceTypeDefinition(rti);
        System.out.println("Local name: " + rtd.getResourceTypeLocalName());
        System.out.println("Namespace: " + rtd.getResourceTypeNamespace());


        Resource res = new ResourceDefaultImpl(rtd);

        if (ResourceAttributeHelper.hasAttributeImplemented(res, "Creatable")) {
            System.out.println(((CreatableResource) res).getPropertyNames());
        } else {
            System.out.println(res.getClass().getName() + " does not implement creatable interface!");
        }

        if (ResourceAttributeHelper.hasAttributeImplemented(res, "Viewable")) {
            System.out.println(((ViewableResource) res).getViewDescriptors());
        } else {
            System.out.println(res.getClass().getName() + " does not implement viewable interface!");
        }

        if (ResourceAttributeHelper.hasAttributeImplemented(res, "Writable")) {
            ((WritableResource) res).write(path);
        } else {
            System.out.println(res.getClass().getName() + " does not implement writable interface!");
        }


        Resource tapeRes = new TapeResource(rtd);
        if (ResourceAttributeHelper.hasAttributeImplemented(tapeRes, "Creatable")) {
            String[] names = ((CreatableResource) tapeRes).getPropertyNames();
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

        Resource invoiceRes = new InvoiceResource(rtd);
        if (ResourceAttributeHelper.hasAttributeImplemented(invoiceRes, "Creatable")) {
            String[] names = ((CreatableResource) invoiceRes).getPropertyNames();
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
        if (ResourceAttributeHelper.hasAttributeImplemented(invoiceRes, "Versionable")) {
            System.out.println(invoiceRes.getClass().getName() + " does implement versionable interface!");
        } else {
            System.out.println(invoiceRes.getClass().getName() + " does not implement versionable interface!");
        }

        Resource websearchRes = new WebSearchResource(rtd);
        if (ResourceAttributeHelper.hasAttributeImplemented(websearchRes, "Continuable")) System.out.println("yeah");
    }
}
