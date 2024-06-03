package me.xginko.craftableinvisibleitemframes.commands.iframe.subcommands;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.commands.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AddItemSubCommand extends SubCommand {

    @Override
    public String label() {
        return "additem";
    }

    @Override
    public String desc() {
        return "Adds an item to the recipe center items";
    }

    @Override
    public String syntax() {
        return "/iframe additem";
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!sender.hasPermission("craftableinvisibleitemframes.cmd.additem")) {
            sender.sendMessage(CraftableInvisibleItemFrames.getLang(sender).no_permission);
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Command can't be executed from console.").color(NamedTextColor.RED));
            return true;
        }

        Player player = (Player) sender;
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();

        if (mainHandItem.getType().equals(Material.AIR)) {
            player.sendMessage(Component.text("You have to hold an item in your hand to add it.").color(NamedTextColor.RED));
            return true;
        }

        if (CraftableInvisibleItemFrames.config().recipe_center_items.contains(mainHandItem)) {
            player.sendMessage(Component.text("Item has already been added.").color(NamedTextColor.WHITE));
            return true;
        }

        CraftableInvisibleItemFrames.config().addToRecipeCenterItems(mainHandItem);
        player.sendMessage(Component.text("Added "+mainHandItem.getAmount()+"x "+mainHandItem.getType().name()+
                " to the recipe center items.").color(NamedTextColor.GREEN));
        return true;
    }
}
