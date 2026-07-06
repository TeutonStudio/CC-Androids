package com.thunderbear06.entity;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.entity.android.AdvancedAndroidEntity;
import com.thunderbear06.entity.android.AndroidEntity;
import com.thunderbear06.entity.android.CommandAndroidEntity;
import com.thunderbear06.entity.android.RogueDroidEntity;
import com.thunderbear06.entity.android.frame.AndroidFrame;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class EntityRegistry {
    public static final DeferredHolder<EntityType<?>, EntityType<AndroidEntity>> ANDROID_ENTITY = CCAndroids.ANDROID;
    public static final DeferredHolder<EntityType<?>, EntityType<AdvancedAndroidEntity>> ADVANCED_ANDROID_ENTITY = CCAndroids.ADVANCED_ANDROID;
    public static final DeferredHolder<EntityType<?>, EntityType<CommandAndroidEntity>> COMMAND_ANDROID_ENTITY = CCAndroids.COMMAND_ANDROID;
    public static final DeferredHolder<EntityType<?>, EntityType<AndroidFrame>> ANDROID_FRAME_ENTITY = CCAndroids.UNFINISHED_ANDROID;
    public static final DeferredHolder<EntityType<?>, EntityType<RogueDroidEntity>> ROGUE_ANDROID_ENTITY = CCAndroids.ROGUE_ANDROID;

    private EntityRegistry() {
    }
}
