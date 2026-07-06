package com.thunderbear06.entity.player;

import com.mojang.authlib.GameProfile;
import com.thunderbear06.ai.AndroidBrain;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class AndroidPlayer {
    private AndroidPlayer() {
    }

    @Nullable
    public static ServerPlayer get(AndroidBrain brain) {
        if (!(brain.getAndroid().level() instanceof ServerLevel serverLevel)) return null;
        Player owner = serverLevel.getNearestPlayer(brain.getAndroid(), 64.0D);
        return owner instanceof ServerPlayer serverPlayer ? serverPlayer : null;
    }

    public static GameProfile fallbackProfile(AndroidBrain brain) {
        return new GameProfile(UUID.nameUUIDFromBytes(brain.getAndroid().getUUID().toString().getBytes()), "Android");
    }
}
