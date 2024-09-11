package org.doodieman.posemaster.config.lang;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.doodieman.posemaster.PoseMaster;

import java.util.Map;

public enum LangConfig {

    PREFIX("prefix", "&6&l[&ePM&6&l]&f"),

    CANNOT_DESTROY("cannot-destroy", "%prefix% &cYou can't break this without a poisonous potato!"),

    TYPE_MOVE_OFFSET("type-move-offset", "%prefix% Type the offset you want it to move.\n%prefix% &7Example: &e0 2 0 &7(Moves up by 2 blocks)"),
    TYPE_MOVE_DIRECTION("type-move-direction", "%prefix% Type how many blocks you want it to move.\n%prefix% &7Example: &e5 &7(Moves 5 blocks in facing direction)"),

    MOVE_SUCCESS("move-success", "%prefix% &aSuccessfully moved the ArmorStand!"),

    TYPE_TELEPORT_LOCATION("type-teleport-location", "%prefix% Type the coordinates you want it to teleport to.\n%prefix% &7Example: &e230 65 45 &7(Teleports to the coordinates)"),
    TELEPORT_SUCCESS("teleport-success", "%prefix% &aSuccessfully teleported the ArmorStand!"),
    TYPE_ROTATION("type-rotation", "%prefix% Type the new rotation within 0-360 degrees."),
    ROTATION_SUCCESS("rotation-success", "%prefix% &aSuccessfully rotated the ArmorStand to %degrees% degrees!"),
    TYPE_ANGLE("type-angle", "%prefix% Type the new angle within 0-360 degrees."),
    ANGLE_SUCCESS("angle-success", "%prefix% &aSuccessfully updated the angle to %degrees% degrees!"),
    RESPONSE_TYPE_INVALID("response-type-invalid", "%prefix% &4'%response%' &cis not a valid %type%!");

    @Getter
    private final String key;
    @Getter
    private final String defaultMessage;

    LangConfig(String key, String defaultMessage) {
        this.key = key;
        this.defaultMessage = defaultMessage;
    }

    private LangConfigHandler getHandler() {
        return PoseMaster.getInstance().getLangConfigHandler();
    }

    // Get the message with optional placeholders and color formatting
    public String getMessage(Map<String, String> placeholders, boolean colorize) {
        FileConfiguration config = getHandler().getConfig();

        String message = config.getString(this.key, this.defaultMessage);
        String prefix = config.getString(PREFIX.getKey(), PREFIX.getDefaultMessage());

        message = message.replace("%prefix%", prefix);

        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace(entry.getKey(), entry.getValue());
            }
        }

        return colorize ? ChatColor.translateAlternateColorCodes('&', message) : message;
    }

    public String getMessage() {
        return getMessage(null, false);
    }

    public String getMessage(Map<String, String> placeholders) {
        return getMessage(placeholders, false);
    }

    public String getColoredMessage() {
        return getMessage(null, true);
    }

    public String getColoredMessage(Map<String, String> placeholders) {
        return getMessage(placeholders, true);
    }

}
