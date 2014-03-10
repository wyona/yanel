package org.wyona.yanel.core.api.attributes.creatable;


/**
 * Represents an Item which can have more than one value. The possible values are not predefined.new values can be added and existing values can be removed.
 * add a parameter called resource.input.collection.action with value 'add' or
 * remove to the request, so that the JellyAdapterForCreatable knows what to to.
 */
public interface ResourceInputItemCollection extends ResourceInputItem {
    public boolean addValue(Object value);
    public boolean addValue(int index, Object value);
    
    public boolean removeValue(Object value);
    public Object removeValue(int index);
    
    public int size();
    
    /**
     * @return -1 when the index can't be determined, otherwise >= 0
     * */
    public int indexOfValue(Object value);

    /**
     * Move the value in the collection
     * */
    public boolean moveValue(int fromIndex, int toIndex);
}
