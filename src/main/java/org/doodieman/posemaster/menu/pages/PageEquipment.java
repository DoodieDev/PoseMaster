package org.doodieman.posemaster.menu.pages;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.doodieman.posemaster.PoseMaster;
import org.doodieman.posemaster.menu.CoreMenu;
import org.doodieman.posemaster.objects.armorstands.PoseArmorStand;
import org.doodieman.posemaster.objects.armorstands.PoseModifyEvent;
import org.doodieman.posemaster.util.ItemBuilder;

import java.util.HashMap;
import java.util.Map;

public class PageEquipment extends CoreMenu implements Listener {

    //Map of the equipment slots
    final Map<Integer, EquipmentSlot> equipmentSlots = new HashMap<>();

    public PageEquipment(Player player, PoseArmorStand poseArmorStand) {
        super(player,  "PAGE_EQUIPMENT", poseArmorStand);

        this.setAllowAirSlots(true);
        equipmentSlots.put(19, EquipmentSlot.OFF_HAND);
        equipmentSlots.put(20, EquipmentSlot.HAND);
        equipmentSlots.put(22, EquipmentSlot.HEAD);
        equipmentSlots.put(23, EquipmentSlot.CHEST);
        equipmentSlots.put(24, EquipmentSlot.LEGS);
        equipmentSlots.put(25, EquipmentSlot.FEET);

        Bukkit.getPluginManager().registerEvents(this, PoseMaster.getInstance());
    }

    @Override
    public void render() {
        this.layout.put(2, new ItemStack(Material.DIAMOND_HOE));

        //Fill with gray glass
        ItemStack emptyGlass = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .name("")
                .build();
        for (int i = 0; i < 36; i++) {
            if (this.equipmentSlots.containsKey(i))
                continue;
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

        super.render();

        //Load the equipment from ArmorStand
        this.loadEquipment();

    }

    @Override
    public void click(int slot, ItemStack clickedItem, ClickType clickType, InventoryType inventoryType) {
        super.click(slot, clickedItem, clickType, inventoryType);

        //What the hell am I even doing here
        new BukkitRunnable() {
            @Override
            public void run() {
                applyEquipment();
            }
        }.runTaskLater(PoseMaster.getInstance(), 1L);
    }

    @Override
    public void closed() {
        HandlerList.unregisterAll(this);
        super.closed();
    }

    //When the ArmorStand is modified by another player, update
    @EventHandler
    public void onModify(PoseModifyEvent event) {
        if (event.getPoseArmorStand() != this.getPoseArmorStand()) return;
        if (event.getModifier().equals(this.player)) return;
        this.loadEquipment();
    }

    //Load the ArmorStand equipment into the menu
    private void loadEquipment() {
        ArmorStand armorStand = this.getPoseArmorStand().getArmorStand();
        if (armorStand.getEquipment() == null) return;

        for (Map.Entry<Integer, EquipmentSlot> entry : this.equipmentSlots.entrySet()) {
            ItemStack item = armorStand.getEquipment().getItem(entry.getValue());
            this.getInventory().setItem(entry.getKey(), item);
        }
    }

    //Apply the equipment from the menu, to the ArmorStand
    private void applyEquipment() {
        ArmorStand armorStand = getPoseArmorStand().getArmorStand();
        if (armorStand.getEquipment() == null) return;

        for (Map.Entry<Integer, EquipmentSlot> entry : this.equipmentSlots.entrySet()) {
            ItemStack item = this.getInventory().getItem(entry.getKey());
            armorStand.getEquipment().setItem(entry.getValue(), item);
        }

        //Call event
        PoseModifyEvent event = new PoseModifyEvent(this.getPoseArmorStand(), this.player);
        Bukkit.getPluginManager().callEvent(event);
    }


}
