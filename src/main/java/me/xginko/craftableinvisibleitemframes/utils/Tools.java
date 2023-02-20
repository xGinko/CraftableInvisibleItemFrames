package me.xginko.craftableinvisibleitemframes.utils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Tools {

    public static Player getRandomNearbyPlayer(Location location) {
        List<Player> nearbyPlayers = new ArrayList<>();
        for (Entity entity : location.getNearbyEntities(3, 3, 3)) {
            if (entity instanceof Player player) {
                nearbyPlayers.add(player);
            }
        }
        return nearbyPlayers.isEmpty() ? null : nearbyPlayers.get(new Random().nextInt(nearbyPlayers.size()));
    }

}
