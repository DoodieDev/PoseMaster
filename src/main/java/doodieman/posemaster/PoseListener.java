package doodieman.posemaster;

import doodieman.posemaster.gui.PoseMenuPositions;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.util.EulerAngle;

public class PoseListener implements Listener {

    @EventHandler ( priority = EventPriority.HIGHEST )
    public void onArmorStandClick(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof ArmorStand)) return;
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) return;

        ArmorStand armorStand = (ArmorStand) event.getRightClicked();
        event.setCancelled(true);
        new PoseMenuPositions(player,armorStand).open();
    }

    @EventHandler
    public void onArmorStandSpawn(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof ArmorStand)) return;

        ArmorStand armorStand = (ArmorStand) event.getEntity();
        EulerAngle zeroEuler = new EulerAngle(0,0,0);
        armorStand.setArms(true);
        armorStand.setGravity(false);
        armorStand.setBodyPose(zeroEuler);
        armorStand.setHeadPose(zeroEuler);
        armorStand.setRightArmPose(zeroEuler);
        armorStand.setLeftArmPose(zeroEuler);
        armorStand.setRightLegPose(zeroEuler);
        armorStand.setLeftLegPose(zeroEuler);

        net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) armorStand).getHandle();
        NBTTagCompound tag = new NBTTagCompound();
        nmsEntity.c(tag);
        tag.setInt("Invulnerable", 1);
        nmsEntity.f(tag);
    }

    @EventHandler
    public void onArmorStandBreak(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof ArmorStand)) return;

        ArmorStand armorStand = (ArmorStand) event.getEntity();

        NBTTagCompound tag = new NBTTagCompound();
        ((CraftEntity) armorStand).getHandle().c(tag);

        if (tag.getInt("Invulnerable") == 1)
            event.setCancelled(true);
    }

}
