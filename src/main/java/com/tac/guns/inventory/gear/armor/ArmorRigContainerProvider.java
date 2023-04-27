package com.tac.guns.inventory.gear.armor;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class ArmorRigContainerProvider implements MenuProvider {

    private ItemStack item;

    private ArmorRigContainer container;
    public ArmorRigContainerProvider(ItemStack item) {
        this.item = item;
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent("AmmoPack");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
        this.container = new ArmorRigContainer(windowId, inv, this.item);
        return container;
    }

    public ArmorRigContainer getContainer() {
        return this.container;
    }
}
