package com.thunderbear06.inventory;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class AndroidInventory extends SimpleContainer {
    public AndroidInventory(int size) {
        super(size);
    }

    public ListTag toTag(HolderLookup.Provider registries) {
        return createTag(registries);
    }

    public void fromTag(ListTag tag, HolderLookup.Provider registries) {
        clearContent();
        super.fromTag(tag, registries);
    }

    public List<ItemStack> clearToList() {
        return removeAllItems();
    }
}
