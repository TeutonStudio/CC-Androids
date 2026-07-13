package com.thunderbear06.client

import com.thunderbear06.CCAndroids
import com.thunderbear06.entity.android.AndroidEntity
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation

class AndroidRenderer(context: EntityRendererProvider.Context) :
    MobRenderer<AndroidEntity, HumanoidModel<AndroidEntity>>(context, HumanoidModel(context.bakeLayer(ModelLayers.PLAYER)), 0.5f) {
    init {
        addLayer(EmissiveTextureLayer(this) { CORE })
        addLayer(EmissiveTextureLayer(this, Companion::variantEmissive))
        addLayer(EmissiveTextureLayer(this, Companion::faceEmissive))
    }

    override fun getTextureLocation(entity: AndroidEntity): ResourceLocation {
        if (entity.variant.toInt() == 1) return KAYLON
        if (entity.variant.toInt() == 2) return PINKY
        if (entity.type == CCAndroids.ADVANCED_ANDROID.get()) return ADVANCED
        if (entity.type == CCAndroids.COMMAND_ANDROID.get()) return COMMAND
        return NORMAL
    }

    companion object {
        private val NORMAL = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/android_normal.png")
        private val ADVANCED = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/android_advanced.png")
        private val COMMAND = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/android_command.png")
        private val PINKY = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/variant/android_pinky.png")
        private val KAYLON = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/variant/android_kaylon.png")
        private val CORE = emissive("core.png")
        private val KAYLON_EMISSIVE = emissive("variant/android_kaylon.png")
        private val PINKY_EMISSIVE = emissive("variant/android_pinky.png")

        private fun variantEmissive(entity: AndroidEntity): ResourceLocation? =
            when (entity.variant.toInt()) {
                1 -> KAYLON_EMISSIVE
                2 -> PINKY_EMISSIVE
                else -> null
            }

        private fun faceEmissive(entity: AndroidEntity): ResourceLocation {
            val face = when (entity.face.toInt()) {
                1 -> "anger"
                2 -> "annoyed"
                3 -> "happy"
                4 -> "sad"
                5 -> "woozy"
                6 -> "command"
                else -> if (entity.type == CCAndroids.COMMAND_ANDROID.get()) "command" else "normal"
            }
            return emissive("face/$face.png")
        }

        private fun emissive(path: String): ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/emissive/$path")
    }
}
