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

package org.wyona.yanel.impl.resources.showrealms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.wyona.yanel.core.map.RealmManager;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceTypeDefinition;
import org.wyona.yanel.core.ResourceTypeRegistry;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.core.util.PathUtil;

import org.wyona.yanel.core.Yanel;
import org.wyona.yarep.core.Repository;
import org.wyona.yanel.core.map.RealmWithConfigurationExceptionImpl;

import org.wyona.yanel.impl.resources.BasicXMLResource;

/**
 * Show all realms registered within this Yanel instance.
 */
public class ShowRealms extends BasicXMLResource {

    private static Logger log = Logger.getLogger(ShowRealms.class);

    /**
     * Get the source XML with all realms.
     * @see org.wyona.yanel.impl.resources.BasicXMLResource#getContentXML(String)
     */
    protected InputStream getContentXML(String viewId) throws Exception {
        Yanel yanel = getYanel();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();

        Element root = doc.createElement("yanel-info");
        doc.appendChild(root);

        RealmManager realms_config = yanel.getRealmConfiguration();
        String realms_file = realms_config.getRealmsConfigurationFile();

        Element envEl = doc.createElement("target-env");
        envEl.appendChild(doc.createTextNode(getYanel().getTargetEnvironment()));
        root.appendChild(envEl);

        Element realmsEl = doc.createElement("realms");
        realmsEl.setAttribute("config", realms_file);
        root.appendChild(realmsEl);

        Realm[] realms = realms_config.getRealms();
        for (Realm realm : realms) {

            String realm_id = realm.getID();
            Element realmEl = doc.createElement("realm");
            Element nameEl = doc.createElement("name");
            nameEl.appendChild(doc.createTextNode(realm.getName()));
            Element idEl = doc.createElement("id");
            idEl.appendChild(doc.createTextNode(realm_id));
            Element mountEl = doc.createElement("mountpoint");
            mountEl.appendChild(doc.createTextNode(realm.getMountPoint()));

            realmEl.appendChild(nameEl);
            realmEl.appendChild(idEl);
            realmEl.appendChild(mountEl);

            if (realm instanceof RealmWithConfigurationExceptionImpl) {
                // This realm threw a configuration exception!
                RealmWithConfigurationExceptionImpl rwce;
                rwce = (RealmWithConfigurationExceptionImpl) realm;
                String message = rwce.getConfigurationException().getMessage();

                // Log the configuration exception error
                log.error(String.format(
                    "Realm '%s' has thrown a configuration exception: %s",
                    realm_id, message));

                // Append exception to XML output
                Element exEl = doc.createElement("exception");
                exEl.appendChild(doc.createTextNode(message));
                realmEl.appendChild(exEl);
            }

            // Append realm to output
            realmsEl.appendChild(realmEl);
        }

        Element rtsEl = doc.createElement("resourcetypes");
        root.appendChild(rtsEl);

        ResourceTypeRegistry rtr = new ResourceTypeRegistry();
        ResourceTypeDefinition[] rtds = rtr.getResourceTypeDefinitions();
        for (ResourceTypeDefinition rt : rtds) {

            // Get local name, namespace, description
            String namespace = rt.getResourceTypeNamespace();
            String localname = rt.getResourceTypeLocalName();

            Element rtEl = doc.createElement("resourcetype");
            Element localnameEl = doc.createElement("localname");
            localnameEl.appendChild(doc.createTextNode(localname));

            Element namespaceEl = doc.createElement("namespace");
            namespaceEl.appendChild(doc.createTextNode(namespace));

            Element descriptionEl = doc.createElement("description");
            descriptionEl.appendChild(doc.createTextNode(
                rt.getResourceTypeDescription()));

            rtEl.appendChild(localnameEl);
            rtEl.appendChild(namespaceEl);
            rtEl.appendChild(descriptionEl);

            // Get document and icon paths
            String raw_path = String.format("%s::%s", namespace, localname);
            String enc_path = URLEncoder.encode(raw_path, "UTF-8");

            String htdocPath = String.format(
                "%s/resource-types/%s/%s/",
                PathUtil.getGlobalHtdocsPath(this),
                enc_path, yanel.getReservedPrefix());

            String docuPath = String.format(
                "%s/doc/index.html", htdocPath);

            String iconPath = String.format(
                "%s/icons/32x32/rt-icon.png", htdocPath);

            Element iconEl = doc.createElement("icon");
            iconEl.setAttribute("alt", localname);
            iconEl.appendChild(doc.createTextNode(iconPath));

            Element docuEl = doc.createElement("docu");
            docuEl.appendChild(doc.createTextNode(docuPath));

            rtEl.appendChild(iconEl);
            rtEl.appendChild(docuEl);
            rtsEl.appendChild(rtEl);
        }

        // Transform the DOM to actual output
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Source source = new DOMSource(doc);
        Result target = new StreamResult(baos);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();
        trans.transform(source, target);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        return is;
    }

    public boolean exists() {
        return true;
    }
}
