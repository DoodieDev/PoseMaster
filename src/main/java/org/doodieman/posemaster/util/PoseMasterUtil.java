package org.doodieman.posemaster.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.doodieman.posemaster.PoseMaster;
import org.doodieman.posemaster.objects.PoseArmorStand;

import java.util.List;
import java.util.UUID;

public class PoseMasterUtil {

    private static FileConfiguration getConfig() {
        return PoseMaster.getInstance().getConfig();
    }

    //Spawn a new ArmorStand with default data (poses, etc.)
    public static PoseArmorStand createPoseArmorStand(Location location) {
        boolean cache = getConfig().getBoolean("auto-cache");
        return createPoseArmorStand(location, cache);
    }
    public static PoseArmorStand createPoseArmorStand(Location location, boolean saveToCache) {
        World world = location.getWorld();
        FileConfiguration config = getConfig();
        if (world == null) return null;

        ArmorStand armorStand = world.spawn(location, ArmorStand.class);
        PoseArmorStand poseArmorStand = new PoseArmorStand(armorStand);

        if (config.getBoolean("use-default-properties"))
            poseArmorStand.applyDefaultProperties();

        if (saveToCache)
            poseArmorStand.saveToCache();

        return poseArmorStand;
    }

    //Convert an existing ArmorStand to a PoseArmorStand
    public static PoseArmorStand convertToPoseArmorStand(ArmorStand armorStand) {
        boolean cache = PoseMaster.getInstance().getConfig().getBoolean("auto-cache");
        return convertToPoseArmorStand(armorStand, cache);
    }
    public static PoseArmorStand convertToPoseArmorStand(ArmorStand armorStand, boolean saveToCache) {
        PoseArmorStand poseArmorStand = new PoseArmorStand(armorStand);
        if (saveToCache)
            poseArmorStand.saveToCache();
        return poseArmorStand;
    }

    //Get a PoseArmorStand from the cache
    public static PoseArmorStand getFromCache(String id) {
        return getCache()
                .stream()
                .filter(as -> as.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    public static PoseArmorStand getFromCache(ArmorStand armorStand) {
        return getCache()
                .stream()
                .filter(as -> as.getArmorStand().equals(armorStand))
                .findFirst()
                .orElse(null);
    }
    public static PoseArmorStand getFromCache(UUID uuid) {
        return getCache()
                .stream()
                .filter(as -> as.getUuid().equals(uuid))
                .findFirst()
                .orElse(null);
    }

    //Get all cached ArmorStands
    public static List<PoseArmorStand> getCache() {
        return PoseMaster.getInstance().getArmorStandCache();
    }
}
