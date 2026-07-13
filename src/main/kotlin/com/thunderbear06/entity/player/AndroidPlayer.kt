package com.thunderbear06.entity.player

import com.mojang.authlib.GameProfile
import com.thunderbear06.ai.AndroidBrain
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

object AndroidPlayer {
    @JvmStatic
    fun get(brain: AndroidBrain): ServerPlayer? {
        val serverLevel = brain.android.level() as? ServerLevel ?: return null
        val owner = serverLevel.getNearestPlayer(brain.android, 64.0)
        return owner as? ServerPlayer
    }

    @JvmStatic
    fun fallbackProfile(brain: AndroidBrain): GameProfile =
        GameProfile(UUID.nameUUIDFromBytes(brain.android.uuid.toString().toByteArray()), "Android")
}
