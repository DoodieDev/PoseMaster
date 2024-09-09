package org.doodieman.posemaster.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.doodieman.posemaster.PoseMaster;
import org.doodieman.posemaster.objects.PoseArmorStand;

import java.util.UUID;

public class PoseMasterUtil {

    //Spawn a new ArmorStand with default data (poses, etc.)
    public static PoseArmorStand spawnDefaultArmorStand(Location location) {
        return spawnDefaultArmorStand(location, false);
    }
    public static PoseArmorStand spawnDefaultArmorStand(Location location, boolean save) {
        World world = location.getWorld();
        if (world == null) return null;

        ArmorStand armorStand = world.spawn(location, ArmorStand.class);
        PoseArmorStand poseArmorStand = new PoseArmorStand(armorStand);

        poseArmorStand.updateArmorStandData();
        if (save)
            poseArmorStand.saveData();

        return poseArmorStand;
    }

    //Takes a vanilla ArmorStand and converts it to a PoseArmorStand
    public static PoseArmorStand getFromArmorStand(ArmorStand armorStand) {
        // TODO: Check if it exists in cache
        return new PoseArmorStand(armorStand);
    }
    public static PoseArmorStand getFromArmorStandUUID(UUID uuid) {
        // TODO: Check if it exists in cache
        return new PoseArmorStand(uuid);
    }


}
