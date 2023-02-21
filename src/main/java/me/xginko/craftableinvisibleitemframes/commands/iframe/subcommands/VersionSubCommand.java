package me.xginko.craftableinvisibleitemframes.commands.iframe.subcommands;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.commands.SubCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class VersionSubCommand extends SubCommand {

    @Override
    public String getName() {
        return "version";
    }

    @Override
    public String getDescription() {
        return "Show the plugin version";
    }

    @Override
    public String getSyntax() {
        return "/iframe version";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender.hasPermission("craftableinvisibleitemframes.cmd.version")) {
            sender.sendMessage("\n");
            sender.sendMessage(
                    ChatColor.WHITE+"CraftableInvisibleItemFrames v"+CraftableInvisibleItemFrames.getInstance().getDescription().getVersion()+
                            ChatColor.WHITE+" by "+ChatColor.AQUA+"xGinko"
            );
            sender.sendMessage("\n");
        } else {
            sender.sendMessage(Component.text(
                    ChatColor.translateAlternateColorCodes('&', CraftableInvisibleItemFrames.getLang(sender).noPermission)
            ));
        }
    }
}
