package com.tac.guns.inventory.gear.armor;

import com.tac.guns.inventory.gear.InventoryListener;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ArmorRigCapabilityProvider implements ICapabilitySerializable<ListTag> {

    public static Capability<IAmmoItemHandler> capability = InventoryListener.RIG_HANDLER_CAPABILITY;
    private RigSlotsHandler itemHandler = new RigSlotsHandler(9*6);
    private LazyOptional<IAmmoItemHandler> optionalStorage = LazyOptional.of(() -> itemHandler);

    public LazyOptional<IAmmoItemHandler> getOptionalStorage() {
        return optionalStorage;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == capability) {
            return optionalStorage.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public ListTag serializeNBT() {
        ListTag nbtTagList = new ListTag();
        int size = itemHandler.getSlots();
        for (int i = 0; i < size; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                stack.save(itemTag);
                nbtTagList.add(itemTag);
            }
        }
        return nbtTagList;
    }

    @Override
    public void deserializeNBT(ListTag nbt) {
        if (!(itemHandler instanceof IItemHandlerModifiable))
            throw new RuntimeException("IItemHandler instance does not implement IItemHandlerModifiable");
        IItemHandlerModifiable itemHandlerModifiable = (IItemHandlerModifiable) itemHandler;
        ListTag tagList = nbt;
        for (int i = 0; i < tagList.size(); i++) {
            CompoundTag itemTags = tagList.getCompound(i);
            int j = itemTags.getInt("Slot");

            if (j >= 0 && j < itemHandler.getSlots()) {
                itemHandlerModifiable.setStackInSlot(j, ItemStack.of(itemTags));
            }
        }
    }
}
