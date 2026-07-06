package com.thunderbear06.client;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class AndroidRenderer extends MobRenderer<AndroidEntity, HumanoidModel<AndroidEntity>> {
    private static final ResourceLocation NORMAL = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/android_normal.png");
    private static final ResourceLocation ADVANCED = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/android_advanced.png");
    private static final ResourceLocation COMMAND = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/android_command.png");
    private static final ResourceLocation PINKY = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/variant/android_pinky.png");
    private static final ResourceLocation KAYLON = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/variant/android_kaylon.png");

    public AndroidRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(AndroidEntity entity) {
        if (entity.getVariant() == 1) return KAYLON;
        if (entity.getVariant() == 2) return PINKY;
        if (entity.getType() == CCAndroids.ADVANCED_ANDROID.get()) return ADVANCED;
        if (entity.getType() == CCAndroids.COMMAND_ANDROID.get()) return COMMAND;
        return NORMAL;
    }
}
