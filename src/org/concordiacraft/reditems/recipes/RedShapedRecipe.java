package org.concordiacraft.reditems.recipes;

import net.minecraft.server.v1_16_R3.ContainerWorkbench;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_16_R3.inventory.RecipeIterator;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.concordiacraft.reditems.items.CustomItem;
import org.concordiacraft.reditems.items.ItemManager;
import org.concordiacraft.reditems.main.RedItems;
import org.concordiacraft.reditems.recipes.RecipeManager;
import org.concordiacraft.redutils.main.utils.RedLog;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/**
 * @author Theorenter
 * Creating shaped recipes for RedItems with the use of custom.
 *
 * For optimal matching of recipes, a regular recipe is created that
 * uses the materials of the items, and in parallel, a matrix is created
 * that already reflects the ItemStacks of the user items.
 */
public class RedShapedRecipe {

    public static void newRedShapedRecipe(JSONObject recipe, CustomItem item) {
        JSONObject shapedObject = (JSONObject) recipe.get("shaped-recipe");
        String recipeKeyString = item.getID() + shapedObject.get("recipe-key");
        Map<String, JSONObject> ingredients = (Map<String, JSONObject>) shapedObject.get("ingredients");

        // How many items will be received
        if (shapedObject.containsKey("amount")) {
            Long amount = (Long) shapedObject.get("amount");
            if (amount > 0 && amount <= item.getItemStack().getMaxStackSize()) {
                item.getItemStack().setAmount(amount.intValue());
            } else {
                RedItems.getPlugin().getRedLogger().warning("The amount of executed items for the recipe " +
                        recipeKeyString + " is specified incorrectly! Max stack size of this itemStack: " +
                        item.getItemStack().getMaxStackSize() + " | Specified number: " + amount);
            }
        }
        JSONArray jsonShape = (JSONArray) shapedObject.get("shape");

        // Creating a matrix for crafting with custom items
        /*ItemStack[] matrix = new ItemStack[9];

        String allShapeRow = "";
        for (Object shapeRow: jsonShape)
            allShapeRow += shapeRow.toString();

        char[] charArray = allShapeRow.toCharArray();

        for (Map.Entry<String, JSONObject> entry : ingredients.entrySet()) {
            char chr = entry.getKey().charAt(0);
            for (int i = 0; i < charArray.length; i++) {
                if (charArray[i] == ' ') { matrix[i] = null; charArray[i] = '.'; continue; }
                if (charArray[i] == chr) {

                    JSONObject obj = entry.getValue();
                    ItemStack itemStack = new ItemStack(Material.getMaterial(obj.get("material").toString()));

                    if (obj.get("custom-item-id") == null) {
                        matrix[i] = itemStack;
                        continue;
                    }
                    String customItemID = obj.get("custom-item-id").toString();
                    if (!ItemManager.getCustomItemList().containsKey(customItemID)) {
                        matrix[i] = new CustomItem(RedItems.getPlugin(), customItemID).getItemStack();
                    } else matrix[i] = ItemManager.getCustomItemList().get(customItemID).getItemStack();
                }
            }
            item.addShapedMatrixRecipe(recipeKeyString, matrix);
        }*/

        List<String> shapeArray = new ArrayList<>();
        for (Object shapeRow: jsonShape) { shapeArray.add(shapeRow.toString()); }

        NamespacedKey recipeKey = new NamespacedKey(RedItems.getPlugin(), recipeKeyString);
        ShapedRecipe shapedRecipe = new ShapedRecipe(recipeKey, item.getItemStack());
        shapedRecipe.shape(shapeArray.get(0), shapeArray.get(1), shapeArray.get(2));
        ingredients.forEach((k, v) -> {
            try {
                String customItemID = v.get("custom-item-id").toString();
                if (!ItemManager.getCustomItemList().containsKey(customItemID)) {
                    ItemStack customItemStack = new CustomItem(RedItems.getPlugin(), customItemID).getItemStack();
                    shapedRecipe.setIngredient(k.charAt(0), new RecipeChoice.ExactChoice(customItemStack));
                } else {
                    shapedRecipe.setIngredient(k.charAt(0), new RecipeChoice.ExactChoice(ItemManager.getCustomItemList().get(customItemID).getItemStack()));
                }
            } catch (NullPointerException e) {
                ItemStack customItem = new ItemStack(Material.getMaterial(v.get("material").toString()));
                shapedRecipe.setIngredient(k.charAt(0), new RecipeChoice.ExactChoice(customItem));
            }
        });

        // Craft-group
        if (shapedObject.containsKey("craft-group"))
            shapedRecipe.setGroup((String) shapedObject.get("craft-group"));

        Bukkit.addRecipe(shapedRecipe);
        RecipeManager.addToCustomRecipesList(recipeKey);
    }
}
