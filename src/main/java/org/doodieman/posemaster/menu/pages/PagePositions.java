package org.doodieman.posemaster.menu.pages;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.doodieman.posemaster.menu.CoreMenu;
import org.doodieman.posemaster.objects.AwaitPlayerResponse;
import org.doodieman.posemaster.objects.AwaitResponseType;
import org.doodieman.posemaster.objects.armorstands.PoseArmorStand;
import org.doodieman.posemaster.util.ItemBuilder;
import org.doodieman.posemaster.util.MathUtil;
import org.doodieman.posemaster.util.StringUtil;

public class PagePositions extends CoreMenu {

    public PagePositions(Player player, PoseArmorStand poseArmorStand) {
        super(player,  "POSITIONS", poseArmorStand);
    }

    @Override
    public void render() {
        ArmorStand armorStand = this.getPoseArmorStand().getArmorStand();

        //Rotate
        ItemStack rotateItem = new ItemBuilder(Material.COMPASS)
                .name("&6Rotation")
                .lore("", "&7Current: &f"+armorStand.getLocation().getYaw(), "", "&aLeft-click to change")
                .colorizeAll()
                .build();
        this.registerAction("ROTATE", 1, rotateItem);

        //Move
        String formattedLocation = StringUtil.formatLocation(armorStand.getLocation(), 2, false);
        ItemStack moveItem = new ItemBuilder(Material.ENDER_PEARL)
                .name("&6Location")
                .lore("", "&7Current: &f"+formattedLocation, "", "&aLeft-click to move", "&aMiddle-click to teleport")
                .colorizeAll()
                .build();
        this.registerAction("MOVE", 7, moveItem);

        //Arms
        this.createPoseItems(9, Material.BONE, "LEFTARM", "Left Arm", armorStand.getLeftArmPose());
        this.createPoseItems(15, Material.BLAZE_ROD, "RIGHTARM", "Right Arm", armorStand.getRightArmPose());
        //Head & Body
        this.createPoseItems(12, Material.DIAMOND_HELMET, "HEAD", "Head", armorStand.getHeadPose());
        this.createPoseItems(21, Material.DIAMOND_CHESTPLATE, "BODY", "Body", armorStand.getBodyPose());
        //Legs
        this.createPoseItems(18, Material.IRON_LEGGINGS, "LEFTLEG", "Left Leg", armorStand.getLeftLegPose());
        this.createPoseItems(24, Material.GOLDEN_LEGGINGS, "RIGHTLEG", "Right Leg", armorStand.getRightLegPose());

        super.render();
    }

    //Creates a row of pose items for one EulerAngle.
    //Example, head x, head y, head z.
    private void createPoseItems(int startSlot, Material material, String type, String label, EulerAngle pose) {
        this.registerAction(type+"-X", startSlot, new ItemBuilder(material)
                .name("&6"+label+" &e[X]")
                .lore("", "&7Current: &f"+MathUtil.radiansToDegreesRounded(pose.getX()), "", "&aLeft-click to change")
                .colorizeAll()
                .hideAttributes()
                .build()
        );
        this.registerAction(type+"-Y", startSlot+1, new ItemBuilder(material)
                .name("&6"+label+" &e[Y]")
                .lore("", "&7Current: &f"+MathUtil.radiansToDegreesRounded(pose.getY()), "", "&aLeft-click to change")
                .colorizeAll()
                .hideAttributes()
                .build()
        );
        this.registerAction(type+"-Z", startSlot+2, new ItemBuilder(material)
                .name("&6"+label+" &e[Z]")
                .lore("", "&7Current: &f"+MathUtil.radiansToDegreesRounded(pose.getZ()), "", "&aLeft-click to change")
                .colorizeAll()
                .hideAttributes()
                .build()
        );
    }

