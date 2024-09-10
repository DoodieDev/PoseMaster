package org.doodieman.posemaster.objects.armorstands;

import lombok.Getter;
import org.bukkit.util.EulerAngle;

public enum ArmorStandProperty {

    //Booleans
    VISIBLE(Boolean.class, true),
    SMALL(Boolean.class, false),
    ARMS(Boolean.class, true),
    BASE_PLATE(Boolean.class, true),
    CUSTOM_NAME_VISIBLE(Boolean.class, false),
    INVULNERABLE(Boolean.class, true),

    //EulerAngles
    HEAD_POSE(EulerAngle.class, new EulerAngle(0, 0, 0)),
    BODY_POSE(EulerAngle.class, new EulerAngle(0, 0, 0)),

    LEFT_ARM_POSE(EulerAngle.class, new EulerAngle(0, 0, 0)),
    RIGHT_ARM_POSE(EulerAngle.class, new EulerAngle(0, 0, 0)),

    LEFT_LEG_POSE(EulerAngle.class, new EulerAngle(0, 0, 0)),
    RIGHT_LEG_POSE(EulerAngle.class, new EulerAngle(0, 0, 0)),

    //Other
    ROTATION(Float.class, null),
    CUSTOM_NAME(String.class, null);

    @Getter
    private final Class<?> type;
    @Getter
    private final Object defaultValue;

    ArmorStandProperty(Class<?> type, Object defaultValue) {
        this.type = type;
        this.defaultValue = defaultValue;
    }

}
