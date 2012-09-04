/*
 * Copyright 2008 Wyona
 */

package org.wyona.yanel.impl.resources.image;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import org.wyona.yarep.core.Node;

import org.apache.log4j.Logger;

import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;
import java.awt.geom.AffineTransform;
import javax.imageio.ImageIO;

/**
 * Resource to scale (down) images (but only jpeg so far)
 */
public class ImageResource extends Resource implements ViewableV2  {
    
    private static Logger log = Logger.getLogger(ImageResource.class);

    private String DEFAULT_CACHE_DIRECTORY = "/cached-images";

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#exists()
     */
    public boolean exists() throws Exception {
        return getRealm().getRepository().existsNode(getPath());
    }

    public long getSize() throws Exception {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#getView(String)
     */
    public View getView(String viewId) throws Exception {
        if (!exists()) {
            throw new org.wyona.yanel.core.ResourceNotFoundException("No such image: " + getPath());
        }

        BufferedImage sourceImg = ImageIO.read(getRealm().getRepository().getNode(getPath()).getInputStream());
        int sourceWidth = sourceImg.getWidth();
        int sourceHeight = sourceImg.getHeight();
        if (log.isDebugEnabled()) log.debug("Source Width: " + sourceWidth + ", Source Height: " + sourceHeight);

        int destWidth = getDestWidth();
        int destHeight = getDestHeight();

        if (log.isDebugEnabled()) log.debug(" Dest Width: " + destWidth + ", Dest Height: " + destHeight);

        double scaleFactor = getScaleFactor(sourceWidth, sourceHeight, destWidth, destHeight); //(double) destHeight / (double) sourceHeight;
        if (log.isDebugEnabled()) log.debug("Scale factor: " + scaleFactor);

        Node cacheNode;
        if (existsMoreRecentCacheNode()) {
            //log.debug("Get image from cache...");
            cacheNode = getCacheNode();
        } else {
            log.debug("Scale image and add scaled version to cache...");
            cacheNode = createCacheNode();
            ImageIO.write(scale(sourceImg, scaleFactor), "JPG", cacheNode.getOutputStream());
        }

        View view = new View();
        view.setInputStream(cacheNode.getInputStream());
        view.setMimeType("image/jpeg");
        return view;
    }

    /**
     * @see org.wyona.yanel.core.api.attributes.ViewableV2#getViewDescriptors()
     */
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

    /**
     * Get destination width
     */
    private int getDestWidth() throws Exception {
        // TODO: Get destination width from query string: getEnvironment().getRequest().getParameter("width")
        if (getResourceConfigProperty("width") != null) {
            return new Integer(getResourceConfigProperty("width")).intValue();
        } else {
            log.debug("No destination width configured");
            if (getResourceConfigProperty("height") != null) {
                return -1;
            } else {
                throw new Exception("Neither height nor width configured!");
            }
        }
    }

    /**
     * Get destination height
     */
    private int getDestHeight() throws Exception {
        // TODO: Get destination height from query string: getEnvironment().getRequest().getParameter("height")
        if (getResourceConfigProperty("height") != null) {
            return new Integer(getResourceConfigProperty("height")).intValue();
        } else {
            log.debug("No destination height configured");
            if (getResourceConfigProperty("width") != null) {
                return -1;
            } else {
                throw new Exception("Neither height nor width configured!");
            }
        }
    }

    /**
     * Get scale factor
     */
    private double getScaleFactor(double sourceWidth, double sourceHeight, double destWidth, double destHeight) {
        if (destWidth > 0 && destHeight <= 0) {
            log.debug("Use width to calculate scale factor");
            return (double) destWidth / (double) sourceWidth;
        } else if (destWidth <= 0 && destHeight > 0) {
            log.debug("Use height to calculate scale factor");
            return (double) destHeight / (double) sourceHeight;
        } else if (destWidth <= 0 && destHeight <= 0) {
            log.error("Neither destination width nor height make sense to calculate scale factor, hence do not scale!");
            return 1;
        } else if (destWidth > 0 && destHeight > 0) {
            log.warn("Width or height could be used, hence use height to calculate scale factor");

            double destRatio = (double) destWidth / (double) destHeight;
            double sourceRatio = (double) sourceWidth / (double) sourceHeight;
            if (sourceRatio !=  destRatio) {
                log.warn("Source (" + sourceRatio + ") and destination (" + destRatio + ") width/height ratio are NOT the same!");
            }

            return (double) destHeight / (double) sourceHeight;
        } else {
            log.error("We should never get here!");
            return 1;
        }
    }

    /**
     * Create cache node
     */
    private Node createCacheNode() throws Exception {
        String cacheRootPath = getResourceConfigProperty("cache-root-path");
        if (cacheRootPath == null) {
            cacheRootPath = DEFAULT_CACHE_DIRECTORY;
            log.warn("No cache root path configured within resource configuration. Use default '" + cacheRootPath + "'!");
        }

        Node cacheNode;
        if (!getRealm().getRepository().existsNode(cacheRootPath + getPath())) {
            cacheNode = org.wyona.yarep.util.YarepUtil.addNodes(getRealm().getRepository(), cacheRootPath + getPath(), org.wyona.yarep.core.NodeType.RESOURCE);
            log.warn("Cached image did not exist yet, hence has been created: " + cacheNode.getPath());
        } else {
            cacheNode = getRealm().getRepository().getNode(cacheRootPath + getPath());
            log.error("The cache already seems to exist: " + cacheNode.getPath());
        }
        return cacheNode;
    }

    /**
     * Get cache node
     */
    private Node getCacheNode() throws Exception {
        String cacheRootPath = getResourceConfigProperty("cache-root-path");
        if (cacheRootPath == null) {
            cacheRootPath = DEFAULT_CACHE_DIRECTORY;
            log.debug("No cache root path configured within resource configuration. Use default '" + cacheRootPath + "'!");
        }

        if (getRealm().getRepository().existsNode(cacheRootPath + getPath())) {
            return getRealm().getRepository().getNode(cacheRootPath + getPath());
        } else {
            log.error("No such cache node: " + cacheRootPath + getPath());
            return null;
        }
    }

    /**
     * Check whether cache node exists and if so, then compare last modified
     */
    private boolean existsMoreRecentCacheNode() throws Exception {
        String cacheRootPath = getResourceConfigProperty("cache-root-path");
        if (cacheRootPath == null) {
            cacheRootPath = DEFAULT_CACHE_DIRECTORY;
            log.debug("No cache root path configured within resource configuration. Use default '" + cacheRootPath + "'!");
        }

        if (getRealm().getRepository().existsNode(cacheRootPath + getPath())) {
            long lastModifiedCacheNode = getRealm().getRepository().getNode(cacheRootPath + getPath()).getLastModified();
            long lastModifiedOriginalNode = getRealm().getRepository().getNode(getPath()).getLastModified();
            if (lastModifiedCacheNode > lastModifiedOriginalNode) {
                //log.debug("Compare last modified: " + lastModifiedCacheNode + ", " + lastModifiedOriginalNode);
                return true;
            } else {
                log.warn("Cached image seems to be out of date: " + cacheRootPath + getPath());
                return false;
            }
        } else {
            log.error("No such cache node: " + cacheRootPath + getPath());
            return false;
        }
    }
}
