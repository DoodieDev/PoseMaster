package doodieman.posemaster.command.arguments;

import doodieman.posemaster.PoseMaster;
import doodieman.posemaster.command.PoseMasterCommand;
import doodieman.posemaster.objects.CopyCollection;
import org.bukkit.entity.Player;

public class PMCommandCopy {

    public PMCommandCopy(Player player, double range) {

        CopyCollection copyCollection = CopyCollection.fromRange(player.getLocation(),range);
        PoseMasterCommand.getCopyCollectionMap().put(player, copyCollection);

        PoseMaster.sendMessage(player,"§aSuccesfully copied §2"+copyCollection.getCopies().size()+" ArmorStands§a!");
    }

}
