package com.thunderbear06.ai.task.tasks

import com.thunderbear06.ai.task.Task
import com.thunderbear06.entity.android.AndroidEntity
import net.minecraft.core.BlockPos

abstract class BlockBasedTask(android: AndroidEntity, @JvmField protected val target: BlockPos) : Task(android) {
    protected fun isInRange(distance: Double): Boolean = target.closerThan(android.blockPosition(), distance)
}
