package org.wyona.yanel.core.api.attributes;

import org.wyona.yanel.core.api.attributes.creatable.ResourceInput;

/**
 * A Modifiable can be modified from a ResourceInput. The ResourceInput, which
 * specifies the format of the expected input must be provided by the Modifiable
 * itself and must be prefilled with the values of the Object to be modified.
 * 
 */
public interface ModifiableV3 {

    /**
     * Modifies the instance and assigns values from the input.
     * 
     * @param input
     * @throws Exception
     */
    public void modify(ResourceInput input) throws Exception;

    /**
     * The ResourceInput must be prefilled with the values of the Resource. This
     * resource is used to do modifications and is then passed back to the
     * modify method.
     * 
     * @return ResourceInput
     * @throws Exception
     */
    public ResourceInput getResourceInputForModification() throws Exception;
    

}