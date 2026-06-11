package dev.ozpyn.minecraftButX.scenario;

import java.util.*;
import org.bukkit.plugin.java.JavaPlugin;

public class ScenarioManager {

    private final Map<String, Scenario> scenarios = new LinkedHashMap<>();
    private final Set<String> active = new HashSet<>();
    private final JavaPlugin plugin;

    public ScenarioManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void register(Scenario scenario) {
        scenarios.put(scenario.getName().toLowerCase(), scenario);
    }

    public boolean enable(String name) {
        Scenario scenario = scenarios.get(name.toLowerCase());
        if (scenario == null || active.contains(name.toLowerCase())) {
            return false;
        }
        active.add(name.toLowerCase());
        scenario.onEnable(plugin);
        return true;
    }

    public boolean disable(String name) {
        Scenario scenario = scenarios.get(name.toLowerCase());
        if (scenario == null || !active.contains(name.toLowerCase())) {
            return false;
        }
        active.remove(name.toLowerCase());
        scenario.onDisable(plugin);
        return true;
    }

    public boolean isActive(String name) {
        return active.contains(name.toLowerCase());
    }

    public boolean exists(String name) {
        return scenarios.containsKey(name.toLowerCase());
    }

    public Set<String> getActive() {
        return Collections.unmodifiableSet(active);
    }

    public Set<String> getInactive() {
        Set<String> inactive = new LinkedHashSet<>(scenarios.keySet());
        inactive.removeAll(active);
        return Collections.unmodifiableSet(inactive);
    }

    public void disableAll() {
        for (String name : List.copyOf(active)) {
            disable(name);
        }
    }
}
