package com.thunderbear06.screen

import com.thunderbear06.CCAndroids
import com.thunderbear06.menu.AndroidMenu
import dan200.computercraft.client.gui.AbstractComputerScreen
import dan200.computercraft.client.gui.widgets.TerminalWidget
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory

class AndroidScreen(menu: AndroidMenu, inventory: Inventory, title: Component) :
    AbstractComputerScreen<AndroidMenu>(menu, inventory, title, 8) {
    init {
        imageWidth = 295
        imageHeight = 217
    }

    override fun createTerminal(): TerminalWidget =
        TerminalWidget(terminalData, computerInput, computerActions, leftPos + 25, topPos + 6)

    override fun renderBg(graphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        val texture = when (family) {
            dan200.computercraft.shared.computer.core.ComputerFamily.ADVANCED -> BACKGROUND_ADVANCED
            dan200.computercraft.shared.computer.core.ComputerFamily.COMMAND -> BACKGROUND_COMMAND
            else -> BACKGROUND_NORMAL
        }
        graphics.blit(texture, leftPos + 17, topPos, 0, 0.0f, 0.0f, 278, 217, 512, 512)
    }

    companion object {
        private val BACKGROUND_NORMAL = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/gui/android_normal.png")
        private val BACKGROUND_ADVANCED = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/gui/android_advanced.png")
        private val BACKGROUND_COMMAND = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/gui/android_command.png")
    }
}
