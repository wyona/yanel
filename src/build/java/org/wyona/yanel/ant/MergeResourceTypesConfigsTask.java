/*
 * Merge global resource-types config with realm specific resource-types configs
 */
package org.wyona.yanel.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

import java.io.File;

import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.core.map.RealmContextConfig;
import org.wyona.yanel.core.map.RealmManagerConfig;

import org.apache.log4j.Logger;

/**
 * Merge resource-types.xml config files of the various realms and core
 */
public class MergeResourceTypesConfigsTask extends Task {

    private static Logger log = Logger.getLogger(MergeResourceTypesConfigsTask.class);

    private Path realmsConfigFile;
    private Path globalResourceTypesConfigFile;

    /**
     *
     */
    public void execute() throws BuildException {
        log.info("Merge ...");
        log("INFO: Realms config file: " + realmsConfigFile);
        log("INFO: Global resource-types config directory: " + globalResourceTypesConfigFile);
        File realmsConfig = new File(realmsConfigFile.toString());
        RealmManagerConfig realmManagerConfig = new RealmManagerConfig();
        try {
            RealmContextConfig[] realmContextConfigs;
            if (realmsConfig.isFile()) {
                realmContextConfigs = realmManagerConfig.getRealmContextConfigs(realmsConfig);

                log("Number of realms: " + realmContextConfigs.length);
                for (int i = 0; i < realmContextConfigs.length; i++) {
                    log("Realm context config: " + realmContextConfigs[i]);
                }
            } else {
                log("ERROR: No such realms config '" + realmsConfig.getAbsolutePath() + "' exists!");
            }
        } catch (Exception e) {
            log("ERROR: " + e.getMessage());
            throw new BuildException(e.getMessage(), e);
        }
    }

    /**
     *
     */
    public void setRealmsConfigFile(Path realmsConfigFile) {
        this.realmsConfigFile = realmsConfigFile;
    }

    /**
     *
     */
    public void setGlobalResourceTypesConfigFile(Path globalResourceTypesConfigFile) {
        this.globalResourceTypesConfigFile = globalResourceTypesConfigFile;
    }
}
