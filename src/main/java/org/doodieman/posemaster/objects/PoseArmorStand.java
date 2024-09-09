package org.doodieman.posemaster.objects;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.doodieman.posemaster.PoseMaster;
import org.doodieman.posemaster.util.PoseMasterUtil;
import org.doodieman.posemaster.util.StringUtil;

import java.util.UUID;

public class PoseArmorStand {

    @Getter
    private final String id;

    @Getter
    private final ArmorStand armorStand;
    @Getter
    private final UUID uuid;

    boolean cached;
    boolean saved;

    /*
        Constructors
    */
    public PoseArmorStand(UUID uuid) {
        this.uuid = uuid;
        this.armorStand = (ArmorStand) Bukkit.getEntity(uuid);
        this.id = StringUtil.generateRandomString(10);
        this.cached = false;
        this.saved = false;
    }

    public PoseArmorStand(ArmorStand armorStand) {
        this.armorStand = armorStand;
        this.uuid = armorStand.getUniqueId();
        this.id = StringUtil.generateRandomString(10);
        this.cached = false;
        this.saved = false;
    }

    //Load this ArmorStand from storage
    public void loadFromStorage() {

    }

    //Save this ArmorStand to storage
    public void saveToStorage() {
        this.saved = true;
    }

    //Save the ArmorStand to cache
    public void saveToCache() {
        PoseMaster.getInstance().getArmorStandCache().add(this);

        this.cached = true;
    }

    //Delete the ArmorStand
    public void delete() {
        //Remove from cache
        if (this.cached) {
            PoseMaster.getInstance().getArmorStandCache().removeIf(as -> as.getId().equals(this.id));
        }

        //Remove from storage
        if (this.saved) {

        }

        if (armorStand != null)
            armorStand.remove();
    }

    //Updates the data for the ArmorStand such as location, poses, equipment, etc.
    public void applyProperties(ArmorStandProperties properties) {
        properties.applyToArmorStand(this.armorStand);
    }

    //Apply the default properties to the ArmorStand
    public void applyDefaultProperties() {
        ArmorStandProperties properties = PoseMaster.getInstance().getDefaultProperties();
        this.applyProperties(properties);
    }

}
