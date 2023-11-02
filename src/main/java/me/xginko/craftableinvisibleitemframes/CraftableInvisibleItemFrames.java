package me.xginko.craftableinvisibleitemframes;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.ServerImplementation;
import me.xginko.craftableinvisibleitemframes.commands.iframe.IFrameCommand;
import me.xginko.craftableinvisibleitemframes.config.Config;
import me.xginko.craftableinvisibleitemframes.config.LanguageCache;
import me.xginko.craftableinvisibleitemframes.enums.Keys;
import me.xginko.craftableinvisibleitemframes.modules.PluginModule;
import org.bstats.bukkit.Metrics;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CraftableInvisibleItemFrames extends JavaPlugin {

    private static CraftableInvisibleItemFrames instance;
    private static HashMap<String, LanguageCache> languageCacheMap;
    private static Config config;
    private static Logger logger;
    private static boolean isGlowVariantCompatible;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        new Metrics(this, 17841);

        logger.info("                         ");
        logger.info("           /*\\           ");
        logger.info("      ┏╍╍╍╍╍╍╍╍╍╍╍┓      ");
        logger.info("      ┋           ┋      ");
        logger.info("      ┋           ┋      ");
        logger.info("      ┋           ┋      ");
        logger.info("      ┗╍╍╍╍╍╍╍╍╍╍╍┛      ");
        logger.info("        Craftable        ");
        logger.info("  Invisible Item Frames  ");
        logger.info("                         ");

        try {
            Class.forName("org.bukkit.entity.GlowItemFrame");
            isGlowVariantCompatible = true;
        } catch (ClassNotFoundException e) {
            isGlowVariantCompatible = false;
        }

        logger.info("Loading Language");
        reloadLang();
        logger.info("Loading Config");
        reloadConfiguration();
        logger.info("Registering Commands");
        getCommand("iframe").setExecutor(new IFrameCommand());
        logger.info("Done.");
    }

    public static CraftableInvisibleItemFrames getInstance()  {
        return instance;
    }
    public static Config getConfiguration() {
        return config;
    }
    public static NamespacedKey getKey(final String key) {
        return new NamespacedKey(instance, key);
    }
    public ServerImplementation getCompatibleScheduler() {
        return new FoliaLib(this).getImpl();
    }
    public static Logger getLog() {
        return logger;
    }
    public static LanguageCache getLang(Locale locale) {
        return getLang(locale.toString().toLowerCase());
    }
    public static LanguageCache getLang(CommandSender commandSender) {
        return commandSender instanceof Player player ? getLang(player.locale()) : getLang(config.default_lang);
    }
    public static LanguageCache getLang(String lang) {
        return config.auto_lang ? languageCacheMap.getOrDefault(lang.replace("-", "_"), languageCacheMap.get(config.default_lang.toString().toLowerCase())) : languageCacheMap.get(config.default_lang.toString().toLowerCase());
    }
    public static boolean isGlowVariantCompatible() {
        return isGlowVariantCompatible;
    }

    public void reloadPlugin() {
        reloadLang();
        reloadConfiguration();
        reapplyOutlineSettingsToAllLoadedInvisibleFrames();
    }

    public void reloadConfiguration() {
        config = new Config();
        PluginModule.reloadModules();
        config.saveConfig();
    }

    public void reapplyOutlineSettingsToAllLoadedInvisibleFrames() {
        for (World world : getServer().getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof ItemFrame itemFrame) {
                    if (itemFrame.getPersistentDataContainer().has(Keys.INVISIBLE_GLOW_ITEM_FRAME.key(), PersistentDataType.BYTE)) {
                        if (itemFrame.getItem().getType().equals(Material.AIR) && config.glowsquid_placed_item_frames_have_glowing_outlines) {
                            itemFrame.setGlowing(true);
                            itemFrame.setVisible(true);
                        } else if (!itemFrame.getItem().getType().equals(Material.AIR)) {
                            itemFrame.setGlowing(false);
                            itemFrame.setVisible(false);
                        }
                    } else if (itemFrame.getPersistentDataContainer().has(Keys.INVISIBLE_ITEM_FRAME.key(), PersistentDataType.BYTE)) {
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

    private Set<String> getDefaultLanguageFiles() {
        Set<String> languageFiles = new HashSet<>();
        try (JarFile jarFile = new JarFile(this.getFile())) {
            jarFile.entries().asIterator().forEachRemaining(jarFileEntry -> {
                final String path = jarFileEntry.getName();
                if (path.startsWith("lang/") && path.endsWith(".yml"))
                    languageFiles.add(path);
            });
        } catch (IOException e) {
            logger.severe("Error while getting default language files! - " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return languageFiles;
    }
}