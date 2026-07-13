package com.thunderbear06.ai.modules

import com.thunderbear06.ai.AndroidBrain
import com.thunderbear06.entity.android.BaseAndroidEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState
import kotlin.math.min

class MiningModule(android: BaseAndroidEntity, brain: AndroidBrain) : AbstractAndroidModule(android, brain) {
    private var breakProgress = 0.0f
    private var breakingPos: BlockPos? = null

    fun canMineBlock(pos: BlockPos): Boolean {
        val player = android.asFakePlayer() ?: return false
        player.setItemInHand(InteractionHand.MAIN_HAND, android.mainHandItem.copy())
        val state: BlockState = android.level().getBlockState(pos)
        return !state.isAir && state.getDestroySpeed(android.level(), pos) >= 0.0f && state.getDestroyProgress(player, android.level(), pos) > 0.0f
    }

    fun mine(pos: BlockPos) {
        val player = android.asFakePlayer() ?: return
        val state: BlockState = android.level().getBlockState(pos)
        if (state.isAir || state.getDestroySpeed(android.level(), pos) < 0.0f) {
            resetBreakProgress(pos)
            return
        }
        val currentBreakingPos = breakingPos
        if (currentBreakingPos != null && currentBreakingPos != pos) resetBreakProgress(currentBreakingPos)
        breakingPos = pos.immutable()
        val originalTool: ItemStack = android.mainHandItem
        player.setItemInHand(InteractionHand.MAIN_HAND, originalTool.copy())
        val destroyProgress = state.getDestroyProgress(player, android.level(), pos)
        if (destroyProgress <= 0.0f) {
            resetBreakProgress(pos)
            return
        }
        android.swing(InteractionHand.MAIN_HAND)
        breakProgress += destroyProgress
        android.level().destroyBlockProgress(android.id, pos, min(9, (breakProgress * 10.0f).toInt()))
        if (breakProgress >= 1.0f) {
            player.gameMode.destroyBlock(pos)
            android.setItemInHand(InteractionHand.MAIN_HAND, player.mainHandItem.copy())
            resetBreakProgress(pos)
        }
    }

    fun resetBreakProgress(pos: BlockPos) {
        val progressPos = breakingPos ?: pos
        android.level().destroyBlockProgress(android.id, progressPos, -1)
        breakProgress = 0.0f
        breakingPos = null
    }
}
