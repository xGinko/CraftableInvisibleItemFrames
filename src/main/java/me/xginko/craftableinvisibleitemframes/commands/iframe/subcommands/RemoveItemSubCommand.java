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
import org.jetbrains.annotations.NotNull;

public class RemoveItemSubCommand extends SubCommand {

    @Override
    public String label() {
        return "removeitem";
    }

    @Override
    public String desc() {
        return "Remove a held item from the recipe center list";
    }

    @Override
    public String syntax() {
        return "/iframe removeitem";
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Command can't be executed from console.").color(NamedTextColor.RED));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("craftableinvisibleitemframes.cmd.removeitem")) {
            player.sendMessage(CraftableInvisibleItemFrames.getLang(player.locale()).no_permission);
            return true;
        }

        ItemStack itemPlayerIsHolding = player.getInventory().getItemInMainHand();

        if (itemPlayerIsHolding.getType().equals(Material.AIR)) {
            player.sendMessage(Component.text("You have to hold an item in your hand.").color(NamedTextColor.RED));
            return true;
        }

        Config config = CraftableInvisibleItemFrames.config();

        if (!config.recipe_center_items.contains(itemPlayerIsHolding)) {
            player.sendMessage(Component.text("Item has not been added to the center recipes yet!").color(NamedTextColor.RED));
            return true;
        }

        config.removeFromRecipeCenterItems(itemPlayerIsHolding);
        player.sendMessage(Component.text("Removed "+itemPlayerIsHolding.getAmount()+"x "+
                itemPlayerIsHolding.getType().name()+" from the possible recipe center items.").color(NamedTextColor.GREEN));
        return true;
    }
}
