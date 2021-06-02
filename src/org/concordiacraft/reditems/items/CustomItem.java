package org.concordiacraft.reditems.items;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.concordiacraft.reditems.main.RedItems;
import org.concordiacraft.reditems.recipes.RedShapedRecipe;
import org.concordiacraft.reditems.recipes.RedFurnaceRecipe;
import org.concordiacraft.redutils.utils.RedFormatter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author Theorenter
 * Add-in for ItemStack, which allows you to create custom items from JSON files.
 */

public class CustomItem {

    // Fields
    private String ID;
    private ItemStack itemStack;
    private ItemMeta customItemMeta;
    /**
     * Constructor of a new custom item.
     * @param plugin a plugin to which custom items will be assigned.
     * @param file JSON file from which the custom item is formed.
     */
    public CustomItem(RedItems plugin, File file) {
        itemCreation(plugin, file);
    }
    public CustomItem(RedItems plugin, String ID) {
        File customItemFile = new File(plugin.getDataFolder() + File.separator + "settings" +
                File.separator + "content" + File.separator + "items" + File.separator + ID + ".json");
        itemCreation(plugin, customItemFile); }

    // Main part
    private void itemCreation(RedItems plugin, File file) {
        try {
            // JSON
            JSONParser jsonParser = new JSONParser();
            Object parsed = jsonParser.parse(new BufferedReader(new InputStreamReader(new FileInputStream(file.getPath()), StandardCharsets.UTF_8)));
            JSONObject customItemJSON = (JSONObject) parsed;

            // Required values
            this.ID = (String) customItemJSON.get("reditems-id");
            itemStack = new ItemStack(Material.valueOf((String) customItemJSON.get("material")));

            // Meta
            JSONObject metaObject = (JSONObject) customItemJSON.get("meta");
            if (metaObject != null) {
                this.customItemMeta = this.itemStack.getItemMeta();

                if (metaObject.containsKey("display-name")) {
                    String formatDisplayName;
                    String displayName = (String) metaObject.get("display-name");
                    formatDisplayName = RedFormatter.format(displayName);
                    customItemMeta.setDisplayName(formatDisplayName);
                }
                if (metaObject.containsKey("lore")) {
                    List<String> lore = (List<String>) metaObject.get("lore");
                    List<String> formatLore = new ArrayList<>();
                    for (String s : lore) {
                        formatLore.add(RedFormatter.format(s));
                    }
                    customItemMeta.setLore(formatLore);
                }
                if (metaObject.containsKey("unbreakable")) {
                    boolean unbreakable = (boolean) metaObject.get("unbreakable");
                    customItemMeta.setUnbreakable(unbreakable);
                }
                if (metaObject.containsKey("custom-model-data")) {
                    Long customModelData = (Long) metaObject.get("custom-model-data");
                    customItemMeta.setCustomModelData(customModelData.intValue());
                }
                // Enchantments
                if (metaObject.containsKey("enchantments")) {
                    JSONArray enchantmentsObject = (JSONArray) metaObject.get("enchantments");
                    for (Object objAEnchant : enchantmentsObject) {
                        JSONObject enchant = (JSONObject) objAEnchant;
                        customItemMeta = addEnchantment(enchant, customItemMeta, itemStack);
                    }
                }
                // Attributes
                if (metaObject.containsKey("attributes")) {
                    JSONArray attributesObject = (JSONArray) metaObject.get("attributes");
                    for (Object objAttribute : attributesObject) {
                        JSONObject attribute = (JSONObject) objAttribute;
                        customItemMeta = addAttribute(attribute, customItemMeta);
                    }
                }
                // Persistent data
                if (metaObject.containsKey("persistent-data")) {
                    JSONArray dataObject = (JSONArray) metaObject.get("persistent-data");
                    for (Object objData : dataObject) {
                        JSONObject data = (JSONObject) objData;
                        customItemMeta = addPersistentData(data, customItemMeta);
                    }
                }
            }

            // Set custom ID
            PersistentDataContainer dataContainer = this.customItemMeta.getPersistentDataContainer();
            dataContainer.set(new NamespacedKey(plugin, "REDITEMS-ID"), PersistentDataType.STRING, this.ID);

            itemStack.setItemMeta(customItemMeta);

            // Recipes
            if (customItemJSON.containsKey("recipes")) {
                JSONArray recipesList = (JSONArray) customItemJSON.get("recipes");
                for (Object objRecipe : recipesList) {
                    JSONObject recipe = (JSONObject) objRecipe;
                    if (recipe.containsKey("shaped-recipe")) { RedShapedRecipe.newRedShapedRecipe(recipe, this); break; }
                    if (recipe.containsKey("furnace-recipe")) { RedFurnaceRecipe.newFurnaceRecipe(recipe, this); break; }
                }
            }

            // Add item to CustomItems list & set final meta
            ItemManager.getCustomItemList().put(this.ID, this);
            ItemManager.getCustomItemStackList().add(this.itemStack);

            ItemManager.customItemLoadingDebugLog(this);
        } catch (IOException | ParseException e) {
            plugin.getRedLogger().error("Cannot create a custom item from the " + file.getName() + " file", e);
        }
    }

