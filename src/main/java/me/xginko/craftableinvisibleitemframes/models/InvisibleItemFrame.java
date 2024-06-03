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

public class InvisibleItemFrame extends ItemStack {

    public InvisibleItemFrame(final int stackSize) {
        this(stackSize, CraftableInvisibleItemFrames.config().default_lang);
    }

    public InvisibleItemFrame(final int stackSize, final Locale locale) {
        super(Material.ITEM_FRAME, stackSize);
        ItemMeta meta = getItemMeta();
        meta.displayName(CraftableInvisibleItemFrames.getLang(locale).invisible_item_frame);
        meta.getPersistentDataContainer().set(Keys.INVISIBLE_ITEM_FRAME.get(), PersistentDataType.BYTE, (byte) 1);
        if (CraftableInvisibleItemFrames.config().regular_item_frames_should_be_enchanted) {
            meta.addEnchant(Enchantment.CHANNELING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        setItemMeta(meta);
    }
}