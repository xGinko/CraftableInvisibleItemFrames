package me.xginko.craftableinvisibleitemframes.commands.iframe;

import me.xginko.craftableinvisibleitemframes.commands.CraftableInvisibleItemFramesCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class IFrameCommand implements CraftableInvisibleItemFramesCommand {
    @Override
    public String label() {
        return "iframe";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }
}
