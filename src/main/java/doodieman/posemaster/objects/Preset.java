package doodieman.posemaster.objects;

import doodieman.posemaster.compat.ArmorStandAccess;
import doodieman.posemaster.compat.ServerFeatures;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

/**
 * A named, persistent snapshot of an ArmorStand. Everything except the
 * position is captured; only the fractional part of the coordinates is kept,
 * so applying a preset snaps the stand's position onto the same decimals.
 */
public class Preset {

    @Getter
    private final String name;
    @Getter
    private final ArmorStandState state;
    @Getter
    private final double fractionX;
    @Getter
    private final double fractionY;
    @Getter
    private final double fractionZ;

    public Preset(String name, ArmorStandState state, double fractionX, double fractionY, double fractionZ) {
        this.name = name;
        this.state = state;
        this.fractionX = fractionX;
        this.fractionY = fractionY;
        this.fractionZ = fractionZ;
    }

    public static Preset fromArmorStand(String name, ArmorStand stand) {
        Location location = stand.getLocation();
        return new Preset(
            name,
            ArmorStandState.fromArmorStand(location, stand),
            fractionalPart(location.getX()),
            fractionalPart(location.getY()),
            fractionalPart(location.getZ())
        );
    }

    /**
     * Applies every captured value to the stand. The integer part of the
     * current position is kept, the fractional parts of the preset are
     * written on top of it.
     */
    public void applyTo(ArmorStand stand) {
        this.state.applyTo(stand);

        Location newLocation = stand.getLocation().clone();
        newLocation.setX(Math.floor(newLocation.getX()) + this.fractionX);
        newLocation.setY(Math.floor(newLocation.getY()) + this.fractionY);
        newLocation.setZ(Math.floor(newLocation.getZ()) + this.fractionZ);
        newLocation.setYaw((float) this.state.getRotation());
        stand.teleport(newLocation);
    }

    private static double fractionalPart(double value) {
        return value - Math.floor(value);
    }

    public void save(ConfigurationSection section) {
        section.set("name", this.name);
        section.set("rotation", this.state.getRotation());

        section.set("locationFraction.x", this.fractionX);
        section.set("locationFraction.y", this.fractionY);
        section.set("locationFraction.z", this.fractionZ);

        if (this.state.getMainHand() != null) section.set("mainHand", this.state.getMainHand().serialize());
        if (this.state.getArmorContents() != null) {
            for (int i = 0; i < this.state.getArmorContents().length; i++) {
                if (this.state.getArmorContents()[i] == null) continue;
                section.set("armorContents." + i, this.state.getArmorContents()[i].serialize());
            }
        }
        if (ServerFeatures.HAS_OFF_HAND && this.state.getOffHand() != null) section.set("offHand", this.state.getOffHand().serialize());

        section.set("visible", this.state.isVisible());
        section.set("small", this.state.isSmall());
        section.set("arms", this.state.isArms());
        section.set("basePlate", this.state.isBasePlate());
        section.set("gravity", this.state.isGravity());
        section.set("marker", this.state.isMarker());

        if (this.state.getCustomName() != null) section.set("customName", this.state.getCustomName());
        section.set("customNameVisible", this.state.isCustomNameVisible());

        savePose(section, "bodyPose", this.state.getBodyPose());
        savePose(section, "headPose", this.state.getHeadPose());
        savePose(section, "leftArmPose", this.state.getLeftArmPose());
        savePose(section, "rightArmPose", this.state.getRightArmPose());
        savePose(section, "leftLegPose", this.state.getLeftLegPose());
        savePose(section, "rightLegPose", this.state.getRightLegPose());

        section.set("invulnerable", this.state.isInvulnerable());
        section.set("disabledSlots", this.state.getDisabledSlots());

        if (ServerFeatures.HAS_GLOW) section.set("glow", this.state.isGlow());
        if (ServerFeatures.HAS_GLOW_COLOR && this.state.getGlowColor() != null) section.set("glowColor", this.state.getGlowColor().asRGB());
        if (ServerFeatures.HAS_SILENT) section.set("silent", this.state.isSilent());
        if (ServerFeatures.HAS_SCALE) section.set("scale", this.state.getScale());
    }

