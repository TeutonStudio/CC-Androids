package com.thunderbear06.ai.modules;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class InteractionModule extends AbstractAndroidModule {
    public InteractionModule(BaseAndroidEntity android, AndroidBrain brain) {
        super(android, brain);
    }

    public void tickDoorInteraction() {
    }

    public void interactWithBlock(InteractionHand hand, BlockPos pos) {
        android.swing(hand);
        ItemStack stack = android.getItemInHand(hand);
        stack.useOn(new net.minecraft.world.item.context.UseOnContext(android.level(), null, hand, stack, new BlockHitResult(Vec3.atCenterOf(pos), android.getDirection(), pos, false)));
    }

    public void interactWithEntity(InteractionHand hand, LivingEntity entity) {
        android.swing(hand);
        entity.interact(android.asPlayerOrNull(), hand);
    }

    public void storeHeldItemInContainer(BlockPos pos, int slot) throws LuaException {
        if (!pos.closerThan(android.blockPosition(), android.getBlockSearchRadius())) throw new LuaException("Position out of range");
        BlockEntity blockEntity = android.level().getBlockEntity(pos);
        if (!(blockEntity instanceof Container container)) throw new LuaException("Targeted block does not have an inventory");
        if (slot < 0 || slot >= container.getContainerSize()) throw new LuaException("Slot out of range");
        ItemStack held = android.getMainHandItem();
        if (held.isEmpty()) throw new LuaException("Hand is empty");
        ItemStack existing = container.getItem(slot);
        if (existing.isEmpty()) {
            container.setItem(slot, held.copy());
            android.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        } else if (ItemStack.isSameItemSameComponents(existing, held)) {
            int transfer = Math.min(held.getCount(), existing.getMaxStackSize() - existing.getCount());
            existing.grow(transfer);
            held.shrink(transfer);
        } else {
            throw new LuaException("There is already a different item in that slot");
        }
        container.setChanged();
    }

    public void grabItemFromContainer(BlockPos pos, int slot) throws LuaException {
        if (!pos.closerThan(android.blockPosition(), android.getBlockSearchRadius())) throw new LuaException("Position out of range");
        BlockEntity blockEntity = android.level().getBlockEntity(pos);
        if (!(blockEntity instanceof Container container)) throw new LuaException("Targeted block does not have an inventory");
        if (slot < 0 || slot >= container.getContainerSize()) throw new LuaException("Slot out of range");
        ItemStack existing = container.getItem(slot);
        if (existing.isEmpty()) throw new LuaException("Slot is empty");
        ItemStack held = android.getMainHandItem();
        if (held.isEmpty()) {
            android.setItemInHand(InteractionHand.MAIN_HAND, existing.copy());
            container.setItem(slot, ItemStack.EMPTY);
        } else if (ItemStack.isSameItemSameComponents(existing, held)) {
            int transfer = Math.min(existing.getCount(), held.getMaxStackSize() - held.getCount());
            held.grow(transfer);
            existing.shrink(transfer);
        } else {
            throw new LuaException("Hand is blocked by a different item");
        }
        container.setChanged();
    }
}
