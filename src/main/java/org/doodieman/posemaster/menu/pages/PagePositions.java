package org.doodieman.posemaster.menu.pages;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.doodieman.posemaster.menu.CoreMenu;
import org.doodieman.posemaster.objects.PoseArmorStand;

public class PagePositions extends CoreMenu {

    public PagePositions(Player player, PoseArmorStand poseArmorStand) {
        super(player,  "POSITIONS", poseArmorStand);
    }

    @Override
    public void render() {
        this.layout.put(1, new ItemStack(Material.DIAMOND_HOE));

        super.render();
    }

}
