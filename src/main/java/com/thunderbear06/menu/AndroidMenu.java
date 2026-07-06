package com.thunderbear06.menu;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.inventory.HandContainer;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.inventory.AbstractComputerMenu;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class AndroidMenu extends AbstractComputerMenu {
    public static final int BORDER = 8;
    public static final int PLAYER_START_Y = 134;
    public static final int PLAYER_START_X = SIDEBAR_WIDTH + BORDER;
    public static final int ANDROID_START_X = SIDEBAR_WIDTH + 175;

    public AndroidMenu(int id, Predicate<Player> canUse, ComputerFamily family, @Nullable ServerComputer computer,
                       @Nullable ComputerContainerData containerData, Inventory playerInventory, Container inventory, Container hands) {
        super(CCAndroids.ANDROID_MENU.get(), id, canUse, family, computer, containerData);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                addSlot(new Slot(inventory, x + y * 3, ANDROID_START_X + 1 + x * 18, PLAYER_START_Y + 1 + y * 18));
            }
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new Slot(playerInventory, x + y * 9 + 9, PLAYER_START_X + x * 18, PLAYER_START_Y + 1 + y * 18));
            }
        }
        for (int x = 0; x < 9; x++) {
            addSlot(new Slot(playerInventory, x, PLAYER_START_X + x * 18, PLAYER_START_Y + 3 * 18 + 5));
        }
        addSlot(new Slot(hands, 0, ANDROID_START_X + 1, PLAYER_START_Y + 3 * 18 + 5));
        addSlot(new Slot(hands, 1, ANDROID_START_X + 1 + 18, PLAYER_START_Y + 3 * 18 + 5));
    }

    public static AndroidMenu ofBrain(int id, Inventory inventory, AndroidBrain brain) {
        return new AndroidMenu(id, player -> brain.getAndroid().isAlive() && player.distanceToSqr(brain.getAndroid()) < 64.0D,
                brain.getAndroid().getComputer().getFamily(), brain.getAndroid().getComputer().getOrCreateServerComputer(),
                null, inventory, brain.getAndroid().inventory, new HandContainer(brain.getAndroid()));
    }

    public static AndroidMenu ofData(int id, Inventory inv, ComputerContainerData data) {
        return new AndroidMenu(id, player -> true, data.family(), null, data, inv, new SimpleContainer(9), new SimpleContainer(2));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotNum) {
        Slot slot = slots.get(slotNum);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack clicked = slot.getItem();
        ItemStack original = clicked.copy();
        if (slotNum < 9) {
            if (!moveItemStackTo(clicked, 9, slots.size(), true)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(clicked, 0, 9, false)) {
            return ItemStack.EMPTY;
        }
        if (clicked.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
        else slot.setChanged();
        return original;
    }
}
