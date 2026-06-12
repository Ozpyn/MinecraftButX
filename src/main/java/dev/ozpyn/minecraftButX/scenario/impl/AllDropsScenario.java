package dev.ozpyn.minecraftButX.scenario.impl;

import dev.ozpyn.minecraftButX.scenario.Scenario;
import dev.ozpyn.minecraftButX.scenario.ScenarioUtils;
import dev.ozpyn.minecraftButX.scenario.ScenarioUtils.DropMode;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class AllDropsScenario implements Scenario, Listener {

    private final String name;
    private final DropMode mode;
    private final Map<EntityType, ItemStack> mobDropTable;
    private final Map<Material, ItemStack> blockDropTable;

    public AllDropsScenario(String name, DropMode mode) {
        this.name = name;
        this.mode = mode;
        this.mobDropTable = mode == DropMode.DECIDED ? new HashMap<>() : null;
        this.blockDropTable = mode == DropMode.DECIDED ? new HashMap<>() : null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return mode == DropMode.DECIDED
            ? "All mob and block drops are randomized per type on first drop"
            : "All mob and block drops are randomized every time";
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
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) return;

        event.getDrops().clear();

        if (mode == DropMode.DECIDED) {
            ItemStack decided = mobDropTable.computeIfAbsent(event.getEntityType(), k -> ScenarioUtils.randomItemStack());
            event.getDrops().add(decided.clone());
        } else {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int count = random.nextInt(1, 4);
            for (int i = 0; i < count; i++) {
                event.getDrops().add(ScenarioUtils.randomItemStack());
            }
        }
    }

    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent event) {
        event.getItems().clear();
        Location loc = event.getBlock().getLocation().add(0.5, 0.5, 0.5);

        if (mode == DropMode.DECIDED) {
            ItemStack decided = blockDropTable.computeIfAbsent(event.getBlock().getType(), k -> ScenarioUtils.randomItemStack());
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
