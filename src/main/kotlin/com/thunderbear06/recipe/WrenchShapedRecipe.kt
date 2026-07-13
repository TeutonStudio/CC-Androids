package com.thunderbear06.recipe

import com.mojang.serialization.MapCodec
import com.thunderbear06.CCAndroids
import net.minecraft.core.NonNullList
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.ShapedRecipe

class WrenchShapedRecipe(recipe: ShapedRecipe) : ShapedRecipe(
    recipe.group,
    recipe.category(),
    recipe.pattern,
    recipe.getResultItem(null),
    recipe.showNotification(),
) {
    override fun getRemainingItems(input: CraftingInput): NonNullList<ItemStack> {
        val remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY)
        for (slot in 0 until input.size()) {
            val stack = input.getItem(slot)
            if (stack.`is`(CCAndroids.WRENCH.get())) remaining[slot] = stack.copy()
        }
        return remaining
    }

    override fun getSerializer(): RecipeSerializer<*> = RecipeRegistry.WRENCH_SHAPED.get()

    class Serializer : RecipeSerializer<WrenchShapedRecipe> {
        override fun codec(): MapCodec<WrenchShapedRecipe> = CODEC

        override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, WrenchShapedRecipe> = STREAM_CODEC

        companion object {
            private val CODEC: MapCodec<WrenchShapedRecipe> =
                ShapedRecipe.Serializer.CODEC.xmap(::WrenchShapedRecipe) { recipe -> recipe }

            private val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, WrenchShapedRecipe> =
                ShapedRecipe.Serializer.STREAM_CODEC.map(::WrenchShapedRecipe) { recipe -> recipe }
        }
    }
}
