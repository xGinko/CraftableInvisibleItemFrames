package me.xginko.craftableinvisibleitemframes.commands.iframe.subcommands;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadSubCommand extends SubCommand {

    @Override
    public String label() {
        return "reload";
    }

    @Override
    public String desc() {
        return "Reload the plugin configuration";
    }

    @Override
    public String syntax() {
        return "/iframe reload";
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!sender.hasPermission("craftableinvisibleitemframes.cmd.reload")) {
            sender.sendMessage(CraftableInvisibleItemFrames.getLang(sender).no_permission);
            return true;
        }

        CraftableInvisibleItemFrames.foliaLib().getImpl().runNextTick(reload -> {
            CraftableInvisibleItemFrames.getInstance().reloadPlugin();
            sender.sendMessage(Component.text("Reload complete.").color(NamedTextColor.GREEN));
        });
        return true;
    }
}
