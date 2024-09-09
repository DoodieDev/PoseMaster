package org.doodieman.posemaster;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.doodieman.posemaster.objects.ArmorStandProperties;
import org.doodieman.posemaster.objects.PoseArmorStand;

import java.util.ArrayList;
import java.util.List;

public final class PoseMaster extends JavaPlugin {

    @Getter
    private static PoseMaster instance;

    @Getter
    private BasicListener listener;

    /*
        CACHE
    */
    @Getter
    private final List<PoseArmorStand> armorStandCache = new ArrayList<>();
    @Getter
    private ArmorStandProperties defaultProperties;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();

        this.loadDefaultProperties();

        this.listener = new BasicListener();

    }

    @Override
    public void onDisable() {

    }

    //Loads the default ArmorStand properties from the config
    public void loadDefaultProperties() {
        ConfigurationSection section = this.getConfig().getConfigurationSection("default-properties");
        this.defaultProperties = ArmorStandProperties.fromConfigSection(section);
    }


}
