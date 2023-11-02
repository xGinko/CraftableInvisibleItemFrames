package me.xginko.craftableinvisibleitemframes.enums;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import org.bukkit.NamespacedKey;

public enum Keys {

    INVISIBLE_ITEM_FRAME(CraftableInvisibleItemFrames.getKey("invisible-itemframe")),
    INVISIBLE_ITEM_FRAME_RECIPE(CraftableInvisibleItemFrames.getKey("invisible-itemframe-recipe")),
    INVISIBLE_GLOW_ITEM_FRAME(CraftableInvisibleItemFrames.getKey("invisible-glowsquid-itemframe"));

    private final NamespacedKey key;

    Keys(NamespacedKey key) {
        this.key = key;
    }

    public NamespacedKey key() {
        return key;
    }

}
