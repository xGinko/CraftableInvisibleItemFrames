package me.xginko.craftableinvisibleitemframes.commands.iframe.subcommands;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

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
            PluginDescriptionFile pluginYml = CraftableInvisibleItemFrames.getInstance().getDescription();
            sender.sendMessage(
                    Component.newline()
                    .append(Component.text(pluginYml.getName()+" "+pluginYml.getVersion()).color(NamedTextColor.GOLD)
                    .append(Component.text(" by ").color(NamedTextColor.GRAY))
                    .append(Component.text(pluginYml.getAuthors().get(0)).color(NamedTextColor.DARK_AQUA))
                    .clickEvent(ClickEvent.openUrl(pluginYml.getWebsite())))
                    .append(Component.newline())
            );
        } else {
            sender.sendMessage(CraftableInvisibleItemFrames.getLang(sender).no_permission);
        }
    }
}
