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
    
    /**
     * @see org.wyona.yanel.impl.resources.BasicXMLResource#getContentXML(String)
     */
    @Override
    protected InputStream getContentXML(String viewId) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }

/*
        SharedResource shared = new SharedResource();
        KKEngIf kkEngine = shared.getKonakartEngineImpl();
*/

        // INFO: Build response document
        Document doc = null;
        try {
            doc = org.wyona.commons.xml.XMLHelper.createDocument(NAMESPACE, "registration");
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }

        // Root element
        Element rootElement = doc.getDocumentElement();

        String email = getEnvironment().getRequest().getParameter("email");
        String uuid = getEnvironment().getRequest().getParameter("uuid");
        if (email != null) {
            processRegistrationRequest(doc, email);
        } else if (uuid != null) {
            if(activateRegistration(uuid, doc)) {
                Element activateSuccessfulE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "activation-successful"));
            } else {
                Element activationFailedE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "activation-failed"));
            }
        } else {
            Element invalidE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "no-input-yet"));
        }
 
        java.io.ByteArrayOutputStream baout = new java.io.ByteArrayOutputStream();
        org.wyona.commons.xml.XMLHelper.writeDocument(doc, baout);
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
     */
    private void sendConfirmationLinkEmail(Document doc, String uuid, String firstame, String lastname, String email) {
        log.warn("DEBUG: Do not register user right away, but send an email containing a confirmation link...");
        Element rootElement = doc.getDocumentElement();

        try {
            String from = "no-reply@wyona.com";
            MailUtil.send(from, email, "Activate User Registration", getActivationURL() + "?uuid=" + uuid);
            Element element = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "confirmation-link-email-sent"));
            element.setAttribute("hours-valid", "" + DEFAULT_TOTAL_VALID_HRS);
        } catch(Exception e) {
            log.error(e, e);
            Element element = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "confirmation-link-email-not-sent"));
            element.setAttribute("exception-message", e.getMessage());
        }
    }

    /**
     * Register user
     */
    private void registerUser(Document doc, String firstname, String lastname, String email, String password) {
        Element rootElement = doc.getDocumentElement();

        try {
            // INFO: KonaKart registration
            //int customerID = kkEngine.registerCustomer(cr);
            long customerID = new java.util.Date().getTime();

            // INFO: Yanel registration
            org.wyona.security.core.api.User user = getRealm().getIdentityManager().getUserManager().createUser("" + customerID, firstname + " " + lastname, email, password);
            org.wyona.security.core.api.User alias = getRealm().getIdentityManager().getUserManager().createAlias(email, "" + customerID);
            // TODO: Move adding to groups into separated method
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
                        getRealm().getIdentityManager().getGroupManager().getGroup(groupIDs[i]).addMember(user);
                    } else {
                        log.warn("No such group: " + groupIDs[i]);
                    }
                }
            }

            Element ncE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "new-customer-registered"));
            ncE.setAttributeNS(NAMESPACE, "id", "" + customerID);

/*
            // Login
            javax.servlet.http.HttpSession httpSession = getEnvironment().getRequest().getSession(true);
            String konakartSessionID = shared.login(email, password, getRealm(), httpSession);
            if (konakartSessionID != null && konakartSessionID.length() > 0) {
                httpSession.setAttribute(shared.KONAKART_SESSION_ID, konakartSessionID);
                Element succE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "login-successful"));
                succE.setAttributeNS(NAMESPACE, "username", "" + email);
                // TODO: Copy/paste shopping cart (???)
            } else {
                Element errE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "login-failed"));
                errE.setAttributeNS(NAMESPACE, "username", "" + email);
                log.error("Login failed for new user: " + email);
            }
*/

