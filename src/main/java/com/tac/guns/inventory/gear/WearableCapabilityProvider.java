package com.tac.guns.inventory.gear;

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

public class WearableCapabilityProvider implements ICapabilitySerializable<ListTag> {


    public static Capability<IWearableItemHandler> capability = InventoryListener.ITEM_HANDLER_CAPABILITY;
    private GearSlotsHandler itemHandler = new GearSlotsHandler(2);
    private LazyOptional<IWearableItemHandler> optionalStorage = LazyOptional.of(() -> itemHandler);
    public LazyOptional<IWearableItemHandler> getOptionalStorage() {
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

    /*public static void syncAmmoCounts(World world)
    {
        this.getUpdateTag();
        if(this.world != null && !this.world.isRemote)
        {
            if(this.world instanceof ServerWorld)
            {
                SUpdateTileEntityPacket packet = this.getUpdatePacket();
                if(packet != null)
                {
                    ServerWorld server = (ServerWorld) this.world;
                    Stream<ServerPlayerEntity> players = server.getChunkProvider().chunkManager.getTrackingPlayers(new ChunkPos(this.pos), false);
                    players.forEach(player -> player.connection.sendPacket(packet));
                }
            }
        }
    }*/

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
        if (itemHandler == null)
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
