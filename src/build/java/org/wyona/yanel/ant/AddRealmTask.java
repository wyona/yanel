/*
 *
 */
package org.wyona.yanel.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

/**
 * Add a third party realm to the local realm configuration
 */
public class AddRealmTask extends Task {

    private Path newRealmConfig;
    private Path newRealmMountPoint;
    private Path newRealmId;

    /**
     *
     */
    public void execute() throws BuildException {
        log("Not implemented yet!");
        log("New (third party) realm configuration: " + newRealmConfig);
        log("New (third party) realm mount point: " + newRealmMountPoint);
        log("New (third party) realm id: " + newRealmId);
        //RealmManager rm = new RealmManager();
    }

    /**
     * Ant file task attribute new (third party) realm config
     */
    public void setNewRealmConfig(Path newRealmConfig) {
        this.newRealmConfig = newRealmConfig;
    }
    
    /**
     * Ant file task attribute new (third party) realm mount point
     */
    public void setNewRealmMountPoint(Path newRealmMountPoint) {
        this.newRealmMountPoint = newRealmMountPoint;
    }
    
    /**
     * Ant file task attribute new (third party) realm id
     */
    public void setNewRealmId(Path newRealmId) {
        this.newRealmId = newRealmId;
    }
  
}


// TODO in the execude methods check whethere or not the directory exists