package org.concordiacraft.reditems.main;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.concordiacraft.reditems.config.ConfigDefault;
import org.concordiacraft.reditems.items.ItemManager;
import org.concordiacraft.reditems.listeners.CustomItemDurability;
import org.concordiacraft.redutils.main.utils.RedLog;
import org.concordiacraft.redutils.main.RedPlugin;

/**
 * @author Theorenter
 * Main class.
 */
public class RedItems extends JavaPlugin implements RedPlugin {

    // Fields
    private static Boolean isDebug;
    private static RedLog rLog;
    private ConfigDefault config;

    @Override
    public void onEnable() {
        checkRedUtils();

        // Config loading
        this.config = new ConfigDefault(this, "settings/config.yml");
        isDebug = (Boolean) ConfigDefault.getCustomConfig().get("plugin.debug");

        // Creating a new log object
        rLog = new RedLog(this);

        rLog.showPluginTitle();


        // Load files
        ItemManager.itemLoadFromResources(this);
        ItemManager.itemLoad(this);

        // Listeners
        Bukkit.getPluginManager().registerEvents(new CustomItemDurability(this), this);
        //Bukkit.getPluginManager().registerEvents(new CustomRecipesDiscovering(this), this);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean isDebug() {
        return isDebug;
    }

    @Override
    public RedLog getRedLogger() {
        return rLog;
    }

    public static RedItems getPlugin() {
        return RedItems.getPlugin(RedItems.class);
    }

    public ConfigDefault getDefaultConfig() { return this.config; }

    private void checkRedUtils() {
        Plugin redUtils = Bukkit.getPluginManager().getPlugin("RedUtils");
        if (redUtils == null || !redUtils.isEnabled()) {
            this.getLogger().severe("To run this plugin, you need to have the RedUtils plugin!");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

}
