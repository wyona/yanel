/*
 * Copyright 2010 Wyona
 */

package org.wyona.yanel.resources.registration;

import org.wyona.yanel.impl.resources.BasicXMLResource;
//import org.wyona.yanel.resources.konakart.shared.SharedResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

/*
import com.konakart.appif.CustomerRegistrationIf;
import com.konakart.appif.KKEngIf;
*/

/**
 * A simple Resource which extends BasicXMLResource
 */
public class KonakartRegistrationSOAPInfResource extends BasicXMLResource {
    
    private static Logger log = Logger.getLogger(KonakartRegistrationSOAPInfResource.class);

    private static String KONAKART_NAMESPACE = "http://www.konakart.com/1.0";
    
    /**
     * This method overrides the method to create the InputStream called by BasicXMLResource
     * Since you extend the BasicXMLResource this has to contain well-formed xml.
     * Should return a InputStream which contains XML. 
     * Use String, StingBuffer, dom, jdom, org.apache.commons.io.IOUtils and so on to generate the XML.
     */
    protected InputStream getContentXML(String viewId) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }

/*
        SharedResource shared = new SharedResource();
        KKEngIf kkEngine = shared.getKonakartEngineImpl();
*/

        // Build document
        org.w3c.dom.Document doc = null;
        try {
            doc = org.wyona.commons.xml.XMLHelper.createDocument(KONAKART_NAMESPACE, "registration");
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }

        // Root element
        Element rootElement = doc.getDocumentElement();

        String email = getEnvironment().getRequest().getParameter("email");
        if (email != null) {
            boolean inputsValid = true;
            if (!isEmailValid(email)) {
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "email-not-valid"));
                inputsValid = false;
            } else {
/*
                if(kkEngine.doesCustomerExistForEmail(email)) {
                    Element exception = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "email-in-use"));
                    inputsValid = false;
                } 
*/
                Element emailE = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "email"));
                emailE.appendChild(doc.createTextNode("" + email)); 
            }

            String password = getEnvironment().getRequest().getParameter("password");
            if (!isPasswordValid(password) || password.length() < 5) {
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "password-not-valid"));
                inputsValid = false;
            }
            String confirmedPassword = getEnvironment().getRequest().getParameter("password2");
            if (password != null && confirmedPassword != null && !password.equals(confirmedPassword)) {
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "passwords-do-not-match"));
                inputsValid = false;
            }

            String firstname = getEnvironment().getRequest().getParameter("firstname");
            if (!isFirstnameValid(firstname)) {
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "firstname-not-valid"));
                inputsValid = false;
            } else {
                Element fnE = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "firstname"));
                fnE.appendChild(doc.createTextNode("" + firstname)); 
            }

            String lastname = getEnvironment().getRequest().getParameter("lastname");
            if (!isLastnameValid(lastname)) {
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "lastname-not-valid"));
                inputsValid = false;
            } else {
                Element fnE = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "lastname"));
                fnE.appendChild(doc.createTextNode("" + lastname)); 
            }

            String gender = isGenderValid(getEnvironment().getRequest().getParameter("salutation"));
            if (gender == null) {
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "gender-not-valid"));
                inputsValid = false;
            } else {
                Element fnE = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "gender"));
                fnE.appendChild(doc.createTextNode("" + gender)); 
            }

            String company = isCompanyValid(getEnvironment().getRequest().getParameter("company"));
            if (company != null && company.length() > 0) {
                Element fnE = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "company"));
                fnE.appendChild(doc.createTextNode("" + company)); 
            }

            String fax = isFaxValid(getEnvironment().getRequest().getParameter("fax"));
            if (fax != null && fax.length() > 0) {
                Element fnE = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "fax"));
                fnE.appendChild(doc.createTextNode("" + fax)); 
            }

            String street = getEnvironment().getRequest().getParameter("street");
            if (!isStreetValid(street)) {
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "street-not-valid"));
                inputsValid = false;
            } else {
                Element fnE = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "street"));
                fnE.appendChild(doc.createTextNode("" + street)); 
            }

            String zip = getEnvironment().getRequest().getParameter("zip");
            if (!isZipValid(zip)) {
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "zip-not-valid"));
                inputsValid = false;
            } else {
                Pattern pzip = Pattern.compile("[1-9][0-9]{3}");
                Matcher mzip = pzip.matcher(zip);
                if(mzip.find()) {
                    zip = mzip.group(0);
                    Element fnE = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "zip"));
                    fnE.appendChild(doc.createTextNode("" + mzip.group(0))); 
                } else {
                    Element exception = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "zip-not-valid"));
                    inputsValid = false;
                }
            }

            String city = getEnvironment().getRequest().getParameter("location");
            if (!isCityValid(city)) {
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "city-not-valid"));
                inputsValid = false;
            } else {
                Element fnE = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "city"));
                fnE.appendChild(doc.createTextNode("" + city)); 
            }

            String phone = getEnvironment().getRequest().getParameter("phone");
            if (!isPhoneValid(phone)) {
                Element exception = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "phone-not-valid"));
                inputsValid = false;
            } else {
                Element fnE = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "phone"));
                fnE.appendChild(doc.createTextNode("" + phone)); 
            }

            if (inputsValid) {
                Element valildE = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "all-inputs-valid"));
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


                try {
                    // INFO: KonaKart registration
                    //int customerID = kkEngine.registerCustomer(cr);
                    long customerID = new java.util.Date().getTime();

                    // INFO: Yanel registration
                    org.wyona.security.core.api.User user = getRealm().getIdentityManager().getUserManager().createUser("" + customerID, firstname + " " + lastname, email, password);
                    org.wyona.security.core.api.User alias = getRealm().getIdentityManager().getUserManager().createAlias(email, "" + customerID);

                    Element ncE = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "new-customer-registered"));
                    ncE.setAttributeNS(KONAKART_NAMESPACE, "id", "" + customerID);

                    javax.servlet.http.HttpSession httpSession = getEnvironment().getRequest().getSession(true);

                    // Login
/*
                    String konakartSessionID = shared.login(email, password, getRealm(), httpSession);
                    if (konakartSessionID != null && konakartSessionID.length() > 0) {
                        httpSession.setAttribute(shared.KONAKART_SESSION_ID, konakartSessionID);
                        Element succE = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "login-successful"));
                        succE.setAttributeNS(KONAKART_NAMESPACE, "username", "" + email);
                        // TODO: Copy/paste shopping cart (???)
                    } else {
                        Element errE = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "login-failed"));
                        errE.setAttributeNS(KONAKART_NAMESPACE, "username", "" + email);
                        log.error("Login failed for new user: " + email);
                    }
*/
/*
                } catch(com.konakart.app.KKUserExistsException e) { // WARN: It seems that KonaKart is using nested exceptions and hence this one is not caught!
                    log.warn(e.getMessage());
                    Element fnE = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "user-already-exists"));
                    fnE.appendChild(doc.createTextNode("" + e.getMessage())); 
*/
                } catch(Exception e) {
                    log.error(e, e);
                    Element fnE = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "registration-failed"));
                    fnE.appendChild(doc.createTextNode("" + e.getMessage())); 
                }
            } else {
                Element invalidE = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "one-or-more-inputs-not-valid"));
            }
        } else {
            Element invalidE = (Element) rootElement.appendChild(doc.createElementNS(KONAKART_NAMESPACE, "no-input-yet"));
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
}
