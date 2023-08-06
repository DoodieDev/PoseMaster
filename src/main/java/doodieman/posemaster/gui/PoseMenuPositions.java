package doodieman.posemaster.gui;

import doodieman.posemaster.PoseMaster;
import doodieman.posemaster.utils.GUI;
import doodieman.posemaster.utils.ItemBuilder;
import doodieman.posemaster.utils.StringUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class PoseMenuPositions extends GUI {

    final ArmorStand armorStand;

    private final Map<Integer, String> actionSlots = new HashMap<>();

    public PoseMenuPositions(Player player, ArmorStand armorStand) {
        super(player, 5, "ID: "+armorStand.getEntityId());
        this.armorStand = armorStand;
    }

    @Override
    public void render() {

        //Create bottom items
        this.createBottomItems();

        //Duplicate
        this.layout.put(4, new ItemBuilder(Material.PRISMARINE_CRYSTALS,"§d§lDuplicate","", "§fCreates a duplicate", "§fof this ArmorStand.", "", "§aClick to duplicate!").build());
        this.actionSlots.put(4, "DUPLICATE");

        //MOVE
        String formattedCoord = StringUtil.roundDecimals(armorStand.getLocation().getX(), 4)+" "+StringUtil.roundDecimals(armorStand.getLocation().getY(), 4)+" "+StringUtil.roundDecimals(armorStand.getLocation().getZ(), 4);
        this.layout.put(7, new ItemBuilder(Material.PISTON_BASE,"§6§lLocation","", "§7Current: §f"+ formattedCoord, "", "§aClick to change!").build());
        this.actionSlots.put(7, "MOVE");

        //Rotate
        this.layout.put(1, new ItemBuilder(Material.MINECART,"§7§lRotation","", "§7Current: §f"+ StringUtil.roundDecimals(armorStand.getLocation().getYaw(), 4), "", "§aClick to change!").build());
        this.actionSlots.put(1, "ROTATE");

        //Poses
        this.createArmPoses();
        this.createLegPoses();
        this.createBodyPoses();
        this.createHeadPoses();

        super.render();
    }

    public void createBottomItems() {
        //Create glass fill
        for (int i = 0; i < 9; i++)
            this.layout.put(i+(4*9), GUIItem.GLASS_FILL.getItem());

        ItemBuilder position = new ItemBuilder(Material.ARMOR_STAND)
            .name("§e§lPosition")
            .lore("", "§fThis page is for changing the", "§fposition of the ArmorStand.", "", "§7§oCurrently selected!")
            .makeGlowing();
        ItemBuilder equipment = new ItemBuilder(Material.LEATHER_CHESTPLATE)
            .name("§6§lEquipment")
            .lore("", "§fThis page is for changing the", "§fequipment of the ArmorStand.", "", "§aClick to open!");
        ItemBuilder settings = new ItemBuilder(Material.ANVIL)
            .name("§8§lSettings")
            .lore("", "§fThis page is for changing the", "§fsettings of the ArmorStand.", "", "§aClick to open!");

        this.layout.put(39, position.build());
        this.layout.put(40, equipment.build());
        this.actionSlots.put(40, "PAGE-EQUIPMENT");
        this.layout.put(41, settings.build());
        this.actionSlots.put(41, "PAGE-SETTINGS");
    }

    public void createArmPoses() {
        //LEFT ARM
        this.layout.put(9, new ItemBuilder(Material.BONE,"§f§lLeft Arm: X","", "§7Current: §f"+radiansToDegreesRounded(armorStand.getLeftArmPose().getX()),"","§aClick to change!").build());
        this.actionSlots.put(9, "LEFTARM-X");
        this.layout.put(10, new ItemBuilder(Material.BONE,"§f§lLeft Arm: Y","", "§7Current: §f"+radiansToDegreesRounded(armorStand.getLeftArmPose().getY()),"","§aClick to change!").build());
        this.actionSlots.put(10, "LEFTARM-Y");
        this.layout.put(11, new ItemBuilder(Material.BONE,"§f§lLeft Arm: Z","", "§7Current: §f"+radiansToDegreesRounded(armorStand.getLeftArmPose().getZ()),"","§aClick to change!").build());
        this.actionSlots.put(11, "LEFTARM-Z");

        //RIGHT ARM
        this.layout.put(15, new ItemBuilder(Material.BLAZE_ROD,"§e§lRight Arm: X","", "§7Current: §f"+radiansToDegreesRounded(armorStand.getRightArmPose().getX()),"","§aClick to change!").build());
        this.actionSlots.put(15, "RIGHTARM-X");
        this.layout.put(16, new ItemBuilder(Material.BLAZE_ROD,"§e§lRight Arm: Y","", "§7Current: §f"+radiansToDegreesRounded(armorStand.getRightArmPose().getY()),"","§aClick to change!").build());
        this.actionSlots.put(16, "RIGHTARM-Y");
        this.layout.put(17, new ItemBuilder(Material.BLAZE_ROD,"§e§lRight Arm: Z","", "§7Current: §f"+radiansToDegreesRounded(armorStand.getRightArmPose().getZ()),"","§aClick to change!").build());
        this.actionSlots.put(17, "RIGHTARM-Z");
    }

    public void createLegPoses() {
        //LEFT LEG
        this.layout.put(18, new ItemBuilder(Material.IRON_LEGGINGS,"§f§lLeft Leg: X","", "§7Current: §f"+radiansToDegreesRounded(armorStand.getLeftLegPose().getX()),"","§aClick to change!").build());
        this.actionSlots.put(18, "LEFTLEG-X");
        this.layout.put(19, new ItemBuilder(Material.IRON_LEGGINGS,"§f§lLeft Leg: Y","", "§7Current: §f"+radiansToDegreesRounded(armorStand.getLeftLegPose().getY()),"","§aClick to change!").build());
        this.actionSlots.put(19, "LEFTLEG-Y");
        this.layout.put(20, new ItemBuilder(Material.IRON_LEGGINGS,"§f§lLeft Leg: Z","", "§7Current: §f"+radiansToDegreesRounded(armorStand.getLeftLegPose().getZ()),"","§aClick to change!").build());
        this.actionSlots.put(20, "LEFTLEG-Z");

        //RIGHT LEG
        this.layout.put(24, new ItemBuilder(Material.GOLD_LEGGINGS,"§e§lRight Leg: X","", "§7Current: §f"+radiansToDegreesRounded(armorStand.getRightLegPose().getX()),"","§aClick to change!").build());
        this.actionSlots.put(24, "RIGHTLEG-X");
        this.layout.put(25, new ItemBuilder(Material.GOLD_LEGGINGS,"§e§lRight Leg: Y","", "§7Current: §f"+radiansToDegreesRounded(armorStand.getRightLegPose().getY()),"","§aClick to change!").build());
        this.actionSlots.put(25, "RIGHTLEG-Y");
        this.layout.put(26, new ItemBuilder(Material.GOLD_LEGGINGS,"§e§lRight Leg: Z","", "§7Current: §f"+radiansToDegreesRounded(armorStand.getRightLegPose().getZ()),"","§aClick to change!").build());
        this.actionSlots.put(26, "RIGHTLEG-Z");
    }

    public void createBodyPoses() {
        //BODY
        this.layout.put(21, new ItemBuilder(Material.DIAMOND_CHESTPLATE,"§b§lBody: X","", "§7Current: §f"+radiansToDegreesRounded(armorStand.getBodyPose().getX()),"","§aClick to change!").build());
        this.actionSlots.put(21, "BODY-X");
        this.layout.put(22, new ItemBuilder(Material.DIAMOND_CHESTPLATE,"§b§lBody: Y","", "§7Current: §f"+radiansToDegreesRounded(armorStand.getBodyPose().getY()),"","§aClick to change!").build());
        this.actionSlots.put(22, "BODY-Y");
        this.layout.put(23, new ItemBuilder(Material.DIAMOND_CHESTPLATE,"§b§lBody: Z","", "§7Current: §f"+radiansToDegreesRounded(armorStand.getBodyPose().getZ()),"","§aClick to change!").build());
        this.actionSlots.put(23, "BODY-Z");
    }

    public void createHeadPoses() {
        //HEAD
        this.layout.put(12, new ItemBuilder(Material.DIAMOND_HELMET,"§b§lHead: X","", "§7Current: §f"+radiansToDegreesRounded(armorStand.getHeadPose().getX()),"","§aClick to change!").build());
        this.actionSlots.put(12, "HEAD-X");
        this.layout.put(13, new ItemBuilder(Material.DIAMOND_HELMET,"§b§lHead: Y","", "§7Current: §f"+radiansToDegreesRounded(armorStand.getHeadPose().getY()),"","§aClick to change!").build());
        this.actionSlots.put(13, "HEAD-Y");
        this.layout.put(14, new ItemBuilder(Material.DIAMOND_HELMET,"§b§lHead: Z","", "§7Current: §f"+radiansToDegreesRounded(armorStand.getHeadPose().getZ()),"","§aClick to change!").build());
        this.actionSlots.put(14, "HEAD-Z");
    }

    @Override
    public void click(int slot, ItemStack clickedItem, ClickType clickType, InventoryType inventoryType) {

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

            case "ROTATE":

                player.closeInventory();
                PoseMaster.sendMessage(player,"§fWrite the desired rotation.");
                PoseMaster.sendMessage(player,"§fMust be between §70 - 360 degrees§f.");

                new PoseAwaitResponse(player, 600L) {
                    @Override
                    public void onRespond(String message) {

                        double value;
                        try {
                            value = Double.parseDouble(message);
                        } catch (NumberFormatException exception) {
                            PoseMaster.sendMessage(player,"§4"+message+" §cis an invalid rotation!");
                            return;
                        }

                        Location newLoc = armorStand.getLocation().clone();
                        newLoc.setYaw((float) value);
                        armorStand.teleport(newLoc);

                        PoseMaster.sendMessage(player,"§aChanged the rotation to §2"+value+"§a!");
                    }

                    @Override
                    public void onTimeout() {
                        PoseMaster.sendMessage(player,"§cYou took too long to respond!");
                    }
                };

                break;

            //Move the ArmorStand
            case "MOVE":

                player.closeInventory();
                PoseMaster.sendMessage(player,"§7Write the offset you wish it to move!");
                PoseMaster.sendMessage(player,"§7If you want it to move up by 2 blocks,");
                PoseMaster.sendMessage(player,"§7then type §f0 2 0§7.");

                new PoseAwaitResponse(player, 600L) {

                    @Override
                    public void onRespond(String message) {
                        Vector vector = stringToVector(message);

                        //Invalid input
                        if (vector == null) {
                            PoseMaster.sendMessage(player,"§4"+message+"§c is an invalid offset!");
                            return;
                        }

                        //Move
                        Location newLocation = armorStand.getLocation().clone().add(vector);
                        armorStand.teleport(newLocation);

                        PoseMaster.sendMessage(player,"§aThe armorstand has been moved!");
                    }

                    @Override
                    public void onTimeout() {
                        PoseMaster.sendMessage(player,"§cYou took too long to respond!");
                    }

                };
                break;

                //Change
            default:

                PoseMaster.sendMessage(player,"§fWrite the desired pose.");
                PoseMaster.sendMessage(player,"§fMust be between §70 - 360 degrees§f.");
                player.closeInventory();

                String[] actionSplit = action.split("-");
                String pose = actionSplit[0];
                String coord = actionSplit[1];

                new PoseAwaitResponse(player,600L) {
                    @Override
                    public void onRespond(String message) {

                        double value;
                        try {
                            value = Double.parseDouble(message);
                        } catch (NumberFormatException exception) {
                            PoseMaster.sendMessage(player,"§4"+message+" §cis an invalid rotation!");
                            return;
                        }

                        //Move
                        changePoseValue(pose,coord,degreesToRadians(value));
                        PoseMaster.sendMessage(player,"§aChanged the pose to §2"+value+"§a!");
                    }

                    @Override
                    public void onTimeout() {
                        PoseMaster.sendMessage(player,"§cYou took too long to respond!");
                    }
                };

                break;

            case "DUPLICATE":

                PoseMaster.sendMessage(player,"§aThe ArmorStand has been duplicated!");
                player.closeInventory();

                Location newLocation = armorStand.getLocation().clone().add(0,5,0);
                ArmorStand newArmorstand = armorStand.getWorld().spawn(newLocation, ArmorStand.class);

                //Equipment
                newArmorstand.getEquipment().setArmorContents(armorStand.getEquipment().getArmorContents());
                newArmorstand.getEquipment().setItemInHand(armorStand.getItemInHand());
                //Basic values
                newArmorstand.setVisible(armorStand.isVisible());
                newArmorstand.setSmall(armorStand.isSmall());
                newArmorstand.setArms(armorStand.hasArms());
                newArmorstand.setBasePlate(armorStand.hasBasePlate());
                if (armorStand.getCustomName() != null)
                    newArmorstand.setCustomName(armorStand.getCustomName());
                newArmorstand.setCustomNameVisible(armorStand.isCustomNameVisible());
                //Poses
                newArmorstand.setBodyPose(armorStand.getBodyPose());
                newArmorstand.setLeftArmPose(armorStand.getLeftArmPose());
                newArmorstand.setRightArmPose(armorStand.getRightArmPose());
                newArmorstand.setLeftLegPose(armorStand.getLeftLegPose());
                newArmorstand.setRightLegPose(armorStand.getRightLegPose());
                newArmorstand.setHeadPose(armorStand.getHeadPose());
                break;

        }
    }

    public double degreesToRadians(double degrees) {
        return (degrees % 360) * (Math.PI / 180);
    }
    public double radiansToDegrees(double radians) {
        return (radians / Math.PI) * 180;
    }

    public double radiansToDegreesRounded(double radians) {
        return StringUtil.roundTwoDecimals(this.radiansToDegrees(radians));
    }

    public Vector stringToVector(String text) {
        try {
            String[] split = text.split(" ");
            double x = Double.parseDouble(split[0]);
            double y = Double.parseDouble(split[1]);
            double z = Double.parseDouble(split[2]);
            return new Vector(x,y,z);
        } catch (Exception exception) {
            return null;
        }
    }

    public void changePoseValue(String pose, String coord, double value) {
        switch (pose) {
            case "BODY":
                EulerAngle bodyPose = armorStand.getBodyPose();
                armorStand.setBodyPose(this.updateEulerAngleCoord(bodyPose,coord,value));
                break;
            case "HEAD":
                EulerAngle headPose = armorStand.getHeadPose();
                armorStand.setHeadPose(this.updateEulerAngleCoord(headPose,coord,value));
                break;
            case "RIGHTARM":
                EulerAngle rightArmPose = armorStand.getRightArmPose();
                armorStand.setRightArmPose(this.updateEulerAngleCoord(rightArmPose,coord,value));
                break;
            case "LEFTARM":
                EulerAngle leftArmPose = armorStand.getLeftArmPose();
                armorStand.setLeftArmPose(this.updateEulerAngleCoord(leftArmPose,coord,value));
                break;
            case "RIGHTLEG":
                EulerAngle rightLegPose = armorStand.getRightLegPose();
                armorStand.setRightLegPose(this.updateEulerAngleCoord(rightLegPose,coord,value));
                break;
            case "LEFTLEG":
                EulerAngle leftLegPose = armorStand.getLeftLegPose();
                armorStand.setLeftLegPose(this.updateEulerAngleCoord(leftLegPose,coord,value));
                break;
        }
    }

    public EulerAngle updateEulerAngleCoord(EulerAngle eulerAngle, String coord, double value) {
        switch (coord) {
            case "X":
                return eulerAngle.setX(value);
            case "Y":
                return eulerAngle.setY(value);
            case "Z":
                return eulerAngle.setZ(value);
        }
        return null;
    }

}
