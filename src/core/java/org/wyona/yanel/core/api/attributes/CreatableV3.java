package org.wyona.yanel.core.api.attributes;

import org.wyona.yanel.core.api.attributes.creatable.ResourceInput;

/**
 * An implementation of CreatableV3 can be created from a ResourceInput. The
 * ResourceInput, which specifies the format of the expected input must be
 * provided by the CreatableV3 itself.
 * 
 */
public interface CreatableV3 {

    /**
     * Creates the Instance and assigns values from input.
     * @param input - normally it should be validated and valid
     * @throws Exception
     */
    public void create(ResourceInput input) throws Exception;

    /**
     * Should return a ResourceInput which shows the expected data at for the cerate method.
     * @return ResourceInput
     * @throws Exception
     */
    public ResourceInput getResourceInputForCreation() throws Exception;

}
