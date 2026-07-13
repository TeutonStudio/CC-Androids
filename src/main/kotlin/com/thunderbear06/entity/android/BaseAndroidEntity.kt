package com.thunderbear06.entity.android

import com.mojang.authlib.GameProfile
import com.thunderbear06.CCAndroids
import com.thunderbear06.ai.AndroidBrain
import com.thunderbear06.computer.AndroidComputerContainer
import com.thunderbear06.computer.EntityComputer
import com.thunderbear06.inventory.AndroidInventory
import com.thunderbear06.tags.TagRegistry
import dan200.computercraft.api.lua.MethodResult
import dan200.computercraft.shared.computer.core.ComputerFamily
import dan200.computercraft.shared.platform.PlatformHelper
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.level.Level
import kotlin.math.max
import kotlin.math.min

abstract class BaseAndroidEntity(entityType: EntityType<out PathfinderMob>, level: Level) : PathfinderMob(entityType, level) {
    lateinit var brain: AndroidBrain
    @JvmField val inventory: AndroidInventory = AndroidInventory(9)
    protected val computerContainer: AndroidComputerContainer = AndroidComputerContainer(this)
    protected val maxFuel: Int = 10000
    var fuel: Int = 0
        protected set
    var isOn: Boolean = false
        private set

    init {
        val navigation = navigation
        if (navigation is GroundPathNavigation) {
            navigation.setCanOpenDoors(true)
            navigation.setCanPassDoors(true)
        }
    }

    override fun aiStep() {
        super.aiStep()
        if (::brain.isInitialized) brain.modules.interactionModule.tickDoorInteraction()
    }

    override fun tick() {
        super.tick()
        if (level().isClientSide) return
        computerContainer.tick()
        if (tickCount % 20 == 0 && !isIdle()) consumeFuel()
    }

    protected open fun isIdle(): Boolean = true

    fun shutdown() {
        isOn = false
        if (::brain.isInitialized) brain.onShutdown()
    }

    fun setOn(on: Boolean) {
        isOn = on
    }

    protected open fun consumeFuel() {
        if (fuel > 0) fuel--
    }

    private fun getFuelMultiplier(stack: ItemStack): Int {
        if (stack.`is`(TagRegistry.MINOR_ANDROID_FUEL)) return 10
        if (stack.`is`(TagRegistry.MEDIUM_ANDROID_FUEL)) return 80
        if (stack.`is`(TagRegistry.MAJOR_ANDROID_FUEL)) return 800
        return 0
    }

    fun addFuel(maxItems: Int, stack: ItemStack): Boolean {
        val multiplier = getFuelMultiplier(stack)
        if (multiplier <= 0) return false
        val fuelNeededItems = max(0, (maxFuel - fuel + multiplier - 1) / multiplier)
        val used = min(min(maxItems, stack.count), fuelNeededItems)
        if (used <= 0) return false
        fuel = min(maxFuel, fuel + used * multiplier)
        stack.shrink(used)
        return true
    }

    open fun hasFuel(): Boolean = fuel > 0

    fun getComputer(): AndroidComputerContainer = computerContainer

    fun pickupGroundItem(itemEntity: ItemEntity): MethodResult {
        if (!itemEntity.isAlive || itemEntity.item.isEmpty) return MethodResult.of(false, "Could not pick up item")
        if (!mainHandItem.isEmpty) return MethodResult.of(false, "Main hand is not empty")
        setItemInHand(InteractionHand.MAIN_HAND, itemEntity.item.copy())
        itemEntity.discard()
        return MethodResult.of(true)
    }

    fun asFakePlayer(): ServerPlayer? {
        val serverLevel = level() as? ServerLevel ?: return null
        val player = PlatformHelper.get().createFakePlayer(serverLevel, GameProfile(uuid, "CCAndroids"))
        player.moveTo(x, y, z, yRot, xRot)
        player.yHeadRot = yHeadRot
        return player
    }

