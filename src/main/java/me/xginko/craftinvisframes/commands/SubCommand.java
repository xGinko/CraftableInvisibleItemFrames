package me.xginko.craftinvisframes.commands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class SubCommand {
    public abstract String label();
    public abstract String desc();
    public abstract String syntax();
    public abstract boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args);
}
