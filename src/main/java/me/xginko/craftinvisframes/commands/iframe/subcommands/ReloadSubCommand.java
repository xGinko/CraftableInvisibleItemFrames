package me.xginko.craftinvisframes.commands.iframe.subcommands;

import me.xginko.craftinvisframes.CraftInvisFrames;
import me.xginko.craftinvisframes.commands.SubCommand;
import me.xginko.craftinvisframes.utils.AdventureUtil;
import me.xginko.craftinvisframes.utils.PluginPermission;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadSubCommand extends SubCommand {

    @Override
    public String label() {
        return "reload";
    }

    @Override
    public String desc() {
        return "Reload the plugin configuration";
    }

    @Override
    public String syntax() {
        return "/iframe reload";
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!sender.hasPermission(PluginPermission.RELOAD_CMD.get())) {
            sender.sendMessage(Component.text("No permission.", AdventureUtil.RED));
            return true;
        }

        CraftInvisFrames.get().getServer().getAsyncScheduler().runNow(CraftInvisFrames.get(), reload -> {
            CraftInvisFrames.get().reloadPlugin();
            sender.sendMessage(Component.text("Reload complete.", AdventureUtil.GREEN));
        });

        return true;
    }
}
