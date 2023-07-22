package me.xginko.craftableinvisibleitemframes.commands.iframe.subcommands;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.commands.SubCommand;
import me.xginko.craftableinvisibleitemframes.utils.ItemUtils;
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
        return "Give yourself an invisible itemframe";
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
            ItemUtils.addToInventoryOrDrop(player, ItemUtils.getRegularInvisibleItemFrame(64, player.locale()));
            player.sendMessage(Component.text("You received 8 invisible itemframes.").color(NamedTextColor.GREEN));
        } else {
            player.sendMessage(CraftableInvisibleItemFrames.getLang(player.locale()).no_permission);
        }
    }
}