/*
        } catch(com.konakart.app.KKUserExistsException e) { // WARN: It seems that KonaKart is using nested exceptions and hence this one is not caught!
            log.warn(e.getMessage());
            Element fnE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "user-already-exists"));
            fnE.appendChild(doc.createTextNode("" + e.getMessage())); 
*/
        } catch(Exception e) {
            log.error(e, e);
            Element fnE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "registration-failed"));
            fnE.appendChild(doc.createTextNode("" + e.getMessage())); 
        }
    }

    /**
     * Save registration request persistently
     * @param email E-Mail address of user
     */
    private void saveRegistrationRequest(String uuid, String firstname, String lastname, String email, String city, String phone, String password) {
        Document doc = getRegistrationRequestAsXML(uuid, firstname, lastname, email, city, phone, password);
        Node node = null;
        try {
            String path = getActivationNodePath(uuid);
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
     * @param email E-Mail address of user
     */
    private Document getRegistrationRequestAsXML(String uuid, String firstname, String lastname, String email, String city, String phone, String password) {
        Document doc = XMLHelper.createDocument(NAMESPACE, "registration-request");
        Element rootElem = doc.getDocumentElement();
        rootElem.setAttribute("uuid", uuid);

        DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        rootElem.setAttribute("request-time", df.format(new Date().getTime()));

        // IMPORTANT TODO: Password needs to be encrypted!
        Element passwordElem = doc.createElementNS(NAMESPACE, "password");
        passwordElem.setTextContent(password);
        rootElem.appendChild(passwordElem);

        Element lastnameElem = doc.createElementNS(NAMESPACE, "lastname");
        lastnameElem.setTextContent(lastname);
        rootElem.appendChild(lastnameElem);

        Element firstnameElem = doc.createElementNS(NAMESPACE, "firstname");
        firstnameElem.setTextContent(firstname);
        rootElem.appendChild(firstnameElem);

        Element emailElem = doc.createElementNS(NAMESPACE, "email");
        emailElem.setTextContent(email);
        rootElem.appendChild(emailElem);

        return doc;
    }

    /**
     * Get activation URL which will be sent via E-Mail (also see YanelServlet#getRequestURLQS(HttpServletRequest, String, boolean))
     */
    public String getActivationURL() throws Exception {
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
        return url.toString();
    }

    /**
     *
     */
    private void processRegistrationRequest(Document doc, String email) throws Exception {
        Element rootElement = doc.getDocumentElement();
            boolean inputsValid = true;
            if (!isEmailValid(email)) {
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "email-not-valid"));
                inputsValid = false;
            } else {
/*
                if(kkEngine.doesCustomerExistForEmail(email)) {
                    Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "email-in-use"));
                    inputsValid = false;
                } 
*/
                Element emailE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "email"));
                emailE.appendChild(doc.createTextNode("" + email)); 
            }

            String password = getEnvironment().getRequest().getParameter("password");
            if (!isPasswordValid(password) || password.length() < 5) {
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "password-not-valid"));
                inputsValid = false;
            }
            String confirmedPassword = getEnvironment().getRequest().getParameter("password2");
            if (password != null && confirmedPassword != null && !password.equals(confirmedPassword)) {
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "passwords-do-not-match"));
                inputsValid = false;
            }

            String firstname = getEnvironment().getRequest().getParameter("firstname");
            if (!isFirstnameValid(firstname)) {
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "firstname-not-valid"));
                inputsValid = false;
            } else {
                Element fnE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "firstname"));
                fnE.appendChild(doc.createTextNode("" + firstname)); 
            }

            String lastname = getEnvironment().getRequest().getParameter("lastname");
            if (!isLastnameValid(lastname)) {
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "lastname-not-valid"));
                inputsValid = false;
            } else {
                Element fnE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "lastname"));
                fnE.appendChild(doc.createTextNode("" + lastname)); 
            }

            String gender = isGenderValid(getEnvironment().getRequest().getParameter("salutation"));
            if (gender == null) {
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "gender-not-valid"));
                inputsValid = false;
            } else {
                Element fnE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "gender"));
                fnE.appendChild(doc.createTextNode("" + gender)); 
            }

            String company = isCompanyValid(getEnvironment().getRequest().getParameter("company"));
            if (company != null && company.length() > 0) {
                Element fnE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "company"));
                fnE.appendChild(doc.createTextNode("" + company)); 
            }

            String fax = isFaxValid(getEnvironment().getRequest().getParameter("fax"));
            if (fax != null && fax.length() > 0) {
                Element fnE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "fax"));
                fnE.appendChild(doc.createTextNode("" + fax)); 
            }

            String street = getEnvironment().getRequest().getParameter("street");
            if (!isStreetValid(street)) {
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "street-not-valid"));
                inputsValid = false;
            } else {
                Element fnE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "street"));
                fnE.appendChild(doc.createTextNode("" + street)); 
            }

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

            String city = getEnvironment().getRequest().getParameter("location");
            if (!isCityValid(city)) {
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "city-not-valid"));
                inputsValid = false;
            } else {
                Element fnE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "city"));
                fnE.appendChild(doc.createTextNode("" + city)); 
            }

            String phone = getEnvironment().getRequest().getParameter("phone");
            if (!isPhoneValid(phone)) {
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "phone-not-valid"));
                inputsValid = false;
            } else {
                Element fnE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "phone"));
                fnE.appendChild(doc.createTextNode("" + phone)); 
            }

            if (inputsValid) {
                Element valildE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "all-inputs-valid"));
