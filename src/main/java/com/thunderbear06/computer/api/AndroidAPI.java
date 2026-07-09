package com.thunderbear06.computer.api;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.ai.task.tasks.AttackEntityTask;
import com.thunderbear06.ai.task.tasks.BreakBlockTask;
import com.thunderbear06.ai.task.tasks.InteractBlockTask;
import com.thunderbear06.ai.task.tasks.InteractEntityTask;
import com.thunderbear06.entity.android.CommandAndroidEntity;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class AndroidAPI implements ILuaAPI {
    private final AndroidBrain brain;

    public AndroidAPI(AndroidBrain brain) {
        this.brain = brain;
    }

    @Override
    public String[] getNames() {
        return new String[]{"android"};
    }

    @Override
    public String getModuleName() {
        return "android";
    }

    private boolean missingFuel() {
        return !brain.getAndroid().hasFuel();
    }

    private BlockPos getPosFromArgs(IArguments args) throws LuaException {
        Object first = args.get(0);
        if (first instanceof Map<?, ?> map) {
            return new BlockPos(number(map.get("x"), "x"), number(map.get("y"), "y"), number(map.get("z"), "z"));
        }
        return new BlockPos(args.getInt(0), args.getInt(1), args.getInt(2));
    }

    private int number(Object value, String key) throws LuaException {
        if (value instanceof Number number) return number.intValue();
        throw new LuaException(key + " must be a number");
    }

    private LivingEntity entity(String uuid) throws LuaException {
        if (!(brain.getAndroid().level() instanceof ServerLevel serverLevel)) throw new LuaException("Server level unavailable");
        UUID parsed;
        try {
            parsed = UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new LuaException("Invalid UUID: " + uuid);
        }
        var entity = serverLevel.getEntity(parsed);
        return entity instanceof LivingEntity living ? living : null;
    }

    @LuaFunction
    public final MethodResult currentTask() {
        return MethodResult.of(brain.getTaskManager().getCurrentTaskName());
    }

    @LuaFunction
    public final MethodResult getSelf() {
        return MethodResult.of(brain.getModules().sensorModule.collectEntityInfo(brain.getAndroid()));
    }

    @LuaFunction
    public final MethodResult attack(String uuid) throws LuaException {
        if (missingFuel()) return MethodResult.of(false, "Android requires fuel.");
        LivingEntity target = entity(uuid);
        if (target == null) return MethodResult.of(false, "Unknown entity or invalid UUID");
        brain.setTask(new AttackEntityTask(brain.getAndroid(), 0.6D, target));
        return MethodResult.of(true);
    }

    @LuaFunction
    public final MethodResult goTo(String uuid) throws LuaException {
        if (missingFuel()) return MethodResult.of(false, "Android requires fuel.");
        try {
            return brain.getModules().navigationModule.moveToEntity(uuid);
        } catch (IllegalArgumentException e) {
            return MethodResult.of(false, "Invalid UUID: " + uuid);
        }
    }

    @LuaFunction
    public final MethodResult moveTo(IArguments args) throws LuaException {
        if (missingFuel()) return MethodResult.of(false, "Android requires fuel.");
        return brain.getModules().navigationModule.moveToBlock(getPosFromArgs(args));
    }

    @LuaFunction
    public final MethodResult breakBlock(IArguments args) throws LuaException {
        if (missingFuel()) return MethodResult.of(false, "Android requires fuel.");
        BlockPos pos = getPosFromArgs(args);
        if (!pos.closerThan(brain.getAndroid().blockPosition(), 100.0D)) return MethodResult.of(false, "Block position must be within 100 blocks");
        brain.setTask(new BreakBlockTask(brain.getAndroid(), 0.6D, pos));
        return MethodResult.of(true);
    }

    @LuaFunction
    public final MethodResult useBlock(IArguments args) throws LuaException {
        if (missingFuel()) return MethodResult.of(false, "Android requires fuel.");
        brain.setTask(new InteractBlockTask(brain.getAndroid(), 0.6D, getPosFromArgs(args)));
        return MethodResult.of(true);
    }

    @LuaFunction
    public final MethodResult useEntity(String uuid) throws LuaException {
        if (missingFuel()) return MethodResult.of(false, "Android requires fuel.");
        LivingEntity target = entity(uuid);
        if (target == null) return MethodResult.of(false, "Unknown entity or invalid UUID");
        brain.setTask(new InteractEntityTask(brain.getAndroid(), 0.6D, target));
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult pickup(IArguments args) throws LuaException {
        String type = args.optString(0).orElse(null);
        var item = brain.getModules().sensorModule.getGroundItem(type);
        return item == null ? MethodResult.of(false, "Could not find item") : brain.getAndroid().pickupGroundItem(item);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult dropItem() {
        return brain.getAndroid().dropHandItem();
    }

    @LuaFunction(mainThread = true)
    public final MethodResult storeItem(int index) {
        ItemStack stack = brain.getAndroid().getMainHandItem();
        if (stack.isEmpty()) return MethodResult.of(false, "No item in hand to stash");
        MethodResult result = brain.getAndroid().canStash(stack, index);
        if (result != null) return result;
        brain.getAndroid().setItemInHand(InteractionHand.MAIN_HAND, brain.getAndroid().stashStack(stack, index));
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult equipSlot(int index) {
        if (index < 0 || index >= brain.getAndroid().inventory.getContainerSize()) return MethodResult.of(false, "Index out of range");
        if (!brain.getAndroid().getMainHandItem().isEmpty()) return MethodResult.of(false, "Cannot equip item while holding an item");
        ItemStack stack = brain.getAndroid().getStashItem(index, true);
        if (stack.isEmpty()) return MethodResult.of(false, "Index is empty");
        brain.getAndroid().setItemInHand(InteractionHand.MAIN_HAND, stack);
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult swapHands() {
        brain.getAndroid().swapOffHandStack();
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getHandInfo(String handName) {
        ItemStack stack;
        if (handName.equals("right") || handName.equals("main")) {
            stack = brain.getAndroid().getMainHandItem();
        } else if (handName.equals("left") || handName.equals("off")) {
            stack = brain.getAndroid().getOffhandItem();
        } else {
            return MethodResult.of(false, "Invalid hand name. Expected main/right or off/left.");
        }
        return MethodResult.of(itemId(stack), stack.getCount());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getSlotInfo(int index) {
        if (index < 0 || index >= brain.getAndroid().inventory.getContainerSize()) return MethodResult.of(false, "Index out of range");
        ItemStack stack = brain.getAndroid().inventory.getItem(index);
        return stack.isEmpty() ? MethodResult.of("empty") : MethodResult.of(itemId(stack), stack.getCount());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult refuel(IArguments args) throws LuaException {
        Optional<Integer> amount = args.optInt(0);
        ItemStack held = brain.getAndroid().getMainHandItem();
        if (held.isEmpty()) return MethodResult.of(false, "Hand is empty");
        if (!brain.getAndroid().addFuel(amount.orElse(held.getCount()), held)) return MethodResult.of(false, "Held item stack cannot be used for fuel");
        return MethodResult.of(true, brain.getAndroid().getFuel());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult fuelLevel() {
        return MethodResult.of(brain.getAndroid().getFuel());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getClosestPlayer() {
        return MethodResult.of(brain.getModules().sensorModule.getClosestPlayer());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getNearbyMobs(IArguments args) throws LuaException {
        return MethodResult.of(brain.getModules().sensorModule.getMobs(args.optString(0).orElse(null)));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getClosestMob(IArguments args) throws LuaException {
        return MethodResult.of(brain.getModules().sensorModule.getClosestMobOfType(args.optString(0).orElse(null)));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getBlocksOfType(String type) {
        return MethodResult.of(brain.getModules().sensorModule.getBlocksOfType(brain.getAndroid().blockPosition(), type));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult getContainerInfo(IArguments args) throws LuaException {
        return MethodResult.of(brain.getModules().sensorModule.getContainerInfo(getPosFromArgs(args)));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult storeHeldItemInContainer(IArguments args) throws LuaException {
        BlockPos pos = getPosFromArgs(args);
        if (args.count() != 2 && args.count() != 4) return MethodResult.of(false, "Expected position table and slot, or x, y, z, slot.");
        int slot = args.count() == 2 ? args.getInt(1) : args.getInt(3);
        brain.getModules().interactionModule.storeHeldItemInContainer(pos, slot);
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult grabItemFromContainer(IArguments args) throws LuaException {
        BlockPos pos = getPosFromArgs(args);
        if (args.count() != 2 && args.count() != 4) return MethodResult.of(false, "Expected position table and slot, or x, y, z, slot.");
        int slot = args.count() == 2 ? args.getInt(1) : args.getInt(3);
        brain.getModules().interactionModule.grabItemFromContainer(pos, slot);
        return MethodResult.of(true);
    }

    @LuaFunction
    public final MethodResult sendChatMessage(String text) {
        brain.getAndroid().sendChatMessage(text);
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult runCommand(String command) {
        if (!(brain.getAndroid() instanceof CommandAndroidEntity)) return MethodResult.of(false, "Command execution requires a Command Android.");
        if (!(brain.getAndroid().level() instanceof ServerLevel serverLevel)) return MethodResult.of(false, "Server level unavailable.");
        try {
            serverLevel.getServer().getCommands().performPrefixedCommand(brain.getAndroid().createCommandSourceStack().withPermission(4), command);
            return MethodResult.of(true);
        } catch (RuntimeException e) {
            return MethodResult.of(false, e.getMessage() == null ? "Command failed." : e.getMessage());
        }
    }

    @LuaFunction
    public final MethodResult changeFace(String faceName) {
        if (!faceName.equals("angry") && !faceName.equals("annoyed") && !faceName.equals("happy")
                && !faceName.equals("sad") && !faceName.equals("woozy")) {
            return MethodResult.of(false, "Unknown face. Expected angry, annoyed, happy, sad, or woozy.");
        }
        brain.getAndroid().setFace(faceName);
        return MethodResult.of(true);
    }

    @LuaFunction
    public final MethodResult cancelTask() {
        brain.getTaskManager().clearCurrentTask();
        return MethodResult.of(true);
    }

    @LuaFunction
    public final MethodResult sit() {
        return MethodResult.of(false, "Create seat support is not implemented in this build.");
    }

    @LuaFunction
    public final MethodResult stand() {
        brain.getAndroid().stopRiding();
        return MethodResult.of(true);
    }

    @LuaFunction
    public final MethodResult isSitting() {
        return MethodResult.of(brain.getAndroid().isPassenger());
    }

    @LuaFunction
    public final MethodResult getVehicleInfo() {
        var vehicle = brain.getAndroid().getVehicle();
        return MethodResult.of(vehicle == null ? Map.of() : Map.of("uuid", vehicle.getStringUUID(), "type", vehicle.getType().toString()));
    }

    private static String itemId(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
    }
}
