package com.thunderbear06.screen;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.menu.AndroidMenu;
import dan200.computercraft.client.gui.AbstractComputerScreen;
import dan200.computercraft.client.gui.widgets.TerminalWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class AndroidScreen extends AbstractComputerScreen<AndroidMenu> {
    private static final ResourceLocation BACKGROUND_NORMAL = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/gui/android_normal.png");
    private static final ResourceLocation BACKGROUND_ADVANCED = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/gui/android_advanced.png");
    private static final ResourceLocation BACKGROUND_COMMAND = ResourceLocation.fromNamespaceAndPath(CCAndroids.MOD_ID, "textures/gui/android_command.png");

    public AndroidScreen(AndroidMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 8);
        imageWidth = 295;
        imageHeight = 217;
    }

    @Override
    protected TerminalWidget createTerminal() {
        return new TerminalWidget(terminalData, computerInput, computerActions, leftPos + 25, topPos + 6);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        ResourceLocation texture = switch (family) {
            case ADVANCED -> BACKGROUND_ADVANCED;
            case COMMAND -> BACKGROUND_COMMAND;
            default -> BACKGROUND_NORMAL;
        };
        graphics.blit(texture, leftPos + 17, topPos, 0, 0.0F, 0.0F, 278, 217, 512, 512);
    }
}
