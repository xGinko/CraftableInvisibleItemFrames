package me.xginko.craftableinvisibleitemframes;

import me.xginko.craftableinvisibleitemframes.commands.CraftableInvisibleItemFramesCommand;
import me.xginko.craftableinvisibleitemframes.config.Config;
import me.xginko.craftableinvisibleitemframes.config.LanguageCache;
import me.xginko.craftableinvisibleitemframes.modules.CraftableInvisibleItemFramesModule;
import org.bstats.bukkit.Metrics;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
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
    private static HashMap<String, LanguageCache> languageCacheMap;
    private static NamespacedKey invisible_item_frame_tag, invisible_item_frame_recipe;

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
        // Returns 12, if minecraft version is 1.12.x, 19 if version is 1.19.x, and so on.
        Pattern MINECRAFT_VERSION_MATCHER = Pattern.compile("\\(MC: \\d\\.(\\d+)(?:\\.\\d+)?\\)");
        Matcher regexMatcher = MINECRAFT_VERSION_MATCHER.matcher(getServer().getVersion());
        if (!regexMatcher.find()) {
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
            logger.severe("#              value would make it unstable.               #");
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
            String version = regexMatcher.group(1);
            minorMCVersion = Integer.parseInt(version);
            logger.info("Detected Version 1." + minorMCVersion);
            if (minorMCVersion < 13) {
                logger.warning("##########################################################");
                logger.warning("#                                                        #");
                logger.warning("#                         WARNING                        #");
                logger.warning("#                                                        #");
                logger.warning("#    This plugin was made for Minecraft Versions 1.13    #");
                logger.warning("#                         and up.                        #");
                logger.warning("#                                                        #");
                logger.warning("##########################################################");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }

        invisible_item_frame_tag = new NamespacedKey(this, "invisible");
        invisible_item_frame_recipe = new NamespacedKey(this, "invisible-recipe");

        logger.info("Loading Config");
        reloadConfiguration();

        logger.info("Registering Commands");
        reloadCommands();

        logger.info("Loading metrics");
        new Metrics(this, 00000000);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static ItemStack generateInvisibleItemFrame() {
        ItemStack item = new ItemStack(Material.ITEM_FRAME, 1);
        ItemMeta meta = item.getItemMeta();
        if (config.should_enchant_frame_items) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
        }
        meta.getPersistentDataContainer().set(invisible_item_frame_tag, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isInvisibleItemFrameRecipe(Recipe recipe) {
        if (recipe instanceof ShapedRecipe shapedRecipe) {
            return shapedRecipe.getKey().equals(invisible_item_frame_recipe);
        }
        return false;
    }

    public void reloadCommands() {
        CraftableInvisibleItemFramesCommand.reloadCommands();
    }

    public void reloadConfiguration() {
        config = new Config();
        CraftableInvisibleItemFramesModule.reloadModules();
        config.saveConfig();
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

    public static NamespacedKey getInvisibleItemFrameTag() {
        return invisible_item_frame_tag;
    }

    public static NamespacedKey getInvisibleItemFrameRecipe() {
        return invisible_item_frame_recipe;
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
}
