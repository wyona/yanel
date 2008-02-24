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
 * A simple Usecase example
 */
public class FromScratchResource extends ExecutableUsecaseResource {
    
    private static Logger log = Logger.getLogger(FromScratchResource.class);
    
    protected static String PARAM_EXAMPLE_FORM_FIELD = "example-form-field";
    protected static String PARAM_EXAMPLE_FORM_FIELD_DATE = "example-form-field-date";
    
    /*
     * This method is executed when submitting the form provided in the default view (probably implemented as a jelly template).
     */
    public void execute() throws UsecaseException {
        // get parameters as string submitted by the form
        
        String exampleFormFieldDate = getParameterAsString(PARAM_EXAMPLE_FORM_FIELD_DATE);
        // add an info message displayed in the done view jelly template
        addInfoMessage("Usecase successfully executed. You accessed the default-view of the usecase at: " + exampleFormFieldDate + ". ");
        addInfoMessage("And now is: " + getDate());
    }

    /*
     * This method is executed when canceling the form provided in the default view (probably implemented as a jelly template).
     */
    public void cancel() throws UsecaseException {
        addInfoMessage("The usecase was canceled at: " + getDate());
    }

    /*
     * Implement some test which are tested before the usecase will e executed
     */
    public boolean checkPreconditions() throws UsecaseException {
        String exampleFormField = getParameterAsString(PARAM_EXAMPLE_FORM_FIELD);
        if (!exampleFormField.equals("hello")) {
            addError("You did not enter hello");
            return false;
        }
        return true;
    }
    
    /*
     * Provide a java object to be used by as a jelly template implemented view
     */
    public String getExampleString() throws Exception {
        return getDate();
    }
    
    /*
     * Private helper method for this example. returns a String with the current date and time
     */
    private String getDate() {
        java.util.Calendar cal = java.util.Calendar.getInstance(java.util.TimeZone.getDefault()); 
        String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"; 
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
        sdf.setTimeZone(java.util.TimeZone.getDefault()); 
        return sdf.format(cal.getTime()); 
    }
}
