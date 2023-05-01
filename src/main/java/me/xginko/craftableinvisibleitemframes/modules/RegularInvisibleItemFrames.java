package me.xginko.craftableinvisibleitemframes.modules;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.config.Config;
import me.xginko.craftableinvisibleitemframes.utils.DroppedFrameLocation;
import me.xginko.craftableinvisibleitemframes.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Iterator;

public class RegularInvisibleItemFrames implements CraftableInvisibleItemFramesModule, Listener {

    private final CraftableInvisibleItemFrames plugin;
    private final Config config;
    private final HashSet<DroppedFrameLocation> droppedRegularFrames = new HashSet<>();

    protected RegularInvisibleItemFrames() {
        this.plugin = CraftableInvisibleItemFrames.getInstance();
        this.config = CraftableInvisibleItemFrames.getConfiguration();
    }

    @Override
    public void enable() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return config.regular_invisible_itemframes_are_enabled;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onHangingPlace(HangingPlaceEvent event) {
        Player player = event.getPlayer();
        if (player != null && event.getEntity() instanceof ItemFrame itemFrameEntity) {
            if (
                    ItemUtils.isRegularInvisibleItemFrame(player.getInventory().getItemInMainHand())
                    || ItemUtils.isRegularInvisibleItemFrame(player.getInventory().getItemInOffHand())
            ) {
                if (!player.hasPermission("craftableinvisibleitemframes.place")) {
                    event.setCancelled(true);
                    return;
                }

                if (config.regular_placed_item_frames_have_glowing_outlines) {
                    itemFrameEntity.setVisible(true);
                    itemFrameEntity.setGlowing(true);
                } else {
                    itemFrameEntity.setVisible(false);
                }

                itemFrameEntity.getPersistentDataContainer().set(CraftableInvisibleItemFrames.getRegularInvisibleItemFrameTag(), PersistentDataType.BYTE, (byte) 1);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onHangingBreak(HangingBreakEvent event) {
        if (event.getEntity() instanceof ItemFrame itemFrame) {
            if (!itemFrame.getPersistentDataContainer().has(CraftableInvisibleItemFrames.getRegularInvisibleItemFrameTag(), PersistentDataType.BYTE)) return;
            // Sets up a bounding box that checks for items near the frame and converts them
            DroppedFrameLocation droppedFrameLocation = new DroppedFrameLocation(itemFrame.getLocation());
            droppedRegularFrames.add(droppedFrameLocation);
            droppedFrameLocation.setRemoval((new BukkitRunnable() {
                @Override
                public void run() {
                    droppedRegularFrames.remove(droppedFrameLocation);
                }
            }).runTaskLater(plugin, 20L));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    private void onItemSpawn(ItemSpawnEvent event) {
        Item itemEntity = event.getEntity();
        if (!itemEntity.getItemStack().getType().equals(Material.ITEM_FRAME)) return;

        Iterator<DroppedFrameLocation> droppedFrameLocationIterator = droppedRegularFrames.iterator();
        while (droppedFrameLocationIterator.hasNext()) {
            DroppedFrameLocation droppedFrameLocation = droppedFrameLocationIterator.next();
            if (droppedFrameLocation.isFrame(itemEntity)) {
                itemEntity.setItemStack(ItemUtils.getRegularInvisibleItemFrame(1, ItemUtils.getRandomNearbyPlayerLocaleOrDefault(itemEntity.getLocation())));
                droppedFrameLocation.getRemoval().cancel();
                droppedFrameLocationIterator.remove();
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof ItemFrame itemFrame) {
            if (
                    itemFrame.getPersistentDataContainer().has(CraftableInvisibleItemFrames.getRegularInvisibleItemFrameTag(), PersistentDataType.BYTE)
            ) {
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (!itemFrame.getItem().getType().equals(Material.AIR)) {
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
            if (
                    itemFrame.getPersistentDataContainer().has(CraftableInvisibleItemFrames.getRegularInvisibleItemFrameTag(), PersistentDataType.BYTE)
            ) {
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (itemFrame.getItem().getType().equals(Material.AIR)) {
                        if (config.regular_placed_item_frames_have_glowing_outlines) itemFrame.setGlowing(true);
                        itemFrame.setVisible(true);
                    }
                }, 1L);
            }
        }
    }
}
