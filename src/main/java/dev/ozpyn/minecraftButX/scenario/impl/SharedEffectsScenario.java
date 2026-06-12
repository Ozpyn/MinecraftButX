package dev.ozpyn.minecraftButX.scenario.impl;

import dev.ozpyn.minecraftButX.scenario.Scenario;
import dev.ozpyn.minecraftButX.scenario.ScenarioUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.plugin.java.JavaPlugin;

public class SharedEffectsScenario implements Scenario, Listener {

    private boolean propagating;

    @Override
    public String getName() {
        return "sharedeffects";
    }

    @Override
    public String getDescription() {
        return "All players share the same status effects within their team";
    }

    @Override
    public void onEnable(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        if (!ScenarioUtils.hasTeams()) {
            ScenarioUtils.warnNoTeams(plugin, "shared effects");
        }
    }

    @Override
    public void onDisable(JavaPlugin plugin) {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if (event.isCancelled() || !(event.getEntity() instanceof Player source)) return;
        if (propagating) return;

        var teammates = ScenarioUtils.getTeammates(source);
        if (teammates.isEmpty()) return;

        propagating = true;
        try {
            PotionEffect newEffect = event.getNewEffect();
            PotionEffect oldEffect = event.getOldEffect();

            for (Player teammate : teammates) {
                switch (event.getAction()) {
                    case ADDED, CHANGED -> {
                        if (newEffect != null) {
                            teammate.addPotionEffect(newEffect);
                        }
                    }
                    case REMOVED -> {
                        if (oldEffect != null) {
                            teammate.removePotionEffect(oldEffect.getType());
                        }
                    }
                    case CLEARED -> {
                        for (PotionEffect effect : teammate.getActivePotionEffects()) {
                            teammate.removePotionEffect(effect.getType());
                        }
                    }
                }
            }
        } finally {
            propagating = false;
        }
    }
}
