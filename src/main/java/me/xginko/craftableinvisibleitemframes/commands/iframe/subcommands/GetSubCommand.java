package me.xginko.craftableinvisibleitemframes.commands.iframe.subcommands;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.commands.SubCommand;
import me.xginko.craftableinvisibleitemframes.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetSubCommand extends SubCommand {
    @Override
    public String getName() {
        return "get";
    }

    @Override
    public String getDescription() {
        return "Give yourself an invisible itemframe";
    }

    @Override
    public String getSyntax() {
        return "/iframe get";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender.hasPermission("craftableinvisibleitemframes.cmd.get")) {
            if (sender instanceof Player player) {
                ItemUtils.addToInventoryOrDrop(player, ItemUtils.getRegularInvisibleItemFrame(8, player.locale()));
                player.sendMessage(Component.text(ChatColor.GREEN + "You received 8 invisible itemframes."));
            } else {
                sender.sendMessage(Component.text(ChatColor.RED + "Command can't be executed from console."));
            }
        } else {
            sender.sendMessage(CraftableInvisibleItemFrames.getLang(sender).noPermission);
        }
    }
}
