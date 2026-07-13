package com.thunderbear06.computer

import com.thunderbear06.entity.android.BaseAndroidEntity
import dan200.computercraft.shared.computer.core.ServerComputer
import net.minecraft.server.level.ServerLevel

class EntityComputer(level: ServerLevel, private val entity: BaseAndroidEntity, properties: Properties) :
    ServerComputer(level, entity.blockPosition(), properties) {
    override fun tickServer() {
        super.tickServer()
        val serverLevel = entity.level() as? ServerLevel
        if (serverLevel != null) setPosition(serverLevel, entity.blockPosition())
    }
}
