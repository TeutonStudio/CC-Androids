package com.thunderbear06.sounds;

import com.thunderbear06.CCAndroids;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class SoundRegistry {
    public static final DeferredHolder<SoundEvent, SoundEvent> ANDROID_AMBIENT = CCAndroids.ANDROID_AMBIENT;
    public static final DeferredHolder<SoundEvent, SoundEvent> ANDROID_HURT = CCAndroids.ANDROID_HURT;
    public static final DeferredHolder<SoundEvent, SoundEvent> ANDROID_DEATH = CCAndroids.ANDROID_DEATH;

    private SoundRegistry() {
    }
}
