package com.thunderbear06.entity.android.frame;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

public class AndroidFrame extends Mob {
    private static final EntityDataAccessor<Byte> COMPONENTS_NEEDED = SynchedEntityData.defineId(AndroidFrame.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> INGOTS_NEEDED = SynchedEntityData.defineId(AndroidFrame.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> HAS_CORE = SynchedEntityData.defineId(AndroidFrame.class, EntityDataSerializers.BOOLEAN);
    private boolean advanced;

    public AndroidFrame(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(COMPONENTS_NEEDED, CCAndroids.CONFIG.CompsForConstruction);
        builder.define(INGOTS_NEEDED, CCAndroids.CONFIG.IngotsForConstruction);
        builder.define(HAS_CORE, false);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(CCAndroids.COMPONENTS.get()) && getComponentsNeeded() > 0) {
            entityData.set(COMPONENTS_NEEDED, (byte) (getComponentsNeeded() - 1));
            consume(stack, player);
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        if ((stack.is(Items.IRON_INGOT) || stack.is(Items.GOLD_INGOT)) && getComponentsNeeded() == 0 && getIngotsNeeded() > 0) {
            if (stack.is(Items.GOLD_INGOT)) advanced = true;
            entityData.set(INGOTS_NEEDED, (byte) (getIngotsNeeded() - 1));
            consume(stack, player);
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        if (stack.is(CCAndroids.REDSTONE_REACTOR.get()) && !hasCore()) {
            entityData.set(HAS_CORE, true);
            consume(stack, player);
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        if ((stack.is(CCAndroids.ANDROID_CPU.get()) || stack.is(Items.COMMAND_BLOCK)) && readyForCpu()) {
            finish(stack.is(Items.COMMAND_BLOCK) ? ComputerFamily.COMMAND : advanced ? ComputerFamily.ADVANCED : ComputerFamily.NORMAL, readComputerId(stack));
            consume(stack, player);
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        return InteractionResult.FAIL;
    }

    private void consume(ItemStack stack, Player player) {
        if (!player.getAbilities().instabuild) stack.shrink(1);
        playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, 1.0F);
    }

    private boolean readyForCpu() {
        return getComponentsNeeded() == 0 && getIngotsNeeded() == 0 && hasCore();
    }

    private int readComputerId(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null || !data.contains("ComputerID")) return -1;
        return data.copyTag().getInt("ComputerID");
    }

    private void finish(ComputerFamily family, int computerID) {
        if (level().isClientSide) return;
        BaseAndroidEntity android = switch (family) {
            case ADVANCED -> CCAndroids.ADVANCED_ANDROID.get().create(level());
            case COMMAND -> CCAndroids.COMMAND_ANDROID.get().create(level());
            default -> CCAndroids.ANDROID.get().create(level());
        };
        if (android == null) return;
        android.moveTo(getX(), getY(), getZ(), getYRot(), getXRot());
        android.getComputer().setFamily(family);
        if (computerID >= 0) android.getComputer().setComputerID(computerID);
        discard();
        level().addFreshEntity(android);
        android.playSound(SoundEvents.BEACON_ACTIVATE, 1.0F, 1.0F);
    }

    public void onBreak() {
        spawnAtLocation(new ItemStack(CCAndroids.ANDROID_FRAME.get()));
        discard();
    }

    public byte getComponentsNeeded() {
        return entityData.get(COMPONENTS_NEEDED);
    }

    public byte getIngotsNeeded() {
        return entityData.get(INGOTS_NEEDED);
    }

    public boolean hasCore() {
        return entityData.get(HAS_CORE);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("ComponentsNeeded", getComponentsNeeded());
        tag.putByte("IngotsNeeded", getIngotsNeeded());
        tag.putBoolean("HasCore", hasCore());
        tag.putBoolean("Advanced", advanced);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(COMPONENTS_NEEDED, tag.getByte("ComponentsNeeded"));
        entityData.set(INGOTS_NEEDED, tag.getByte("IngotsNeeded"));
        entityData.set(HAS_CORE, tag.getBoolean("HasCore"));
        advanced = tag.getBoolean("Advanced");
    }
}
