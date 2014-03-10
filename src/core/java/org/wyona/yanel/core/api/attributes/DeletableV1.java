package org.wyona.yanel.core.api.attributes;

import org.wyona.yanel.core.api.attributes.creatable.ResourceInput;

/**
 * A Deletable knows how to delete itself. This interface should not be mixed
 * with the Modifiable.
 * 
 */
public interface DeletableV1 {
    /**
     * Deletes the instance
     * 
     * @param input
     * @throws Exception
     */
    public void delete(ResourceInput input) throws Exception;

    /**
     * Values in the input may hint how the deletion should proceed.
     * Normally it will be an input with only one item, identifying the resource to delete.
     * However, there may be cases when a user is guided through a conversation where additional 
     * info is needed to perform the delete (e.g. some confirmation input).
     * 
     * @return ResourceInput
     * @throws Exception
     */
    public ResourceInput getResourceInputForDeletion() throws Exception;
}
