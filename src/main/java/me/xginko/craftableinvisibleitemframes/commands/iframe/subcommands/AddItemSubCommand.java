package me.xginko.craftableinvisibleitemframes.commands.iframe.subcommands;

import me.xginko.craftableinvisibleitemframes.commands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AddItemSubCommand extends SubCommand {

    @Override
    public String getName() {
        return "additem";
    }

    @Override
    public String getDescription() {
        return "Adds an item to the recipe center items";
    }

    @Override
    public String getSyntax() {
        return "/iframe additem";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender.hasPermission("craftableinvisibleitemframes.additem")) {
            if (sender instanceof Player player) {
                ItemStack itemStack = player.getInventory().getItemInMainHand();

            }
        }
    }
}
