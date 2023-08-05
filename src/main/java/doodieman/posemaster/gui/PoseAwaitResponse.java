package doodieman.posemaster.gui;

import doodieman.posemaster.PoseMaster;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class PoseAwaitResponse implements Listener {

    final Player player;
    BukkitTask timeoutTask;

    final long timeoutTime;

    public PoseAwaitResponse(Player player, long timeout) {
        this.player = player;
        this.timeoutTime = timeout;

        this.start();
    }

    @EventHandler ( priority = EventPriority.HIGHEST )
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.getPlayer() != player) return;

        String message = event.getMessage();
        this.timeoutTask.cancel();
        event.setCancelled(true);

        Bukkit.getScheduler().runTask(PoseMaster.getInstance(), () -> {
            HandlerList.unregisterAll(PoseAwaitResponse.this);
            this.onRespond(message);
        });
    }


    public void onRespond(String message) {}
    public void onTimeout() {}


    public void cancel() {
        this.timeoutTask.cancel();
        HandlerList.unregisterAll(PoseAwaitResponse.this);
    }

    public void start() {
        if (PoseMaster.getInstance().getAwaitResponseMap().containsKey(player)) {
            PoseMaster.getInstance().getAwaitResponseMap().get(player).cancel();
        }
        PoseMaster.getInstance().getAwaitResponseMap().put(player, this);

        this.timeoutTask = new BukkitRunnable() {
            @Override
            public void run() {
                HandlerList.unregisterAll(PoseAwaitResponse.this);
                PoseAwaitResponse.this.onTimeout();
            }
        }.runTaskLater(PoseMaster.getInstance(),timeoutTime);

        Bukkit.getPluginManager().registerEvents(this, PoseMaster.getInstance());
    }

}
