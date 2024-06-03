package me.xginko.craftableinvisibleitemframes;

import com.tcoded.folialib.FoliaLib;
import me.xginko.craftableinvisibleitemframes.commands.iframe.IFrameCommand;
import me.xginko.craftableinvisibleitemframes.config.Config;
import me.xginko.craftableinvisibleitemframes.config.LanguageCache;
import me.xginko.craftableinvisibleitemframes.models.ReApplyGlowOutlinesTask;
import me.xginko.craftableinvisibleitemframes.modules.PluginModule;
import org.bstats.bukkit.Metrics;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

public final class CraftableInvisibleItemFrames extends JavaPlugin {

    private static CraftableInvisibleItemFrames instance;
    private static FoliaLib foliaLib;
    private static Map<String, LanguageCache> languageCacheMap;
    private static Config config;
    private static Logger logger;
    private static boolean isGlowVariantCompatible;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        foliaLib = new FoliaLib(this);
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
        new IFrameCommand().enable();
        logger.info("Done.");
    }

    public static CraftableInvisibleItemFrames getInstance()  {
        return instance;
    }
    public static Config config() {
        return config;
    }
    public static NamespacedKey getKey(final String key) {
        return new NamespacedKey(instance, key);
    }
    public static FoliaLib getFoliaLib() {
        return foliaLib;
    }
    public static Logger logger() {
        return logger;
    }
    public static LanguageCache getLang(Locale locale) {
        return getLang(locale.toString().toLowerCase());
    }
    public static LanguageCache getLang(CommandSender commandSender) {
        return commandSender instanceof Player ? getLang(((Player) commandSender).locale()) : getLang(config.default_lang);
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
        foliaLib.getImpl().runNextTick(new ReApplyGlowOutlinesTask(
                config.glowsquid_placed_item_frames_have_glowing_outlines,
                config.regular_placed_item_frames_have_glowing_outlines));
    }

    public void reloadConfiguration() {
        config = new Config();
        PluginModule.reloadModules();
        config.saveConfig();
    }

    public void reloadLang() {
        languageCacheMap = new HashMap<>();
        try {
            for (String localeString : getAvailableTranslations()) {
                logger.info("Found language file for " + localeString);
                languageCacheMap.put(localeString, new LanguageCache(localeString));
            }
        } catch (Throwable t) {
            logger.severe("Error loading translation file(s)! - " + t.getLocalizedMessage());
        } finally {
            if (languageCacheMap.isEmpty()) {
                logger.severe("Unable to load translations. Disabling.");
                getServer().getPluginManager().disablePlugin(this);
            } else {
                logger.info("Loaded " + languageCacheMap.size() + " translations");
            }
        }
    }

    private SortedSet<String> getAvailableTranslations() {
        try (final JarFile pluginJar = new JarFile(getFile())) {
            final File langDirectory = new File(getDataFolder() + "/lang");
            Files.createDirectories(langDirectory.toPath());
            final Pattern langPattern = Pattern.compile("([a-z]{1,3}_[a-z]{1,3})(\\.yml)", Pattern.CASE_INSENSITIVE);
            return Stream.concat(pluginJar.stream().map(ZipEntry::getName), Arrays.stream(langDirectory.listFiles()).map(File::getName))
                    .map(langPattern::matcher)
                    .filter(Matcher::find)
                    .map(matcher -> matcher.group(1))
                    .collect(Collectors.toCollection(TreeSet::new));
        } catch (Throwable t) {
            logger.severe("Failed querying for available translations! - " + t.getLocalizedMessage());
            return new TreeSet<>();
        }
    }
}