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

package org.wyona.yanel.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.wyona.yanel.core.ResourceTypeDefinition;
import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.ResourceTypeRegistry;
import org.wyona.yanel.core.Yanel;
import org.wyona.yanel.core.map.Map;
//import org.wyona.yanel.core.map.MapFactory;
import org.wyona.yanel.core.api.attributes.ViewableV1;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.api.attributes.CreatableV2;
import org.wyona.yanel.core.util.ResourceAttributeHelper;
import org.wyona.yanel.core.Path;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 */
public class CreateUsecaseHelper {

    private static Logger log = Logger.getLogger(CreateUsecaseHelper.class);

    /**
     *
     */
    public HttpServletResponse create(HttpServletRequest request, HttpServletResponse response, org.wyona.yanel.core.Yanel yanel) throws IOException {

        String createName = request.getParameter("create.name");
        String resourceType = request.getParameter("resource.type");
        String create = request.getParameter("create");

        PrintWriter w = response.getWriter();
        if (resourceType == null || createName == null) {
            w.print(resourcesTypeSelectScreen(createName));
        } else {
            if(create == null){
                if (resourceType.equals("") || createName.equals("")) {
                    w.print(resourcesTypeSelectScreen(createName));
                } else {
                    w.print(createResourceScreen(createName, resourceType));
                }
            } else{
                w.print(doCreate(resourceType, request, createName, yanel));
            }
        }
        return response;
    }

    /**
     * @return A XHTML page with the resource selection screen
     */
    public String resourcesTypeSelectScreen(String createName) {
        StringBuffer form = new StringBuffer();
        form.append("<?xml version=\"1.0\"?>");
        form.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        form.append("<body>");
        form.append("<form>");
        form.append("<input type=\"hidden\" name=\"yanel.usecase\" value=\"create\"/>");
        if (createName != null) {
            form.append("Name: <input type=\"text\" name=\"create.name\" value=\"" + createName + "\"/>");
        } else {
            form.append("Name: <input type=\"text\" name=\"create.name\"/>");
        }
        form.append("<br/>Resource Type: <select name=\"resource.type\">");

        ResourceTypeRegistry rtr = new ResourceTypeRegistry();
        ResourceTypeDefinition[] rtds = rtr.getResourceTypeDefinitions();
        for (int i = 0; i < rtds.length; i++) {
            try {
                Resource resource = rtr.newResource(rtds[i].getResourceTypeUniversalName());
                if (resource != null && ResourceAttributeHelper.hasAttributeImplemented(resource, "Creatable", "2")) {
                    form.append("<option value=\"" + rtds[i].getResourceTypeUniversalName() + "\">" + rtds[i].getResourceTypeLocalName() + "</option>");
                }
            } catch(Exception e) {
                log.error(e);
            }
        }

        form.append("</select>");
        form.append("<br/><input type=\"submit\" value=\"Next\"/>");
        form.append("</form>");
        form.append("</body>");
        form.append("</html>");

        return form.toString();
    }

    /**
     * @return a html page with the resource selction screen as a string
     */
    public String createResourceScreen(String createName, String rti) {

        String resourcesCreateScreen = null;
        String[] PropertyNames = null;

        ResourceTypeRegistry rtr = new ResourceTypeRegistry();

        try {
            Resource resource = rtr.newResource(rti);
            if (resource != null) {
                if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Creatable", "2")) {
                    PropertyNames = ((CreatableV2) resource).getPropertyNames();
                    ((CreatableV2) resource).setProperty("Name", createName);

                    StringBuffer form = new StringBuffer();
                    form.append("<?xml version=\"1.0\"?>");
                    form.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
                    form.append("<body>");
                    form.append("<h1>Create new resource</h1>");
                    if (createName == null) {
                        form.append("Name: <input type=\"text\"/><br/>");
                    } else {
                        form.append("Name: " + createName + "<br/>");
                    }
                    form.append("Resource Type: " + rti + "<br/><br/>");
                    form.append("<form>");
                    form.append("<input type=\"hidden\" name=\"yanel.usecase\" value=\"create\"/>");
                    form.append("<input type=\"hidden\" name=\"resource.type\" value=\""+rti+"\"/>");
                    form.append("<input type=\"hidden\" name=\"create.name\" value=\""+createName+"\"/>");
                    form.append("<input type=\"hidden\" name=\"create\" value=\"create.resource\"/>");

                    if (PropertyNames.length == 0) {
                        form.append("<p>No resource specific properties!</p>");
                    } else {
                        form.append("<p>Resource specific properties:</p>");
                        for (int i = 0; i < PropertyNames.length; i++) {
                            form.append(PropertyNames[i] + ": <input name=\"" + PropertyNames[i]
                                + "\" value=\""
                                + ((CreatableV2) resource).getProperty(PropertyNames[i])
                                + "\" size=\"60\">");
                        }
                    }

                    form.append("<br/><br/><input type=\"submit\" value=\"Create Resource\"/>");
                    form.append("</form>");
                    form.append("</body>");
                    form.append("</html>");

                    resourcesCreateScreen = form.toString();
                    return resourcesCreateScreen;
                }

            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            String message = e.toString();
            log.error(e.getMessage(), e);
            // Element exceptionElement = (Element)
            // rootElement.appendChild(doc.createElement("exception"));
            // exceptionElement.appendChild(doc.createTextNode(message));
            // setYanelOutput(response, doc);
            // response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return message;
        }

        return resourcesCreateScreen;
    }
    
    /**
     *
     */
    public String doCreate(String resourceType, HttpServletRequest request, String createName, org.wyona.yanel.core.Yanel yanel) {
        String responseAfterCreationScreen = null;
        ResourceTypeRegistry rtr = new ResourceTypeRegistry();
        String[] PropertyNames = null;
        
        try {
            Resource resource = rtr.newResource(resourceType);
            if (resource != null) {
                resource.setYanel(yanel);
                //resource.setRDT();
                if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Creatable", "2")) {
                    PropertyNames = ((CreatableV2) resource).getPropertyNames();
                    

                        ((CreatableV2) resource).create(request, createName);
                        
                        //response after creation, better would be a redirect to the fresh created resource
                        StringBuffer form = new StringBuffer();
                        form.append("<?xml version=\"1.0\"?>");
                        form.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
                        form.append("<body>");
                        form.append("New resource has been created successfully: <a href=\"" + createName + "\">" + createName + "</a>");
                        form.append("</body>");
                        responseAfterCreationScreen = form.toString();
                    }else{
                        //response after creation failed
                        StringBuffer form = new StringBuffer();
                        form.append("<?xml version=\"1.0\"?>");
                        form.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
                        form.append("<body>");
                        form.append("creation NOT successfull!");
                        form.append("</body>");
                        responseAfterCreationScreen = form.toString();
                    }
                return responseAfterCreationScreen;
                }

            }
         catch (Exception e) {
            log.error(e.getMessage(), e);
            String message = e.toString();
            log.error(e.getMessage(), e);
            // Element exceptionElement = (Element)
            // rootElement.appendChild(doc.createElement("exception"));
            // exceptionElement.appendChild(doc.createTextNode(message));
            // setYanelOutput(response, doc);
            // response.setStatus(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return message;
        }
        return responseAfterCreationScreen;
    }

}
