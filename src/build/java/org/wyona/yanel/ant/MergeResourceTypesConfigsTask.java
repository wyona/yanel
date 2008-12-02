/*
 * ALSO SEE: build/lenya/src-tools/org/apache/lenya/cms/ant/CopyJavaSourcesTask.java
 */
package org.wyona.yanel.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

import java.io.File;

import org.wyona.yanel.core.map.Realm;
import org.wyona.yanel.core.map.RealmManager;

/**
 * Merge resource-types.xml config files of the various realms and core
 */
public class MergeResourceTypesConfigsTask extends Task {

    private Path defaultRealmsConfigDir;
    private Path localRealmsConfigDir;

    /**
     *
     */
    public void execute() throws BuildException {
        log("INFO: Default realms config directory: " + defaultRealmsConfigDir);
        log("INFO: Local realms config directory: " + localRealmsConfigDir);
        File defaultRealmsConfig = new File(defaultRealmsConfigDir.toString(), "realms.xml");
        File localRealmsConfig = new File(localRealmsConfigDir.toString(), "local.realms.xml");
        RealmManager realmManager;
        try {
            if (localRealmsConfig.isFile()) {
                log("INFO: Local realms config exists: " + localRealmsConfig.getAbsolutePath());
                realmManager = new RealmManager("yanel.xml");
                //realmManager = new RealmManager("local.realms.xml");
                //realmManager = new RealmManager(localRealmsConfig.getAbsolutePath());
            } else {
                log("WARN: No local realms config '" + localRealmsConfig.getAbsolutePath() + "' exists, hence use default one '" + defaultRealmsConfig.getAbsolutePath() + "'");
                realmManager = new RealmManager("yanel.xml");
                //realmManager = new RealmManager(defaultRealmsConfig.getAbsolutePath());
            }
        } catch (Exception e) {
            log("ERROR: " + e.getMessage());
            throw new BuildException(e.getMessage(), e);
        }

        Realm[] realms = realmManager.getRealms();
        log("Number of realms: " + realms.length);
        for (int i = 0; i < realms.length; i++) {
            log("Realm config: " + realms[i].getConfigFile());
        }
        log("HUGO");
    }

    /**
     *
     */
    public void setDefaultRealmsConfigDir(Path defaultRealmsConfigDir) {
        this.defaultRealmsConfigDir = defaultRealmsConfigDir;
    }

    /**
     *
     */
    public void setLocalRealmsConfigDir(Path localRealmsConfigDir) {
        this.localRealmsConfigDir = localRealmsConfigDir;
    }
}
