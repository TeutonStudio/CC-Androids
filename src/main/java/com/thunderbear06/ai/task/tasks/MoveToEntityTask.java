package com.thunderbear06.ai.task.tasks;

import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.navigation.PathNavigation;

public class MoveToEntityTask extends EntityBasedTask {
    private final double moveSpeed;
    private final PathNavigation navigation;

    public MoveToEntityTask(AndroidEntity android, double moveSpeed, LivingEntity entity) {
        super(android, entity);
        this.moveSpeed = moveSpeed;
        this.navigation = android.getNavigation();
    }

    @Override
    public String getName() {
        return "movingToEntity";
    }

    @Override
    public boolean shouldTick() {
        return super.shouldTick() && !isInRange(1.5D);
    }

    @Override
    public void firstTick() {
    }

    @Override
    public void tick() {
        super.tick();
        if (navigation.isDone()) navigation.moveTo(getTarget(), moveSpeed);
    }

    @Override
    public void lastTick() {
        navigation.stop();
    }
}
