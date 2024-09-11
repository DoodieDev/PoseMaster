package org.doodieman.posemaster.menu.pages;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.doodieman.posemaster.config.lang.LangConfig;
import org.doodieman.posemaster.menu.CoreMenu;
import org.doodieman.posemaster.objects.AwaitPlayerResponse;
import org.doodieman.posemaster.objects.AwaitResponseType;
import org.doodieman.posemaster.objects.armorstands.PoseArmorStand;
import org.doodieman.posemaster.util.ItemBuilder;
import org.doodieman.posemaster.util.MathUtil;
import org.doodieman.posemaster.util.StringUtil;

import java.util.Map;

public class PagePositions extends CoreMenu {

    public PagePositions(Player player, PoseArmorStand poseArmorStand) {
        super(player,  "PAGE_POSITIONS", poseArmorStand);
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
                .lore(
                        "",
                        "&7Current: &f"+formattedLocation,
                        "",
                        "&aLeft-click to move it to a coordinate",
                        "&aMiddle-click to move it an amount of blocks",
                        "&aRight-click to move it in the direction you're facing"
                )
                .colorizeAll()
                .build();
        this.registerAction("LOCATION", 7, moveItem);

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

        //Change the LOCATION action according to ClickType
        if (action.equals("LOCATION") && clickType == ClickType.LEFT)
            action = "TELEPORT";
        else if (action.equals("LOCATION") && clickType == ClickType.RIGHT)
            action = "MOVE-DIRECTION";
        else if (action.equals("LOCATION") && clickType == ClickType.MIDDLE)
            action = "MOVE";

        switch (action) {

            //Move the ArmorStand by an offset
            case "MOVE" -> {

                //Lang message
                String langMessage = LangConfig.TYPE_MOVE_OFFSET.getColoredMessage();
                player.sendMessage(langMessage);

                new AwaitPlayerResponse(player, AwaitResponseType.VECTOR) {

                    @Override
                    public void onVectorResponse(Vector vector) {
                        //Move
                        Location location = armorStand.getLocation().clone();
                        location.add(vector);
                        armorStand.teleport(location);

                        //Lang message - Success
                        String langMessage = LangConfig.MOVE_SUCCESS.getColoredMessage();
                        player.sendMessage(langMessage);
                    }

                }.start();
            }

            //Move the ArmorStand the direction player is facing
            case "MOVE-DIRECTION" -> {
                //Lang message
                String langMessage = LangConfig.TYPE_MOVE_DIRECTION.getColoredMessage();
                player.sendMessage(langMessage);

                new AwaitPlayerResponse(player, AwaitResponseType.DOUBLE) {

                    @Override
                    public void onDoubleResponse(Double amount) {

                        Vector direction = player.getLocation().getDirection();
                        Location location = armorStand.getLocation().clone();

                        direction.setX(Math.round(direction.getX()) * amount);
                        direction.setY(Math.round(direction.getY()) * amount);
                        direction.setZ(Math.round(direction.getZ()) * amount);

                        location.add(direction);
                        armorStand.teleport(location);

                        //Lang message - Success
                        String langMessage = LangConfig.MOVE_SUCCESS.getColoredMessage();
                        player.sendMessage(langMessage);
                    }

                }.start();
            }

            //Teleport the ArmorStand to specific coordinates
            case "TELEPORT" -> {
                //Lang message
                String langMessage = LangConfig.TYPE_TELEPORT_LOCATION.getColoredMessage();
                player.sendMessage(langMessage);

                new AwaitPlayerResponse(player, AwaitResponseType.VECTOR) {

                    @Override
                    public void onVectorResponse(Vector vector) {
                        //Teleport
                        Location location = armorStand.getLocation().clone();
                        location.setX(vector.getX());
                        location.setY(vector.getY());
                        location.setZ(vector.getZ());
                        armorStand.teleport(location);

                        //Lang message - success
                        String langMessage = LangConfig.TELEPORT_SUCCESS.getColoredMessage();
                        player.sendMessage(langMessage);
                    }

                }.start();
            }

            //Rotate the ArmorStand (changes the Yaw)
            case "ROTATE" -> {
                //Lang message
                String langMessage = LangConfig.TYPE_ROTATION.getColoredMessage();
                player.sendMessage(langMessage);

                new AwaitPlayerResponse(player, AwaitResponseType.DOUBLE) {

                    @Override
                    public void onDoubleResponse(Double degrees) {
                        //Rotate
                        Location location = armorStand.getLocation().clone();
                        location.setYaw(degrees.floatValue());
                        armorStand.teleport(location);

                        //Lang message - success
                        Map<String, String> placeholders = Map.of(
                                "%degrees%", degrees.toString()
                        );
                        String langMessage = LangConfig.ROTATION_SUCCESS.getColoredMessage(placeholders);
                        player.sendMessage(langMessage);
                    }

                }.start();
            }

            //Change a pose (arms, body, etc..)
            default -> {

                //Lang message
                String langMessage = LangConfig.TYPE_ANGLE.getColoredMessage();
                player.sendMessage(langMessage);

                String[] actionSplit = action.split("-");
                String type = actionSplit[0];
                String coordinate = actionSplit[1];

                new AwaitPlayerResponse(player, AwaitResponseType.DOUBLE) {

                    @Override
                    public void onDoubleResponse(Double degrees) {
                        //Change angle
                        changePoseValue(type, coordinate, degrees);

                        //Lang message - success
                        Map<String, String> placeholders = Map.of(
                                "%degrees%", degrees.toString()
                        );
                        String langMessage = LangConfig.ANGLE_SUCCESS.getColoredMessage(placeholders);
                        player.sendMessage(langMessage);
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
