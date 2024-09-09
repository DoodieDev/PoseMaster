package org.doodieman.posemaster;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
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

    @Override
    public void onEnable() {
        instance = this;

        this.listener = new BasicListener();
    }

    @Override
    public void onDisable() {

    }


}
