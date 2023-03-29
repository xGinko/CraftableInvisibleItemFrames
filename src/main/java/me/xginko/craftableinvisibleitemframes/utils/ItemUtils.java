package me.xginko.craftableinvisibleitemframes.utils;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.config.Config;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ItemUtils {

    public static ItemStack getGlowsquidInvisibleItemFrame(int amount) {
        ItemStack invisible_glowsquid_frame = new ItemStack(Material.GLOW_ITEM_FRAME, amount);
        ItemMeta meta = invisible_glowsquid_frame.getItemMeta();
        Config config = CraftableInvisibleItemFrames.getConfiguration();
        if (config.glowsquid_item_frames_should_be_enchanted) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.CHANNELING, 1, true);
        }
        meta.displayName(Component.text((ChatColor.translateAlternateColorCodes('&', CraftableInvisibleItemFrames.getLang(config.default_lang).glow_invisible_item_frame))));
        meta.getPersistentDataContainer().set(CraftableInvisibleItemFrames.getGlowsquidInvisibleItemFrameTag(), PersistentDataType.BYTE, (byte) 1);
        invisible_glowsquid_frame.setItemMeta(meta);
        return invisible_glowsquid_frame;
    }

    public static ItemStack getGlowsquidInvisibleItemFrame(int amount, Locale locale) {
        ItemStack localed_invisible_glowsquid_frame = getGlowsquidInvisibleItemFrame(amount);
        ItemMeta meta = localed_invisible_glowsquid_frame.getItemMeta();
        meta.displayName(Component.text((ChatColor.translateAlternateColorCodes('&', CraftableInvisibleItemFrames.getLang(locale).glow_invisible_item_frame))));
        localed_invisible_glowsquid_frame.setItemMeta(meta);
        return localed_invisible_glowsquid_frame;
    }

    public static ItemStack getRegularInvisibleItemFrame(int amount) {
        ItemStack invisible_regular_item_frame = new ItemStack(Material.ITEM_FRAME, amount);
        ItemMeta meta = invisible_regular_item_frame.getItemMeta();
        Config config = CraftableInvisibleItemFrames.getConfiguration();
        if (config.regular_item_frames_should_be_enchanted) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.CHANNELING, 1, true);
        }
        meta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', CraftableInvisibleItemFrames.getLang(config.default_lang).invisible_item_frame)));
        meta.getPersistentDataContainer().set(CraftableInvisibleItemFrames.getRegularInvisibleItemFrameTag(), PersistentDataType.BYTE, (byte) 1);
        invisible_regular_item_frame.setItemMeta(meta);
        return invisible_regular_item_frame;
    }

    public static ItemStack getRegularInvisibleItemFrame(int amount, Locale locale) {
        ItemStack localed_invisible_regular_item_frame = getRegularInvisibleItemFrame(amount);
        ItemMeta meta = localed_invisible_regular_item_frame.getItemMeta();
        meta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', CraftableInvisibleItemFrames.getLang(locale).invisible_item_frame)));
        localed_invisible_regular_item_frame.setItemMeta(meta);
        return localed_invisible_regular_item_frame;
    }

    public static Player getRandomNearbyPlayer(Location location) {
        List<Player> nearbyPlayers = new ArrayList<>();
        for (Entity entity : location.getNearbyEntities(4, 4, 4)) {
            if (entity instanceof Player player) {
                nearbyPlayers.add(player);
            }
        }
        return nearbyPlayers.isEmpty() ? null : nearbyPlayers.get(new Random().nextInt(nearbyPlayers.size()));
    }
}
