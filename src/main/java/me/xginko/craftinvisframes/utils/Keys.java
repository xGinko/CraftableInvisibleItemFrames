package me.xginko.craftinvisframes.utils;

import me.xginko.craftinvisframes.CraftInvisFrames;
import org.bukkit.NamespacedKey;

public enum Keys {

    INVISIBLE_ITEM_FRAME("invisible-itemframe"),
    INVISIBLE_GLOW_ITEM_FRAME("invisible-glowsquid-itemframe"),

    INVISIBLE_ITEM_FRAME_RECIPE("invisible-itemframe-recipe"),
    INVISIBLE_GLOW_ITEM_FRAME_RECIPE("invisible-glow-itemframe-recipe");

    private final NamespacedKey key;

    Keys(String identifier) {
        this.key = new NamespacedKey(CraftInvisFrames.get(), identifier);
    }

    public NamespacedKey get() {
        return key;
    }
}
