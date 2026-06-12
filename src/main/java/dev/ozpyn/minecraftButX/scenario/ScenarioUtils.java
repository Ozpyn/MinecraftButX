package dev.ozpyn.minecraftButX.scenario;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

public final class ScenarioUtils {

    public enum DropMode {
        RANDOM,
        DECIDED
    }

    private static final List<Material> ITEMS = Arrays.stream(Material.values())
        .filter(Material::isItem)
        .filter(m -> m != Material.AIR)
        .toList();

    private ScenarioUtils() {}

    public static ItemStack randomItemStack() {
        return randomItemStack(1, 64);
    }

    public static ItemStack randomItemStack(int min, int max) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Material material = ITEMS.get(random.nextInt(ITEMS.size()));
        int amount = random.nextInt(min, max + 1);
        return new ItemStack(material, amount);
    }

    public static Material randomMaterial() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return ITEMS.get(random.nextInt(ITEMS.size()));
    }

    public static boolean hasTeams() {
        return !Bukkit.getScoreboardManager().getMainScoreboard().getTeams().isEmpty();
    }

    public static Team getPlayerTeam(Player player) {
        return Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName());
    }

    public static List<Player> getTeammates(Player player) {
        Team team = getPlayerTeam(player);
        if (team == null) return List.of();
        return Bukkit.getOnlinePlayers().stream()
            .filter(p -> !p.equals(player) && team.hasEntry(p.getName()))
            .collect(Collectors.toList());
    }

    public static void warnNoTeams(JavaPlugin plugin, String feature) {
        plugin.getLogger().warning("No Minecraft teams exist! Use /team add <name> and /team join <name> to create teams for " + feature + ".");
        Bukkit.getOnlinePlayers().stream()
            .filter(p -> p.hasPermission("minecraftbutx.admin") || p.isOp())
            .forEach(p -> p.sendMessage(
                Component.text("[MinecraftButX] Warning: No teams exist! Create a team with /team add <name> and /team join <name> for " + feature + " to work.")
                    .color(NamedTextColor.YELLOW)));
    }
}
