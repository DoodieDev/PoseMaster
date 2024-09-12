package org.doodieman.posemaster.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.A;
import org.doodieman.posemaster.PoseMaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUI implements Listener {

    public final Player player;
    @Getter
    final Inventory inventory;
    final int rows;
    final String title;

    public Map<Integer, ItemStack> layout = new HashMap<>();
    //Don't cancel click events on air slots
    @Setter
    private boolean allowAirSlots;
    //Allow specific slots to be unlocked
    @Getter
    private final List<Integer> unlockedSlots;

    public GUI(Player player, int rows, String title) {
        this.player = player;
        this.rows = rows;
        this.title = title;
        this.allowAirSlots = false;
        this.unlockedSlots = new ArrayList<>();
        this.inventory = Bukkit.createInventory(null, rows * 9, ChatColor.translateAlternateColorCodes('&', this.title));
    }

    public void click(int slot, ItemStack clickedItem, ClickType clickType, InventoryType inventoryType) { }
    public void closed() {}

    public void render() {
        this.inventory.clear();

        for (Map.Entry<Integer, ItemStack> layoutItem : this.layout.entrySet()) {
            inventory.setItem(layoutItem.getKey(),layoutItem.getValue());
        }
    }

    public void open() {
        this.render();
        player.openInventory(inventory);
        Bukkit.getPluginManager().registerEvents(this, PoseMaster.getInstance());
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!event.getInventory().equals(inventory)) return;
        if (event.getClickedInventory() == null) return;

        //Allow clicking unlocked slots
        if (this.unlockedSlots.contains(event.getRawSlot())) {
            this.click(event.getRawSlot(), event.getCurrentItem(), event.getClick(), event.getClickedInventory().getType());
            return;
        }
        //Allow clicking air slots
        if (this.allowAirSlots) {
            ItemStack item = this.layout.get(event.getRawSlot());
            if (item == null || item.getType() == Material.AIR) {
                this.click(event.getRawSlot(), event.getCurrentItem(), event.getClick(), event.getClickedInventory().getType());
                return;
            }
        }

        event.setCancelled(true);
        this.click(event.getRawSlot(), event.getCurrentItem(), event.getClick(), event.getClickedInventory().getType());
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent event) {
        if (!event.getInventory().equals(inventory)) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event) {
        if (!event.getInventory().equals(inventory)) return;

        HandlerList.unregisterAll(this);
        this.closed();
    }

}
