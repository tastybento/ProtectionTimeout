package com.wasteofplastic.protectiontimeout;


import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.wasteofplastic.askyblock.ASkyBlock;


public class ProtectionTimeout extends JavaPlugin {
    private ASkyBlock asb;

    @Override
    public void onEnable() {
        // Enable the plugin
        PluginManager manager = getServer().getPluginManager();
        // Check for ASkyBlock
        asb = (ASkyBlock) manager.getPlugin("ASkyBlock");

        if (asb == null) {
            getLogger().severe("ASkyBlock not loaded. Disabling plugin");
            getServer().getPluginManager().disablePlugin(this);
        } else {
            getLogger().info("Using ASkyBlock version " + asb.getDescription().getVersion());
            // Load config
            saveDefaultConfig();
            // Load from config
            reloadConfig();
                       
            // Register the listener to blocks being broken by players
            manager.registerEvents(new NewIslandListener(this), this);
        }

    }

    @Override
    public void onDisable() {
        saveConfig();
    }



    /**
     * @return the asb
     */
    public ASkyBlock getAsb() {
        return asb;
    }

}
