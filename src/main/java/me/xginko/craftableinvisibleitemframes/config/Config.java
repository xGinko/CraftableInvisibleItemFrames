package me.xginko.craftableinvisibleitemframes.config;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.enums.Keys;
import me.xginko.craftableinvisibleitemframes.models.InvisibleItemFrame;
import me.xginko.craftableinvisibleitemframes.utils.CommonUtil;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Config {

    private final CraftableInvisibleItemFrames plugin;
    private FileConfiguration config;
    private final File configPath;
    public final List<ItemStack> recipe_center_items;
    public final Locale default_lang;
    public final boolean auto_lang, regular_invisible_itemframes_are_enabled, regular_placed_item_frames_have_glowing_outlines,
            regular_item_frames_should_be_enchanted, glowsquid_invisible_itemframes_are_enabled,
            glowsquid_placed_item_frames_have_glowing_outlines, glowsquid_item_frames_should_be_enchanted;

    public Config() {
        this.plugin = CraftableInvisibleItemFrames.getInstance();
        plugin.reloadConfig();
        config = plugin.getConfig();
        configPath = new File(plugin.getDataFolder(), "config.yml");

        // Language Settings
        this.default_lang = Locale.forLanguageTag(getString("language.default-language", "en_us").replace("_", "-"));
        this.auto_lang = getBoolean("language.auto-language", true);

        // Regular invis iframes
        this.regular_invisible_itemframes_are_enabled = getBoolean("regular-invisible-item-frames.enabled", true);
        this.regular_placed_item_frames_have_glowing_outlines = getBoolean("regular-invisible-item-frames.glowing-outlines", true);
        this.regular_item_frames_should_be_enchanted = getBoolean("regular-invisible-item-frames.enchant-frame-items", true);

        // Glowsquid invis iframes
        this.glowsquid_invisible_itemframes_are_enabled = getBoolean("invisible-glow-item-frames.enabled", CraftableInvisibleItemFrames.isGlowVariantCompatible());
        this.glowsquid_placed_item_frames_have_glowing_outlines = getBoolean("invisible-glow-item-frames.glowing-outlines", true);
        this.glowsquid_item_frames_should_be_enchanted = getBoolean("invisible-glow-item-frames.enchant-frame-items", true);

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

        this.recipe_center_items = getItemStackList("recipe-center-items", defaults);
        this.registerRecipe(recipe_center_items);
    }

    public void saveConfig() {
        try {
            config.save(configPath);
            config = CraftableInvisibleItemFrames.getInstance().getConfig();
        } catch (IOException e) {
            CraftableInvisibleItemFrames.getLog().severe("Failed to save configuration file! - " + e.getLocalizedMessage());
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

    private void setRecipeCenterItems(List<ItemStack> centerItems) {
        config.set("recipe-center-items", centerItems.stream().distinct().collect(Collectors.toList()));
        plugin.saveConfig();
        unregisterRecipe();
        registerRecipe(centerItems);
    }

    public void registerRecipe(List<ItemStack> centerItems) {
        CraftableInvisibleItemFrames plugin = CraftableInvisibleItemFrames.getInstance();
        plugin.getCompatibleScheduler().runNextTick(addRecipe -> {
            plugin.getServer().addRecipe(new ShapedRecipe(Keys.INVISIBLE_ITEM_FRAME_RECIPE.key(), new InvisibleItemFrame(8))
                    .shape("FFF", "FPF", "FFF")
                    .setIngredient('F', Material.ITEM_FRAME)
                    .setIngredient('P', new RecipeChoice.ExactChoice(centerItems))
            );
        });
    }

    public void unregisterRecipe() {
        CraftableInvisibleItemFrames plugin = CraftableInvisibleItemFrames.getInstance();
        plugin.getCompatibleScheduler().runNextTick(removeRecipe -> {
            Iterator<Recipe> recipeIterator = plugin.getServer().recipeIterator();
            while (recipeIterator.hasNext()) {
                if (CommonUtil.isInvisibleItemFrameRecipe(recipeIterator.next())) {
                    recipeIterator.remove();
                    return;
                }
            }
        });
    }
}