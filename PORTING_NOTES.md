# CC: Androids 1.21.1 NeoForge Porting Notes

## Current status

- The mod builds against Minecraft 1.21.1, NeoForge 21.1.x and CC:Tweaked 1.120.0.
- `computercraft` is a required dependency in `neoforge.mods.toml`.
- Android entities, Android frames, items, spawn eggs, the creative tab, menu type, sounds and recipes are registered through NeoForge deferred registers.
- Androids own a persistent ComputerCraft `ServerComputer` through `AndroidComputerContainer` and `EntityComputer`.
- The Android computer receives `ComputerComponents.ANDROID_COMPUTER`, allowing the `android` Lua API factory to resolve the owning Android brain.
- Fuel, inventory, computer id, label, owner profile, lock state, variant and face are saved through entity NBT.
- Android frame construction can preserve a deconstructed Android CPU `ComputerID`.
- Android interaction now uses a CC:Tweaked/NeoForge fake player through `PlatformHelper.createFakePlayer` instead of passing a null player to block or entity interaction paths.
- `sendChatMessage` sends a prefixed system message to nearby players and logs the same message server-side.

## Tested in this work pass

- `./gradlew clean build` completed successfully.
- `./gradlew build` completed successfully after the P0 mining/container/wrench fixes in the current pass.
- Final `./gradlew build` completed successfully, and `timeout 60 ./gradlew runServer` reached `Done (...)!` with the new spawn/event registrations loaded before the timeout stopped it.
- `./gradlew runClient` started Minecraft 1.21.1 with NeoForge, CC:Tweaked and CC: Androids. The automated run was stopped by timeout after startup/world load, not by a mod crash.
- `timeout 60 ./gradlew runServer` started a dedicated NeoForge server with CC:Tweaked and CC: Androids, reached `Done (...)!`, then stopped through the timeout.
- The client log showed an integrated world loading and `RecipeManager` loading recipes without `cc_androids` datapack errors.
- No `cc_androids` sound JSON errors were reported in the client log. The remaining goat horn warnings are vanilla/Minecraft warnings.

Manual in-game interaction was not automated in this pass. Android spawning, terminal opening and Lua commands are supported by the code paths, but should still be smoke-tested by hand in a dev world.

## Lua API status

Implemented and expected to be usable:

- `android.currentTask()`
- `android.getSelf()`
- `android.attack(uuid)`
- `android.goTo(uuid)`
- `android.moveTo(x, y, z)` and `android.moveTo({ x = x, y = y, z = z })`
- `android.breakBlock(x, y, z)`
- `android.useBlock(x, y, z)`
- `android.useEntity(uuid)`
- `android.pickup(optionalItemType)`
- `android.dropItem()`
- `android.storeItem(index)`
- `android.equipSlot(index)`
- `android.swapHands()`
- `android.getHandInfo(handName)`
- `android.getSlotInfo(index)`
- `android.refuel(optionalAmount)`
- `android.fuelLevel()`
- `android.getClosestPlayer()`
- `android.getNearbyMobs(optionalType)`
- `android.getClosestMob(optionalType)`
- `android.getBlocksOfType(type)`
- `android.getContainerInfo(x, y, z)`
- `android.storeHeldItemInContainer(x, y, z, slot)`
- `android.grabItemFromContainer(x, y, z, slot)`
- `android.sendChatMessage(text)`
- `android.runCommand(command)` on Command Androids only
- `android.changeFace(faceName)`
- `android.cancelTask()`

Return values use `true, data` / `false, error` for mutating or fallible operations. Some read-only legacy helpers still return their direct value, such as `fuelLevel()` and `currentTask()`, to keep ROM programs simple.

The bundled ROM programs have been adjusted to the `ok, err` convention for `android.refuel()`.

## Simplified or incomplete systems

