package com.thunderbear06.entity.android

import com.thunderbear06.CCAndroids
import dan200.computercraft.shared.computer.core.ComputerFamily
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.level.Level

open class AdvancedAndroidEntity(entityType: EntityType<out PathfinderMob>, level: Level) : AndroidEntity(entityType, level) {
    init {
        computerContainer.family = ComputerFamily.ADVANCED
    }

    override val entitySearchRadius: Double
        get() = super.entitySearchRadius * 3.0

    override val blockSearchRadius: Int
        get() = super.blockSearchRadius * 3

    companion object {
        @JvmStatic
        fun createAndroidAttributes(): AttributeSupplier.Builder =
            createMobAttributes()
                .add(Attributes.MAX_HEALTH, CCAndroids.CONFIG.AdvAndroidMaxHealth.toDouble())
                .add(Attributes.ATTACK_DAMAGE, CCAndroids.CONFIG.AdvAndroidDamage.toDouble())
                .add(Attributes.MOVEMENT_SPEED, CCAndroids.CONFIG.AdvAndroidSpeed.toDouble())
                .add(Attributes.ARMOR, CCAndroids.CONFIG.AdvAndroidArmor.toDouble())
    }
}