    public static Preset load(ConfigurationSection section) {
        if (section == null) return null;

        ArmorStandState state = new ArmorStandState();
        state.setRotation(section.getDouble("rotation", 0.0));

        try {
            state.setMainHand(deserializeItem(section.getConfigurationSection("mainHand")));
            if (section.contains("armorContents")) {
                ItemStack[] contents = new ItemStack[4];
                for (int i = 0; i < contents.length; i++) {
                    contents[i] = deserializeItem(section.getConfigurationSection("armorContents." + i));
                }
                state.setArmorContents(contents);
            }
            if (ServerFeatures.HAS_OFF_HAND) state.setOffHand(deserializeItem(section.getConfigurationSection("offHand")));
        } catch (IllegalArgumentException ignored) {
        }

        state.setVisible(section.getBoolean("visible", true));
        state.setSmall(section.getBoolean("small", false));
        state.setArms(section.getBoolean("arms", true));
        state.setBasePlate(section.getBoolean("basePlate", true));
        state.setGravity(section.getBoolean("gravity", true));
        state.setMarker(section.getBoolean("marker", false));

        if (section.contains("customName")) state.setCustomName(section.getString("customName"));
        state.setCustomNameVisible(section.getBoolean("customNameVisible", false));

        state.setBodyPose(loadPose(section, "bodyPose"));
        state.setHeadPose(loadPose(section, "headPose"));
        state.setLeftArmPose(loadPose(section, "leftArmPose"));
        state.setRightArmPose(loadPose(section, "rightArmPose"));
        state.setLeftLegPose(loadPose(section, "leftLegPose"));
        state.setRightLegPose(loadPose(section, "rightLegPose"));

        state.setInvulnerable(section.getBoolean("invulnerable", true));
        state.setDisabledSlots(section.getInt("disabledSlots", ArmorStandAccess.EQUIPMENT_LOCK_MASK));

        if (ServerFeatures.HAS_GLOW) state.setGlow(section.getBoolean("glow", false));
        if (ServerFeatures.HAS_GLOW_COLOR && section.contains("glowColor")) state.setGlowColor(Color.fromRGB(section.getInt("glowColor")));
        if (ServerFeatures.HAS_SILENT) state.setSilent(section.getBoolean("silent", false));
        if (ServerFeatures.HAS_SCALE) state.setScale(section.getDouble("scale", 1.0));

        String name = section.getString("name");
        if (name == null) return null;

        ConfigurationSection fractionSection = section.getConfigurationSection("locationFraction");
        double fractionX = fractionSection == null ? 0.0 : fractionSection.getDouble("x", 0.0);
        double fractionY = fractionSection == null ? 0.0 : fractionSection.getDouble("y", 0.0);
        double fractionZ = fractionSection == null ? 0.0 : fractionSection.getDouble("z", 0.0);

        return new Preset(name, state, fractionX, fractionY, fractionZ);
    }

    private static void savePose(ConfigurationSection section, String path, EulerAngle pose) {
        if (pose == null) return;
        section.set(path + ".x", pose.getX());
        section.set(path + ".y", pose.getY());
        section.set(path + ".z", pose.getZ());
    }

    private static EulerAngle loadPose(ConfigurationSection section, String path) {
        ConfigurationSection poseSection = section.getConfigurationSection(path);
        if (poseSection == null) return new EulerAngle(0, 0, 0);
        return new EulerAngle(poseSection.getDouble("x"), poseSection.getDouble("y"), poseSection.getDouble("z"));
    }

    private static ItemStack deserializeItem(ConfigurationSection section) {
        if (section == null) return null;
        try {
            return ItemStack.deserialize(section.getValues(false));
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
}
