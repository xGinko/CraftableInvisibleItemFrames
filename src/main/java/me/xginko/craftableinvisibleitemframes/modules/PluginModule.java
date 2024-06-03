package me.xginko.craftableinvisibleitemframes.modules;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;

import java.util.HashSet;

public interface PluginModule {

    void enable();
    boolean shouldEnable();
    void disable();

    HashSet<PluginModule> modules = new HashSet<>();

    static void reloadModules() {
        modules.forEach(PluginModule::disable);
        modules.clear();

        modules.add(new RegularInvisibleItemFrames());
        if (CraftableInvisibleItemFrames.config().glowsquid_invisible_itemframes_are_enabled) {
            if (CraftableInvisibleItemFrames.isGlowVariantCompatible()) modules.add(new GlowsquidInvisibleItemFrames());
            else CraftableInvisibleItemFrames.logger().warning("Glow item frames can not be enabled on this version.");
        }
        modules.add(new TranslateCraftingSuggestion());

        modules.forEach(module -> {
            if (module.shouldEnable()) module.enable();
        });
    }
}
