package me.xginko.craftinvisframes.utils;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.util.BoundingBox;

public final class BrokenFrameArea {

    private final BoundingBox box;
    
    public BrokenFrameArea(Location location) {
        this.box = BoundingBox.of(location, 1.0, 1.0, 1.0);
    }

    public boolean contains(Item item) {
        return box.contains(item.getBoundingBox());
    }
}