/*
                CustomerRegistrationIf cr = new com.konakart.app.CustomerRegistration();
                cr.setEmailAddr(email);
                cr.setPassword(password);
                cr.setFirstName(firstname);
                cr.setLastName(lastname);
                cr.setGender(gender);
                cr.setBirthDate(new java.util.GregorianCalendar(1992, 1, 1, 0, 0)); // INFO: No birthday necessary, hence invent something
                cr.setTelephoneNumber(phone);
                if (fax != null) {
                    cr.setFaxNumber(fax);
                }
                cr.setStreetAddress(street);
                cr.setCity(city);
                cr.setPostcode(zip);
                cr.setState(getResourceConfigProperty("default-zone"));
                com.konakart.appif.CountryIf cn = kkEngine.getCountryPerName("Switzerland");
                if(cn == null) {
                    com.konakart.appif.CountryIf[] cns = kkEngine.getAllCountries(); // We use the first country in the database.
                    cr.setCountryId(cns[0].getId());
                } else {
                    cr.setCountryId(cn.getId());
                }
                if (company != null) {
                    cr.setCompany(company);
                }
*/

                boolean emailConfigurationRequired = true;
                if (getResourceConfigProperty("email-confirmation") != null) {
                    emailConfigurationRequired = new Boolean(getResourceConfigProperty("email-confirmation")).booleanValue();
                }
                if (!emailConfigurationRequired) {
                    log.warn("User will be registered without email configuration!");
                    registerUser(doc, firstname, lastname, email, password);
                } else {
                    String uuid = java.util.UUID.randomUUID().toString();
                    saveRegistrationRequest(uuid, firstname, lastname, email, city, phone, password);
                    sendConfirmationLinkEmail(doc, uuid, firstname, lastname, email);
                }
            } else {
                Element invalidE = (Element) rootElement.appendChild(doc.createElementNS(NAMESPACE, "one-or-more-inputs-not-valid"));
            }
    }

    /**
     * Try to activate user registration
     * @param uuid UUID of user registration activation request
     */
    private boolean activateRegistration(String uuid, Document doc) {
        try {
            String path = getActivationNodePath(uuid);
            if (getRealm().getRepository().existsNode(path)) {

                UserRegistrationBean urBean = readRegistrationRequest(getRealm().getRepository().getNode(path));

                registerUser(doc, urBean.getFirstname(), urBean.getLastname(), urBean.getEmail(), urBean.getPassword());
                getRealm().getRepository().getNode(path).delete();
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
        String firstname = (String) xpath.evaluate("/ur:registration-request/ur:firstname", doc, XPathConstants.STRING);
        String lastname = (String) xpath.evaluate("/ur:registration-request/ur:lastname", doc, XPathConstants.STRING);
        String email = (String) xpath.evaluate("/ur:registration-request/ur:email", doc, XPathConstants.STRING);
        String password = (String) xpath.evaluate("/ur:registration-request/ur:password", doc, XPathConstants.STRING);
        UserRegistrationBean urBean = new UserRegistrationBean(firstname, lastname, email, password);
        return urBean;
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
