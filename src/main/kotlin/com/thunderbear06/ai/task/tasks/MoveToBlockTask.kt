package com.thunderbear06.ai.task.tasks

import com.thunderbear06.entity.android.AndroidEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.ai.navigation.PathNavigation

open class MoveToBlockTask(android: AndroidEntity, private val moveSpeed: Double, pos: BlockPos) : BlockBasedTask(android, pos) {
    private val navigation: PathNavigation = android.navigation

    override val name: String = "movingToBlock"

    override fun shouldTick(): Boolean = !isInRange(2.0)

    override fun firstTick() {
    }

    override fun tick() {
        if (navigation.isDone) {
            navigation.moveTo(target.x.toDouble(), target.y.toDouble(), target.z.toDouble(), moveSpeed)
        }
    }

    override fun lastTick() {
        navigation.stop()
    }
}
