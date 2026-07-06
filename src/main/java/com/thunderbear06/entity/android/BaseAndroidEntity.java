package com.thunderbear06.entity.android;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.computer.AndroidComputerContainer;
import com.thunderbear06.computer.EntityComputer;
import com.thunderbear06.inventory.AndroidInventory;
import com.thunderbear06.tags.TagRegistry;
import dan200.computercraft.api.lua.MethodResult;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public abstract class BaseAndroidEntity extends PathfinderMob {
    public AndroidBrain brain;
    public final AndroidInventory inventory = new AndroidInventory(9);
    protected final AndroidComputerContainer computerContainer = new AndroidComputerContainer(this);
    protected final int maxFuel = 10000;
    protected int fuel;
    private boolean on;

    protected BaseAndroidEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        if (getNavigation() instanceof GroundPathNavigation navigation) {
            navigation.setCanOpenDoors(true);
            navigation.setCanPassDoors(true);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (brain != null) brain.getModules().interactionModule.tickDoorInteraction();
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        computerContainer.tick();
        if (tickCount % 20 == 0 && !isIdle()) consumeFuel();
    }

    protected boolean isIdle() {
        return true;
    }

    public void shutdown() {
        on = false;
        if (brain != null) brain.onShutdown();
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    protected void consumeFuel() {
        if (fuel > 0) fuel--;
    }

    private int getFuelMultiplier(ItemStack stack) {
        if (stack.is(TagRegistry.MINOR_ANDROID_FUEL)) return 10;
        if (stack.is(TagRegistry.MEDIUM_ANDROID_FUEL)) return 80;
        if (stack.is(TagRegistry.MAJOR_ANDROID_FUEL)) return 800;
        return 0;
    }

    public boolean addFuel(int maxItems, ItemStack stack) {
        int multiplier = getFuelMultiplier(stack);
        if (multiplier <= 0) return false;
        int fuelNeededItems = Math.max(0, (maxFuel - fuel + multiplier - 1) / multiplier);
        int used = Math.min(Math.min(maxItems, stack.getCount()), fuelNeededItems);
        if (used <= 0) return false;
        fuel = Math.min(maxFuel, fuel + used * multiplier);
        stack.shrink(used);
        return true;
    }

    public int getFuel() {
        return fuel;
    }

    public boolean hasFuel() {
        return fuel > 0;
    }

    public AndroidComputerContainer getComputer() {
        return computerContainer;
    }

    public MethodResult pickupGroundItem(ItemEntity itemEntity) {
        if (!itemEntity.isAlive() || itemEntity.getItem().isEmpty()) return MethodResult.of(false, "Could not pick up item");
        if (!getMainHandItem().isEmpty()) return MethodResult.of(false, "Main hand is not empty");
        setItemInHand(InteractionHand.MAIN_HAND, itemEntity.getItem().copy());
        itemEntity.discard();
        return MethodResult.of(true);
    }

    public MethodResult dropHandItem() {
        ItemStack itemStack = getMainHandItem();
        if (itemStack.isEmpty()) return MethodResult.of(false, "Hand is empty");
        spawnAtLocation(itemStack.copy());
        setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        return MethodResult.of(true);
    }

    public ItemStack stashStack(ItemStack stack, int index) {
        ItemStack stored = inventory.getItem(index);
        if (stored.isEmpty()) {
            inventory.setItem(index, stack.copy());
            return ItemStack.EMPTY;
        }
        if (ItemStack.isSameItemSameComponents(stored, stack)) {
            int transfer = Math.min(stack.getCount(), stored.getMaxStackSize() - stored.getCount());
            stored.grow(transfer);
            stack.shrink(transfer);
        }
        return stack.isEmpty() ? ItemStack.EMPTY : stack;
    }

    public ItemStack getStashItem(int index, boolean remove) {
        return remove ? inventory.removeItemNoUpdate(index) : inventory.getItem(index);
    }

    public void swapOffHandStack() {
        ItemStack main = getMainHandItem().copy();
        setItemInHand(InteractionHand.MAIN_HAND, getOffhandItem().copy());
        setItemInHand(InteractionHand.OFF_HAND, main);
    }

    public MethodResult canStash(ItemStack stack, int index) {
        if (index < 0 || index >= inventory.getContainerSize()) return MethodResult.of(false, "Index out of range");
        ItemStack stored = inventory.getItem(index);
        if (!stored.isEmpty() && !ItemStack.isSameItemSameComponents(stored, stack)) return MethodResult.of(false, "Index is occupied by another item stack");
        return null;
    }

    public void sendChatMessage(String message) {
        if (level().getServer() != null) {
            CCAndroids.LOGGER.info("[Android {}] {}", getStringUUID(), message);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("Items", inventory.toTag(registryAccess()));
        tag.putInt("Fuel", fuel);
        tag.putBoolean("IsOn", on);
        CompoundTag computer = new CompoundTag();
        computerContainer.writeNbt(computer);
        if (brain != null) brain.writeNbt(computer);
        tag.put("ComputerEntity", computer);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Items")) inventory.fromTag(tag.getList("Items", 10), registryAccess());
        fuel = tag.getInt("Fuel");
        on = tag.getBoolean("IsOn");
        if (tag.contains("ComputerEntity")) {
            CompoundTag computer = tag.getCompound("ComputerEntity");
            computerContainer.readNbt(computer);
            if (brain != null) brain.readNbt(computer);
        }
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.FALL) || source.is(DamageTypes.MAGIC)) return false;
        return super.hurt(source, amount);
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        if (!level().isClientSide) {
            EntityComputer computer = computerContainer.getServerComputer();
            if (computer != null) computer.close();
        }
    }

    public double getEntitySearchRadius() {
        return 10.0D;
    }

    public int getBlockSearchRadius() {
        return 10;
    }

    public Player asPlayerOrNull() {
        return level().getNearestPlayer(this, 0.1D);
    }
}
