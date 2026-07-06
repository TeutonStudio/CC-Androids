package com.thunderbear06.client;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.entity.android.frame.AndroidFrame;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class AndroidFrameRenderer extends MobRenderer<AndroidFrame, HumanoidModel<AndroidFrame>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/android_unfinished.png");

    public AndroidFrameRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(AndroidFrame entity) {
        return TEXTURE;
    }
}
