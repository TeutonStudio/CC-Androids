package com.thunderbear06.item;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.entity.android.frame.AndroidFrame;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AndroidFrameItem extends Item {
    public AndroidFrameItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getClickedFace().getAxis().isVertical() && context.getClickedFace().getStepY() < 0) return InteractionResult.FAIL;
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        Vec3 center = Vec3.atBottomCenterOf(pos);
        AABB box = CCAndroids.UNFINISHED_ANDROID.get().getDimensions().makeBoundingBox(center.x, center.y, center.z);
        if (!level.noCollision(box) || !level.getEntities(null, box).isEmpty()) return InteractionResult.FAIL;
        if (!level.isClientSide) {
            AndroidFrame frame = CCAndroids.UNFINISHED_ANDROID.get().create(level);
            if (frame == null) return InteractionResult.FAIL;
            frame.moveTo(center.x, center.y, center.z, context.getRotation(), 0.0F);
            level.addFreshEntity(frame);
            level.playSound(null, frame, SoundEvents.ANVIL_LAND, net.minecraft.sounds.SoundSource.BLOCKS, 0.75F, 0.8F);
        }
        context.getItemInHand().shrink(1);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
