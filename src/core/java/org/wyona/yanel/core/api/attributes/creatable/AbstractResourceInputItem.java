package org.wyona.yanel.core.api.attributes.creatable;

import org.apache.log4j.Logger;

/**
 * Implements name and validation functionality for a ResourceInputItem.
 *
 */
public abstract class AbstractResourceInputItem implements ResourceInputItem {

    private static Logger log = Logger.getLogger(AbstractResourceInputItem.class);
    
    private String name;
    
    private Validator validator = null;
    
    private ValidationMessage validationMessage;
    
    /**
     * Creates an InputItem with the specified name.
     * @param name
     */
    public AbstractResourceInputItem(String name) {
        this.name = name;
    }
    
    /* 
     * @see org.wyona.yanel.core.api.attributes.creatable.ResourceInputItem#getName()
     */
    public final String getName() {
        return this.name;
    }
    
    public final void rename( String newName ){
        this.name = newName;        
    }
    
    /**
     * @return the validator associated with this item
     */
    public final Validator getValidator() {
        return validator;
    }

    /**
     * Set the validator for this item
     * @param validator
     */
    public final void setValidator(Validator validator) {
        this.validator = validator;
    }
    
    /**
     * Validates the value and returns <code>true</code> when the validation is ok.
     */
    public final boolean validate() {
        log.debug("Validate item ...");
        validationMessage = new ValidationMessage(getName(), this.getValue(), "Assumed to be valid", true);
        if(validator != null){
            ValidationMessage vm = validator.validate(this);
            if(vm != null){
                validationMessage = vm;
            }
        }
        
        return validationMessage.isValidationOK();
    }
    
    public ValidationMessage getValidationMessage() {
        return validationMessage;
    }
    
    /**
     * Removes the validation message and calls doSetValue()
     * */
    public final void setValue(Object value) {
        removeValidationMessage();
        doSetValue(value);
    }
    
    /**
     * Set value only without any additional steps 
     * */
    public abstract void doSetValue(Object value);
    
    public final void removeValidationMessage(){
        validationMessage = null;
    }

}
