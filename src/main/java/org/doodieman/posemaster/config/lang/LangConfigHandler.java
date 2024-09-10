package org.doodieman.posemaster.config.lang;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.doodieman.posemaster.PoseMaster;

import java.io.File;
import java.io.IOException;

public class LangConfigHandler {

    @Getter
    private final FileConfiguration config;
    final File langFile;

    public LangConfigHandler() {
        langFile = new File(PoseMaster.getInstance().getDataFolder(), "lang.yml");
        this.config = this.loadConfig();
    }

    public FileConfiguration loadConfig() {
        if (!langFile.exists()) {
            try {
                langFile.getParentFile().mkdirs();
                langFile.createNewFile();
            } catch (IOException e) {
                PoseMaster.getInstance().getLogger().severe("Error loading lang.yml: "+e.getMessage());
            }
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(langFile);

        // Set the default language keys, if they don't exist
        for (LangConfig lang : LangConfig.values()) {
            if (!config.contains(lang.getKey())) {
                config.set(lang.getKey(), lang.getDefaultMessage());
            }
        }

        try {
            config.save(langFile);
        } catch (IOException e) {
            PoseMaster.getInstance().getLogger().severe("Error saving lang.yml: "+e.getMessage());
        }

        return config;
    }

}
