package com.thunderbear06.recipe;

import com.thunderbear06.CCAndroids;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class RecipeRegistry {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, CCAndroids.MOD_ID);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<WrenchShapedRecipe>> WRENCH_SHAPED =
            SERIALIZERS.register("wrench_shaped", WrenchShapedRecipe.Serializer::new);

    private RecipeRegistry() {
    }
}
