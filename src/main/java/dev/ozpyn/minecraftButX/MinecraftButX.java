package dev.ozpyn.minecraftButX;

import dev.ozpyn.minecraftButX.command.DisableCommand;
import dev.ozpyn.minecraftButX.command.EnableCommand;
import dev.ozpyn.minecraftButX.scenario.ScenarioManager;
import dev.ozpyn.minecraftButX.scenario.impl.AllDropsScenario;
import dev.ozpyn.minecraftButX.scenario.impl.BlockDropsScenario;
import dev.ozpyn.minecraftButX.scenario.impl.ChestLootScenario;
import dev.ozpyn.minecraftButX.scenario.impl.FishingLootScenario;
import dev.ozpyn.minecraftButX.scenario.impl.MobDropsScenario;
import dev.ozpyn.minecraftButX.scenario.impl.NoFallDamageScenario;
import dev.ozpyn.minecraftButX.scenario.impl.NoHungerScenario;
import dev.ozpyn.minecraftButX.scenario.impl.SharedEffectsScenario;
import dev.ozpyn.minecraftButX.scenario.impl.SharedHealthScenario;
import dev.ozpyn.minecraftButX.scenario.impl.SharedHungerScenario;
import org.bukkit.plugin.java.JavaPlugin;

public final class MinecraftButX extends JavaPlugin {

    private ScenarioManager scenarioManager;

    @Override
    public void onEnable() {
        this.scenarioManager = new ScenarioManager(this);

        scenarioManager.register(new NoFallDamageScenario());
        scenarioManager.register(new NoHungerScenario());
        scenarioManager.register(new MobDropsScenario());
        scenarioManager.register(new BlockDropsScenario());
        scenarioManager.register(new AllDropsScenario());
        scenarioManager.register(new ChestLootScenario());
        scenarioManager.register(new FishingLootScenario());
        scenarioManager.register(new SharedHungerScenario());
        scenarioManager.register(new SharedHealthScenario());
        scenarioManager.register(new SharedEffectsScenario());

        var enableCommand = getCommand("enable");
        if (enableCommand != null) {
            var executor = new EnableCommand(scenarioManager);
            enableCommand.setExecutor(executor);
            enableCommand.setTabCompleter(executor);
        }

        var disableCommand = getCommand("disable");
        if (disableCommand != null) {
            var executor = new DisableCommand(scenarioManager);
            disableCommand.setExecutor(executor);
            disableCommand.setTabCompleter(executor);
        }

        getLogger().info("MinecraftButX started!");
    }

    @Override
    public void onDisable() {
        if (scenarioManager != null) {
            scenarioManager.disableAll();
        }
        getLogger().info("MinecraftButX stopped!");
    }
}
