package me.xginko.craftableinvisibleitemframes.config;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Config {

    private CraftableInvisibleItemFrames plugin;
    private FileConfiguration config;
    private final File configPath;
    private final Logger logger;

    public final Locale default_lang;
    public final boolean auto_lang, regular_invisible_itemframes_are_enabled, regular_placed_item_frames_have_glowing_outlines, regular_item_frames_should_be_enchanted,
                glowsquid_invisible_itemframes_are_enabled, glowsquid_placed_item_frames_have_glowing_outlines, glowsquid_item_frames_should_be_enchanted;
    public final double config_version;
    public final List<ItemStack> recipe_center_items = new ArrayList<>();

    public Config() {
        this.plugin = CraftableInvisibleItemFrames.getInstance();
        plugin.reloadConfig();
        config = plugin.getConfig();
        configPath = new File(plugin.getDataFolder(), "config.yml");
        logger = CraftableInvisibleItemFrames.getLog();

        // Config Version - This has no function yet but can be used to parse older configs to new versions in the future.
        this.config_version = getDouble("config-version", 1.0);

        // Language Settings
        this.default_lang = Locale.forLanguageTag(getString("language.default-language", "en_us").replace("_", "-"));
        this.auto_lang = getBoolean("language.auto-language", true);

        // Regular invis iframes
        this.regular_invisible_itemframes_are_enabled = getBoolean("regular-invisible-itemframes.enabled", true);
        this.regular_placed_item_frames_have_glowing_outlines = getBoolean("regular-invisible-itemframes.glowing-outlines", true);
        this.regular_item_frames_should_be_enchanted = getBoolean("regular-invisible-itemframes.enchant-frame-items", true);

        // Glowsquid invis iframes
        boolean can_do_glowsquid_frames = getBoolean("glowsquid-invisible-itemframes.enabled", true);
        if (CraftableInvisibleItemFrames.getMCVersion() < 17) can_do_glowsquid_frames = false;
        this.glowsquid_invisible_itemframes_are_enabled = can_do_glowsquid_frames;
        this.glowsquid_placed_item_frames_have_glowing_outlines = getBoolean("glowsquid-invisible-itemframes.glowing-outlines", true);
        this.glowsquid_item_frames_should_be_enchanted = getBoolean("glowsquid-invisible-itemframes.enchant-frame-items", true);

        // Recipe center items
        List<ItemStack> defaults = new ArrayList<>();
        // Short invis
        ItemStack short_lingering_invisibility = new ItemStack(Material.LINGERING_POTION, 1);
        PotionMeta short_lingering_invis_meta = (PotionMeta) short_lingering_invisibility.getItemMeta();
        short_lingering_invis_meta.setBasePotionData(new PotionData(PotionType.INVISIBILITY, false, false));
        short_lingering_invisibility.setItemMeta(short_lingering_invis_meta);
        defaults.add(short_lingering_invisibility);
        // Long invis
        ItemStack long_lingering_invisibility = new ItemStack(Material.LINGERING_POTION, 1);
        PotionMeta long_lingering_invis_meta = (PotionMeta) long_lingering_invisibility.getItemMeta();
        long_lingering_invis_meta.setBasePotionData(new PotionData(PotionType.INVISIBILITY, true, false));
        long_lingering_invisibility.setItemMeta(long_lingering_invis_meta);
        defaults.add(long_lingering_invisibility);
        // config.set("recipe-center-items", defaults.stream().distinct().collect(Collectors.toList()));

        this.recipe_center_items.addAll(
                getItemStackList("recipe-center-items", defaults)
        );
    }

    public void saveConfig() {
        try {
            config.save(configPath);
            config = CraftableInvisibleItemFrames.getInstance().getConfig();
        } catch (IOException e) {
            logger.severe("Failed to save configuration file! - " + e.getLocalizedMessage());
        }
    }

    public boolean getBoolean(String path, boolean def) {
        if (config.isSet(path)) return config.getBoolean(path, def);
        config.set(path, def);
        return def;
    }

    public String getString(String path, String def) {
        if (config.isSet(path)) return config.getString(path, def);
        config.set(path, def);
        return def;
    }

    public double getDouble(String path, double def) {
        if (config.isSet(path)) return config.getDouble(path, def);
        config.set(path, def);
        return def;
    }

    public List<ItemStack> getItemStackList(String path, List<ItemStack> def) {
        if (config.isSet(path)) return (List<ItemStack>) config.getList(path);
        config.set(path, def);
        return def;
    }

    public void addToRecipeCenterItems(ItemStack item) {
        recipe_center_items.add(item);
        setRecipeCenterItems(recipe_center_items);
    }

    public void removeFromRecipeCenterItems(ItemStack item) {
        recipe_center_items.remove(item);
        setRecipeCenterItems(recipe_center_items);
    }

    private void setRecipeCenterItems(List<ItemStack> recipeItems) {
        config.set("recipe-center-items", recipeItems.stream().distinct().collect(Collectors.toList()));
        plugin.saveConfig();
        plugin.reloadPlugin();
    }
}
