package com.thunderbear06.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.thunderbear06.CCAndroids;
import com.thunderbear06.entity.AndroidEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class AndroidRenderer extends MobRenderer<AndroidEntity, HumanoidModel<AndroidEntity>> {
    private static final ResourceLocation NORMAL = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/android_normal.png");
    private static final ResourceLocation ADVANCED = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/android_advanced.png");
    private static final ResourceLocation COMMAND = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/android_command.png");
    private static final ResourceLocation ROGUE = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/android_rogue.png");
    private static final ResourceLocation UNFINISHED = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/android_unfinished.png");

    public AndroidRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(AndroidEntity entity) {
        if (entity.getType() == CCAndroids.ADVANCED_ANDROID.get()) {
            return ADVANCED;
        }
        if (entity.getType() == CCAndroids.COMMAND_ANDROID.get()) {
            return COMMAND;
        }
        if (entity.getType() == CCAndroids.ROGUE_ANDROID.get()) {
            return ROGUE;
        }
        if (entity.getType() == CCAndroids.UNFINISHED_ANDROID.get()) {
            return UNFINISHED;
        }
        return NORMAL;
    }

    @Override
    public void render(AndroidEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(0.92F, 0.92F, 0.92F);
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        poseStack.popPose();
    }
}
