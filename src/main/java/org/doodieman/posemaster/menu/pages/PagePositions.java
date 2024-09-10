package org.doodieman.posemaster.menu.pages;

import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;
import org.doodieman.posemaster.menu.CoreMenu;
import org.doodieman.posemaster.objects.AwaitPlayerResponse;
import org.doodieman.posemaster.objects.AwaitResponseType;
import org.doodieman.posemaster.objects.armorstands.PoseArmorStand;
import org.doodieman.posemaster.util.ItemBuilder;
import org.doodieman.posemaster.util.MathUtil;

public class PagePositions extends CoreMenu {

    public PagePositions(Player player, PoseArmorStand poseArmorStand) {
        super(player,  "POSITIONS", poseArmorStand);
    }

    @Override
    public void render() {
        ArmorStand armorStand = this.getPoseArmorStand().getArmorStand();

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
    public void onAction(String action) {
        player.closeInventory();

        switch (action) {

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
