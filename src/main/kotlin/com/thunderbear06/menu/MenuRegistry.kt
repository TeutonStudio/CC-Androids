package com.thunderbear06.menu

import com.thunderbear06.CCAndroids
import net.minecraft.world.inventory.MenuType
import net.neoforged.neoforge.registries.DeferredHolder

object MenuRegistry {
    @JvmField val ANDROID: DeferredHolder<MenuType<*>, MenuType<AndroidMenu>> = CCAndroids.ANDROID_MENU
}
