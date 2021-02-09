package org.concordiacraft.reditems.recipes;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.concordiacraft.reditems.items.CustomItem;
import org.concordiacraft.reditems.main.RedItems;
import org.json.simple.JSONObject;
/**
 * @author Theorenter
 * Creating furnace recipes for RedItems.
 */
public class RedFurnaceRecipe {
    /**
     * Creating default furnace recipes.
     * @param recipe JSON configurations for recipe.
     * @param item what item is the recipe created for.
     */
    public static void newFurnaceRecipe(JSONObject recipe, CustomItem item) {
        JSONObject furnaceObject = (JSONObject) recipe.get("furnace-recipe");
        String keyString = item.getID() + furnaceObject.get("recipe-key");
        Material fromItemStack = Material.getMaterial((String)furnaceObject.get("source-material"));
        Double experience = (Double) furnaceObject.get("experience");
        Long cookingTime = (Long) furnaceObject.get("cooking-time");

        NamespacedKey namespacedKey = new NamespacedKey(RedItems.getPlugin(), keyString);
        FurnaceRecipe furnaceRecipe = new FurnaceRecipe(namespacedKey, item.getItemStack(), fromItemStack, experience.floatValue(), cookingTime.intValue());

        Bukkit.addRecipe(furnaceRecipe);
    }
}
