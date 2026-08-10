package doodieman.posemaster.gui;

import doodieman.posemaster.PoseMaster;
import doodieman.posemaster.compat.ArmorStandAccess;
import doodieman.posemaster.compat.MenuAssets;
import doodieman.posemaster.compat.ServerFeatures;
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
import java.util.Map;

public class PoseMenuSettings extends GUI {

    final ArmorStand armorStand;

    private final Map<Integer, String> actionSlots = new HashMap<>();


    public PoseMenuSettings(Player player, ArmorStand armorStand) {
        super(player, 5, "ID: " + armorStand.getEntityId());
        this.armorStand = armorStand;
    }

    @Override
    public void open() {
        PoseMaster.getInstance().getLastMenuPageMap().put(player.getUniqueId(), "SETTINGS");
        super.open();
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
            .lore("", "§fThis page is for changing the", "§fsettings of the ArmorStand.", "", "§7§oCurrently selected!")
            .makeGlowing();
        ItemBuilder presets = new ItemBuilder(Material.BOOK)
            .name("§d§lPresets")
            .lore("", "§fThis page is for saving and applying", "§fpresets to the ArmorStand.", "", "§aClick to open!");

        this.layout.put(39, position.build());
        this.actionSlots.put(39, "PAGE-POSITION");
        this.layout.put(40, equipment.build());
        this.actionSlots.put(40, "PAGE-EQUIPMENT");
        this.layout.put(41, settings.build());
        this.layout.put(44, presets.build());
        this.actionSlots.put(44, "PAGE-PRESETS");
    }

