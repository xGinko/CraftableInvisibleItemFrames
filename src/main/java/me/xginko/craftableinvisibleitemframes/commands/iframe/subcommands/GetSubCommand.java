package me.xginko.craftableinvisibleitemframes.commands.iframe.subcommands;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.commands.SubCommand;
import me.xginko.craftableinvisibleitemframes.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GetSubCommand extends SubCommand {
    @Override
    public String getName() {
        return "get";
    }

    @Override
    public String getDescription() {
        return "Give yourself an invisible itemframe";
    }

    @Override
    public String getSyntax() {
        return "/iframe get";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (sender.hasPermission("craftableinvisibleitemframes.cmd.get")) {
            if (sender instanceof Player player) {
                ItemStack invisible_regular_item_frame = ItemUtils.getRegularInvisibleItemFrame(8);
                ItemMeta meta = invisible_regular_item_frame.getItemMeta();
                meta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', CraftableInvisibleItemFrames.getLang(sender).invisible_item_frame)));
                invisible_regular_item_frame.setItemMeta(meta);
                player.getInventory().addItem(invisible_regular_item_frame);
                player.sendMessage(Component.text(ChatColor.GREEN + "Added an invisible item frame to your inventory"));
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
