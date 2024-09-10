package org.doodieman.posemaster;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.doodieman.posemaster.config.lang.LangConfigHandler;
import org.doodieman.posemaster.objects.armorstands.PoseArmorStand;

import java.util.ArrayList;
import java.util.List;

public final class PoseMaster extends JavaPlugin {

    @Getter
    private static PoseMaster instance;
    @Getter
    private BasicListener listener;
    @Getter
    private LangConfigHandler langConfigHandler;

    /*
        CACHE
    */
    @Getter
    private final List<PoseArmorStand> armorStandCache = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();

        this.langConfigHandler = new LangConfigHandler();
        this.listener = new BasicListener();
    }

    @Override
    public void onDisable() {

    }

}
