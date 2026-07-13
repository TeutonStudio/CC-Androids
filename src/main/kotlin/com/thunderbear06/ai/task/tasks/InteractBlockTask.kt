package com.thunderbear06.ai.task.tasks

import com.thunderbear06.entity.android.AndroidEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand

class InteractBlockTask(android: AndroidEntity, moveSpeed: Double, pos: BlockPos) : MoveToBlockTask(android, moveSpeed, pos) {
    private var complete = false

    override val name: String = "usingBlock"

    override fun shouldTick(): Boolean = !complete

    override fun tick() {
        if (isInRange(2.5)) {
            android.lookControl.setLookAt(target.x.toDouble(), target.y.toDouble(), target.z.toDouble())
            android.brain.modules.interactionModule.interactWithBlock(InteractionHand.MAIN_HAND, target)
            complete = true
        } else {
            super.tick()
        }
    }
}
