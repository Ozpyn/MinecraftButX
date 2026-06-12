package dev.ozpyn.minecraftButX.scenario.impl;

import dev.ozpyn.minecraftButX.scenario.Scenario;
import dev.ozpyn.minecraftButX.scenario.ScenarioUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;

public class SharedInventoryScenario implements Scenario, Listener {

    private final String name;
    private final String description;
    private final Set<Integer> sharedSlots;
    private final boolean disableOnDamage;
    private final Map<UUID, Set<Integer>> disabledSlots;
    private final Random random;
    private JavaPlugin plugin;
    private Map<String, ItemStack> snapshot;
    private BukkitTask task;

    public SharedInventoryScenario(String name, String description, Set<Integer> sharedSlots) {
        this(name, description, sharedSlots, false);
    }

    public SharedInventoryScenario(String name, String description, Set<Integer> sharedSlots, boolean disableOnDamage) {
        this.name = name;
        this.description = description;
        this.sharedSlots = sharedSlots;
        this.disableOnDamage = disableOnDamage;
        this.disabledSlots = new HashMap<>();
        this.random = new Random();
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
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
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

        if (disableOnDamage) {
            for (Map.Entry<UUID, Set<Integer>> entry : disabledSlots.entrySet()) {
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player != null && player.isOnline()) {
                    for (int slot : entry.getValue()) {
                        player.getInventory().setItem(slot, null);
                    }
                }
            }
            disabledSlots.clear();
        }

        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!disableOnDamage) return;
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        List<Player> team = new ArrayList<>();
        team.add(player);
        team.addAll(ScenarioUtils.getTeammates(player));

        Set<Integer> playerDisabled = disabledSlots.get(player.getUniqueId());

        List<Integer> validSlots = new ArrayList<>();
        for (int slot : sharedSlots) {
            if (playerDisabled != null && playerDisabled.contains(slot)) continue;
            validSlots.add(slot);
        }

        if (validSlots.isEmpty()) return;

        int chosenSlot = validSlots.get(random.nextInt(validSlots.size()));

        for (Player target : team) {
            Set<Integer> targetDisabled = disabledSlots.computeIfAbsent(target.getUniqueId(), k -> new HashSet<>());
            if (targetDisabled.contains(chosenSlot)) continue;

            target.getInventory().setItem(chosenSlot, createDisabledBarrier());
            targetDisabled.add(chosenSlot);
        }
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

        if (disableOnDamage) {
            restoreBarriers();
        }
    }

    private void restoreBarriers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Set<Integer> playerDisabled = disabledSlots.get(player.getUniqueId());

            for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
                if (playerDisabled != null && playerDisabled.contains(slot)) continue;
                if (isDisabledBarrier(player.getInventory().getItem(slot))) {
                    player.getInventory().setItem(slot, null);
                }
            }

            if (isDisabledBarrier(player.getOpenInventory().getCursor())) {
                player.getOpenInventory().setCursor(null);
            }

            if (playerDisabled == null || playerDisabled.isEmpty()) continue;
            for (int slot : playerDisabled) {
                if (!isDisabledBarrier(player.getInventory().getItem(slot))) {
                    player.getInventory().setItem(slot, createDisabledBarrier());
                }
            }
        }
    }

    private static ItemStack createDisabledBarrier() {
        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta meta = barrier.getItemMeta();
        meta.setDisplayName("§cDisabled");
        barrier.setItemMeta(meta);
        return barrier;
    }

    private static boolean isDisabledBarrier(ItemStack item) {
        if (item == null || item.getType() != Material.BARRIER) return false;
        if (!item.hasItemMeta()) return false;
        return "§cDisabled".equals(item.getItemMeta().getDisplayName());
    }

    private static boolean itemsEqual(ItemStack a, ItemStack b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.getType() == b.getType() && a.getAmount() == b.getAmount();
    }
}