- Pocket upgrades and real peripheral bridging are not implemented. `DummyPocket` is only a safe placeholder.
- Modem/rednet support through hand items or pocket upgrades is not implemented.
- Mining runs through the CC:Tweaked/NeoForge fake player and `ServerPlayerGameMode#destroyBlock`, so it now follows the normal player break path for events, permissions, tools, enchantments and drops. Manual protection-mod smoke testing is still recommended.
- Container interaction now uses NeoForge `IItemHandler` block capabilities with side context and range checks. Manual smoke testing against both vanilla chests and capability-only modded inventories is still recommended.
- Fake-player interaction is the preferred path for block use, entity use and block breaking. If a future CC:Tweaked or NeoForge version changes `PlatformHelper.createFakePlayer`, this path must be retested.
- Components use the registered `cc_androids:wrench_shaped` serializer. Only that recipe returns the unchanged wrench; the item is no longer a global crafting remainder.
- Entity renderers select normal/advanced/command/rogue textures and render full-bright core, variant and face overlays. The GUI remains a minimal port.
- `android.sit()` is still a Create-seat stub returning `false, "Create seat support is not implemented in this build."`.

## CC:Tweaked shared-class risk

The port currently uses several `dan200.computercraft.shared.*` classes:

- `ServerComputer`
- `ServerContext`
- `ComputerContainerData`
- `AbstractComputerMenu`
- `PlatformHelper`

These are required for the current NeoForge integration approach but are less stable than the public API surface. Recheck this integration when updating CC:Tweaked beyond 1.120.x.

## Create support

Create is not a hard dependency and is not required to start the mod. Seat support is intentionally deferred until the base Android computer, task, fuel and GUI paths have been manually smoke-tested.

## Current pass P0 notes

- Mining now computes destroy progress with the CC:Tweaked/NeoForge fake player and finishes through `ServerPlayerGameMode#destroyBlock`, so NeoForge block break events, player permission checks, tool harvest rules, tool damage and loot/drop handling run through the vanilla player path. Unbreakable blocks and blocks with zero destroy progress are still ignored.
- Block and entity use now use the same fake player profile (`UUID` = Android entity UUID, name = `CCAndroids`) through `PlatformHelper`, with held-item changes copied back to the Android hand after the interaction.
- Container info, insert and extract paths now query NeoForge `Capabilities.ItemHandler.BLOCK` with a side derived from the Android's position relative to the block. This respects sided/capability inventories when a provider exposes them. Manual in-game validation against vanilla chests and modded capability-only inventories is still recommended.
- The components recipe now uses a dedicated shaped-recipe serializer and returns the exact wrench stack unchanged. Wrench durability is consumed only by Android deconstruction.

## Current pass P1 notes

- Rogue Android natural spawning uses the conventional `#c:is_cave` biome tag, weight 12, pack size 1, and an explicit local-light `< 4` placement predicate.
- The Rogue Android loot table already existed. Its current weights are CPU 1, components 50 and iron ingot 10 over three rolls, intentionally making components common and CPU rare.
- Command Android construction is now enforced in code: Android frames reject Command Block CPU insertion unless the player has creative/instabuild privileges.
- Command Androids are invulnerable to non-creative damage through both `hurt` and `isInvulnerableTo`.
- Command Androids expose `android.runCommand(command)` to Lua. It runs through the Android entity command source with permission level 4. This name is a port assumption; adjust bundled/user Lua scripts if they expected a different legacy name.
- Command Android CPU recovery was already represented by dropping a Command Block with the same `ComputerID` custom data used by Android CPUs. Re-inserting that Command Block into a frame reconstructs a Command Android with the preserved computer id.
- Advanced Android support was already present: gold ingots during frame construction set the advanced tier, which uses dedicated config values (`AdvAndroidMaxHealth`, `AdvAndroidDamage`, `AdvAndroidSpeed`, `AdvAndroidArmor`) and longer search ranges.
- `chat_message` events are now queued to active Android computers when a player sends chat within 50 blocks. Event arguments are `username`, `rawText`, and sender `uuid`.
- CPU recovery now covers normal, advanced and command tiers through the existing `ComputerFamily` and `ComputerID` custom-data path. Advanced CPUs use the normal Android CPU item plus gold-tier frame state; Command CPUs use a Command Block.
- Component crafting uses `cc_androids:wrench_shaped`; the serializer returns the wrench without damage.

## Current pass P2/P3 notes

