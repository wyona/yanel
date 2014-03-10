package org.wyona.yanel.core.api.attributes.creatable;

/**
 * Interface to validate ResourceInputItems. Different implementations will
 * validate in different ways. If validation fails, a ValidationException will
 * be thrown.
 * 
 */
public interface Validator {

    /**
     * Method to validate a ResourceInputItem.
     * @param item
     */
    public ValidationMessage validate(ResourceInputItem item);

}
