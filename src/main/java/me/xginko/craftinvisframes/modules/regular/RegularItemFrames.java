package me.xginko.craftinvisframes.modules.regular;

import me.xginko.craftinvisframes.modules.FrameModule;
import me.xginko.craftinvisframes.utils.BrokenFrameArea;
import me.xginko.craftinvisframes.utils.ExpiringSet;
import me.xginko.craftinvisframes.utils.Keys;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Recipe;
import org.bukkit.persistence.PersistentDataType;

import java.time.Duration;
import java.util.Iterator;

public class RegularItemFrames extends FrameModule implements Listener {

    private final Component item_display_name;
    private final boolean enchant_item;

    private ExpiringSet<BrokenFrameArea> invisFramesBeingPunched;

    public RegularItemFrames() {
        super("regular-item-frames", true);
        this.item_display_name = plugin.config().getTranslation(configPath + ".item-display-name",
                "<!italic><#8f2deb>Invisible Item Frame");
        this.enchant_item = plugin.config().getBoolean(configPath + ".enchant-item", true);
    }

    @Override
    public void enable() {
        invisFramesBeingPunched = new ExpiringSet<>(Duration.ofSeconds(1));
        plugin.getServer().getGlobalRegionScheduler().execute(plugin, () -> plugin.getServer().addRecipe(
                new InvisbleItemFrameRecipe(new InvisibleItemFrame(8, item_display_name, enchant_item)), true));
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
        if (invisFramesBeingPunched != null) {
            invisFramesBeingPunched.clear();
            invisFramesBeingPunched.cleanUp();
            invisFramesBeingPunched = null;
        }
        if (plugin.isEnabled()) plugin.getServer().getGlobalRegionScheduler().execute(plugin, () -> {
            Iterator<Recipe> recipeIterator = plugin.getServer().recipeIterator();
            while (recipeIterator.hasNext()) {
                if (InvisbleItemFrameRecipe.isInvisFrameRecipe(recipeIterator.next())) {
                    recipeIterator.remove();
                    return;
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onHangingPlace(HangingPlaceEvent event) {
        if (event.getEntity().getType() != EntityType.ITEM_FRAME) return;
        if (!InvisibleItemFrame.isInvisibleItemFrame(event.getItemStack())) return;

        ItemFrame itemFrame = (ItemFrame) event.getEntity();

        if (!plugin.config().regular_placed_item_frames_have_glowing_outlines) {
            itemFrame.setVisible(false);
        } else {
            itemFrame.setVisible(true);
            itemFrame.setGlowing(true);
        }

        itemFrame.getPersistentDataContainer().set(Keys.INVISIBLE_ITEM_FRAME.get(), PersistentDataType.BYTE, (byte) 1);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onHangingBreak(HangingBreakEvent event) {
        if (event.getEntity().getType() == EntityType.ITEM_FRAME
                && event.getEntity().getPersistentDataContainer().has(Keys.INVISIBLE_ITEM_FRAME.get())) {
            invisFramesBeingPunched.add(new BrokenFrameArea(event.getEntity().getLocation()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onItemSpawn(ItemSpawnEvent event) {
        if (event.getEntity().getItemStack().getType() != Material.ITEM_FRAME) return;

        for (BrokenFrameArea brokenFrameArea : invisFramesBeingPunched) {
            if (brokenFrameArea.contains(event.getEntity())
                    && event.getEntity().getItemStack().getType() == Material.ITEM_FRAME) {
                event.getEntity().setItemStack(new InvisibleItemFrame(1, item_display_name, enchant_item));
                invisFramesBeingPunched.remove(brokenFrameArea);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType() != EntityType.ITEM_FRAME) return;
        if (!event.getRightClicked().getPersistentDataContainer().has(Keys.INVISIBLE_ITEM_FRAME.get())) return;

        event.getRightClicked().getScheduler().execute(plugin, () -> {
            ItemFrame itemFrame = (ItemFrame) event.getRightClicked();
            if (itemFrame.getItem().getType() != Material.AIR) {
                itemFrame.setGlowing(false);
                itemFrame.setVisible(false);
            }
        }, null, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity().getType() != EntityType.ITEM_FRAME) return;
        if (!event.getEntity().getPersistentDataContainer().has(Keys.INVISIBLE_ITEM_FRAME.get())) return;

        event.getEntity().getScheduler().execute(plugin, () -> {
            ItemFrame itemFrame = (ItemFrame) event.getEntity();
            if (itemFrame.getItem().getType() == Material.AIR) {
                itemFrame.setVisible(true);
                itemFrame.setGlowing(plugin.config().regular_placed_item_frames_have_glowing_outlines);
            }
        }, null, 1L);
    }
}