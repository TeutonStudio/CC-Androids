package com.thunderbear06.item;

import com.thunderbear06.entity.android.AndroidEntity;
import com.thunderbear06.entity.android.CommandAndroidEntity;
import com.thunderbear06.entity.android.frame.AndroidFrame;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class WrenchItem extends Item {
    public WrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand) {
        if (entity instanceof CommandAndroidEntity) return InteractionResult.FAIL;
        if (entity instanceof AndroidEntity android && !android.isLocked()) {
            if (!user.level().isClientSide) {
                android.deconstruct();
                if (user.level() instanceof ServerLevel serverLevel) stack.hurtAndBreak(1, serverLevel, user instanceof net.minecraft.server.level.ServerPlayer sp ? sp : null, item -> {});
            }
            return InteractionResult.sidedSuccess(user.level().isClientSide);
        }
        if (entity instanceof AndroidFrame frame) {
            if (!user.level().isClientSide) frame.onBreak();
            return InteractionResult.sidedSuccess(user.level().isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("gui.cc_androids.tooltip.wrench").withStyle(ChatFormatting.GRAY));
    }
}
