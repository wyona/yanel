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
import org.wyona.yanel.core.ResourceTypeDefinition;
import org.wyona.yanel.core.ResourceTypeRegistry;
import org.wyona.yanel.core.api.attributes.CreatableV1;
import org.wyona.yanel.core.api.attributes.CreatableV2;
import org.wyona.yanel.core.api.attributes.ModifiableV1;
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.map.Map;
import org.wyona.yanel.core.map.MapFactory;

import org.wyona.yanel.util.ResourceAttributeHelper;

/*
import com.wyonapictures.yanel.impl.resources.TapeResource;
import com.wyona.yanel.impl.resources.InvoiceResource;
import org.wyona.yanel.impl.resources.WebSearchResource;
*/

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
            if (value.equals("")) {
                System.out.println("No path has been specified!");
                return;
            }
            System.out.println("The following value has been entered: " + value);
            path = new Path(value);
        } catch (Exception e) {
            System.err.println(e);
        }

        String rti = map.getResourceTypeIdentifier(path);
        System.out.println("Resource Type Identifier: " + rti);

        if (rti == null) {
            System.err.println("Abort, because resource type identifier is null!");
            return;
        }

        ResourceTypeRegistry rtr = new ResourceTypeRegistry();
        ResourceTypeDefinition rtd = rtr.getResourceTypeDefinition(rti);
        System.out.println("Local name: " + rtd.getResourceTypeLocalName());
        System.out.println("Namespace: " + rtd.getResourceTypeNamespace());


        Resource res = null;
        try {
            res = rtr.newResource(rti);
        } catch(Exception e) {
            System.err.println("Exception 435435: " + e);
            return;
        }

        if (ResourceAttributeHelper.hasAttributeImplemented(res, "Creatable", "1")) {
            System.out.println(((CreatableV1) res).getPropertyNames());
        } else if (ResourceAttributeHelper.hasAttributeImplemented(res, "Creatable", "2")) {
            System.out.println(((CreatableV2) res).getPropertyType("name"));
        } else {
            System.out.println(res.getClass().getName() + " does not implement creatable interface!");
        }

        if (ResourceAttributeHelper.hasAttributeImplemented(res, "Viewable", "1")) {
            System.out.println("View Descriptors: " + ((ViewableV1) res).getViewDescriptors());
            String viewId = null;
            View view = ((ViewableV1) res).getView(path, viewId);
            System.out.println("mime-type: " + view.getMimeType());
            try {
                BufferedReader bReader = new BufferedReader(new java.io.InputStreamReader(view.getInputStream()));
                System.out.println("1. Line: " + bReader.readLine());
                System.out.println("2. Line: " + bReader.readLine());
                System.out.println("3. Line: " + bReader.readLine());
                System.out.println("4. Line: " + bReader.readLine());
                System.out.println("5. Line: " + bReader.readLine());
            } catch(Exception e) {
                System.err.println(e);
            }
        } else {
            System.out.println(res.getClass().getName() + " does not implement viewable interface!");
        }

        if (ResourceAttributeHelper.hasAttributeImplemented(res, "Modifiable", "1")) {
            java.io.Reader reader = ((ModifiableV1) res).getReader(path);
        } else {
            System.out.println(res.getClass().getName() + " does not implement writable interface!");
        }


        Resource tapeRes = null;
        try {
            tapeRes = rtr.newResource("<{http://www.wyonapictures.com/yanel/resource/1.0}tape/>");
        } catch(Exception e) {
            System.err.println(e);
            return;
        }
        tapeRes.setRTD(rtd);
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
            invoiceRes = rtr.newResource("<{http://www.wyona.com/yanel/resource/1.0}invoice/>");
        } catch(Exception e) {
            System.err.println(e);
            return;
        }
        invoiceRes.setRTD(rtd);
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
    }
}
