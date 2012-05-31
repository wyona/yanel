/*
 * Copyright 2010 Wyona
 */
package org.wyona.yanel.resources.registration;

import org.wyona.yanel.core.util.MailUtil;

import org.wyona.yanel.impl.resources.BasicXMLResource;
//import org.wyona.yanel.resources.konakart.shared.SharedResource;

import org.wyona.commons.xml.XMLHelper;

import org.wyona.yarep.core.Node;
import org.wyona.yarep.core.NodeType;
import org.wyona.yarep.util.YarepUtil;

import org.wyona.security.core.api.User;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.net.URL;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

/*
import com.konakart.appif.CustomerRegistrationIf;
import com.konakart.appif.KKEngIf;
*/

/**
 * A resource to register new users
 */
public class UserRegistrationResource extends BasicXMLResource {
    
    private static Logger log = Logger.getLogger(UserRegistrationResource.class);

    static String NAMESPACE = "http://www.wyona.org/yanel/user-registration/1.0";

    private static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    private static final long  DEFAULT_TOTAL_VALID_HRS = 24L;

    private static final String FROM_ADDRESS_PROP_NAME = "fromEmail";
    
    /**
     * @see org.wyona.yanel.impl.resources.BasicXMLResource#getContentXML(String)
     */
    @Override
    protected InputStream getContentXML(String viewId) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }

        java.io.ByteArrayOutputStream baout = new java.io.ByteArrayOutputStream();
        org.wyona.commons.xml.XMLHelper.writeDocument(generateResponseDocument(), baout);
        return new java.io.ByteArrayInputStream(baout.toByteArray());
    }

    /**
     * Check whether email address is valid
     */
    private boolean isEmailValid(String email) {
        if (email != null && email.length() > 0 && email.indexOf("@") > 0) {
            return true;
        }
        return false;
    }

    /**
     * Check whether password is valid
     */
    private boolean isPasswordValid(String password) {
        if (password != null && password.length() > 0) {
            return true;
        }
        return false;
    }

    /**
     * Check whether firstname is valid
     */
    private boolean isFirstnameValid(String firstname) {
        if (firstname != null && firstname.length() > 0) {
            return true;
        }
        return false;
    }

    /**
     * Check whether lastname is valid
     */
    private boolean isLastnameValid(String lastname) {
        if (lastname != null && lastname.length() > 0) {
            return true;
        }
        return false;
    }

    /**
     * Check whether street is valid
     */
    private boolean isStreetValid(String street) {
        if (street != null && street.length() > 0) {
            return true;
        }
        return false;
    }

    /**
     * Check whether zip code is valid
     */
    private boolean isZipValid(String zipCode) {
        if (zipCode != null && zipCode.length() > 0) {
            return true;
        }
        return false;
    }

    /**
     * Check whether city is valid
     */
    private boolean isCityValid(String city) {
        if (city != null && city.length() > 0) {
            return true;
        }
        return false;
    }

    /**
     * Check whether phone number is valid
     */
    private boolean isPhoneValid(String phone) {
        if (phone != null && phone.length() > 0) {
            return true;
        }
        return false;
    }

    /**
     * Check whether gender is valid
     * @return Either 'm' or 'f' and otherwise null
     */
    private String isGenderValid(String gender) {
        if (gender != null && gender.length() > 0) {
            if (gender.equals("male")) {
                return "m";
            } else if (gender.equals("female")) {
                return "f";
            }
        }
        log.error("No such gender: " + gender);
        return null;
    }

    /**
     * Check whether company is valid
     * @return Either company name or null
     */
    private String isCompanyValid(String company) {
        if (company != null && company.length() > 0) {
            return company;
        }
        return null;
    }

    /**
     * Check whether fax number is valid
     * @return Either fax number or null
     */
    private String isFaxValid(String fax) {
        if (fax != null && fax.length() > 0) {
            return fax;
        }
        return null;
    }

    /**
     * Exists?
     */
    public boolean exists() {
        return true;
    }

    /**
     * Send email containing a confirmation link
     * @param userRegBean Bean containing all information about user registration
     */
    private void sendConfirmationLinkEmail(Document doc, UserRegistrationBean userRegBean) {
        log.info("Do not register user right away, but send an email to '" + userRegBean.getEmail() + "' containing a confirmation link...");
        Element rootElement = doc.getDocumentElement();

        try {
            Element element = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "confirmation-link-email"));
            if (sendNotificationsEnabled()) {
                MailUtil.send(getResourceConfigProperty(FROM_ADDRESS_PROP_NAME), userRegBean.getEmail(), "Activate User Registration (sent by Yanel)", getActivationURL(userRegBean));
                element.setAttribute("sent-by-yanel", "true");
            } else {
                element.setAttribute("sent-by-yanel", "false");
            }
            element.setAttribute("hours-valid", "" + DEFAULT_TOTAL_VALID_HRS);
            if (getResourceConfigProperty("include-activation-link") != null && getResourceConfigProperty("include-activation-link").equals("true")) {
                log.warn("Activation link will be part of response! Because of security reasons this should only be done for development or testing environments.");
                element.setAttribute("activation-link", getActivationURL(userRegBean));
            }
        } catch(Exception e) {
            log.error(e, e);
            Element element = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "confirmation-link-email-not-sent"));
            element.setAttribute("email", userRegBean.getEmail());
            element.setAttribute("exception-message", e.getMessage());
        }
    }

    /**
     * Register user
     * @param userRegBean User registration bean containing gender, firstname, etc.
     */
    private void registerUser(Document doc, UserRegistrationBean userRegBean) throws Exception {
        Element rootElement = doc.getDocumentElement();

        try {
            // INFO: Yanel registration
            if (getRealm().getIdentityManager().getUserManager().existsAlias(userRegBean.getEmail())) {
                throw new Exception("Alias '" + userRegBean.getEmail() + "' already exists, hence do not create user: " + userRegBean.getFirstname() + " " + userRegBean.getLastname());
            }

            User user = activateUser(userRegBean);

            addUserToGroups(user);

            getRealm().getIdentityManager().getUserManager().createAlias(userRegBean.getEmail(), user.getID());

            Element ncE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "new-customer-registered"));
            ncE.setAttributeNS(NAMESPACE, "id", user.getID());
        } catch(Exception e) {
            log.error(e, e);
            Element fnE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "registration-failed"));
            fnE.appendChild(doc.createTextNode("" + e.getMessage())); 
        }
    }

    /**
     * Activate user
     * @param userRegBean User registration bean containing gender, firstname, etc.
     */
    protected User activateUser(UserRegistrationBean userRegBean) throws Exception {
        long customerID = new java.util.Date().getTime();
        // TODO: Use encrypted password
        User user = getRealm().getIdentityManager().getUserManager().createUser("" + customerID, userRegBean.getFirstname() + " " + userRegBean.getLastname(), userRegBean.getEmail(), userRegBean.getPassword());
        // TODO: user.setProperty("gender", gender);
        user.setLanguage(getContentLanguage());
        user.save(); // INFO: User needs to be saved persistently before adding an alias, because otherwise one can add an alias though, but the 'link' from the user to the alias will not be created!
       return user;
    }

    /**
     * Save registration request persistently
     * @param urb User registration bean containing E-Mail address of user, etc.
     */
    protected void saveRegistrationRequest(UserRegistrationBean urb) throws Exception {
        Document doc = getRegistrationRequestAsXML(urb);
        Node node = null;
        try {
            String path = getActivationNodePath(urb.getUUID());
            if (!getRealm().getRepository().existsNode(path)) {
                node = YarepUtil.addNodes(getRealm().getRepository(), path, NodeType.RESOURCE);
            } else {
                log.error("Node already exists: " + "TODO");
                return;
            }
            XMLHelper.writeDocument(doc, node.getOutputStream());
        } catch(Exception e) {
            log.error(e, e);
        }
    }

    /**
     * Generate registration request as XML
     * @param urb User registration bean containing E-Mail address of user, etc.
     */
    private Document getRegistrationRequestAsXML(UserRegistrationBean urb) { // TODO: What about custom fields?!
        Document doc = XMLHelper.createDocument(NAMESPACE, "registration-request");
        Element rootElem = doc.getDocumentElement();
        rootElem.setAttribute("uuid", urb.getUUID());

        DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        rootElem.setAttribute("request-time", df.format(new Date().getTime()));

        // IMPORTANT TODO: Password needs to be encrypted!
        Element passwordElem = doc.createElementNS(NAMESPACE, "password");
        passwordElem.setAttribute("algorithm", "plaintext");
        passwordElem.setTextContent(urb.getPassword());
/*
        passwordElem.setAttribute("algorithm", "SHA-256");
        passwordElem.setTextContent(encrypt(urb.getPassword()));
        // TODO: What about salt?!
*/
        rootElem.appendChild(passwordElem);

        Element genderElem = doc.createElementNS(NAMESPACE, "gender");
        genderElem.setTextContent(urb.getGender());
        rootElem.appendChild(genderElem);

        Element lastnameElem = doc.createElementNS(NAMESPACE, "lastname");
        lastnameElem.setTextContent(urb.getLastname());
        rootElem.appendChild(lastnameElem);

        Element firstnameElem = doc.createElementNS(NAMESPACE, "firstname");
        firstnameElem.setTextContent(urb.getFirstname());
        rootElem.appendChild(firstnameElem);

        Element emailElem = doc.createElementNS(NAMESPACE, "email");
        emailElem.setTextContent(urb.getEmail());
        rootElem.appendChild(emailElem);

        return doc;
    }

    /**
     * Get activation URL which will be sent via E-Mail (also see YanelServlet#getRequestURLQS(HttpServletRequest, String, boolean))
     * @param userRegBean User registration bean containing 'all' information about registration request
     */
    public String getActivationURL(UserRegistrationBean userRegBean) throws Exception {
        //https://192.168.1.69:8443/yanel" + request.getServletPath().toString()
        URL url = new URL(request.getRequestURL().toString());
        org.wyona.yanel.core.map.Realm realm = getRealm();
        if (realm.isProxySet()) {
            // TODO: Finish proxy settings replacement

            String proxyHostName = realm.getProxyHostName();
            log.debug("Proxy host name: " + proxyHostName);
            if (proxyHostName != null) {
                url = new URL(url.getProtocol(), proxyHostName, url.getPort(), url.getFile());
            }

            int proxyPort = realm.getProxyPort();
            if (proxyPort >= 0) {
                url = new URL(url.getProtocol(), url.getHost(), proxyPort, url.getFile());
            } else {
                url = new URL(url.getProtocol(), url.getHost(), url.getDefaultPort(), url.getFile());
            }

            String proxyPrefix = realm.getProxyPrefix();
            if (proxyPrefix != null) {
                url = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile().substring(proxyPrefix.length()));
            }
        } else {
            log.warn("No proxy set.");
        }
        String uuid = userRegBean.getUUID();
        return url.toString() + "?uuid=" + uuid;
    }

    /**
     * Get homepage URL which will be sent via E-Mail (also see YanelServlet#getRequestURLQS(HttpServletRequest, String, boolean))
     */
    public String getHomepageURL() throws Exception {
        //https://192.168.1.69:8443/yanel" + request.getServletPath().toString()
        URL url = new URL(request.getRequestURL().toString());

        if (realm.isProxySet()) {
            org.wyona.yanel.core.map.Realm realm = getRealm();
            // TODO: Finish proxy settings replacement

            String proxyHostName = realm.getProxyHostName();
            log.debug("Proxy host name: " + proxyHostName);
            if (proxyHostName != null) {
                url = new URL(url.getProtocol(), proxyHostName, url.getPort(), url.getFile());
            }

            int proxyPort = realm.getProxyPort();
            if (proxyPort >= 0) {
                url = new URL(url.getProtocol(), url.getHost(), proxyPort, url.getFile());
            } else {
                url = new URL(url.getProtocol(), url.getHost(), url.getDefaultPort(), url.getFile());
            }

            String proxyPrefix = realm.getProxyPrefix();
            if (proxyPrefix != null) {
                url = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile().substring(proxyPrefix.length()));
            }
        } else {
            log.warn("No proxy set.");
        }
        return url.toString().replace("registration", "index"); // TODO: Replace hardcoded registration...
    }

    /**
     * @param email E-Mail of user which will be used as username/alias
     */
    private void processRegistrationRequest(Document doc, String email) throws Exception {
        Element rootElement = doc.getDocumentElement();

        UserRegistrationBean userRegBean = areSubmittedValuesValid(doc, email);
        if (userRegBean != null) {
            boolean emailConfigurationRequired = true;
            if (getResourceConfigProperty("email-confirmation") != null) {
                emailConfigurationRequired = new Boolean(getResourceConfigProperty("email-confirmation")).booleanValue();
            }
            if (!emailConfigurationRequired) {
                log.warn("User will be registered without email configuration! Because of security reasons this should only be done for development or testing environments.");
                registerUser(doc, userRegBean);
            } else {
                String uuid = java.util.UUID.randomUUID().toString();
                userRegBean.setUUID(uuid);
                try {
                    saveRegistrationRequest(userRegBean);
                    // TODO: Already create user, because of password encryption, but disable via expire?!
                    sendConfirmationLinkEmail(doc, userRegBean);
                } catch(Exception e) {
                   log.error(e, e);
                    Element invalidE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "one-or-more-inputs-not-valid"));
                    invalidE.appendChild(doc.createTextNode(e.getMessage())); 
                    return;
                }
            }
            rootElement.appendChild(doc.createElementNS(NAMESPACE, "all-inputs-valid"));
        } else {
            rootElement.appendChild(doc.createElementNS(NAMESPACE, "one-or-more-inputs-not-valid"));
        }
    }

    /**
     * Try to activate user registration
     * @param uuid UUID of user registration activation request
     * @return true if user registration activation was successful, otherwise return false if actication failed
     */
    protected boolean activateRegistration(String uuid, Document doc) {
        try {
            String path = getActivationNodePath(uuid);
            if (getRealm().getRepository().existsNode(path)) {

                UserRegistrationBean urBean = readRegistrationRequest(getRealm().getRepository().getNode(path));

                registerUser(doc, urBean);
                getRealm().getRepository().getNode(path).delete();

                String homepageURL = getHomepageURL();

                if (sendNotificationsEnabled()) {
                    MailUtil.send(getResourceConfigProperty(FROM_ADDRESS_PROP_NAME), urBean.getEmail(), "User Registration Successful", homepageURL);
                }

                Element rootElement = doc.getDocumentElement();
                // TODO: Add gender/salutation
                Element emailE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "email"));
                emailE.appendChild(doc.createTextNode(urBean.getEmail()));
                Element firstnameE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "firstname"));
                firstnameE.appendChild(doc.createTextNode(urBean.getFirstname()));
                Element lastnameE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "lastname"));
                lastnameE.appendChild(doc.createTextNode(urBean.getLastname()));

                return true;
            } else {
                log.error("No such activation request node: " + path);
                return false;
            }
        } catch(Exception e) {
            log.error(e, e);
            return false;
        }
    }

    /**
     *
     */
    private String getActivationNodePath(String uuid) {
        return "/user-registration-requests/" + uuid + ".xml";
    }

    /**
     * Read user registration request from repository node
     * @param node Repository node containing firstname, lastname, etc.
     */
    private UserRegistrationBean readRegistrationRequest(Node node) throws Exception {
        Document doc = XMLHelper.readDocument(node.getInputStream());
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new UserRegistrationNamespaceContext());

        // TODO: Get creation date to determine expire date!
        String uuid = (String) xpath.evaluate("/ur:registration-request/@uuid", doc, XPathConstants.STRING);
        String gender = (String) xpath.evaluate("/ur:registration-request/ur:gender", doc, XPathConstants.STRING);
        String firstname = (String) xpath.evaluate("/ur:registration-request/ur:firstname", doc, XPathConstants.STRING);
        String lastname = (String) xpath.evaluate("/ur:registration-request/ur:lastname", doc, XPathConstants.STRING);
        String email = (String) xpath.evaluate("/ur:registration-request/ur:email", doc, XPathConstants.STRING);
        String password = (String) xpath.evaluate("/ur:registration-request/ur:password", doc, XPathConstants.STRING);

        UserRegistrationBean urBean = new UserRegistrationBean(gender, firstname, lastname, email, password, "TODO", "TODO");
        urBean.setUUID(uuid);

        return urBean;
    }

    /**
     * Check whether notification emails should be sent (In the case of a "continuous integration" environment one might not want to send emails)
     */
    private boolean sendNotificationsEnabled() {
        try {
            String value = getResourceConfigProperty("send-notification-emails");
            if (value != null && value.equals("false")) {
                return false;
            }
        } catch(Exception e) {
            log.error(e, e);
        }
        return true;
    }

    /**
     * Add registered user to particular groups by default
     * @param user User to be added to groups
     */
    private void addUserToGroups(User user) throws Exception {
        String groupsCSV = getResourceConfigProperty("groups");
        if (groupsCSV != null) {
                String[] groupIDs = null;
                if (groupsCSV.indexOf(",") >= 0) {
                    groupIDs = groupsCSV.split(",");
                } else {
                    groupIDs = new String[1];
                    groupIDs[0] = groupsCSV;
                }
                for (int i = 0; i < groupIDs.length; i++) {
                    if (getRealm().getIdentityManager().getGroupManager().existsGroup(groupIDs[i])) {
                        log.warn("DEBUG: Add user '" + user.getEmail() + "' to group: " + groupIDs[i]);
                        getRealm().getIdentityManager().getGroupManager().getGroup(groupIDs[i]).addMember(user);
                    } else {
                        log.warn("No such group: " + groupIDs[i]);
                    }
                }
        }
    }

    /**
     * Generate document which is used for response
     */
    protected Document generateResponseDocument() throws Exception {
        Document doc = getEmptyDocument();
        Element rootElement = doc.getDocumentElement();
        String email = getEnvironment().getRequest().getParameter("email");
        String uuid = getEnvironment().getRequest().getParameter("uuid");
        if (email != null) {
            processRegistrationRequest(doc, email);
        } else if (uuid != null) {
            if(activateRegistration(uuid, doc)) {
                rootElement.appendChild(doc.createElementNS(NAMESPACE, "activation-successful"));
            } else {
                rootElement.appendChild(doc.createElementNS(NAMESPACE, "activation-failed"));
            }
        } else {
            rootElement.appendChild(doc.createElementNS(NAMESPACE, "no-input-yet"));
        }
        return doc;
    }

    /**
     * Get empty document to start with
     */
    private Document getEmptyDocument() throws Exception {
        Document doc = null;
        try {
            doc = org.wyona.commons.xml.XMLHelper.createDocument(NAMESPACE, "registration");
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }
        return doc;
    }

    /**
     * Check whether submitted fields are valid
     * @param doc DOM document to generate response
     * @param email E-Mail of user which will be used as username/alias
     * @return user registration information if all fields are valid, otherwise return null (and add errors to DOM document)
     */
    protected UserRegistrationBean areSubmittedValuesValid(Document doc, String email) throws Exception {
        boolean inputsValid = true;
        Element rootElement = doc.getDocumentElement();
        // INFO: Check email
            if (!isEmailValid(email)) {
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "email-not-valid"));
                inputsValid = false;
            } else {
                // TODO: if (getRealm().getIdentityManager().getUserManager().existsUser(email)) {
                if (getRealm().getIdentityManager().getUserManager().existsAlias(email)) {
                    log.warn("E-Mail '" + email + "' is already used as alias!");
                    Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "email-in-use"));
                    inputsValid = false;
                }
                Element emailE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "email"));
                emailE.appendChild(doc.createTextNode("" + email)); 
            }

        // INFO: Check password
            String password = getEnvironment().getRequest().getParameter("password");
            if (!isPasswordValid(password) || password.length() < 5) {
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "password-not-valid"));
                inputsValid = false;
            }
        // INFO: Check password confirmed
            String confirmedPassword = getEnvironment().getRequest().getParameter("password2");
            if (password != null && confirmedPassword != null && !password.equals(confirmedPassword)) {
                log.warn("Passwords do not match!");
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "passwords-do-not-match"));
                inputsValid = false;
            }

        // INFO: Check firstname
            String firstname = getEnvironment().getRequest().getParameter("firstname");
            if (!isFirstnameValid(firstname)) {
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "firstname-not-valid"));
                inputsValid = false;
            } else {
                Element fnE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "firstname"));
                fnE.appendChild(doc.createTextNode("" + firstname)); 
            }

        // INFO: Check lastname
            String lastname = getEnvironment().getRequest().getParameter("lastname");
            if (!isLastnameValid(lastname)) {
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "lastname-not-valid"));
                inputsValid = false;
            } else {
                Element fnE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "lastname"));
                fnE.appendChild(doc.createTextNode("" + lastname)); 
            }

        // INFO: Check gender (mandatory)
            String gender = isGenderValid(getEnvironment().getRequest().getParameter("salutation"));
            if (gender == null) {
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "gender-not-valid"));
                inputsValid = false;
            } else {
                Element fnE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "gender"));
                fnE.appendChild(doc.createTextNode("" + gender)); 
            }

        // INFO: Check company (optional)
            String company = isCompanyValid(getEnvironment().getRequest().getParameter("company"));
            if (company != null && company.length() > 0) {
                Element fnE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "company"));
                fnE.appendChild(doc.createTextNode("" + company)); 
            }

        // INFO: Check fax (optional)
            String fax = isFaxValid(getEnvironment().getRequest().getParameter("fax"));
            if (fax != null && fax.length() > 0) {
                Element fnE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "fax"));
                fnE.appendChild(doc.createTextNode("" + fax)); 
            }

        // INFO: Check street
        String street = getEnvironment().getRequest().getParameter("street");
        if (!isStreetValid(street)) {
            Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "street-not-valid"));
            inputsValid = false;
        } else {
            Element fnE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "street"));
            fnE.appendChild(doc.createTextNode("" + street)); 
        }

        // INFO: Check zip
            String zip = getEnvironment().getRequest().getParameter("zip");
            if (!isZipValid(zip)) {
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "zip-not-valid"));
                inputsValid = false;
            } else {
                Pattern pzip = Pattern.compile("[1-9][0-9]{3}");
                Matcher mzip = pzip.matcher(zip);
                if(mzip.find()) {
                    zip = mzip.group(0);
                    Element fnE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "zip"));
                    fnE.appendChild(doc.createTextNode("" + mzip.group(0))); 
                } else {
                    Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "zip-not-valid"));
                    inputsValid = false;
                }
            }

        // INFO: Check city
            String city = getEnvironment().getRequest().getParameter("location");
            if (!isCityValid(city)) {
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "city-not-valid"));
                inputsValid = false;
            } else {
                Element fnE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "city"));
                fnE.appendChild(doc.createTextNode("" + city)); 
            }

        // INFO: Check phone
        String phone = getEnvironment().getRequest().getParameter("phone");
        if (!isPhoneValid(phone)) {
            Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "phone-not-valid"));
            inputsValid = false;
        } else {
            Element fnE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "phone"));
            fnE.appendChild(doc.createTextNode("" + phone)); 
        }

        if (inputsValid) {
            UserRegistrationBean userRegBean = new UserRegistrationBean(gender, firstname, lastname, email, password, city, phone);
            userRegBean.setStreetName(street);
            userRegBean.setZipCode(zip);
            return userRegBean;
        } else {
            return null;
        }

    }
}

/**
 *
 */
class UserRegistrationNamespaceContext implements javax.xml.namespace.NamespaceContext {

    /**
     *
     */
    public String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("No prefix provided!");
        } else if (prefix.equals("ur")) {
            return UserRegistrationResource.NAMESPACE;
        } else if (prefix.equals("xhtml")) {
            return "http://www.w3.org/1999/xhtml";
        } else if (prefix.equals("dc")) {
            return "http://purl.org/dc/elements/1.1/";
        } else if (prefix.equals("dcterms")) {
            return "http://purl.org/dc/terms/";
        } else {
            return javax.xml.XMLConstants.NULL_NS_URI;
        }
    }

    /**
     *
     */
    public String getPrefix(String namespaceURI) {
        // Not needed in this context.
        throw new UnsupportedOperationException();
    }

    /**
     *
     */
    public java.util.Iterator getPrefixes(String namespaceURI) {
        // Not needed in this context.
        throw new UnsupportedOperationException();
    }
}
