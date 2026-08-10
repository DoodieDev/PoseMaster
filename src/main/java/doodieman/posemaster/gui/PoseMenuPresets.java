package doodieman.posemaster.gui;

import doodieman.posemaster.PoseMaster;
import doodieman.posemaster.compat.MenuAssets;
import doodieman.posemaster.objects.Preset;
import doodieman.posemaster.utils.GUI;
import doodieman.posemaster.utils.ItemBuilder;
import doodieman.posemaster.utils.StringUtil;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PoseMenuPresets extends GUI {

    final ArmorStand armorStand;

    private final Map<Integer, String> actionSlots = new HashMap<>();
    private int page = 0;

    private final int presetsPerPage = 35;

    public PoseMenuPresets(Player player, ArmorStand armorStand) {
        super(player, 5, "ID: " + armorStand.getEntityId());
        this.armorStand = armorStand;
    }

    @Override
    public void open() {
        PoseMaster.getInstance().getLastMenuPageMap().put(player.getUniqueId(), "PRESETS");
        super.open();
    }

    @Override
    public void render() {

        this.layout.clear();
        this.actionSlots.clear();

        this.createBottomItems();

        List<Preset> presets = PoseMaster.getInstance().getPresetManager().getPresets();
        int totalPresets = presets.size();
        int maxPage = Math.max(0, (int) Math.ceil(totalPresets / (double) this.presetsPerPage) - 1);
        if (this.page > maxPage) this.page = maxPage;

        //Save preset
        this.layout.put(8, new ItemBuilder(Material.BOOK)
            .name("§d§lSave Preset")
            .lore("", "§fSaves the current ArmorStand", "§fvalues as a new preset.", "", "§aClick to save!")
            .build());
        this.actionSlots.put(8, "SAVE");

        //Preset entries
        int startIndex = this.page * this.presetsPerPage;
        int presetIndex = startIndex;
        for (int i = 0; i < (4 * 9); i++) {
            if (i == 8) continue;
            if (presetIndex >= totalPresets) continue;

            Preset preset = presets.get(presetIndex);
            presetIndex++;

            this.layout.put(i, new ItemBuilder(Material.PAPER)
                .name("§d" + preset.getName())
                .lore("", "§fLeft click: §aapply preset", "§fMiddle click: §cdelete preset")
                .build());
            this.actionSlots.put(i, preset.getName());
        }

        //Pagination
        if (this.page > 0) {
            this.layout.put(36, new ItemBuilder(Material.ARROW, "§f§lPrevious Page", "", "§aClick to go back a page!").build());
            this.actionSlots.put(36, "PREV");
        }

        this.layout.put(37, new ItemBuilder(MenuAssets.whiteGlassPane())
            .name("§fPage §7" + (this.page + 1) + "§f/§7" + (maxPage + 1))
            .build());

        if (this.page < maxPage) {
            this.layout.put(38, new ItemBuilder(Material.ARROW, "§f§lNext Page", "", "§aClick to go forward a page!").build());
            this.actionSlots.put(38, "NEXT");
        }

        super.render();
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
            .lore("", "§fThis page is for changing the", "§fequipment of the ArmorStand.", "", "§aClick to open!");
        ItemBuilder settings = new ItemBuilder(Material.ANVIL)
            .name("§8§lSettings")
            .lore("", "§fThis page is for changing the", "§fsettings of the ArmorStand.", "", "§aClick to open!");
        ItemBuilder presets = new ItemBuilder(Material.BOOK)
            .name("§d§lPresets")
            .lore("", "§fThis page is for saving and applying", "§fpresets to the ArmorStand.", "", "§7§oCurrently selected!")
            .makeGlowing();

        this.layout.put(39, position.build());
        this.actionSlots.put(39, "PAGE-POSITION");
        this.layout.put(40, equipment.build());
        this.actionSlots.put(40, "PAGE-EQUIPMENT");
        this.layout.put(41, settings.build());
        this.actionSlots.put(41, "PAGE-SETTINGS");
        this.layout.put(44, presets.build());
    }

    @Override
    public void click(int slot, ItemStack clickedItem, ClickType clickType, InventoryType inventoryType) {

        if (!actionSlots.containsKey(slot)) return;
        String action = actionSlots.get(slot);

        this.playClickSound();

        switch (action) {
            case "PAGE-POSITION":
                new PoseMenuPositions(player, armorStand).open();
                break;
            case "PAGE-EQUIPMENT":
                new PoseMenuEquipment(player, armorStand).open();
                break;
            case "PAGE-SETTINGS":
                new PoseMenuSettings(player, armorStand).open();
                break;

            case "PREV":
                this.page--;
                this.render();
                break;

            case "NEXT":
                this.page++;
                this.render();
                break;

            case "SAVE":
                player.closeInventory();
                PoseMaster.sendMessage(player, "§7Write the name of the preset.");

                new PoseAwaitResponse(player, 600L) {

                    @Override
                    public void onRespond(String message) {
                        String name = StringUtil.colorize(message).trim();
                        if (name.isEmpty()) {
                            PoseMaster.sendMessage(player, "§cThe name cannot be empty!");
                            return;
                        }

                        boolean overwritten = PoseMaster.getInstance().getPresetManager().getPreset(name) != null;
                        Preset preset = Preset.fromArmorStand(name, armorStand);
                        PoseMaster.getInstance().getPresetManager().savePreset(preset);

                        PoseMaster.sendMessage(player, (overwritten ? "§aOverwrote the preset §2'" + name + "§2'§a!" : "§aSaved the preset §2'" + name + "§2'§a!"));
                    }

                    @Override
                    public void onTimeout() {
                        PoseMaster.sendMessage(player, "§cYou took too long to respond!");
                    }
                };
                break;

            //Preset entry
            default:

                if (clickType == ClickType.MIDDLE) {
                    PoseMaster.getInstance().getPresetManager().deletePreset(action);
                    PoseMaster.sendMessage(player, "§cDeleted the preset §4'" + action + "§4'§c!");
                    this.render();
                    break;
                }

                Preset preset = PoseMaster.getInstance().getPresetManager().getPreset(action);
                if (preset == null) {
                    this.render();
                    break;
                }

                preset.applyTo(armorStand);
                PoseMaster.sendMessage(player, "§aApplied the preset §2'" + action + "§2'§a!");
                break;
        }
    }

}
