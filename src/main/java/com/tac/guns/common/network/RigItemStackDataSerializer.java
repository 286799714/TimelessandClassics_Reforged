package com.tac.guns.common.network;

import com.tac.guns.common.ItemStackWrapper;
import com.tac.guns.inventory.gear.armor.ArmorRigCapabilityProvider;
import com.tac.guns.inventory.gear.armor.RigSlotsHandler;
import com.tac.guns.item.transition.wearables.ArmorRigItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.item.ItemStack;

public class RigItemStackDataSerializer implements EntityDataSerializer<ItemStackWrapper> {
    public static final RigItemStackDataSerializer INSTANCE = new RigItemStackDataSerializer();

    private RigItemStackDataSerializer(){
        EntityDataSerializers.registerSerializer(this);
    }

    public void write(FriendlyByteBuf p_135182_, ItemStackWrapper p_135183_) {
        p_135182_.writeItemStack(p_135183_.itemStack(), false);
    }

    public ItemStackWrapper read(FriendlyByteBuf p_135188_) {
        ItemStack itemStack = p_135188_.readItem();
        if(itemStack.getItem() instanceof ArmorRigItem) {
            RigSlotsHandler itemHandler = (RigSlotsHandler) itemStack.getCapability(ArmorRigCapabilityProvider.capability).resolve().get();
            itemHandler.deserializeNBT(itemStack.getTag().getCompound("storage"));
        }
        return new ItemStackWrapper(itemStack);
    }

    public ItemStackWrapper copy(ItemStackWrapper p_135176_) {
        return p_135176_.copy();
    }
}
