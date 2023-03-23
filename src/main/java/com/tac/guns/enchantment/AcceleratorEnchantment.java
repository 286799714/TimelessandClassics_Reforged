package com.tac.guns.enchantment;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

import net.minecraft.enchantment.Enchantment.Rarity;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class AcceleratorEnchantment extends GunEnchantment
{
    public AcceleratorEnchantment()
    {
        super(Rarity.RARE, EnchantmentTypes.GUN, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND}, Type.PROJECTILE);
    }

    @Override
    public int getMaxLevel()
    {
        return 3;
    }

    @Override
    public int getMinCost(int level)
    {
        return 10 + (level - 1) * 10;
    }
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return false;
    }
    @Override
    public int getMaxCost(int level)
    {
        return this.getMinCost(level) + 20;
    }
}
