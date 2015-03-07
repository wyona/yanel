/*
 * Copyright 2011 Wyona
 */

package org.wyona.yanel.impl.resources.image;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import org.wyona.yarep.core.Node;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;
import java.awt.geom.AffineTransform;
import javax.imageio.ImageIO;

/**
 * Resource to scale (down) images if mobile view is requested (but only jpeg so far)
 */
public class MobileImageResource extends ImageResource  {
    
    private static Logger log = LogManager.getLogger(MobileImageResource.class);

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#getView(String)
     */
    @Override
    public View getView(String viewId) throws Exception {
        if (isMobileView()) {
            BufferedImage sourceImg = ImageIO.read(getRealm().getRepository().getNode(getPath()).getInputStream());
            int sourceWidth = sourceImg.getWidth();
            if (sourceWidth > 300) { // TODO: Make limit of 300 configurable
                //log.debug("Scale image for mobile view...");
                return super.getView(viewId);
            } else {
                //log.debug("Get original image, because original image width <= 300 (" + sourceWidth + ")");
                View view = new View();
                view.setInputStream(getRealm().getRepository().getNode(getPath()).getInputStream());
                view.setMimeType("image/jpeg");
                return view;
            }
        } else {
            //log.debug("Get original image, because mobile view not requested...");
            View view = new View();
            view.setInputStream(getRealm().getRepository().getNode(getPath()).getInputStream());
            view.setMimeType("image/jpeg");
            return view;
        }
    }

    /**
     * Check whether mobile view is requested. Please overwrite this method in case there exist other rules than just being a mobile device.
     */
    private boolean isMobileView() {
        String VIEW_ID_PARAM_NAME = "yanel.resource.viewid";
        try {
/*
            String isMobileViewParamValue = getResourceConfigProperty("is-mobile-view");
            if (isMobileViewParamValue != null && isMobileViewParamValue.equals("false")) { // INFO: In some cases one wants to disable the mobile view completely (including mobile CSS, if applicable)
                return false;
            }
*/

            String viewIdFromSession = (String) request.getSession(true).getAttribute(VIEW_ID_PARAM_NAME);
            if (viewIdFromSession != null && !viewIdFromSession.equals("mobile")) {
                log.warn("It seems like the view id is set inside session, but not set to mobile: " + viewIdFromSession);
                return false;
            }
        } catch(Exception e) {
            log.error(e, e);
        }
        return isMobileDevice();
    }

    /**
     * Check whether user agent is a mobile device
     */
    protected boolean isMobileDevice() {
        javax.servlet.http.HttpSession session = getEnvironment().getRequest().getSession(true);
        if (session != null) {
            String mobileDevice = (String) session.getAttribute("yanel.mobile");
            //TODO: String mobileDevice = (String) getEnvironment().getRequest().getSession(true).getAttribute(org.wyona.yanel.servlet.YanelServlet.MOBILE_KEY);
            if (mobileDevice != null && !mobileDevice.equals("false")) {
                return true;
            } else {
                return false;
            }
        } else {
            log.warn("No HTTP session available (maybe because Yanel is used via the command line or some custom junit tests do not provide session handling)!");
            return false;
        }
    }
}
