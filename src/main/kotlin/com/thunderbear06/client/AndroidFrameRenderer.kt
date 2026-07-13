package com.thunderbear06.client

import com.thunderbear06.CCAndroids
import com.thunderbear06.entity.android.frame.AndroidFrame
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation

class AndroidFrameRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<AndroidFrame, HumanoidModel<AndroidFrame>>(context, HumanoidModel(context.bakeLayer(ModelLayers.PLAYER)), 0.5f) {
    override fun getTextureLocation(entity: AndroidFrame): ResourceLocation =
        if (entity.hasCore()) CORE_TEXTURE else TEXTURE

    companion object {
        private val TEXTURE = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/android_unfinished.png")
        private val CORE_TEXTURE = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/android_unfinished_core.png")
    }
}
