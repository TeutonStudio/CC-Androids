package com.thunderbear06.entity.android

import com.thunderbear06.CCAndroids
import dan200.computercraft.shared.computer.core.ComputerFamily
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.level.Level

class CommandAndroidEntity(entityType: EntityType<out PathfinderMob>, level: Level) : AdvancedAndroidEntity(entityType, level) {
    init {
        computerContainer.family = ComputerFamily.COMMAND
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean =
        source.isCreativePlayer && super.hurt(source, amount)

    override fun isInvulnerableTo(source: DamageSource): Boolean =
        !source.isCreativePlayer || super.isInvulnerableTo(source)

    override fun hasFuel(): Boolean = true

    companion object {
        @JvmStatic
        fun createAndroidAttributes(): AttributeSupplier.Builder =
            createMobAttributes()
                .add(Attributes.MAX_HEALTH, CCAndroids.CONFIG.ComAndroidMaxHealth.toDouble())
                .add(Attributes.ATTACK_DAMAGE, CCAndroids.CONFIG.ComAndroidDamage.toDouble())
                .add(Attributes.MOVEMENT_SPEED, CCAndroids.CONFIG.ComAndroidSpeed.toDouble())
                .add(Attributes.ARMOR, CCAndroids.CONFIG.ComAndroidArmor.toDouble())
    }
}
