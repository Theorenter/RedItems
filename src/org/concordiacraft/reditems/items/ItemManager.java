package org.concordiacraft.reditems.items;

import org.concordiacraft.reditems.main.RedItems;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ItemManager {
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
        for (File customItemFile : customItemsFile.listFiles()) { new CustomItem(plugin, customItemFile); }
    }
}
