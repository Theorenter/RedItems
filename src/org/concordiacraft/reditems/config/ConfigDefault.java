package org.concordiacraft.reditems.config;

import org.concordiacraft.reditems.main.RedItems;
import org.concordiacraft.redutils.config.ExtendedRedConfig;

import java.util.ArrayList;
import java.util.List;

public final class ConfigDefault extends ExtendedRedConfig {

    private final boolean debug;
    private List<String> removedRecipes;

    public ConfigDefault(RedItems plugin, String YMLFileName) {
        super(plugin, YMLFileName);

        this.debug = customConfig.getBoolean("plugin.debug");
        this.removedRecipes = new ArrayList<>(customConfig.getStringList("recipes.removed"));


        customConfig = null;
    }

    public boolean isDebug() { return debug; }
    public List<String> getRemovedRecipes() { return removedRecipes; }
}
