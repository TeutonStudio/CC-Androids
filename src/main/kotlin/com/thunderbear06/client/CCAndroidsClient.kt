package com.thunderbear06.client

import com.thunderbear06.CCAndroids
import com.thunderbear06.screen.AndroidScreen
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent

@EventBusSubscriber(modid = CCAndroids.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object CCAndroidsClient {
    @SubscribeEvent
    @JvmStatic
    fun registerEntityRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerEntityRenderer(CCAndroids.ANDROID.get(), ::AndroidRenderer)
        event.registerEntityRenderer(CCAndroids.ADVANCED_ANDROID.get(), ::AndroidRenderer)
        event.registerEntityRenderer(CCAndroids.COMMAND_ANDROID.get(), ::AndroidRenderer)
        event.registerEntityRenderer(CCAndroids.UNFINISHED_ANDROID.get(), ::AndroidFrameRenderer)
        event.registerEntityRenderer(CCAndroids.ROGUE_ANDROID.get(), ::RogueAndroidRenderer)
    }

    @SubscribeEvent
    @JvmStatic
    fun registerScreens(event: RegisterMenuScreensEvent) {
        event.register(CCAndroids.ANDROID_MENU.get(), ::AndroidScreen)
    }
}
