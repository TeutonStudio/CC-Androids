package com.thunderbear06.entity.android

import com.thunderbear06.CCAndroids
import com.thunderbear06.ai.AndroidBrain
import dan200.computercraft.shared.computer.core.ComputerFamily
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import kotlin.math.roundToInt

open class AndroidEntity(entityType: EntityType<out PathfinderMob>, level: Level) : BaseAndroidEntity(entityType, level) {
    init {
        brain = AndroidBrain(this)
        computerContainer.family = ComputerFamily.NORMAL
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(LOCKED, false)
        builder.define(VARIANT, 0.toByte())
        builder.define(FACE, 0.toByte())
    }

    override fun registerGoals() {
        goalSelector.addGoal(7, LookAtPlayerGoal(this, Player::class.java, 10.0f))
    }

    override fun aiStep() {
        super.aiStep()
        if (!level().isClientSide) {
            if (hasFuel()) brain.taskManager.tick() else navigation.stop()
        }
    }

    override fun isIdle(): Boolean = brain.taskManager.isIdle()

    override fun mobInteract(player: Player, hand: InteractionHand): InteractionResult {
        if (isLocked && !brain.isOwningPlayer(player)) {
            player.displayClientMessage(Component.translatable("entity.cc_androids.android.locked"), true)
            playSound(SoundEvents.IRON_TRAPDOOR_CLOSE, 1.0f, 1.0f)
            return InteractionResult.FAIL
        }
        if (player.isShiftKeyDown) {
            val playerStack = player.getItemInHand(hand)
            val androidStack = mainHandItem.copy()
            setItemInHand(InteractionHand.MAIN_HAND, playerStack.copy())
            player.setItemInHand(hand, androidStack)
            return InteractionResult.sidedSuccess(level().isClientSide)
        }
        val stack = player.getItemInHand(hand)
        if (stack.`is`(CCAndroids.COMPONENTS.get()) && health < maxHealth) {
            heal(5.0f)
            stack.shrink(1)
            playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0f, 1.0f)
            return InteractionResult.sidedSuccess(level().isClientSide)
        }
        if (stack.`is`(Items.GRAY_DYE)) {
            variant = 1
            stack.shrink(if (player.abilities.instabuild) 0 else 1)
            return InteractionResult.sidedSuccess(level().isClientSide)
        }
        if (stack.`is`(Items.PINK_DYE)) {
            variant = 2
            stack.shrink(if (player.abilities.instabuild) 0 else 1)
            return InteractionResult.sidedSuccess(level().isClientSide)
        }
        if (!level().isClientSide && player is ServerPlayer) {
            if (brain.owningPlayerProfile == null) brain.setOwningPlayer(player.gameProfile)
            getComputer().openComputer(player)
        }
        return InteractionResult.sidedSuccess(level().isClientSide)
    }

    var isLocked: Boolean
        get() = entityData.get(LOCKED)
        set(locked) = entityData.set(LOCKED, locked)

    var variant: Byte
        get() = entityData.get(VARIANT)
        set(variant) = entityData.set(VARIANT, variant)

    val face: Byte
        get() = entityData.get(FACE)

    fun setFace(faceName: String) {
        entityData.set(
            FACE,
            when (faceName) {
                "angry" -> 1
                "annoyed" -> 2
                "happy" -> 3
                "sad" -> 4
                "woozy" -> 5
                "command" -> 6
                else -> 0
            }.toByte(),
        )
    }

    fun deconstruct() {
        dropAndroidContents(true)
        spawnAtLocation(ItemStack(CCAndroids.ANDROID_FRAME.get()))
        discard()
    }

    fun dropConstructionMaterials(fullConstructionRefund: Boolean) {
        val refund = if (fullConstructionRefund) 1.0f else CCAndroids.CONFIG.CompsDroppedOnDeathPercentage
        val components = (CCAndroids.CONFIG.CompsForConstruction * refund).roundToInt()
        if (components > 0) spawnAtLocation(ItemStack(CCAndroids.COMPONENTS.get(), components))

        val ingotRefund = if (fullConstructionRefund) 1.0f else CCAndroids.CONFIG.IngotsDroppedOnDeathPercentage
        val ingots = (CCAndroids.CONFIG.IngotsForConstruction * ingotRefund).roundToInt()
        if (ingots > 0) spawnAtLocation(ItemStack(if (this is AdvancedAndroidEntity) Items.GOLD_INGOT else Items.IRON_INGOT, ingots))
    }

    override fun getAmbientSound(): SoundEvent = CCAndroids.ANDROID_AMBIENT.get()

    override fun getHurtSound(source: DamageSource): SoundEvent = CCAndroids.ANDROID_HURT.get()

    override fun getDeathSound(): SoundEvent = CCAndroids.ANDROID_DEATH.get()

    override fun addAdditionalSaveData(tag: CompoundTag) {
        super.addAdditionalSaveData(tag)
        tag.putBoolean("Locked", isLocked)
        tag.putByte("Variant", variant)
        tag.putByte("Face", face)
    }

    override fun readAdditionalSaveData(tag: CompoundTag) {
        super.readAdditionalSaveData(tag)
        isLocked = tag.getBoolean("Locked")
        variant = tag.getByte("Variant")
        entityData.set(FACE, tag.getByte("Face"))
    }

    companion object {
        private val LOCKED: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(AndroidEntity::class.java, EntityDataSerializers.BOOLEAN)
        private val VARIANT: EntityDataAccessor<Byte> = SynchedEntityData.defineId(AndroidEntity::class.java, EntityDataSerializers.BYTE)
        private val FACE: EntityDataAccessor<Byte> = SynchedEntityData.defineId(AndroidEntity::class.java, EntityDataSerializers.BYTE)

        @JvmStatic
        fun createAndroidAttributes(): AttributeSupplier.Builder =
            createMobAttributes()
                .add(Attributes.MAX_HEALTH, CCAndroids.CONFIG.AndroidMaxHealth.toDouble())
                .add(Attributes.ATTACK_DAMAGE, CCAndroids.CONFIG.AndroidDamage.toDouble())
                .add(Attributes.MOVEMENT_SPEED, CCAndroids.CONFIG.AndroidSpeed.toDouble())
                .add(Attributes.ARMOR, CCAndroids.CONFIG.AndroidArmor.toDouble())
    }
}
