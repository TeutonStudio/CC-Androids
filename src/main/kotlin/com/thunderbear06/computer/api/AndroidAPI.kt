package com.thunderbear06.computer.api

import com.thunderbear06.ai.AndroidBrain
import com.thunderbear06.ai.task.tasks.AttackEntityTask
import com.thunderbear06.ai.task.tasks.BreakBlockTask
import com.thunderbear06.ai.task.tasks.InteractBlockTask
import com.thunderbear06.ai.task.tasks.InteractEntityTask
import com.thunderbear06.entity.android.CommandAndroidEntity
import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.ILuaAPI
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.lua.MethodResult
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import java.util.UUID

class AndroidAPI(private val brain: AndroidBrain) : ILuaAPI {
    override fun getNames(): Array<String> = arrayOf("android")

    override fun getModuleName(): String = "android"

    private fun missingFuel(): Boolean = !brain.android.hasFuel()

    @Throws(LuaException::class)
    private fun getPosFromArgs(args: IArguments): BlockPos {
        val first = args[0]
        if (first is Map<*, *>) {
            return BlockPos(number(first["x"], "x"), number(first["y"], "y"), number(first["z"], "z"))
        }
        return BlockPos(args.getInt(0), args.getInt(1), args.getInt(2))
    }

    @Throws(LuaException::class)
    private fun number(value: Any?, key: String): Int {
        if (value is Number) return value.toInt()
        throw LuaException("$key must be a number")
    }

    @Throws(LuaException::class)
    private fun entity(uuid: String): LivingEntity? {
        val serverLevel = brain.android.level() as? ServerLevel ?: throw LuaException("Server level unavailable")
        val parsed = try {
            UUID.fromString(uuid)
        } catch (_: IllegalArgumentException) {
            throw LuaException("Invalid UUID: $uuid")
        }
        return serverLevel.getEntity(parsed) as? LivingEntity
    }

    @LuaFunction
    fun currentTask(): MethodResult = MethodResult.of(brain.taskManager.getCurrentTaskName())

    @LuaFunction
    fun getSelf(): MethodResult = MethodResult.of(brain.modules.sensorModule.collectEntityInfo(brain.android))

    @LuaFunction
    @Throws(LuaException::class)
    fun attack(uuid: String): MethodResult {
        if (missingFuel()) return MethodResult.of(false, "Android requires fuel.")
        val target = entity(uuid) ?: return MethodResult.of(false, "Unknown entity or invalid UUID")
        brain.setTask(AttackEntityTask(brain.android, 0.6, target))
        return MethodResult.of(true)
    }

    @LuaFunction
    @Throws(LuaException::class)
    fun goTo(uuid: String): MethodResult {
        if (missingFuel()) return MethodResult.of(false, "Android requires fuel.")
        return try {
            brain.modules.navigationModule.moveToEntity(uuid)
        } catch (_: IllegalArgumentException) {
            MethodResult.of(false, "Invalid UUID: $uuid")
        }
    }

    @LuaFunction
    @Throws(LuaException::class)
    fun moveTo(args: IArguments): MethodResult {
        if (missingFuel()) return MethodResult.of(false, "Android requires fuel.")
        return brain.modules.navigationModule.moveToBlock(getPosFromArgs(args))
    }

    @LuaFunction
    @Throws(LuaException::class)
    fun breakBlock(args: IArguments): MethodResult {
        if (missingFuel()) return MethodResult.of(false, "Android requires fuel.")
        val pos = getPosFromArgs(args)
        if (!pos.closerThan(brain.android.blockPosition(), 100.0)) return MethodResult.of(false, "Block position must be within 100 blocks")
        brain.setTask(BreakBlockTask(brain.android, 0.6, pos))
        return MethodResult.of(true)
    }

    @LuaFunction
    @Throws(LuaException::class)
    fun useBlock(args: IArguments): MethodResult {
        if (missingFuel()) return MethodResult.of(false, "Android requires fuel.")
        brain.setTask(InteractBlockTask(brain.android, 0.6, getPosFromArgs(args)))
        return MethodResult.of(true)
    }

    @LuaFunction
    @Throws(LuaException::class)
    fun useEntity(uuid: String): MethodResult {
        if (missingFuel()) return MethodResult.of(false, "Android requires fuel.")
        val target = entity(uuid) ?: return MethodResult.of(false, "Unknown entity or invalid UUID")
        brain.setTask(InteractEntityTask(brain.android, 0.6, target))
        return MethodResult.of(true)
    }

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun pickup(args: IArguments): MethodResult {
        val type = args.optString(0).orElse(null)
        val item = brain.modules.sensorModule.getGroundItem(type)
        return item?.let { brain.android.pickupGroundItem(it) } ?: MethodResult.of(false, "Could not find item")
    }

    @LuaFunction(mainThread = true)
    fun dropItem(): MethodResult = brain.android.dropHandItem()

    @LuaFunction(mainThread = true)
    fun storeItem(index: Int): MethodResult {
        val stack = brain.android.mainHandItem
        if (stack.isEmpty) return MethodResult.of(false, "No item in hand to stash")
        val result = brain.android.canStash(stack, index)
        if (result != null) return result
        brain.android.setItemInHand(InteractionHand.MAIN_HAND, brain.android.stashStack(stack, index))
        return MethodResult.of(true)
    }

    @LuaFunction(mainThread = true)
    fun equipSlot(index: Int): MethodResult {
        if (index < 0 || index >= brain.android.inventory.containerSize) return MethodResult.of(false, "Index out of range")
        if (!brain.android.mainHandItem.isEmpty) return MethodResult.of(false, "Cannot equip item while holding an item")
        val stack = brain.android.getStashItem(index, true)
        if (stack.isEmpty) return MethodResult.of(false, "Index is empty")
        brain.android.setItemInHand(InteractionHand.MAIN_HAND, stack)
        return MethodResult.of(true)
    }

