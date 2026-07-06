package com.thunderbear06.ai.task.tasks;

import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;

public class InteractBlockTask extends MoveToBlockTask {
    private boolean complete;

    public InteractBlockTask(AndroidEntity android, double moveSpeed, BlockPos pos) {
        super(android, moveSpeed, pos);
    }

    @Override
    public String getName() {
        return "usingBlock";
    }

    @Override
    public boolean shouldTick() {
        return !complete;
    }

    @Override
    public void tick() {
        if (isInRange(2.5D)) {
            android.getLookControl().setLookAt(getTarget().getX(), getTarget().getY(), getTarget().getZ());
            android.brain.getModules().interactionModule.interactWithBlock(InteractionHand.MAIN_HAND, getTarget());
            complete = true;
        } else {
            super.tick();
        }
    }
}
