package org.concordiacraft.reditems.items;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.concordiacraft.reditems.main.RedItems;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ItemManager {

    private static RedItems plugin;

    // Fields
    private static HashMap<String, CustomItem> customItemList = new HashMap<>();
    private static ArrayList<ItemStack> customItemStackList = new ArrayList<>();

    public static void init(RedItems plugin) {
        ItemManager.plugin = plugin;
        itemLoadFromResources();
        itemLoad();
    }

    private static void itemLoadFromResources() {
        File itemsData = new File(plugin.getDataFolder() + File.separator + "settings" + File.separator + "content" + File.separator + "items");
        if (!itemsData.isDirectory()) {
            CodeSource src = plugin.getClass().getProtectionDomain().getCodeSource();
            URL jar = src.getLocation();
            try {
                ZipInputStream zip = new ZipInputStream(jar.openStream());
                while (true) {
                    ZipEntry e = zip.getNextEntry();
                    if (e == null)
                        break;
                    String fileFullName = e.getName();
                    if ((fileFullName.startsWith("settings/content/items/")) && fileFullName.endsWith(".json")) {
                        plugin.saveResource(fileFullName, false);
                    }
                }
            } catch (IOException e) {
                plugin.getRedLogger().error("An error occurred when creating custom items!", e);
            }
        }
    }
    private static void itemLoad() {
        File customItemsFile = new File(plugin.getDataFolder() + File.separator + "settings" + File.separator + "content" + File.separator + "items");
        if (customItemsFile.listFiles().length == 0) { return; }
        for (File customItemFile : customItemsFile.listFiles())
        {
            // Validation is required to prevent the user item from being re-created.
            String customFileName = customItemFile.getName().replace(".json", "");
            if (!customItemList.containsKey(customFileName)) {
                new CustomItem(plugin, customItemFile);
            }
        }
    }
    // Getters & Setters
    public static ArrayList<ItemStack> getCustomItemStackList() { return customItemStackList; }

    public static HashMap<String, CustomItem> getCustomItemList() { return customItemList; }

    public static boolean isCustomItem(ItemStack itemStack) { return customItemStackList.contains(itemStack); }

    public static CustomItem getCustomItem(String ID) { return customItemList.get(ID); }

    public static void customItemLoadingDebugLog(CustomItem ci) {
        if (!plugin.isDebug()) { return; }
        plugin.getRedLogger().debugStyled("");
        plugin.getRedLogger().debugStyled("[");
        plugin.getRedLogger().debugStyled("    reditems-ID: " + ci.getID());
        plugin.getRedLogger().debugStyled("    material: " + ci.getItemStack().getType().getKey());
        if (ci.getItemStack().hasItemMeta()) {
            ItemMeta im = ci.getItemStack().getItemMeta();

            plugin.getRedLogger().debugStyled("    meta: [");
            if (im.hasDisplayName())
                plugin.getRedLogger().debugStyled("        display-name: " + im.getDisplayName());
            if (im.hasLore()) {
                plugin.getRedLogger().debugStyled("        lore: [");
                for (String s : im.getLore()) {
                    plugin.getRedLogger().debugStyled("        " + s);
                }
                plugin.getRedLogger().debugStyled("        ]");
            }

            PersistentDataContainer pdc = im.getPersistentDataContainer();
            if (!pdc.getKeys().isEmpty()) {
                plugin.getRedLogger().debugStyled("        persistent-data (keys): [");
                for (NamespacedKey key : pdc.getKeys()) {
                    plugin.getRedLogger().debugStyled("            " + key.getKey());
                }
                plugin.getRedLogger().debugStyled("        ]");
            }

            plugin.getRedLogger().debugStyled("    ]");

        } else {
            plugin.getRedLogger().debugStyled("    meta: null");
        }
        plugin.getRedLogger().debugStyled("];");
    }
}
