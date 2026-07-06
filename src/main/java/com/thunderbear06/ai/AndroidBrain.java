package com.thunderbear06.ai;

import com.mojang.authlib.GameProfile;
import com.thunderbear06.CCAndroids;
import com.thunderbear06.ai.modules.AndroidModules;
import com.thunderbear06.ai.task.Task;
import com.thunderbear06.ai.task.TaskManager;
import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class AndroidBrain {
    private final AndroidEntity android;
    private final TaskManager taskManager = new TaskManager();
    private final AndroidModules modules;
    private GameProfile owningPlayerProfile;

    public AndroidBrain(AndroidEntity android) {
        this.android = android;
        this.modules = new AndroidModules(android, this);
    }

    public void onShutdown() {
        taskManager.clearCurrentTask();
    }

    public void setTask(Task task) {
        if (CCAndroids.CONFIG.DebugLogging) CCAndroids.LOGGER.info("Set android task to {}", task.getName());
        taskManager.setCurrentTask(task);
    }

    public AndroidEntity getAndroid() {
        return android;
    }

    public AndroidModules getModules() {
        return modules;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public boolean isOwningPlayer(Player player) {
        return owningPlayerProfile != null && owningPlayerProfile.getId().equals(player.getGameProfile().getId());
    }

    public GameProfile getOwningPlayerProfile() {
        return owningPlayerProfile;
    }

    public void setOwningPlayer(GameProfile profile) {
        owningPlayerProfile = profile;
    }

    public void writeNbt(CompoundTag tag) {
        if (owningPlayerProfile == null) return;
        tag.putUUID("OwningPlayerUUID", owningPlayerProfile.getId());
        tag.putString("OwningPlayerName", owningPlayerProfile.getName());
    }

    public void readNbt(CompoundTag tag) {
        if (tag.hasUUID("OwningPlayerUUID")) {
            owningPlayerProfile = new GameProfile(tag.getUUID("OwningPlayerUUID"), tag.getString("OwningPlayerName"));
        }
    }
}
