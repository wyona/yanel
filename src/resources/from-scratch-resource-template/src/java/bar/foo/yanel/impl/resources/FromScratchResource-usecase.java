/*
 * Copyright 2007 Wyona
 */

package bar.foo.yanel.impl.resources;

import org.wyona.yanel.impl.resources.usecase.ExecutableUsecaseResource;
import org.wyona.yanel.impl.resources.usecase.UsecaseException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;


/**
 * A simple usecase which is based on ExecutableUsecaseResource
 */
public class FromScratchResource extends ExecutableUsecaseResource {
    
    private static Logger log = Logger.getLogger(FromScratchResource.class);
    
    
    /*
     * This method is executed when submitting the form provided in the default view (probably implemented as a jelly template).
     */
    public void execute() throws UsecaseException {

    }

    /*
     * This method is executed when canceling the form provided in the default view (probably implemented as a jelly template).
     */
    public void cancel() throws UsecaseException {

    }
    
    /*
     * Implement some test which are tested before the usecase will e executed
     */
    public boolean checkPreconditions() throws UsecaseException {

    }
}
