package doodieman.posemaster.objects;

import doodieman.posemaster.PoseMaster;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class ArmorStandCopy {

    @Getter @Setter
    private Vector offset = null;
    @Getter @Setter
    private Location originalCenter = null;

    @Getter @Setter
    private ItemStack itemInHand = null;
    @Getter @Setter
    private ItemStack[] armorContents = null;
    @Getter @Setter
    private boolean visible = true;
    @Getter @Setter
    private boolean small = false;
    @Getter @Setter
    private boolean arms = true;
    @Getter @Setter
    private boolean basePlate = true;
    @Getter @Setter
    private boolean customNameVisible = false;
    @Getter @Setter
    private String customName = null;
    @Getter @Setter
    private EulerAngle bodyPose = null;
    @Getter @Setter
    private EulerAngle headPose = null;
    @Getter @Setter
    private EulerAngle leftArmPose = null;
    @Getter @Setter
    private EulerAngle rightArmPose = null;
    @Getter @Setter
    private EulerAngle leftLegPose = null;
    @Getter @Setter
    private EulerAngle rightLegPose = null;
    @Getter @Setter
    private double rotation = 0;

    public static ArmorStandCopy fromArmorStand(Location center, ArmorStand armorStand) {
        ArmorStandCopy copy = new ArmorStandCopy();

        copy.setOriginalCenter(center.clone());
        copy.setOffset(new Vector(
            armorStand.getLocation().getX() - center.getX(),
            armorStand.getLocation().getY() - center.getY(),
            armorStand.getLocation().getZ() - center.getZ()
        ));

        copy.setRotation(armorStand.getLocation().getYaw());

        //Equipment
        copy.setArmorContents(armorStand.getEquipment().getArmorContents());
        copy.setItemInHand(armorStand.getItemInHand());

        //Basic values
        copy.setVisible(armorStand.isVisible());
        copy.setSmall(armorStand.isSmall());
        copy.setArms(armorStand.hasArms());
        copy.setBasePlate(armorStand.hasBasePlate());
        if (armorStand.getCustomName() != null)
            copy.setCustomName(armorStand.getCustomName());
        copy.setCustomNameVisible(armorStand.isCustomNameVisible());

        //Poses
        copy.setBodyPose(armorStand.getBodyPose());
        copy.setLeftArmPose(armorStand.getLeftArmPose());
        copy.setRightArmPose(armorStand.getRightArmPose());
        copy.setLeftLegPose(armorStand.getLeftLegPose());
        copy.setRightLegPose(armorStand.getRightLegPose());
        copy.setHeadPose(armorStand.getHeadPose());

        return copy;
    }

    public ArmorStand spawnArmorStand(Location center) {

        Location newLocation = center.clone();
        newLocation.add(
            this.offset.getX(),
            this.offset.getY(),
            this.offset.getZ()
        );

        newLocation.setYaw((float) this.getRotation());

        ArmorStand armorStand = center.getWorld().spawn(newLocation,ArmorStand.class);

        //Equipment
        armorStand.getEquipment().setArmorContents(this.getArmorContents());
        armorStand.getEquipment().setItemInHand(this.getItemInHand());

        //Basic values
        armorStand.setVisible(this.isVisible());
        armorStand.setSmall(this.isSmall());
        armorStand.setArms(this.isArms());
        armorStand.setBasePlate(this.isBasePlate());
        if (armorStand.getCustomName() != null)
            armorStand.setCustomName(this.getCustomName());
        armorStand.setCustomNameVisible(this.isCustomNameVisible());

        //Poses
        armorStand.setBodyPose(this.getBodyPose());
        armorStand.setLeftArmPose(this.getLeftArmPose());
        armorStand.setRightArmPose(this.getRightArmPose());
        armorStand.setLeftLegPose(this.getLeftLegPose());
        armorStand.setRightLegPose(this.getRightLegPose());
        armorStand.setHeadPose(this.getHeadPose());

        return armorStand;
    }

}
