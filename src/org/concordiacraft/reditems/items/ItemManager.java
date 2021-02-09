package org.concordiacraft.reditems.items;

import org.bukkit.inventory.ItemStack;
import org.concordiacraft.reditems.main.RedItems;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ItemManager {

    // Fields
    private static HashMap<String, CustomItem> customItemList = new HashMap<>();
    private static ArrayList<ItemStack> customItemStackList = new ArrayList<>();

    public static void itemLoadFromResources(RedItems plugin) {
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
    public static void itemLoad(RedItems plugin) {
        File customItemsFile = new File(plugin.getDataFolder() + File.separator + "settings" + File.separator + "content" + File.separator + "items");
        if (customItemsFile.listFiles().length == 0) { return; }
        for (File customItemFile : customItemsFile.listFiles())
        {
            // Validation is required to prevent the user item from being re-created.
            String customFileName = customItemFile.getName().replace(".json", "");
            if (!customItemList.containsKey(customFileName)) new CustomItem(plugin, customItemFile);
        }
    }
    // Getters & Setters
    public static ArrayList<ItemStack> getCustomItemStackList() { return customItemStackList; }

    public static HashMap<String, CustomItem> getCustomItemList() { return customItemList; }

    public static boolean isCustomItem(ItemStack itemStack) {
        return customItemStackList.contains(itemStack);
    }

    public static CustomItem getCustomItem(String ID) {
        return customItemList.get(ID);
    }
}
