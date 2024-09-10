package org.doodieman.posemaster.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.doodieman.posemaster.PoseMaster;

public enum NormalConfig {

    AUTO_CACHE("auto-cache", true),
    USE_DEFAULT_PROPERTIES("use-default-properties", true);

    final String path;
    final Object defaultValue;

    NormalConfig(String path, Object defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }

    private FileConfiguration getConfig() {
        return PoseMaster.getInstance().getConfig();
    }

    public Object get() {
        return getConfig().get(this.path);
    }

    public boolean getBoolean() {
        return getConfig().getBoolean(this.path, (boolean) this.defaultValue);
    }

    public String getString() {
        return getConfig().getString(this.path, (String) this.defaultValue);
    }

}
