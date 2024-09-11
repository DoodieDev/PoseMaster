package org.doodieman.posemaster;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.doodieman.posemaster.config.lang.LangConfigHandler;
import org.doodieman.posemaster.objects.armorstands.PoseArmorStand;
import org.doodieman.posemaster.objects.players.PlayerCache;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class PoseMaster extends JavaPlugin {

    @Getter
    private static PoseMaster instance;
    @Getter
    private BasicListener listener;
    @Getter
    private LangConfigHandler langConfigHandler;

    /*
        CACHES
    */
    @Getter
    private final List<PoseArmorStand> armorStandCache = new ArrayList<>();
    @Getter
    private final List<PlayerCache> playerCache = new ArrayList<>();

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

    public PlayerCache getPlayerCache(Player player) {
        return this.getPlayerCache(player.getUniqueId());
    }

    public PlayerCache getPlayerCache(UUID uuid) {
        PlayerCache cache = this.playerCache.stream()
                .filter(playerCache -> playerCache.getUuid().equals(uuid))
                .findFirst()
                .orElse(new PlayerCache(uuid));

        if (!playerCache.contains(cache))
            playerCache.add(cache);

        return cache;
    }

}
