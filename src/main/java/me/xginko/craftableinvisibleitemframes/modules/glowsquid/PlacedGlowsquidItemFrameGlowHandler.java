package me.xginko.craftableinvisibleitemframes.modules.glowsquid;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.modules.CraftableInvisibleItemFramesModule;
import org.bukkit.Material;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.persistence.PersistentDataType;

public class PlacedGlowsquidItemFrameGlowHandler implements CraftableInvisibleItemFramesModule, Listener {

    private final CraftableInvisibleItemFrames plugin;

    public PlacedGlowsquidItemFrameGlowHandler() {
        this.plugin = CraftableInvisibleItemFrames.getInstance();
    }

    @Override
    public void enable() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return CraftableInvisibleItemFrames.getConfiguration().glowsquid_placed_item_frames_have_glowing_outlines;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof GlowItemFrame glowItemFrame) {
            if (
                    glowItemFrame.getPersistentDataContainer().has(CraftableInvisibleItemFrames.getGlowsquidInvisibleItemFrameTag(), PersistentDataType.BYTE)
            ) {
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (!glowItemFrame.getItem().getType().equals(Material.AIR)) {
                        glowItemFrame.setGlowing(false);
                        glowItemFrame.setVisible(false);
                    }
                }, 1L);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof GlowItemFrame glowItemFrame) {
            if (
                    glowItemFrame.getPersistentDataContainer().has(CraftableInvisibleItemFrames.getGlowsquidInvisibleItemFrameTag(), PersistentDataType.BYTE)
            ) {
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (glowItemFrame.getItem().getType().equals(Material.AIR)) {
                        glowItemFrame.setGlowing(true);
                        glowItemFrame.setVisible(true);
                    }
                }, 1L);
            }
        }
    }
}
