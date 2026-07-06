package com.thunderbear06.item;

import com.thunderbear06.CCAndroids;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;

public final class ItemRegistry {
    public static final DeferredItem<Item> WRENCH = CCAndroids.WRENCH;
    public static final DeferredItem<Item> COMPONENTS = CCAndroids.COMPONENTS;
    public static final DeferredItem<Item> ANDROID_CPU = CCAndroids.ANDROID_CPU;
    public static final DeferredItem<Item> REDSTONE_REACTOR = CCAndroids.REDSTONE_REACTOR;
    public static final DeferredItem<Item> ANDROID_FRAME = CCAndroids.ANDROID_FRAME;
    public static final DeferredItem<SpawnEggItem> ANDROID_SPAWN_EGG = CCAndroids.ANDROID_SPAWN;
    public static final DeferredItem<SpawnEggItem> ANDROID_ADVANCED_SPAWN_EGG = CCAndroids.ADVANCED_ANDROID_SPAWN;
    public static final DeferredItem<SpawnEggItem> ANDROID_COMMAND_SPAWN_EGG = CCAndroids.COMMAND_ANDROID_SPAWN;
    public static final DeferredItem<SpawnEggItem> ANDROID_ROGUE_SPAWN_EGG = CCAndroids.ROGUE_ANDROID_SPAWN;

    private ItemRegistry() {
    }
}
