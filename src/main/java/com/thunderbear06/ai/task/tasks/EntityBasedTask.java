package com.thunderbear06.ai.task.tasks;

import com.thunderbear06.ai.task.Task;
import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.world.entity.LivingEntity;

public abstract class EntityBasedTask extends Task {
    private final LivingEntity target;

    protected EntityBasedTask(AndroidEntity android, LivingEntity target) {
        super(android);
        this.target = target;
    }

    @Override
    public boolean shouldTick() {
        return target.isAlive();
    }

    @Override
    public void tick() {
        android.getLookControl().setLookAt(target);
    }

    protected boolean isInRange(double distance) {
        return android.distanceToSqr(target) <= distance * distance;
    }

    protected LivingEntity getTarget() {
        return target;
    }
}
