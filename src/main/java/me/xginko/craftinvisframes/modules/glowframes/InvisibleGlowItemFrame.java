package me.xginko.craftinvisframes.modules.glowframes;

import me.xginko.craftinvisframes.utils.Keys;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public final class InvisibleGlowItemFrame extends ItemStack {

    public InvisibleGlowItemFrame(int stackSize, Component displayName, boolean enchant) {
        super(Material.GLOW_ITEM_FRAME, stackSize);
        ItemMeta itemMeta = getItemMeta();
        itemMeta.displayName(displayName);
        itemMeta.getPersistentDataContainer().set(Keys.INVISIBLE_GLOW_ITEM_FRAME.get(), PersistentDataType.BYTE, (byte) 1);
        if (enchant) {
            itemMeta.addEnchant(Enchantment.CHANNELING, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_STORED_ENCHANTS);
        }
        setItemMeta(itemMeta);
    }

    public static boolean isInvisibleGlowItemFrame(@Nullable ItemStack itemStack) {
        return  itemStack != null
                && itemStack.getType() == Material.GLOW_ITEM_FRAME
                && itemStack.getItemMeta().getPersistentDataContainer()
                .has(Keys.INVISIBLE_GLOW_ITEM_FRAME.get(), PersistentDataType.BYTE);
    }
}