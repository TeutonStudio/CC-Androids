package com.thunderbear06.sounds

import com.thunderbear06.CCAndroids
import net.minecraft.sounds.SoundEvent
import net.neoforged.neoforge.registries.DeferredHolder

object SoundRegistry {
    @JvmField val ANDROID_AMBIENT: DeferredHolder<SoundEvent, SoundEvent> = CCAndroids.ANDROID_AMBIENT
    @JvmField val ANDROID_HURT: DeferredHolder<SoundEvent, SoundEvent> = CCAndroids.ANDROID_HURT
    @JvmField val ANDROID_DEATH: DeferredHolder<SoundEvent, SoundEvent> = CCAndroids.ANDROID_DEATH
}