    // ItemMeta mods
    private ItemMeta addEnchantment(JSONObject enchantment, ItemMeta itemMeta, ItemStack itemStack) {
        String enchantmentString = (String) enchantment.get("enchantment");
        Long level = (Long) enchantment.get("level");
        Boolean ignoreLevelRestriction = (Boolean) enchantment.get("ignore-level-restriction");
        Enchantment enchant;
        switch (enchantmentString) {
            case "ARROW-DAMAGE": { enchant = Enchantment.ARROW_DAMAGE; break; }
            case "ARROW-FIRE": { enchant = Enchantment.ARROW_FIRE; break; }
            case "ARROW-INFINITE": { enchant = Enchantment.ARROW_INFINITE; break; }
            case "ARROW-KNOCKBACK": { enchant = Enchantment.ARROW_KNOCKBACK; break; }
            case "BINDING-CURSE": { enchant = Enchantment.BINDING_CURSE; break; }
            case "CHANNELING": { enchant = Enchantment.CHANNELING; break; }
            case "DAMAGE-ALL": { enchant = Enchantment.DAMAGE_ALL; break; }
            case "DAMAGE-ARTHROPODS": { enchant = Enchantment.DAMAGE_ARTHROPODS; break; }
            case "DAMAGE-UNDEAD": { enchant = Enchantment.DAMAGE_UNDEAD; break; }
            case "DEPTH-STRIDER": { enchant = Enchantment.DEPTH_STRIDER; break; }
            case "DIG-SPEED": { enchant = Enchantment.DIG_SPEED; break; }
            case "DURABILITY": { enchant = Enchantment.DURABILITY; break; }
            case "FIRE-ASPECT": { enchant = Enchantment.FIRE_ASPECT; break; }
            case "FROST-WALKER": { enchant = Enchantment.FROST_WALKER; break; }
            case "IMPALING": { enchant = Enchantment.IMPALING; break; }
            case "KNOCKBACK": { enchant = Enchantment.KNOCKBACK; break; }
            case "LOOT-BONUS-BLOCKS": { enchant = Enchantment.LOOT_BONUS_BLOCKS; break; }
            case "LOOT-BONUS-MOBS": { enchant = Enchantment.LOOT_BONUS_MOBS; break; }
            case "LOYALTY": { enchant = Enchantment.LOYALTY; break; }
            case "LUCK": { enchant = Enchantment.LUCK; break; }
            case "LURE": { enchant = Enchantment.LURE; break; }
            case "MENDING": { enchant = Enchantment.MENDING; break; }
            case "MULTISHOT": { enchant = Enchantment.MULTISHOT; break; }
            case "OXYGEN": { enchant = Enchantment.OXYGEN; break; }
            case "PIERCING": { enchant = Enchantment.PIERCING; break; }
            case "PROTECTION-ENVIRONMENTAL": { enchant = Enchantment.PROTECTION_ENVIRONMENTAL; break; }
            case "PROTECTION-EXPLOSIONS": { enchant = Enchantment.PROTECTION_EXPLOSIONS; break; }
            case "PROTECTION-FALL": { enchant = Enchantment.PROTECTION_FALL; break; }
            case "PROTECTION-FIRE": { enchant = Enchantment.PROTECTION_FIRE; break; }
            case "PROTECTION-PROJECTILE": { enchant = Enchantment.PROTECTION_PROJECTILE; break; }
            case "QUICK-CHARGE": { enchant = Enchantment.QUICK_CHARGE; break; }
            case "RIPTIDE": { enchant = Enchantment.RIPTIDE; break; }
            case "SILK-TOUCH": { enchant = Enchantment.SILK_TOUCH; break; }
            case "SOUL-SPEED": { enchant = Enchantment.SOUL_SPEED; break; }
            case "SWEEPING-EDGE": { enchant = Enchantment.SWEEPING_EDGE; break; }
            case "THORNS": { enchant = Enchantment.THORNS; break; }
            case "VANISHING-CURSE": { enchant = Enchantment.VANISHING_CURSE; break; }
            case "WATER-WORKER": { enchant = Enchantment.WATER_WORKER; break; }
            default: {
                RedItems.getPlugin().getRedLogger().warning("Field \"enchantment\" for the item \"" + this.ID + "\" is set incorrectly!");
                RedItems.getPlugin().getRedLogger().warning("Here is a list of acceptable values for this field (last values):");
                for (Object element : Arrays.stream(Enchantment.values()).toArray()) { RedItems.getPlugin().getRedLogger().warning(element.toString().replace('_', '-')); }
                return itemMeta;
            }
        }
        if (!enchant.canEnchantItem(itemStack)) {
            RedItems.getPlugin().getRedLogger().warning("Enchantment \"" + enchantmentString + "\" cannot be applied to an item \""+ this.ID + "\"");
        }
        if ((level > enchant.getMaxLevel()) && (ignoreLevelRestriction == false)) {
            RedItems.getPlugin().getRedLogger().warning("The maximum enchantment \"" + enchantmentString + "\" of an item \""+ this.ID +"\" is: " + enchant.getMaxLevel());
        }
        itemMeta.addEnchant(enchant, level.intValue(), ignoreLevelRestriction);
        return itemMeta;
    }

