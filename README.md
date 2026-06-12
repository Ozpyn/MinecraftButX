# MinecraftButX

A PaperMC plugin that lets server admins toggle chaotic gameplay modifications called *scenarios*. Each scenario changes a specific game mechanic, and multiple scenarios can be active at once.

## Building

Requires JDK 26.

```bash
./gradlew build
```

To run a test server:

```bash
./gradlew runServer
```

## Usage

| Command | Description |
|---|---|
| `/enable <scenario>` | Enable a scenario |
| `/disable <scenario>` | Disable an active scenario |

Tab-completion shows available vs. active scenarios.

## Scenarios

### Drops

| Scenario | Description |
|---|---|
| `mobdrops` | Mob drops are randomized every time |
| `mobdropsdecided` | Each mob type drops the same random item (decided on first kill) |
| `blockdrops` | Block drops are randomized every time |
| `blockdropsdecided` | Each block type drops the same random item (decided on first break) |
| `alldrops` | Both mob and block drops are randomized every time |
| `alldropsdecided` | Each source drops the same random item (decided on first drop) |

### Fishing

| Scenario | Description |
|---|---|
| `fishingloot` | Fishing catches are randomized |

### Shared (team-based)

These scenarios require players to be in a Minecraft team (`/team add`, `/team join`). Effects are scoped per team — only teammates sync with each other.

Note: scenarios are server-wide. Once enabled, the event listeners are active for everyone; the team check only limits *who syncs with whom*. If team A enables `sharedhunger`, team B's hunger also syncs among its own members (as long as they're in a team). Per-team toggling is not currently supported.

| Scenario | Description |
|---|---|
| `sharedhunger` | All teammates share the same hunger bar |
| `sharedhealth` | All teammates share the same health bar |
| `sharedeffects` | All teammates share the same potion effects |
| `sharedarmour` | All teammates share armor slots |
| `sharedhotbar` | All teammates share the hotbar |
| `sharedoffhand` | All teammates share the offhand slot |
| `sharedinventory` | All teammates share the full inventory |

## Adding a new scenario

Create a class in `src/main/java/dev/ozpyn/minecraftButX/scenario/impl/` that implements the `Scenario` interface (and optionally `Listener` for events), then register it in `MinecraftButX.java`:

```java
scenarioManager.register(new YourScenario());
```
