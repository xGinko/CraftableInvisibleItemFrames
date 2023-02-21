package me.xginko.craftableinvisibleitemframes.commands.iframe.subcommands;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.commands.SubCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
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
        if (sender.hasPermission("craftableinvisibleitemframes.cmd.additem")) {
            if (sender instanceof Player player) {
                ItemStack itemPlayerIsHolding = player.getInventory().getItemInMainHand();
                if (!CraftableInvisibleItemFrames.getConfiguration().recipe_center_items.contains(itemPlayerIsHolding)) {
                    CraftableInvisibleItemFrames.getConfiguration().addToRecipeCenterItems(itemPlayerIsHolding);
                    player.sendMessage(Component.text(ChatColor.GREEN + "Added "+itemPlayerIsHolding.getAmount()+"x "+itemPlayerIsHolding.getType().name()+" to the recipe center items."));
                } else {
                    player.sendMessage(Component.text(ChatColor.RED + "That item is already added!"));
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
