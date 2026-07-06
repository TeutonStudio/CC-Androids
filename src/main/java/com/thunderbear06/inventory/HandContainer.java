package com.thunderbear06.inventory;

import com.thunderbear06.entity.android.BaseAndroidEntity;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HandContainer implements Container {
    private final BaseAndroidEntity android;

    public HandContainer(BaseAndroidEntity android) {
        this.android = android;
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return android.getMainHandItem().isEmpty() && android.getOffhandItem().isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return switch (slot) {
            case 0 -> android.getMainHandItem();
            case 1 -> android.getOffhandItem();
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = getItem(slot);
        if (stack.isEmpty()) return ItemStack.EMPTY;
        ItemStack removed = stack.split(amount);
        if (stack.isEmpty()) setItem(slot, ItemStack.EMPTY);
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = getItem(slot);
        setItem(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) android.setItemInHand(InteractionHand.MAIN_HAND, stack);
        if (slot == 1) android.setItemInHand(InteractionHand.OFF_HAND, stack);
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player player) {
        return android.isAlive() && player.distanceToSqr(android) < 64.0D;
    }

    @Override
    public void clearContent() {
        android.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        android.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
    }
}
