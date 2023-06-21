package doodieman.posemaster.gui;

import doodieman.posemaster.utils.GUI;
import doodieman.posemaster.utils.ItemBuilder;
import doodieman.posemaster.utils.StringUtil;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.graalvm.compiler.lir.aarch64.AArch64ArithmeticOp.ARMv8ConstantCategory;

import java.util.HashMap;
import java.util.Map;

public class PoseMenuSettings extends GUI {

    final ArmorStand armorStand;

    private final Map<Integer, String> actionSlots = new HashMap<>();


    public PoseMenuSettings(Player player, ArmorStand armorStand) {
        super(player, 5, "ID: "+armorStand.getEntityId());
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
            .lore("", "§fThis page is for changing the", "§fequipment of the ArmorStand.", "", "§aClick to open!");
        ItemBuilder settings = new ItemBuilder(Material.ANVIL)
            .name("§8§lSettings")
            .lore("", "§fThis page is for changing the", "§fsettings of the ArmorStand.", "", "§7§oCurrently selected!")
            .makeGlowing();

        this.layout.put(39, position.build());
        this.actionSlots.put(39, "PAGE-POSITION");
        this.layout.put(40, equipment.build());
        this.actionSlots.put(40, "PAGE-EQUIPMENT");
        this.layout.put(41, settings.build());
    }

    @Override
    public void render() {

        //Render bottom items
        this.createBottomItems();

        //Toggle Small
        String currentSmall = armorStand.isSmall() ? "§aEnabled" : "§cDisabled";
        ItemBuilder small = new ItemBuilder(Material.DOUBLE_PLANT)
            .name("§6§lSmall")
            .lore("", "§7Current: "+currentSmall, "", "§aClick to toggle!");
        if (armorStand.isSmall()) small.makeGlowing();

        //Invulnerable
        NBTTagCompound tag = new NBTTagCompound();
        ((CraftEntity) armorStand).getHandle().c(tag);
        String currentInvul = tag.getInt("Invulnerable") == 1 ? "§aEnabled" : "§cDisabled";
        ItemBuilder invulnerable = new ItemBuilder(Material.GOLDEN_APPLE)
            .name("§f§lInvulnerable")
            .lore("", "§7Current: "+currentInvul, "", "§aClick to toggle!");
        if (tag.getInt("Invulnerable") == 1) invulnerable.makeGlowing();

        //Visible
        String currentVisible = armorStand.isVisible() ? "§aEnabled" : "§cDisabled";
        ItemBuilder visible = new ItemBuilder(Material.GLASS_BOTTLE)
            .name("§f§lVisibility")
            .lore("", "§7Current: "+currentVisible, "", "§aClick to toggle!");
        if (armorStand.isVisible()) visible.makeGlowing();
        if (armorStand.isVisible()) visible.material(Material.POTION);

        //Arms
        String currentArms = armorStand.hasArms() ? "§aEnabled" : "§cDisabled";
        ItemBuilder arms = new ItemBuilder(Material.STICK)
            .name("§6§lArms")
            .lore("", "§7Current: "+currentArms, "", "§aClick to toggle!");
        if (armorStand.hasArms()) arms.makeGlowing();

        //Gravity
        String currentGravity = armorStand.hasGravity() ? "§aEnabled" : "§cDisabled";
        ItemBuilder gravity = new ItemBuilder(Material.FEATHER)
            .name("§b§lGravity")
            .lore("", "§7Current: "+currentGravity, "", "§aClick to toggle!");
        if (armorStand.hasGravity()) gravity.makeGlowing();

        //Name visisble
        String currentNameVisible = armorStand.isCustomNameVisible() ? "§aEnabled" : "§cDisabled";
        ItemBuilder nameVisible = new ItemBuilder(Material.NAME_TAG)
            .name("§e§lName Visibility")
            .lore("", "§7Current: "+currentNameVisible, "", "§aClick to toggle!");
        if (armorStand.isCustomNameVisible()) nameVisible.makeGlowing();

        //Baseplate
        String currentBaseplate = armorStand.hasBasePlate() ? "§aEnabled" : "§cDisabled";
        ItemBuilder baseplate = new ItemBuilder(Material.STEP)
            .name("§f§lBaseplate")
            .lore("", "§7Current: "+currentBaseplate, "", "§aClick to toggle!");
        if (armorStand.hasBasePlate()) baseplate.makeGlowing();

        //Equip lock
        String currentEquipLock = tag.getInt("DisabledSlots") == 31 ? "§aEnabled" : "§cDisabled";
        ItemBuilder equiplock = new ItemBuilder(Material.IRON_CHESTPLATE)
            .name("§f§lEquipment Lock")
            .lore("", "§7Current: "+currentEquipLock, "", "§aClick to toggle!");
        if (tag.getInt("DisabledSlots") == 31) equiplock.makeGlowing();

        //Change name
        String currentName = armorStand.getCustomName() == null ? "§fArmor Stand" : StringUtil.colorize(armorStand.getCustomName());
        ItemBuilder changeName = new ItemBuilder(Material.SIGN)
            .name("§6§lChange Name")
            .lore("", "§7Current: "+currentName, "", "§aClick to change!");
        if (armorStand.getCustomName() != null) changeName.makeGlowing();

        //Delete
        ItemBuilder delete = new ItemBuilder(Material.TNT)
            .name("§c§lDelete ArmorStand")
            .lore("", "§cClick to delete!");



        this.layout.put(10, baseplate.build());
        this.actionSlots.put(10,"BASEPLATE");

        this.layout.put(11, arms.build());
        this.actionSlots.put(11,"ARMS");

        this.layout.put(13, gravity.build());
        this.actionSlots.put(13,"GRAVITY");

        this.layout.put(15, invulnerable.build());
        this.actionSlots.put(15,"INVULNERABLE");

        this.layout.put(16, small.build());
        this.actionSlots.put(16,"SMALL");

        //Next line

        this.layout.put(19, equiplock.build());
        this.actionSlots.put(19,"EQUIPLOCK");

        this.layout.put(20, visible.build());
        this.actionSlots.put(20,"VISIBLE");

        this.layout.put(22, delete.build());
        this.actionSlots.put(22,"DELETE");

        this.layout.put(24, changeName.build());
        this.actionSlots.put(24, "CHANGENAME");

        this.layout.put(25, nameVisible.build());
        this.actionSlots.put(25,"NAMEVISIBLE");




        super.render();
    }

