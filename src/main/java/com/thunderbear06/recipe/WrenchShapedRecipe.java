package com.thunderbear06.recipe;

import com.mojang.serialization.MapCodec;
import com.thunderbear06.CCAndroids;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

public final class WrenchShapedRecipe extends ShapedRecipe {
    public WrenchShapedRecipe(ShapedRecipe recipe) {
        super(recipe.getGroup(), recipe.category(), recipe.pattern, recipe.getResultItem(null), recipe.showNotification());
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);
        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack stack = input.getItem(slot);
            if (stack.is(CCAndroids.WRENCH.get())) remaining.set(slot, stack.copy());
        }
        return remaining;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.WRENCH_SHAPED.get();
    }

    public static final class Serializer implements RecipeSerializer<WrenchShapedRecipe> {
        private static final MapCodec<WrenchShapedRecipe> CODEC =
                ShapedRecipe.Serializer.CODEC.xmap(WrenchShapedRecipe::new, recipe -> recipe);
        private static final StreamCodec<RegistryFriendlyByteBuf, WrenchShapedRecipe> STREAM_CODEC =
                ShapedRecipe.Serializer.STREAM_CODEC.map(WrenchShapedRecipe::new, recipe -> recipe);

        @Override
        public MapCodec<WrenchShapedRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, WrenchShapedRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
