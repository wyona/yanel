/*
 * Copyright 2010 Wyona
 */
package org.wyona.yanel.resources.registration;

import org.wyona.yanel.core.util.MailUtil;

import org.wyona.yanel.impl.resources.BasicXMLResource;

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

import javax.mail.internet.InternetAddress;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

/**
 * A resource to register new users
 */
public class UserRegistrationResource extends BasicXMLResource {
    
    private static Logger log = LogManager.getLogger(UserRegistrationResource.class);

    static String NAMESPACE = "http://www.wyona.org/yanel/user-registration/1.0";

    private static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    private static final long  DEFAULT_TOTAL_VALID_HRS = 24L;

    private static final String FROM_ADDRESS_PROP_NAME = "fromEmail";

    private static final String ONE_OR_MORE_INPUTS_NOT_VALID = "one-or-more-inputs-not-valid";

    private static final String ADMIN_CONFIRMATION_KEY = "admin-confirmation-key";
    private static final String ADMINISTRATOR_CONFIRMED = "administrator-confirmed";

    protected static final String EMAIL = "email";
    protected static final String FIRSTNAME = "firstname";
    protected static final String LASTNAME = "lastname";
    protected static final String STREET = "street";
    protected static final String CITY = "location";
    protected static final String ZIP = "zip";
    protected static final String GENDER = "gender";
    protected static final String SALUTATION = "salutation";
    protected static final String PHONE = "phone";
    protected static final String COMPANY = "company";

    private static final String IS_PRE_AUTH_ATTR_NAME = "is-pre-authenticated";
    private static final String PASSWORD_ELEMENT_NAME = "password";

    protected static final String PASSWORD = "password";
    protected static final String PASSWORD_CONFIRMED = "password2";
    
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
     * Check whether first name is valid
     * @param firstname First name
     * @return true when first name is valid and false otherwise
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
     * Check whether zip code is neither null nor has zero length
     */
    private boolean isZipNotEmpty(String zipCode) {
        if (zipCode != null && zipCode.length() > 0) {
            return true;
        }
        return false;
    }

    /**
     * Check whether format of zip code is valid
     * @param zipCode ZIP code
     * @return valid zip code or null if not valid
     */
    protected String isZipValid(String zipCode) {
        Pattern pzip = Pattern.compile("[1-9][0-9]{3}"); // INFO: Example of valid ZIP: 1234, Example of not-valid ZIP: 01234
        Matcher mzip = pzip.matcher(zipCode);
        if(mzip.find()) {
            return mzip.group(0);
        } else {
            log.warn("Format of ZIP '" + zipCode + "' is not valid!");
            return null;
        }
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
            element.setAttribute("sent-by-yanel", "false");
            if (sendNotificationsEnabled()) {
                if (administratorConfirmationRequired()) {
                    String adminConfirmationKey = java.util.UUID.randomUUID().toString();
                    setAdminConfirmationKey(userRegBean.getUUID(), adminConfirmationKey);

                    StringBuilder body = new StringBuilder();
                    body.append("A user with email address '" + userRegBean.getEmail() + "' has sent a registration request.");
                    body.append("\n\nPlease confirm the request by clicking on the following link:");
                    body.append("\n\n" + getActivationURL(userRegBean) + "&" + ADMIN_CONFIRMATION_KEY + "=" + adminConfirmationKey);
                    body.append("\n\nNote that this confirmation link is valid only for the next " + getHoursValid() + " hours.");
                    MailUtil.send(getResourceConfigProperty(FROM_ADDRESS_PROP_NAME), getResourceConfigProperty("administrator-email"), "[" + getRealm().getName() + "] Confirm User Registration Request", body.toString());
                    Element adminConfirmationRequiredEl = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "admin-confirmation-required"));
                }

                MailUtil.send(getResourceConfigProperty(FROM_ADDRESS_PROP_NAME), userRegBean.getEmail(), getSubject(), getConfirmationEmailBody(getActivationURL(userRegBean)));

