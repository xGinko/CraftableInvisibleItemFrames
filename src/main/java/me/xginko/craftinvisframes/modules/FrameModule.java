package me.xginko.craftinvisframes.modules;

import com.google.common.collect.ImmutableList;
import me.xginko.craftinvisframes.CraftInvisFrames;
import me.xginko.craftinvisframes.utils.Disableable;
import me.xginko.craftinvisframes.utils.Enableable;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class FrameModule implements Enableable, Disableable {

    protected static final List<Class<? extends FrameModule>> AVAILABLE_MODULES;
    protected static final Map<Class<? extends FrameModule>, FrameModule> ENABLED_MODULES;

    static {
        AVAILABLE_MODULES = new Reflections(FrameModule.class.getPackage().getName())
                .get(Scanners.SubTypes.of(FrameModule.class).asClass())
                .stream()
                .filter(clazz -> !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers()))
                .map(clazz -> (Class<? extends FrameModule>) clazz)
                .sorted(Comparator.comparing(Class::getSimpleName))
                .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
        ENABLED_MODULES = new ConcurrentHashMap<>(AVAILABLE_MODULES.size());
    }

    public static <T extends FrameModule> T get(Class<T> clazz) {
        return (T) ENABLED_MODULES.getOrDefault(clazz, null);
    }

    protected final CraftInvisFrames plugin = CraftInvisFrames.get();
    protected final String configPath, logFormat;
    protected final boolean enabled_in_config;

    public FrameModule(String configPath, boolean defEnabled, String comment) {
        this.configPath = configPath;
        String[] paths = configPath.split("\\.");
        this.logFormat = "<" + (paths.length < 3 ? configPath : paths[paths.length - 2] + "." + paths[paths.length - 1]) + "> {}";
        if (comment == null) {
            this.enabled_in_config = plugin.config().getBoolean(configPath + ".enable", defEnabled);
        } else {
            this.enabled_in_config = plugin.config().getBoolean(configPath + ".enable", defEnabled, comment);
        }
    }

    public FrameModule(String configPath, boolean defEnabled) {
        this(configPath, defEnabled, null);
    }

    public boolean shouldEnable() {
        return enabled_in_config;
    }

    public static void disableAll() {
        for (Map.Entry<Class<? extends FrameModule>, FrameModule> entry : ENABLED_MODULES.entrySet()) {
            try {
                entry.getValue().disable();
            } catch (Throwable t) {
                CraftInvisFrames.get().logger().error("Error disabling module '{}'", entry.getKey().getSimpleName(), t);
            }
        }
        ENABLED_MODULES.clear();
    }

    public static void reloadAll() {
        disableAll();

        for (Class<? extends FrameModule> moduleClass : AVAILABLE_MODULES) {
            try {
                FrameModule module = moduleClass.getDeclaredConstructor().newInstance();
                if (module.shouldEnable()) {
                    ENABLED_MODULES.put(moduleClass, module);
                }
            } catch (Throwable t) {
                if (t.getCause() instanceof NoClassDefFoundError) {
                    CraftInvisFrames.get().logger().info("Dependencies for module class {} missing.", moduleClass.getSimpleName());
                } else {
                    CraftInvisFrames.get().logger().warn("Failed initialising module class '{}'.", moduleClass.getSimpleName(), t);
                }
            }
        }

        for (Map.Entry<Class<? extends FrameModule>, FrameModule> entry : ENABLED_MODULES.entrySet()) {
            try {
                entry.getValue().enable();
            } catch (Throwable t) {
                CraftInvisFrames.get().logger().warn("Failed enabling module class '{}'.", entry.getKey().getSimpleName(), t);
            }
        }
    }

    protected void error(String message, Throwable throwable) {
        plugin.logger().error(logFormat, message, throwable);
    }

    protected void error(String message) {
        plugin.logger().error(logFormat, message);
    }

    protected void warn(String message) {
        plugin.logger().warn(logFormat, message);
    }

    protected void info(String message) {
        plugin.logger().info(logFormat, message);
    }

    protected void notRecognized(Class<?> clazz, String unrecognized) {
        warn("Unable to parse " + clazz.getSimpleName() + " at '" + unrecognized + "'. Please check your configuration.");
    }
}
