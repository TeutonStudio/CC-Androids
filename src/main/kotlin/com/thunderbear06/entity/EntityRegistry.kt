package com.thunderbear06.entity

import com.thunderbear06.CCAndroids
import com.thunderbear06.entity.android.AdvancedAndroidEntity
import com.thunderbear06.entity.android.AndroidEntity
import com.thunderbear06.entity.android.CommandAndroidEntity
import com.thunderbear06.entity.android.RogueDroidEntity
import com.thunderbear06.entity.android.frame.AndroidFrame
import net.minecraft.world.entity.EntityType
import net.neoforged.neoforge.registries.DeferredHolder

object EntityRegistry {
    @JvmField val ANDROID_ENTITY: DeferredHolder<EntityType<*>, EntityType<AndroidEntity>> = CCAndroids.ANDROID
    @JvmField val ADVANCED_ANDROID_ENTITY: DeferredHolder<EntityType<*>, EntityType<AdvancedAndroidEntity>> = CCAndroids.ADVANCED_ANDROID
    @JvmField val COMMAND_ANDROID_ENTITY: DeferredHolder<EntityType<*>, EntityType<CommandAndroidEntity>> = CCAndroids.COMMAND_ANDROID
    @JvmField val ANDROID_FRAME_ENTITY: DeferredHolder<EntityType<*>, EntityType<AndroidFrame>> = CCAndroids.UNFINISHED_ANDROID
    @JvmField val ROGUE_ANDROID_ENTITY: DeferredHolder<EntityType<*>, EntityType<RogueDroidEntity>> = CCAndroids.ROGUE_ANDROID
}
