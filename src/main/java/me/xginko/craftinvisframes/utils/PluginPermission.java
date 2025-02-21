package me.xginko.craftinvisframes.utils;

import me.xginko.craftinvisframes.CraftInvisFrames;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public enum PluginPermission {

    RELOAD_CMD(new Permission("craftinvisframes.cmd.reload", PermissionDefault.OP)),
    VERSION_CMD(new Permission("craftinvisframes.cmd.version", PermissionDefault.OP));

    private final Permission permission;

    PluginPermission(Permission permission) {
        this.permission = permission;
    }

    public Permission get() {
        return permission;
    }

    public static void registerAll() {
        for (PluginPermission permission : PluginPermission.values()) {
            try {
                CraftInvisFrames.get().getServer().getPluginManager().addPermission(permission.get());
            } catch (IllegalArgumentException e) {
                CraftInvisFrames.get().logger().warn("Permission '{}' is already registered.", permission.get().getName());
            }
        }
    }

    public static void unregisterAll() {
        for (PluginPermission permission : PluginPermission.values()) {
            CraftInvisFrames.get().getServer().getPluginManager().removePermission(permission.get());
        }
    }
}
