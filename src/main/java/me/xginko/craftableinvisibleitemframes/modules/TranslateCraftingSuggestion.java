package me.xginko.craftableinvisibleitemframes.modules;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

public class TranslateCraftingSuggestion implements CraftableInvisibleItemFramesModule, Listener {

    protected TranslateCraftingSuggestion() {}

    @Override
    public void enable() {
        CraftableInvisibleItemFrames plugin = CraftableInvisibleItemFrames.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return CraftableInvisibleItemFrames.getConfiguration().auto_lang;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    private void onCraft(PrepareItemCraftEvent event) {
        if (CraftableInvisibleItemFrames.isInvisibleRegularFrameRecipe(event.getRecipe())) {
            Player player = (Player) event.getView().getPlayer();
            if (!player.hasPermission("craftableinvisibleitemframes.craft")) return;
            event.getInventory().setResult(ItemUtils.getRegularInvisibleItemFrame(1, player.locale()));
        }
    }
}