    @Override
    public void onAction(String action, ClickType clickType) {
        ArmorStand armorStand = getPoseArmorStand().getArmorStand();
        player.closeInventory();

        //Change to teleport if middle-clicked
        if (action.equals("MOVE") && clickType == ClickType.MIDDLE) {
            action = "TELEPORT";
        }

        switch (action) {

            //Move the ArmorStand by an offset
            case "MOVE" -> {
                player.sendMessage("§aWrite the offset, example: 0 2 0");

                new AwaitPlayerResponse(player, AwaitResponseType.VECTOR) {

                    @Override
                    public void onVectorResponse(Vector vector) {
                        player.sendMessage("§aMoved the armorstand!");

                        Location location = armorStand.getLocation().clone();
                        location.add(vector);
                        armorStand.teleport(location);
                    }

                }.start();
            }

            //Teleport the ArmorStand to specific coordinates
            case "TELEPORT" -> {
                player.sendMessage("§aWrite the location, example: 0 2 0");

                new AwaitPlayerResponse(player, AwaitResponseType.VECTOR) {

                    @Override
                    public void onVectorResponse(Vector vector) {
                        player.sendMessage("§aTeleported the armorstand!");

                        Location location = armorStand.getLocation().clone();
                        location.setX(vector.getX());
                        location.setY(vector.getY());
                        location.setZ(vector.getZ());
                        armorStand.teleport(location);
                    }

                }.start();
            }

            //Rotate the ArmorStand (changes the Yaw)
            case "ROTATE" -> {
                player.sendMessage("§aWrite the desired rotation 0-360 degrees");

                new AwaitPlayerResponse(player, AwaitResponseType.DOUBLE) {

                    @Override
                    public void onDoubleResponse(Double degrees) {
                        player.sendMessage("§aChanged the rotation to "+degrees);

                        Location location = armorStand.getLocation().clone();
                        location.setYaw(degrees.floatValue());
                        armorStand.teleport(location);
                    }

                }.start();
            }

            //Change a pose (arms, body, etc..)
            default -> {

                player.sendMessage("§aWrite the desired pose 0-360 degrees");

                String[] actionSplit = action.split("-");
                String type = actionSplit[0];
                String coordinate = actionSplit[1];

                new AwaitPlayerResponse(player, AwaitResponseType.DOUBLE) {

                    @Override
                    public void onDoubleResponse(Double degrees) {
                        player.sendMessage("§aChanged the pose to "+degrees);
                        changePoseValue(type, coordinate, degrees);
                    }

                }.start();
            }

        }
    }

    public void changePoseValue(String type, String coordinate, double degrees) {
        ArmorStand armorStand = getPoseArmorStand().getArmorStand();
        double radians = MathUtil.degreesToRadians(degrees);

        switch (type) {
            case "BODY":
                EulerAngle bodyPose = armorStand.getBodyPose();
                armorStand.setBodyPose(this.updateEulerAngleCoordinate(bodyPose, coordinate, radians));
                break;
            case "HEAD":
                EulerAngle headPose = armorStand.getHeadPose();
                armorStand.setHeadPose(this.updateEulerAngleCoordinate(headPose, coordinate, radians));
                break;
            case "RIGHTARM":
                EulerAngle rightArmPose = armorStand.getRightArmPose();
                armorStand.setRightArmPose(this.updateEulerAngleCoordinate(rightArmPose, coordinate, radians));
                break;
            case "LEFTARM":
                EulerAngle leftArmPose = armorStand.getLeftArmPose();
                armorStand.setLeftArmPose(this.updateEulerAngleCoordinate(leftArmPose, coordinate, radians));
                break;
            case "RIGHTLEG":
                EulerAngle rightLegPose = armorStand.getRightLegPose();
                armorStand.setRightLegPose(this.updateEulerAngleCoordinate(rightLegPose, coordinate, radians));
                break;
            case "LEFTLEG":
                EulerAngle leftLegPose = armorStand.getLeftLegPose();
                armorStand.setLeftLegPose(this.updateEulerAngleCoordinate(leftLegPose, coordinate, radians));
                break;
        }
    }

    public EulerAngle updateEulerAngleCoordinate(EulerAngle eulerAngle, String coordinate, double value) {
        return switch (coordinate) {
            case "X" -> eulerAngle.setX(value);
            case "Y" -> eulerAngle.setY(value);
            case "Z" -> eulerAngle.setZ(value);
            default -> null;
        };
    }


}
