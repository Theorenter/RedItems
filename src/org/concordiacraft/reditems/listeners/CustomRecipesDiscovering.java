package org.concordiacraft.reditems.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;
import org.concordiacraft.reditems.main.RedItems;
import org.concordiacraft.reditems.recipes.RecipeManager;

public class CustomRecipesDiscovering implements Listener {
    RedItems plugin;
    public CustomRecipesDiscovering(RedItems plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCustomRecipeBlock(PlayerRecipeDiscoverEvent e) {
        if (RecipeManager.getCustomRecipesList().contains(e.getRecipe())) {
            e.setCancelled(true);
        }
    }
}