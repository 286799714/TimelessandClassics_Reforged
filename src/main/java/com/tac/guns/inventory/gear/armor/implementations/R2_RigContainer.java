package com.tac.guns.inventory.gear.armor.implementations;

import com.tac.guns.init.ModContainers;
import com.tac.guns.inventory.gear.armor.AmmoSlot;
import com.tac.guns.inventory.gear.armor.ArmorRigCapabilityProvider;
import com.tac.guns.inventory.gear.armor.IRigContainer;
import com.tac.guns.inventory.gear.armor.RigSlotsHandler;
import com.tac.guns.item.transition.wearables.ArmorRigItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class R2_RigContainer extends AbstractContainerMenu implements IRigContainer {
    public final static int ROW_NUM = 2; //swap per row count
    public R2_RigContainer(int windowId, Inventory inv, ItemStack item) {
        super(ModContainers.ARMOR_R2.get(), windowId); // Swap per row count
        RigSlotsHandler itemHandler = (RigSlotsHandler) item.getCapability(ArmorRigCapabilityProvider.capability).resolve().get();
        int i = (this.getNumRows() - 4) * 18;

        for(int j = 0; j < this.getNumRows(); ++j) {
            for(int k = 0; k < 9; ++k) {
                this.addSlot(new AmmoSlot(itemHandler, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }

        for(int l = 0; l < 3; ++l) {
            for(int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(inv, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
            }
        }

        for(int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(inv, i1, 8 + i1 * 18, 161 + i));
        }
    }

    public R2_RigContainer(int windowId, Inventory inv) {
        super(ModContainers.ARMOR_R2.get(), windowId);  // Swap per row count

        int i = (this.getNumRows() - 4) * 18;
        ItemStackHandler itemHandler = new ItemStackHandler(9*getNumRows());
        for(int j = 0; j < this.getNumRows(); ++j) {
            for(int k = 0; k < 9; ++k) {
                this.addSlot(new AmmoSlot(itemHandler, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }

        for(int l = 0; l < 3; ++l) {
            for(int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(inv, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
            }
        }

        for(int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(inv, i1, 8 + i1 * 18, 161 + i));
        }
    }


    @Override
    public boolean stillValid(Player playerIn) {
        return true;
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
        if(slotId <= 0) {
            super.clicked(slotId, dragType, clickTypeIn, player);
            return;
        }
        Slot slot = this.slots.get(slotId);
        if(slot.hasItem()) {
            if(slot.getItem().getItem() instanceof ArmorRigItem) return;
        }
        super.clicked(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < this.getNumRows() * 9) {
                if (!this.moveItemStackTo(itemstack1, this.getNumRows() * 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.getNumRows() * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }
    @Override
    public AbstractContainerMenu getSelf() {
        return this;
    }
    @Override
    public int getNumRows(){return ROW_NUM;}
    private static boolean isAmmo(ItemStack stack, ResourceLocation id)
    {
        return stack != null && stack.getItem().getRegistryName().equals(id);
    }
}
