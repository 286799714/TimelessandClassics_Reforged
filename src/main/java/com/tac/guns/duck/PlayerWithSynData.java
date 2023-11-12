package com.tac.guns.duck;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.item.ItemStack;

public interface PlayerWithSynData {

    ItemStack getRig();

    void setRig(ItemStack newRig);

    void updateRig();
}
