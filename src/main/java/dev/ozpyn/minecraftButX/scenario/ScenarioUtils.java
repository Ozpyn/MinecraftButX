package dev.ozpyn.minecraftButX.scenario;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class ScenarioUtils {

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
}
