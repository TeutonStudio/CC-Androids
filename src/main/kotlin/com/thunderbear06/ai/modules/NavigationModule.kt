package com.thunderbear06.ai.modules

import com.thunderbear06.ai.AndroidBrain
import com.thunderbear06.ai.task.tasks.MoveToBlockTask
import com.thunderbear06.ai.task.tasks.MoveToEntityTask
import com.thunderbear06.entity.android.AndroidEntity
import com.thunderbear06.entity.android.BaseAndroidEntity
import dan200.computercraft.api.lua.MethodResult
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import java.util.UUID

class NavigationModule(android: BaseAndroidEntity, brain: AndroidBrain) : AbstractAndroidModule(android, brain) {
    fun moveToBlock(pos: BlockPos): MethodResult {
        if (!android.level().isInWorldBounds(pos)) return MethodResult.of(false, "Block position must be within build limit")
        brain.setTask(MoveToBlockTask(android as AndroidEntity, 0.6, pos))
        return MethodResult.of(true)
    }

    fun moveToEntity(uuid: String): MethodResult {
        val serverLevel = android.level() as? ServerLevel ?: return MethodResult.of(false, "Server level unavailable")
        val entity = serverLevel.getEntity(UUID.fromString(uuid))
        if (entity !is LivingEntity || !entity.isAlive) return MethodResult.of(false, "Unknown entity or invalid UUID")
        brain.setTask(MoveToEntityTask(android as AndroidEntity, 0.6, entity))
        return MethodResult.of(true)
    }
}
