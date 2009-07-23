                                                                                                                        /*
 * Copyright 2009 Wyona
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

package org.wyona.yanel.impl.resources.forgotpw;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.management.timer.Timer;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.validator.EmailValidator;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.wyona.commons.xml.XMLHelper;
import org.wyona.security.core.api.User;
import org.wyona.yanel.impl.resources.BasicXMLResource;
import org.wyona.yanel.impl.resources.SendMail;
import org.wyona.yarep.core.Node;
import org.wyona.yarep.core.NodeType;


/**
 * This resource is responsible for managing the forgot password functionality.
 * The following constant control the flow with the UI.
 * {@value #SUBMITFORGOTPASSWORD}  is passed when the user clicks on the first screen to submit email.
 *
 * {@value #SUBMITNEWPW}  is passed when the user enter the new password and submits the form.
 *
 * If the query string has pwresetid then we know that the user clicked on the link send via email.
 */
public class ForgotPassword extends BasicXMLResource {

    private static Logger log = Logger.getLogger(ForgotPassword.class);
    private long totalValidHrs;

    private static final String SUBMITFORGOTPASSWORD = "submitForgotPW";
    private static final String SUBMITNEWPW = "submitNewPW";
    private static final String NAMESPACE = "http://www.wyona.org/yanel/1.0";
    private static final long  DEFAULT_TOTAL_VALID_HRS = 24L;

