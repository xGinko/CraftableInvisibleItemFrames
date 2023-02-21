package me.xginko.craftableinvisibleitemframes.commands.iframe.subcommands;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.commands.SubCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RemoveItemSubCommand extends SubCommand {

    @Override
    public String getName() {
        return "removeitem";
    }

    @Override
    public String getDescription() {
        return "Remove a held item from the recipe center list";
    }

    @Override
    public String getSyntax() {
        return "/iframe removeitem";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender.hasPermission("craftableinvisibleitemframes.cmd.removeitem")) {
            if (sender instanceof Player player) {
                ItemStack itemPlayerIsHolding = player.getInventory().getItemInMainHand();
                if (!itemPlayerIsHolding.getType().equals(Material.AIR)) {
                    if (CraftableInvisibleItemFrames.getConfiguration().recipe_center_items.contains(itemPlayerIsHolding)) {
                        CraftableInvisibleItemFrames.getConfiguration().removeFromRecipeCenterItems(itemPlayerIsHolding);
                        player.sendMessage(Component.text(ChatColor.GREEN + "Removed "+itemPlayerIsHolding.getAmount()+"x "+itemPlayerIsHolding.getType().name()+" from the recipe center items."));
                    } else {
                        player.sendMessage(Component.text(ChatColor.RED + "Item has not been added to the center recipes yet!"));
                    }
                } else {
                    player.sendMessage(Component.text(ChatColor.RED + "You have to hold an item in your hand."));
                }
            } else {
                sender.sendMessage(Component.text(ChatColor.RED + "Command can't be executed from console."));
            }
        } else {
            sender.sendMessage(Component.text(
                    ChatColor.translateAlternateColorCodes('&', CraftableInvisibleItemFrames.getLang(sender).noPermission)
            ));
        }
    }
}
