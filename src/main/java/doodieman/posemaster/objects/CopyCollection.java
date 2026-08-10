package doodieman.posemaster.objects;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class CopyCollection {

    @Getter
    private final List<ArmorStandState> copies;

    public CopyCollection(List<ArmorStandState> copies) {
        this.copies = copies;
    }

    public static CopyCollection fromRange(Location center, double range) {
        List<ArmorStandState> copyList = new ArrayList<>();

        for (Entity entity : center.getWorld().getNearbyEntities(center, range, range, range)) {
            if (!(entity instanceof ArmorStand)) continue;
            ArmorStand armorStand = (ArmorStand) entity;
            copyList.add(ArmorStandState.fromArmorStand(center, armorStand));
        }

        return new CopyCollection(copyList);
    }

    public void paste(Location center) {
        this.copies.forEach(copy -> copy.spawnArmorStand(center));
    }

}
