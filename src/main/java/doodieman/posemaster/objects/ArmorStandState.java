package doodieman.posemaster.objects;

import doodieman.posemaster.compat.ArmorStandAccess;
import doodieman.posemaster.compat.ServerFeatures;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

/**
 * Single copy/duplicate/paste transfer object. Replaces ArmorStandCopy.
 *
 * Every mutable value is cloned on capture and on apply. Item writes are
 * null-safe (never write null references). Copy/paste is in-memory only.
 */
public class ArmorStandState {

    @Getter @Setter
    private Vector offset;
    @Getter @Setter
    private double rotation;

    @Getter @Setter
    private ItemStack mainHand;
    @Getter @Setter
    private ItemStack[] armorContents;
    @Getter @Setter
    private ItemStack offHand;

    @Getter @Setter
    private boolean visible = true;
    @Getter @Setter
    private boolean small = false;
    @Getter @Setter
    private boolean arms = true;
    @Getter @Setter
    private boolean basePlate = true;
    @Getter @Setter
    private boolean gravity = true;
    @Getter @Setter
    private boolean marker = false;

    @Getter @Setter
    private String customName;
    @Getter @Setter
    private boolean customNameVisible = false;

    @Getter @Setter
    private EulerAngle bodyPose;
    @Getter @Setter
    private EulerAngle headPose;
    @Getter @Setter
    private EulerAngle leftArmPose;
    @Getter @Setter
    private EulerAngle rightArmPose;
    @Getter @Setter
    private EulerAngle leftLegPose;
    @Getter @Setter
    private EulerAngle rightLegPose;

    @Getter @Setter
    private boolean invulnerable = true;
    @Getter @Setter
    private int disabledSlots = ArmorStandAccess.EQUIPMENT_LOCK_MASK;

    @Getter @Setter
    private boolean glow = false;
    @Getter @Setter
    private boolean silent = false;
    @Getter @Setter
    private double scale = 1.0;

    public static ArmorStandState fromArmorStand(Location center, ArmorStand stand) {
        ArmorStandState state = new ArmorStandState();
        Location standLocation = stand.getLocation();

        state.offset = new Vector(
            standLocation.getX() - center.getX(),
            standLocation.getY() - center.getY(),
            standLocation.getZ() - center.getZ()
        );
        state.rotation = standLocation.getYaw();

        state.mainHand = cloneOrNull(ArmorStandAccess.getMainHand(stand));
        state.armorContents = cloneArray(stand.getEquipment().getArmorContents());
        if (ServerFeatures.HAS_OFF_HAND) state.offHand = cloneOrNull(ArmorStandAccess.getOffHand(stand));

        state.visible = stand.isVisible();
        state.small = stand.isSmall();
        state.arms = stand.hasArms();
        state.basePlate = stand.hasBasePlate();
        state.gravity = stand.hasGravity();
        state.marker = stand.isMarker();

        state.customName = stand.getCustomName();
        state.customNameVisible = stand.isCustomNameVisible();

        state.bodyPose = copy(stand.getBodyPose());
        state.headPose = copy(stand.getHeadPose());
        state.leftArmPose = copy(stand.getLeftArmPose());
        state.rightArmPose = copy(stand.getRightArmPose());
        state.leftLegPose = copy(stand.getLeftLegPose());
        state.rightLegPose = copy(stand.getRightLegPose());

        state.invulnerable = ArmorStandAccess.isInvulnerable(stand);
        state.disabledSlots = ArmorStandAccess.getDisabledSlots(stand);
        if (ServerFeatures.HAS_GLOW) state.glow = ArmorStandAccess.isGlowing(stand);
        if (ServerFeatures.HAS_SILENT) state.silent = ArmorStandAccess.isSilent(stand);
        if (ServerFeatures.HAS_SCALE) state.scale = ArmorStandAccess.getScale(stand);

        return state;
    }

    public ArmorStand spawnArmorStand(Location center) {
        Location newLocation = center.clone();
        newLocation.add(offset.getX(), offset.getY(), offset.getZ());
        newLocation.setYaw((float) rotation);

        ArmorStand stand = center.getWorld().spawn(newLocation, ArmorStand.class);
        this.applyTo(stand);
        return stand;
    }

    public void applyTo(ArmorStand stand) {
        // Equipment (null-safe writes)
        if (mainHand != null) ArmorStandAccess.setMainHand(stand, mainHand.clone());
        if (armorContents != null) {
            ItemStack[] contents = new ItemStack[armorContents.length];
            for (int i = 0; i < contents.length; i++) {
                contents[i] = armorContents[i] == null ? null : armorContents[i].clone();
            }
            stand.getEquipment().setArmorContents(contents);
        }
        if (offHand != null) ArmorStandAccess.setOffHand(stand, offHand.clone());

        // Basic values
        stand.setVisible(visible);
        stand.setSmall(small);
        stand.setArms(arms);
        stand.setBasePlate(basePlate);
        stand.setGravity(gravity);
        if (customName != null) stand.setCustomName(customName);
        stand.setCustomNameVisible(customNameVisible);

        // Poses
        if (bodyPose != null) stand.setBodyPose(copy(bodyPose));
        if (headPose != null) stand.setHeadPose(copy(headPose));
        if (leftArmPose != null) stand.setLeftArmPose(copy(leftArmPose));
        if (rightArmPose != null) stand.setRightArmPose(copy(rightArmPose));
        if (leftLegPose != null) stand.setLeftLegPose(copy(leftLegPose));
        if (rightLegPose != null) stand.setRightLegPose(copy(rightLegPose));

        // Version-gated values
        ArmorStandAccess.setInvulnerable(stand, invulnerable);
        ArmorStandAccess.setDisabledSlots(stand, disabledSlots);
        if (ServerFeatures.HAS_GLOW) ArmorStandAccess.setGlowing(stand, glow);
        if (ServerFeatures.HAS_SILENT) ArmorStandAccess.setSilent(stand, silent);
        if (ServerFeatures.HAS_SCALE) ArmorStandAccess.setScale(stand, scale);

        // Marker last: a marker stand cannot have arms (vanilla semantics)
        stand.setMarker(marker);
        if (marker) stand.setArms(false);
    }

    private static ItemStack cloneOrNull(ItemStack item) {
        return item == null ? null : item.clone();
    }

    private static ItemStack[] cloneArray(ItemStack[] items) {
        if (items == null) return null;
        ItemStack[] clones = new ItemStack[items.length];
        for (int i = 0; i < items.length; i++) {
            clones[i] = items[i] == null ? null : items[i].clone();
        }
        return clones;
    }

    private static EulerAngle copy(EulerAngle angle) {
        return angle == null ? null : new EulerAngle(angle.getX(), angle.getY(), angle.getZ());
    }
}
