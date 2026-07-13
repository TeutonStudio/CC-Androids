package com.thunderbear06.tags

import com.thunderbear06.CCAndroids
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item

object TagRegistry {
    @JvmField val MINOR_ANDROID_FUEL: TagKey<Item> = register("minor_android_fuel")
    @JvmField val MEDIUM_ANDROID_FUEL: TagKey<Item> = register("medium_android_fuel")
    @JvmField val MAJOR_ANDROID_FUEL: TagKey<Item> = register("major_android_fuel")

    private fun register(name: String): TagKey<Item> =
        TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, name))
}
