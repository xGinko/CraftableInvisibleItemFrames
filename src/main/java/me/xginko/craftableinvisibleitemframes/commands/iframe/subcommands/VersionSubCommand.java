package me.xginko.craftableinvisibleitemframes.commands.iframe.subcommands;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

public class VersionSubCommand extends SubCommand {

    @Override
    public String label() {
        return "version";
    }

    @Override
    public String desc() {
        return "Show the plugin version";
    }

    @Override
    public String syntax() {
        return "/iframe version";
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!sender.hasPermission("craftableinvisibleitemframes.cmd.version")) {
            sender.sendMessage(CraftableInvisibleItemFrames.getLang(sender).no_permission);
            return true;
        }

        PluginDescriptionFile pluginYml = CraftableInvisibleItemFrames.getInstance().getDescription();

        sender.sendMessage(Component.empty());
        sender.sendMessage(
                Component.text(pluginYml.getName()+" "+pluginYml.getVersion()).color(NamedTextColor.GOLD)
                .append(Component.text(" by ").color(NamedTextColor.GRAY))
                .append(Component.text(pluginYml.getAuthors().get(0)).color(NamedTextColor.DARK_AQUA))
                .clickEvent(ClickEvent.openUrl(pluginYml.getWebsite()))
        );
        sender.sendMessage(Component.empty());
        return true;
    }
}
