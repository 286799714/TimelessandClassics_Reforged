package com.tac.guns.common.network;

import com.mojang.logging.LogUtils;
import com.tac.guns.inventory.gear.armor.ArmorRigCapabilityProvider;
import com.tac.guns.inventory.gear.armor.RigSlotsHandler;
import com.tac.guns.item.transition.wearables.ArmorRigItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.item.ItemStack;

public class RigItemStackDataSerializer implements EntityDataSerializer<ItemStack> {
    public static final RigItemStackDataSerializer INSTANCE = new RigItemStackDataSerializer();

    private RigItemStackDataSerializer(){
        EntityDataSerializers.registerSerializer(this);
    }

    public void write(FriendlyByteBuf p_135182_, ItemStack p_135183_) {
        p_135182_.writeItemStack(p_135183_, false);
    }

    public ItemStack read(FriendlyByteBuf p_135188_) {
        ItemStack itemStack = p_135188_.readItem();
        RigSlotsHandler itemHandler = (RigSlotsHandler) itemStack.getCapability(ArmorRigCapabilityProvider.capability).resolve().get();
        itemHandler.deserializeNBT(itemStack.getTag().getCompound("storage"));
        return itemStack;
    }

    public ItemStack copy(ItemStack p_135176_) {
        return p_135176_.copy();
    }
}
