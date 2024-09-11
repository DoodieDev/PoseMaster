package org.doodieman.posemaster.objects.armorstands;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.EulerAngle;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ArmorStandProperties {

    private Map<ArmorStandProperty, Object> properties = new HashMap<>();

    // Write the properties to a ConfigurationSection
    public void writeToConfigSection(ConfigurationSection section) {
        for (Map.Entry<ArmorStandProperty, Object> entry : properties.entrySet()) {
            section.set(entry.getKey().toString(), entry.getValue());
        }
    }

    // Load the properties from a ConfigurationSection
    public static ArmorStandProperties fromConfigSection(ConfigurationSection section) {
        ArmorStandProperties armorStandProperties = new ArmorStandProperties();

        for (ArmorStandProperty property : ArmorStandProperty.values()) {
            Object value = section.get(property.toString());

            if (property.getType().isInstance(value)) {
                armorStandProperties.getProperties().put(property, value);
            }
            else {
                armorStandProperties.getProperties().put(property, property.getDefaultValue());
            }
        }

        return armorStandProperties;
    }

    //Get all the default properties from the enum
    public static ArmorStandProperties fromDefault() {
        ArmorStandProperties armorStandProperties = new ArmorStandProperties();

        for (ArmorStandProperty property : ArmorStandProperty.values()) {
            armorStandProperties.getProperties().put(property, property.getDefaultValue());
        }

        return armorStandProperties;
    }

    // Apply properties to an ArmorStand
    public void applyToArmorStand(ArmorStand armorStand) {
        for (Map.Entry<ArmorStandProperty, Object> entry : properties.entrySet()) {
            ArmorStandProperty property = entry.getKey();
            Object value = entry.getValue();

            if (value == null) continue;

            switch (property) {
                //Booleans
                case VISIBLE:
                    armorStand.setVisible((Boolean) value);
                    break;
                case SMALL:
                    armorStand.setSmall((Boolean) value);
                    break;
                case ARMS:
                    armorStand.setArms((Boolean) value);
                    break;
                case BASE_PLATE:
                    armorStand.setBasePlate((Boolean) value);
                    break;
                case CUSTOM_NAME_VISIBLE:
                    armorStand.setCustomNameVisible((Boolean) value);
                    break;
                case INVULNERABLE:
                    armorStand.setInvulnerable((Boolean) value);
                    break;
                case GRAVITY:
                    armorStand.setGravity((Boolean) value);
                    break;

                //EulerAngles
                case HEAD_POSE:
                    armorStand.setHeadPose((EulerAngle) value);
                    break;
                case BODY_POSE:
                    armorStand.setBodyPose((EulerAngle) value);
                    break;
                case LEFT_ARM_POSE:
                    armorStand.setLeftArmPose((EulerAngle) value);
                    break;
                case RIGHT_ARM_POSE:
                    armorStand.setRightArmPose((EulerAngle) value);
                    break;
                case LEFT_LEG_POSE:
                    armorStand.setLeftLegPose((EulerAngle) value);
                    break;
                case RIGHT_LEG_POSE:
                    armorStand.setRightLegPose((EulerAngle) value);
                    break;

                //Other
                case ROTATION:
                    float rotation = (float) value;
                    Location newLocation = armorStand.getLocation();
                    newLocation.setYaw(rotation);
                    armorStand.teleport(newLocation);
                    break;
                case CUSTOM_NAME:
                    armorStand.setCustomName((String) value);
                    break;
            }
        }
    }

    // Get the properties from an ArmorStand
    public static ArmorStandProperties fromArmorStand(ArmorStand armorStand) {
        ArmorStandProperties armorStandProperties = new ArmorStandProperties();

        for (ArmorStandProperty property : ArmorStandProperty.values()) {
            switch (property) {
                //Booleans
                case VISIBLE:
                    armorStandProperties.getProperties().put(property, armorStand.isVisible());
                    break;
                case SMALL:
                    armorStandProperties.getProperties().put(property, armorStand.isSmall());
                    break;
                case ARMS:
                    armorStandProperties.getProperties().put(property, armorStand.hasArms());
                    break;
                case BASE_PLATE:
                    armorStandProperties.getProperties().put(property, armorStand.hasBasePlate());
                    break;
                case CUSTOM_NAME_VISIBLE:
                    armorStandProperties.getProperties().put(property, armorStand.isCustomNameVisible());
                    break;
                case INVULNERABLE:
                    armorStandProperties.getProperties().put(property, armorStand.isInvulnerable());
                    break;
                case GRAVITY:
                    armorStandProperties.getProperties().put(property, armorStand.hasGravity());
                    break;

                // EulerAngles
                case HEAD_POSE:
                    armorStandProperties.getProperties().put(property, armorStand.getHeadPose());
                    break;
                case BODY_POSE:
                    armorStandProperties.getProperties().put(property, armorStand.getBodyPose());
                    break;
                case LEFT_ARM_POSE:
                    armorStandProperties.getProperties().put(property, armorStand.getLeftArmPose());
                    break;
                case RIGHT_ARM_POSE:
                    armorStandProperties.getProperties().put(property, armorStand.getRightArmPose());
                    break;
                case LEFT_LEG_POSE:
                    armorStandProperties.getProperties().put(property, armorStand.getLeftLegPose());
                    break;
                case RIGHT_LEG_POSE:
                    armorStandProperties.getProperties().put(property, armorStand.getRightLegPose());
                    break;

                // Other
                case ROTATION:
                    float rotation = armorStand.getLocation().getYaw();
                    armorStandProperties.getProperties().put(property, rotation);
                    break;
                case CUSTOM_NAME:
                    String name = armorStand.getCustomName();
                    if (name == null) break;
                    armorStandProperties.getProperties().put(property, name);
                    break;
            }
        }

        return armorStandProperties;
    }

}
