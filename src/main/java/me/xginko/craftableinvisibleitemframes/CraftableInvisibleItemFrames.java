package me.xginko.craftableinvisibleitemframes;

import me.xginko.craftableinvisibleitemframes.commands.CraftableInvisibleItemFramesCommand;
import me.xginko.craftableinvisibleitemframes.config.Config;
import me.xginko.craftableinvisibleitemframes.config.LanguageCache;
import me.xginko.craftableinvisibleitemframes.modules.CraftableInvisibleItemFramesModule;
import net.kyori.adventure.text.Component;
import org.bstats.bukkit.Metrics;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CraftableInvisibleItemFrames extends JavaPlugin {

    private static CraftableInvisibleItemFrames instance;
    private static Logger logger;
    private static Config config;
    private static int minorMCVersion = Integer.MIN_VALUE;
    private static NamespacedKey TAG_regular_invisible_item_frame, RECIPE_regular_invisible_item_frame, TAG_glowsquid_invisible_item_frame;
    private static HashMap<String, LanguageCache> languageCacheMap;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();

        // Fancy enable
        logger.info("                                                    ");
        logger.info("           Craftable Invisible Itemframes           ");
        logger.info("                   Made by xGinko                   ");
        logger.info("                                                    ");

        // Detect Minecraft version.
        Matcher versionMatcher = Pattern.compile("\\(MC: \\d\\.(\\d+)(?:\\.\\d+)?\\)").matcher(getServer().getVersion());
        if (!versionMatcher.find()) {
            logger.severe("############################################################");
            logger.severe("#                                                          #");
            logger.severe("#                          ERROR                           #");
            logger.severe("#                                                          #");
            logger.severe("#   Report this along with your server type and version    #");
            logger.severe("#                  to the plugins github:                  #");
            logger.severe("#  https://github.com/xGinko/CraftableInvisibleItemFrames  #");
            logger.severe("#                                                          #");
            logger.severe("#  An error occurred while trying to determine the game's  #");
            logger.severe("#  version. Because some features of this plugin rely on   #");
            logger.severe("#    on knowing the version, an error while parsing the    #");
            logger.severe("#              value could make it unstable.               #");
            logger.severe("#       For this reason the plugin will disable now.       #");
            logger.severe("#                                                          #");
            logger.severe("############################################################");
            logger.severe("                                                          ");
            logger.severe("  Server name:                                            ");
            logger.severe("  '"+getServer().getName()+"'                             ");
            logger.severe("  Server version:                                         ");
            logger.severe("  '"+getServer().getVersion()+"'                          ");
            logger.severe("                                                          ");
            getServer().getPluginManager().disablePlugin(this);
            return;
        } else {
            minorMCVersion = Integer.parseInt(versionMatcher.group(1));
            logger.info("Detected Version 1." + minorMCVersion);
            if (minorMCVersion < 16) {
                logger.warning("##########################################################");
                logger.warning("#                                                        #");
                logger.warning("#                         WARNING                        #");
                logger.warning("#                                                        #");
                logger.warning("#    This plugin was made for Minecraft Versions 1.16    #");
                logger.warning("#                         and up.                        #");
                logger.warning("#                                                        #");
                logger.warning("##########################################################");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }

        logger.info("Registering Namespaced Keys");
        TAG_regular_invisible_item_frame = new NamespacedKey(this, "invisible-itemframe");
        RECIPE_regular_invisible_item_frame = new NamespacedKey(this, "invisible-itemframe-recipe");
        TAG_glowsquid_invisible_item_frame = new NamespacedKey(this, "invisible-glowsquid-itemframe");

        logger.info("Loading Language");
        reloadLang();

        logger.info("Loading Config");
        reloadConfiguration();

        logger.info("Registering Recipe");
        reloadRecipe();

        logger.info("Registering Commands");
        CraftableInvisibleItemFramesCommand.reloadCommands();

        logger.info("Loading Metrics");
        new Metrics(this, 17841);
    }

    @Override
    public void onDisable() {
        removeRecipe(RECIPE_regular_invisible_item_frame);
    }

    public void removeRecipe(NamespacedKey recipeKey) {
        Iterator<Recipe> recipeIterator = getServer().recipeIterator();
        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();
            if (recipe instanceof ShapedRecipe shapedRecipe) {
                if (shapedRecipe.getKey().equals(recipeKey)) {
                    recipeIterator.remove();
                    break;
                }
            }
        }
    }

    public void reapplyOutlineSettingsToAllLoadedInvisibleFrames() {
        for (World world : getServer().getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof ItemFrame itemFrame) {
                    if (itemFrame.getPersistentDataContainer().has(TAG_glowsquid_invisible_item_frame)) {
                        if (itemFrame.getItem().getType().equals(Material.AIR) && config.glowsquid_placed_item_frames_have_glowing_outlines) {
                            itemFrame.setGlowing(true);
                            itemFrame.setVisible(true);
                        } else if (!itemFrame.getItem().getType().equals(Material.AIR)) {
                            itemFrame.setGlowing(false);
                            itemFrame.setVisible(false);
                        }
                    } else if (itemFrame.getPersistentDataContainer().has(TAG_regular_invisible_item_frame)) {
                        if (itemFrame.getItem().getType().equals(Material.AIR) && config.regular_placed_item_frames_have_glowing_outlines) {
                            itemFrame.setGlowing(true);
                            itemFrame.setVisible(true);
                        } else if (!itemFrame.getItem().getType().equals(Material.AIR)) {
                            itemFrame.setGlowing(false);
                            itemFrame.setVisible(false);
                        }
                    }
                }
            }
        }
    }

    public void reloadRecipe() {
        removeRecipe(RECIPE_regular_invisible_item_frame);
        ItemStack invisible_regular_item_frame = new ItemStack(Material.ITEM_FRAME, 1);
        ItemMeta meta = invisible_regular_item_frame.getItemMeta();
        if (config.regular_item_frames_should_be_enchanted) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        }
        meta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', getLang(config.default_lang).invisible_item_frame)));
        meta.getPersistentDataContainer().set(TAG_regular_invisible_item_frame, PersistentDataType.BYTE, (byte) 1);
        invisible_regular_item_frame.setItemMeta(meta);
        invisible_regular_item_frame.setAmount(8);
        ShapedRecipe invisRecipe = new ShapedRecipe(RECIPE_regular_invisible_item_frame, invisible_regular_item_frame);
        invisRecipe.shape("FFF", "FPF", "FFF");
        invisRecipe.setIngredient('F', Material.ITEM_FRAME);
        invisRecipe.setIngredient('P', new RecipeChoice.ExactChoice(config.recipe_center_items));
        getServer().addRecipe(invisRecipe);
    }

    public void reloadPlugin() {
        reloadLang();
        reloadConfiguration();
        reloadRecipe();
        CraftableInvisibleItemFramesCommand.reloadCommands();
        reapplyOutlineSettingsToAllLoadedInvisibleFrames();
    }

    public void reloadConfiguration() {
        config = new Config();
        CraftableInvisibleItemFramesModule.reloadModules();
        config.saveConfig();
    }

    public void reloadLang() {
        languageCacheMap = new HashMap<>();
        try {
            File langDirectory = new File(instance.getDataFolder() + File.separator + "lang");
            Files.createDirectories(langDirectory.toPath());
            for (String fileName : getDefaultLanguageFiles()) {
                String localeString = fileName.substring(fileName.lastIndexOf('/') + 1, fileName.lastIndexOf('.'));
                logger.info(String.format("Found language file for %s", localeString));
                LanguageCache langCache = new LanguageCache(localeString);
                languageCacheMap.put(localeString, langCache);
            }
            Pattern langPattern = Pattern.compile("([a-z]{1,3}_[a-z]{1,3})(\\.yml)", Pattern.CASE_INSENSITIVE);
            for (File langFile : langDirectory.listFiles()) {
                Matcher langMatcher = langPattern.matcher(langFile.getName());
                if (langMatcher.find()) {
                    String localeString = langMatcher.group(1).toLowerCase();
                    if(!languageCacheMap.containsKey(localeString)) {
                        logger.info(String.format("Found language file for %s", localeString));
                        LanguageCache langCache = new LanguageCache(localeString);
                        languageCacheMap.put(localeString, langCache);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.severe("Error loading language files! Language files will not reload to avoid errors, make sure to correct this before restarting the server!");
        }
    }

    private Set<String> getDefaultLanguageFiles(){
        Reflections reflections = new Reflections("lang", Scanners.Resources);
        return reflections.getResources(Pattern.compile("([a-z]{1,3}_[a-z]{1,3})(\\.yml)"));
    }

    public static LanguageCache getLang(String lang) {
        lang = lang.replace("-", "_");
        if (config.auto_lang) {
            return languageCacheMap.getOrDefault(lang, languageCacheMap.get(config.default_lang.toString().toLowerCase()));
        } else {
            return languageCacheMap.get(config.default_lang.toString().toLowerCase());
        }
    }

    public static LanguageCache getLang(Locale locale) {
        return getLang(locale.toString().toLowerCase());
    }

    public static LanguageCache getLang(CommandSender commandSender) {
        if (commandSender instanceof Player player) {
            return getLang(player.locale());
        } else {
            return getLang(config.default_lang);
        }
    }

    public static CraftableInvisibleItemFrames getInstance()  {
        return instance;
    }

    public static Config getConfiguration() {
        return config;
    }

    public static Logger getLog() {
        return logger;
    }

    public static int getMCVersion() {
        return minorMCVersion;
    }

    public static NamespacedKey getRegularInvisibleItemFrameTag() {
        return TAG_regular_invisible_item_frame;
    }

    public static boolean isInvisibleRegularFrameRecipe(Recipe recipe) {
        return recipe instanceof ShapedRecipe shapedRecipe && shapedRecipe.getKey().equals(RECIPE_regular_invisible_item_frame);
    }

    public static NamespacedKey getGlowsquidInvisibleItemFrameTag() {
        return TAG_glowsquid_invisible_item_frame;
    }
}
