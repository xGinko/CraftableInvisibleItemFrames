package me.xginko.craftinvisframes.modules;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.xginko.craftinvisframes.utils.Keys;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.persistence.PersistentDataType;

import java.util.function.Consumer;

public class OutlineRecheck extends FrameModule implements Consumer<ScheduledTask> {

    public OutlineRecheck() {
        super("recheck-glowing-outlines-on-reload", true);
    }

    @Override
    public void enable() {
        plugin.getServer().getGlobalRegionScheduler().run(plugin, this);
    }

    @Override
    public void disable() {
    }

    @Override
    public void accept(ScheduledTask scheduledTask) {
        for (World world : plugin.getServer().getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                plugin.getServer().getRegionScheduler().execute(plugin, chunk.getWorld(), chunk.getX(), chunk.getZ(), () -> {
                    if (!chunk.isEntitiesLoaded()) return;

                    for (Entity entity : chunk.getEntities()) {
                        if (entity.getType() != EntityType.ITEM_FRAME) continue;

                        ItemFrame itemFrame = (ItemFrame) entity;

                        if (itemFrame.getPersistentDataContainer().has(Keys.INVISIBLE_GLOW_ITEM_FRAME.get(), PersistentDataType.BYTE)) {
                            if (plugin.config().glowsquid_placed_item_frames_have_glowing_outlines
                                    && itemFrame.getItem().getType() == Material.AIR) {
                                itemFrame.setGlowing(true);
                                itemFrame.setVisible(true);
                            }
                            else if (itemFrame.getItem().getType() != Material.AIR) {
                                itemFrame.setGlowing(false);
                                itemFrame.setVisible(false);
                            }
                        }

                        if (itemFrame.getPersistentDataContainer().has(Keys.INVISIBLE_ITEM_FRAME.get(), PersistentDataType.BYTE)) {
                            if (plugin.config().regular_placed_item_frames_have_glowing_outlines
                                    && itemFrame.getItem().getType() == Material.AIR) {
                                itemFrame.setGlowing(true);
                                itemFrame.setVisible(true);
                            }
                            else if (itemFrame.getItem().getType() != Material.AIR) {
                                itemFrame.setGlowing(false);
                                itemFrame.setVisible(false);
                            }
                        }
                    }
                });
            }
        }
    }
}
