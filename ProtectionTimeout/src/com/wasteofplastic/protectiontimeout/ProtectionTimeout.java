/*******************************************************************************
 * This file is part of ProtectionTimeout.
 *
 *     ProtectionTimeout is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     ProtectionTimeout is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with ProtectionTimeout.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
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
