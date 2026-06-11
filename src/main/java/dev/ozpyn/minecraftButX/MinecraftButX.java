package dev.ozpyn.minecraftButX;

import org.bukkit.plugin.java.JavaPlugin;

public final class MinecraftButX extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("MinecraftButX started!")
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        // Shutdown each mode before stopping
        getLogger().info("MinecraftButX stopped!")
    }
}
