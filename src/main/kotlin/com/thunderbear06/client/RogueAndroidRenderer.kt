package com.thunderbear06.client

import com.thunderbear06.CCAndroids
import com.thunderbear06.entity.android.RogueDroidEntity
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation

class RogueAndroidRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<RogueDroidEntity, HumanoidModel<RogueDroidEntity>>(context, HumanoidModel(context.bakeLayer(ModelLayers.PLAYER)), 0.5f) {
    init {
        addLayer(EmissiveTextureLayer(this) { EMISSIVE })
    }

    override fun getTextureLocation(entity: RogueDroidEntity): ResourceLocation = TEXTURE

    companion object {
        private val TEXTURE = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/android_rogue.png")
        private val EMISSIVE = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/emissive/android_rogue_e.png")
    }
}
