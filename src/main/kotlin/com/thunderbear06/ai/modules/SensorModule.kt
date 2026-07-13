package com.thunderbear06.ai.modules

import com.thunderbear06.ai.AndroidBrain
import com.thunderbear06.entity.android.BaseAndroidEntity
import dan200.computercraft.api.lua.LuaException
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.items.IItemHandler

class SensorModule(
    android: BaseAndroidEntity,
    brain: AndroidBrain,
    private val entitySearchRadius: Double,
    private val blockSearchRadius: Int,
) : AbstractAndroidModule(android, brain) {
    fun getMobs(type: String?): List<HashMap<String, Any>> {
        val result = ArrayList<HashMap<String, Any>>()
        for (entity in android.level().getEntitiesOfClass(
            LivingEntity::class.java,
            android.boundingBox.inflate(entitySearchRadius),
            { e -> e !== android && e.isAlive && matchesType(e, type) },
        )) {
            result.add(collectEntityInfo(entity))
        }
        return result
    }

    fun getClosestMobOfType(type: String?): HashMap<String, Any> =
        android.level().getEntitiesOfClass(
            LivingEntity::class.java,
            android.boundingBox.inflate(entitySearchRadius),
            { e -> e !== android && e.isAlive && matchesType(e, type) },
        ).minByOrNull { it.distanceToSqr(android) }?.let(::collectEntityInfo) ?: HashMap()

    fun getClosestPlayer(): HashMap<String, Any> {
        val player: Player? = android.level().getNearestPlayer(android, 100.0)
        return if (player == null) HashMap() else collectEntityInfo(player)
    }

    fun getGroundItem(type: String?): ItemEntity? {
        for (item in android.level().getEntitiesOfClass(ItemEntity::class.java, android.boundingBox.inflate(5.0))) {
            if (type == null || BuiltInRegistries.ITEM.getKey(item.item.item).toString().contains(type)) return item
        }
        return null
    }

    fun getBlocksOfType(origin: BlockPos, type: String): List<HashMap<String, Int>> {
        val blocks = ArrayList<HashMap<String, Int>>()
        BlockPos.betweenClosed(
            origin.offset(-blockSearchRadius, -blockSearchRadius, -blockSearchRadius),
            origin.offset(blockSearchRadius, blockSearchRadius, blockSearchRadius),
        ).forEach { pos ->
            if (BuiltInRegistries.BLOCK.getKey(android.level().getBlockState(pos).block).toString().contains(type)) {
                val data = HashMap<String, Int>()
                data["x"] = pos.x
                data["y"] = pos.y
                data["z"] = pos.z
                blocks.add(data)
            }
        }
        return blocks
    }

    fun collectEntityInfo(entity: Entity): HashMap<String, Any> {
        val info = HashMap<String, Any>()
        info["uuid"] = entity.stringUUID
        info["name"] = entity.name.string
        info["posX"] = entity.x
        info["posY"] = entity.y
        info["posZ"] = entity.z
        if (entity is LivingEntity) info["health"] = entity.health
        return info
    }

    @Throws(LuaException::class)
    fun getContainerInfo(pos: BlockPos): HashMap<String, Any> {
        if (!pos.closerThan(android.blockPosition(), blockSearchRadius.toDouble())) throw LuaException("Position out of range")
        val info = HashMap<String, Any>()
        val handler: IItemHandler = android.level().getCapability(Capabilities.ItemHandler.BLOCK, pos, accessSide(pos)) ?: return info
        info["slotCount"] = handler.slots
        val slots = ArrayList<List<Any>>()
        for (i in 0 until handler.slots) {
            val stack: ItemStack = handler.getStackInSlot(i)
            slots.add(listOf(if (stack.isEmpty) "empty" else BuiltInRegistries.ITEM.getKey(stack.item).toString(), stack.count))
        }
        info["slots"] = slots
        return info
    }

    private fun accessSide(pos: BlockPos): Direction {
        val delta: Vec3 = android.position().subtract(Vec3.atCenterOf(pos))
        return Direction.getNearest(delta.x, delta.y, delta.z)
    }

    private fun matchesType(entity: LivingEntity, type: String?): Boolean =
        type == null || BuiltInRegistries.ENTITY_TYPE.getKey(entity.type).toString().contains(type)
}
