package doodieman.posemaster.gui;

import doodieman.posemaster.PoseMaster;
import doodieman.posemaster.utils.GUI;
import doodieman.posemaster.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class PoseMenuEquipment extends GUI {

    final ArmorStand armorStand;

    private final Map<Integer, String> actionSlots = new HashMap<>();

    public PoseMenuEquipment(Player player, ArmorStand armorStand) {
        super(player, 5, "ID: "+armorStand.getEntityId());
        this.setAllowAirSlots(true);
        this.armorStand = armorStand;
    }

    public void createBottomItems() {
        //Create glass fill
        for (int i = 0; i < 9; i++)
            this.layout.put(i+(4*9), GUIItem.GLASS_FILL.getItem());

        ItemBuilder position = new ItemBuilder(Material.ARMOR_STAND)
            .name("§e§lPosition")
            .lore("", "§fThis page is for changing the", "§fposition of the ArmorStand.", "", "§aClick to open!");
        ItemBuilder equipment = new ItemBuilder(Material.LEATHER_CHESTPLATE)
            .name("§6§lEquipment")
            .lore( "", "§fThis page is for changing the", "§fequipment of the ArmorStand.", "", "§7§oCurrently selected!")
            .makeGlowing();
        ItemBuilder settings = new ItemBuilder(Material.ANVIL)
            .name("§8§lSettings")
            .lore("", "§fThis page is for changing the", "§fsettings of the ArmorStand.", "", "§aClick to open!");

        this.layout.put(39, position.build());
        this.actionSlots.put(39, "PAGE-POSITION");
        this.layout.put(40, equipment.build());
        this.layout.put(41, settings.build());
        this.actionSlots.put(41, "PAGE-SETTINGS");
    }

    @Override
    public void render() {
        this.createBottomItems();

        for (int i = 0; i < (4*9); i++) {
            if (i >= 20 && i <= 24) continue;
            this.layout.put(i,new ItemBuilder(Material.STAINED_GLASS_PANE, "").setDurability((short) 7).build());
        }

        this.layout.put(11,new ItemBuilder(Material.IRON_HELMET,"§f§lHelmet","", "§fPlace the helmet of the", "§fArmorStand below.").build());
        this.layout.put(12,new ItemBuilder(Material.IRON_CHESTPLATE,"§f§lChestplate","", "§fPlace the chestplate of the", "§fArmorStand below.").build());
        this.layout.put(13,new ItemBuilder(Material.IRON_LEGGINGS,"§f§lLeggings","", "§fPlace the leggings of the", "§fArmorStand below.").build());
        this.layout.put(14,new ItemBuilder(Material.IRON_BOOTS,"§f§lBoots","", "§fPlace the boots of the", "§fArmorStand below.").build());
        this.layout.put(15,new ItemBuilder(Material.IRON_SWORD,"§f§lTool","", "§fPlace the tool of the", "§fArmorStand below.").build());

        super.render();

        this.menu.setItem(20, armorStand.getHelmet());
        this.menu.setItem(21, armorStand.getChestplate());
        this.menu.setItem(22, armorStand.getLeggings());
        this.menu.setItem(23, armorStand.getBoots());
        this.menu.setItem(24, armorStand.getItemInHand());
    }

    @Override
    public void click(int slot, ItemStack clickedItem, ClickType clickType, InventoryType inventoryType) {

        this.save();

        if (!actionSlots.containsKey(slot)) return;
        String action = actionSlots.get(slot);

        this.playClickSound();

        switch (action) {
            case "PAGE-EQUIPMENT":
                new PoseMenuEquipment(player,armorStand).open();
                break;
            case "PAGE-SETTINGS":
                new PoseMenuSettings(player,armorStand).open();
                break;
            case "PAGE-POSITION":
                new PoseMenuPositions(player,armorStand).open();
                break;

        }
    }
    @Override
    public void closed() {
        this.save();
    }

    public void save() {
        new BukkitRunnable() {
            @Override
            public void run() {
                armorStand.setHelmet(menu.getItem(20));
                armorStand.setChestplate(menu.getItem(21));
                armorStand.setLeggings(menu.getItem(22));
                armorStand.setBoots(menu.getItem(23));
                armorStand.setItemInHand(menu.getItem(24));
            }
        }.runTaskLater(PoseMaster.getInstance(),1L);
    }

}
