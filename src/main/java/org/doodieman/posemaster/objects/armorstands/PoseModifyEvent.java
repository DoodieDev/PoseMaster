package org.doodieman.posemaster.objects.armorstands;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PoseModifyEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    @Getter
    private final Player modifier;
    @Getter
    private final PoseArmorStand poseArmorStand;

    public PoseModifyEvent(PoseArmorStand poseArmorStand, Player modifier) {
        this.poseArmorStand = poseArmorStand;
        this.modifier = modifier;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
