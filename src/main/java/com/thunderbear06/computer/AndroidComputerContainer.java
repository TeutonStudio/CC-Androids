package com.thunderbear06.computer;

import com.thunderbear06.component.ComputerComponents;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import com.thunderbear06.menu.AndroidMenu;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerContext;
import dan200.computercraft.shared.computer.core.TerminalSize;
import dan200.computercraft.shared.config.Config;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.platform.PlatformHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class AndroidComputerContainer {
    private final BaseAndroidEntity android;
    private UUID instanceID;
    private int computerID = -1;
    private boolean startOn;
    private Component label = Component.translatable("entity.cc_androids.android");
    private ComputerFamily family = ComputerFamily.NORMAL;

    public AndroidComputerContainer(BaseAndroidEntity android) {
        this.android = android;
    }

    public void tick() {
        if (android.level().isClientSide || android.level().getServer() == null) return;
        if (computerID < 0 && !startOn && !android.isOn()) return;
        EntityComputer computer = getOrCreateServerComputer();
        if (startOn || android.isOn()) {
            computer.turnOn();
            android.setOn(true);
            startOn = false;
        }
        computer.keepAlive();
        if (!computer.isOn() && android.isOn()) android.shutdown();
        String computerLabel = computer.getLabel();
        if (computerLabel != null && !computerLabel.isBlank()) {
            label = Component.literal(computerLabel);
            android.setCustomName(label);
        }
    }

    public void openComputer(ServerPlayer player) {
        EntityComputer computer = getOrCreateServerComputer();
        computer.turnOn();
        android.setOn(true);
        PlatformHelper.get().openMenu(
                player,
                label,
                (syncId, playerInventory, ignored) -> AndroidMenu.ofBrain(syncId, playerInventory, android.brain),
                new ComputerContainerData(computer, ItemStack.EMPTY)
        );
    }

    public EntityComputer getOrCreateServerComputer() {
        MinecraftServer server = android.level().getServer();
        if (server == null || !(android.level() instanceof ServerLevel serverLevel)) {
            throw new IllegalStateException("Cannot access Android computer on the client.");
        }
        if (instanceID != null) {
            ServerComputer existing = ServerContext.get(server).registry().get(instanceID);
            if (existing instanceof EntityComputer entityComputer) return entityComputer;
        }
        if (computerID < 0) computerID = ComputerCraftAPI.createUniqueNumberedSaveDir(server, "computer");
        EntityComputer computer = createComputer(serverLevel, computerID);
        instanceID = computer.register();
        return computer;
    }

    private EntityComputer createComputer(ServerLevel level, int id) {
        ServerComputer.Properties properties = ServerComputer.properties(id, family)
                .addComponent(ComputerComponents.ANDROID_COMPUTER, android.brain)
                .label(label.getString())
                .terminalSize(new TerminalSize(Config.TURTLE_TERM_WIDTH, Config.TURTLE_TERM_HEIGHT));
        return new EntityComputer(level, android, properties);
    }

    @Nullable
    public EntityComputer getServerComputer() {
        MinecraftServer server = android.level().getServer();
        if (server == null || instanceID == null) return null;
        ServerComputer computer = ServerContext.get(server).registry().get(instanceID);
        return computer instanceof EntityComputer entityComputer ? entityComputer : null;
    }

    public void writeNbt(CompoundTag tag) {
        tag.putBoolean("StartOn", startOn || android.isOn());
        tag.putInt("ComputerID", computerID);
        tag.putString("ComputerLabel", label.getString());
        tag.putString("ComputerFamily", family.name());
    }

    public void readNbt(CompoundTag tag) {
        startOn = tag.getBoolean("StartOn");
        if (tag.contains("ComputerID")) computerID = tag.getInt("ComputerID");
        if (tag.contains("ComputerLabel")) label = Component.literal(tag.getString("ComputerLabel"));
        if (tag.contains("ComputerFamily")) {
            try {
                family = ComputerFamily.valueOf(tag.getString("ComputerFamily"));
            } catch (IllegalArgumentException ignored) {
                family = ComputerFamily.NORMAL;
            }
        }
    }

    public int getComputerID() {
        return computerID;
    }

    public void setComputerID(int computerID) {
        this.computerID = computerID;
    }

    public ComputerFamily getFamily() {
        return family;
    }

    public void setFamily(ComputerFamily family) {
        this.family = family;
    }

    public void close() {
        EntityComputer computer = getServerComputer();
        if (computer != null) computer.close();
    }
}
