package doodieman.posemaster;

import doodieman.posemaster.compat.ArmorStandAccess;
import doodieman.posemaster.gui.PoseMenuEquipment;
import doodieman.posemaster.gui.PoseMenuPositions;
import doodieman.posemaster.gui.PoseMenuPresets;
import doodieman.posemaster.gui.PoseMenuSettings;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

public class PoseListener implements Listener {

    @EventHandler ( priority = EventPriority.HIGHEST )
    public void onArmorStandClick(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof ArmorStand)) return;
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) return;

        ArmorStand armorStand = (ArmorStand) event.getRightClicked();
        event.setCancelled(true);
        this.openMenu(player, armorStand);
    }

    @EventHandler ( priority = EventPriority.HIGHEST )
    public void onArmorStandSpawn(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof ArmorStand)) return;

        applyDefaultState((ArmorStand) event.getEntity());
    }

    /**
     * Marker armor stands have no hitbox (vanilla), so they can never be clicked
     * or hit. This handler lets the potato edit the nearest marker stand within
     * range by interacting in the air/on a block: right-click opens the menu,
     * left-click removes the stand.
     */
    @EventHandler ( priority = EventPriority.HIGHEST )
    public void onMarkerPotatoInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK
            && action != Action.LEFT_CLICK_AIR && action != Action.LEFT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) return;
        if (!this.isHoldingTriggerItem(player)) return;

        ArmorStand nearestMarker = this.findNearestMarkerStand(player, 5.0);
        if (nearestMarker == null) return;

        event.setCancelled(true);

        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            this.openMenu(player, nearestMarker);
        } else {
            nearestMarker.remove();
            PoseMaster.sendMessage(player, "§7Removed the nearest §fmarker§7 armor stand.");
        }
    }

    @EventHandler ( priority = EventPriority.HIGHEST )
    public void onArmorStandDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof ArmorStand)) return;

        ArmorStand armorStand = (ArmorStand) event.getEntity();

        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
                if (this.isHoldingTriggerItem(player)) {
                    event.setCancelled(true);
                    armorStand.remove();
                    return;
                }
            }
        }

        if (ArmorStandAccess.isInvulnerable(armorStand))
            event.setCancelled(true);
    }

    /**
     * Opens the menu page the player had open last. Defaults to the position
     * page on first use.
     */
    private void openMenu(Player player, ArmorStand armorStand) {
        String lastPage = PoseMaster.getInstance().getLastMenuPageMap().get(player.getUniqueId());
        if ("SETTINGS".equals(lastPage)) {
            new PoseMenuSettings(player, armorStand).open();
        } else if ("EQUIPMENT".equals(lastPage)) {
            new PoseMenuEquipment(player, armorStand).open();
        } else if ("PRESETS".equals(lastPage)) {
            new PoseMenuPresets(player, armorStand).open();
        } else {
            new PoseMenuPositions(player, armorStand).open();
        }
    }

    /**
     * Defaults applied to every spawned armor stand. Isolated so it can later
     * become configurable without changing behavior.
     */
    private void applyDefaultState(ArmorStand armorStand) {
        EulerAngle zeroEuler = new EulerAngle(0, 0, 0);
        armorStand.setArms(true);
        armorStand.setGravity(false);
        armorStand.setBodyPose(zeroEuler);
        armorStand.setHeadPose(zeroEuler);
        armorStand.setRightArmPose(zeroEuler);
        armorStand.setLeftArmPose(zeroEuler);
        armorStand.setRightLegPose(zeroEuler);
        armorStand.setLeftLegPose(zeroEuler);

        ArmorStandAccess.setInvulnerable(armorStand, true);
        ArmorStandAccess.setDisabledSlots(armorStand, ArmorStandAccess.EQUIPMENT_LOCK_MASK);
    }

    private boolean isHoldingTriggerItem(Player player) {
        ItemStack item = ArmorStandAccess.getMainHand(player.getEquipment());
        return item != null && item.getType() == Material.POISONOUS_POTATO;
    }

    private ArmorStand findNearestMarkerStand(Player player, double radius) {
        ArmorStand nearest = null;
        double closest = radius * radius;
        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if (!(entity instanceof ArmorStand)) continue;

            ArmorStand stand = (ArmorStand) entity;
            if (!stand.isMarker()) continue;

            double distance = player.getLocation().distanceSquared(stand.getLocation());
            if (distance < closest) {
                closest = distance;
                nearest = stand;
            }
        }
        return nearest;
    }

}
