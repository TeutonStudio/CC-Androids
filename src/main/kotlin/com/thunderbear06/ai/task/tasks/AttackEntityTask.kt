package com.thunderbear06.ai.task.tasks

import com.thunderbear06.entity.android.AndroidEntity
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.LivingEntity

class AttackEntityTask(android: AndroidEntity, moveSpeed: Double, entity: LivingEntity) : MoveToEntityTask(android, moveSpeed, entity) {
    private var attackCooldown = 0

    override val name: String = "attacking"

    override fun tick() {
        if (attackCooldown-- > 0) return
        if (isInRange(2.0)) {
            attackCooldown = 10
            android.lookControl.setLookAt(target)
            android.swing(InteractionHand.MAIN_HAND)
            android.doHurtTarget(target)
        } else {
            super.tick()
        }
    }
}
