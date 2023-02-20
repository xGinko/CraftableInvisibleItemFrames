package me.xginko.craftableinvisibleitemframes.modules;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.modules.glowing.GlowsquidInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.modules.regular.RegularInvisibleItemFrames;
import org.bukkit.event.HandlerList;

import java.util.HashSet;

public interface CraftableInvisibleItemFramesModule {

    void enable();
    boolean shouldEnable();

    HashSet<CraftableInvisibleItemFramesModule> modules = new HashSet<>();

    static void reloadModules() {
        modules.clear();
        CraftableInvisibleItemFrames plugin = CraftableInvisibleItemFrames.getInstance();
        plugin.getServer().getScheduler().cancelTasks(plugin);
        HandlerList.unregisterAll(plugin);

        modules.add(new RegularInvisibleItemFrames());
        modules.add(new PlacedItemFrameGlowEnabler());
        if (CraftableInvisibleItemFrames.getConfiguration().can_do_glowsquid_frames) {
            modules.add(new GlowsquidInvisibleItemFrames());
        }

        for (CraftableInvisibleItemFramesModule module : modules) {
            if (module.shouldEnable()) module.enable();
        }
    }
}
