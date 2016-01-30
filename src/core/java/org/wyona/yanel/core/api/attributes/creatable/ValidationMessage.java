package org.wyona.yanel.core.api.attributes.creatable;

/**
 * Keeps the info about the validation of an input item
 */
public class ValidationMessage {
    private String itemName;
    private Object value;
    private String message;
    private boolean validationOK;

    public ValidationMessage(String itemName, Object value, boolean valueValid) {
        this(itemName, value, null, valueValid);
    }
    
    public ValidationMessage(String itemName, Object value, String message, boolean validationOK) {
        if(itemName == null){
            throw new NullPointerException("Item name must be provided");
        }
        this.itemName = itemName;
        this.value = value;
        setMessage(message);
        this.validationOK = validationOK;
    }

    /**
     * Get name of item
     */
    public String getItemName() {
        return itemName;
    }

    public Object getValue() {
        return value;
    }

    /**
     * Get message as string
     */
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isValidationOK() {
        return validationOK;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getMessage();
    }
}
