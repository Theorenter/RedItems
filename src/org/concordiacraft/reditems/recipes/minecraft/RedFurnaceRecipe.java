package org.concordiacraft.reditems.recipes.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
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
     * @param itemStack what item is the recipe created for.
     * @param itemID Item ID.
     */
    public static void newFurnaceRecipe(JSONObject recipe, ItemStack itemStack, String itemID) {
        JSONObject furnaceObject = (JSONObject) recipe.get("furnace-recipe");
        String keyString = itemID + furnaceObject.get("recipe-key");
        Material fromItemStack = Material.getMaterial((String)furnaceObject.get("source-material"));
        Double experience = (Double) furnaceObject.get("experience");
        Long cookingTime = (Long) furnaceObject.get("cooking-time");

        NamespacedKey namespacedKey = new NamespacedKey(RedItems.getPlugin(), keyString);
        FurnaceRecipe furnaceRecipe = new FurnaceRecipe(namespacedKey, itemStack, fromItemStack, experience.floatValue(), cookingTime.intValue());

        Bukkit.addRecipe(furnaceRecipe);
    }
}
