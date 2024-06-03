package me.xginko.craftableinvisibleitemframes.commands.iframe.subcommands;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.commands.SubCommand;
import me.xginko.craftableinvisibleitemframes.models.InvisibleItemFrame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class GetSubCommand extends SubCommand {
    @Override
    public String label() {
        return "get";
    }

    @Override
    public String desc() {
        return "Give yourself invisible item frames";
    }

    @Override
    public String syntax() {
        return "/iframe get";
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Command can't be executed from console.").color(NamedTextColor.RED));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("craftableinvisibleitemframes.cmd.get")) {
            player.sendMessage(CraftableInvisibleItemFrames.getLang(player.locale()).no_permission);
            return true;
        }

        Map<Integer, ItemStack> items_that_did_not_fit = player.getInventory().addItem(new InvisibleItemFrame(64, player.locale()));
        for (Map.Entry<Integer, ItemStack> entry : items_that_did_not_fit.entrySet()) {
            player.getWorld().dropItemNaturally(player.getLocation(), entry.getValue()).setPickupDelay(1);
        }

        player.sendMessage(Component.text("Received 64 invisible item frames.").color(NamedTextColor.GREEN));
        return true;
    }
}
