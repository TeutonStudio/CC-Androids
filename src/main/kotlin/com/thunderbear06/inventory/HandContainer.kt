package com.thunderbear06.inventory

import com.thunderbear06.entity.android.BaseAndroidEntity
import net.minecraft.world.Container
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

class HandContainer(private val android: BaseAndroidEntity) : Container {
    override fun getContainerSize(): Int = 2

    override fun isEmpty(): Boolean = android.mainHandItem.isEmpty && android.offhandItem.isEmpty

    override fun getItem(slot: Int): ItemStack =
        when (slot) {
            0 -> android.mainHandItem
            1 -> android.offhandItem
            else -> ItemStack.EMPTY
        }

    override fun removeItem(slot: Int, amount: Int): ItemStack {
        val stack = getItem(slot)
        if (stack.isEmpty) return ItemStack.EMPTY
        val removed = stack.split(amount)
        if (stack.isEmpty) setItem(slot, ItemStack.EMPTY)
        return removed
    }

    override fun removeItemNoUpdate(slot: Int): ItemStack {
        val stack = getItem(slot)
        setItem(slot, ItemStack.EMPTY)
        return stack
    }

    override fun setItem(slot: Int, stack: ItemStack) {
        if (slot == 0) android.setItemInHand(InteractionHand.MAIN_HAND, stack)
        if (slot == 1) android.setItemInHand(InteractionHand.OFF_HAND, stack)
    }

    override fun setChanged() {
    }

    override fun stillValid(player: Player): Boolean = android.isAlive && player.distanceToSqr(android) < 64.0

    override fun clearContent() {
        android.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY)
        android.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY)
    }
}
