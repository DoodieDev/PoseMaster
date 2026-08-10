package doodieman.posemaster.gui;

import doodieman.posemaster.PoseMaster;
import doodieman.posemaster.compat.ArmorStandAccess;
import doodieman.posemaster.compat.MenuAssets;
import doodieman.posemaster.compat.ServerFeatures;
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
        super(player, 5, "ID: " + armorStand.getEntityId());
        this.setAllowAirSlots(true);
        this.armorStand = armorStand;
    }

    public void createBottomItems() {
        //Create glass fill
        for (int i = 0; i < 9; i++)
            this.layout.put(i + (4 * 9), GUIItem.GLASS_FILL.getItem());

        ItemBuilder position = new ItemBuilder(Material.ARMOR_STAND)
            .name("§e§lPosition")
            .lore("", "§fThis page is for changing the", "§fposition of the ArmorStand.", "", "§aClick to open!");
        ItemBuilder equipment = new ItemBuilder(Material.LEATHER_CHESTPLATE)
            .name("§6§lEquipment")
            .lore("", "§fThis page is for changing the", "§fequipment of the ArmorStand.", "", "§7§oCurrently selected!")
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
        this.layout.clear();
        this.actionSlots.clear();

        this.createBottomItems();

        int lastEquipmentSlot = ServerFeatures.HAS_OFF_HAND ? 25 : 24;
        for (int i = 0; i < (4 * 9); i++) {
            if (i >= 20 && i <= lastEquipmentSlot) continue;
            this.layout.put(i, MenuAssets.grayGlassPane());
        }

        this.layout.put(11, new ItemBuilder(Material.IRON_HELMET, "§f§lHelmet", "", "§fPlace the helmet of the", "§fArmorStand below.").build());
        this.layout.put(12, new ItemBuilder(Material.IRON_CHESTPLATE, "§f§lChestplate", "", "§fPlace the chestplate of the", "§fArmorStand below.").build());
        this.layout.put(13, new ItemBuilder(Material.IRON_LEGGINGS, "§f§lLeggings", "", "§fPlace the leggings of the", "§fArmorStand below.").build());
        this.layout.put(14, new ItemBuilder(Material.IRON_BOOTS, "§f§lBoots", "", "§fPlace the boots of the", "§fArmorStand below.").build());
        this.layout.put(15, new ItemBuilder(Material.IRON_SWORD, "§f§lMain Hand", "", "§fPlace the main-hand item of the", "§fArmorStand below.").build());
        if (ServerFeatures.HAS_OFF_HAND) {
            this.layout.put(16, new ItemBuilder(MenuAssets.shieldMaterial(), "§f§lOff Hand", "", "§fPlace the off-hand item of the", "§fArmorStand below.").build());
        }

        super.render();

        this.menu.setItem(20, armorStand.getHelmet());
        this.menu.setItem(21, armorStand.getChestplate());
        this.menu.setItem(22, armorStand.getLeggings());
        this.menu.setItem(23, armorStand.getBoots());
        this.menu.setItem(24, ArmorStandAccess.getMainHand(armorStand));
        if (ServerFeatures.HAS_OFF_HAND) {
            this.menu.setItem(25, ArmorStandAccess.getOffHand(armorStand));
        }
    }

    @Override
    public void click(int slot, ItemStack clickedItem, ClickType clickType, InventoryType inventoryType) {

        this.save();

        if (!actionSlots.containsKey(slot)) return;
        String action = actionSlots.get(slot);

        this.playClickSound();

        switch (action) {
            case "PAGE-EQUIPMENT":
                new PoseMenuEquipment(player, armorStand).open();
                break;
            case "PAGE-SETTINGS":
                new PoseMenuSettings(player, armorStand).open();
                break;
            case "PAGE-POSITION":
                new PoseMenuPositions(player, armorStand).open();
                break;

        }
    }

    @Override
    public void closed() {
        this.save();
    }

    public void save() {
        final ItemStack helmet = cloneOrAir(this.menu.getItem(20));
        final ItemStack chestplate = cloneOrAir(this.menu.getItem(21));
        final ItemStack leggings = cloneOrAir(this.menu.getItem(22));
        final ItemStack boots = cloneOrAir(this.menu.getItem(23));
        final ItemStack mainHand = cloneOrAir(this.menu.getItem(24));
        final ItemStack offHand = ServerFeatures.HAS_OFF_HAND ? cloneOrAir(this.menu.getItem(25)) : null;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!armorStand.isValid()) return;
                armorStand.setHelmet(helmet);
                armorStand.setChestplate(chestplate);
                armorStand.setLeggings(leggings);
                armorStand.setBoots(boots);
                ArmorStandAccess.setMainHand(armorStand, mainHand);
                if (offHand != null) ArmorStandAccess.setOffHand(armorStand, offHand);
            }
        }.runTaskLater(PoseMaster.getInstance(), 1L);
    }

    private ItemStack cloneOrAir(ItemStack item) {
        if (item == null) return new ItemStack(Material.AIR);
        return item.clone();
    }

}
