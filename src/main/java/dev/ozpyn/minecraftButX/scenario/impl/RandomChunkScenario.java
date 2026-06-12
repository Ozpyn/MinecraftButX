package dev.ozpyn.minecraftButX.scenario.impl;

import dev.ozpyn.minecraftButX.scenario.Scenario;
import dev.ozpyn.minecraftButX.scenario.ScenarioUtils;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class RandomChunkScenario implements Scenario, Listener {

    private final Set<Material> protectedBlocks = EnumSet.of(
            Material.BEDROCK,
            Material.AIR,
            Material.CAVE_AIR,
            Material.VOID_AIR,
            Material.WATER,
            Material.LAVA,
            Material.CHEST,
            Material.TRAPPED_CHEST,
            Material.ENDER_CHEST,
            Material.BARREL,
            Material.SHULKER_BOX,
            Material.SPAWNER,
            Material.END_PORTAL,
            Material.END_PORTAL_FRAME,
            Material.NETHER_PORTAL,
            Material.OBSIDIAN
    );

    @Override
    public String getName() {
        return "randomchunks";
    }

    @Override
    public String getDescription() {
        return "Every time a player enters a new chunk, all (non-protected) blocks "
                + "in that chunk are overwritten with the same random block";
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
    public void onPlayerMove(PlayerMoveEvent event) {
        handleMovement(event.getPlayer(), event.getFrom(), event.getTo());
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        handleMovement(event.getPlayer(), event.getFrom(), event.getTo());
    }

    private void handleMovement(Player player, org.bukkit.Location from, org.bukkit.Location to) {
        if (to == null) return;

        int fromChunkX = from.getBlockX() >> 4;
        int fromChunkZ = from.getBlockZ() >> 4;
        int toChunkX = to.getBlockX() >> 4;
        int toChunkZ = to.getBlockZ() >> 4;

        if (fromChunkX == toChunkX && fromChunkZ == toChunkZ) {
            return;
        }

        overwriteChunk(to.getChunk());
    }

    private void overwriteChunk(Chunk chunk) {
        World world = chunk.getWorld();
        Material randomMaterial = pickRandomBlock();

        int baseX = chunk.getX() << 4;
        int baseZ = chunk.getZ() << 4;
        int minY = world.getMinHeight();
        int maxY = world.getMaxHeight();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = minY; y < maxY; y++) {
                    Block block = world.getBlockAt(baseX + x, y, baseZ + z);

                    if (protectedBlocks.contains(block.getType())) {
                        continue;
                    }

                    block.setType(randomMaterial, false);
                }
            }
        }
    }

    private Material pickRandomBlock() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Material[] all = Material.values();
        Material chosen;

        do {
            chosen = all[random.nextInt(all.length)];
        } while (!chosen.isBlock()
                || chosen.isAir()
                || chosen == Material.BEDROCK
                || protectedBlocks.contains(chosen));

        return chosen;
    }
}