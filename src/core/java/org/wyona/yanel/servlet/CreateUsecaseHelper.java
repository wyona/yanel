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
import org.wyona.yarep.core.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 */
public class CreateUsecaseHelper {

    private static Logger log = Logger.getLogger("test");

    /**
     *
     */
    public HttpServletResponse create(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String createName = request.getParameter("create.name");
        String resourceType = request.getParameter("resource.type");

        if (resourceType == null) {
            PrintWriter w = response.getWriter();
            w.print(resourcesTypeSelectScreen(createName));
        } else {
            PrintWriter w = response.getWriter();
            w.print(createResourceScreen(createName, resourceType));
        }
        return response;





/*
        String create = request.getParameter("create");
        String path = request.getRequestURI();

        
        if (create != null && create.equals("create.resource")) {
            
            String[] PropertyNames = null;

            ResourceTypeRegistry rtr = new ResourceTypeRegistry();
            try {
                Resource resource = rtr.newResource(resourceType);
                if (resource != null) {
                    if (ResourceAttributeHelper.hasAttributeImplemented(resource, "Creatable", "2")) {
                        PropertyNames = ((CreatableV2) resource).getPropertyNames();
                        
                        for (int i = 0; i < PropertyNames.length; i++) {
                            
                            if(PropertyNames[i].equals("Name")){
                                ((CreatableV2) resource).create(path+"/"+request.getParameter(PropertyNames[i]));
                            }
                        }
                        
                        try {
                            String parent = request.getRequestURL().toString().substring(0,request.getRequestURL().toString().indexOf("?"));
                            URL url = new URL(parent+"/"+createName);
                            
                            response.setHeader("Location", url.toString());
                            response.setStatus(javax.servlet.http.HttpServletResponse.SC_MOVED_PERMANENTLY);
                            return response;
                        } catch (Exception e) {
                            log.error(e);
                        }

                        return response;
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
                return response;
            }
            
            

        }
        // if no resourceType is available it returns the resourcesSelectScreen
        if (resourceType == null) {
            String resourcesSelectScreen = this.resourcesTypeSelectScreen(createName);

            PrintWriter w = response.getWriter();
            w.print(resourcesSelectScreen);
            return response;
        } else {
            String resourcesCreateScreen = this.createResourceScreen(createName, resourceType);
            if (ResourceExistsHelper.resourceExists(path)) {
                if (createName == null) {
                    // make child of path, ask for name
                    System.out.println("make child of path, ask for name. resourec " + path
                            + " exists");
                    createResourceScreen("", resourceType);
                } else {
                    // create child of request path with name createName
                    System.out.println("create child of request path with name createName. resourec "
                            + path + " exists");
                    createResourceScreen(createName, resourceType);
                }
            } else {
                // check if parent exists
                String parent = "";
                if (path.lastIndexOf("/") != -1) {
                    parent = path.substring(0, path.lastIndexOf("/"));
                }
                if (ResourceExistsHelper.resourceExists(parent)) {
                    createName = path.substring(path.lastIndexOf("/"), path.length());
                    log.debug("the requested resource (" + path
                            + ") does not exist, will create it");
                    // build this path
                    System.out.println("build this path. resource " + path + " exists");
                    createResourceScreen(createName, resourceType);
                } else {
                    log.debug("the requested resource (" + path
                            + ") does not exist, even not its parent ( " + parent
                            + " ). please create the parent first");
                    // build this path
                    System.out.println("the requested resource (" + path
                            + ") does not exist, even not its parent( " + parent
                            + " ). please create the parent first");
                }
            }
            PrintWriter w = response.getWriter();
            w.print(resourcesCreateScreen);
            return response;
        }
*/

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
        form.append("<br/><input type=\"submit\" value=\"Proceed\"/>");
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
                    }
                    for (int i = 0; i < PropertyNames.length; i++) {
                        form.append(PropertyNames[i] + ":<input name=\"" + PropertyNames[i]
                                + "\" value=\""
                                + ((CreatableV2) resource).getProperty(PropertyNames[i])
                                + "\" size=\"60\">");
                    }

                    form.append("<input type=\"submit\" value=\"Create Resource\"/>");
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

}
