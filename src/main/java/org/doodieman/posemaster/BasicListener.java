package org.doodieman.posemaster;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
        PoseArmorStand poseArmorStand = PoseMasterUtil.convertToPoseArmorStand(armorStand);

        if (ConfigOption.USE_DEFAULT_PROPERTIES.getBoolean()) {
            poseArmorStand.applyDefaultProperties();
        }
    }

    @EventHandler
    public void onArmorStandClick(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked().getType() != EntityType.ARMOR_STAND)
            return;

        ArmorStand armorStand = (ArmorStand) event.getRightClicked();
        PoseArmorStand poseArmorStand = PoseMasterUtil.getFromCache(armorStand);

        //If it is not in the cache, convert it to PoseArmorStand.
        if (poseArmorStand == null) {
            poseArmorStand = PoseMasterUtil.convertToPoseArmorStand(armorStand);
        }

        // TODO: Open menu
    }

    @EventHandler
    public void onArmorStandBreak(EntityDamageByEntityEvent event) {
        if (event.getEntityType() != EntityType.ARMOR_STAND)
            return;
        if (event.getDamager().getType() != EntityType.PLAYER)
            return;

        Player player = (Player) event.getDamager();
        ArmorStand armorStand = (ArmorStand) event.getEntity();
        PoseArmorStand poseArmorStand = PoseMasterUtil.getFromCache(armorStand);

        if (poseArmorStand == null)
            return;

        event.setCancelled(true);

        if (player.getInventory().getItemInMainHand().getType() != Material.POISONOUS_POTATO) {
            player.sendMessage("§cYou can only break this with a poisonous potato!");
            return;
        }

        poseArmorStand.delete();
    }

}
