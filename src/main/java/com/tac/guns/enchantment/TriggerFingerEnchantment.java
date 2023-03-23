package com.tac.guns.enchantment;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import net.minecraft.enchantment.Enchantment.Rarity;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class TriggerFingerEnchantment extends GunEnchantment
{
    public TriggerFingerEnchantment()
    {
        super(Rarity.RARE, EnchantmentTypes.SEMI_AUTO_GUN, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND}, Type.WEAPON);
    }

    @Override
    public int getMaxLevel()
    {
        return 2;
    }

    @Override
    public int getMinCost(int level)
    {
        return 0;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return false;
    }

    @Override
    public int getMaxCost(int level)
    {
        return this.getMinCost(level) + 40;
    }
}
