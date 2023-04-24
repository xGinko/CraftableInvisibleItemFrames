package me.xginko.craftableinvisibleitemframes.modules;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
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

        modules.add(new TranslateCraftingSuggestion());
        modules.add(new RegularInvisibleItemFrames());
        if (CraftableInvisibleItemFrames.getConfiguration().glowsquid_invisible_itemframes_are_enabled) {
            modules.add(new GlowsquidInvisibleItemFrames());
        }

        for (CraftableInvisibleItemFramesModule module : modules) {
            if (module.shouldEnable()) module.enable();
        }
    }
}
