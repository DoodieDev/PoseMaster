package doodieman.posemaster.command.arguments;

import doodieman.posemaster.PoseMaster;
import doodieman.posemaster.command.PoseMasterCommand;
import doodieman.posemaster.objects.CopyCollection;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PMCommandPaste {

    public PMCommandPaste(Player player) {

        if (!PoseMasterCommand.getCopyCollectionMap().containsKey(player)) {
            PoseMaster.sendMessage(player,"§cYou do not have a copy!");
            PoseMaster.sendMessage(player,"§cUse: /posemaster copy <range>");
            return;
        }

        Location location = new Location(
            player.getWorld(),
            player.getLocation().getBlockX()+0.5,
            player.getLocation().getY(),
            player.getLocation().getBlockZ()+0.5
        );

        CopyCollection copyCollection = PoseMasterCommand.getCopyCollectionMap().get(player);
        copyCollection.paste(location);

        PoseMaster.sendMessage(player,"§aSuccesfully pasted §2"+copyCollection.getCopies().size()+" ArmorStands§a!");
    }

}
