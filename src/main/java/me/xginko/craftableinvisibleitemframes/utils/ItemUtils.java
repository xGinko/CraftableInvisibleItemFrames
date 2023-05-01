package me.xginko.craftableinvisibleitemframes.utils;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.config.Config;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;
import java.util.Locale;
import java.util.Random;

public class ItemUtils {

    public static ItemStack getGlowsquidInvisibleItemFrame(int amount) {
        ItemStack invisible_glowsquid_frame = new ItemStack(Material.GLOW_ITEM_FRAME, amount);
        invisible_glowsquid_frame.editMeta(meta -> {
            Config config = CraftableInvisibleItemFrames.getConfiguration();
            if (config.glowsquid_item_frames_should_be_enchanted) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                meta.addEnchant(Enchantment.CHANNELING, 1, true);
            }
            meta.displayName(CraftableInvisibleItemFrames.getLang(config.default_lang).glow_invisible_item_frame);
            meta.getPersistentDataContainer().set(CraftableInvisibleItemFrames.getGlowsquidInvisibleItemFrameTag(), PersistentDataType.BYTE, (byte) 1);
        });
        return invisible_glowsquid_frame;
    }

    public static ItemStack getGlowsquidInvisibleItemFrame(int amount, Locale locale) {
        ItemStack localed_invisible_glowsquid_frame = getGlowsquidInvisibleItemFrame(amount);
        localed_invisible_glowsquid_frame.editMeta(meta -> meta.displayName(CraftableInvisibleItemFrames.getLang(locale).glow_invisible_item_frame));
        return localed_invisible_glowsquid_frame;
    }

    public static boolean isGlowsquidInvisibleItemFrame(ItemStack itemStack) {
        if (itemStack == null) return false;
        if (itemStack.getType().equals(Material.ITEM_FRAME)) {
            return itemStack.getItemMeta().getPersistentDataContainer().has(CraftableInvisibleItemFrames.getGlowsquidInvisibleItemFrameTag());
        } else {
            return false;
        }
    }

    public static ItemStack getRegularInvisibleItemFrame(int amount) {
        ItemStack invisible_regular_item_frame = new ItemStack(Material.ITEM_FRAME, amount);
        invisible_regular_item_frame.editMeta(meta -> {
            Config config = CraftableInvisibleItemFrames.getConfiguration();
            if (config.regular_item_frames_should_be_enchanted) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                meta.addEnchant(Enchantment.CHANNELING, 1, true);
            }
            meta.displayName(CraftableInvisibleItemFrames.getLang(config.default_lang).invisible_item_frame);
            meta.getPersistentDataContainer().set(CraftableInvisibleItemFrames.getRegularInvisibleItemFrameTag(), PersistentDataType.BYTE, (byte) 1);
        });
        return invisible_regular_item_frame;
    }

    public static ItemStack getRegularInvisibleItemFrame(int amount, Locale locale) {
        ItemStack localed_invisible_regular_item_frame = getRegularInvisibleItemFrame(amount);
        localed_invisible_regular_item_frame.editMeta(meta -> meta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', CraftableInvisibleItemFrames.getLang(locale).invisible_item_frame))));
        return localed_invisible_regular_item_frame;
    }

    public static boolean isRegularInvisibleItemFrame(ItemStack itemStack) {
        if (itemStack == null) return false;
        if (itemStack.getType().equals(Material.ITEM_FRAME)) {
            return itemStack.getItemMeta().getPersistentDataContainer().has(CraftableInvisibleItemFrames.getRegularInvisibleItemFrameTag());
        } else {
            return false;
        }
    }

    public static Locale getRandomNearbyPlayerLocaleOrDefault(Location location) {
        Collection<Player> nearbyPlayers = location.getNearbyPlayers(4,4,4);
        return nearbyPlayers.isEmpty() ? CraftableInvisibleItemFrames.getConfiguration().default_lang : nearbyPlayers.stream().toList().get(new Random().nextInt(nearbyPlayers.size())).locale();
    }

    public static void addToInventoryOrDrop(Player player, ItemStack item) {
        player.getInventory().addItem(item).forEach((index, itemsThatDidNotFit) -> player.getWorld().dropItemNaturally(player.getLocation(), itemsThatDidNotFit).setPickupDelay(0));
    }
}
