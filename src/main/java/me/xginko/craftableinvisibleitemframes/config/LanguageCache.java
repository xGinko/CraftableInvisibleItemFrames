package me.xginko.craftableinvisibleitemframes.config;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class LanguageCache {

    private final FileConfiguration fileConfiguration;
    boolean addedMissing = false;
    public Component no_permission, invisible_item_frame, glow_invisible_item_frame;

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

            this.no_permission = getTranslation("no-permission", "<red>You don't have permission to use this command.");
            this.invisible_item_frame = getTranslation("invisible-itemframe", "<white>Invisible Item Frame");
            this.glow_invisible_item_frame = getTranslation("glow-invisible-itemframe", "<white>Glow Invisible Item Frame");

            if (addedMissing) fileConfiguration.save(langFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            CraftableInvisibleItemFrames.logger().warning("Translation file " + langFile + " is not formatted properly. Skipping it.");
        }
    }

    public Component getTranslation(String path, String defaultTranslation) {
        String translation = fileConfiguration.getString(path);
        if (translation == null) {
            fileConfiguration.set(path, defaultTranslation);
            addedMissing = true;
            return MiniMessage.miniMessage().deserialize(defaultTranslation);
        }
        return MiniMessage.miniMessage().deserialize(translation);
    }
}
