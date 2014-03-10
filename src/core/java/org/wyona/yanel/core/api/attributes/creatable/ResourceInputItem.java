package org.wyona.yanel.core.api.attributes.creatable;


/**
 * Represents a single input item.
 */
public interface ResourceInputItem {

    public static final int INPUT_TYPE_TEXT = 0;
    public static final int INPUT_TYPE_TEXTAREA = 1;
    public static final int INPUT_TYPE_HIDDEN = 2;
    public static final int INPUT_TYPE_PASSWORD = 3;
    public static final int INPUT_TYPE_FILE_UPLOAD = 4;
    public static final int INPUT_TYPE_RADIO = 5;
    public static final int INPUT_TYPE_CHECK = 6;
    public static final int INPUT_TYPE_SELECT = 7;
    public static final int INPUT_TYPE_LISTBOX = 8;
    public static final int INPUT_TYPE_COLLECTION = 9;
    
    /**
     * Type cane have the following values:
     * INPUT_TYPE_TEXT = 0;
     * INPUT_TYPE_TEXTAREA = 1;
     * INPUT_TYPE_HIDDEN = 2;
     * INPUT_TYPE_PASSWORD = 3;
     * INPUT_TYPE_FILE_UPLOAD = 4;
     * INPUT_TYPE_RADIO = 5;
     * INPUT_TYPE_CHECK = 6;
     * INPUT_TYPE_SELECT = 7;
     * INPUT_TYPE_LISTBOX = 8;
     * 
     * @return type of the InputItem
     * 
     */
    public int getType();

    /**
     * @return the name of the item.
     */
    public String getName();
    
    /**
     * Changes the name to the passed in value;
     * @param name
     */
    public void rename( String name );
    

    /**
     * Depending on the type, this can be very different. A cast will be
     * necessary after retrieving the value.
     * 
     * @return the value of the item or <code>null</code> when there is no value
     */
    public Object getValue();

    /**
     * Sets the value of the item. Depending on the items type, different kind
     * of values are expected.
     * This method must remove the validation message, because it may be inconsistent
     * with the new value
     * 
     * @param value of the item or <code>null</code> to reset it
     */
    public void setValue(Object value);

    /**
     * Validates the item on the current input and sets the validation message
     * */
    public boolean validate();
    
    /**
     * @return last validation message or <code>null</code> when no validation happend or it is inconsistent
     * */
    public ValidationMessage getValidationMessage();
}
