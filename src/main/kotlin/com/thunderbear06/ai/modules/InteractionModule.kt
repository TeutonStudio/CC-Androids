package com.thunderbear06.ai.modules

import com.thunderbear06.ai.AndroidBrain
import com.thunderbear06.entity.android.BaseAndroidEntity
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.shared.platform.PlatformHelper
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.Container
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.items.IItemHandler

class InteractionModule(android: BaseAndroidEntity, brain: AndroidBrain) : AbstractAndroidModule(android, brain) {
    fun tickDoorInteraction() {
    }

    fun interactWithBlock(hand: InteractionHand, pos: BlockPos) {
        val player = android.asFakePlayer() ?: return
        android.swing(hand)
        val stack = android.getItemInHand(hand)
        player.setItemInHand(hand, stack.copy())
        PlatformHelper.get().useOn(player, player.getItemInHand(hand), BlockHitResult(Vec3.atCenterOf(pos), accessSide(pos), pos, false))
        android.setItemInHand(hand, player.getItemInHand(hand).copy())
    }

    fun interactWithEntity(hand: InteractionHand, entity: LivingEntity) {
        val player = android.asFakePlayer() ?: return
        android.swing(hand)
        player.setItemInHand(hand, android.getItemInHand(hand).copy())
        PlatformHelper.get().interactWithEntity(player, entity, entity.position())
        android.setItemInHand(hand, player.getItemInHand(hand).copy())
    }

    @Throws(LuaException::class)
    fun storeHeldItemInContainer(pos: BlockPos, slot: Int) {
        if (!pos.closerThan(android.blockPosition(), android.blockSearchRadius.toDouble())) throw LuaException("Position out of range")
        val handler = itemHandler(pos) ?: throw LuaException("Targeted block does not have an inventory")
        if (slot < 0 || slot >= handler.slots) throw LuaException("Slot out of range")
        val held = android.mainHandItem
        if (held.isEmpty) throw LuaException("Hand is empty")
        val remainder = handler.insertItem(slot, held.copy(), false)
        if (ItemStack.matches(remainder, held)) throw LuaException("Item could not be inserted into that slot")
        android.setItemInHand(InteractionHand.MAIN_HAND, remainder)
        setChanged(pos)
    }

    @Throws(LuaException::class)
    fun grabItemFromContainer(pos: BlockPos, slot: Int) {
        if (!pos.closerThan(android.blockPosition(), android.blockSearchRadius.toDouble())) throw LuaException("Position out of range")
        val handler = itemHandler(pos) ?: throw LuaException("Targeted block does not have an inventory")
        if (slot < 0 || slot >= handler.slots) throw LuaException("Slot out of range")
        val held = android.mainHandItem
        val existing = handler.getStackInSlot(slot)
        if (existing.isEmpty) throw LuaException("Slot is empty")
        val limit = if (held.isEmpty) existing.maxStackSize else minOf(held.maxStackSize - held.count, existing.maxStackSize)
        if (limit <= 0) throw LuaException("Hand is full")
        var extracted = handler.extractItem(slot, limit, true)
        if (extracted.isEmpty) throw LuaException("Item could not be extracted from that slot")
        if (!held.isEmpty && !ItemStack.isSameItemSameComponents(extracted, held)) throw LuaException("Hand is blocked by a different item")
        extracted = handler.extractItem(slot, limit, false)
        if (held.isEmpty) {
            android.setItemInHand(InteractionHand.MAIN_HAND, extracted)
        } else if (!extracted.isEmpty) {
            held.grow(extracted.count)
        }
        setChanged(pos)
    }

    private fun itemHandler(pos: BlockPos): IItemHandler? =
        android.level().getCapability(Capabilities.ItemHandler.BLOCK, pos, accessSide(pos))

    private fun accessSide(pos: BlockPos): Direction {
        val delta = android.position().subtract(Vec3.atCenterOf(pos))
        return Direction.getNearest(delta.x, delta.y, delta.z)
    }

    private fun setChanged(pos: BlockPos) {
        val blockEntity: BlockEntity? = android.level().getBlockEntity(pos)
        if (blockEntity is Container) blockEntity.setChanged()
    }
}
