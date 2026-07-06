package com.thunderbear06.entity.player;

import com.mojang.authlib.GameProfile;
import com.thunderbear06.ai.AndroidBrain;
import dan200.computercraft.shared.platform.PlatformHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
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

    @Nullable
    public static ServerPlayer fake(AndroidBrain brain) {
        if (!(brain.getAndroid().level() instanceof ServerLevel serverLevel)) return null;
        GameProfile profile = brain.getOwningPlayerProfile() == null ? fallbackProfile(brain) : brain.getOwningPlayerProfile();
        ServerPlayer player = PlatformHelper.get().createFakePlayer(serverLevel, profile);
        player.moveTo(brain.getAndroid().getX(), brain.getAndroid().getY(), brain.getAndroid().getZ(), brain.getAndroid().getYRot(), brain.getAndroid().getXRot());
        player.setItemInHand(InteractionHand.MAIN_HAND, brain.getAndroid().getMainHandItem().copy());
        player.setItemInHand(InteractionHand.OFF_HAND, brain.getAndroid().getOffhandItem().copy());
        return player;
    }

    public static void copyHandsBack(AndroidBrain brain, ServerPlayer player) {
        brain.getAndroid().setItemInHand(InteractionHand.MAIN_HAND, player.getMainHandItem().copy());
        brain.getAndroid().setItemInHand(InteractionHand.OFF_HAND, player.getOffhandItem().copy());
    }
}
