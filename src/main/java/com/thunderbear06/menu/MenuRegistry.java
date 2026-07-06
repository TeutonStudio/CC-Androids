package com.thunderbear06.menu;

import com.thunderbear06.CCAndroids;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class MenuRegistry {
    public static final DeferredHolder<MenuType<?>, MenuType<AndroidMenu>> ANDROID = CCAndroids.ANDROID_MENU;

    private MenuRegistry() {
    }
}