    fun dropHandItem(): MethodResult {
        val itemStack = mainHandItem
        if (itemStack.isEmpty) return MethodResult.of(false, "Hand is empty")
        spawnAtLocation(itemStack.copy())
        setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY)
        return MethodResult.of(true)
    }

    fun stashStack(stack: ItemStack, index: Int): ItemStack {
        val stored = inventory.getItem(index)
        if (stored.isEmpty) {
            inventory.setItem(index, stack.copy())
            return ItemStack.EMPTY
        }
        if (ItemStack.isSameItemSameComponents(stored, stack)) {
            val transfer = min(stack.count, stored.maxStackSize - stored.count)
            stored.grow(transfer)
            stack.shrink(transfer)
        }
        return if (stack.isEmpty) ItemStack.EMPTY else stack
    }

    fun getStashItem(index: Int, remove: Boolean): ItemStack =
        if (remove) inventory.removeItemNoUpdate(index) else inventory.getItem(index)

    fun swapOffHandStack() {
        val main = mainHandItem.copy()
        setItemInHand(InteractionHand.MAIN_HAND, offhandItem.copy())
        setItemInHand(InteractionHand.OFF_HAND, main)
    }

    fun canStash(stack: ItemStack, index: Int): MethodResult? {
        if (index < 0 || index >= inventory.containerSize) return MethodResult.of(false, "Index out of range")
        val stored = inventory.getItem(index)
        if (!stored.isEmpty) return MethodResult.of(false, "Index is occupied")
        return null
    }

    fun sendChatMessage(message: String) {
        val server = level().server ?: return
        val line = Component.literal("[Android ${displayName?.string ?: stringUUID}] $message")
        server.playerList.players.stream()
            .filter { player -> player.level() === level() }
            .filter { player -> player.distanceToSqr(this) <= 64.0 * 64.0 }
            .forEach { player -> player.sendSystemMessage(line) }
        CCAndroids.LOGGER.info("[Android {}] {}", stringUUID, message)
    }

    protected fun dropAndroidContents(fullConstructionRefund: Boolean) {
        dropCpu()
        spawnAtLocation(ItemStack(CCAndroids.REDSTONE_REACTOR.get()))
        val main = mainHandItem
        if (!main.isEmpty) spawnAtLocation(main.copy())
        val off = offhandItem
        if (!off.isEmpty) spawnAtLocation(off.copy())
        setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY)
        setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY)
        for (stack in inventory.clearToList()) {
            if (!stack.isEmpty) spawnAtLocation(stack)
        }
        if (this is AndroidEntity) dropConstructionMaterials(fullConstructionRefund)
    }

    private fun dropCpu() {
        val cpuItem: Item = if (computerContainer.family == ComputerFamily.COMMAND) Items.COMMAND_BLOCK else CCAndroids.ANDROID_CPU.get()
        val stack = ItemStack(cpuItem)
        if (computerContainer.computerID >= 0) {
            val tag = CompoundTag()
            tag.putInt("ComputerID", computerContainer.computerID)
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
        }
        spawnAtLocation(stack)
    }

    override fun addAdditionalSaveData(tag: CompoundTag) {
        super.addAdditionalSaveData(tag)
        tag.put("Items", inventory.toTag(registryAccess()))
        tag.putInt("Fuel", fuel)
        tag.putBoolean("IsOn", isOn)
        val computer = CompoundTag()
        computerContainer.writeNbt(computer)
        if (::brain.isInitialized) brain.writeNbt(computer)
        tag.put("ComputerEntity", computer)
    }

    override fun readAdditionalSaveData(tag: CompoundTag) {
        super.readAdditionalSaveData(tag)
        if (tag.contains("Items")) inventory.fromTag(tag.getList("Items", 10), registryAccess())
        fuel = tag.getInt("Fuel")
        isOn = tag.getBoolean("IsOn")
        if (tag.contains("ComputerEntity")) {
            val computer = tag.getCompound("ComputerEntity")
            computerContainer.readNbt(computer)
            if (::brain.isInitialized) brain.readNbt(computer)
        }
    }

    override fun removeWhenFarAway(distanceToClosestPlayer: Double): Boolean = false

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        if (source.`is`(DamageTypes.FALL) || source.`is`(DamageTypes.MAGIC)) return false
        return super.hurt(source, amount)
    }

    override fun remove(reason: RemovalReason) {
        super.remove(reason)
        if (!level().isClientSide) {
            val computer: EntityComputer? = computerContainer.serverComputer
            computer?.close()
        }
    }

    override fun dropCustomDeathLoot(level: ServerLevel, damageSource: DamageSource, recentlyHit: Boolean) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit)
        dropAndroidContents(false)
    }

    open val entitySearchRadius: Double = 10.0
    open val blockSearchRadius: Int = 10
}
