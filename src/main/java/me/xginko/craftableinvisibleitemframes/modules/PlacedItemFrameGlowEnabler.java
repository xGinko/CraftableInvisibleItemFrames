package me.xginko.craftableinvisibleitemframes.modules;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.persistence.PersistentDataType;

public class PlacedItemFrameGlowEnabler implements CraftableInvisibleItemFramesModule, Listener {

    private final CraftableInvisibleItemFrames plugin;

    protected PlacedItemFrameGlowEnabler() {
        this.plugin = CraftableInvisibleItemFrames.getInstance();
    }

    @Override
    public void enable() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return false;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof ItemFrame itemFrame) {
            if (itemFrame.getPersistentDataContainer().has(CraftableInvisibleItemFrames.getInvisibleItemFrameTag(), PersistentDataType.BYTE)) {
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if(!itemFrame.getItem().getType().equals(Material.AIR)) {
                        itemFrame.setGlowing(false);
                        itemFrame.setVisible(false);
                    }
                }, 1L);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof ItemFrame itemFrame) {
            if (itemFrame.getPersistentDataContainer().has(CraftableInvisibleItemFrames.getInvisibleItemFrameTag(), PersistentDataType.BYTE)) {
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if(itemFrame.getItem().getType().equals(Material.AIR)) {
                        itemFrame.setGlowing(true);
                        itemFrame.setVisible(true);
                    }
                }, 1L);
            }
        }
    }
}
