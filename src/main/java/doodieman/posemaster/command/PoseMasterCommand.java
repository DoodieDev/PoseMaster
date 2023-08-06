package doodieman.posemaster.command;

import doodieman.posemaster.PoseMaster;
import doodieman.posemaster.command.arguments.PMCommandCopy;
import doodieman.posemaster.command.arguments.PMCommandPaste;
import doodieman.posemaster.objects.CopyCollection;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PoseMasterCommand implements CommandExecutor {

    @Getter
    private final static Map<OfflinePlayer, CopyCollection> copyCollectionMap = new HashMap<>();


    private void sendHelp(Player player) {
        player.sendMessage("");
        PoseMaster.sendMessage(player,"§6/posemaster copy <range>");
        PoseMaster.sendMessage(player,"§6/posemaster paste");
        player.sendMessage("");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by a Player!");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            this.sendHelp(player);
            return true;
        }

        switch (args[0].toUpperCase()) {

            case "COPY":

                if (args.length < 2) {
                    PoseMaster.sendMessage(player,"§cYou need to type a range!");
                    PoseMaster.sendMessage(player,"§cUse: /posemaster copy <range>");
                    break;
                }

                double range;
                try {
                    range = Double.parseDouble(args[1]);
                } catch (NumberFormatException exception) {
                    PoseMaster.sendMessage(player,"§4"+args[1]+" §cis an invalid range!");
                    break;
                }

                new PMCommandCopy(player, range);
                break;

            case "PASTE":
                new PMCommandPaste(player);
                break;

            default:
                this.sendHelp(player);
                break;
        }

        return true;
    }

}
