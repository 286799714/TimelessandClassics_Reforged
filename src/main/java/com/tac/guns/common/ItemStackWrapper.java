package com.tac.guns.common;

import net.minecraft.world.item.ItemStack;

public record ItemStackWrapper(ItemStack itemStack) {

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    public ItemStackWrapper copy(){
        return new ItemStackWrapper(itemStack);
    }
}
