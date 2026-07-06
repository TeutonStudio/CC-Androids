package com.thunderbear06.entity.android;

import com.thunderbear06.CCAndroids;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class RogueDroidEntity extends Monster {
    public RogueDroidEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAndroidAttributes() {
        return createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, CCAndroids.CONFIG.RogueMaxHealth)
                .add(Attributes.ATTACK_DAMAGE, CCAndroids.CONFIG.RogueDamage)
                .add(Attributes.MOVEMENT_SPEED, CCAndroids.CONFIG.RogueSpeed)
                .add(Attributes.ARMOR, CCAndroids.CONFIG.RogueArmor);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 0.6D, false));
        goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return CCAndroids.ANDROID_AMBIENT.get();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return CCAndroids.ANDROID_HURT.get();
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return CCAndroids.ANDROID_DEATH.get();
    }
}
