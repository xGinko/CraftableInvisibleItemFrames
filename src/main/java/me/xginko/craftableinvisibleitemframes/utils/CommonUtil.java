package me.xginko.craftableinvisibleitemframes.utils;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.enums.Keys;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;
import java.util.Locale;
import java.util.Random;

public class CommonUtil {

    public static boolean isInvisibleItemFrame(ItemStack itemStack) {
        return
                itemStack != null
                && itemStack.getType().equals(Material.ITEM_FRAME)
                && itemStack.getItemMeta().getPersistentDataContainer().has(Keys.INVISIBLE_ITEM_FRAME.key(), PersistentDataType.BYTE)
        ;
    }

    public static Locale getRandomNearbyPlayerLang(Location location) {
        Collection<Player> nearbyPlayers = location.getNearbyPlayers(4,4,4);
        return nearbyPlayers.isEmpty() ? CraftableInvisibleItemFrames.getConfiguration().default_lang : nearbyPlayers.stream().toList().get(new Random().nextInt(nearbyPlayers.size())).locale();
    }

    public static void addToInventoryOrDrop(Player player, ItemStack item) {
        player.getInventory().addItem(item).forEach((index, itemsThatDidNotFit) -> player.getWorld().dropItemNaturally(player.getLocation(), itemsThatDidNotFit).setPickupDelay(1));
    }
}
