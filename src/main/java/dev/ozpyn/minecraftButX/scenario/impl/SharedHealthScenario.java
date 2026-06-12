package dev.ozpyn.minecraftButX.scenario.impl;

import dev.ozpyn.minecraftButX.scenario.Scenario;
import dev.ozpyn.minecraftButX.scenario.ScenarioUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SharedHealthScenario implements Scenario, Listener {

    @Override
    public String getName() {
        return "sharedhealth";
    }

    @Override
    public String getDescription() {
        return "All players share the same health bar within their team";
    }

    @Override
    public void onEnable(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        if (!ScenarioUtils.hasTeams()) {
            ScenarioUtils.warnNoTeams(plugin, "shared health");
        }
    }

    @Override
    public void onDisable(JavaPlugin plugin) {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled() || !(event.getEntity() instanceof Player source)) return;

        var teammates = ScenarioUtils.getTeammates(source);
        if (teammates.isEmpty()) return;

        double newHealth = Math.max(0, source.getHealth() - event.getFinalDamage());
        for (Player teammate : teammates) {
            teammate.setHealth(newHealth);
        }
    }

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (event.isCancelled() || !(event.getEntity() instanceof Player source)) return;

        var teammates = ScenarioUtils.getTeammates(source);
        if (teammates.isEmpty()) return;

        double newHealth = Math.min(source.getMaxHealth(), source.getHealth() + event.getAmount());
        for (Player teammate : teammates) {
            teammate.setHealth(newHealth);
        }
    }
}
