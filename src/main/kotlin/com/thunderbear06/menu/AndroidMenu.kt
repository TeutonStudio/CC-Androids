package com.thunderbear06.menu

import com.thunderbear06.CCAndroids
import com.thunderbear06.ai.AndroidBrain
import com.thunderbear06.inventory.HandContainer
import dan200.computercraft.shared.computer.core.ComputerFamily
import dan200.computercraft.shared.computer.core.ServerComputer
import dan200.computercraft.shared.computer.inventory.AbstractComputerMenu
import dan200.computercraft.shared.network.container.ComputerContainerData
import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import java.util.function.Predicate

class AndroidMenu(
    id: Int,
    canUse: Predicate<Player>,
    family: ComputerFamily,
    computer: ServerComputer?,
    containerData: ComputerContainerData?,
    playerInventory: Inventory,
    inventory: Container,
    hands: Container,
) : AbstractComputerMenu(CCAndroids.ANDROID_MENU.get(), id, canUse, family, computer, containerData) {
    init {
        for (y in 0..2) {
            for (x in 0..2) {
                addSlot(Slot(inventory, x + y * 3, ANDROID_START_X + 1 + x * 18, PLAYER_START_Y + 1 + y * 18))
            }
        }
        for (y in 0..2) {
            for (x in 0..8) {
                addSlot(Slot(playerInventory, x + y * 9 + 9, PLAYER_START_X + x * 18, PLAYER_START_Y + 1 + y * 18))
            }
        }
        for (x in 0..8) {
            addSlot(Slot(playerInventory, x, PLAYER_START_X + x * 18, PLAYER_START_Y + 3 * 18 + 5))
        }
        addSlot(Slot(hands, 0, ANDROID_START_X + 1, PLAYER_START_Y + 3 * 18 + 5))
        addSlot(Slot(hands, 1, ANDROID_START_X + 1 + 18, PLAYER_START_Y + 3 * 18 + 5))
    }

    override fun quickMoveStack(player: Player, slotNum: Int): ItemStack {
        val slot = slots[slotNum]
        if (!slot.hasItem()) return ItemStack.EMPTY
        val clicked = slot.item
        val original = clicked.copy()
        if (slotNum < 9) {
            if (!moveItemStackTo(clicked, 9, slots.size, true)) return ItemStack.EMPTY
        } else if (!moveItemStackTo(clicked, 0, 9, false)) {
            return ItemStack.EMPTY
        }
        if (clicked.isEmpty) slot.setByPlayer(ItemStack.EMPTY) else slot.setChanged()
        return original
    }

    companion object {
        const val BORDER: Int = 8
        const val PLAYER_START_Y: Int = 134
        const val PLAYER_START_X: Int = SIDEBAR_WIDTH + BORDER
        const val ANDROID_START_X: Int = SIDEBAR_WIDTH + 175

        @JvmStatic
        fun ofBrain(id: Int, inventory: Inventory, brain: AndroidBrain): AndroidMenu =
            AndroidMenu(
                id,
                Predicate { player -> brain.android.isAlive && player.distanceToSqr(brain.android) < 64.0 },
                brain.android.getComputer().family,
                brain.android.getComputer().orCreateServerComputer,
                null,
                inventory,
                brain.android.inventory,
                HandContainer(brain.android),
            )

        @JvmStatic
        fun ofData(id: Int, inv: Inventory, data: ComputerContainerData): AndroidMenu =
            AndroidMenu(id, Predicate { true }, data.family(), null, data, inv, SimpleContainer(9), SimpleContainer(2))
    }
}
