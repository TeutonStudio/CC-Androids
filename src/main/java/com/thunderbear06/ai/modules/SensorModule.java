package com.thunderbear06.ai.modules;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SensorModule extends AbstractAndroidModule {
    private final double entitySearchRadius;
    private final int blockSearchRadius;

    public SensorModule(BaseAndroidEntity android, AndroidBrain brain, double entitySearchRadius, int blockSearchRadius) {
        super(android, brain);
        this.entitySearchRadius = entitySearchRadius;
        this.blockSearchRadius = blockSearchRadius;
    }

    public List<HashMap<String, Object>> getMobs(@Nullable String type) {
        List<HashMap<String, Object>> result = new ArrayList<>();
        for (LivingEntity entity : android.level().getEntitiesOfClass(LivingEntity.class, android.getBoundingBox().inflate(entitySearchRadius), e -> e != android && e.isAlive() && matchesType(e, type))) {
            result.add(collectEntityInfo(entity));
        }
        return result;
    }

    public HashMap<String, Object> getClosestMobOfType(@Nullable String type) {
        return android.level().getEntitiesOfClass(LivingEntity.class, android.getBoundingBox().inflate(entitySearchRadius), e -> e != android && e.isAlive() && matchesType(e, type))
                .stream().min((a, b) -> Double.compare(a.distanceToSqr(android), b.distanceToSqr(android)))
                .map(this::collectEntityInfo).orElseGet(HashMap::new);
    }

    public HashMap<String, Object> getClosestPlayer() {
        Player player = android.level().getNearestPlayer(android, 100.0D);
        return player == null ? new HashMap<>() : collectEntityInfo(player);
    }

    public @Nullable ItemEntity getGroundItem(@Nullable String type) {
        for (ItemEntity item : android.level().getEntitiesOfClass(ItemEntity.class, android.getBoundingBox().inflate(5.0D))) {
            if (type == null || BuiltInRegistries.ITEM.getKey(item.getItem().getItem()).toString().contains(type)) return item;
        }
        return null;
    }

    public List<HashMap<String, Integer>> getBlocksOfType(BlockPos origin, String type) {
        List<HashMap<String, Integer>> blocks = new ArrayList<>();
        BlockPos.betweenClosed(origin.offset(-blockSearchRadius, -blockSearchRadius, -blockSearchRadius), origin.offset(blockSearchRadius, blockSearchRadius, blockSearchRadius)).forEach(pos -> {
            if (BuiltInRegistries.BLOCK.getKey(android.level().getBlockState(pos).getBlock()).toString().contains(type)) {
                HashMap<String, Integer> data = new HashMap<>();
                data.put("x", pos.getX());
                data.put("y", pos.getY());
                data.put("z", pos.getZ());
                blocks.add(data);
            }
        });
        return blocks;
    }

    public HashMap<String, Object> collectEntityInfo(Entity entity) {
        HashMap<String, Object> info = new HashMap<>();
        info.put("uuid", entity.getStringUUID());
        info.put("name", entity.getName().getString());
        info.put("posX", entity.getX());
        info.put("posY", entity.getY());
        info.put("posZ", entity.getZ());
        if (entity instanceof LivingEntity living) info.put("health", living.getHealth());
        return info;
    }

    public HashMap<String, Object> getContainerInfo(BlockPos pos) throws LuaException {
        if (!pos.closerThan(android.blockPosition(), blockSearchRadius)) throw new LuaException("Position out of range");
        BlockEntity blockEntity = android.level().getBlockEntity(pos);
        HashMap<String, Object> info = new HashMap<>();
        if (!(blockEntity instanceof Container container)) return info;
        info.put("slotCount", container.getContainerSize());
        List<List<Object>> slots = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            slots.add(List.of(stack.getHoverName().getString(), stack.getCount()));
        }
        info.put("slots", slots);
        return info;
    }

    private boolean matchesType(LivingEntity entity, @Nullable String type) {
        return type == null || BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString().contains(type);
    }
}
