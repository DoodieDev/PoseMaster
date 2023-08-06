package doodieman.posemaster.objects;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class CopyCollection {

    @Getter
    private final List<ArmorStandCopy> copies;

    public CopyCollection(List<ArmorStandCopy> copies) {
        this.copies = copies;
    }

    public static CopyCollection fromRange(Location center, double range) {
        List<ArmorStandCopy> copyList = new ArrayList<>();

        for (Entity entity : center.getWorld().getNearbyEntities(center,range,range,range)) {
            if (!(entity instanceof ArmorStand)) continue;
            ArmorStand armorStand = (ArmorStand) entity;
            copyList.add(ArmorStandCopy.fromArmorStand(center, armorStand));
        }

        return new CopyCollection(copyList);
    }

    public void paste(Location center) {
        this.copies.forEach(copy -> copy.spawnArmorStand(center));
    }



}
