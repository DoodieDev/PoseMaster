package doodieman.posemaster;

import doodieman.posemaster.compat.ArmorStandAccess;
import doodieman.posemaster.gui.PoseMenuPositions;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
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
        new PoseMenuPositions(player, armorStand).open();
    }

    @EventHandler ( priority = EventPriority.HIGHEST )
    public void onArmorStandSpawn(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof ArmorStand)) return;

        applyDefaultState((ArmorStand) event.getEntity());
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
        ArmorStandAccess.setDisabledSlots(armorStand, 31);
    }

    private boolean isHoldingTriggerItem(Player player) {
        ItemStack item = ArmorStandAccess.getMainHand(player.getEquipment());
        return item != null && item.getType() == Material.POISONOUS_POTATO;
    }

}
