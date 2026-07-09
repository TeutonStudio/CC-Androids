package com.thunderbear06.client;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.entity.android.RogueDroidEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RogueAndroidRenderer extends MobRenderer<RogueDroidEntity, HumanoidModel<RogueDroidEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/android_rogue.png");
    private static final ResourceLocation EMISSIVE = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/entity/emissive/android_rogue_e.png");

    public RogueAndroidRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
        addLayer(new EmissiveTextureLayer<>(this, entity -> EMISSIVE));
    }

    @Override
    public ResourceLocation getTextureLocation(RogueDroidEntity entity) {
        return TEXTURE;
    }
}
