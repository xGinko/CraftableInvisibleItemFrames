package me.xginko.craftableinvisibleitemframes.modules;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.models.InvisibleItemFrame;
import me.xginko.craftableinvisibleitemframes.utils.Util;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class TranslateCraftingSuggestion implements PluginModule, Listener {

    protected TranslateCraftingSuggestion() {}

    @Override
    public void enable() {
        CraftableInvisibleItemFrames plugin = CraftableInvisibleItemFrames.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldEnable() {
        return CraftableInvisibleItemFrames.config().auto_lang;
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onCraft(PrepareItemCraftEvent event) {
        if (!Util.isInvisibleItemFrameRecipe(event.getRecipe())) return;
        if (!(event.getView().getPlayer() instanceof Player)) return;
        Player player = (Player) event.getView().getPlayer();

        if (player.hasPermission("craftableinvisibleitemframes.craft")) {
            final ItemStack resultItem = event.getInventory().getResult();
            event.getInventory().setResult(new InvisibleItemFrame(resultItem == null ? 8 : resultItem.getAmount(), player.locale()));
        }
    }
}
