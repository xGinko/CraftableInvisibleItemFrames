package me.xginko.craftableinvisibleitemframes.commands;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.commands.iframe.IFrameCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;

import java.util.HashSet;

public interface CraftableInvisibleItemFramesCommand extends CommandExecutor {

    String label();

    HashSet<CraftableInvisibleItemFramesCommand> commands = new HashSet<>();
    static void reloadCommands() {
        commands.clear();

        commands.add(new IFrameCommand());

        CraftableInvisibleItemFrames plugin = CraftableInvisibleItemFrames.getInstance();
        CommandMap commandMap = plugin.getServer().getCommandMap();
        for (CraftableInvisibleItemFramesCommand command : commands) {
            plugin.getCommand(command.label()).unregister(commandMap);
            plugin.getCommand(command.label()).setExecutor(command);
        }
    }

    @Override
    boolean onCommand(CommandSender sender, Command command, String label, String[] args);

}