    @Override
    public void click(int slot, ItemStack clickedItem, ClickType clickType, InventoryType inventoryType) {

        if (!actionSlots.containsKey(slot)) return;
        String action = actionSlots.get(slot);

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

            case "SMALL":
                armorStand.setSmall(!armorStand.isSmall());
                this.render();
                break;

            case "INVULNERABLE":
                NBTTagCompound tag = new NBTTagCompound();
                ((CraftEntity) armorStand).getHandle().c(tag);
                boolean currentInvul = tag.getInt("Invulnerable") == 1;
                if (currentInvul)
                    tag.setInt("Invulnerable", 0);
                else
                    tag.setInt("Invulnerable", 1);

                ((CraftEntity) armorStand).getHandle().f(tag);
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
                NBTTagCompound equipLockTag = new NBTTagCompound();
                ((CraftEntity) armorStand).getHandle().c(equipLockTag);
                boolean currentEquipLock = equipLockTag.getInt("DisabledSlots") == 31;
                if (currentEquipLock)
                    equipLockTag.setInt("DisabledSlots", 0);
                else
                    equipLockTag.setInt("DisabledSlots", 31);

                ((CraftEntity) armorStand).getHandle().f(equipLockTag);
                this.render();
                break;

            case "CHANGENAME":
                player.sendMessage("§aType the new name of the armorstand.");
                player.sendMessage("§aColor codes are supported!");
                player.closeInventory();

                new PoseAwaitResponse(player,600L) {

                    @Override
                    public void onRespond(String message) {
                        String value = StringUtil.colorize(message);
                        armorStand.setCustomName(value);
                        player.sendMessage("§aSuccessfully changed the Armorstand name to '"+value+"'!");
                    }

                    @Override
                    public void onTimeout() {
                        player.sendMessage("§cYou took too long to respond. Open the menu to try again!");
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
