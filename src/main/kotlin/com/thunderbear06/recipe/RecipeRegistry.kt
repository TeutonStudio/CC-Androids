package com.thunderbear06.recipe

import com.thunderbear06.CCAndroids
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.RecipeSerializer
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object RecipeRegistry {
    @JvmField
    val SERIALIZERS: DeferredRegister<RecipeSerializer<*>> =
        DeferredRegister.create(Registries.RECIPE_SERIALIZER, CCAndroids.MOD_ID)

    @JvmField
    val WRENCH_SHAPED: DeferredHolder<RecipeSerializer<*>, RecipeSerializer<WrenchShapedRecipe>> =
        SERIALIZERS.register("wrench_shaped", Supplier { WrenchShapedRecipe.Serializer() })
}
