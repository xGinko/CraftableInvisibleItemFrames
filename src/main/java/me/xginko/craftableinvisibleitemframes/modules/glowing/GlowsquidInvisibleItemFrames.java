package me.xginko.craftableinvisibleitemframes.modules.glowing;

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
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Iterator;

import static me.xginko.craftableinvisibleitemframes.utils.Tools.getRandomNearbyPlayer;

public class GlowsquidInvisibleItemFrames implements CraftableInvisibleItemFramesModule, Listener {

    private final CraftableInvisibleItemFrames plugin;
    private final NamespacedKey glowsquid_invisible_item_frame_tag, glowsquid_invisible_item_frame_recipe;
    private final boolean placed_item_frames_have_glowing_outline;
    private final HashSet<DroppedFrameLocation> droppedFrames = new HashSet<>();
    private final ItemStack invisible_glowsquid_item_frame;

    public GlowsquidInvisibleItemFrames() {
        this.plugin = CraftableInvisibleItemFrames.getInstance();
        Config config = CraftableInvisibleItemFrames.getConfiguration();
        this.placed_item_frames_have_glowing_outline = config.getBoolean("glowsquid-invisible-itemframes.enable-glowing-outline-on-frames", true);

        this.glowsquid_invisible_item_frame_tag = CraftableInvisibleItemFrames.getGlowsquidInvisibleItemFrameTag();
        this.glowsquid_invisible_item_frame_recipe = CraftableInvisibleItemFrames.getGlowsquidInvisibleItemFrameRecipeKey();

        ItemStack invisible_glowsquid_frame = new ItemStack(Material.GLOW_ITEM_FRAME, 1);
        ItemMeta meta = invisible_glowsquid_frame.getItemMeta();
        if (config.should_enchant_frame_items) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
        }
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', CraftableInvisibleItemFrames.getLang(config.default_lang).glow_invisible_item_frame));
        meta.getPersistentDataContainer().set(glowsquid_invisible_item_frame_tag, PersistentDataType.BYTE, (byte) 1);
        invisible_glowsquid_frame.setItemMeta(meta);
        this.invisible_glowsquid_item_frame = invisible_glowsquid_frame;

        // register recipe
        invisible_glowsquid_frame.setAmount(8);
        ShapedRecipe invisRecipe = new ShapedRecipe(glowsquid_invisible_item_frame_recipe, invisible_glowsquid_frame);
        invisRecipe.shape("FFF", "FPF", "FFF");
        invisRecipe.setIngredient('F', Material.GLOW_ITEM_FRAME);
        invisRecipe.setIngredient('P', new RecipeChoice.ExactChoice(config.recipe_center_items));
        plugin.getServer().addRecipe(invisRecipe);
    }

    @Override
    public void enable() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return CraftableInvisibleItemFrames.getConfiguration().getBoolean("glowsquid-invisible-itemframes.enabled", true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    private void onCraft(PrepareItemCraftEvent event) {
        if (!isGlowsquidInvisibleRecipe(event.getRecipe())) return;

        Player player = (Player) event.getView().getPlayer();
        if (player.hasPermission("craftableinvisibleitemframes.craft")) {
            boolean foundFrame = false;
            boolean foundInkSac = false;
            for (ItemStack item : event.getInventory().getMatrix()) {
                if (item == null || item.getType().equals(Material.AIR)) continue;

                if(item.getType().equals(Material.GLOW_INK_SAC)) {
                    if (foundInkSac) return;
                    foundInkSac = true;
                    continue;
                }

                if(
                        item.getItemMeta().getPersistentDataContainer().has(glowsquid_invisible_item_frame_tag, PersistentDataType.BYTE)
                        && !item.getType().equals(Material.GLOW_ITEM_FRAME))
                {
                    if (foundFrame) return;
                    foundFrame = true;
                    continue;
                }

                // Item isn't what we're looking for
                return;
            }

            if(foundFrame && foundInkSac) {
                ItemStack invisibleGlowingItem = invisible_glowsquid_item_frame;
                ItemMeta meta = invisibleGlowingItem.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', CraftableInvisibleItemFrames.getLang(player.locale()).glow_invisible_item_frame));
                invisibleGlowingItem.setItemMeta(meta);

                event.getInventory().setResult(invisibleGlowingItem);
            }
        } else {
            event.getInventory().setResult(null);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onHangingPlace(HangingPlaceEvent event) {
        Player player = event.getPlayer();
        if (player != null && event.getEntity() instanceof ItemFrame itemFrameEntity) {
            // Get the frame item that the player placed
            ItemStack itemFrameInHand;
            if (player.getInventory().getItemInMainHand().getType().equals(Material.GLOW_ITEM_FRAME)) {
                itemFrameInHand = player.getInventory().getItemInMainHand();
            }
            else if (player.getInventory().getItemInOffHand().getType().equals(Material.GLOW_ITEM_FRAME)) {
                itemFrameInHand = player.getInventory().getItemInOffHand();
            } else {
                return;
            }

            // If the frame item has the invisible tag, make the placed item frame invisible
            if (!itemFrameInHand.getItemMeta().getPersistentDataContainer().has(glowsquid_invisible_item_frame_tag, PersistentDataType.BYTE)) return;
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
            itemFrameEntity.getPersistentDataContainer().set(glowsquid_invisible_item_frame_tag, PersistentDataType.BYTE, (byte) 1);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onHangingBreak(HangingBreakEvent event) {
        if (event.getEntity() instanceof ItemFrame itemFrame) {
            if (!itemFrame.getPersistentDataContainer().has(glowsquid_invisible_item_frame_tag, PersistentDataType.BYTE)) return;
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
        if(!item.getItemStack().getType().equals(Material.GLOW_ITEM_FRAME)) return;

        String itemDisplayName;
        Player randomNearbyPlayer = getRandomNearbyPlayer(item.getLocation());
        if (randomNearbyPlayer == null) {
            itemDisplayName = CraftableInvisibleItemFrames.getLang(CraftableInvisibleItemFrames.getConfiguration().default_lang).glow_invisible_item_frame;
        } else {
            itemDisplayName = CraftableInvisibleItemFrames.getLang(randomNearbyPlayer.locale()).glow_invisible_item_frame;
        }

        Iterator<DroppedFrameLocation> iterator = droppedFrames.iterator();
        while (iterator.hasNext()) {
            DroppedFrameLocation droppedFrameLocation = iterator.next();
            if(droppedFrameLocation.isFrame(item)) {
                ItemStack invisibleItemFrame = invisible_glowsquid_item_frame;
                ItemMeta meta = invisibleItemFrame.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemDisplayName));
                invisibleItemFrame.setItemMeta(meta);

                event.getEntity().setItemStack(invisibleItemFrame);

                droppedFrameLocation.getRemoval().cancel();
                iterator.remove();
                break;
            }
        }
    }

    private boolean isGlowsquidInvisibleRecipe(Recipe recipe) {
        return recipe instanceof ShapedRecipe shapedRecipe && shapedRecipe.getKey().equals(glowsquid_invisible_item_frame_recipe);
    }
}
