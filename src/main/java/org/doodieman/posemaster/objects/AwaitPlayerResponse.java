package org.doodieman.posemaster.objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.doodieman.posemaster.PoseMaster;
import org.doodieman.posemaster.config.lang.LangConfig;
import org.doodieman.posemaster.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class AwaitPlayerResponse implements Listener {

    private static final Map<Player, AwaitPlayerResponse> activeResponses = new HashMap<>();

    final Player player;
    final long waitTime;
    final AwaitResponseType responseType;

    BukkitTask task;

    public AwaitPlayerResponse(Player player) {
        this(player, 600L, AwaitResponseType.STRING);
    }
    public AwaitPlayerResponse(Player player, long waitTime) {
        this(player, waitTime, AwaitResponseType.STRING);
    }
    public AwaitPlayerResponse(Player player, AwaitResponseType responseType) {
        this(player, 600L, responseType);
    }
    public AwaitPlayerResponse(Player player, long waitTime, AwaitResponseType responseType) {
        this.player = player;
        this.waitTime = waitTime;
        this.responseType = responseType;
    }

    public void onStringResponse(String response) {}
    public void onDoubleResponse(Double response) {}
    public void onVectorResponse(Vector response) {}

    public void onTimeout() {}

    //Formats the chat message to the correct response type
    private void handleResponse(String message) {

        switch (this.responseType) {

            case STRING -> this.onStringResponse(message);

            case DOUBLE -> {
                double value;
                try {
                    value = Double.parseDouble(message);
                    this.onDoubleResponse(value);

                } catch (NumberFormatException exception) {

                    //Send error message
                    Map<String, String> placeholders = Map.of(
                            "%response%", message,
                            "%type%", "number"
                    );
                    String langMessage = LangConfig.RESPONSE_TYPE_INVALID.getColoredMessage(placeholders);
                    player.sendMessage(langMessage);

                }
            }

            case VECTOR -> {
                Vector vector = StringUtil.stringToVector(message);
                if (vector == null) {

                    //Send error message
                    Map<String, String> placeholders = Map.of(
                            "%response%", message,
                            "%type%", "vector"
                    );
                    String langMessage = LangConfig.RESPONSE_TYPE_INVALID.getColoredMessage(placeholders);
                    player.sendMessage(langMessage);

                    return;
                }
                this.onVectorResponse(vector);
            }

        }
    }

    @EventHandler( priority = EventPriority.HIGHEST )
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.getPlayer() != player) return;

        String message = event.getMessage();

        this.task.cancel();
        event.setCancelled(true);

        //Handle the response on sync
        Bukkit.getScheduler().runTask(PoseMaster.getInstance(), () -> {
            HandlerList.unregisterAll(this);
            this.handleResponse(message);
        });
    }

    public void cancel() {
        this.task.cancel();
        activeResponses.remove(player);
        HandlerList.unregisterAll(this);
    }

    public void start() {
        //Already awaiting another response. Cancel the other one first.
        if (activeResponses.containsKey(player))
            activeResponses.get(player).cancel();

        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                cancel();
                onTimeout();
            }
        }.runTaskLater(PoseMaster.getInstance(), this.waitTime);

        Bukkit.getPluginManager().registerEvents(this, PoseMaster.getInstance());
    }

}
