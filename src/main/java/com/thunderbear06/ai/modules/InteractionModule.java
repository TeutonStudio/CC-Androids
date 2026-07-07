package com.thunderbear06.ai.modules;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.shared.platform.PlatformHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public class InteractionModule extends AbstractAndroidModule {
    public InteractionModule(BaseAndroidEntity android, AndroidBrain brain) {
        super(android, brain);
    }

    public void tickDoorInteraction() {
    }

    public void interactWithBlock(InteractionHand hand, BlockPos pos) {
        ServerPlayer player = android.asFakePlayer();
        if (player == null) return;
        android.swing(hand);
        ItemStack stack = android.getItemInHand(hand);
        player.setItemInHand(hand, stack.copy());
        PlatformHelper.get().useOn(player, player.getItemInHand(hand), new BlockHitResult(Vec3.atCenterOf(pos), accessSide(pos), pos, false));
        android.setItemInHand(hand, player.getItemInHand(hand).copy());
    }

    public void interactWithEntity(InteractionHand hand, LivingEntity entity) {
        ServerPlayer player = android.asFakePlayer();
        if (player == null) return;
        android.swing(hand);
        player.setItemInHand(hand, android.getItemInHand(hand).copy());
        PlatformHelper.get().interactWithEntity(player, entity, entity.position());
        android.setItemInHand(hand, player.getItemInHand(hand).copy());
    }

    public void storeHeldItemInContainer(BlockPos pos, int slot) throws LuaException {
        if (!pos.closerThan(android.blockPosition(), android.getBlockSearchRadius())) throw new LuaException("Position out of range");
        IItemHandler handler = itemHandler(pos);
        if (handler == null) throw new LuaException("Targeted block does not have an inventory");
        if (slot < 0 || slot >= handler.getSlots()) throw new LuaException("Slot out of range");
        ItemStack held = android.getMainHandItem();
        if (held.isEmpty()) throw new LuaException("Hand is empty");
        ItemStack remainder = handler.insertItem(slot, held.copy(), false);
        if (ItemStack.matches(remainder, held)) throw new LuaException("Item could not be inserted into that slot");
        android.setItemInHand(InteractionHand.MAIN_HAND, remainder);
        setChanged(pos);
    }

    public void grabItemFromContainer(BlockPos pos, int slot) throws LuaException {
        if (!pos.closerThan(android.blockPosition(), android.getBlockSearchRadius())) throw new LuaException("Position out of range");
        IItemHandler handler = itemHandler(pos);
        if (handler == null) throw new LuaException("Targeted block does not have an inventory");
        if (slot < 0 || slot >= handler.getSlots()) throw new LuaException("Slot out of range");
        ItemStack held = android.getMainHandItem();
        ItemStack existing = handler.getStackInSlot(slot);
        if (existing.isEmpty()) throw new LuaException("Slot is empty");
        int limit = held.isEmpty() ? existing.getMaxStackSize() : Math.min(held.getMaxStackSize() - held.getCount(), existing.getMaxStackSize());
        if (limit <= 0) throw new LuaException("Hand is full");
        ItemStack extracted = handler.extractItem(slot, limit, true);
        if (extracted.isEmpty()) throw new LuaException("Item could not be extracted from that slot");
        if (!held.isEmpty() && !ItemStack.isSameItemSameComponents(extracted, held)) throw new LuaException("Hand is blocked by a different item");
        extracted = handler.extractItem(slot, limit, false);
        if (held.isEmpty()) {
            android.setItemInHand(InteractionHand.MAIN_HAND, extracted);
        } else if (!extracted.isEmpty()) {
            held.grow(extracted.getCount());
        }
        setChanged(pos);
    }

    private IItemHandler itemHandler(BlockPos pos) {
        return android.level().getCapability(Capabilities.ItemHandler.BLOCK, pos, accessSide(pos));
    }

    private Direction accessSide(BlockPos pos) {
        Vec3 delta = android.position().subtract(Vec3.atCenterOf(pos));
        return Direction.getNearest(delta.x, delta.y, delta.z);
    }

    private void setChanged(BlockPos pos) {
        BlockEntity blockEntity = android.level().getBlockEntity(pos);
        if (blockEntity instanceof Container container) container.setChanged();
    }
}
