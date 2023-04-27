package com.tac.guns.util;

import com.tac.guns.client.screen.UpgradeBenchScreen;
import com.tac.guns.common.Gun;
import com.tac.guns.init.ModEnchantments;
import com.tac.guns.item.GunItem;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.HashMap;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class GunEnchantmentHelper
{
    // UNSAFE, ME NO LIKEY - CLUMSYALIEN
    public static HashMap<String, UpgradeBenchScreen.RequirementItem> upgradeableEnchs =
        new HashMap<String, UpgradeBenchScreen.RequirementItem>()
    {{
        put("Over Pressured",
                new UpgradeBenchScreen.RequirementItem(new int[]{2,3,3,5}, new int[]{5,7,10,15},
                        ModEnchantments.ACCELERATOR.get()));
        /*put("Over Capacity",
                new UpgradeBenchScreen.RequirementItem(new int[]{1,2,3}, new int[]{1,3,6},
                        ModEnchantments.OVER_CAPACITY.get()));*/
        put("Advanced Rifling",
                new UpgradeBenchScreen.RequirementItem(new int[]{1,2,3}, new int[]{4,6,8},
                        ModEnchantments.RIFLING.get()));
        put("Buffered",
                new UpgradeBenchScreen.RequirementItem(new int[]{2,4}, new int[]{5,11},
                        ModEnchantments.BUFFERED.get()));
        put("Puncturing",
                new UpgradeBenchScreen.RequirementItem(new int[]{1,2,3,4,5,6}, new int[]{4,6,8,11,14,17},
                        ModEnchantments.PUNCTURING.get()));
    }};

    public static int getReloadInterval(ItemStack weapon)
    {
        int interval = ((GunItem)weapon.getItem()).getGun().getReloads().getinterReloadPauseTicks();//10;
        //int level = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.QUICK_HANDS.get(), weapon);
        //if(level > 0)
        //{
        //    interval -= 3 * level;
        //}
        return Math.max(interval, 1);
    }
    public static double getAimDownSightSpeed(ItemStack weapon)
    {
        int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.LIGHTWEIGHT.get(), weapon);
        if(level > 0)
        {
            return 1.0 + (0.075 * level);
        }
        return 1;
    }
    public static float getSpreadModifier(ItemStack weapon)
    {
        int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.RIFLING.get(), weapon);
        if(level > 0)
        {
            return 1.0f - (0.0333f * level);
        }
        return 1f;
    }
    public static float getWeightModifier(ItemStack weapon)
    {
        int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.LIGHTWEIGHT.get(), weapon);
        if(level > 0)
        {
            return 0.4f * level;
        }
        return 0f;
    }
    public static double getProjectileSpeedModifier(ItemStack weapon)
    {
        int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.ACCELERATOR.get(), weapon);
        if(level > 0)
        {
            return 1.0 + 0.0333 * level;
        }
        return 1.0;
    }

    public static float getAcceleratorDamage(ItemStack weapon, float damage)
    {
        int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.ACCELERATOR.get(), weapon);
        if(level > 0)
        {
            return damage + damage * (0.05F * level);
        }
        return damage;
    }
    public static float getBufferedRecoil(ItemStack weapon)
    {
        int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.BUFFERED.get(), weapon);
        if(level > 0)
        {
            return (1-(0.15F * level));
        }
        return 1;
    }
    public static float getPuncturingChance(ItemStack weapon)
    {
        int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.PUNCTURING.get(), weapon);
        return level * 0.05F;
    }
}
