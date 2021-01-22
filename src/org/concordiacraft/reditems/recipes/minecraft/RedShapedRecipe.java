package org.concordiacraft.reditems.recipes.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.concordiacraft.reditems.main.RedItems;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Theorenter
 * Creating shaped recipes for RedItems.
 */
public class RedShapedRecipe {
    /**
     * Creating default shaped recipes.
     * @param recipe JSON configurations for recipe.
     * @param itemStack What item is the recipe created for.
     * @param itemID Item ID.
     */
    public static void newShapedRecipe(JSONObject recipe, ItemStack itemStack, String itemID) {
        JSONObject shapedObject = (JSONObject) recipe.get("shaped-recipe");
        String keyString = itemID + shapedObject.get("recipe-key");
        Map<String, String> ingredients = (Map<String, String>) shapedObject.get("ingredients");

        if (shapedObject.containsKey("amount")) {
            Long amount = (Long) shapedObject.get("amount");
            if (amount > 0 && amount <= itemStack.getMaxStackSize()) {
                itemStack.setAmount(amount.intValue());
            } else {
                RedItems.getPlugin().getRedLogger().warning("The amount of executed items for the recipe " +
                        keyString + " is specified incorrectly! Max stack size of this itemStack: " +
                        itemStack.getMaxStackSize() + " | Specified number: " + amount);
            }
        }
        JSONArray jsonShape = (JSONArray) shapedObject.get("shape");
        List<String> shape = new ArrayList<>();
        for (Object shapeRow: jsonShape) { shape.add(shapeRow.toString()); }

        NamespacedKey namespacedKey = new NamespacedKey(RedItems.getPlugin(), keyString);
        ShapedRecipe shapedRecipe = new ShapedRecipe(namespacedKey, itemStack);
        shapedRecipe.shape(shape.get(0), shape.get(1), shape.get(2));
        ingredients.forEach((k, v) -> shapedRecipe.setIngredient(k.charAt(0), Material.getMaterial(v)));

        // Craft-group
        if (shapedObject.containsKey("craft-group"))
            shapedRecipe.setGroup((String) shapedObject.get("craft-group"));

        Bukkit.addRecipe(shapedRecipe);
    }
}
