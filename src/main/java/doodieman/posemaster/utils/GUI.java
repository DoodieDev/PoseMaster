package doodieman.posemaster.utils;

import doodieman.posemaster.PoseMaster;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GUI implements Listener {
    protected Player player;
    protected Inventory menu;

    public Map<Integer, ItemStack> layout = new HashMap<>();

    public final Long clickCooldown = 100L;
    private Long currentCooldown = 0L;


    private final int rows;
    private final String name;
    @Setter
    private boolean allowAirSlots;

    public GUI(Player player, int rows, String name) {
        this.player = player;
        this.menu = Bukkit.createInventory(null, (rows*9), name);

        this.rows = rows;
        this.name = name;
        this.allowAirSlots = false;
    }

    public void render() {
        menu.clear();

        for (Map.Entry<Integer, ItemStack> layoutItem : layout.entrySet()) {
            menu.setItem(layoutItem.getKey(),layoutItem.getValue());
        }
    }

    public enum GUIItem {

        EXIT_MENU(new ItemBuilder(Material.NETHER_STAR,1, (byte) 0,"§fLuk menuen", "§7§oTryk for at lukke menuen.").build()),
        GLASS_FILL(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 0, "§r").build()),
        BACK(new ItemBuilder(Material.ARROW, 1, (byte) 0, "§fTilbage","§7§oTryk for at gå tilbage","§7§otil den forrige menu.").build());

        private final ItemStack item;
        GUIItem(ItemStack item) {
            this.item = item;
        }
        public ItemStack getItem() {
            return this.item;
        }
    }

    //This is the event called when clicking.
    public void click(int slot, ItemStack clickedItem, ClickType clickType, InventoryType inventoryType) { }
    public void closed() {}


    //Click cooldown.
    private void setCooldown() {
        this.currentCooldown = System.currentTimeMillis();
    }
    private boolean hasCooldown() {
        return System.currentTimeMillis() < currentCooldown + clickCooldown;
    }

    //Open the menu to a player here
    public void open() {
        this.render();
        player.openInventory(menu);
        PoseMaster.getInstance().getServer().getPluginManager().registerEvents(this, PoseMaster.getInstance());
    }

    public void renameTitle(String newName) {
        this.menu = Bukkit.createInventory(null, (rows*9), newName);
        this.render();
        HandlerList.unregisterAll(this); // Unregister events while re-opening
        player.openInventory(menu);
        PoseMaster.getInstance().getServer().getPluginManager().registerEvents(this, PoseMaster.getInstance());
    }

    public void playClickSound() {
        player.playSound(player.getLocation(), Sound.CLICK,0.5f,1.2f);
    }

    //EVENT LISTENERS
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!event.getInventory().equals(menu)) return;
        if(event.getClickedInventory() == null) return;

        //Allow clicking air slots
        if (allowAirSlots && !layout.containsKey(event.getRawSlot())) {
            this.click(event.getRawSlot(), event.getCurrentItem(), event.getClick(), event.getClickedInventory().getType());
            return;
        }
        event.setCancelled(true);

        if(hasCooldown()) return;
        setCooldown();



        this.click(event.getRawSlot(), event.getCurrentItem(), event.getClick(), event.getClickedInventory().getType());
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent event) {
        if (!event.getInventory().equals(menu)) return;
        if (!allowAirSlots) {
            event.setCancelled(true);
            return;
        }

        boolean cancel = false;
        for (int slot : event.getRawSlots()) {
            if (this.layout.containsKey(slot))
                cancel = true;
        }
        event.setCancelled(cancel);
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event) {
        if (event.getInventory().equals(menu)) {
            HandlerList.unregisterAll(this);
            this.closed();
        }
    }
}
