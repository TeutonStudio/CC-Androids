package com.thunderbear06.computer.api;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.ai.task.tasks.AttackEntityTask;
import com.thunderbear06.ai.task.tasks.BreakBlockTask;
import com.thunderbear06.ai.task.tasks.InteractBlockTask;
import com.thunderbear06.ai.task.tasks.InteractEntityTask;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import net.minecraft.core.BlockPos;
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
        var entity = serverLevel.getEntity(UUID.fromString(uuid));
        return entity instanceof LivingEntity living ? living : null;
    }

    @LuaFunction
    public MethodResult currentTask() {
        return MethodResult.of(brain.getTaskManager().getCurrentTaskName());
    }

    @LuaFunction
    public MethodResult getSelf() {
        return MethodResult.of(brain.getModules().sensorModule.collectEntityInfo(brain.getAndroid()));
    }

    @LuaFunction
    public MethodResult attack(String uuid) throws LuaException {
        if (missingFuel()) return MethodResult.of(false, "Android requires fuel.");
        LivingEntity target = entity(uuid);
        if (target == null) return MethodResult.of(false, "Unknown entity or invalid UUID");
        brain.setTask(new AttackEntityTask(brain.getAndroid(), 0.6D, target));
        return MethodResult.of(true);
    }

    @LuaFunction
    public MethodResult goTo(String uuid) throws LuaException {
        if (missingFuel()) return MethodResult.of(false, "Android requires fuel.");
        return brain.getModules().navigationModule.moveToEntity(uuid);
    }

    @LuaFunction
    public MethodResult moveTo(IArguments args) throws LuaException {
        if (missingFuel()) return MethodResult.of(false, "Android requires fuel.");
        return brain.getModules().navigationModule.moveToBlock(getPosFromArgs(args));
    }

    @LuaFunction
    public MethodResult breakBlock(IArguments args) throws LuaException {
        if (missingFuel()) return MethodResult.of(false, "Android requires fuel.");
        BlockPos pos = getPosFromArgs(args);
        if (!pos.closerThan(brain.getAndroid().blockPosition(), 100.0D)) return MethodResult.of(false, "Block position must be within 100 blocks");
        brain.setTask(new BreakBlockTask(brain.getAndroid(), 0.6D, pos));
        return MethodResult.of(true);
    }

    @LuaFunction
    public MethodResult useBlock(IArguments args) throws LuaException {
        if (missingFuel()) return MethodResult.of(false, "Android requires fuel.");
        brain.setTask(new InteractBlockTask(brain.getAndroid(), 0.6D, getPosFromArgs(args)));
        return MethodResult.of(true);
    }

    @LuaFunction
    public MethodResult useEntity(String uuid) throws LuaException {
        if (missingFuel()) return MethodResult.of(false, "Android requires fuel.");
        LivingEntity target = entity(uuid);
        if (target == null) return MethodResult.of(false, "Unknown entity or invalid UUID");
        brain.setTask(new InteractEntityTask(brain.getAndroid(), 0.6D, target));
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public MethodResult pickup(IArguments args) throws LuaException {
        String type = args.optString(0).orElse(null);
        var item = brain.getModules().sensorModule.getGroundItem(type);
        return item == null ? MethodResult.of(false, "Could not find item") : brain.getAndroid().pickupGroundItem(item);
    }

    @LuaFunction(mainThread = true)
    public MethodResult dropItem() {
        return brain.getAndroid().dropHandItem();
    }

    @LuaFunction(mainThread = true)
    public MethodResult storeItem(int index) {
        ItemStack stack = brain.getAndroid().getMainHandItem();
        if (stack.isEmpty()) return MethodResult.of(false, "No item in hand to stash");
        MethodResult result = brain.getAndroid().canStash(stack, index);
        if (result != null) return result;
        brain.getAndroid().setItemInHand(InteractionHand.MAIN_HAND, brain.getAndroid().stashStack(stack, index));
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public MethodResult equipSlot(int index) {
        if (index < 0 || index >= brain.getAndroid().inventory.getContainerSize()) return MethodResult.of(false, "Index out of range");
        if (!brain.getAndroid().getMainHandItem().isEmpty()) return MethodResult.of(false, "Cannot equip item while holding an item");
        ItemStack stack = brain.getAndroid().getStashItem(index, true);
        if (stack.isEmpty()) return MethodResult.of(false, "Index is empty");
        brain.getAndroid().setItemInHand(InteractionHand.MAIN_HAND, stack);
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public MethodResult swapHands() {
        brain.getAndroid().swapOffHandStack();
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public MethodResult getHandInfo(String handName) {
        ItemStack stack = handName.equals("left") || handName.equals("off") ? brain.getAndroid().getOffhandItem() : brain.getAndroid().getMainHandItem();
        return MethodResult.of(stack.getHoverName().getString(), stack.getCount());
    }

    @LuaFunction(mainThread = true)
    public MethodResult getSlotInfo(int index) {
        if (index < 0 || index >= brain.getAndroid().inventory.getContainerSize()) return MethodResult.of(false, "Index out of range");
        ItemStack stack = brain.getAndroid().inventory.getItem(index);
        return stack.isEmpty() ? MethodResult.of("empty") : MethodResult.of(stack.getHoverName().getString(), stack.getCount());
    }

    @LuaFunction(mainThread = true)
    public MethodResult refuel(IArguments args) throws LuaException {
        Optional<Integer> amount = args.optInt(0);
        ItemStack held = brain.getAndroid().getMainHandItem();
        if (held.isEmpty()) return MethodResult.of(false, "Hand is empty");
        if (!brain.getAndroid().addFuel(amount.orElse(held.getCount()), held)) return MethodResult.of(false, "Held item stack cannot be used for fuel");
        return MethodResult.of(true, brain.getAndroid().getFuel());
    }

    @LuaFunction(mainThread = true)
    public MethodResult fuelLevel() {
        return MethodResult.of(brain.getAndroid().getFuel());
    }

    @LuaFunction(mainThread = true)
    public MethodResult getClosestPlayer() {
        return MethodResult.of(brain.getModules().sensorModule.getClosestPlayer());
    }

    @LuaFunction(mainThread = true)
    public MethodResult getNearbyMobs(IArguments args) throws LuaException {
        return MethodResult.of(brain.getModules().sensorModule.getMobs(args.optString(0).orElse(null)));
    }

    @LuaFunction(mainThread = true)
    public MethodResult getClosestMob(IArguments args) throws LuaException {
        return MethodResult.of(brain.getModules().sensorModule.getClosestMobOfType(args.optString(0).orElse(null)));
    }

    @LuaFunction(mainThread = true)
    public MethodResult getBlocksOfType(String type) {
        return MethodResult.of(brain.getModules().sensorModule.getBlocksOfType(brain.getAndroid().blockPosition(), type));
    }

    @LuaFunction(mainThread = true)
    public MethodResult getContainerInfo(IArguments args) throws LuaException {
        return MethodResult.of(brain.getModules().sensorModule.getContainerInfo(getPosFromArgs(args)));
    }

    @LuaFunction(mainThread = true)
    public MethodResult storeHeldItemInContainer(IArguments args) throws LuaException {
        BlockPos pos = getPosFromArgs(args);
        int slot = args.count() == 2 ? args.getInt(1) : args.getInt(3);
        brain.getModules().interactionModule.storeHeldItemInContainer(pos, slot);
        return MethodResult.of(true);
    }

    @LuaFunction(mainThread = true)
    public MethodResult grabItemFromContainer(IArguments args) throws LuaException {
        BlockPos pos = getPosFromArgs(args);
        int slot = args.count() == 2 ? args.getInt(1) : args.getInt(3);
        brain.getModules().interactionModule.grabItemFromContainer(pos, slot);
        return MethodResult.of(true);
    }

    @LuaFunction
    public MethodResult sendChatMessage(String text) {
        brain.getAndroid().sendChatMessage(text);
        return MethodResult.of(true);
    }

    @LuaFunction
    public MethodResult changeFace(String faceName) {
        brain.getAndroid().setFace(faceName);
        return MethodResult.of(true);
    }

    @LuaFunction
    public MethodResult cancelTask() {
        brain.getTaskManager().clearCurrentTask();
        return MethodResult.of(true);
    }

    @LuaFunction
    public MethodResult sit() {
        return MethodResult.of(false, "Create seat support is not implemented in this build.");
    }

    @LuaFunction
    public MethodResult stand() {
        brain.getAndroid().stopRiding();
        return MethodResult.of(true);
    }

    @LuaFunction
    public MethodResult isSitting() {
        return MethodResult.of(brain.getAndroid().isPassenger());
    }

    @LuaFunction
    public MethodResult getVehicleInfo() {
        var vehicle = brain.getAndroid().getVehicle();
        return MethodResult.of(vehicle == null ? Map.of() : Map.of("uuid", vehicle.getStringUUID(), "type", vehicle.getType().toString()));
    }
}
