package me.xginko.craftableinvisibleitemframes.modules;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.config.Config;
import me.xginko.craftableinvisibleitemframes.config.LanguageCache;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class TranslateItemStacks implements CraftableInvisibleItemFramesModule, Listener {

    private final HashSet<String> availableItemTranslations = new HashSet<>();
    private final HashSet<Material> frameMaterials = new HashSet<>();

    protected TranslateItemStacks() {
        for (Map.Entry<String, LanguageCache> languageCacheEntry : CraftableInvisibleItemFrames.getLanguageCacheMap().entrySet()) {
            availableItemTranslations.add(ChatColor.translateAlternateColorCodes('&', languageCacheEntry.getValue().invisible_item_frame));
            availableItemTranslations.add(ChatColor.translateAlternateColorCodes('&', languageCacheEntry.getValue().glow_invisible_item_frame));
        }
        // valueOf since we want to keep things backwards compatible
        try {
            frameMaterials.add(Material.valueOf("GLOW_ITEM_FRAME"));
            frameMaterials.add(Material.valueOf("ITEM_FRAME"));
        } catch (IllegalArgumentException ignored) {}
    }

    @Override
    public void enable() {
        CraftableInvisibleItemFrames plugin = CraftableInvisibleItemFrames.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        Config config = CraftableInvisibleItemFrames.getConfiguration();
        return config.auto_lang && config.getBoolean("language.translate-non-renamed-items", false);
    }

    private void translateNameIfNotRenamed(ItemStack itemStack, Locale locale) {
        if (itemStack == null) return;
        if (!frameMaterials.contains(itemStack.getType())) return;

        ItemMeta meta = itemStack.getItemMeta();
        if (meta.getPersistentDataContainer().has(CraftableInvisibleItemFrames.getRegularInvisibleItemFrameTag())) {
            TextComponent translatedDisplayName = Component.text(ChatColor.translateAlternateColorCodes('&', CraftableInvisibleItemFrames.getLang(locale).invisible_item_frame));
            if (availableItemTranslations.contains(meta.getDisplayName()) && !Objects.equals(itemStack.displayName(), translatedDisplayName)) {
                meta.displayName(translatedDisplayName);
                itemStack.setItemMeta(meta);
            }
        } else if (itemStack.getItemMeta().getPersistentDataContainer().has(CraftableInvisibleItemFrames.getGlowsquidInvisibleItemFrameTag())) {
            TextComponent translatedDisplayName = Component.text(ChatColor.translateAlternateColorCodes('&', CraftableInvisibleItemFrames.getLang(locale).glow_invisible_item_frame));
            if (availableItemTranslations.contains(meta.getDisplayName()) && !Objects.equals(itemStack.displayName(), translatedDisplayName)) {
                meta.displayName(translatedDisplayName);
                itemStack.setItemMeta(meta);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            translateNameIfNotRenamed(event.getCurrentItem(), player.locale());
            translateNameIfNotRenamed(event.getCursor(), player.locale());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            translateNameIfNotRenamed(event.getItem().getItemStack(), player.locale());
        }
    }
}
