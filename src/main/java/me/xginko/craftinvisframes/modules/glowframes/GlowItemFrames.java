package me.xginko.craftinvisframes.modules.glowframes;

import me.xginko.craftinvisframes.modules.FrameModule;
import me.xginko.craftinvisframes.modules.regular.InvisbleItemFrameRecipe;
import me.xginko.craftinvisframes.modules.regular.InvisibleItemFrame;
import me.xginko.craftinvisframes.utils.BrokenFrameArea;
import me.xginko.craftinvisframes.utils.ExpiringSet;
import me.xginko.craftinvisframes.utils.Keys;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.GlowItemFrame;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.time.Duration;

public class GlowItemFrames extends FrameModule implements Listener {

    private final Component item_display_name;
    private final boolean enchant_item;

    private ExpiringSet<BrokenFrameArea> invisFramesBeingPunched;

    public GlowItemFrames() {
        super("glow-item-frames", true);
        this.item_display_name = plugin.config().getTranslation(configPath + ".item-display-name",
                "<!italic><#8f2deb>Invisible Glow Item Frame");
        this.enchant_item = plugin.config().getBoolean(configPath + ".enchant-item", true);
    }

    @Override
    public void enable() {
        invisFramesBeingPunched = new ExpiringSet<>(Duration.ofSeconds(1));
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
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onCraft(PrepareItemCraftEvent event) {
        if (InvisbleItemFrameRecipe.isInvisFrameRecipe(event.getRecipe())) return;

        boolean foundInvisIframe = false;
        boolean foundGlowIncSac = false;

        for (ItemStack item : event.getInventory().getMatrix()) {
            if (item == null || item.getType() == Material.AIR) continue;

            if (item.getType() == Material.GLOW_INK_SAC) {
                if (foundGlowIncSac) return;
                foundGlowIncSac = true;
                continue;
            }

            if (InvisibleItemFrame.isInvisibleItemFrame(item)) {
                if (foundInvisIframe) return;
                foundInvisIframe = true;
                continue;
            }

            return;
        }

        if (foundInvisIframe && foundGlowIncSac) {
            event.getInventory().setResult(new InvisibleGlowItemFrame(1, item_display_name, enchant_item));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onHangingPlace(HangingPlaceEvent event) {
        if (event.getEntity().getType() != EntityType.GLOW_ITEM_FRAME) return;
        if (!InvisibleGlowItemFrame.isInvisibleGlowItemFrame(event.getItemStack())) return;

        GlowItemFrame glowItemFrame = (GlowItemFrame) event.getEntity();

        if (!plugin.config().glowsquid_placed_item_frames_have_glowing_outlines) {
            glowItemFrame.setVisible(false);
        } else {
            glowItemFrame.setVisible(true);
            glowItemFrame.setGlowing(true);
        }

        glowItemFrame.getPersistentDataContainer().set(Keys.INVISIBLE_GLOW_ITEM_FRAME.get(), PersistentDataType.BYTE, (byte) 1);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onHangingBreak(HangingBreakEvent event) {
        if (event.getEntity().getType() == EntityType.GLOW_ITEM_FRAME
                && event.getEntity().getPersistentDataContainer().has(Keys.INVISIBLE_GLOW_ITEM_FRAME.get())) {
            invisFramesBeingPunched.add(new BrokenFrameArea(event.getEntity().getLocation()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onItemSpawn(ItemSpawnEvent event) {
        if (event.getEntity().getItemStack().getType() != Material.GLOW_ITEM_FRAME) return;

        for (BrokenFrameArea brokenFrameArea : invisFramesBeingPunched) {
            if (brokenFrameArea.contains(event.getEntity())
                    && event.getEntity().getItemStack().getType() == Material.GLOW_ITEM_FRAME) {
                event.getEntity().setItemStack(new InvisibleItemFrame(1, item_display_name, enchant_item));
                invisFramesBeingPunched.remove(brokenFrameArea);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType() != EntityType.GLOW_ITEM_FRAME) return;
        if (!event.getRightClicked().getPersistentDataContainer().has(Keys.INVISIBLE_GLOW_ITEM_FRAME.get())) return;

        event.getRightClicked().getScheduler().execute(plugin, () -> {
            GlowItemFrame glowItemFrame = (GlowItemFrame) event.getRightClicked();
            if (glowItemFrame.getItem().getType() != Material.AIR) {
                glowItemFrame.setGlowing(false);
                glowItemFrame.setVisible(false);
            }
        }, null, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity().getType() != EntityType.GLOW_ITEM_FRAME) return;
        if (!event.getEntity().getPersistentDataContainer().has(Keys.INVISIBLE_GLOW_ITEM_FRAME.get())) return;

        event.getEntity().getScheduler().execute(plugin, () -> {
            GlowItemFrame glowItemFrame = (GlowItemFrame) event.getEntity();
            if (glowItemFrame.getItem().getType() == Material.AIR) {
                glowItemFrame.setVisible(true);
                glowItemFrame.setGlowing(plugin.config().glowsquid_placed_item_frames_have_glowing_outlines);
            }
        }, null, 1L);
    }
}