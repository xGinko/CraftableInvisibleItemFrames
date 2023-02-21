package me.xginko.craftableinvisibleitemframes.commands.iframe;

import me.xginko.craftableinvisibleitemframes.commands.CraftableInvisibleItemFramesCommand;
import me.xginko.craftableinvisibleitemframes.commands.SubCommand;
import me.xginko.craftableinvisibleitemframes.commands.iframe.subcommands.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class IFrameCommand implements CraftableInvisibleItemFramesCommand, TabCompleter {

    private final ArrayList<SubCommand> subcommands = new ArrayList<>();
    private final List<String> tabcompleters = new ArrayList<>();

    public IFrameCommand() {
        subcommands.add(new ReloadSubCommand());
        subcommands.add(new AddItemSubCommand());
        subcommands.add(new RemoveItemSubCommand());
        subcommands.add(new GetSubCommand());
        subcommands.add(new VersionSubCommand());
        for (SubCommand subcommand : subcommands) {
            tabcompleters.add(subcommand.getName());
        }
    }

    @Override
    public String label() {
        return "iframe";
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length > 0) {
            return tabcompleters;
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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
        sender.sendMessage(ChatColor.GRAY+"-----------------------------------------------------");
        sender.sendMessage(ChatColor.WHITE+"CraftableInvisibleItemFrames Commands ");
        sender.sendMessage(ChatColor.GRAY+"-----------------------------------------------------");
        for (SubCommand subcommand : subcommands) {
            sender.sendMessage(
                    ChatColor.WHITE + subcommand.getSyntax()
                            + ChatColor.DARK_GRAY + " - "
                            + ChatColor.GRAY + subcommand.getDescription()
            );
        }
        sender.sendMessage(ChatColor.GRAY+"-----------------------------------------------------");
    }
}
