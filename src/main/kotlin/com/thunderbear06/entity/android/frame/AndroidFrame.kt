package com.thunderbear06.entity.android.frame

import com.thunderbear06.CCAndroids
import com.thunderbear06.entity.android.BaseAndroidEntity
import dan200.computercraft.shared.computer.core.ComputerFamily
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.level.Level

class AndroidFrame(entityType: EntityType<out Mob>, level: Level) : Mob(entityType, level) {
    private var advanced = false
    private var ingotTypeSelected = false

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(COMPONENTS_NEEDED, CCAndroids.CONFIG.CompsForConstruction)
        builder.define(INGOTS_NEEDED, CCAndroids.CONFIG.IngotsForConstruction)
        builder.define(HAS_CORE, false)
    }

    override fun mobInteract(player: Player, hand: InteractionHand): InteractionResult {
        val stack = player.getItemInHand(hand)
        if (stack.`is`(CCAndroids.COMPONENTS.get()) && componentsNeeded > 0) {
            entityData.set(COMPONENTS_NEEDED, (componentsNeeded - 1).toByte())
            consume(stack, player)
            return InteractionResult.sidedSuccess(level().isClientSide)
        }
        if ((stack.`is`(Items.IRON_INGOT) || stack.`is`(Items.GOLD_INGOT)) && ingotsNeeded > 0) {
            val insertingGold = stack.`is`(Items.GOLD_INGOT)
            if (ingotTypeSelected && advanced != insertingGold) return InteractionResult.FAIL
            advanced = insertingGold
            ingotTypeSelected = true
            entityData.set(INGOTS_NEEDED, (ingotsNeeded - 1).toByte())
            consume(stack, player)
            return InteractionResult.sidedSuccess(level().isClientSide)
        }
        if (stack.`is`(CCAndroids.REDSTONE_REACTOR.get()) && !hasCore()) {
            entityData.set(HAS_CORE, true)
            consume(stack, player)
            return InteractionResult.sidedSuccess(level().isClientSide)
        }
        if ((stack.`is`(CCAndroids.ANDROID_CPU.get()) || stack.`is`(Items.COMMAND_BLOCK)) && readyForCpu()) {
            if (stack.`is`(Items.COMMAND_BLOCK) && !player.abilities.instabuild) return InteractionResult.FAIL
            finish(if (stack.`is`(Items.COMMAND_BLOCK)) ComputerFamily.COMMAND else if (advanced) ComputerFamily.ADVANCED else ComputerFamily.NORMAL, readComputerId(stack))
            consume(stack, player)
            return InteractionResult.sidedSuccess(level().isClientSide)
        }
        return InteractionResult.FAIL
    }

    private fun consume(stack: ItemStack, player: Player) {
        if (!player.abilities.instabuild) stack.shrink(1)
        playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0f, 1.0f)
    }

    private fun readyForCpu(): Boolean = componentsNeeded.toInt() == 0 && ingotsNeeded.toInt() == 0 && hasCore()

    private fun readComputerId(stack: ItemStack): Int {
        val data: CustomData = stack.get(DataComponents.CUSTOM_DATA) ?: return -1
        if (!data.contains("ComputerID")) return -1
        return data.copyTag().getInt("ComputerID")
    }

    private fun finish(family: ComputerFamily, computerID: Int) {
        if (level().isClientSide) return
        val android: BaseAndroidEntity = when (family) {
            ComputerFamily.ADVANCED -> CCAndroids.ADVANCED_ANDROID.get().create(level())
            ComputerFamily.COMMAND -> CCAndroids.COMMAND_ANDROID.get().create(level())
            else -> CCAndroids.ANDROID.get().create(level())
        } ?: return
        android.moveTo(x, y, z, yRot, xRot)
        android.getComputer().family = family
        if (computerID >= 0) android.getComputer().computerID = computerID
        discard()
        level().addFreshEntity(android)
        android.playSound(SoundEvents.BEACON_ACTIVATE, 1.0f, 1.0f)
    }

    fun onBreak() {
        spawnAtLocation(ItemStack(CCAndroids.ANDROID_FRAME.get()))
        discard()
    }

    val componentsNeeded: Byte
        get() = entityData.get(COMPONENTS_NEEDED)

    val ingotsNeeded: Byte
        get() = entityData.get(INGOTS_NEEDED)

    fun hasCore(): Boolean = entityData.get(HAS_CORE)

    override fun addAdditionalSaveData(tag: CompoundTag) {
        super.addAdditionalSaveData(tag)
        tag.putByte("ComponentsNeeded", componentsNeeded)
        tag.putByte("IngotsNeeded", ingotsNeeded)
        tag.putBoolean("HasCore", hasCore())
        tag.putBoolean("Advanced", advanced)
        tag.putBoolean("IngotTypeSelected", ingotTypeSelected)
    }

    override fun readAdditionalSaveData(tag: CompoundTag) {
        super.readAdditionalSaveData(tag)
        entityData.set(COMPONENTS_NEEDED, tag.getByte("ComponentsNeeded"))
        entityData.set(INGOTS_NEEDED, tag.getByte("IngotsNeeded"))
        entityData.set(HAS_CORE, tag.getBoolean("HasCore"))
        advanced = tag.getBoolean("Advanced")
        ingotTypeSelected = if (tag.contains("IngotTypeSelected")) {
            tag.getBoolean("IngotTypeSelected")
        } else {
            ingotsNeeded < CCAndroids.CONFIG.IngotsForConstruction
        }
    }

    companion object {
        private val COMPONENTS_NEEDED: EntityDataAccessor<Byte> = SynchedEntityData.defineId(AndroidFrame::class.java, EntityDataSerializers.BYTE)
        private val INGOTS_NEEDED: EntityDataAccessor<Byte> = SynchedEntityData.defineId(AndroidFrame::class.java, EntityDataSerializers.BYTE)
        private val HAS_CORE: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(AndroidFrame::class.java, EntityDataSerializers.BOOLEAN)

        @JvmStatic
        fun createAttributes(): AttributeSupplier.Builder = Mob.createMobAttributes()
    }
}
