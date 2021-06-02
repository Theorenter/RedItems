package org.concordiacraft.reditems.recipes;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.concordiacraft.reditems.main.RedItems;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Theorenter
 * Just recipe manager.
 */
public class RecipeManager {

    // Fields
    private static ArrayList<NamespacedKey> customRecipesList = new ArrayList<>();

    /**
     * Remove default recipes from config by NameSpacedKey.
     */
    public static void removeDefaultRecipes() {
        List<String> materialsForDelete = RedItems.getPlugin().getDefaultConfig().getRemovedRecipes();
        for (String item : materialsForDelete) {
            Bukkit.removeRecipe(NamespacedKey.minecraft(item));
        }
    }

    /**
     * @return list of custom NameSpacedKeys.
     */
    public static ArrayList<NamespacedKey> getCustomRecipesList() {
        return customRecipesList;
    }

    /**
     * Add to customRecipeList new NameSpacedKey.
     * @param nsk NameSpacedKey of custom recipe.
     */
    public static void addToCustomRecipesList(NamespacedKey nsk) {
        customRecipesList.add(nsk);
    }
}