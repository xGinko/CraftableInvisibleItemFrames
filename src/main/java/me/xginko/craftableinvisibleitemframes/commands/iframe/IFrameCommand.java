package me.xginko.craftableinvisibleitemframes.commands.iframe;

import me.xginko.craftableinvisibleitemframes.commands.SubCommand;
import me.xginko.craftableinvisibleitemframes.commands.iframe.subcommands.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class IFrameCommand implements CommandExecutor, TabCompleter {

    private final List<SubCommand> subcommands = new ArrayList<>(5);
    private final List<String> tabCompleter = new ArrayList<>(5);

    public IFrameCommand() {
        subcommands.add(new ReloadSubCommand());
        subcommands.add(new AddItemSubCommand());
        subcommands.add(new RemoveItemSubCommand());
        subcommands.add(new GetSubCommand());
        subcommands.add(new VersionSubCommand());
        for (SubCommand subcommand : subcommands) {
            tabCompleter.add(subcommand.getName());
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return args.length == 1 ? tabCompleter : null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length > 0) {
            boolean cmdExists = false;
            for (SubCommand subcommand : subcommands) {
                if (args[0].equalsIgnoreCase(subcommand.getName())) {
                    subcommand.perform(sender, args);
                    cmdExists = true;
                }
            }
            if (!cmdExists) showCommandOverviewTo(sender);
        } else {
            showCommandOverviewTo(sender);
        }
        return true;
    }

    private void showCommandOverviewTo(CommandSender sender) {
        sender.sendMessage(Component.text("-----------------------------------------------------").color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("CraftableInvisibleItemFrames Commands ").color(NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("-----------------------------------------------------").color(NamedTextColor.GRAY));
        for (SubCommand subcommand : subcommands) {
            sender.sendMessage(Component.text(subcommand.getSyntax()).color(NamedTextColor.WHITE)
                    .append(Component.text(" - ").color(NamedTextColor.DARK_GRAY))
                    .append(Component.text(subcommand.getDescription())).color(NamedTextColor.GRAY)
            );
        }
        sender.sendMessage(Component.text("-----------------------------------------------------").color(NamedTextColor.GRAY));
    }
}
