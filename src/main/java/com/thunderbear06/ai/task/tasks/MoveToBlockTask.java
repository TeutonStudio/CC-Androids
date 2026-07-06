package com.thunderbear06.ai.task.tasks;

import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.navigation.PathNavigation;

public class MoveToBlockTask extends BlockBasedTask {
    private final double moveSpeed;
    private final PathNavigation navigation;

    public MoveToBlockTask(AndroidEntity android, double moveSpeed, BlockPos pos) {
        super(android, pos);
        this.moveSpeed = moveSpeed;
        this.navigation = android.getNavigation();
    }

    @Override
    public String getName() {
        return "movingToBlock";
    }

    @Override
    public boolean shouldTick() {
        return !isInRange(2.0D);
    }

    @Override
    public void firstTick() {
    }

    @Override
    public void tick() {
        if (navigation.isDone()) {
            navigation.moveTo(getTarget().getX(), getTarget().getY(), getTarget().getZ(), moveSpeed);
        }
    }

    @Override
    public void lastTick() {
        navigation.stop();
    }
}
