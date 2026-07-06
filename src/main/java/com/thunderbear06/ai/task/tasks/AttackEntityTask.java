package com.thunderbear06.ai.task.tasks;

import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;

public class AttackEntityTask extends MoveToEntityTask {
    private int attackCooldown;

    public AttackEntityTask(AndroidEntity android, double moveSpeed, LivingEntity entity) {
        super(android, moveSpeed, entity);
    }

    @Override
    public String getName() {
        return "attacking";
    }

    @Override
    public void tick() {
        if (attackCooldown-- > 0) return;
        if (isInRange(2.0D)) {
            attackCooldown = 10;
            android.getLookControl().setLookAt(getTarget());
            android.swing(InteractionHand.MAIN_HAND);
            android.doHurtTarget(getTarget());
        } else {
            super.tick();
        }
    }
}
