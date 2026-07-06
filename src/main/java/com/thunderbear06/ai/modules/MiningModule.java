package com.thunderbear06.ai.modules;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;

public class MiningModule extends AbstractAndroidModule {
    private float breakProgress;

    public MiningModule(BaseAndroidEntity android, AndroidBrain brain) {
        super(android, brain);
    }

    public boolean canMineBlock(BlockPos pos) {
        BlockState state = android.level().getBlockState(pos);
        return !state.isAir() && state.getDestroySpeed(android.level(), pos) >= 0.0F;
    }

    public void mine(BlockPos pos) {
        if (!canMineBlock(pos)) return;
        android.swing(InteractionHand.MAIN_HAND);
        breakProgress += Math.max(0.2F, android.getMainHandItem().getDestroySpeed(android.level().getBlockState(pos)));
        android.level().destroyBlockProgress(android.getId(), pos, Math.min(9, (int) breakProgress));
        if (breakProgress >= 10.0F) {
            android.level().destroyBlock(pos, true, android);
            resetBreakProgress(pos);
        }
    }

    public void resetBreakProgress(BlockPos pos) {
        android.level().destroyBlockProgress(android.getId(), pos, -1);
        breakProgress = 0.0F;
    }
}
