package org.wyona.yanel.servlet.security.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wyona.commons.xml.XMLHelper;
import org.wyona.yanel.core.Environment;
import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.core.util.YarepUtil;
import org.wyona.yarep.core.Node;
import org.wyona.yarep.core.Repository;

/**
 * Utility class in order to support auto login
 */
public class AutoLogin {
    
    private static Logger log = Logger.getLogger(AutoLogin.class);
    private static final String COOKIE_NAME = "YANELAUTOLOGIN";
    private static final String SEP = "___";
    
    private static final String NAMESPACE = "http://www.wyona.org/yanel/1.0";
    private static final String BASEDIR = "/autologin-tokens/";
    private static final String EXPIRES_FORMAT = "yyyyMMdd.HH.mm.ss";
    private static final SimpleDateFormat expiresSdf = new SimpleDateFormat(EXPIRES_FORMAT);
    private static final String XML_ATTR_USERNAME = "username";
    private static final String XML_ATTR_EXPIRES = "expires";
    private static final String XML_ATTR_TOKEN = "token";
    
    // With the following two parameters you can define after what time a token gets replaced.
    // However: this expiry date is only verified and maybe replaced if the user starts a new session.
    // Means: if the session timeout is 4h and you configure here 30min, the cookie token won't be replaced within the session.
    private static final int TOKEN_EXPIRY_UNIT = Calendar.MINUTE;
    private static final int TOKEN_EXPIRY_AMOUNT = 30;

    /**
     * Disable Auto Login feature (deletes cookie)
     */
    public static void disableAutoLogin(HttpServletRequest request, HttpServletResponse response, Repository repo) {
        Cookie currentCookie = getCookie(request);
        if (currentCookie != null) {
            deleteToken(repo, getYarepPath(getUsername(currentCookie), getToken(currentCookie)));

            log.warn("DEBUG: Remove auto login cookie...");
            Cookie newCookie = new Cookie(COOKIE_NAME, null);
            newCookie.setMaxAge(0); // INFO: A zero value tells the browser to delete the cookie immediately.
            response.addCookie(newCookie);
        } else {
            log.warn("No auto login cookie to delete!");
        }
    }
 
    /**
     *
     */
    public static void enableAutoLogin(String username, HttpServletResponse response, Realm realm) {
        try {
            //set cookie
            Cookie cookie = setNewCookie(username, response);
            //save token
            saveToken(cookie, realm.getRepository());
        } catch (Exception e) {
            log.fatal("Could not enable Auto Login feature! Exception: "+e,e);
        }
    }
    
    public static String getUsername(HttpServletRequest request) {
        return getUsername(getCookie(request));
    }

