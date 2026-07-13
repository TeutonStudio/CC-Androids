package com.thunderbear06.entity.android

import com.thunderbear06.CCAndroids
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.entity.npc.AbstractVillager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class RogueDroidEntity(entityType: EntityType<out Monster>, level: Level) : Monster(entityType, level) {
    override fun registerGoals() {
        goalSelector.addGoal(1, MeleeAttackGoal(this, 0.6, false))
        goalSelector.addGoal(2, WaterAvoidingRandomStrollGoal(this, 0.6))
        goalSelector.addGoal(3, LookAtPlayerGoal(this, Player::class.java, 8.0f))
        goalSelector.addGoal(4, RandomLookAroundGoal(this))
        targetSelector.addGoal(1, NearestAttackableTargetGoal(this, Player::class.java, true))
        targetSelector.addGoal(2, NearestAttackableTargetGoal(this, AbstractVillager::class.java, false))
    }

    override fun getAmbientSound(): SoundEvent = CCAndroids.ANDROID_AMBIENT.get()

    override fun getHurtSound(source: DamageSource): SoundEvent = CCAndroids.ANDROID_HURT.get()

    override fun getDeathSound(): SoundEvent = CCAndroids.ANDROID_DEATH.get()

    companion object {
        @JvmStatic
        fun createAndroidAttributes(): AttributeSupplier.Builder =
            createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, CCAndroids.CONFIG.RogueMaxHealth.toDouble())
                .add(Attributes.ATTACK_DAMAGE, CCAndroids.CONFIG.RogueDamage.toDouble())
                .add(Attributes.MOVEMENT_SPEED, CCAndroids.CONFIG.RogueSpeed.toDouble())
                .add(Attributes.ARMOR, CCAndroids.CONFIG.RogueArmor.toDouble())
    }
}
