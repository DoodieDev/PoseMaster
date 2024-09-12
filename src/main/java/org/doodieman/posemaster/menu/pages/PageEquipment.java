package org.doodieman.posemaster.menu.pages;

import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.doodieman.posemaster.menu.CoreMenu;
import org.doodieman.posemaster.objects.armorstands.PoseArmorStand;
import org.doodieman.posemaster.util.ItemBuilder;

public class PageEquipment extends CoreMenu {

    public PageEquipment(Player player, PoseArmorStand poseArmorStand) {
        super(player,  "PAGE_EQUIPMENT", poseArmorStand);
    }

    @Override
    public void render() {
        this.layout.put(2, new ItemStack(Material.DIAMOND_HOE));

        //Fill with gray glass
        ItemStack emptyGlass = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .name("")
                .build();
        for (int i = 0; i < 36; i++) {
            this.layout.put(i, emptyGlass);
        }

        //Display slots
        this.layout.put(10, new ItemBuilder(Material.SHIELD)
                .name("&6Off-hand")
                .lore("&7Place an item below")
                .hideAttributes().colorizeAll().build());
        this.layout.put(11, new ItemBuilder(Material.IRON_SWORD)
                .name("&6Main hand")
                .lore("&7Place an item below")
                .hideAttributes().colorizeAll().build());

        this.layout.put(13, new ItemBuilder(Material.IRON_HELMET)
                .name("&6Helmet")
                .lore("&7Place an item below")
                .hideAttributes().colorizeAll().build());
        this.layout.put(14, new ItemBuilder(Material.IRON_CHESTPLATE)
                .name("&6Chestplate")
                .lore("&7Place an item below")
                .hideAttributes().colorizeAll().build());
        this.layout.put(15, new ItemBuilder(Material.IRON_LEGGINGS)
                .name("&6Leggings")
                .lore("&7Place an item below")
                .hideAttributes().colorizeAll().build());
        this.layout.put(16, new ItemBuilder(Material.IRON_BOOTS)
                .name("&6Boots")
                .lore("&7Place an item below")
                .hideAttributes().colorizeAll().build());

        //Load the equipment from ArmorStand
        this.loadEquipment();

        super.render();
    }

    private void loadEquipment() {
        ArmorStand armorStand = this.getPoseArmorStand().getArmorStand();
        if (armorStand.getEquipment() == null) return;
        //Offhand
        ItemStack offhand = armorStand.getEquipment().getItemInOffHand();
        this.layout.put(19, offhand);
        //Main hand
        ItemStack mainHand = armorStand.getEquipment().getItemInMainHand();
        this.layout.put(20, mainHand);
        //Helmet
        ItemStack helmet = armorStand.getEquipment().getHelmet();
        this.layout.put(22, helmet);
        //Chestplate
        ItemStack chestplate = armorStand.getEquipment().getChestplate();
        this.layout.put(23, chestplate);
        //Leggings
        ItemStack leggings = armorStand.getEquipment().getLeggings();
        this.layout.put(24, leggings);
        //Boots
        ItemStack boots = armorStand.getEquipment().getBoots();
        this.layout.put(25, boots);
    }

}
