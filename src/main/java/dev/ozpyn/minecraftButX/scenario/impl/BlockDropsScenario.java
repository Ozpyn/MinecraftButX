package dev.ozpyn.minecraftButX.scenario.impl;

import dev.ozpyn.minecraftButX.scenario.Scenario;
import dev.ozpyn.minecraftButX.scenario.ScenarioUtils;
import dev.ozpyn.minecraftButX.scenario.ScenarioUtils.DropMode;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockDropsScenario implements Scenario, Listener {

    private final String name;
    private final DropMode mode;
    private final Map<Material, ItemStack> dropTable;

    public BlockDropsScenario(String name, DropMode mode) {
        this.name = name;
        this.mode = mode;
        this.dropTable = mode == DropMode.DECIDED ? new HashMap<>() : null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return mode == DropMode.DECIDED
            ? "Each block type drops a random item decided on first break"
            : "All block drops are randomized every time";
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
    public void onBlockDropItem(BlockDropItemEvent event) {
        event.getItems().clear();
        Location loc = event.getBlock().getLocation().add(0.5, 0.5, 0.5);

        if (mode == DropMode.DECIDED) {
            ItemStack decided = dropTable.computeIfAbsent(event.getBlock().getType(), k -> ScenarioUtils.randomItemStack());
            event.getBlock().getWorld().dropItemNaturally(loc, decided.clone());
        } else {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int count = random.nextInt(1, 4);
            for (int i = 0; i < count; i++) {
                ItemStack stack = ScenarioUtils.randomItemStack();
                event.getBlock().getWorld().dropItemNaturally(loc, stack);
            }
        }
    }
}
