package me.xginko.craftableinvisibleitemframes.commands.iframe.subcommands;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.commands.SubCommand;
import me.xginko.craftableinvisibleitemframes.config.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Command can't be executed from console.").color(NamedTextColor.RED));
            return;
        }

        if (player.hasPermission("craftableinvisibleitemframes.cmd.removeitem")) {
            ItemStack itemPlayerIsHolding = player.getInventory().getItemInMainHand();
            if (!itemPlayerIsHolding.getType().equals(Material.AIR)) {
                Config config = CraftableInvisibleItemFrames.getConfiguration();
                if (config.recipe_center_items.contains(itemPlayerIsHolding)) {
                    config.removeFromRecipeCenterItems(itemPlayerIsHolding);
                    player.sendMessage(Component.text("Removed "+itemPlayerIsHolding.getAmount()+"x "+itemPlayerIsHolding.getType().name()+" from the possible recipe center items.").color(NamedTextColor.GREEN));
                } else {
                    player.sendMessage(Component.text("Item has not been added to the center recipes yet!").color(NamedTextColor.RED));
                }
            } else {
                player.sendMessage(Component.text("You have to hold an item in your hand.").color(NamedTextColor.RED));
            }
        } else {
            player.sendMessage(CraftableInvisibleItemFrames.getLang(player.locale()).no_permission);
        }
    }
}
