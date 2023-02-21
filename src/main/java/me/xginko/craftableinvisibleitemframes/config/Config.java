package me.xginko.craftableinvisibleitemframes.config;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Config {

    private ConfigFile config;
    private final File configFile;
    private final Logger logger;

    public final Locale default_lang;
    public final boolean auto_lang, regular_invisible_itemframes_are_enabled, regular_placed_item_frames_have_glowing_outlines, regular_item_frames_should_be_enchanted,
                glowsquid_invisible_itemframes_are_enabled, glowsquid_placed_item_frames_have_glowing_outlines, glowsquid_item_frames_should_be_enchanted;
    public final double config_version;
    public final HashSet<ItemStack> recipe_center_items = new HashSet<>();

    public Config() {
        configFile = new File(CraftableInvisibleItemFrames.getInstance().getDataFolder(), "config.yml");
        logger = CraftableInvisibleItemFrames.getLog();
        createFiles();
        loadConfig();

        // Config Version - This has no function yet but can be used to parse older configs to new versions in the future.
        this.config_version = getDouble("config-version", 1.0);

        // Language Settings
        config.addSection("Language");
        config.addDefault("language", null); // force config order of language
        this.default_lang = Locale.forLanguageTag(getString("language.default-language", "en_us", "The default language that will be used if auto-language is false or no matching language file was found.").replace("_", "-"));
        this.auto_lang = getBoolean("language.auto-language", true, "If set to true, will display messages based on client language");

        // Regular invis iframes
        config.addSection("Regular Invisible Itemframes");
        this.regular_invisible_itemframes_are_enabled = config.getBoolean("regular-invisible-itemframes.enabled", true);
        this.regular_placed_item_frames_have_glowing_outlines = config.getBoolean("regular-invisible-itemframes.glowing-outlines", true);
        this.regular_item_frames_should_be_enchanted = config.getBoolean("regular-invisible-itemframes.enchant-frame-items", true);

        // Glowsquid invis iframes
        config.addSection("Glowsquid Invisible Itemframes");
        boolean can_do_glowsquid_frames = config.getBoolean("glowsquid-invisible-itemframes.enabled", true);
        if (CraftableInvisibleItemFrames.getMCVersion() < 17) can_do_glowsquid_frames = false;
        this.glowsquid_invisible_itemframes_are_enabled = can_do_glowsquid_frames;
        this.glowsquid_placed_item_frames_have_glowing_outlines = config.getBoolean("glowsquid-invisible-itemframes.glowing-outlines", true);
        this.glowsquid_item_frames_should_be_enchanted = config.getBoolean("glowsquid-invisible-itemframes.enchant-frame-items", true);

        // Recipe center items
        config.addSection("Recipe Center Items");
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
        this.recipe_center_items.addAll(
                config.getList("recipe-center-items", defaults.stream().distinct().collect(Collectors.toList()))
        );
    }

    private void createFiles() {
        try {
            File parent = new File(configFile.getParent());
            if (!parent.exists()) {
                if (!parent.mkdir()) logger.severe("Unable to create plugin config directory.");
            }
            if (!configFile.exists()) {
                configFile.createNewFile(); // Result can be ignored because this method only returns false if the file already exists,
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadConfig() {
        try {
            config = ConfigFile.loadConfig(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            config.save();
        } catch (IOException e) {
            logger.severe("Failed to save config file! - " + e.getLocalizedMessage());
        }
    }

    public void addToRecipeCenterItems(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().equals(Material.AIR)) return;
        recipe_center_items.add(itemStack);
        config.set("recipe-center-items", recipe_center_items.stream().distinct().collect(Collectors.toList()));
        saveConfig();
    }

    public void removeFromRecipeCenterItems(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().equals(Material.AIR)) return;
        recipe_center_items.remove(itemStack);
        config.set("recipe-center-items", recipe_center_items.stream().distinct().collect(Collectors.toList()));
        saveConfig();
    }

    public boolean getBoolean(String path, boolean def, String comment) {
        config.addDefault(path, def, comment);
        return config.getBoolean(path, def);
    }

    public boolean getBoolean(String path, boolean def) {
        config.addDefault(path, def);
        return config.getBoolean(path, def);
    }

    public String getString(String path, String def, String comment) {
        config.addDefault(path, def, comment);
        return config.getString(path, def);
    }

    public String getString(String path, String def) {
        config.addDefault(path, def);
        return config.getString(path, def);
    }

    public double getDouble(String path, Double def, String comment) {
        config.addDefault(path, def, comment);
        return config.getDouble(path, def);
    }

    public double getDouble(String path, Double def) {
        config.addDefault(path, def);
        return config.getDouble(path, def);
    }

    public int getInt(String path, int def, String comment) {
        config.addDefault(path, def, comment);
        return config.getInteger(path, def);
    }

    public int getInt(String path, int def) {
        config.addDefault(path, def);
        return config.getInteger(path, def);
    }

    public List<String> getList(String path, List<String> def, String comment) {
        config.addDefault(path, def, comment);
        return config.getStringList(path);
    }

    public List<String> getList(String path, List<String> def) {
        config.addDefault(path, def);
        return config.getStringList(path);
    }

    public ConfigSection getConfigSection(String path, Map<String, Object> defaultKeyValue) {
        config.makeSectionLenient(path);
        config.addDefault(path, defaultKeyValue);
        return config.getConfigSection(path);
    }

    public ConfigSection getConfigSection(String path, Map<String, Object> defaultKeyValue, String comment) {
        config.makeSectionLenient(path);
        config.addDefault(path, defaultKeyValue, comment);
        return config.getConfigSection(path);
    }

    public void addComment(String path, String comment) {
        config.addComment(path, comment);
    }
}
