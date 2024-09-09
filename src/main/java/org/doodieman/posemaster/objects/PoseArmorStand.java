package org.doodieman.posemaster.objects;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.doodieman.posemaster.PoseMaster;
import org.doodieman.posemaster.util.StringUtil;

import java.util.UUID;

public class PoseArmorStand {

    @Getter
    private final String id;

    @Getter
    private final ArmorStand armorStand;
    @Getter
    private final UUID uuid;

    /*
        Constructors
     */
    public PoseArmorStand(UUID uuid) {
        this.uuid = uuid;
        this.armorStand = (ArmorStand) Bukkit.getEntity(uuid);
        this.id = StringUtil.generateRandomString(10);
    }
    public PoseArmorStand(ArmorStand armorStand) {
        this.armorStand = armorStand;
        this.uuid = armorStand.getUniqueId();
        this.id = StringUtil.generateRandomString(10);
    }

    //Load the ArmorStand data from a data file
    public void loadData() {

    }

    //SAve the ArmorStand data to a data file, and the cache
    public void saveData() {
        PoseMaster.getInstance().getArmorStandCache().add(this);
    }

    //Updates the data for the ArmorStand such as location, poses, equipment, etc.
    //If the ArmorStand has not been loaded, it will set the default values.
    public void updateArmorStandData() {
        this.armorStand.setArms(true);
    }


}