    private ItemMeta addAttribute(JSONObject attribute, ItemMeta itemMeta) {
        String attributeMod = (String) attribute.get("attribute-modifier");
        Long amount = (Long) attribute.get("attribute-amount");
        String operationSt = (String) attribute.get("attribute-operation");
        String eqSlot = (String) attribute.get("equipment-slot");

        AttributeModifier modifier;

        AttributeModifier.Operation operation = null;
        if (operationSt != null) {

            switch (operationSt) {
                case "ADD-NUMBER": {
                    operation = AttributeModifier.Operation.ADD_NUMBER;
                    break;
                }
                case "ADD-SCALAR": {
                    operation = AttributeModifier.Operation.ADD_SCALAR;
                    break;
                }
                case "MULTIPLY-SCALAR": {
                    operation = AttributeModifier.Operation.MULTIPLY_SCALAR_1;
                }
                default: {
                    RedItems.getPlugin().getRedLogger().warning("Field \"attribute-operation\" for the item \"" + this.ID + "\" is set incorrectly!");
                    RedItems.getPlugin().getRedLogger().warning("Available values for this field: \"ADD-NUMBER\", \"ADD-SCALAR\", \"MULTIPLY-SCALAR\"");
                }
            }
        }

        EquipmentSlot equipmentSlot = null;
        if (eqSlot != null) {
            switch (eqSlot) {
                case "HAND": { equipmentSlot = EquipmentSlot.HAND; break; }
                case "OFF-HAND": { equipmentSlot = EquipmentSlot.OFF_HAND; break; }
                case "HEAD": { equipmentSlot = EquipmentSlot.HEAD; break; }
                case "CHEST": { equipmentSlot = EquipmentSlot.CHEST; break; }
                case "LEGS": { equipmentSlot = EquipmentSlot.LEGS; break; }
                case "FEET": { equipmentSlot = EquipmentSlot.FEET; break; }
                default : {
                    RedItems.getPlugin().getRedLogger().warning("Field \"equipment-slot\" for the item \"" + this.ID + "\" is set incorrectly!");
                    RedItems.getPlugin().getRedLogger().warning("Available values for this field: \"HAND\", \"OFF-HAND\", \"HEAD\", \"CHEST\", \"LEGS\", \"FEET\" (or you can simply delete this field so that the modifier applies to everything at once)");
                }
            }
            modifier = new AttributeModifier(UUID.randomUUID(), attributeMod, amount, operation, equipmentSlot);
        } else {
            modifier = new AttributeModifier(UUID.randomUUID(), attributeMod, amount, operation);
        }

        Attribute atr = null;
        switch (attributeMod) {
            case "GENERIC-ARMOR": { atr = Attribute.GENERIC_ARMOR; break; }
            case "GENERIC-ARMOR-TOUGHNESS": { atr = Attribute.GENERIC_ARMOR_TOUGHNESS; break; }
            case "GENERIC-ATTACK-DAMAGE": { atr = Attribute.GENERIC_ATTACK_DAMAGE; break; }
            case "GENERIC-ATTACK-KNOCKBACK": { atr = Attribute.GENERIC_ATTACK_KNOCKBACK; break; }
            case "GENERIC-ATTACK-SPEED": { atr = Attribute.GENERIC_ATTACK_SPEED; break; }
            case "GENERIC-FLYING-SPEED": { atr = Attribute.GENERIC_FLYING_SPEED; break; }
            case "GENERIC-FOLLOW-RANGE": { atr = Attribute.GENERIC_FOLLOW_RANGE; break; }
            case "GENERIC-KNOCKBACK-RESISTANCE": { atr = Attribute.GENERIC_KNOCKBACK_RESISTANCE; break; }
            case "GENERIC-LUCK": { atr = Attribute.GENERIC_LUCK; break; }
            case "GENERIC-MAX-HEALTH": { atr = Attribute.GENERIC_MAX_HEALTH; break; }
            case "GENERIC-MOVEMENT-SPEED": { atr = Attribute.GENERIC_MOVEMENT_SPEED; break; }
            default : {
                RedItems.getPlugin().getRedLogger().warning("Field \"attribute-modifier\" for the item \"" + this.ID + "\" is set incorrectly!");
                RedItems.getPlugin().getRedLogger().warning("Available values for this field: \"GENERIC-ARMOR\", \"GENERIC-ARMOR-TOUGHNESS\", \"GENERIC-ATTACK-DAMAGE\", \"GENERIC-ATTACK-KNOCKBACK\", \"GENERIC-ATTACK-SPEED\", \"GENERIC-FOLLOW-RANGE\", \"GENERIC-KNOCKBACK-RESISTANCE\", \"GENERIC-LUCK\", \"GENERIC-MAX-HEALTH\", \"GENERIC-MOVEMENT-SPEED\"");
            }
        }
        itemMeta.addAttributeModifier(atr, modifier);
        return itemMeta;
    }

