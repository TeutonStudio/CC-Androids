package com.thunderbear06.ai

import com.mojang.authlib.GameProfile
import com.thunderbear06.CCAndroids
import com.thunderbear06.ai.modules.AndroidModules
import com.thunderbear06.ai.task.Task
import com.thunderbear06.ai.task.TaskManager
import com.thunderbear06.entity.android.AndroidEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.player.Player

class AndroidBrain(val android: AndroidEntity) {
    val taskManager: TaskManager = TaskManager()
    val modules: AndroidModules = AndroidModules(android, this)
    var owningPlayerProfile: GameProfile? = null
        private set

    fun onShutdown() {
        taskManager.clearCurrentTask()
    }

    fun setTask(task: Task) {
        if (CCAndroids.CONFIG.DebugLogging) CCAndroids.LOGGER.info("Set android task to {}", task.name)
        taskManager.setCurrentTask(task)
    }

    fun isOwningPlayer(player: Player): Boolean =
        owningPlayerProfile?.id == player.gameProfile.id

    fun setOwningPlayer(profile: GameProfile) {
        owningPlayerProfile = profile
    }

    fun writeNbt(tag: CompoundTag) {
        val profile = owningPlayerProfile ?: return
        tag.putUUID("OwningPlayerUUID", profile.id)
        tag.putString("OwningPlayerName", profile.name)
    }

    fun readNbt(tag: CompoundTag) {
        if (tag.hasUUID("OwningPlayerUUID")) {
            owningPlayerProfile = GameProfile(tag.getUUID("OwningPlayerUUID"), tag.getString("OwningPlayerName"))
        }
    }
}
