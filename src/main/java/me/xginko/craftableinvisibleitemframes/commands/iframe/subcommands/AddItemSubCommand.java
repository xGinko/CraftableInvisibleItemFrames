package me.xginko.craftableinvisibleitemframes.commands.iframe.subcommands;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
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
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Command can't be executed from console.").color(NamedTextColor.RED));
            return;
        }

        Player player = (Player) sender;

        if (player.hasPermission("craftableinvisibleitemframes.cmd.additem")) {
            ItemStack itemPlayerIsHolding = player.getInventory().getItemInMainHand();
            if (!itemPlayerIsHolding.getType().equals(Material.AIR)) {
                if (!CraftableInvisibleItemFrames.getConfiguration().recipe_center_items.contains(itemPlayerIsHolding)) {
                    CraftableInvisibleItemFrames.getConfiguration().addToRecipeCenterItems(itemPlayerIsHolding);
                    player.sendMessage(Component.text("Added "+itemPlayerIsHolding.getAmount()+"x "+itemPlayerIsHolding.getType().name()+" to the recipe center items.").color(NamedTextColor.GREEN));
                } else {
                    player.sendMessage(Component.text("Item has already been added.").color(NamedTextColor.WHITE));
                }
            } else {
                player.sendMessage(Component.text("You have to hold an item in your hand to add it.").color(NamedTextColor.RED));
            }
        } else {
            player.sendMessage(CraftableInvisibleItemFrames.getLang(player.locale()).no_permission);
        }
    }
}
