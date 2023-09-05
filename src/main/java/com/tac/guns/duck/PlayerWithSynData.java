package com.tac.guns.duck;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.item.ItemStack;

public interface PlayerWithSynData {
    EntityDataAccessor<ItemStack> getRigDataAccessor();

    ItemStack getRig();

    void setRig(ItemStack itemStack);
}
