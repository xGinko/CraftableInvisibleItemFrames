package me.xginko.craftableinvisibleitemframes.enums;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import org.bukkit.NamespacedKey;

public enum Keys {

    INVISIBLE_ITEM_FRAME(get("invisible-itemframe")),
    INVISIBLE_ITEM_FRAME_RECIPE(get("invisible-itemframe-recipe")),
    INVISIBLE_GLOW_ITEM_FRAME(get("invisible-glowsquid-itemframe"));

    private final NamespacedKey key;

    Keys(NamespacedKey key) {
        this.key = key;
    }

    public static NamespacedKey get(String key) {
        return new NamespacedKey(CraftableInvisibleItemFrames.getInstance(), key);
    }

    public NamespacedKey get() {
        return key;
    }
}
