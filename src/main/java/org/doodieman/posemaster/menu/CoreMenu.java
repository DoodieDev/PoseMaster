package org.doodieman.posemaster.menu;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.doodieman.posemaster.menu.pages.PageEquipment;
import org.doodieman.posemaster.menu.pages.PageHome;
import org.doodieman.posemaster.menu.pages.PagePositions;
import org.doodieman.posemaster.menu.pages.PageProperties;
import org.doodieman.posemaster.objects.armorstands.PoseArmorStand;
import org.doodieman.posemaster.util.GUI;
import org.doodieman.posemaster.util.ItemBuilder;

import java.util.HashMap;
import java.util.Map;

public class CoreMenu extends GUI {

    final String type;
    @Getter
    private final PoseArmorStand poseArmorStand;
    private final Map<Integer, String> actionSlots = new HashMap<>();

    public CoreMenu(Player player, String type, PoseArmorStand poseArmorStand) {
        super(player, 5, "ID: &0"+poseArmorStand.getId());
        this.poseArmorStand = poseArmorStand;
        this.type = type;
    }

    @Override
    public void render() {
        //Add filler glass
        ItemStack fillerGlass = new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE)
                .name("")
                .build();
        for (int i = 0; i < 9; i++) {
            int slot = i + (4 * 9);
            this.layout.put(slot, fillerGlass);
        }

        //Home page item
        ItemStack pageHome = new ItemBuilder(Material.WRITABLE_BOOK)
                .name("&6Home")
                .lore("&6- &fCool lore", "", "&aLeft-click to open")
                .glow(this.type.equals("HOME"))
                .colorizeAll()
                .build();
        this.registerAction("PAGE_HOME", 38, pageHome);
        //Positions page item
        ItemStack pagePositions = new ItemBuilder(Material.ARMOR_STAND)
                .name("&6Positions")
                .lore("&7Here you can modify the ArmorStand", "&7exactly as you wish.", "", "&aLeft-click to open")
                .glow(this.type.equals("POSITIONS"))
                .colorizeAll()
                .build();
        this.registerAction("PAGE_POSITIONS", 39, pagePositions);
        //Equipment page item
        ItemStack pageEquipment = new ItemBuilder(Material.IRON_CHESTPLATE)
                .name("&6Equipment")
                .lore("&6- &fCool lore", "", "&aLeft-click to open")
                .glow(this.type.equals("EQUIPMENT"))
                .colorizeAll()
                .hideAttributes()
                .build();
        this.registerAction("PAGE_EQUIPMENT", 41, pageEquipment);
        //Properties page item
        ItemStack pageProperties = new ItemBuilder(Material.ANVIL)
                .name("&6Properties")
                .lore("&6- &fCool lore", "", "&aLeft-click to open")
                .glow(this.type.equals("PROPERTIES"))
                .colorizeAll()
                .build();
        this.registerAction("PAGE_PROPERTIES", 42, pageProperties);

        super.render();
    }

    public void onAction(String action, ClickType clickType) {}

    @Override
    public void click(int slot, ItemStack clickedItem, ClickType clickType, InventoryType inventoryType) {
        if (!actionSlots.containsKey(slot)) return;

        String action = actionSlots.get(slot);
        switch (action) {

            case "PAGE_HOME":
                if (this.type.equals("HOME")) break;
                new PageHome(this.player, this.poseArmorStand).open();
                break;

            case "PAGE_POSITIONS":
                if (this.type.equals("POSITIONS")) break;
                new PagePositions(this.player, this.poseArmorStand).open();
                break;

            case "PAGE_EQUIPMENT":
                if (this.type.equals("EQUIPMENT")) break;
                new PageEquipment(this.player, this.poseArmorStand).open();
                break;

            case "PAGE_PROPERTIES":
                if (this.type.equals("PROPERTIES")) break;
                new PageProperties(this.player, this.poseArmorStand).open();
                break;

            default:
                this.onAction(action, clickType);
                break;
        }

    }

    public void registerAction(String action, int slot, ItemStack item) {
        this.layout.put(slot, item);
        this.actionSlots.put(slot, action);
    }



}
