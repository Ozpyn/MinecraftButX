package dev.ozpyn.minecraftButX.scenario;

import org.bukkit.plugin.java.JavaPlugin;

public interface Scenario {
    String getName();
    String getDescription();
    default void onEnable(JavaPlugin plugin) {}
    default void onDisable(JavaPlugin plugin) {}
}
