package com.thunderbear06.ai.modules;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class MiningModule extends AbstractAndroidModule {
    private float breakProgress;
    private BlockPos breakingPos;

    public MiningModule(BaseAndroidEntity android, AndroidBrain brain) {
        super(android, brain);
    }

    public boolean canMineBlock(BlockPos pos) {
        ServerPlayer player = android.asFakePlayer();
        if (player == null) return false;
        player.setItemInHand(InteractionHand.MAIN_HAND, android.getMainHandItem().copy());
        BlockState state = android.level().getBlockState(pos);
        return !state.isAir() && state.getDestroySpeed(android.level(), pos) >= 0.0F && state.getDestroyProgress(player, android.level(), pos) > 0.0F;
    }

    public void mine(BlockPos pos) {
        ServerPlayer player = android.asFakePlayer();
        if (player == null) return;
        BlockState state = android.level().getBlockState(pos);
        if (state.isAir() || state.getDestroySpeed(android.level(), pos) < 0.0F) {
            resetBreakProgress(pos);
            return;
        }
        if (breakingPos != null && !breakingPos.equals(pos)) resetBreakProgress(breakingPos);
        breakingPos = pos.immutable();
        ItemStack originalTool = android.getMainHandItem();
        player.setItemInHand(InteractionHand.MAIN_HAND, originalTool.copy());
        float destroyProgress = state.getDestroyProgress(player, android.level(), pos);
        if (destroyProgress <= 0.0F) {
            resetBreakProgress(pos);
            return;
        }
        android.swing(InteractionHand.MAIN_HAND);
        breakProgress += destroyProgress;
        android.level().destroyBlockProgress(android.getId(), pos, Math.min(9, (int) (breakProgress * 10.0F)));
        if (breakProgress >= 1.0F) {
            player.gameMode.destroyBlock(pos);
            android.setItemInHand(InteractionHand.MAIN_HAND, player.getMainHandItem().copy());
            resetBreakProgress(pos);
        }
    }

    public void resetBreakProgress(BlockPos pos) {
        BlockPos progressPos = breakingPos == null ? pos : breakingPos;
        android.level().destroyBlockProgress(android.getId(), progressPos, -1);
        breakProgress = 0.0F;
        breakingPos = null;
    }
}
