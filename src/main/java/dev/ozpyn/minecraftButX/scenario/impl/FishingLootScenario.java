package dev.ozpyn.minecraftButX.scenario.impl;

import dev.ozpyn.minecraftButX.scenario.Scenario;
import dev.ozpyn.minecraftButX.scenario.ScenarioUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class FishingLootScenario implements Scenario, Listener {

    @Override
    public String getName() {
        return "fishingloot";
    }

    @Override
    public String getDescription() {
        return "Fishing loot is randomized";
    }

    @Override
    public void onEnable(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void onDisable(JavaPlugin plugin) {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;

        Entity caught = event.getCaught();
        if (caught instanceof Item item) {
            item.setItemStack(ScenarioUtils.randomItemStack());
        }
    }
}
