package me.xginko.craftinvisframes.commands.iframe.subcommands;

import io.papermc.paper.plugin.configuration.PluginMeta;
import me.xginko.craftinvisframes.CraftInvisFrames;
import me.xginko.craftinvisframes.commands.SubCommand;
import me.xginko.craftinvisframes.utils.AdventureUtil;
import me.xginko.craftinvisframes.utils.PluginPermission;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
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
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!sender.hasPermission(PluginPermission.VERSION_CMD.get())) {
            sender.sendMessage(Component.text("No permission.", AdventureUtil.RED));
            return true;
        }

        PluginMeta pluginMeta = CraftInvisFrames.get().getPluginMeta();

        sender.sendMessage(Component.newline()
                .append(
                        Component.text(String.join(" ", pluginMeta.getName(), pluginMeta.getVersion()), AdventureUtil.PURPLE)
                                .clickEvent(ClickEvent.openUrl(pluginMeta.getWebsite()))
                )
                .append(Component.text(" by ", NamedTextColor.DARK_GRAY))
                .append(
                        Component.text(String.join(", ", pluginMeta.getAuthors()), AdventureUtil.GINKO_BLUE)
                                .clickEvent(ClickEvent.openUrl("https://github.com/xGinko"))
                )
                .append(Component.newline())
        );

        return true;
    }
}
