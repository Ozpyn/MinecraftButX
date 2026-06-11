package dev.ozpyn.minecraftButX.command;

import dev.ozpyn.minecraftButX.scenario.ScenarioManager;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DisableCommand implements CommandExecutor, TabCompleter {

    private final ScenarioManager manager;

    public DisableCommand(ScenarioManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Component.text("Usage: /disable <scenario>").color(NamedTextColor.RED));
            return true;
        }
        String name = args[0].toLowerCase();
        if (manager.disable(name)) {
            sender.sendMessage(Component.text("Disabled scenario: " + name).color(NamedTextColor.GREEN));
        } else if (!manager.exists(name)) {
            sender.sendMessage(Component.text("Scenario not found: " + name).color(NamedTextColor.RED));
        } else {
            sender.sendMessage(Component.text("Scenario not active: " + name).color(NamedTextColor.RED));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return manager.getActive().stream()
                .filter(s -> s.startsWith(args[0].toLowerCase()))
                .toList();
        }
        return List.of();
    }
}
