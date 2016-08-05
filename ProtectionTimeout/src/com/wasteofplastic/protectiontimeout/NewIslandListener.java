package com.wasteofplastic.protectiontimeout;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.wasteofplastic.askyblock.Island;
import com.wasteofplastic.askyblock.Island.Flags;
import com.wasteofplastic.askyblock.events.IslandNewEvent;
import com.wasteofplastic.askyblock.events.IslandResetEvent;

public class NewIslandListener implements Listener {

    private final ProtectionTimeout plugin;
    private HashMap<UUID, Long> timeouts = new HashMap<UUID, Long>();

    /**
     * @param plugin
     * @param topTenLocation
     * @param direction
     */
    public NewIslandListener(final ProtectionTimeout plugin) {
        this.plugin = plugin;
        loadTimeouts();
        // Run a repeating task to check if the timeouts have been hit
        plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {

            public void run() {
                //plugin.getLogger().info("Running repeating task");
                Iterator<Entry<UUID, Long>> it = timeouts.entrySet().iterator();
                boolean updated = false;
                while (it.hasNext()) {
                    Entry<UUID, Long> en = it.next();                 
                    if (en.getValue() < System.currentTimeMillis()) {
                        // Timeout
                        plugin.getLogger().info("Protection timeout for " + plugin.getServer().getOfflinePlayer(en.getKey()).getName());
                        Island island = plugin.getAsb().getGrid().getIsland(en.getKey());
                        if (island != null) {
                            for (String flag : plugin.getConfig().getConfigurationSection("flags").getKeys(false)) {
                                // Check if the flag exists in the enum
                                try {
                                    Flags protectionFlag = Flags.valueOf(flag);
                                    island.setIgsFlag(protectionFlag, plugin.getConfig().getBoolean("flags." + flag));
                                    //plugin.getLogger().info("DEBUG: setting " + protectionFlag.toString() + " = " + plugin.getConfig().getBoolean("flags." + flag));
                                } catch (Exception e) {
                                    plugin.getLogger().severe("Flag " + flag + " is unknown - skipping");
                                }
                            }
                            plugin.getLogger().info("Reset protections.");
                        } else {
                            plugin.getLogger().severe(plugin.getServer().getOfflinePlayer(en.getKey()).getName() + " does not have an island!");
                        }
                        // Remove from config
                        plugin.getConfig().set("timeouts." + en.getKey().toString(), null);
                        updated = true;
                        it.remove();
                    } else {
                        //plugin.getLogger().info("DEBUG: " + (en.getValue() - System.currentTimeMillis())/1000);
                    }
                }
                // Save if a change was made
                if (updated) {
                    plugin.saveConfig();
                }
            }}, 120L, 20L);
    }

    private void loadTimeouts() {
        plugin.getLogger().info("Loading timeouts");
        timeouts.clear();
        if (plugin.getConfig().isConfigurationSection("timeouts")) {
            for (String uuid : plugin.getConfig().getConfigurationSection("timeouts").getKeys(false)) {
                try {
                    UUID playerUuid = UUID.fromString(uuid);
                    timeouts.put(playerUuid, plugin.getConfig().getLong("timeouts." + uuid));
                } catch (Exception e) {
                    plugin.getLogger().severe("Could not parse UUID " + uuid + " skipping!");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onNewIsland(final IslandNewEvent event) {
        // Set timeout
        long timeout = System.currentTimeMillis() + plugin.getConfig().getInt("timeout", 600) * 1000;
        timeouts.put(event.getPlayer().getUniqueId(), timeout);
        plugin.getLogger().info("New island for " + event.getPlayer().getName() + " - timeout set");
        plugin.getConfig().set("timeouts." + event.getPlayer().getUniqueId().toString(), timeout);
        plugin.saveConfig();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onNewIsland(final IslandResetEvent event) {
        // Set timeout
        long timeout = System.currentTimeMillis() + plugin.getConfig().getInt("timeout", 600) * 1000;
        timeouts.put(event.getPlayer().getUniqueId(), timeout);
        plugin.getLogger().info("Reset island for " + event.getPlayer().getName() + " - timeout set");
        plugin.getConfig().set("timeouts." + event.getPlayer().getUniqueId().toString(), timeout);
        plugin.saveConfig();
    }

}