- Pocket upgrades/peripheral bridging and modem/rednet remain deferred. `DummyPocket` is still only a placeholder.
- `android.sit()` remains a Create-seat stub because Create is not present as a project dependency.
- Renderer variants for normal/advanced/command/rogue and two dye variants are active. Full-bright core, dye-variant, rogue and dynamic face overlays are wired through a dedicated render layer.
- Additional shared CC:Tweaked classes now in use: `ComputerFamily` and `PlatformHelper` are referenced outside the computer container as part of tier handling and fake-player integration. Prefer replacing `PlatformHelper` only if CC:Tweaked exposes a stable public fake-player/interact helper in a future version.

## Status 2026-07-09

- **P0.1 blocked (manual interaction):** `./gradlew compileJava` succeeds and `runClient` reaches the Minecraft main menu without a mod crash. This environment cannot drive mouse/keyboard input in the game window, so Frame construction, terminal opening and `print(android.fuelLevel())` are not claimed as tested.
- **P0.2 complete (code/build):** registered custom shaped serializer; the Components recipe alone returns an unchanged wrench.
- **P0.3 complete (code/build):** fake-player tool state is synchronized before speed calculation; air/unbreakable/zero-progress states are rejected; break visuals reset on cancellation, target change and completion; final destruction uses `ServerPlayerGameMode#destroyBlock`.
- **P0.4 complete (code/build):** all three container APIs use sided NeoForge block `IItemHandler` capabilities with range and slot checks.
- **P0.5 complete (code/build):** Command Android stats, Creative-only damage, permission-4 commands, and Command-Block CPU recovery all use the shared `ComputerID` path.
- **P0.6 complete (code/build):** cave-tag spawn restriction, strict light `< 4`, and weights Components 50 / iron 10 / CPU 1 verified.
- **P1.1 complete (code/build):** normal, advanced and command CPU recovery share `BaseAndroidEntity.dropCpu`, frame `readComputerId`, and `AndroidComputerContainer`.
- **P1.2 documented; manual execution open:** see regression checklist below.
- **P1.3 code complete, reload test blocked:** occupied stash slots fail; item APIs return registry IDs; fuel drains only while a task is active. A dedicated server reached `Done`, but Gradle did not forward console commands, so the targeted save/restart assertion remains manual.
- **P2.1 complete (code/build):** tier textures and emissive renderer layers are active; unfinished frames visually expose reactor installation.
- **P2.2 complete (code/build):** five accepted faces are validated, stored in NBT, synchronized entity data, and mapped to emissive textures. Invalid names return an error.
- **P3 not started:** prohibited while the P0.1 manual smoke test remains blocked.
- Wrench deconstruction of normal/advanced Androids now returns the frame, full construction materials, reactor, inventory/hand contents, and CPU carrying the existing `ComputerID`.

## Regressions-Checkliste

Nach jedem CC:Tweaked-Update manuell in einer Dev-Welt:

1. Frame mit 8 Components, 10 passenden Ingots und Reaktor in variierter Reihenfolge bauen, CPU zuletzt einsetzen.
2. Terminal öffnen und `print(android.fuelLevel())` ausführen.
3. Mit passendem und unpassendem Werkzeug je einen Block über `android.breakBlock` abbauen; Bedrock und Luft müssen ohne Break-Fortschritt enden.
4. Einen durch Claim-/Protection-Mod geschützten Block testen; der Fake-Player-Abbau muss abgelehnt werden.
5. Vanilla-Container und mindestens einen capability-only Mod-Container lesen, befüllen und entnehmen.
6. Fuel, Face, Variant, Lock, Inventar und Computer-ID notieren; Chunk entladen oder Server stoppen, neu laden und alle Werte erneut prüfen.

## Annahmen (Codex)

- Das Original beschreibt die Wrench beim Components-Crafting als nicht verbraucht, aber keinen Haltbarkeitsverlust. Deshalb wird sie dort unverändert zurückgegeben; Haltbarkeit kostet nur Dekonstruktion.
- Minecraft 1.21.1 besitzt keine alte Biome-Category-API. `#c:is_cave` ist der interoperable NeoForge-Ersatz für die angegebene Höhlen-Kategorie und erlaubt Mod-Biomen, sich einzutragen.
- Die zehn Konstruktions-Ingots müssen homogen sein. Der erste Ingot legt Normal (Eisen) oder Advanced (Gold) fest; ein Wechsel während derselben Konstruktion wird abgelehnt.