    @Override
    public void render() {

        //Dynamic layout: rebuilt from scratch on every render
        this.layout.clear();
        this.actionSlots.clear();

        //Render bottom items
        this.createBottomItems();

        //Items are sorted by their lowest supported server version:
        //lowest version at the top, highest version at the bottom.

        //----- 1.8.8+ -----

        //Small
        String currentSmall = armorStand.isSmall() ? "§aEnabled" : "§cDisabled";
        ItemBuilder small = new ItemBuilder(MenuAssets.grassMaterial())
            .name("§e§lSmall")
            .lore("", "§7Current: " + currentSmall, "", "§aClick to toggle!");
        if (armorStand.isSmall()) small.makeGlowing();

        //Marker (1.8.8+)
        String currentMarker = armorStand.isMarker() ? "§aEnabled" : "§cDisabled";
        ItemBuilder marker = new ItemBuilder(MenuAssets.carpetMaterial())
            .name("§9§lMarker")
            .lore("", "§7Current: " + currentMarker, "", "§aClick to toggle!", "", "§cWarning: marker stands have no", "§chitbox and cannot be clicked", "§cor hit anymore!", "§7Right-click with the poisonous", "§7potato nearby to edit, left-click", "§7to remove.");
        if (armorStand.isMarker()) marker.makeGlowing();

        //Arms
        String currentArms = armorStand.hasArms() ? "§aEnabled" : "§cDisabled";
        ItemBuilder arms = new ItemBuilder(Material.STICK)
            .name("§6§lArms")
            .lore("", "§7Current: " + currentArms, "", "§aClick to toggle!");
        if (armorStand.hasArms()) arms.makeGlowing();

        //Gravity
        String currentGravity = armorStand.hasGravity() ? "§aEnabled" : "§cDisabled";
        ItemBuilder gravity = new ItemBuilder(Material.FEATHER)
            .name("§f§lGravity")
            .lore("", "§7Current: " + currentGravity, "", "§aClick to toggle!");
        if (armorStand.hasGravity()) gravity.makeGlowing();

        //Baseplate
        String currentBaseplate = armorStand.hasBasePlate() ? "§aEnabled" : "§cDisabled";
        ItemBuilder baseplate = new ItemBuilder(MenuAssets.stoneSlabMaterial())
            .name("§7§lBaseplate")
            .lore("", "§7Current: " + currentBaseplate, "", "§aClick to toggle!");
        if (armorStand.hasBasePlate()) baseplate.makeGlowing();

        //Visible
        String currentVisible = armorStand.isVisible() ? "§aEnabled" : "§cDisabled";
        ItemBuilder visible = new ItemBuilder(Material.GLASS_BOTTLE)
            .name("§b§lVisibility")
            .lore("", "§7Current: " + currentVisible, "", "§aClick to toggle!");
        if (armorStand.isVisible()) visible.makeGlowing();
        if (armorStand.isVisible()) visible.material(Material.POTION);

        //Name visibility
        String currentNameVisible = armorStand.isCustomNameVisible() ? "§aEnabled" : "§cDisabled";
        ItemBuilder nameVisible = new ItemBuilder(Material.NAME_TAG)
            .name("§e§lName Visibility")
            .lore("", "§7Current: " + currentNameVisible, "", "§aClick to toggle!");
        if (armorStand.isCustomNameVisible()) nameVisible.makeGlowing();

        //Change name
        String currentName = armorStand.getCustomName() == null ? "§fArmor Stand" : StringUtil.colorize(armorStand.getCustomName());
        ItemBuilder changeName = new ItemBuilder(MenuAssets.signMaterial())
            .name("§e§lChange Name")
            .lore("", "§7Current: " + currentName, "", "§aClick to change!");
        if (armorStand.getCustomName() != null) changeName.makeGlowing();

        //----- 1.9+ -----

        //Invulnerable
        String currentInvul = ArmorStandAccess.isInvulnerable(armorStand) ? "§aEnabled" : "§cDisabled";
        ItemBuilder invulnerable = new ItemBuilder(Material.GOLDEN_APPLE)
            .name("§e§lInvulnerable")
            .lore("", "§7Current: " + currentInvul, "", "§aClick to toggle!");
        if (ArmorStandAccess.isInvulnerable(armorStand)) invulnerable.makeGlowing();

        //Glow (1.9+)
        ItemBuilder glow = null;
        if (ServerFeatures.HAS_GLOW) {
            String currentGlow = ArmorStandAccess.isGlowing(armorStand) ? "§aEnabled" : "§cDisabled";
            glow = new ItemBuilder(Material.GLOWSTONE_DUST)
                .name("§e§lGlow")
                .lore("", "§7Current: " + currentGlow, "", "§aClick to toggle!");
            if (ArmorStandAccess.isGlowing(armorStand)) glow.makeGlowing();
        }

        //Silent (1.9+)
        ItemBuilder silent = null;
        if (ServerFeatures.HAS_SILENT) {
            String currentSilent = ArmorStandAccess.isSilent(armorStand) ? "§aEnabled" : "§cDisabled";
            silent = new ItemBuilder(MenuAssets.woolMaterial())
                .name("§7§lSilent")
                .lore("", "§7Current: " + currentSilent, "", "§aClick to toggle!");
            if (ArmorStandAccess.isSilent(armorStand)) silent.makeGlowing();
        }

        //----- 1.16.2+ -----

        //Equipment lock
        String currentEquipLock = ArmorStandAccess.getDisabledSlots(armorStand) == ArmorStandAccess.EQUIPMENT_LOCK_MASK ? "§aEnabled" : "§cDisabled";
        ItemBuilder equiplock = new ItemBuilder(Material.IRON_CHESTPLATE)
            .name("§f§lEquipment Lock")
            .lore("", "§7Current: " + currentEquipLock, "", "§aClick to toggle!");
        if (ArmorStandAccess.getDisabledSlots(armorStand) == ArmorStandAccess.EQUIPMENT_LOCK_MASK) equiplock.makeGlowing();

        //----- 1.20.5+ -----

        //Scale (1.20.5+)
        ItemBuilder scale = null;
        if (ServerFeatures.HAS_SCALE) {
            String currentScale = StringUtil.roundTwoDecimals(ArmorStandAccess.getScale(armorStand)) + "";
            scale = new ItemBuilder(Material.QUARTZ)
                .name("§d§lScale")
                .lore("", "§7Current: §f" + currentScale, "", "§aClick to change!");
            if (ArmorStandAccess.getScale(armorStand) != 1.0) scale.makeGlowing();
        }

        //Delete (always the last item)
        ItemBuilder delete = new ItemBuilder(Material.TNT)
            .name("§c§lDelete ArmorStand")
            .lore("", "§cClick to delete!");

        //----- Layout: lowest supported version at the top, highest at the bottom -----

        this.layout.put(10, small.build());
        this.actionSlots.put(10, "SMALL");

        this.layout.put(11, marker.build());
        this.actionSlots.put(11, "MARKER");

        this.layout.put(12, arms.build());
        this.actionSlots.put(12, "ARMS");

        this.layout.put(13, gravity.build());
        this.actionSlots.put(13, "GRAVITY");

        this.layout.put(14, baseplate.build());
        this.actionSlots.put(14, "BASEPLATE");

        this.layout.put(15, visible.build());
        this.actionSlots.put(15, "VISIBLE");

        this.layout.put(16, nameVisible.build());
        this.actionSlots.put(16, "NAMEVISIBLE");

        //Next line

        this.layout.put(19, changeName.build());
        this.actionSlots.put(19, "CHANGENAME");

        this.layout.put(20, invulnerable.build());
        this.actionSlots.put(20, "INVULNERABLE");

        if (glow != null) {
            this.layout.put(21, glow.build());
            this.actionSlots.put(21, "GLOW");
        }

        if (silent != null) {
            this.layout.put(22, silent.build());
            this.actionSlots.put(22, "SILENT");
        }

        this.layout.put(23, equiplock.build());
        this.actionSlots.put(23, "EQUIPLOCK");

        if (scale != null) {
            this.layout.put(24, scale.build());
            this.actionSlots.put(24, "SCALE");
        }

        //Delete is always the last item
        this.layout.put(25, delete.build());
        this.actionSlots.put(25, "DELETE");

        super.render();
    }

