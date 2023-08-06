package doodieman.posemaster;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import doodieman.posemaster.command.PoseMasterCommand;
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
    @Getter
    private ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        instance = this;
        this.protocolManager = ProtocolLibrary.getProtocolManager();

        Bukkit.getPluginManager().registerEvents(new PoseListener(),this);
        Bukkit.getPluginCommand("posemaster").setExecutor(new PoseMasterCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static void sendMessage(Player player, String message) {
        player.sendMessage("§6§l[§ePM§6§l]§r "+message);
    }

}
