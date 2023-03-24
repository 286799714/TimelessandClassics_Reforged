package com.tac.guns.inventory.gear.backpack;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class BackpackContainerProvider implements MenuProvider {

    private ItemStack item;

    public BackpackContainerProvider(ItemStack item) {
        this.item = item;
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent("AmmoPack");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
        BackpackContainer container = new BackpackContainer(windowId, inv, this.item);
        return container;
    }
}
