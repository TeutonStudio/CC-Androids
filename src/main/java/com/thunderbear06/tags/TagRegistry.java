package com.thunderbear06.tags;

import com.thunderbear06.CCAndroids;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class TagRegistry {
    public static final TagKey<Item> MINOR_ANDROID_FUEL = register("minor_android_fuel");
    public static final TagKey<Item> MEDIUM_ANDROID_FUEL = register("medium_android_fuel");
    public static final TagKey<Item> MAJOR_ANDROID_FUEL = register("major_android_fuel");

    private TagRegistry() {
    }

    private static TagKey<Item> register(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, name));
    }
}
