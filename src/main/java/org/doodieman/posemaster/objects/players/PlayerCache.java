package org.doodieman.posemaster.objects.players;

import lombok.Getter;
import lombok.Setter;
import org.doodieman.posemaster.menu.CoreMenu;
import org.doodieman.posemaster.objects.armorstands.PoseArmorStand;

import java.util.UUID;

public class PlayerCache {

    @Getter
    private final UUID uuid;

    //The last PoseArmorStand modified/interacted with
    @Getter @Setter
    private PoseArmorStand lastPoseArmorStand;

    //The last opened page type in a menu
    @Getter @Setter
    private String lastMenuPage;

    public PlayerCache(UUID uuid) {
        this.uuid = uuid;
    }

}
