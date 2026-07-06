package com.thunderbear06.ai.task.tasks;

import com.thunderbear06.ai.task.Task;
import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.core.BlockPos;

public abstract class BlockBasedTask extends Task {
    private final BlockPos target;

    protected BlockBasedTask(AndroidEntity android, BlockPos target) {
        super(android);
        this.target = target;
    }

    protected boolean isInRange(double distance) {
        return target.closerThan(android.blockPosition(), distance);
    }

    protected BlockPos getTarget() {
        return target;
    }
}
