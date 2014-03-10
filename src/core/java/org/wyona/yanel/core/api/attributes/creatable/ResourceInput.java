package org.wyona.yanel.core.api.attributes.creatable;

import java.util.List;


/**
 * A ResourceInput holds different ResourceInputItems which specify the ResourceInput's structure.
 */
public interface ResourceInput {
    /**
     *  Adds an item 
     * @param item ResourceInputItem
     */
    public void add(ResourceInputItem item);
    
    /**
     * Gets a specific ResourceInputItem by name.
     * 
     * @param name
     * @return ResourceInputItem
     * @throws Exception
     */
    public ResourceInputItem getItem(String name) throws Exception;
    
    /**
     * Return all items on this ResourceInput.
     * @return
     * @throws Exception
     */
    public ResourceInputItem [] getItems() throws Exception;

    /**
     * Gets the names of all ResourceInputItems
     * @return String[] specifying all the names off the input items.
     * @throws Exception
     */
    public String[] getItemNames() throws Exception;


    /**
     * Should be called in cases when the validation is not performed directly on each input item.
     * It validates <b>only invalid or not yet validated items</b>.
     * */
    public boolean validate();
    
    /**
     * Tells if the input is valid without doing the validation. It simply checks if every item is ok.
     * If the item was not validated it is assumed to be invalid. So make sure validate() has been called beforehand.
     * 
     * @return
     * */
    public boolean isValid();
   
    /**
     * @return validation messages for every input item
     * */
    public List<ValidationMessage> getValidationMessages();
}
