package me.xginko.craftinvisframes.config;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import io.github.thatsmusic99.configurationmaster.api.Title;
import me.xginko.craftinvisframes.CraftInvisFrames;
import me.xginko.craftinvisframes.utils.AdventureUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class Config {

    private final ConfigFile config;

    public final boolean regular_placed_item_frames_have_glowing_outlines, glowsquid_placed_item_frames_have_glowing_outlines;

    public Config() throws Exception {
        // Load config.yml with ConfigMaster
        this.config = ConfigFile.loadConfig(new File(CraftInvisFrames.get().getDataFolder(), "config.yml"));

        this.config.setTitle(new Title().withWidth(80)
                .addSolidLine()
                .addLine(" ", Title.Pos.CENTER)
                .addLine(CraftInvisFrames.get().getName(), Title.Pos.CENTER)
                .addLine(" ", Title.Pos.CENTER)
                .addSolidLine());

        this.regular_placed_item_frames_have_glowing_outlines = getBoolean("regular-item-frames.glowing-outlines", true);
        this.glowsquid_placed_item_frames_have_glowing_outlines = getBoolean("glow-item-frames.glowing-outlines", true);
    }

    public void saveConfig() {
        try {
            this.config.save();
        } catch (Exception e) {
            CraftInvisFrames.get().logger().error("Failed to save config file!", e);
        }
    }

    public ConfigFile master() {
        return config;
    }

    public Component getTranslation(String path, String defaultTranslation) {
        return MiniMessage.miniMessage().deserialize(AdventureUtil.replaceAmpersand(getString(path, defaultTranslation)));
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

    public double getDouble(String path, double def, String comment) {
        config.addDefault(path, def, comment);
        return config.getDouble(path, def);
    }

    public double getDouble(String path, double def) {
        config.addDefault(path, def);
        return config.getDouble(path, def);
    }

    public float getFloat(String path, float def) {
        config.addDefault(path, def);
        return config.getFloat(path, def);
    }

    public float getFloat(String path, float def, @NotNull String comment) {
        config.addDefault(path, def, comment);
        return config.getFloat(path, def);
    }

    public int getInt(String path, int def, String comment) {
        config.addDefault(path, def, comment);
        return config.getInteger(path, def);
    }

    public int getInt(String path, int def) {
        config.addDefault(path, def);
        return config.getInteger(path, def);
    }

    public long getLong(String path, long def, String comment) {
        config.addDefault(path, def, comment);
        return config.getLong(path, def);
    }

    public long getLong(String path, long def) {
        config.addDefault(path, def);
        return config.getLong(path, def);
    }

    public List<String> getList(String path, List<String> def, String comment) {
        config.addDefault(path, def, comment);
        return config.getStringList(path);
    }

    public List<String> getList(String path, List<String> def) {
        config.addDefault(path, def);
        return config.getStringList(path);
    }
}