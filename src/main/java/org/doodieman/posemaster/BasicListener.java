package org.doodieman.posemaster;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.doodieman.posemaster.objects.PoseArmorStand;
import org.doodieman.posemaster.util.PoseMasterUtil;

public class BasicListener implements Listener {

    public BasicListener() {
        Bukkit.getPluginManager().registerEvents(this, PoseMaster.getInstance());
    }

    @EventHandler
    public void onArmorStandSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() != EntityType.ARMOR_STAND)
            return;
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.DEFAULT)
            return;

        ArmorStand armorStand = (ArmorStand) event.getEntity();

        //Convert the armor stand to a PoseArmorStand object
        PoseArmorStand poseArmorStand = PoseMasterUtil.getFromArmorStand(armorStand);
        poseArmorStand.updateArmorStandData();
        Bukkit.broadcastMessage("Debug: Created PoseArmorStand: ");
    }

    @EventHandler
    public void onArmorStandClick(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked().getType() != EntityType.ARMOR_STAND)
            return;

        ArmorStand armorStand = (ArmorStand) event.getRightClicked();
        PoseArmorStand poseArmorStand = PoseMasterUtil.getFromArmorStand(armorStand);

        Bukkit.broadcastMessage("Debug: Clicked ArmorStand with id "+poseArmorStand.getId());
    }

}
