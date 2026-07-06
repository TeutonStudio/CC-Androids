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
- `android.changeFace(faceName)`
- `android.cancelTask()`

Return values use `true, data` / `false, error` for mutating or fallible operations. Some read-only legacy helpers still return their direct value, such as `fuelLevel()` and `currentTask()`, to keep ROM programs simple.

The bundled ROM programs have been adjusted to the `ok, err` convention for `android.refuel()`.

## Simplified or incomplete systems

- Pocket upgrades and real peripheral bridging are not implemented. `DummyPocket` is only a safe placeholder.
- Modem/rednet support through hand items or pocket upgrades is not implemented.
- Mining is functional but still simplified. It runs server-side, ignores air and unbreakable blocks, and clears break progress, but does not yet fully mirror player permission/event/tool behavior.
- Container interaction uses direct `Container` access with range and slot checks. It is not yet permission-perfect for every modded container.
- Fake-player interaction is the preferred path for block and entity use. If a future CC:Tweaked or NeoForge version changes `PlatformHelper.createFakePlayer`, this path must be retested.
- The old custom `wrench_shaped` recipe is not active. Current recipes use vanilla shaped crafting, so the wrench may be consumed in the components recipe as a temporary deviation.
- The renderer and GUI are minimal ports. Old emissive face overlays and advanced visual variants are not fully restored.
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
