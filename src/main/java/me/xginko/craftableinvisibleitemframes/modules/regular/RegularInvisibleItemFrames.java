package me.xginko.craftableinvisibleitemframes.modules.regular;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.config.Config;
import me.xginko.craftableinvisibleitemframes.modules.CraftableInvisibleItemFramesModule;
import me.xginko.craftableinvisibleitemframes.utils.DroppedFrameLocation;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Iterator;

import static me.xginko.craftableinvisibleitemframes.utils.Tools.getRandomNearbyPlayer;

public class RegularInvisibleItemFrames implements CraftableInvisibleItemFramesModule, Listener {

    private final CraftableInvisibleItemFrames plugin;
    private final NamespacedKey regular_invisible_item_frame_tag, regular_invisible_item_frame_recipe;
    private final boolean placed_item_frames_have_glowing_outline;
    private final HashSet<DroppedFrameLocation> droppedFrames = new HashSet<>();
    private final ItemStack untitled_invisible_item_frame;

    public RegularInvisibleItemFrames() {
        this.plugin = CraftableInvisibleItemFrames.getInstance();
        Config config = CraftableInvisibleItemFrames.getConfiguration();
        this.placed_item_frames_have_glowing_outline = config.getBoolean("regular-invisible-itemframes.enable-glowing-outline-on-frames", true);

        regular_invisible_item_frame_tag = new NamespacedKey(plugin, "invisible-itemframe");
        regular_invisible_item_frame_recipe = new NamespacedKey(plugin, "invisible-itemframe-recipe");

        ItemStack invisible_item_frame = new ItemStack(Material.ITEM_FRAME, 1);
        ItemMeta meta = invisible_item_frame.getItemMeta();
        if (config.should_enchant_frame_items) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
        }
        meta.getPersistentDataContainer().set(regular_invisible_item_frame_tag, PersistentDataType.BYTE, (byte) 1);
        invisible_item_frame.setItemMeta(meta);
        this.untitled_invisible_item_frame = invisible_item_frame;
    }

    @Override
    public void enable() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return CraftableInvisibleItemFrames.getConfiguration().getBoolean("regular-invisible-itemframes.enabled", true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onHangingPlace(HangingPlaceEvent event) {
        if (event.getPlayer() == null) return;
        if (event.getEntity() instanceof ItemFrame itemFrameEntity) {
            // Get the frame item that the player placed
            ItemStack itemFrameInHand;
            Player player = event.getPlayer();
            if (player.getInventory().getItemInMainHand().getType().equals(Material.ITEM_FRAME)) {
                itemFrameInHand = player.getInventory().getItemInMainHand();
            }
            else if (player.getInventory().getItemInOffHand().getType().equals(Material.ITEM_FRAME)) {
                itemFrameInHand = player.getInventory().getItemInOffHand();
            } else {
                return;
            }

            // If the frame item has the invisible tag, make the placed item frame invisible
            if (!itemFrameInHand.getItemMeta().getPersistentDataContainer().has(regular_invisible_item_frame_tag, PersistentDataType.BYTE)) return;
            if (!player.hasPermission("craftableinvisibleitemframes.place")) {
                event.setCancelled(true);
                return;
            }
            if (placed_item_frames_have_glowing_outline) {
                itemFrameEntity.setVisible(true);
                itemFrameEntity.setGlowing(true);
            } else {
                itemFrameEntity.setVisible(false);
            }
            itemFrameEntity.getPersistentDataContainer().set(regular_invisible_item_frame_tag, PersistentDataType.BYTE, (byte) 1);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onHangingBreak(HangingBreakEvent event) {
        if (event.getEntity() instanceof ItemFrame itemFrame) {
            if (!itemFrame.getPersistentDataContainer().has(regular_invisible_item_frame_tag, PersistentDataType.BYTE)) return;
            // Sets up a bounding box that checks for items near the frame and converts them
            DroppedFrameLocation droppedFrameLocation = new DroppedFrameLocation(itemFrame.getLocation());
            droppedFrames.add(droppedFrameLocation);
            droppedFrameLocation.setRemoval((new BukkitRunnable() {
                @Override
                public void run() {
                    droppedFrames.remove(droppedFrameLocation);
                }
            }).runTaskLater(plugin, 20L));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    private void onItemSpawn(ItemSpawnEvent event) {
        Item item = event.getEntity();
        if(!item.getItemStack().getType().equals(Material.ITEM_FRAME)) return;

        String itemDisplayName;
        Player randomNearbyPlayer = getRandomNearbyPlayer(item.getLocation());
        if (randomNearbyPlayer == null) {
            itemDisplayName = CraftableInvisibleItemFrames.getLang(CraftableInvisibleItemFrames.getConfiguration().default_lang).invisible_item_frame;
        } else {
            itemDisplayName = CraftableInvisibleItemFrames.getLang(randomNearbyPlayer.locale()).invisible_item_frame;
        }

        Iterator<DroppedFrameLocation> iter = droppedFrames.iterator();
        while(iter.hasNext()) {
            DroppedFrameLocation droppedFrameLocation = iter.next();
            if(droppedFrameLocation.isFrame(item)) {
                ItemStack invisibleItemFrame = untitled_invisible_item_frame;
                ItemMeta meta = invisibleItemFrame.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemDisplayName));
                invisibleItemFrame.setItemMeta(meta);
                invisibleItemFrame.setType(Material.ITEM_FRAME);

                event.getEntity().setItemStack(invisibleItemFrame);

                droppedFrameLocation.getRemoval().cancel();
                iter.remove();
                break;
            }
        }
    }
}
