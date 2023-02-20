package me.xginko.craftableinvisibleitemframes.config;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class LanguageCache {

    private final FileConfiguration fileConfiguration;
    boolean addedMissing = false;
    public String noPermission, invisible_item_frame, glow_invisible_item_frame;

    public LanguageCache(String lang) {
        CraftableInvisibleItemFrames plugin = CraftableInvisibleItemFrames.getInstance();
        File langFile = new File(plugin.getDataFolder() + File.separator + "lang", lang + ".yml");
        fileConfiguration = new YamlConfiguration();

        if (!langFile.exists()) {
            langFile.getParentFile().mkdirs();
            plugin.saveResource("lang" + File.separator + lang + ".yml", false);
        }
        try {
            fileConfiguration.load(langFile);

            // No Permission
            this.noPermission = getStringTranslation("no-permission", "&cYou don't have permission to use this command.");
            // Not on the ceiling
            this.invisible_item_frame = getStringTranslation("invisible-itemframe", "&fInvisible Item Frame");
            this.glow_invisible_item_frame = getStringTranslation("glow-invisible-itemframe", "&fGlow Invisible Item Frame");


            if (addedMissing) fileConfiguration.save(langFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            CraftableInvisibleItemFrames.getLog().warning("Translation file " + langFile + " is not formatted properly. Skipping it.");
        }
    }

    public String getStringTranslation(String path, String defaultTranslation) {
        String translation = fileConfiguration.getString(path);
        if (translation == null) {
            fileConfiguration.set(path, defaultTranslation);
            addedMissing = true;
            return defaultTranslation;
        }
        return translation;
    }

}
