package me.xginko.craftableinvisibleitemframes.modules;

import com.tcoded.folialib.impl.ServerImplementation;
import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.enums.Keys;
import me.xginko.craftableinvisibleitemframes.models.DroppedFrameLocation;
import me.xginko.craftableinvisibleitemframes.models.InvisibleGlowItemFrame;
import me.xginko.craftableinvisibleitemframes.utils.Util;
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
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

public class GlowsquidInvisibleItemFrames implements PluginModule, Listener {

    private final ServerImplementation scheduler;
    private final Set<DroppedFrameLocation> droppedGlowsquidFrames = new CopyOnWriteArraySet<>();
    private final boolean placed_item_frames_have_glowing_outlines;

    protected GlowsquidInvisibleItemFrames() {
        this.scheduler = CraftableInvisibleItemFrames.foliaLib().getImpl();
        this.placed_item_frames_have_glowing_outlines = CraftableInvisibleItemFrames.config().glowsquid_placed_item_frames_have_glowing_outlines;
    }

    @Override
    public void enable() {
        CraftableInvisibleItemFrames plugin = CraftableInvisibleItemFrames.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return CraftableInvisibleItemFrames.config().glowsquid_invisible_itemframes_are_enabled;
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onCraft(PrepareItemCraftEvent event) {
        if (!Util.isInvisibleItemFrameRecipe(event.getRecipe())) return;
        if (!(event.getView().getPlayer() instanceof Player)) return;
        Player player = (Player) event.getView().getPlayer();

        boolean foundInvisIframe = false;
        boolean foundGlowIncSac = false;
        CraftingInventory craftingInventory = event.getInventory();

        for (ItemStack item : craftingInventory.getMatrix()) {
            if (item == null || item.getType() == Material.AIR) continue;

            if (item.getType() == Material.GLOW_INK_SAC) {
                if (foundGlowIncSac) return;
                foundGlowIncSac = true;
                continue;
            }

            if (Util.isInvisibleItemFrame(item)) {
                if (foundInvisIframe) return;
                foundInvisIframe = true;
                continue;
            }

            return;
        }

        if (foundInvisIframe && foundGlowIncSac) {
            if (player.hasPermission("craftableinvisibleitemframes.craft")) {
                craftingInventory.setResult(new InvisibleGlowItemFrame(1, player.locale()));
            } else {
                craftingInventory.setResult(null);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onHangingPlace(HangingPlaceEvent event) {
        Hanging hanging = event.getEntity();
        if (hanging.getType() != EntityType.GLOW_ITEM_FRAME) return;
        if (!Util.isInvisibleGlowItemFrame(event.getItemStack())) return;
        final Player player = event.getPlayer();
        if (player == null) return;

        if (!player.hasPermission("craftableinvisibleitemframes.place")) {
            event.setCancelled(true);
            return;
        }

        scheduler.runAtEntity(hanging, manage -> {
            GlowItemFrame glowItemFrame = (GlowItemFrame) hanging;
            if (!placed_item_frames_have_glowing_outlines) {
                glowItemFrame.setVisible(false);
            } else {
                glowItemFrame.setVisible(true);
                glowItemFrame.setGlowing(true);
            }
            glowItemFrame.getPersistentDataContainer().set(Keys.INVISIBLE_GLOW_ITEM_FRAME.get(), PersistentDataType.BYTE, (byte) 1);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onHangingBreak(HangingBreakEvent event) {
        Hanging hanging = event.getEntity();
        if (hanging.getType() != EntityType.GLOW_ITEM_FRAME) return;
        if (!hanging.getPersistentDataContainer().has(Keys.INVISIBLE_GLOW_ITEM_FRAME.get(), PersistentDataType.BYTE)) return;

        // Sets up a bounding box that checks for items near the frame to convert them
        DroppedFrameLocation droppedFrameLocation = new DroppedFrameLocation(hanging.getLocation());
        droppedGlowsquidFrames.add(droppedFrameLocation);
        droppedFrameLocation.setRemoval(scheduler.runLater(() -> droppedGlowsquidFrames.remove(droppedFrameLocation), 1, TimeUnit.SECONDS));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onItemSpawn(ItemSpawnEvent event) {
        Item itemEntity = event.getEntity();
        if (itemEntity.getItemStack().getType() != Material.GLOW_ITEM_FRAME) return;

        Iterator<DroppedFrameLocation> droppedFrameLocationIterator = droppedGlowsquidFrames.iterator();
        while (droppedFrameLocationIterator.hasNext()) {
            DroppedFrameLocation droppedFrameLocation = droppedFrameLocationIterator.next();
            if (droppedFrameLocation.isFrame(itemEntity)) {
                itemEntity.setItemStack(new InvisibleGlowItemFrame(1, Util.getRandomNearbyPlayerLang(itemEntity.getLocation())));
                droppedFrameLocation.getRemoval().cancel();
                droppedFrameLocationIterator.remove();
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity clicked = event.getRightClicked();
        if (clicked.getType() != EntityType.GLOW_ITEM_FRAME) return;
        if (!clicked.getPersistentDataContainer().has(Keys.INVISIBLE_GLOW_ITEM_FRAME.get(), PersistentDataType.BYTE)) return;

        scheduler.runAtEntityLater(clicked, () -> {
            GlowItemFrame glowItemFrame = (GlowItemFrame) clicked;
            if (glowItemFrame.getItem().getType() != Material.AIR) {
                glowItemFrame.setGlowing(false);
                glowItemFrame.setVisible(false);
            }
        }, 50, TimeUnit.MILLISECONDS);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();
        if (damaged.getType() != EntityType.GLOW_ITEM_FRAME) return;
        if (!damaged.getPersistentDataContainer().has(Keys.INVISIBLE_GLOW_ITEM_FRAME.get(), PersistentDataType.BYTE)) return;

        scheduler.runAtEntityLater(damaged, () -> {
            GlowItemFrame glowItemFrame = (GlowItemFrame) damaged;
            if (glowItemFrame.getItem().getType() == Material.AIR) {
                glowItemFrame.setVisible(true);
                if (placed_item_frames_have_glowing_outlines)
                    glowItemFrame.setGlowing(true);
            }
        }, 50, TimeUnit.MILLISECONDS);
    }
}