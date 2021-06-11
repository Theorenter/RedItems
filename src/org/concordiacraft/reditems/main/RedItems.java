package org.concordiacraft.reditems.main;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.concordiacraft.reditems.config.ConfigDefault;
import org.concordiacraft.reditems.items.ItemManager;
import org.concordiacraft.reditems.listeners.CustomItemDurability;
import org.concordiacraft.redutils.main.RedPlugin;
import org.concordiacraft.redutils.utils.RedLog;

import java.io.File;

/**
 * @author Theorenter
 * Main class.
 */
public class RedItems extends JavaPlugin implements RedPlugin {

    // Fields
    private static boolean debug = false;
    private static RedLog redLog;
    private ConfigDefault config;

    @Override
    public void onEnable() {
        checkRedUtils();

        // Creating a new log object
        redLog = new RedLog(this);

        // Config loading
        this.config = new ConfigDefault(this, "settings" + File.separator + "config.yml");

        redLog.showPluginTitle();


        // Load files
        ItemManager.init(this);

        // Listeners
        Bukkit.getPluginManager().registerEvents(new CustomItemDurability(this), this);
        //Bukkit.getPluginManager().registerEvents(new CustomRecipesDiscovering(this), this);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean isDebug() { return debug; }

    @Override
    public RedLog getRedLogger() {
        return redLog;
    }

    @Override
    public void setDebug(boolean debugStatus) { debug = debugStatus; }

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
