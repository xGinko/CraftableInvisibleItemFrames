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
        return itemStack != null
        && itemStack.getType().equals(Material.ITEM_FRAME)
        && itemStack.getItemMeta().getPersistentDataContainer().has(Keys.INVISIBLE_ITEM_FRAME.key(), PersistentDataType.BYTE);
    }

    public static Locale getRandomNearbyPlayerLang(Location location) {
        Collection<Player> nearbyPlayers = location.getNearbyPlayers(4,4,4);
        if (nearbyPlayers.isEmpty()) return CraftableInvisibleItemFrames.getConfiguration().default_lang;
        return nearbyPlayers.stream().toList().get(new Random().nextInt(nearbyPlayers.size())).locale();
    }
}