    /**
     * This is the main method that handles all view request. The first time the request
     * is made to enter the data.
     */
    @Override
    protected InputStream getContentXML(String viewId) throws Exception {
        HttpServletRequest request = getEnvironment().getRequest();

        try {
            String hrsValid = getResourceConfigProperty("num-hrs-valid");
            if(hrsValid != null && !hrsValid.equals("")) {
                totalValidHrs = Long.parseLong(hrsValid);
            } else {
                totalValidHrs = DEFAULT_TOTAL_VALID_HRS ;
            }
        } catch(Exception ex) {
            log.error("num-hrs-valid flag not properly set: " + ex, ex);
            totalValidHrs = DEFAULT_TOTAL_VALID_HRS;
        }
        //Node xsltNode = getRealm().getRepository().getNode(getResourceConfigProperty("src"));
        Document adoc = XMLHelper.createDocument(NAMESPACE, "yanel-forgotpw");
        processUserAction(request, adoc);
        DOMSource source = new DOMSource(adoc);
        StringWriter xmlAsWriter = new StringWriter();
        StreamResult result = new StreamResult(xmlAsWriter);
        TransformerFactory.newInstance().newTransformer().transform(source, result);
        // write changes
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlAsWriter.toString().getBytes("UTF-8"));
        return inputStream;
    }

    private void processUserAction(HttpServletRequest request, Document adoc) throws Exception {
        String action = determineAction(request);
        log.debug("action performed: " + action);

        Element rootElement = adoc.getDocumentElement();
        if (action.equals(SUBMITFORGOTPASSWORD)) {
            String strVal = processForgotPW(request);
            Element statusElement = (Element) rootElement.appendChild(adoc.createElementNS(NAMESPACE, "show-message"));
            if(!strVal.equals("success")) {
                statusElement.setTextContent(strVal);
            } else {
                statusElement.setTextContent("Password change was successful. Please check your email.");
            }
        } else if (request.getParameter("pwresetid") != null && !request.getParameter("pwresetid").equals("") && !action.equals(SUBMITNEWPW)){
            User usr = findRepoUser(request.getParameter("pwresetid"), totalValidHrs);
            if(usr == null) {
                Element statusElement = (Element) rootElement.appendChild(adoc.createElementNS(NAMESPACE, "show-message"));
                statusElement.setTextContent("Unable to find forgot password request. Please try again.");
            } else {
                Element requestpwElement = (Element) rootElement.appendChild(adoc.createElementNS(NAMESPACE, "requestnewpw"));
                Element guidElement = (Element) requestpwElement.appendChild(adoc.createElementNS(NAMESPACE, "guid"));
                guidElement.setTextContent(request.getParameter("pwresetid"));
            }
        } else if(action.equals(SUBMITNEWPW)) {
            String retStr = updatePassword(request);
            Element statusElement = (Element) rootElement.appendChild(adoc.createElementNS(NAMESPACE, "show-message"));
            if(!retStr.equals("success")) {
                statusElement.setTextContent(retStr);
            } else {
                statusElement.setTextContent("Password has been successfully reset. Please login with your new password.");
            }
        } else {
            log.debug("default handler");
            Element requestElement = (Element) rootElement.appendChild(adoc.createElementNS(NAMESPACE, "requestemail"));
        }
    }

    private User findRepoUser(String usrGuid, long duration_hour) throws Exception {
        User upUser = null;
        Map<String, ResetPWExpire> pwHM = getOblivionMap(getEnvironment().getRequest());

        ResetPWExpire resetObj = pwHM.get(usrGuid);
        if (resetObj == null) {
            Node [] children = null;
            children = getRealm().getRepository().getNode("nodePath").getNodes();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = null;
            db = dbf.newDocumentBuilder();

            for(int i=0; i< children.length; i++) {
                Document doc = null;
                doc = db.parse(children[i].getInputStream());
                Element userElem = doc.getElementById("user");
                String userid = userElem.getAttribute("id");
                String guid = getTextValue(userElem, "guid");
                long savedDateTime = getLongValue(userElem, "starttime");
                String email = getTextValue(userElem, "email");

                if(guid.equals(usrGuid) && validGuid(savedDateTime, duration_hour)) {
                    upUser = realm.getIdentityManager().getUserManager().getUser(userid);
                }
            }
        }   else {
            boolean check = validGuid(resetObj.getDateTime(), duration_hour);
            if(check) {
                upUser = realm.getIdentityManager().getUserManager().getUser(resetObj.getUserId());
            }
        }
        return upUser;
    }

    private boolean validGuid(long starDT, long duration_hour) throws Exception {
        long currentDT = new Date().getTime();
        long expireTime= starDT + duration_hour * Timer.ONE_HOUR;

        return (expireTime>currentDT);
    }

    private String getTextValue(Element ele, String tagName) throws Exception {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if(nl != null && nl.getLength() > 0) {
            Element el = (Element)nl.item(0);
            textVal = el.getFirstChild().getNodeValue();
        }

        return textVal;
    }


    private long getLongValue(Element ele, String tagName) throws Exception {
        return Long.parseLong(getTextValue(ele,tagName));
    }

    /* Determine the requested view: defaultView, submitProfile,
    * submitPassword,submitGroup, submitDelete
    *
    * @param request
    * @return name of the desired view
    */
   private String determineAction(HttpServletRequest request) throws Exception {
       boolean submit = false;
       String action = "defaultView";

       Enumeration<?> enumeration = request.getParameterNames();
       while (enumeration.hasMoreElements() && !submit) {
           action = enumeration.nextElement().toString();
           if (action.startsWith("submit")) {
               submit = true;
           }
       }
       return action;
   }

   private Map<String, ResetPWExpire> getOblivionMap(HttpServletRequest request) throws Exception {
       javax.servlet.http.HttpSession session = request.getSession(true);
       Map<String, ResetPWExpire> pwHM = (Map<String, ResetPWExpire>) session.getAttribute("pwreset");
       if (pwHM == null) {
           pwHM = new HashMap<String, ResetPWExpire>();
           session.setAttribute("pwreset", pwHM);
       }
       return pwHM;
   }

    /**
     * Updates the user profile
     *
     * @param request
     *            The request containing the data to update
     * @param transformer
     */
    private String processForgotPW(HttpServletRequest request) throws Exception {
        String email = request.getParameter("email");
        String retStr = "";
        if (email == null || ("").equals(email)) {
            retStr = "E-mail address is empty.";
        } else if (! EmailValidator.getInstance().isValid(email)) {
            retStr = email + " is not a valid E-mail address.";
        } else {
            User[] userList = realm.getIdentityManager().getUserManager().getUsers(true);
            boolean userFnd = false;
            for(int i=0; i< userList.length; i++) {
                if (userList[i].getEmail().equals(email)) {
                    userFnd = true;
                    UUID ranUUid = UUID.randomUUID();
                    String guid = ranUUid.toString();

                    ResetPWExpire pwexp = new ResetPWExpire(userList[i].getID(), new Date().getTime(), guid, userList[i].getEmail());
                    Map<String, ResetPWExpire> pwHM = getOblivionMap(getEnvironment().getRequest());
                    pwHM.put(pwexp.getGuid(), pwexp);
                    String emailStr = "Please go to the following URL to reset password: <" + request.getRequestURL().toString() + "?pwresetid=" + guid + ">.";
                    log.debug(emailStr);
                    String emailServer = getResourceConfigProperty("smtpHost");
                    int port = Integer.parseInt(getResourceConfigProperty("smtpPort"));
                    String from = getResourceConfigProperty("smtpFrom");
                    String to =  userList[i].getEmail();
                    SendMail.send(emailServer, port, from, to, "password reset reset needs your confirmation", emailStr);
                    String xmlStrVal = generateXML(pwexp);


                    String fileName = pwexp.getUserId() + ".xml";
                    String filePath = "";
                    filePath = File.separator + getResourceConfigProperty("change-password-requests-path") + File.separator + fileName;
                    writeXMLOutput(filePath, xmlStrVal);
                    retStr = "success";
                    break;
                }
            }
            if(!userFnd) {
                retStr = "Unable to find user based on the "+email+" E-mail address.";
            }
        }
        return retStr;
    }

    private String generateXML(ResetPWExpire resetObj) throws Exception {
        org.w3c.dom.Document adoc = org.wyona.commons.xml.XMLHelper.createDocument(NAMESPACE, "user");
        Element userElement = adoc.getDocumentElement();

        Element emailElement = (Element) userElement.appendChild(adoc.createElement("email"));
        emailElement.setTextContent(resetObj.getEmail());
        Element startTimeElement = (Element) userElement.appendChild(adoc.createElement("starttime"));
        startTimeElement.setTextContent(Long.toString(resetObj.getDateTime()));

        Element guidElement = (Element) userElement.appendChild(adoc.createElement("guid"));
        guidElement.setTextContent(resetObj.getGuid());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer t = factory.newTransformer(); // identity transform
        DOMSource source = new DOMSource(adoc);
        StreamResult result = new StreamResult(baos);
        t.transform(source, result);

        return baos.toString();
    }

    private void writeXMLOutput(String path, String outputVal) throws Exception {
        Node fileToStore = null;
        if (getRealm().getRepository().existsNode(path)) {
            fileToStore = getRealm().getRepository().getNode(path);
        } else {
            fileToStore = getRealm().getRepository().getRootNode().addNode(path, NodeType.RESOURCE);
        }
        InputStream in = new ByteArrayInputStream(outputVal.getBytes());
        OutputStream out = fileToStore.getOutputStream();
        byte buffer[] = new byte[8192];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        out.close();
        in.close();
    }

    /**
     * Change user password.
     */
    private String updatePassword(HttpServletRequest request) throws Exception {
        String retStr = "";
        String plainPassword = request.getParameter("newPassword");
        boolean confirmation = plainPassword.equals(request.getParameter("newPasswordConfirmation"));
        if (confirmation && !plainPassword.equals("")) {
            User user = findRepoUser(request.getParameter("guid"), totalValidHrs);
            if(user !=null) {
                user.setPassword(plainPassword);
                user.save();
                retStr = "success";
            } else {
                retStr = "Unable to find user for password reset.";
            }
        } else {
            retStr = "Either no new password was supplied or the password supplied and its confirmation password do not match.";
        }
        return retStr;
    }

    @Override
    public boolean exists() throws Exception {
        log.warn("Not implemented yet!");
        return true;
    }
}
