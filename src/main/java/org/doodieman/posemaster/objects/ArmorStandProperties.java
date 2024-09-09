package org.doodieman.posemaster.objects;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ArmorStandProperties {

    private Map<String, Object> properties = new HashMap<>();

    //Write the properties to a ConfigurationSection
    public void writeToConfigSection(ConfigurationSection section) {
        for (Map.Entry<String, Object> entry : properties.entrySet())
            section.set(entry.getKey(), entry.getValue());
    }
    //Load the properties from a section
    public static ArmorStandProperties fromConfigSection(ConfigurationSection section) {
        ArmorStandProperties properties = new ArmorStandProperties();

        for (String key : section.getKeys(false))
            properties.getProperties().put(key, section.get(key));

        return properties;
    }


    //Apply properties to an ArmorStand
    public void applyToArmorStand(ArmorStand armorStand) {
        armorStand.setVisible((boolean) properties.getOrDefault("visible", armorStand.isVisible()));
        armorStand.setArms((boolean) properties.getOrDefault("arms", armorStand.hasArms()));
        armorStand.setSmall((boolean) properties.getOrDefault("small", armorStand.isSmall()));
    }

    //Get the properties from an ArmorStand
    public static ArmorStandProperties fromArmorStand(ArmorStand armorStand) {
        ArmorStandProperties properties = new ArmorStandProperties();

        properties.getProperties().put("visible", armorStand.isVisible());
        properties.getProperties().put("arms", armorStand.hasArms());
        properties.getProperties().put("small", armorStand.isSmall());

        return properties;
    }
}