    /**
     * This method checks whether the current user should get logged in automatically based on an existing auto login cookie
     * @param request HTTP request
     * @return true means that this user can be logged in automatically.
     */
    public static boolean existsAutoLoginCookie(HttpServletRequest request) {
        try {
            Cookie cookie = getCookie(request);
            if (cookie != null) {
                return true;
            } else {
                //log.debug("No auto login cookie exists yet.");
                return false;
            }
        } catch (Exception e) {
            log.error("Can not find out whether to perform auto login or not! Exception message : " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * This method checks whether the current user should get logged in automatically based on the cookie information and the persisted token and if so, then does the auto login
     * @param request
     * @param response
     * @param realm
     * @return true means that this user can be logged in automatically.
     */
    public static boolean tryAutoLogin(HttpServletRequest request, HttpServletResponse response, Realm realm) {
        try {
            Cookie cookie = getCookie(request);
            if (cookie != null) {
                log.debug("Checking Autologin Cookie");
                String username = getUsername(cookie);
                String token = getToken(cookie);
                if (username != null && token != null) {
                    String yarepPath = getYarepPath(username, token);
                    log.debug("Checking node "+yarepPath);
                    Node node = null;
                    try {
                        node = realm.getRepository().getNode(yarepPath);
                    } catch (Exception e) {
                        log.warn("node '"+yarepPath+"' does not exist");
                        log.warn("We did not login the user automatically although auto login cookie seems to exist!");
                        return false;
                    }

                    if (node != null) {
                        Document doc = XMLHelper.readDocument(node.getInputStream());
                        Element el = (Element)doc.getElementsByTagNameNS(NAMESPACE, XML_ATTR_TOKEN).item(0);
                        String savedUsername = el.getAttribute(XML_ATTR_USERNAME);
                        String savedToken = el.getAttribute("value");
                        String expiryString = el.getAttribute(XML_ATTR_EXPIRES);
                        log.debug("Retrieved username '"+username+"' and token '"+savedToken+"' from saved Token");
                        if (username.equals(savedUsername) && token.equals(savedToken)) {
                            log.debug("retrieved cookie matches for user '"+username+"'");
                            if (hasTokenExpired(expiryString)) {
                                Cookie newCookie = setNewCookie(username, response);
                                saveToken(newCookie, realm.getRepository());
                                deleteToken(realm.getRepository(), yarepPath);
                                log.debug("Token was expired and has been renewed now.");
                            }
                            return true;
                        } else {
                            log.warn("Username/Token did no match (" + username + ", " + token + "), hence about auto login");
                            return false;
                        }
                    } else {
                        log.warn("No persistent yarep node containing username/token!");
                        return false;
                    }
                } else {
                    log.warn("No username/token inside auto login cookie!");
                    return false;
                }
            } else {
                //log.debug("No auto login cookie exists yet.");
                return false;
            }
        } catch (Exception e) {
            log.error("Can not find out whether to perform auto login or not! Exception message: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Set cookie in response
     */
    private static Cookie setNewCookie(String username, HttpServletResponse response) {
        Cookie result = null;
        if (username != null) {
            String token = UUID.randomUUID().toString();
            Cookie cookie = new Cookie(COOKIE_NAME,token+SEP+username);
            cookie.setMaxAge(Integer.MAX_VALUE);
            cookie.setPath("/");
            response.addCookie(cookie);
            result = cookie;
        }
        return result;
    }

    /**
     * Get specific auto login cookie
     */
    private static Cookie getCookie(HttpServletRequest request) {
        Cookie result = null;
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie c : request.getCookies()) {
                    if (c.getName().equals(COOKIE_NAME)) {
                        result = c;
                        break;
                    }
                }
            }
            
        } catch (Exception e) {
            log.error("Error in retrieving cookie from request");
            log.error(e,e);
        }
        
        return result;
    }
 
    /**
     * Get username from cookie
     * @param cookie Auto login cookie
     */
    private static String getUsername(Cookie cookie) {
        String result = null;
        if (cookie != null) {
            try {
                result = cookie.getValue();
                result = result.substring(result.lastIndexOf(SEP)+SEP.length());
            } catch (Exception e) {
                log.error("Can not extract username from cookie with name '"+cookie.getName()+"' and value '"+cookie.getValue()+"'");
                log.error(e,e);
            }
        }
        return result;
    }

    private static String getToken(Cookie cookie) {
        String result = null;
        if (cookie != null) {
            try {
                result = cookie.getValue();
                result = result.substring(0, result.lastIndexOf(SEP));
            } catch (Exception e) {
                log.error("Can not extract token from cookie with name '"+cookie.getName()+"' and value '"+cookie.getValue()+"'");
                log.error(e,e);
            }
        }
        return result;
    }

    /**
     * Save auto login token persistently
     */
    private static void saveToken(Cookie cookie, Repository repo) {
        String username = getUsername(cookie);
        String token = getToken(cookie);
        if (username != null && token != null) {
            String yarepPath = getYarepPath(username, token);
            Document doc = XMLHelper.createDocument(NAMESPACE, "root");
            Element el = doc.createElementNS(NAMESPACE, XML_ATTR_TOKEN);
            el.setAttribute("value", token);
            el.setAttribute(XML_ATTR_USERNAME, username);
            el.setAttribute(XML_ATTR_EXPIRES, getExpiryString());
            doc.getDocumentElement().appendChild(el);
            
            // try to delete node if it exists
            deleteToken(repo, yarepPath);
            
            // save the token
            try {
                Node node = YarepUtil.addNodes(repo, yarepPath, org.wyona.yarep.core.NodeType.RESOURCE);
                XMLHelper.writeDocument(doc, node.getOutputStream());
                log.debug("Autologin Token saved at "+yarepPath);
            } catch (Exception e) {
                log.error("Could not save token for Auto-Login. Exception: "+e,e);
            }
        }
    }
    
    private static String getExpiryString() {
        String result = null;
        try {
            Calendar cal = Calendar.getInstance();
            cal.add(TOKEN_EXPIRY_UNIT, TOKEN_EXPIRY_AMOUNT);
            result = expiresSdf.format(cal.getTime());
            
        } catch (Exception e) {
            log.error("Can not calculate expiry date! Exception: "+e,e);
        }
        return result;
    }
    
    private static boolean hasTokenExpired(String expiryString) {
        boolean result = true;
        try {
            if (expiryString != null) {
                Date expiryDate = expiresSdf.parse(expiryString);
                if (expiryDate.before(new Date())) {
                    result = true;
                } else {
                    result = false;
                }
            }
        } catch (Exception e) {
            log.error("Can not parse the expirydate '"+expiryString+"' from the token XML! Exception: "+e,e);
        }
        return result;
    }

    private static void deleteToken(Repository repo, String yarepPath) {
        try {
            if (repo.existsNode(yarepPath)) {
                repo.getNode(yarepPath).delete();
            }
        } catch (Exception e) {
            // we could log an exception here but deletion is not that important, so we skip the logging.
        }
    }

    private static String getYarepPath(String username, String tokenID) {
        return BASEDIR+username+"-tokens/"+tokenID+".xml";
    }
}
