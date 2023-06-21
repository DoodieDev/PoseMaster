package doodieman.posemaster;

import doodieman.posemaster.gui.PoseAwaitResponse;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class PoseMaster extends JavaPlugin {

    @Getter
    private static PoseMaster instance;
    @Getter
    private final Map<Player, PoseAwaitResponse> awaitResponseMap = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;

        Bukkit.getPluginManager().registerEvents(new PoseListener(),this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
