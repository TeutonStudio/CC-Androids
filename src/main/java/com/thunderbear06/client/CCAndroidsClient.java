package com.thunderbear06.client;

import com.thunderbear06.CCAndroids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = CCAndroids.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class CCAndroidsClient {
    private CCAndroidsClient() {
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(CCAndroids.ANDROID.get(), AndroidRenderer::new);
        event.registerEntityRenderer(CCAndroids.ADVANCED_ANDROID.get(), AndroidRenderer::new);
        event.registerEntityRenderer(CCAndroids.COMMAND_ANDROID.get(), AndroidRenderer::new);
        event.registerEntityRenderer(CCAndroids.UNFINISHED_ANDROID.get(), AndroidRenderer::new);
        event.registerEntityRenderer(CCAndroids.ROGUE_ANDROID.get(), AndroidRenderer::new);
    }
}
