package org.concordiacraft.reditems.recipes;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.concordiacraft.reditems.config.ConfigDefault;
import org.concordiacraft.reditems.main.RedItems;

import java.util.List;

public class DefaultRecipesRemover {

    public static void removeMaterials() {
        List<String> materialsForDelete = RedItems.getPlugin().getDefaultConfig().getCustomConfig().getStringList("recipes.removed");
        for (String item : materialsForDelete) {
            Bukkit.removeRecipe(NamespacedKey.minecraft(item));
        }
    }
}