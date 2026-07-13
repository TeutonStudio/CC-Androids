package com.thunderbear06.ai.task.tasks

import com.thunderbear06.ai.task.Task
import com.thunderbear06.entity.android.AndroidEntity
import net.minecraft.world.entity.LivingEntity

abstract class EntityBasedTask(android: AndroidEntity, @JvmField protected val target: LivingEntity) : Task(android) {
    override fun shouldTick(): Boolean = target.isAlive

    override fun tick() {
        android.lookControl.setLookAt(target)
    }

    protected fun isInRange(distance: Double): Boolean = android.distanceToSqr(target) <= distance * distance
}