    private ItemMeta addPersistentData(JSONObject persistentData, ItemMeta itemMeta) {
        String dataKey = (String) persistentData.get("data-key");
        String dataTypeStr = (String) persistentData.get("data-type");
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        PersistentDataType persistentDataType;

        switch (dataTypeStr) {
            case "BYTE" : {
                persistentDataType = PersistentDataType.BYTE;
                Long dataValue = (Long) persistentData.get("data-value");
                dataContainer.set(new NamespacedKey(RedItems.getPlugin(), dataKey), persistentDataType, dataValue.byteValue());
                break;
            }
            case "BYTE_ARRAY" : {
                persistentDataType = PersistentDataType.BYTE_ARRAY;
                Byte[] dataValue = (Byte[]) persistentData.get("data-value");
                dataContainer.set(new NamespacedKey(RedItems.getPlugin(), dataKey), persistentDataType, dataValue);
                break;
            }
            case "DOUBLE" : {
                persistentDataType = PersistentDataType.DOUBLE;
                Double dataValue = (Double) persistentData.get("data-value");
                dataContainer.set(new NamespacedKey(RedItems.getPlugin(), dataKey), persistentDataType, dataValue);
                break;
            }
            case "FLOAT" : {
                persistentDataType = PersistentDataType.FLOAT;
                Float dataValue = (Float) persistentData.get("data-value");
                dataContainer.set(new NamespacedKey(RedItems.getPlugin(), dataKey), persistentDataType, dataValue);
                break;
            }
            case "INTEGER" : {
                persistentDataType = PersistentDataType.INTEGER;
                Long dataValue = (Long) persistentData.get("data-value");
                dataContainer.set(new NamespacedKey(RedItems.getPlugin(), dataKey), persistentDataType, dataValue.intValue());
                break;
            }
            case "INTEGER_ARRAY" : {
                persistentDataType = PersistentDataType.INTEGER_ARRAY;
                JSONArray valuesArray = (JSONArray) persistentData.get("data-value");
                int[] dataValue = new int[valuesArray.size()];
                int i = 0;
                for (Object el : valuesArray) {
                    dataValue[i] = ((Long) el).intValue();
                    i++;
                }
                dataContainer.set(new NamespacedKey(RedItems.getPlugin(), dataKey), persistentDataType, dataValue);
                break;
            }
            case "LONG" : {
                persistentDataType = PersistentDataType.LONG;
                Long dataValue = (Long) persistentData.get("data-value");
                dataContainer.set(new NamespacedKey(RedItems.getPlugin(), dataKey), persistentDataType, dataValue);
                break;
            }
            case "LONG_ARRAY" : {
                persistentDataType = PersistentDataType.LONG_ARRAY;
                Long[] dataValue = (Long[]) persistentData.get("data-value");
                dataContainer.set(new NamespacedKey(RedItems.getPlugin(), dataKey), persistentDataType, dataValue);
                break;
            }
            case "SHORT" : {
                persistentDataType = PersistentDataType.SHORT;
                Short dataValue = (Short) persistentData.get("data-value");
                dataContainer.set(new NamespacedKey(RedItems.getPlugin(), dataKey), persistentDataType, dataValue);
                break;
            }
            case "STRING" : {
                persistentDataType = PersistentDataType.STRING;
                String dataValue = (String) persistentData.get("data-value");
                dataContainer.set(new NamespacedKey(RedItems.getPlugin(), dataKey), persistentDataType, dataValue);
                break;
            }
            default : {
                RedItems.getPlugin().getRedLogger().warning("Field \"data-type\" for the item \"" + this.ID + "\" is set incorrectly!");
                RedItems.getPlugin().getRedLogger().warning("Here is a list of acceptable values for this field:");
                RedItems.getPlugin().getRedLogger().warning("BYTE, BYTE_ARRAY, DOUBLE, FLOAT, INTEGER, INTEGER_ARRAY, SHORT, STRING");
                return itemMeta; }
        }
        return itemMeta;
    }

    /**
     * Get a custom ID of CustomItem.
     * @return custom ID.
     */
    public String getID() {
        return this.ID;
    }

    /**
     * Get ItemStack from the CustomItem add-in.
     * @return custom ItemStack.
     */
    public ItemStack getItemStack() { return this.itemStack; }
}