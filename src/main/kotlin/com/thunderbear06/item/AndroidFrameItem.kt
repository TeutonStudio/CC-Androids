package com.thunderbear06.item

import com.thunderbear06.CCAndroids
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3

class AndroidFrameItem(properties: Properties) : Item(properties) {
    override fun useOn(context: UseOnContext): InteractionResult {
        if (context.clickedFace.axis.isVertical && context.clickedFace.stepY < 0) return InteractionResult.FAIL
        val level: Level = context.level
        val pos: BlockPos = context.clickedPos.relative(context.clickedFace)
        val center: Vec3 = Vec3.atBottomCenterOf(pos)
        val box: AABB = CCAndroids.UNFINISHED_ANDROID.get().dimensions.makeBoundingBox(center.x, center.y, center.z)
        if (!level.noCollision(box) || level.getEntities(null, box).isNotEmpty()) return InteractionResult.FAIL
        if (!level.isClientSide) {
            val frame = CCAndroids.UNFINISHED_ANDROID.get().create(level) ?: return InteractionResult.FAIL
            frame.moveTo(center.x, center.y, center.z, context.rotation, 0.0f)
            level.addFreshEntity(frame)
            level.playSound(null, frame, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.75f, 0.8f)
        }
        context.itemInHand.shrink(1)
        return InteractionResult.sidedSuccess(level.isClientSide)
    }
}
