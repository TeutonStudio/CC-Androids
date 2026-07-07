package com.thunderbear06.entity.android;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.ai.AndroidBrain;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class AndroidEntity extends BaseAndroidEntity {
    private static final EntityDataAccessor<Boolean> LOCKED = SynchedEntityData.defineId(AndroidEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Byte> VARIANT = SynchedEntityData.defineId(AndroidEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> FACE = SynchedEntityData.defineId(AndroidEntity.class, EntityDataSerializers.BYTE);

    public AndroidEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        brain = new AndroidBrain(this);
        computerContainer.setFamily(ComputerFamily.NORMAL);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(LOCKED, false);
        builder.define(VARIANT, (byte) 0);
        builder.define(FACE, (byte) 0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 10.0F));
    }

    public static AttributeSupplier.Builder createAndroidAttributes() {
        return createMobAttributes()
                .add(Attributes.MAX_HEALTH, CCAndroids.CONFIG.AndroidMaxHealth)
                .add(Attributes.ATTACK_DAMAGE, CCAndroids.CONFIG.AndroidDamage)
                .add(Attributes.MOVEMENT_SPEED, CCAndroids.CONFIG.AndroidSpeed)
                .add(Attributes.ARMOR, CCAndroids.CONFIG.AndroidArmor);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide) {
            if (hasFuel()) brain.getTaskManager().tick();
            else getNavigation().stop();
        }
    }

    @Override
    protected boolean isIdle() {
        return brain.getTaskManager().isIdle();
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (isLocked() && !brain.isOwningPlayer(player)) {
            player.displayClientMessage(Component.translatable("entity.cc_androids.android.locked"), true);
            playSound(SoundEvents.IRON_TRAPDOOR_CLOSE, 1.0F, 1.0F);
            return InteractionResult.FAIL;
        }
        if (player.isShiftKeyDown()) {
            ItemStack playerStack = player.getItemInHand(hand);
            ItemStack androidStack = getMainHandItem().copy();
            setItemInHand(InteractionHand.MAIN_HAND, playerStack.copy());
            player.setItemInHand(hand, androidStack);
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(CCAndroids.COMPONENTS.get()) && getHealth() < getMaxHealth()) {
            heal(5.0F);
            stack.shrink(1);
            playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, 1.0F);
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        if (stack.is(Items.GRAY_DYE)) {
            setVariant((byte) 1);
            stack.shrink(player.getAbilities().instabuild ? 0 : 1);
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        if (stack.is(Items.PINK_DYE)) {
            setVariant((byte) 2);
            stack.shrink(player.getAbilities().instabuild ? 0 : 1);
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        if (!level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            if (brain.getOwningPlayerProfile() == null) brain.setOwningPlayer(player.getGameProfile());
            getComputer().openComputer(serverPlayer);
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    public boolean isLocked() {
        return entityData.get(LOCKED);
    }

    public void setLocked(boolean locked) {
        entityData.set(LOCKED, locked);
    }

    public byte getVariant() {
        return entityData.get(VARIANT);
    }

    public void setVariant(byte variant) {
        entityData.set(VARIANT, variant);
    }

    public byte getFace() {
        return entityData.get(FACE);
    }

    public void setFace(String faceName) {
        entityData.set(FACE, switch (faceName) {
            case "angry" -> (byte) 1;
            case "annoyed" -> (byte) 2;
            case "happy" -> (byte) 3;
            case "sad" -> (byte) 4;
            case "woozy" -> (byte) 5;
            case "command" -> (byte) 6;
            default -> (byte) 0;
        });
    }

    public void deconstruct() {
        spawnAtLocation(new ItemStack(CCAndroids.ANDROID_FRAME.get()));
        discard();
    }

    public void dropConstructionMaterials(boolean fullConstructionRefund) {
        float refund = fullConstructionRefund ? 1.0F : CCAndroids.CONFIG.CompsDroppedOnDeathPercentage;
        int components = Math.round(CCAndroids.CONFIG.CompsForConstruction * refund);
        if (components > 0) spawnAtLocation(new ItemStack(CCAndroids.COMPONENTS.get(), components));

        float ingotRefund = fullConstructionRefund ? 1.0F : CCAndroids.CONFIG.IngotsDroppedOnDeathPercentage;
        int ingots = Math.round(CCAndroids.CONFIG.IngotsForConstruction * ingotRefund);
        if (ingots > 0) spawnAtLocation(new ItemStack(this instanceof AdvancedAndroidEntity ? Items.GOLD_INGOT : Items.IRON_INGOT, ingots));
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

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Locked", isLocked());
        tag.putByte("Variant", getVariant());
        tag.putByte("Face", getFace());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setLocked(tag.getBoolean("Locked"));
        setVariant(tag.getByte("Variant"));
        entityData.set(FACE, tag.getByte("Face"));
    }
}
