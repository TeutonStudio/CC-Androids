package com.thunderbear06.inventory

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.ListTag
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack

class AndroidInventory(size: Int) : SimpleContainer(size) {
    fun toTag(registries: HolderLookup.Provider): ListTag = createTag(registries)

    override fun fromTag(tag: ListTag, registries: HolderLookup.Provider) {
        clearContent()
        super.fromTag(tag, registries)
    }

    fun clearToList(): List<ItemStack> = removeAllItems()
}
