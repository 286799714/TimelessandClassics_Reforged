package com.tac.guns.inventory.gear.armor;

import com.tac.guns.item.transition.wearables.ArmorRigItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public interface IRigContainer {
    int getNumRows();
    AbstractContainerMenu getSelf();
}
