package com.thunderbear06.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Function;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

final class EmissiveTextureLayer<T extends Entity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private final Function<T, ResourceLocation> texture;

    EmissiveTextureLayer(RenderLayerParent<T, M> parent, Function<T, ResourceLocation> texture) {
        super(parent);
        this.texture = texture;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        ResourceLocation location = texture.apply(entity);
        if (location == null) return;
        VertexConsumer vertices = buffer.getBuffer(RenderType.entityTranslucentEmissive(location));
        getParentModel().renderToBuffer(poseStack, vertices, 0xF000F0, OverlayTexture.NO_OVERLAY);
    }
}
