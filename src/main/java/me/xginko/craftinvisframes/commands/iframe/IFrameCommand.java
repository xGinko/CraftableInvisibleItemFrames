package me.xginko.craftinvisframes.commands.iframe;

import me.xginko.craftinvisframes.CraftInvisFrames;
import me.xginko.craftinvisframes.commands.SubCommand;
import me.xginko.craftinvisframes.commands.iframe.subcommands.ReloadSubCommand;
import me.xginko.craftinvisframes.commands.iframe.subcommands.VersionSubCommand;
import me.xginko.craftinvisframes.utils.AdventureUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class IFrameCommand extends Command {

    private final List<SubCommand> subcommands;
    private final List<String> tabCompleter;

    public IFrameCommand() {
        super(
                "iframe",
                "InvisibleItemFrames admin commands",
                "/iframes <version, reload, get, add, remove>",
                Collections.singletonList("/invisframes")
        );
        subcommands = List.of(new ReloadSubCommand(), new VersionSubCommand());
        tabCompleter = subcommands.stream().map(SubCommand::label).toList();
    }

    @SuppressWarnings("deprecation")
    public void enable() {
        CraftInvisFrames plugin = CraftInvisFrames.get();
        plugin.getServer().getCommandMap().register(plugin.getDescription().getName().toLowerCase(), this);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String @NotNull [] args)
            throws CommandException, IllegalArgumentException
    {
        return args.length == 1 ? tabCompleter : Collections.emptyList();
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String @NotNull [] args) {
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
        sender.sendMessage(Component.text(" CraftableInvisibleItemFrames Commands ", AdventureUtil.PURPLE));
        sender.sendMessage(Component.empty());
        for (SubCommand subcommand : subcommands) {
            sender.sendMessage(Component.text(subcommand.syntax(), AdventureUtil.LIGHT_PURPLE)
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text(subcommand.desc(), AdventureUtil.LIGHT_PURPLE)));
        }
        sender.sendMessage(Component.empty());
    }
}
