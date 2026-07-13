package com.thunderbear06.ai.task.tasks

import com.thunderbear06.entity.android.AndroidEntity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.navigation.PathNavigation

open class MoveToEntityTask(android: AndroidEntity, private val moveSpeed: Double, entity: LivingEntity) : EntityBasedTask(android, entity) {
    private val navigation: PathNavigation = android.navigation

    override val name: String = "movingToEntity"

    override fun shouldTick(): Boolean = super.shouldTick() && !isInRange(1.5)

    override fun firstTick() {
    }

    override fun tick() {
        super.tick()
        if (navigation.isDone) navigation.moveTo(target, moveSpeed)
    }

    override fun lastTick() {
        navigation.stop()
    }
}
