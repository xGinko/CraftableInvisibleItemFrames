package me.xginko.craftableinvisibleitemframes.modules;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.config.Config;
import me.xginko.craftableinvisibleitemframes.utils.DroppedFrameLocation;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Iterator;

import static me.xginko.craftableinvisibleitemframes.utils.ItemUtils.getGlowsquidInvisibleItemFrame;
import static me.xginko.craftableinvisibleitemframes.utils.ItemUtils.getRandomNearbyPlayer;

public class GlowsquidInvisibleItemFrames implements CraftableInvisibleItemFramesModule, Listener {

    private final CraftableInvisibleItemFrames plugin;
    private final Config config;
    private final HashSet<DroppedFrameLocation> droppedGlowsquidFrames = new HashSet<>();

    protected GlowsquidInvisibleItemFrames() {
        this.plugin = CraftableInvisibleItemFrames.getInstance();
        this.config = CraftableInvisibleItemFrames.getConfiguration();
    }

    @Override
    public void enable() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return config.glowsquid_invisible_itemframes_are_enabled;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    private void onCraft(PrepareItemCraftEvent event) {
        if (CraftableInvisibleItemFrames.isInvisibleRegularFrameRecipe(event.getRecipe())) return;

        if (event.getView().getPlayer() instanceof Player player) {
            if (!player.hasPermission("craftableinvisibleitemframes.craft")) {
                event.getInventory().setResult(null);
            } else {
                boolean foundInvisibleRegularItemFrame = false;
                boolean foundGlowInkSac = false;
                for (ItemStack item : event.getInventory().getMatrix()) {
                    if (item == null || item.getType().equals(Material.AIR)) continue;

                    if (item.getType().equals(Material.GLOW_INK_SAC)) {
                        if (foundGlowInkSac) return;
                        foundGlowInkSac = true;
                        continue;
                    }

                    if (item.getType().equals(Material.ITEM_FRAME)) {
                        if (item.getItemMeta().getPersistentDataContainer().has(CraftableInvisibleItemFrames.getRegularInvisibleItemFrameTag(), PersistentDataType.BYTE)) {
                            if (foundInvisibleRegularItemFrame) return;
                            foundInvisibleRegularItemFrame = true;
                            continue;
                        }
                    }

                    // Item isn't what we're looking for
                    return;
                }

                if (foundInvisibleRegularItemFrame && foundGlowInkSac) {
                    event.getInventory().setResult(getGlowsquidInvisibleItemFrame(1, player.locale()));
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onHangingPlace(HangingPlaceEvent event) {
        Player player = event.getPlayer();
        if (player != null && event.getEntity() instanceof GlowItemFrame glowItemFrame) {
            // Get the frame item that the player placed
            ItemStack itemFrameInHand;
            if (player.getInventory().getItemInMainHand().getType().equals(Material.GLOW_ITEM_FRAME)) {
                itemFrameInHand = player.getInventory().getItemInMainHand();
            } else if (player.getInventory().getItemInOffHand().getType().equals(Material.GLOW_ITEM_FRAME)) {
                itemFrameInHand = player.getInventory().getItemInOffHand();
            } else return;

            // If the frame item has the invisible tag, make the placed item frame invisible
            if (!itemFrameInHand.getItemMeta().getPersistentDataContainer().has(CraftableInvisibleItemFrames.getGlowsquidInvisibleItemFrameTag(), PersistentDataType.BYTE)) return;
            if (!player.hasPermission("craftableinvisibleitemframes.place")) {
                event.setCancelled(true);
                return;
            }
            if (config.glowsquid_placed_item_frames_have_glowing_outlines) {
                glowItemFrame.setVisible(true);
                glowItemFrame.setGlowing(true);
            } else {
                glowItemFrame.setVisible(false);
            }
            glowItemFrame.getPersistentDataContainer().set(CraftableInvisibleItemFrames.getGlowsquidInvisibleItemFrameTag(), PersistentDataType.BYTE, (byte) 1);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onHangingBreak(HangingBreakEvent event) {
        if (event.getEntity() instanceof GlowItemFrame glowItemFrame) {
            if (!glowItemFrame.getPersistentDataContainer().has(CraftableInvisibleItemFrames.getGlowsquidInvisibleItemFrameTag(), PersistentDataType.BYTE)) return;
            // Sets up a bounding box that checks for items near the frame and converts them
            DroppedFrameLocation droppedFrameLocation = new DroppedFrameLocation(glowItemFrame.getLocation());
            droppedGlowsquidFrames.add(droppedFrameLocation);
            droppedFrameLocation.setRemoval((new BukkitRunnable() {
                @Override
                public void run() {
                    droppedGlowsquidFrames.remove(droppedFrameLocation);
                }
            }).runTaskLater(plugin, 20L));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    private void onItemSpawn(ItemSpawnEvent event) {
        Item item = event.getEntity();
        if (!item.getItemStack().getType().equals(Material.GLOW_ITEM_FRAME)) return;

        String itemDisplayName;
        Player randomNearbyPlayer = getRandomNearbyPlayer(item.getLocation());
        if (randomNearbyPlayer == null) {
            itemDisplayName = CraftableInvisibleItemFrames.getLang(config.default_lang).glow_invisible_item_frame;
        } else {
            itemDisplayName = CraftableInvisibleItemFrames.getLang(randomNearbyPlayer.locale()).glow_invisible_item_frame;
        }

        Iterator<DroppedFrameLocation> droppedFrameLocationIterator = droppedGlowsquidFrames.iterator();
        while (droppedFrameLocationIterator.hasNext()) {
            DroppedFrameLocation droppedFrameLocation = droppedFrameLocationIterator.next();
            if (droppedFrameLocation.isFrame(item)) {
                ItemStack invisibleGlowsquidItemFrame = getGlowsquidInvisibleItemFrame(1);
                ItemMeta meta = invisibleGlowsquidItemFrame.getItemMeta();
                meta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', itemDisplayName)));
                invisibleGlowsquidItemFrame.setItemMeta(meta);

                event.getEntity().setItemStack(invisibleGlowsquidItemFrame);

                droppedFrameLocation.getRemoval().cancel();
                droppedFrameLocationIterator.remove();
                break;
            }
        }
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
                        if (config.glowsquid_placed_item_frames_have_glowing_outlines) glowItemFrame.setGlowing(true);
                        glowItemFrame.setVisible(true);
                    }
                }, 1L);
            }
        }
    }
}
