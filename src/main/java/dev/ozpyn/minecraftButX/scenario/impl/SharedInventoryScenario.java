package dev.ozpyn.minecraftButX.scenario.impl;

import dev.ozpyn.minecraftButX.scenario.Scenario;
import dev.ozpyn.minecraftButX.scenario.ScenarioUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;

public class SharedInventoryScenario implements Scenario {

    private final String name;
    private final String description;
    private final Set<Integer> sharedSlots;
    private JavaPlugin plugin;
    private Map<String, ItemStack> snapshot;
    private BukkitTask task;

    public SharedInventoryScenario(String name, String description, Set<Integer> sharedSlots) {
        this.name = name;
        this.description = description;
        this.sharedSlots = sharedSlots;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void onEnable(JavaPlugin plugin) {
        this.plugin = plugin;
        snapshot = new HashMap<>();
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::sync, 0L, 1L);

        if (!ScenarioUtils.hasTeams()) {
            ScenarioUtils.warnNoTeams(plugin, name);
        }
    }

    @Override
    public void onDisable(JavaPlugin plugin) {
        if (task != null) {
            task.cancel();
            task = null;
        }
        snapshot = null;
    }

    private void sync() {
        Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);
        if (players.length < 2) return;

        Map<Team, List<Player>> teams = new HashMap<>();
        for (Player player : players) {
            Team team = ScenarioUtils.getPlayerTeam(player);
            if (team != null) {
                teams.computeIfAbsent(team, k -> new ArrayList<>()).add(player);
            }
        }

        for (Map.Entry<Team, List<Player>> entry : teams.entrySet()) {
            List<Player> members = entry.getValue();
            if (members.size() < 2) continue;

            for (int slot : sharedSlots) {
                String snapKey = entry.getKey().getName() + ":" + slot;
                ItemStack expected = snapshot.get(snapKey);

                Player changer = null;
                ItemStack newItem = null;
                for (Player player : members) {
                    ItemStack current = player.getInventory().getItem(slot);
                    if (!itemsEqual(current, expected)) {
                        changer = player;
                        newItem = current != null ? current.clone() : null;
                        break;
                    }
                }

                if (changer != null) {
                    for (Player player : members) {
                        if (player != changer) {
                            player.getInventory().setItem(slot, newItem != null ? newItem.clone() : null);
                        }
                    }
                    snapshot.put(snapKey, newItem);
                }
            }
        }
    }

    private static boolean itemsEqual(ItemStack a, ItemStack b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.getType() == b.getType() && a.getAmount() == b.getAmount();
    }
}
