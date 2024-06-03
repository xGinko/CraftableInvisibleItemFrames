package me.xginko.craftableinvisibleitemframes.models;

import com.tcoded.folialib.impl.ServerImplementation;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.enums.Keys;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.persistence.PersistentDataType;

import java.util.function.Consumer;

public class ReApplyGlowOutlinesTask implements Consumer<WrappedTask> {

    private final boolean glow_item_frames, reg_item_frames;

    public ReApplyGlowOutlinesTask(boolean glowItemFrames, boolean regItemFrames) {
        this.glow_item_frames = glowItemFrames;
        this.reg_item_frames = regItemFrames;
    }

    @Override
    public void accept(WrappedTask wrappedTask) {
        ServerImplementation scheduler = CraftableInvisibleItemFrames.foliaLib().getImpl();
        for (World world : CraftableInvisibleItemFrames.getInstance().getServer().getWorlds()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                // Workaround due to FoliaLib not having a method that allows passing chunk x and y
                Location chunkLoc = new Location(world, chunk.getX() << 4, world.getMaxHeight(), chunk.getZ() << 4);
                scheduler.runAtLocation(chunkLoc, forEachChunk -> { // Folia 1.20.4 changes made this necessary
                    if (!chunk.isLoaded()) return;

                    for (Entity entity : world.getEntities()) {
                        scheduler.runAtEntity(entity, applyOutlineSettings -> { // Folia 1.20.4 changes made this necessary
                            if (!entity.getType().name().contains("ITEM_FRAME")) return;
                            ItemFrame itemFrame = (ItemFrame) entity;

                            if (itemFrame.getPersistentDataContainer().has(Keys.INVISIBLE_GLOW_ITEM_FRAME.key(), PersistentDataType.BYTE)) {
                                if (glow_item_frames && itemFrame.getItem().getType().equals(Material.AIR)) {
                                    itemFrame.setGlowing(true);
                                    itemFrame.setVisible(true);
                                }
                                else if (!itemFrame.getItem().getType().equals(Material.AIR)) {
                                    itemFrame.setGlowing(false);
                                    itemFrame.setVisible(false);
                                }
                            }

                            if (itemFrame.getPersistentDataContainer().has(Keys.INVISIBLE_ITEM_FRAME.key(), PersistentDataType.BYTE)) {
                                if (reg_item_frames && itemFrame.getItem().getType() == Material.AIR) {
                                    itemFrame.setGlowing(true);
                                    itemFrame.setVisible(true);
                                }
                                else if (itemFrame.getItem().getType() != Material.AIR) {
                                    itemFrame.setGlowing(false);
                                    itemFrame.setVisible(false);
                                }
                            }
                        });
                    }
                });
            }
        }
    }
}
