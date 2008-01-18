package org.wyona.yanel.core;


public abstract class Constants {
    /**
     * General parameters and values for passing requests to Yanel (e.g. http://.../yanel/resource.xml?param=value) 
     * */
    public static interface Request{
        /**
         * Controls which view a viewable resource should choose
         * */
        public static final String YANEL_RESOURCE_VIEWID = "yanel.resource.viewid";
        /**
         * The value for the default view
         * */
        public static final String DEFAULT_VIEW_ID = "default";
        
        /**
         * The value for the source view
         * */
        public static final String SOURCE_VIEW_ID = "source";
    }
    
    private Constants(){};
}
