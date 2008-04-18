/*
 * Copyright 2008 Wyona
 */

package org.wyona.yanel.impl.resources.image;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import org.apache.log4j.Logger;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;
import java.awt.geom.AffineTransform;
import javax.imageio.ImageIO;

/**
 *
 */
public class ImageResource extends Resource implements ViewableV2  {
    
    private static Logger log = Logger.getLogger(ImageResource.class);

    public boolean exists() throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    public long getSize() throws Exception {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     *
     */
    public View getView(String viewId) throws Exception {
        View view = new View();

        // TODO: Compare last modified!

        BufferedImage sourceImg = ImageIO.read(getRealm().getRepository().getNode(getPath()).getInputStream());
        int sourceWidth = sourceImg.getWidth();
        int sourceHeight = sourceImg.getHeight();
        if (log.isDebugEnabled()) log.debug("Source Width: " + sourceWidth + ", Source Height: " + sourceHeight);
        double sourceRatio = (double) sourceWidth / (double) sourceHeight;

        int destWidth = sourceWidth;
        //if (getEnvironment().getRequest().getParameter("width") != null) destWidth = new Integer(getEnvironment().getRequest().getParameter("width")).intValue();
        if (getResourceConfigProperty("width") != null) destWidth = new Integer(getResourceConfigProperty("width")).intValue();

        int destHeight = sourceHeight;
        //if (getEnvironment().getRequest().getParameter("height") != null) destHeight = new Integer(getEnvironment().getRequest().getParameter("height")).intValue();
        if (getResourceConfigProperty("height") != null) destHeight = new Integer(getResourceConfigProperty("height")).intValue();

        if (log.isDebugEnabled()) log.debug(" Dest Width: " + destWidth + ", Dest Height: " + destHeight);

        double destRatio = (double) destWidth / (double) destHeight;

        if (sourceRatio !=  destRatio) log.error("Source (" + sourceRatio + ") and destination (" + destRatio + ") width/height ratio are NOT the same!");

        double scaleFactor = (double) destHeight / (double) sourceHeight;
        if (log.isDebugEnabled()) log.debug("Scale factor: " + scaleFactor);

        String cacheRootPath = getResourceConfigProperty("cache-root-path");
        if (cacheRootPath == null) {
            cacheRootPath = "/cached-images";
            log.warn("No cache root path configured within resource configuration. Use default '" + cacheRootPath + "'!");
        }
        org.wyona.yarep.core.Node cacheNode = org.wyona.yarep.util.YarepUtil.addNodes(getRealm().getRepository(), cacheRootPath + getPath(), org.wyona.yarep.core.NodeType.RESOURCE);

        ImageIO.write(scale(sourceImg, scaleFactor), "JPG", cacheNode.getOutputStream());

        view.setInputStream(cacheNode.getInputStream());
        view.setMimeType("image/jpeg");
        return view;
    }

    public ViewDescriptor[] getViewDescriptors() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Also see http://forum.java.sun.com/thread.jspa?threadID=530573&messageID=2555124
     */
    private static BufferedImage scale(BufferedImage orig, double scale) {
        if (scale == 1.0) return orig; // same size ?
                                                               
        AffineTransform resizeDefinition = AffineTransform.getScaleInstance(scale, scale);
        AffineTransformOp resizer = new AffineTransformOp(resizeDefinition, AffineTransformOp.TYPE_BILINEAR);
        int width = (int) (orig.getWidth() * scale);
        int height = (int) (orig.getHeight() * scale);
        BufferedImage result = new BufferedImage(width, height, orig.getType());
        resizer.filter(orig, result);
        return result;
    }
}
