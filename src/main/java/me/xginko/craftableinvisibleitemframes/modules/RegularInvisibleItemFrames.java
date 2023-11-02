package me.xginko.craftableinvisibleitemframes.modules;

import com.tcoded.folialib.impl.ServerImplementation;
import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.enums.Keys;
import me.xginko.craftableinvisibleitemframes.models.InvisibleItemFrame;
import me.xginko.craftableinvisibleitemframes.models.DroppedFrameLocation;
import me.xginko.craftableinvisibleitemframes.utils.CommonUtil;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class RegularInvisibleItemFrames implements PluginModule, Listener {

    private final ServerImplementation scheduler;
    private final HashSet<DroppedFrameLocation> droppedRegularFrames = new HashSet<>();
    private final boolean placed_item_frames_have_glowing_outlines;

    protected RegularInvisibleItemFrames() {
        this.scheduler = CraftableInvisibleItemFrames.getInstance().getCompatibleScheduler();
        this.placed_item_frames_have_glowing_outlines = CraftableInvisibleItemFrames.getConfiguration().regular_placed_item_frames_have_glowing_outlines;
    }

    @Override
    public void enable() {
        CraftableInvisibleItemFrames plugin = CraftableInvisibleItemFrames.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return CraftableInvisibleItemFrames.getConfiguration().regular_invisible_itemframes_are_enabled;
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onHangingPlace(HangingPlaceEvent event) {
        Hanging hanging = event.getEntity();
        if (!hanging.getType().equals(EntityType.ITEM_FRAME)) return;
        Player player = event.getPlayer();
        if (player == null) return;

        if (
                CommonUtil.isInvisibleItemFrame(player.getInventory().getItemInMainHand())
                || CommonUtil.isInvisibleItemFrame(player.getInventory().getItemInOffHand())
        ) {
            if (!player.hasPermission("craftableinvisibleitemframes.place")) {
                event.setCancelled(true);
                return;
            }

            scheduler.runAtEntity(hanging, manage -> {
                ItemFrame itemFrame = (ItemFrame) hanging;
                if (!placed_item_frames_have_glowing_outlines) {
                    itemFrame.setVisible(false);
                } else {
                    itemFrame.setVisible(true);
                    itemFrame.setGlowing(true);
                }
                itemFrame.getPersistentDataContainer().set(Keys.INVISIBLE_ITEM_FRAME.key(), PersistentDataType.BYTE, (byte) 1);
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onHangingBreak(HangingBreakEvent event) {
        Hanging hanging = event.getEntity();
        if (!hanging.getType().equals(EntityType.ITEM_FRAME)) return;
        if (!hanging.getPersistentDataContainer().has(Keys.INVISIBLE_ITEM_FRAME.key(), PersistentDataType.BYTE)) return;

        // Sets up a bounding box that checks for items near the frame and converts them
        DroppedFrameLocation droppedFrameLocation = new DroppedFrameLocation(hanging.getLocation());
        droppedRegularFrames.add(droppedFrameLocation);
        droppedFrameLocation.setRemoval(scheduler.runLater(() -> droppedRegularFrames.remove(droppedFrameLocation), 1, TimeUnit.SECONDS));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onItemSpawn(ItemSpawnEvent event) {
        Item itemEntity = event.getEntity();
        if (!itemEntity.getItemStack().getType().equals(Material.ITEM_FRAME)) return;

        Iterator<DroppedFrameLocation> droppedFrameLocationIterator = droppedRegularFrames.iterator();
        while (droppedFrameLocationIterator.hasNext()) {
            DroppedFrameLocation droppedFrameLocation = droppedFrameLocationIterator.next();
            if (droppedFrameLocation.isFrame(itemEntity)) {
                itemEntity.setItemStack(new InvisibleItemFrame(1, CommonUtil.getRandomNearbyPlayerLang(itemEntity.getLocation())));
                droppedFrameLocation.getRemoval().cancel();
                droppedFrameLocationIterator.remove();
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity clicked = event.getRightClicked();
        if (!clicked.getType().equals(EntityType.ITEM_FRAME)) return;
        if (!clicked.getPersistentDataContainer().has(Keys.INVISIBLE_ITEM_FRAME.key(), PersistentDataType.BYTE)) return;

        scheduler.runAtEntityLater(clicked, () -> {
            ItemFrame itemFrame = (ItemFrame) clicked;
            if (!itemFrame.getItem().getType().equals(Material.AIR)) {
                itemFrame.setGlowing(false);
                itemFrame.setVisible(false);
            }
        }, 50, TimeUnit.MILLISECONDS);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();
        if (!damaged.getType().equals(EntityType.ITEM_FRAME)) return;
        if (!damaged.getPersistentDataContainer().has(Keys.INVISIBLE_ITEM_FRAME.key(), PersistentDataType.BYTE)) return;

        scheduler.runAtEntityLater(damaged, () -> {
            ItemFrame itemFrame = (ItemFrame) damaged;
            if (itemFrame.getItem().getType().equals(Material.AIR)) {
                itemFrame.setVisible(true);
                if (placed_item_frames_have_glowing_outlines)
                    itemFrame.setGlowing(true);
            }
        }, 50, TimeUnit.MILLISECONDS);
    }
}