    @Override
    public void click(int slot, ItemStack clickedItem, ClickType clickType, InventoryType inventoryType) {

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

            case "PAGE-PRESETS":
                new PoseMenuPresets(player, armorStand).open();
                break;

            case "SMALL":
                armorStand.setSmall(!armorStand.isSmall());
                this.render();
                break;

            case "INVULNERABLE":
                ArmorStandAccess.setInvulnerable(armorStand, !ArmorStandAccess.isInvulnerable(armorStand));
                this.render();
                break;

            case "VISIBLE":
                armorStand.setVisible(!armorStand.isVisible());
                this.render();
                break;

            case "ARMS":
                armorStand.setArms(!armorStand.hasArms());
                this.render();
                break;

            case "GRAVITY":
                armorStand.setGravity(!armorStand.hasGravity());
                this.render();
                break;

            case "NAMEVISIBLE":
                armorStand.setCustomNameVisible(!armorStand.isCustomNameVisible());
                this.render();
                break;

            case "BASEPLATE":
                armorStand.setBasePlate(!armorStand.hasBasePlate());
                this.render();
                break;

            case "EQUIPLOCK":
                boolean currentEquipLock = ArmorStandAccess.getDisabledSlots(armorStand) == ArmorStandAccess.EQUIPMENT_LOCK_MASK;
                ArmorStandAccess.setDisabledSlots(armorStand, currentEquipLock ? 0 : ArmorStandAccess.EQUIPMENT_LOCK_MASK);
                this.render();
                break;

            case "MARKER":
                armorStand.setMarker(!armorStand.isMarker());
                //Vanilla semantics: a marker stand cannot have arms
                if (armorStand.isMarker()) armorStand.setArms(false);
                this.render();
                break;

            case "GLOW":
                ArmorStandAccess.setGlowing(armorStand, !ArmorStandAccess.isGlowing(armorStand));
                this.render();
                break;

            case "SILENT":
                ArmorStandAccess.setSilent(armorStand, !ArmorStandAccess.isSilent(armorStand));
                this.render();
                break;

            case "SCALE":
                player.closeInventory();
                PoseMaster.sendMessage(player, "§7Write the desired scale.");
                PoseMaster.sendMessage(player, "§7Must be between §f0.0625 - 16.0§7.");

                new PoseAwaitResponse(player, 600L) {

                    @Override
                    public void onRespond(String message) {

                        double value;
                        try {
                            value = Double.parseDouble(message);
                        } catch (NumberFormatException exception) {
                            PoseMaster.sendMessage(player, "§4" + message + " §cis an invalid scale!");
                            return;
                        }

                        ArmorStandAccess.setScale(armorStand, value);
                        PoseMaster.sendMessage(player, "§aChanged the scale to §2" + StringUtil.roundTwoDecimals(ArmorStandAccess.getScale(armorStand)) + "§a!");
                    }

                    @Override
                    public void onTimeout() {
                        PoseMaster.sendMessage(player, "§cYou took too long to respond!");
                    }
                };
                break;

            case "CHANGENAME":
                PoseMaster.sendMessage(player, "§7Enter the new name for the ArmorStand.");
                player.closeInventory();

                new PoseAwaitResponse(player, 600L) {

                    @Override
                    public void onRespond(String message) {
                        String value = StringUtil.colorize(message);
                        armorStand.setCustomName(value);
                        PoseMaster.sendMessage(player, "§aChanged the name to §2'" + value + "§2'§a!");
                    }

                    @Override
                    public void onTimeout() {
                        PoseMaster.sendMessage(player, "§cYou took too long to respond!");
                    }
                };
                break;

            case "DELETE":
                armorStand.remove();
                player.closeInventory();
                break;
        }

    }

}