    @LuaFunction(mainThread = true)
    fun swapHands(): MethodResult {
        brain.android.swapOffHandStack()
        return MethodResult.of(true)
    }

    @LuaFunction(mainThread = true)
    fun getHandInfo(handName: String): MethodResult {
        val stack: ItemStack = when (handName) {
            "right", "main" -> brain.android.mainHandItem
            "left", "off" -> brain.android.offhandItem
            else -> return MethodResult.of(false, "Invalid hand name. Expected main/right or off/left.")
        }
        return MethodResult.of(itemId(stack), stack.count)
    }

    @LuaFunction(mainThread = true)
    fun getSlotInfo(index: Int): MethodResult {
        if (index < 0 || index >= brain.android.inventory.containerSize) return MethodResult.of(false, "Index out of range")
        val stack = brain.android.inventory.getItem(index)
        return if (stack.isEmpty) MethodResult.of("empty") else MethodResult.of(itemId(stack), stack.count)
    }

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun refuel(args: IArguments): MethodResult {
        val amount = args.optInt(0)
        val held = brain.android.mainHandItem
        if (held.isEmpty) return MethodResult.of(false, "Hand is empty")
        if (!brain.android.addFuel(amount.orElse(held.count), held)) return MethodResult.of(false, "Held item stack cannot be used for fuel")
        return MethodResult.of(true, brain.android.fuel)
    }

    @LuaFunction(mainThread = true)
    fun fuelLevel(): MethodResult = MethodResult.of(brain.android.fuel)

    @LuaFunction(mainThread = true)
    fun getClosestPlayer(): MethodResult = MethodResult.of(brain.modules.sensorModule.getClosestPlayer())

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun getNearbyMobs(args: IArguments): MethodResult =
        MethodResult.of(brain.modules.sensorModule.getMobs(args.optString(0).orElse(null)))

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun getClosestMob(args: IArguments): MethodResult =
        MethodResult.of(brain.modules.sensorModule.getClosestMobOfType(args.optString(0).orElse(null)))

    @LuaFunction(mainThread = true)
    fun getBlocksOfType(type: String): MethodResult =
        MethodResult.of(brain.modules.sensorModule.getBlocksOfType(brain.android.blockPosition(), type))

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun getContainerInfo(args: IArguments): MethodResult =
        MethodResult.of(brain.modules.sensorModule.getContainerInfo(getPosFromArgs(args)))

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun storeHeldItemInContainer(args: IArguments): MethodResult {
        val pos = getPosFromArgs(args)
        if (args.count() != 2 && args.count() != 4) return MethodResult.of(false, "Expected position table and slot, or x, y, z, slot.")
        val slot = if (args.count() == 2) args.getInt(1) else args.getInt(3)
        brain.modules.interactionModule.storeHeldItemInContainer(pos, slot)
        return MethodResult.of(true)
    }

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun grabItemFromContainer(args: IArguments): MethodResult {
        val pos = getPosFromArgs(args)
        if (args.count() != 2 && args.count() != 4) return MethodResult.of(false, "Expected position table and slot, or x, y, z, slot.")
        val slot = if (args.count() == 2) args.getInt(1) else args.getInt(3)
        brain.modules.interactionModule.grabItemFromContainer(pos, slot)
        return MethodResult.of(true)
    }

    @LuaFunction
    fun sendChatMessage(text: String): MethodResult {
        brain.android.sendChatMessage(text)
        return MethodResult.of(true)
    }

    @LuaFunction(mainThread = true)
    fun runCommand(command: String): MethodResult {
        if (brain.android !is CommandAndroidEntity) return MethodResult.of(false, "Command execution requires a Command Android.")
        val serverLevel = brain.android.level() as? ServerLevel ?: return MethodResult.of(false, "Server level unavailable.")
        return try {
            serverLevel.server.commands.performPrefixedCommand(brain.android.createCommandSourceStack().withPermission(4), command)
            MethodResult.of(true)
        } catch (e: RuntimeException) {
            MethodResult.of(false, e.message ?: "Command failed.")
        }
    }

    @LuaFunction
    fun changeFace(faceName: String): MethodResult {
        if (faceName != "angry" && faceName != "annoyed" && faceName != "happy" && faceName != "sad" && faceName != "woozy") {
            return MethodResult.of(false, "Unknown face. Expected angry, annoyed, happy, sad, or woozy.")
        }
        brain.android.setFace(faceName)
        return MethodResult.of(true)
    }

    @LuaFunction
    fun cancelTask(): MethodResult {
        brain.taskManager.clearCurrentTask()
        return MethodResult.of(true)
    }

    @LuaFunction
    fun sit(): MethodResult = MethodResult.of(false, "Create seat support is not implemented in this build.")

    @LuaFunction
    fun stand(): MethodResult {
        brain.android.stopRiding()
        return MethodResult.of(true)
    }

    @LuaFunction
    fun isSitting(): MethodResult = MethodResult.of(brain.android.isPassenger)

    @LuaFunction
    fun getVehicleInfo(): MethodResult {
        val vehicle = brain.android.vehicle
        return MethodResult.of(vehicle?.let { mapOf("uuid" to it.stringUUID, "type" to it.type.toString()) } ?: emptyMap<String, String>())
    }

    companion object {
        private fun itemId(stack: ItemStack): String = BuiltInRegistries.ITEM.getKey(stack.item).toString()
    }
}
