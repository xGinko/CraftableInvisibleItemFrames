package me.xginko.craftinvisframes.utils;

import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

public final class AdventureUtil {

    public static final TextColor PURPLE, LIGHT_PURPLE, GINKO_BLUE, RED, YELLOW, WHITE, GREEN;

    static {
        WHITE = TextColor.fromHexString("#FBEBFC");
        PURPLE = TextColor.fromHexString("#D0A7FF");
        LIGHT_PURPLE = TextColor.fromHexString("#EDDCFF");
        YELLOW = TextColor.fromHexString("#C0C000");
        GINKO_BLUE = TextColor.fromHexString("#21FFF5");
        RED = TextColor.fromHexString("#C00050");
        GREEN = TextColor.fromHexString("#32A852");
    }

    public static @NotNull String replaceAmpersand(@NotNull String string) {
        string = string.replace("&0", "<black>");
        string = string.replace("&1", "<dark_blue>");
        string = string.replace("&2", "<dark_green>");
        string = string.replace("&3", "<dark_aqua>");
        string = string.replace("&4", "<dark_red>");
        string = string.replace("&5", "<dark_purple>");
        string = string.replace("&6", "<gold>");
        string = string.replace("&7", "<gray>");
        string = string.replace("&8", "<dark_gray>");
        string = string.replace("&9", "<blue>");
        string = string.replace("&a", "<green>");
        string = string.replace("&b", "<aqua>");
        string = string.replace("&c", "<red>");
        string = string.replace("&d", "<light_purple>");
        string = string.replace("&e", "<yellow>");
        string = string.replace("&f", "<white>");
        string = string.replace("&k", "<obfuscated>");
        string = string.replace("&l", "<bold>");
        string = string.replace("&m", "<strikethrough>");
        string = string.replace("&n", "<underlined>");
        string = string.replace("&o", "<italic>");
        string = string.replace("&r", "<reset>");
        return string;
    }
}
