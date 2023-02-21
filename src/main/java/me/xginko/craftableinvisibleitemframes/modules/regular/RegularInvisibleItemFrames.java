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
import org.bukkit.inventory.*;
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
    private final HashSet<DroppedFrameLocation> droppedRegularFrames = new HashSet<>();
    private final ItemStack template_invisible_regular_item_frame;

    public RegularInvisibleItemFrames() {
        this.plugin = CraftableInvisibleItemFrames.getInstance();
        this.regular_invisible_item_frame_tag = CraftableInvisibleItemFrames.getRegularInvisibleItemFrameTag();
        this.regular_invisible_item_frame_recipe = CraftableInvisibleItemFrames.getRegularInvisibleItemFrameRecipeKey();

        Config config = CraftableInvisibleItemFrames.getConfiguration();
        this.placed_item_frames_have_glowing_outline = config.getBoolean("regular-invisible-itemframes.glowing-outlines", true);
        boolean should_enchant_frame_items = config.getBoolean("regular-invisible-itemframes.enchant-frame-items", true);

        ItemStack invisible_regular_item_frame = new ItemStack(Material.ITEM_FRAME, 1);
        ItemMeta meta = invisible_regular_item_frame.getItemMeta();
        if (should_enchant_frame_items) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
        }
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', CraftableInvisibleItemFrames.getLang(config.default_lang).invisible_item_frame));
        meta.getPersistentDataContainer().set(regular_invisible_item_frame_tag, PersistentDataType.BYTE, (byte) 1);
        invisible_regular_item_frame.setItemMeta(meta);
        this.template_invisible_regular_item_frame = invisible_regular_item_frame;

        // register recipe
        invisible_regular_item_frame.setAmount(8);
        ShapedRecipe invisRecipe = new ShapedRecipe(regular_invisible_item_frame_recipe, invisible_regular_item_frame);
        invisRecipe.shape("FFF", "FPF", "FFF");
        invisRecipe.setIngredient('F', Material.ITEM_FRAME);
        invisRecipe.setIngredient('P', new RecipeChoice.ExactChoice(config.recipe_center_items));
        plugin.getServer().addRecipe(invisRecipe);
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
            itemDisplayName = CraftableInvisibleItemFrames.getLang(CraftableInvisibleItemFrames.getConfiguration().default_lang).invisible_item_frame;
        } else {
            itemDisplayName = CraftableInvisibleItemFrames.getLang(randomNearbyPlayer.locale()).invisible_item_frame;
        }

        Iterator<DroppedFrameLocation> droppedFrameLocationIterator = droppedRegularFrames.iterator();
        while (droppedFrameLocationIterator.hasNext()) {
            DroppedFrameLocation droppedFrameLocation = droppedFrameLocationIterator.next();
            if (droppedFrameLocation.isFrame(item)) {
                ItemStack invisibleRegularItemFrame = getInvisibleRegularItemFrame();
                ItemMeta meta = invisibleRegularItemFrame.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemDisplayName));
                invisibleRegularItemFrame.setItemMeta(meta);
                invisibleRegularItemFrame.setType(Material.ITEM_FRAME);

                event.getEntity().setItemStack(invisibleRegularItemFrame);

                droppedFrameLocation.getRemoval().cancel();
                droppedFrameLocationIterator.remove();
                break;
            }
        }
    }

    private boolean isInvisibleRegularFrameRecipe(Recipe recipe) {
        return recipe instanceof ShapedRecipe shapedRecipe && shapedRecipe.getKey().equals(regular_invisible_item_frame_recipe);
    }

    private ItemStack getInvisibleRegularItemFrame() {
        return template_invisible_regular_item_frame;
    }
}
