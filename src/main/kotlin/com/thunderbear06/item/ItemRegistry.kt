package com.thunderbear06.item

import com.thunderbear06.CCAndroids
import net.minecraft.world.item.Item
import net.minecraft.world.item.SpawnEggItem
import net.neoforged.neoforge.registries.DeferredItem

object ItemRegistry {
    @JvmField val WRENCH: DeferredItem<Item> = CCAndroids.WRENCH
    @JvmField val COMPONENTS: DeferredItem<Item> = CCAndroids.COMPONENTS
    @JvmField val ANDROID_CPU: DeferredItem<Item> = CCAndroids.ANDROID_CPU
    @JvmField val REDSTONE_REACTOR: DeferredItem<Item> = CCAndroids.REDSTONE_REACTOR
    @JvmField val ANDROID_FRAME: DeferredItem<Item> = CCAndroids.ANDROID_FRAME
    @JvmField val ANDROID_SPAWN_EGG: DeferredItem<SpawnEggItem> = CCAndroids.ANDROID_SPAWN
    @JvmField val ANDROID_ADVANCED_SPAWN_EGG: DeferredItem<SpawnEggItem> = CCAndroids.ADVANCED_ANDROID_SPAWN
    @JvmField val ANDROID_COMMAND_SPAWN_EGG: DeferredItem<SpawnEggItem> = CCAndroids.COMMAND_ANDROID_SPAWN
    @JvmField val ANDROID_ROGUE_SPAWN_EGG: DeferredItem<SpawnEggItem> = CCAndroids.ROGUE_ANDROID_SPAWN
}
