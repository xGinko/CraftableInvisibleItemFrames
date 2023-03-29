package me.xginko.craftableinvisibleitemframes.modules.regular;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.config.Config;
import me.xginko.craftableinvisibleitemframes.modules.CraftableInvisibleItemFramesModule;
import me.xginko.craftableinvisibleitemframes.utils.DroppedFrameLocation;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Iterator;

import static me.xginko.craftableinvisibleitemframes.utils.ItemUtils.getRegularInvisibleItemFrame;
import static me.xginko.craftableinvisibleitemframes.utils.ItemUtils.getRandomNearbyPlayer;

public class RegularInvisibleItemFrames implements CraftableInvisibleItemFramesModule, Listener {

    private final CraftableInvisibleItemFrames plugin;
    private final Config config;
    private final HashSet<DroppedFrameLocation> droppedRegularFrames = new HashSet<>();

    public RegularInvisibleItemFrames() {
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
            // Get the frame item that the player placed
            ItemStack itemFrameInHand;
            if (player.getInventory().getItemInMainHand().getType().equals(Material.ITEM_FRAME)) {
                itemFrameInHand = player.getInventory().getItemInMainHand();
            } else if (player.getInventory().getItemInOffHand().getType().equals(Material.ITEM_FRAME)) {
                itemFrameInHand = player.getInventory().getItemInOffHand();
            } else return;

            // If the frame item has the invisible tag, make the placed item frame invisible
            if (!itemFrameInHand.getItemMeta().getPersistentDataContainer().has(CraftableInvisibleItemFrames.getRegularInvisibleItemFrameTag(), PersistentDataType.BYTE)) return;
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
        Item item = event.getEntity();
        if (!item.getItemStack().getType().equals(Material.ITEM_FRAME)) return;

        String itemDisplayName;
        Player randomNearbyPlayer = getRandomNearbyPlayer(item.getLocation());
        if (randomNearbyPlayer == null) {
            itemDisplayName = CraftableInvisibleItemFrames.getLang(config.default_lang).invisible_item_frame;
        } else {
            itemDisplayName = CraftableInvisibleItemFrames.getLang(randomNearbyPlayer.locale()).invisible_item_frame;
        }

        Iterator<DroppedFrameLocation> droppedFrameLocationIterator = droppedRegularFrames.iterator();
        while (droppedFrameLocationIterator.hasNext()) {
            DroppedFrameLocation droppedFrameLocation = droppedFrameLocationIterator.next();
            if (droppedFrameLocation.isFrame(item)) {
                ItemStack invisibleRegularItemFrame = getRegularInvisibleItemFrame(1);
                ItemMeta meta = invisibleRegularItemFrame.getItemMeta();
                meta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', itemDisplayName)));
                invisibleRegularItemFrame.setItemMeta(meta);

                event.getEntity().setItemStack(invisibleRegularItemFrame);

                droppedFrameLocation.getRemoval().cancel();
                droppedFrameLocationIterator.remove();
                break;
            }
        }
    }
    
}
