package com.tac.guns.item;


import com.tac.guns.item.attachment.IGunSkin;
import com.tac.guns.item.attachment.impl.GunSkin;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public class GunSkinItem extends Item implements IGunSkin, IColored{
    public static final String CUSTOM_MODIFIER = "CustomModifier";
    private final GunSkin gunSkin;

    public GunSkinItem(GunSkin gunSkin, Properties properties) {
        super(properties);
        this.gunSkin = gunSkin;
    }

    public static boolean hasCustomModifier(ItemStack stack){
        return stack!=null && stack.getTag()!=null && stack.getTag().contains(CUSTOM_MODIFIER, Tag.TAG_STRING);
    }

    public static void setCustomModifier(ItemStack stack, ResourceLocation location){
        if(stack!=null && location!=null) {
            stack.getOrCreateTag().putString(CUSTOM_MODIFIER, location.toString());
        }
    }

    @Override
    public GunSkin getProperties() {
        return this.gunSkin;
    }


    @Override
    public boolean canColor(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment == Enchantments.BINDING_CURSE || super.canApplyAtEnchantingTable(stack, enchantment);
    }
}
