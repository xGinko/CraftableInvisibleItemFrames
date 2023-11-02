package me.xginko.craftableinvisibleitemframes.commands.iframe.subcommands;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.commands.SubCommand;
import me.xginko.craftableinvisibleitemframes.models.InvisibleItemFrame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetSubCommand extends SubCommand {
    @Override
    public String getName() {
        return "get";
    }

    @Override
    public String getDescription() {
        return "Give yourself invisible item frames";
    }

    @Override
    public String getSyntax() {
        return "/iframe get";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Command can't be executed from console.").color(NamedTextColor.RED));
            return;
        }

        if (player.hasPermission("craftableinvisibleitemframes.cmd.get")) {
            player.getInventory().addItem(new InvisibleItemFrame(64, player.locale())).forEach((index, itemsThatDidNotFit) -> {
                player.getWorld().dropItemNaturally(player.getLocation(), itemsThatDidNotFit).setPickupDelay(1);
            });
            player.sendMessage(Component.text("Received 64 invisible item frames.").color(NamedTextColor.GREEN));
        } else {
            player.sendMessage(CraftableInvisibleItemFrames.getLang(player.locale()).no_permission);
        }
    }
}
