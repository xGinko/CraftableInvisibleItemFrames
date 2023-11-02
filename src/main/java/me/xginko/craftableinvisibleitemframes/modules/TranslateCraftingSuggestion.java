package me.xginko.craftableinvisibleitemframes.modules;

import me.xginko.craftableinvisibleitemframes.CraftableInvisibleItemFrames;
import me.xginko.craftableinvisibleitemframes.enums.Keys;
import me.xginko.craftableinvisibleitemframes.models.InvisibleItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class TranslateCraftingSuggestion implements PluginModule, Listener {

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

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onCraft(PrepareItemCraftEvent event) {
        if (
                event.getView().getPlayer() instanceof Player player
                && player.hasPermission("craftableinvisibleitemframes.craft")
                && event.getRecipe() instanceof ShapedRecipe shapedRecipe
                && shapedRecipe.getKey().equals(Keys.INVISIBLE_ITEM_FRAME_RECIPE.key())
        ) {
            final ItemStack resultItem = event.getInventory().getResult();
            event.getInventory().setResult(new InvisibleItemFrame(resultItem == null ? 8 : resultItem.getAmount(), player.locale()));
        }
    }
}
