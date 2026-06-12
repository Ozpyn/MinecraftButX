package dev.ozpyn.minecraftButX.scenario.impl;

import dev.ozpyn.minecraftButX.scenario.Scenario;
import dev.ozpyn.minecraftButX.scenario.ScenarioUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SharedHungerScenario implements Scenario, Listener {

    @Override
    public String getName() {
        return "sharedhunger";
    }

    @Override
    public String getDescription() {
        return "All players share the same hunger bar within their team";
    }

    @Override
    public void onEnable(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        if (!ScenarioUtils.hasTeams()) {
            ScenarioUtils.warnNoTeams(plugin, "shared hunger");
        }
    }

    @Override
    public void onDisable(JavaPlugin plugin) {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player source)) return;

        var teammates = ScenarioUtils.getTeammates(source);
        if (teammates.isEmpty()) return;

        int newFood = event.getFoodLevel();
        for (Player teammate : teammates) {
            teammate.setFoodLevel(newFood);
            teammate.setSaturation(source.getSaturation());
            teammate.setExhaustion(source.getExhaustion());
        }
    }
}
