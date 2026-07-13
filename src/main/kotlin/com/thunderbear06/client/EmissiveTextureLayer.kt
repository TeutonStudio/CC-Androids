package com.thunderbear06.client

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity

internal class EmissiveTextureLayer<T : Entity, M : EntityModel<T>>(
    parent: RenderLayerParent<T, M>,
    private val texture: (T) -> ResourceLocation?,
) : RenderLayer<T, M>(parent) {
    override fun render(
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int,
        entity: T,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTicks: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float,
    ) {
        val location = texture(entity) ?: return
        val vertices = buffer.getBuffer(RenderType.entityTranslucentEmissive(location))
        parentModel.renderToBuffer(poseStack, vertices, 0xF000F0, OverlayTexture.NO_OVERLAY)
    }
}
