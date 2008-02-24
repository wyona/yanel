/*
 * Copyright 2007 Wyona
 */

package bar.foo.yanel.impl.resources;

import org.wyona.yanel.core.Resource;
import org.wyona.yanel.core.api.attributes.ViewableV2;
import org.wyona.yanel.core.attributes.viewable.View;
import org.wyona.yanel.core.attributes.viewable.ViewDescriptor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * A simple HelloWorldResource which implements ViewableV2 to output "Hello World!"
 */
public class FromScratchResource extends Resource implements ViewableV2 {
    
    private static Logger log = Logger.getLogger(FromScratchResource.class);
    
    /* 
     * Since we don't read from somewhere which could not exist we just return true
     */
    public boolean exists() throws Exception {
        return true;
    }

    /* 
     * Here we could return the size of the InputStream of the view 
     * to allow the browser to know about before it receives the request 
     */
    public long getSize() throws Exception {
        return -1;
    }

    /*
     * Here we can describe the view returned by this resource-type.
     * in this simple case it's just the default with a mime-type "text/plain".
     * Now yanel is able to tell you what views are provided an which mime-type they will have.
     */
    public ViewDescriptor[] getViewDescriptors() {
        ViewDescriptor[] viewDescriptor = new ViewDescriptor[1];
        viewDescriptor[0] = new ViewDescriptor("default");
        viewDescriptor[0].setMimeType("text/plain");
        return viewDescriptor;
    }
    
    /*
     * Here the view is created which contains the InputStream and the mime-type 
     */
    public View getView(String viewId) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("requested viewId: " + viewId);
        }
        View view = new View();
        view.setInputStream(getHelloWorld());
        view.setMimeType(getViewDescriptors()[0].getMimeType());
        return view;
    }
    
    /*
     * This private method creates the InputStream used by the view
     */
    private InputStream getHelloWorld() {
        String helloWorld = "Hello World!";
        return new ByteArrayInputStream(helloWorld.getBytes());
    }
}
