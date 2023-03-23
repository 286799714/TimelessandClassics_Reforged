package com.tac.guns.enchantment;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

import net.minecraft.enchantment.Enchantment.Rarity;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class CollateralEnchantment extends GunEnchantment
{
    public CollateralEnchantment()
    {
        super(Rarity.RARE, EnchantmentTypes.GUN, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND}, Type.PROJECTILE);
    }
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return false;
    }
    @Override
    public int getMinCost(int level)
    {
        return 10;
    }

    @Override
    public int getMaxCost(int level)
    {
        return this.getMinCost(level) + 20;
    }
}
