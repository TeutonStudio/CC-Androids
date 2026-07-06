package com.thunderbear06.ai.modules;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.ai.task.tasks.MoveToBlockTask;
import com.thunderbear06.ai.task.tasks.MoveToEntityTask;
import com.thunderbear06.entity.android.AndroidEntity;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import dan200.computercraft.api.lua.MethodResult;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

public class NavigationModule extends AbstractAndroidModule {
    public NavigationModule(BaseAndroidEntity android, AndroidBrain brain) {
        super(android, brain);
    }

    public MethodResult moveToBlock(BlockPos pos) {
        if (!android.level().isInWorldBounds(pos)) return MethodResult.of(false, "Block position must be within build limit");
        brain.setTask(new MoveToBlockTask((AndroidEntity) android, 0.6D, pos));
        return MethodResult.of(true);
    }

    public MethodResult moveToEntity(String uuid) {
        if (!(android.level() instanceof ServerLevel serverLevel)) return MethodResult.of(false, "Server level unavailable");
        var entity = serverLevel.getEntity(UUID.fromString(uuid));
        if (!(entity instanceof LivingEntity living) || !living.isAlive()) return MethodResult.of(false, "Unknown entity or invalid UUID");
        brain.setTask(new MoveToEntityTask((AndroidEntity) android, 0.6D, living));
        return MethodResult.of(true);
    }
}
