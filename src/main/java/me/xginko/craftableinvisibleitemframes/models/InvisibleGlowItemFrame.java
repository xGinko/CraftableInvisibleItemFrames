package me.xginko.craftableinvisibleitemframes.models;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.enums.Keys;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Locale;

public class InvisibleGlowItemFrame extends ItemStack {

    public InvisibleGlowItemFrame(final int stackSize) {
        this(stackSize, CraftableInvisibleItemFrames.config().default_lang);
    }

    public InvisibleGlowItemFrame(final int stackSize, final Locale locale) {
        super(Material.GLOW_ITEM_FRAME, stackSize);
        ItemMeta meta = getItemMeta();
        meta.displayName(CraftableInvisibleItemFrames.getLang(locale).glow_invisible_item_frame);
        meta.getPersistentDataContainer().set(Keys.INVISIBLE_GLOW_ITEM_FRAME.key(), PersistentDataType.BYTE, (byte) 1);
        if (CraftableInvisibleItemFrames.config().glowsquid_item_frames_should_be_enchanted) {
            meta.addEnchant(Enchantment.CHANNELING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        setItemMeta(meta);
    }
}