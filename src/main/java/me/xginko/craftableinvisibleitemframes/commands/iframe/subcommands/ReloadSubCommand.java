package me.xginko.craftableinvisibleitemframes.commands.iframe.subcommands;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.commands.SubCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadSubCommand extends SubCommand {

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reload the plugin configuration";
    }

    @Override
    public String getSyntax() {
        return "/iframe reload";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender.hasPermission("craftableinvisibleitemframes.cmd.reload")) {
            CraftableInvisibleItemFrames.getInstance().reloadPlugin();
            sender.sendMessage(Component.text(ChatColor.GREEN + "Reload complete."));
        } else {
            sender.sendMessage(CraftableInvisibleItemFrames.getLang(sender).noPermission);
        }
    }
}
