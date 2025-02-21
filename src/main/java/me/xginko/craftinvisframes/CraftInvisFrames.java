package me.xginko.craftinvisframes;

import me.xginko.craftinvisframes.commands.iframe.IFrameCommand;
import me.xginko.craftinvisframes.config.Config;
import me.xginko.craftinvisframes.modules.FrameModule;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Files;

public final class CraftInvisFrames extends JavaPlugin {

    private static CraftInvisFrames instance;
    private IFrameCommand iFrameCommand;
    private Config config;
    private ComponentLogger logger;

    @Override
    public void onLoad() {
        String shadedLibs = getClass().getPackage().getName() + ".libs";
        Configurator.setLevel(shadedLibs + ".reflections.Reflections", Level.WARN);
    }

    @Override
    public void onEnable() {
        instance = this;
        logger = getComponentLogger();

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

        reloadPlugin();

        logger.info("Registering Commands");
        iFrameCommand = new IFrameCommand();
        iFrameCommand.enable();
        logger.info("Done.");
    }

    @Override
    public void onDisable() {
        FrameModule.disableAll();
        getServer().getGlobalRegionScheduler().cancelTasks(this);
        getServer().getAsyncScheduler().cancelTasks(this);
        iFrameCommand.unregister(getServer().getCommandMap());
        instance = null;
        config = null;
        logger = null;
    }

    public static CraftInvisFrames get()  {
        return instance;
    }

    public Config config() {
        return config;
    }

    public ComponentLogger logger() {
        return logger;
    }

    public void reloadPlugin() {
        try {
            Files.createDirectories(getDataFolder().toPath());
            config = new Config();
            FrameModule.reloadAll();
            config.saveConfig();
        } catch (Exception e) {
            logger.error("Failed to load modules!", e);
        }
    }
}