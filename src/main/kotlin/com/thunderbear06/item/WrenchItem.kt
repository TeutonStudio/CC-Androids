package com.thunderbear06.item

import com.thunderbear06.entity.android.AndroidEntity
import com.thunderbear06.entity.android.CommandAndroidEntity
import com.thunderbear06.entity.android.frame.AndroidFrame
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag

class WrenchItem(properties: Properties) : Item(properties) {
    override fun interactLivingEntity(stack: ItemStack, user: Player, entity: LivingEntity, hand: InteractionHand): InteractionResult {
        if (entity is CommandAndroidEntity) return InteractionResult.FAIL
        if (entity is AndroidEntity && !entity.isLocked) {
            if (!user.level().isClientSide) {
                entity.deconstruct()
                val serverLevel = user.level() as? ServerLevel
                if (serverLevel != null) {
                    stack.hurtAndBreak(1, serverLevel, user as? ServerPlayer) { }
                }
            }
            return InteractionResult.sidedSuccess(user.level().isClientSide)
        }
        if (entity is AndroidFrame) {
            if (!user.level().isClientSide) entity.onBreak()
            return InteractionResult.sidedSuccess(user.level().isClientSide)
        }
        return InteractionResult.PASS
    }

    override fun appendHoverText(stack: ItemStack, context: TooltipContext, tooltipComponents: MutableList<Component>, tooltipFlag: TooltipFlag) {
        tooltipComponents.add(Component.translatable("gui.cc_androids.tooltip.wrench").withStyle(ChatFormatting.GRAY))
    }
}
