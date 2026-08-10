package doodieman.posemaster;

import doodieman.posemaster.command.PoseMasterCommand;
import doodieman.posemaster.compat.ServerFeatures;
import doodieman.posemaster.gui.PoseAwaitResponse;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PoseMaster extends JavaPlugin {

    @Getter
    private static PoseMaster instance;
    @Getter
    private final Map<UUID, PoseAwaitResponse> awaitResponseMap = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;

        if (!ServerFeatures.preloadNbtApi()) {
            getLogger().severe("Failed to initialize the NBT API. PoseMaster will be disabled!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        ServerFeatures.logStatus(getLogger());

        Bukkit.getPluginManager().registerEvents(new PoseListener(), this);
        Bukkit.getPluginCommand("posemaster").setExecutor(new PoseMasterCommand());
    }

    @Override
    public void onDisable() {
        for (PoseAwaitResponse response : new ArrayList<>(this.awaitResponseMap.values())) {
            response.cancel();
        }
        this.awaitResponseMap.clear();
    }

    public static void sendMessage(Player player, String message) {
        player.sendMessage("§6§l[§ePM§6§l]§r " + message);
    }

}
