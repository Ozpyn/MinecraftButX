package dev.ozpyn.minecraftButX.scenario;

import dev.ozpyn.minecraftButX.scenario.impl.SharedInventoryScenario;
import java.util.HashSet;
import java.util.Set;

public class SharedInventoryBuilder {

    private String name = "sharedinventory";
    private String description = "Selected inventory slots are shared among all players";
    private boolean disableOnDamage = false;
    private final Set<Integer> slots = new HashSet<>();

    public SharedInventoryBuilder name(String name) {
        this.name = name;
        return this;
    }

    public SharedInventoryBuilder description(String description) {
        this.description = description;
        return this;
    }

    public SharedInventoryBuilder slot(int slot) {
        slots.add(slot);
        return this;
    }

    public SharedInventoryBuilder slots(int... slotArray) {
        for (int s : slotArray) slots.add(s);
        return this;
    }

    public SharedInventoryBuilder armor() {
        return slots(36, 37, 38, 39);
    }

    public SharedInventoryBuilder hotbar() {
        for (int i = 0; i < 9; i++) slots.add(i);
        return this;
    }

    public SharedInventoryBuilder offhand() {
        return slot(40);
    }

    public SharedInventoryBuilder inventory() {
        for (int i = 0; i < 36; i++) slots.add(i);
        return this;
    }

    public SharedInventoryBuilder damagedisable() {
        for (int i = 0; i < 36; i++) slots.add(i);
        return this;
    }

    public SharedInventoryBuilder disableOnDamage() {
        this.disableOnDamage = true;
        return this;
    }

    public SharedInventoryScenario build() {
        return new SharedInventoryScenario(name, description, Set.copyOf(slots), disableOnDamage);
    }

    public static SharedInventoryScenario sharedArmour() {
        return new SharedInventoryBuilder()
            .name("sharedarmour")
            .description("All players share armor slots")
            .armor()
            .build();
    }

    public static SharedInventoryScenario sharedHotbar() {
        return new SharedInventoryBuilder()
            .name("sharedhotbar")
            .description("All players share the hotbar")
            .hotbar()
            .build();
    }

    public static SharedInventoryScenario sharedOffhand() {
        return new SharedInventoryBuilder()
            .name("sharedoffhand")
            .description("All players share the offhand slot")
            .offhand()
            .build();
    }

    public static SharedInventoryScenario sharedInventory() {
        return new SharedInventoryBuilder()
            .name("sharedinventory")
            .description("All players share the full inventory")
            .inventory()
            .build();
    }

    public static SharedInventoryScenario disableSlotOnDamage() {
        return new SharedInventoryBuilder()
                .disableOnDamage()
                .name("disableslotondamage")
                .description("Randomly disables an inventory slot when a player takes damage")
                .damagedisable()
                .build();
    }
}
