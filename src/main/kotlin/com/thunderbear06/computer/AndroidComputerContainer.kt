package com.thunderbear06.computer

import com.thunderbear06.component.ComputerComponents
import com.thunderbear06.entity.android.BaseAndroidEntity
import com.thunderbear06.menu.AndroidMenu
import dan200.computercraft.api.ComputerCraftAPI
import dan200.computercraft.shared.computer.core.ComputerFamily
import dan200.computercraft.shared.computer.core.ServerComputer
import dan200.computercraft.shared.computer.core.ServerContext
import dan200.computercraft.shared.computer.core.TerminalSize
import dan200.computercraft.shared.config.Config
import dan200.computercraft.shared.network.container.ComputerContainerData
import dan200.computercraft.shared.platform.PlatformHelper
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import java.util.UUID

class AndroidComputerContainer(private val android: BaseAndroidEntity) {
    private var instanceID: UUID? = null
    var computerID: Int = -1
    private var startOn = false
    private var label: Component = Component.translatable("entity.cc_androids.android")
    var family: ComputerFamily = ComputerFamily.NORMAL

    fun tick() {
        if (android.level().isClientSide || android.level().server == null) return
        if (computerID < 0 && !startOn && !android.isOn) return
        val computer = orCreateServerComputer
        if (startOn || android.isOn) {
            computer.turnOn()
            android.setOn(true)
            startOn = false
        }
        computer.keepAlive()
        if (!computer.isOn && android.isOn) android.shutdown()
        val computerLabel = computer.label
        if (computerLabel != null && computerLabel.isNotBlank()) {
            label = Component.literal(computerLabel)
            android.customName = label
        }
    }

    fun openComputer(player: ServerPlayer) {
        val computer = orCreateServerComputer
        computer.turnOn()
        android.setOn(true)
        PlatformHelper.get().openMenu(
            player,
            label,
            { syncId, playerInventory, _ -> AndroidMenu.ofBrain(syncId, playerInventory, android.brain) },
            ComputerContainerData(computer, ItemStack.EMPTY),
        )
    }

    val orCreateServerComputer: EntityComputer
        get() {
            val server: MinecraftServer = android.level().server
                ?: throw IllegalStateException("Cannot access Android computer on the client.")
            val serverLevel = android.level() as? ServerLevel
                ?: throw IllegalStateException("Cannot access Android computer on the client.")
            val id = instanceID
            if (id != null) {
                val existing = ServerContext.get(server).registry().get(id)
                if (existing is EntityComputer) return existing
            }
            if (computerID < 0) computerID = ComputerCraftAPI.createUniqueNumberedSaveDir(server, "computer")
            val computer = createComputer(serverLevel, computerID)
            instanceID = computer.register()
            return computer
        }

    private fun createComputer(level: ServerLevel, id: Int): EntityComputer {
        val properties = ServerComputer.properties(id, family)
            .addComponent(ComputerComponents.ANDROID_COMPUTER, android.brain)
            .label(label.string)
            .terminalSize(TerminalSize(Config.TURTLE_TERM_WIDTH, Config.TURTLE_TERM_HEIGHT))
        return EntityComputer(level, android, properties)
    }

    val serverComputer: EntityComputer?
        get() {
            val server = android.level().server ?: return null
            val id = instanceID ?: return null
            val computer = ServerContext.get(server).registry().get(id)
            return computer as? EntityComputer
        }

    fun writeNbt(tag: CompoundTag) {
        tag.putBoolean("StartOn", startOn || android.isOn)
        tag.putInt("ComputerID", computerID)
        tag.putString("ComputerLabel", label.string)
        tag.putString("ComputerFamily", family.name)
    }

    fun readNbt(tag: CompoundTag) {
        startOn = tag.getBoolean("StartOn")
        if (tag.contains("ComputerID")) computerID = tag.getInt("ComputerID")
        if (tag.contains("ComputerLabel")) label = Component.literal(tag.getString("ComputerLabel"))
        if (tag.contains("ComputerFamily")) {
            family = try {
                ComputerFamily.valueOf(tag.getString("ComputerFamily"))
            } catch (_: IllegalArgumentException) {
                ComputerFamily.NORMAL
            }
        }
    }

    fun close() {
        serverComputer?.close()
    }
}
