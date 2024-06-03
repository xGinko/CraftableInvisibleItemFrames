package me.xginko.craftableinvisibleitemframes.commands.iframe;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.commands.SubCommand;
import me.xginko.craftableinvisibleitemframes.commands.iframe.subcommands.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class IFrameCommand extends Command {

    private final List<SubCommand> subcommands;
    private final List<String> tabCompleter;

    public IFrameCommand() {
        super(
                "iframe",
                "InvisibleItemframes admin commands",
                "/iframes <version, reload, get, add, remove>",
                Collections.singletonList("/invisframes")
        );
        subcommands = Arrays.asList(
                new ReloadSubCommand(),
                new VersionSubCommand(),
                new GetSubCommand(),
                new AddItemSubCommand(),
                new RemoveItemSubCommand());
        tabCompleter = subcommands.stream().map(SubCommand::label).collect(Collectors.toList());
    }

    @SuppressWarnings("deprecation")
    public void enable() {
        CraftableInvisibleItemFrames plugin = CraftableInvisibleItemFrames.getInstance();
        plugin.getServer().getCommandMap().register(plugin.getDescription().getName().toLowerCase(), this);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args)
            throws CommandException, IllegalArgumentException
    {
        return args.length == 1 ? tabCompleter : Collections.emptyList();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length >= 1) {
            for (SubCommand subCommand : subcommands) {
                if (args[0].equalsIgnoreCase(subCommand.label())) {
                    return subCommand.execute(sender, commandLabel, args);
                }
            }
        }

        showCommandOverviewTo(sender);
        return true;
    }

    private void showCommandOverviewTo(CommandSender sender) {
        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text(" CraftableInvisibleItemFrames Commands ").color(NamedTextColor.WHITE));
        sender.sendMessage(Component.empty());
        for (SubCommand subcommand : subcommands) {
            sender.sendMessage(Component.text(subcommand.syntax()).color(NamedTextColor.WHITE)
                    .append(Component.text(" - ").color(NamedTextColor.DARK_GRAY))
                    .append(Component.text(subcommand.desc())).color(NamedTextColor.GRAY));
        }
        sender.sendMessage(Component.empty());
    }
}
