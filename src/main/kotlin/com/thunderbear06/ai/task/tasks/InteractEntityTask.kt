package com.thunderbear06.ai.task.tasks

import com.thunderbear06.entity.android.AndroidEntity
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.LivingEntity

class InteractEntityTask(android: AndroidEntity, moveSpeed: Double, entity: LivingEntity) : MoveToEntityTask(android, moveSpeed, entity) {
    private var complete = false

    override val name: String = "usingEntity"

    override fun shouldTick(): Boolean = target.isAlive && !complete

    override fun tick() {
        if (isInRange(2.0)) {
            android.lookControl.setLookAt(target)
            android.brain.modules.interactionModule.interactWithEntity(InteractionHand.MAIN_HAND, target)
            complete = true
        } else {
            super.tick()
        }
    }
}
