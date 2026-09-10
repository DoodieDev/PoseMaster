package doodieman.posemaster.gui;

import doodieman.posemaster.PoseMaster;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

/**
 * Waits for a chat response from a player. Keyed by player UUID so the map
 * entry is replaced/cleaned up reliably; entries are removed on respond,
 * timeout, cancel, player quit and plugin disable. All callbacks and cleanup
 * run on the server thread.
 */
public class PoseAwaitResponse implements Listener {

    final UUID playerUuid;
    BukkitTask timeoutTask;

    final long timeoutTicks;

    public PoseAwaitResponse(Player player, long timeoutTicks) {
        this.playerUuid = player.getUniqueId();
        this.timeoutTicks = timeoutTicks;

        this.start();
    }

    @EventHandler ( priority = EventPriority.HIGHEST )
    public void onChat(AsyncPlayerChatEvent event) {
        if (!event.getPlayer().getUniqueId().equals(playerUuid)) return;

        String message = event.getMessage();
        this.timeoutTask.cancel();
        event.setCancelled(true);

        Bukkit.getScheduler().runTask(PoseMaster.getInstance(), () -> {
            this.unregister();
            this.onRespond(message);
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (event.getPlayer().getUniqueId().equals(playerUuid)) this.cancel();
    }

    public void onRespond(String message) {}
    public void onTimeout() {}


    public void cancel() {
        if (this.timeoutTask != null) this.timeoutTask.cancel();
        this.unregister();
    }

    public void start() {
        PoseAwaitResponse previous = PoseMaster.getInstance().getAwaitResponseMap().put(playerUuid, this);
        if (previous != null) previous.cancel();

        this.timeoutTask = new BukkitRunnable() {
            @Override
            public void run() {
                PoseAwaitResponse.this.unregister();
                PoseAwaitResponse.this.onTimeout();
            }
        }.runTaskLater(PoseMaster.getInstance(), timeoutTicks);

        Bukkit.getPluginManager().registerEvents(this, PoseMaster.getInstance());
    }

    private void unregister() {
        HandlerList.unregisterAll(this);
        PoseMaster.getInstance().getAwaitResponseMap().remove(this.playerUuid, this);
    }

}
