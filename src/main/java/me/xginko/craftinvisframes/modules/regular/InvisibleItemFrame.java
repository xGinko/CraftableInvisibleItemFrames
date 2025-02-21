package me.xginko.craftinvisframes.modules.regular;

import me.xginko.craftinvisframes.utils.Keys;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class InvisibleItemFrame extends ItemStack {

    public InvisibleItemFrame(int stackSize, Component displayName, boolean enchant) {
        super(Material.ITEM_FRAME, stackSize);
        ItemMeta meta = getItemMeta();
        meta.displayName(displayName);
        meta.getPersistentDataContainer().set(Keys.INVISIBLE_ITEM_FRAME.get(), PersistentDataType.BYTE, (byte) 1);
        if (enchant) {
            meta.addEnchant(Enchantment.CHANNELING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_STORED_ENCHANTS);
        }
        setItemMeta(meta);
    }

    public static boolean isInvisibleItemFrame(ItemStack itemStack) {
        return  itemStack != null
                && itemStack.getType() == Material.ITEM_FRAME
                && itemStack.getItemMeta().getPersistentDataContainer()
                .has(Keys.INVISIBLE_ITEM_FRAME.get(), PersistentDataType.BYTE);
    }
}