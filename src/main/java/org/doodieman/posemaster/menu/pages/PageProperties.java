package org.doodieman.posemaster.menu.pages;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.doodieman.posemaster.menu.CoreMenu;
import org.doodieman.posemaster.objects.armorstands.PoseArmorStand;

public class PageProperties extends CoreMenu {

    public PageProperties(Player player, PoseArmorStand poseArmorStand) {
        super(player,  "PAGE_PROPERTIES", poseArmorStand);

    }

    @Override
    public void render() {
        this.layout.put(3, new ItemStack(Material.DIAMOND_HOE));

        super.render();
    }

}
