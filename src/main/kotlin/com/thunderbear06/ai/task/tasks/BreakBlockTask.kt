package com.thunderbear06.ai.task.tasks

import com.thunderbear06.entity.android.AndroidEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand

class BreakBlockTask(android: AndroidEntity, moveSpeed: Double, pos: BlockPos) : MoveToBlockTask(android, moveSpeed, pos) {
    override val name: String = "breakingBlock"

    override fun shouldTick(): Boolean = android.brain.modules.miningModule.canMineBlock(target)

    override fun tick() {
        val center = target.center
        android.lookControl.setLookAt(center.x, center.y, center.z)
        if (isInRange(3.0)) {
            android.swing(InteractionHand.MAIN_HAND)
            android.brain.modules.miningModule.mine(target)
        } else {
            super.tick()
        }
    }

    override fun lastTick() {
        android.brain.modules.miningModule.resetBreakProgress(target)
        super.lastTick()
    }
}
