package com.tac.guns.item.TransitionalTypes.wearables;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CurioCapabilityProvider implements ICurio {

    private final ItemStack stack;

    public CurioCapabilityProvider(ItemStack stack){
        this.stack = stack;
    }

    @Override
    public ItemStack getStack() {
        return this.stack;
    }

    @Nonnull
    @Override
    public SoundInfo getEquipSound(SlotContext slotContext)
    {
        return new SoundInfo(SoundEvents.ARMOR_EQUIP_CHAIN, 1.0F, 1.0F);
    }

    @Override
    public boolean canUnequip(String identifier, LivingEntity livingEntity)
    {
                /*if(!Config.SERVER.lockBackpackIntoSlot.get())
                    return true;*/
        CompoundTag tag = stack.getTag();
        return tag == null || tag.getList("Items", Tag.TAG_COMPOUND).isEmpty();
    }

    @Nonnull
    @Override
    public DropRule getDropRule(LivingEntity livingEntity)
    {
        return DropRule.DEFAULT;
    }

    @Nullable
    @Override
    public CompoundTag writeSyncData() {
        return stack.getShareTag();
    }
}
