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

    /**
     *
     */
    public void execute() throws BuildException {
        log("Not implemented yet!");
        log("New (third party) realm configuration: " + newRealmConfig);
        //RealmManager rm = new RealmManager();
    }

    /**
     * Ant file task attribute new (third party) realm config
     */
    public void setNewRealmConfig(Path newRealmConfig) {
        this.newRealmConfig = newRealmConfig;
    }
}