                element.setAttribute("sent-by-yanel", "true");
            }

            element.setAttribute("hours-valid", "" + getHoursValid());
            if (getResourceConfigProperty("include-activation-link") != null && getResourceConfigProperty("include-activation-link").equals("true")) {
                log.warn("Activation link will be part of response! Because of security reasons this should only be done for development or testing environments.");
                element.setAttribute("activation-link", getActivationURL(userRegBean));
            }
        } catch(Exception e) {
            log.error(e, e);
            Element element = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "confirmation-link-email-not-sent"));
            element.setAttribute(EMAIL, userRegBean.getEmail());
            element.setAttribute("exception-message", e.getMessage());
        }
    }

    /**
     * Get subject of confirmation email
     */
    private String getSubject() throws Exception {
        String subject = "Activate User Registration (sent by Yanel)";
        if (getResourceConfigProperty("subject") != null) {
            subject = getResourceConfigProperty("subject");
        }
        return subject;
    }

    /**
     * Get body of confirmation email
     * @param url Email confirmation link
     * @return body of confirmation email
     */
    private String getConfirmationEmailBody(String url) throws Exception {
        String body = null;
        if (getResourceConfigProperty("email-body-template-path") != null) {
            Node templateNode = getRealm().getRepository().getNode(getResourceConfigProperty("email-body-template-path"));
            InputStream in = templateNode.getInputStream();
            body = org.apache.commons.io.IOUtils.toString(in);
            in.close();
        } else {
            String htdocsPath = "rthtdocs:/registration-confirmation-email-template.txt";
            org.wyona.yanel.core.source.SourceResolver resolver = new org.wyona.yanel.core.source.SourceResolver(this);
            javax.xml.transform.Source source = resolver.resolve(htdocsPath, null);
            InputStream in = ((javax.xml.transform.stream.StreamSource) source).getInputStream();
            body = org.apache.commons.io.IOUtils.toString(in);
            in.close();
        }
        body = body.replace("@CONFIRMATION_LINK@", url);
        body= body.replace("@VALID_HRS@", "" + getHoursValid());
        return body;
    }

    /**
     * Get hours valid
     */
    private long getHoursValid() throws Exception {
        if (getResourceConfigProperty("hours-valid") != null) {
            return new Long(getResourceConfigProperty("hours-valid")).longValue();
        }
        return DEFAULT_TOTAL_VALID_HRS;
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

            // TODO: getUserManager().existsAlias(userRegBean.getEmail())
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
     * Create user profile access policy
     * @param id User ID
     */
    private void createUserProfileAccessPolicy(String id) throws Exception {
        // TODO: Also see src/resources/user-mgmt/src/java/org/wyona/yanel/impl/resources/CreateUserResource.java

        org.wyona.security.core.api.PolicyManager policyManager = getRealm().getPolicyManager();
        org.wyona.security.core.api.Policy policy = policyManager.createEmptyPolicy();
        org.wyona.security.core.UsecasePolicy usecasePolicy = new org.wyona.security.core.UsecasePolicy("view");
        usecasePolicy.addIdentity(new org.wyona.security.core.api.Identity(id, id), true);
        policy.addUsecasePolicy(usecasePolicy);
        // TODO: Replace "/users" by org.wyona.yanel.servlet.YanelGlobalResourceTypeMatcher#usersPathPrefix
        policyManager.setPolicy("/" + getYanel().getReservedPrefix() + "/users/" + id + ".html", policy);
    }

    /**
     * Activate user
     * @param userRegBean User registration bean containing gender, firstname, etc.
     * @return activated user
     */
    protected User activateUser(UserRegistrationBean userRegBean) throws Exception {
        long customerID = new java.util.Date().getTime();

        createUserProfileAccessPolicy("" + customerID);

        if (userRegBean.isPreAuthenticated()) {
            log.warn("User '" + customerID + "' is pre-authenticated and we will try to create a user account without password, but the user management implementation might not support accounts without password ...");
        }
        // TODO: Use encrypted password
        User user = getRealm().getIdentityManager().getUserManager().createUser("" + customerID, getName(userRegBean.getFirstname(), userRegBean.getLastname()), userRegBean.getEmail(), userRegBean.getPassword());
        // TODO: user.setProperty(GENDER, gender);
        user.setLanguage(getContentLanguage());
        user.save(); // INFO: User needs to be saved persistently before adding an alias, because otherwise one can add an alias though, but the 'link' from the user to the alias will not be created!
       return user;
    }

    /**
     * Get name of user
     */
    private String getName(String firstname, String lastname) {
        if (firstname == null && lastname == null) {
            return null;
        } else {
            return firstname + " " + lastname;
        }
    }

    /**
     * Save registration request persistently
     * @param urb User registration bean containing E-Mail address of user, etc.
     * @throws ValidationException if during saving the registration request more validation errors occur, which might be the case if third-party system is involved
     * @throws Exception if some generic error occurs
     */
    protected void saveRegistrationRequest(UserRegistrationBean urb) throws ValidationException, Exception {
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
     * @return user registration bean as XML document
     */
    private Document getRegistrationRequestAsXML(UserRegistrationBean urb) { // TODO: What about custom fields?!
        Document doc = XMLHelper.createDocument(NAMESPACE, "registration-request");
        Element rootElem = doc.getDocumentElement();
        rootElem.setAttribute("uuid", urb.getUUID());

        DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        rootElem.setAttribute("request-time", df.format(new Date().getTime()));

        Element passwordElem = doc.createElementNS(NAMESPACE, PASSWORD_ELEMENT_NAME);
        if (urb.isPreAuthenticated()) {
            passwordElem.setAttribute(IS_PRE_AUTH_ATTR_NAME, "true");
        } else {
            // IMPORTANT TODO: Password needs to be encrypted!
            passwordElem.setAttribute("algorithm", "plaintext");
            passwordElem.setTextContent(urb.getPassword());
/*
            passwordElem.setAttribute("algorithm", "SHA-256");
            passwordElem.setTextContent(encrypt(urb.getPassword()));
            // TODO: What about salt?!
*/
        }
        rootElem.appendChild(passwordElem);

        Element genderElem = doc.createElementNS(NAMESPACE, GENDER);
        genderElem.setTextContent(urb.getGender());
        rootElem.appendChild(genderElem);

        Element lastnameElem = doc.createElementNS(NAMESPACE, LASTNAME);
        lastnameElem.setTextContent(urb.getLastname());
        rootElem.appendChild(lastnameElem);

        Element firstnameElem = doc.createElementNS(NAMESPACE, FIRSTNAME);
        firstnameElem.setTextContent(urb.getFirstname());
        rootElem.appendChild(firstnameElem);

        Element emailElem = doc.createElementNS(NAMESPACE, EMAIL);
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
     * @param doc XML document containing response to client
     * @param email E-Mail of user which will be used as username/alias
     */
    private void processRegistrationRequest(Document doc, String email) throws Exception {
        Element rootElement = doc.getDocumentElement();

        addSubmittedValuesToResponse(doc);
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
                } catch(ValidationException e) {
                    log.error(e, e);
                    Element invalidE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, ONE_OR_MORE_INPUTS_NOT_VALID));
                    invalidE.appendChild(doc.createTextNode("Validation errors: " + e.getMessage())); 

                    ValidationError[] ves = e.getValidationErrors();
                    if (ves != null) {
                        for (int i = 0; i < ves.length; i++) {
                            Element validationErrorEl = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "validation-error"));
                            validationErrorEl.setAttributeNS(NAMESPACE, "key", ves[i].getKey());
                            validationErrorEl.setAttributeNS(NAMESPACE, "value", ves[i].getValue());
                            validationErrorEl.setAttributeNS(NAMESPACE, "code", ves[i].getErrorCode());
                            log.warn("Validation error: '" + ves[i].getKey() + "', '" + ves[i].getValue()+ "', '" + ves[i].getErrorCode() + "')");
                        }
                    }

                    return;
                } catch(Exception e) {
                    log.error(e, e);
                    Element invalidE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, ONE_OR_MORE_INPUTS_NOT_VALID));
                    invalidE.appendChild(doc.createTextNode(e.getMessage())); 
                    return;
                }
            }
            rootElement.appendChild(doc.createElementNS(NAMESPACE, "all-inputs-valid"));
        } else {
            log.warn("One or more inputs are not valid...see returned XML for more details!");
            rootElement.appendChild(doc.createElementNS(NAMESPACE, ONE_OR_MORE_INPUTS_NOT_VALID));
        }
    }

    /**
     * Check whether administrator needs to confirm registration request
     */
    private boolean administratorConfirmationRequired() throws Exception {
        if (getResourceConfigProperty("administrator-confirmation-required") != null && getResourceConfigProperty("administrator-confirmation-required").equals("true")) {
            return true;
        }
        return false;
    }

    /**
     * Check whether administrator key matches
     * @param adminConfirmationKey Confirmation key as UUID
     * @param uuid UUID of user registration activation request
     * @param doc Document containing response to administrator trying to confirm registration request
     * @return true if administrator key matches, otherwise return false
     */
    private boolean adminKeyMatches(String adminConfirmationKey, String uuid, Document doc) throws Exception {
        String path = getActivationNodePath(uuid);
        if (getRealm().getRepository().existsNode(path)) {
            Node registrationRequestNode = getRealm().getRepository().getNode(path);
            UserRegistrationBean urBean = readRegistrationRequest(registrationRequestNode);
            if (adminConfirmationKey.equals(urBean.getAdministratorConfirmationKey())) {
                return true;
            } else {
                log.warn("Keys did not match!");
            }
        } else {
            log.error("No such activation request node: " + path);
        }
        return false;
    }

    /**
     * Try to activate user registration
     * @param uuid UUID of user registration activation request
     * @param doc Document containing response to user trying to activate account
     * @return true if user registration activation was successful, otherwise return false if actication failed
     */
    protected boolean activateRegistration(String uuid, Document doc) {
        try {
            String path = getActivationNodePath(uuid);
            if (getRealm().getRepository().existsNode(path)) {

                Node registrationRequestNode = getRealm().getRepository().getNode(path);
                UserRegistrationBean urBean = readRegistrationRequest(registrationRequestNode);

                Element rootElement = doc.getDocumentElement();

                if (administratorConfirmationRequired() && !urBean.hasAdministratorConfirmedRegistration()) {
                    log.warn("Administrator has not confirmed registration request yet!");
                    rootElement.appendChild(doc.createElement("administrator-not-confirmed-yet"));
                    setConfirmedByUser(registrationRequestNode);
                    return false;
                } else {
                    registerUser(doc, urBean);
                    getRealm().getRepository().getNode(path).delete();

                if (doNotifyAdministrator()) {
                    StringBuilder body = new StringBuilder();
                    body.append("The following user account has been activated:");
                    body.append("\n\n" + urBean.getEmail());
                    body.append("\n\nvia " + getHomepageURL());
                    MailUtil.send(getResourceConfigProperty(FROM_ADDRESS_PROP_NAME), getResourceConfigProperty("administrator-email"), "[" + getRealm().getName() + "] User account has been created", body.toString());
                }

                if (sendNotificationsEnabled() && sendActivationSuccessfulEmail()) {
                    StringBuilder body = new StringBuilder();
                    body.append("Thank you for your registration.");
                    body.append("\n\nYou have successfully activated your account.");
                    body.append("\n\n" + getHomepageURL());
                    MailUtil.send(getResourceConfigProperty(FROM_ADDRESS_PROP_NAME), urBean.getEmail(), "[" + getRealm().getName() + "] User Registration Successful", body.toString());
                }

                // TODO: Add gender/salutation

                Element emailE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, EMAIL));
                emailE.appendChild(doc.createTextNode(urBean.getEmail()));

                Element firstnameE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, FIRSTNAME));
                firstnameE.appendChild(doc.createTextNode(urBean.getFirstname()));

                Element lastnameE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, LASTNAME));
                lastnameE.appendChild(doc.createTextNode(urBean.getLastname()));

                return true;
                }
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
     * Set attribute that administrator has confirmed registration request
     * @param requestUUID UUID of registration request
     */
    private void setAdminConfirmed(String requestUUID) throws Exception {
        String path = getActivationNodePath(requestUUID);
        if (getRealm().getRepository().existsNode(path)) {
            Node node = getRealm().getRepository().getNode(path);
            Document doc = XMLHelper.readDocument(node.getInputStream());
            doc.getDocumentElement().setAttribute(ADMINISTRATOR_CONFIRMED, "true");
            java.io.OutputStream out = node.getOutputStream();
            XMLHelper.writeDocument(doc, out);
            out.close();
        } else {
            log.warn("No such reqgistration request '" + path + "'!");
        }
    }

    /**
     * Add administrator confirmation key to registration request
     * @param requestUUID UUID of registration request
     * @param adminKey Administrator confirmation key
     */
    private void setAdminConfirmationKey(String requestUUID, String adminKey) throws Exception {
        String path = getActivationNodePath(requestUUID);
        if (getRealm().getRepository().existsNode(path)) {
            Node node = getRealm().getRepository().getNode(path);
            Document doc = XMLHelper.readDocument(node.getInputStream());
            doc.getDocumentElement().setAttribute(ADMIN_CONFIRMATION_KEY, adminKey);
            java.io.OutputStream out = node.getOutputStream();
            XMLHelper.writeDocument(doc, out);
            out.close();
        } else {
            log.warn("No such reqgistration request '" + path + "'!");
        }
    }

    /**
     * Set flag that user confirmed email address
     * @param node Repository node containing registration request (firstname, lastname, etc.)
     */
    private void setConfirmedByUser(Node node) throws Exception {
        Document doc = XMLHelper.readDocument(node.getInputStream());
        DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        doc.getDocumentElement().setAttribute("date-confirmed-by-user", df.format(new Date().getTime()));
        java.io.OutputStream out = node.getOutputStream();
        XMLHelper.writeDocument(doc, out);
        out.close();
    }

    /**
     * Read user registration request from repository node
     * @param node Repository node containing firstname, lastname, etc.
     * @return user registration bean containing information about submitted user registration
     */
    private UserRegistrationBean readRegistrationRequest(Node node) throws Exception {
        Document doc = XMLHelper.readDocument(node.getInputStream());
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new UserRegistrationNamespaceContext());

        // TODO: Get creation date to determine expire date!
        String uuid = (String) xpath.evaluate("/ur:registration-request/@uuid", doc, XPathConstants.STRING);
        String gender = (String) xpath.evaluate("/ur:registration-request/ur:" + GENDER, doc, XPathConstants.STRING);
        String firstname = (String) xpath.evaluate("/ur:registration-request/ur:" + FIRSTNAME, doc, XPathConstants.STRING);
        String lastname = (String) xpath.evaluate("/ur:registration-request/ur:" + LASTNAME, doc, XPathConstants.STRING);
        String email = (String) xpath.evaluate("/ur:registration-request/ur:" + EMAIL, doc, XPathConstants.STRING);

        boolean isPreAuthenticated = false;
        String isPreAuthenticatedStr = (String) xpath.evaluate("/ur:registration-request/ur:" + PASSWORD_ELEMENT_NAME + "/@" + IS_PRE_AUTH_ATTR_NAME, doc, XPathConstants.STRING);
        if (isPreAuthenticatedStr != null && isPreAuthenticatedStr.equals("true")) {
            isPreAuthenticated = true;
        }

        String password = null;
        if (!isPreAuthenticated) {
            password = (String) xpath.evaluate("/ur:registration-request/ur:" + PASSWORD_ELEMENT_NAME, doc, XPathConstants.STRING);
        }

        UserRegistrationBean urBean = new UserRegistrationBean(gender, firstname, lastname, email, password, "TODO", "TODO");
        urBean.setPreAuthenticated(isPreAuthenticated);

        urBean.setUUID(uuid);

        if (doc.getDocumentElement().hasAttribute(ADMINISTRATOR_CONFIRMED)) {
            if (doc.getDocumentElement().getAttribute(ADMINISTRATOR_CONFIRMED).equals("true")) {
                urBean.setAdministratorConfirmed(true);
            }
        } else {
            urBean.setAdministratorConfirmed(false);
        }

        if (doc.getDocumentElement().hasAttribute(ADMIN_CONFIRMATION_KEY)) {
            urBean.setAdministratorConfirmationKey(doc.getDocumentElement().getAttribute(ADMIN_CONFIRMATION_KEY));
        }

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
     * Check whether an email should be sent when activation was successful
     */
    private boolean sendActivationSuccessfulEmail() {
        try {
            String value = getResourceConfigProperty("send-activation-successful-email");
            if (value != null && value.equals("false")) {
                return false;
            }
        } catch(Exception e) {
            log.error(e, e);
        }
        return true;
    }

    /**
     * Check whether notification email should be sent to administrator
     */
    private boolean doNotifyAdministrator() {
        try {
            String value = getResourceConfigProperty("notify-administrator");
            if (value != null && value.equals("true")) {
                return true;
            }
        } catch(Exception e) {
            log.error(e, e);
        }
        return false;
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
                        log.debug("Add user '" + user.getEmail() + "' to group: " + groupIDs[i]);
                        getRealm().getIdentityManager().getGroupManager().getGroup(groupIDs[i]).addMember(user);
                    } else {
                        log.warn("No such group: " + groupIDs[i]);
                    }
                }
        }
    }

    /**
     * Generate document which is used for response
     * @return XML document containing response
     */
    protected Document generateResponseDocument() throws Exception {
        Document doc = getEmptyDocument();
        Element rootElement = doc.getDocumentElement();

        String email = null;
        if (getEnvironment().getRequest().getParameter(EMAIL) != null) {
            try {
                email = new InternetAddress(getEnvironment().getRequest().getParameter(EMAIL)).getAddress();
            } catch(Exception e) {
                Element exceptionEl = (Element) rootElement.appendChild(doc.createElement("invalid-email-address"));
                exceptionEl.appendChild(doc.createTextNode(e.getMessage()));
                log.error(e, e);
            }
        }

        String uuid = getEnvironment().getRequest().getParameter("uuid");
        String adminConfirmationKey = getEnvironment().getRequest().getParameter(ADMIN_CONFIRMATION_KEY);
        if (email != null) { // INFO: Somebody tries to register (Please note that the email can also be empty in case somebody forgets to enter an email, but the query string parameter 'email' will exist anyway)
            processRegistrationRequest(doc, email);
        } else if (uuid != null) { // INFO: Somebody (user or administrator) tries to activate registration
            if (adminConfirmationKey != null) {
                log.warn("DEBUG: Administrator confirms registration request.");
                if (adminKeyMatches(adminConfirmationKey, uuid, doc)) {
                    setAdminConfirmed(uuid);

                    String path = getActivationNodePath(uuid);
                    if (getRealm().getRepository().existsNode(path)) {
                        Node registrationRequestNode = getRealm().getRepository().getNode(path);
                        UserRegistrationBean urBean = readRegistrationRequest(registrationRequestNode);

                        Element adminConfirmedEl = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "administrator-confirmed"));
                        adminConfirmedEl.setAttribute("user-email", urBean.getEmail());

                        StringBuilder body = new StringBuilder("Administrator has confirmed your registration request.");
                        body.append("\n\nTo activate your account, you need to click on the following link:");
                        body.append("\n\n" + getActivationURL(urBean));
                        // TODO: Calculate remaining time
                        //body.append("\n\nNote that this confirmation link is valid only for the next " + getHoursValid() + " hours.");
                        MailUtil.send(getResourceConfigProperty(FROM_ADDRESS_PROP_NAME), urBean.getEmail(), "[" + getRealm().getName() + "] Administrator has confirmed your registration request", body.toString());
                    }
                } else {
                    log.warn("Administrator key did not match!");
                }
            } else {
                if(activateRegistration(uuid, doc)) {
                    rootElement.appendChild(doc.createElementNS(NAMESPACE, "activation-successful"));
                } else {
                    rootElement.appendChild(doc.createElementNS(NAMESPACE, "activation-failed"));
                }
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
     * @param doc XML document containing response to client
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
                Element emailE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, EMAIL));
                emailE.appendChild(doc.createTextNode("" + email)); 
            }

        String password = null;
        boolean preAuthenticated = false;
        String preAuthReqHeaderName = getResourceConfigProperty("pre-auth-request-header");
        if (preAuthReqHeaderName != null && getEnvironment().getRequest().getHeader(preAuthReqHeaderName) != null) {
            String preAuthUserName = getEnvironment().getRequest().getHeader(preAuthReqHeaderName);
            preAuthenticated = true;
            log.warn("DEBUG: Pre authenticated user: " + preAuthUserName);
        } else {
            // INFO: Check password
            password = getEnvironment().getRequest().getParameter(PASSWORD);
            int minPwdLength = getMinPwdLength();
            int maxPwdLength = getMaxPwdLength();
            if (!isPasswordValid(password) || password.length() < minPwdLength || password.length() > maxPwdLength) {
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "password-not-valid"));
                log.error("Password not valid");
                inputsValid = false;
            }
            // INFO: Check password confirmed
            String confirmedPassword = getEnvironment().getRequest().getParameter(PASSWORD_CONFIRMED);
            if (password != null && confirmedPassword != null && !password.equals(confirmedPassword)) {
                log.warn("Passwords do not match!");
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "passwords-do-not-match"));
                inputsValid = false;
            }
        }

        // INFO: Check firstname
            String firstname = getEnvironment().getRequest().getParameter(FIRSTNAME);
            if (!isFirstnameValid(firstname)) {
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "firstname-not-valid"));
                inputsValid = false;
            } else {
                Element fnE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, FIRSTNAME));
                fnE.appendChild(doc.createTextNode("" + firstname)); 
            }

        // INFO: Check lastname
            String lastname = getEnvironment().getRequest().getParameter(LASTNAME);
            if (!isLastnameValid(lastname)) {
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "lastname-not-valid"));
                inputsValid = false;
            } else {
                Element fnE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, LASTNAME));
                fnE.appendChild(doc.createTextNode("" + lastname)); 
            }

        // INFO: Check gender (mandatory)
            String gender = isGenderValid(getEnvironment().getRequest().getParameter(SALUTATION)); // INFO: Please note that the gender is determined based on the salutation parameter
            if (gender == null) {
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "gender-not-valid"));
                inputsValid = false;
            } else {
                Element fnE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, GENDER));
                fnE.appendChild(doc.createTextNode("" + gender)); 
            }

        // INFO: Check company (optional)
            String company = isCompanyValid(getEnvironment().getRequest().getParameter(COMPANY));
            if (company != null && company.length() > 0) {
                Element fnE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, COMPANY));
                fnE.appendChild(doc.createTextNode("" + company)); 
            }

        // INFO: Check fax (optional)
            String fax = isFaxValid(getEnvironment().getRequest().getParameter("fax"));
            if (fax != null && fax.length() > 0) {
                Element fnE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "fax"));
                fnE.appendChild(doc.createTextNode("" + fax)); 
            }

        // INFO: Check street
        String street = getEnvironment().getRequest().getParameter(STREET);
        if (!isStreetValid(street)) {
            Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "street-not-valid"));
            log.warn("'" + STREET + "' not valid!");
            inputsValid = false;
        } else {
            Element fnE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, STREET));
            fnE.appendChild(doc.createTextNode("" + street)); 
        }

        // INFO: Check zip
        String zip = getEnvironment().getRequest().getParameter(ZIP);
        if (!isZipNotEmpty(zip)) {
            log.warn("ZIP '" + zip + "' is not valid!");
            Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "zip-not-valid"));
            inputsValid = false;
        } else {
            log.debug("Submitted ZIP: " + zip);
            String formattedZip = isZipValid(zip);
            if (formattedZip != null) {
                log.debug("Formatted ZIP: " + formattedZip);
                if (!zip.equals(formattedZip)) {
                    log.warn("Submitted zip code '" + zip + "' has been modified: " + formattedZip);
                }
                Element fnE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, ZIP));
                fnE.appendChild(doc.createTextNode(formattedZip)); 
            } else {
                log.warn("Format of ZIP '" + zip + "' is not valid!");
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "zip-not-valid"));
                inputsValid = false;
            }
        }

        // INFO: Check city
            String city = getEnvironment().getRequest().getParameter(CITY);
            if (!isCityValid(city)) {
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "city-not-valid"));
                log.warn("'" + CITY + "' not valid!");
                inputsValid = false;
            } else {
                Element fnE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "city"));
                fnE.appendChild(doc.createTextNode("" + city)); 
            }

        // INFO: Check phone
        String phone = getEnvironment().getRequest().getParameter(PHONE);
        if (!isPhoneValid(phone)) {
            Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "phone-not-valid"));
            log.warn("'" + PHONE + "' not valid!");
            inputsValid = false;
        } else {
            Element fnE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, PHONE));
            fnE.appendChild(doc.createTextNode("" + phone)); 
        }

        if (inputsValid) {
            UserRegistrationBean userRegBean = new UserRegistrationBean(gender, firstname, lastname, email, password, city, phone);
            userRegBean.setStreetName(street);
            userRegBean.setZipCode(zip);
            userRegBean.setPreAuthenticated(preAuthenticated);
            return userRegBean;
        } else {
            return null;
        }
    }

    /**
     * Get minimum password length
     */
    private int getMinPwdLength() throws Exception {
        String minPwdLengthSt = getResourceConfigProperty("min-password-length");
        if (minPwdLengthSt != null) {
            return new Integer(minPwdLengthSt).intValue();
        }
        int DEFAULT_MIN_PWD_LENGTH = 5;
        log.warn("No minimal password length configured, hence use default value: " + DEFAULT_MIN_PWD_LENGTH);
        return DEFAULT_MIN_PWD_LENGTH;
    }

    /**
     * Get maximum password length
     */
    private int getMaxPwdLength() throws Exception {
        String maxPwdLengthSt = getResourceConfigProperty("max-password-length");
        if (maxPwdLengthSt != null) {
            return new Integer(maxPwdLengthSt).intValue();
        }
        int DEFAULT_MAX_PWD_LENGTH = 15;
        log.warn("No maximum password length configured, hence use default value: " + DEFAULT_MAX_PWD_LENGTH);
        return DEFAULT_MAX_PWD_LENGTH;
    }

    /**
     * Add all submitted input parameters to response document such that they can be used for further processing (if necessary)
     * @param doc XML document containing response to client
     * @return DOM element containing submitted inputs
     */
    protected Element addSubmittedValuesToResponse(Document doc) throws Exception {
        Element submittedElem = (Element) doc.getDocumentElement().appendChild(doc.createElement("submitted-inputs"));

        Element emailElem = doc.createElementNS(NAMESPACE, EMAIL);
        emailElem.setTextContent(new InternetAddress(getEnvironment().getRequest().getParameter(EMAIL)).getAddress());
        submittedElem.appendChild(emailElem);

        Element lastnameElem = doc.createElementNS(NAMESPACE, LASTNAME);
        lastnameElem.setTextContent(getEnvironment().getRequest().getParameter(LASTNAME));
        submittedElem.appendChild(lastnameElem);

        Element firstnameElem = doc.createElementNS(NAMESPACE, FIRSTNAME);
        firstnameElem.setTextContent(getEnvironment().getRequest().getParameter(FIRSTNAME));
        submittedElem.appendChild(firstnameElem);

        Element streetElem = doc.createElementNS(NAMESPACE, STREET);
        streetElem.setTextContent(getEnvironment().getRequest().getParameter(STREET));
        submittedElem.appendChild(streetElem);

        Element cityElem = doc.createElementNS(NAMESPACE, CITY);
        cityElem.setTextContent(getEnvironment().getRequest().getParameter(CITY));
        submittedElem.appendChild(cityElem);

        Element zipElem = doc.createElementNS(NAMESPACE, ZIP);
        zipElem.setTextContent(getEnvironment().getRequest().getParameter(ZIP));
        submittedElem.appendChild(zipElem);

        Element companyElem = doc.createElementNS(NAMESPACE, COMPANY);
        companyElem.setTextContent(getEnvironment().getRequest().getParameter(COMPANY));
        submittedElem.appendChild(companyElem);

        Element phoneElem = doc.createElementNS(NAMESPACE, PHONE);
        phoneElem.setTextContent(getEnvironment().getRequest().getParameter(PHONE));
        submittedElem.appendChild(phoneElem);

        Element genderElem = doc.createElementNS(NAMESPACE, GENDER);
        genderElem.setTextContent(getEnvironment().getRequest().getParameter(SALUTATION));
        submittedElem.appendChild(genderElem);

        return submittedElem;
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
