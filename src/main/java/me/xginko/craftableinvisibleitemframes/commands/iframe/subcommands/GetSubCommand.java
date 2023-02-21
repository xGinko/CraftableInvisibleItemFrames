package me.xginko.craftableinvisibleitemframes.commands.iframe.subcommands;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.commands.SubCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

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
                ItemStack invisible_regular_item_frame = new ItemStack(Material.ITEM_FRAME, 1);
                ItemMeta meta = invisible_regular_item_frame.getItemMeta();
                if (CraftableInvisibleItemFrames.getConfiguration().regular_item_frames_should_be_enchanted) {
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    meta.addEnchant(Enchantment.DURABILITY, 1, true);
                }
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', CraftableInvisibleItemFrames.getLang(sender).invisible_item_frame));
                meta.getPersistentDataContainer().set(CraftableInvisibleItemFrames.getInstance().regular_invisible_item_frame_tag, PersistentDataType.BYTE, (byte) 1);
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
