package doodieman.posemaster.command.arguments;

import doodieman.posemaster.PoseMaster;
import doodieman.posemaster.command.PoseMasterCommand;
import doodieman.posemaster.objects.CopyCollection;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PMCommandCopy {

    public PMCommandCopy(Player player, double range) {


        Location location = new Location(
            player.getWorld(),
            player.getLocation().getBlockX()+0.5,
            player.getLocation().getY(),
            player.getLocation().getBlockZ()+0.5
        );

        CopyCollection copyCollection = CopyCollection.fromRange(location,range);
        PoseMasterCommand.getCopyCollectionMap().put(player, copyCollection);

        PoseMaster.sendMessage(player,"§aSuccesfully copied §2"+copyCollection.getCopies().size()+" ArmorStands§a!");
    }

}
