package com.tac.guns.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class InventoryUtil
{
    public static int getItemStackAmount(Player player, ItemStack find)
    {
        int count = 0;
        for(ItemStack stack : player.getInventory().items)
        {
            if(!stack.isEmpty() && areItemStacksEqualIgnoreCount(stack, find))
            {
                count += stack.getCount();
            }
        }
        return count;
    }

    public static boolean hasIngredient(Player player, Pair<Ingredient, Integer> pair)
    {
        int count = 0;
        for(int i = 0; i < player.getInventory().getContainerSize(); i++)
        {
            ItemStack stack = player.getInventory().getItem(i);
            if(pair.getFirst().test(stack))
            {
                count += stack.getCount();
            }
        }
        return pair.getSecond() <= count;
    }

    public static boolean removeItemStackFromIngredient(Player player, Pair<Ingredient, Integer> pair)
    {
        int amount = pair.getSecond();
        for(int i = 0; i < player.getInventory().getContainerSize(); i++)
        {
            ItemStack stack = player.getInventory().getItem(i);
            if(pair.getFirst().test(stack))
            {
                if(amount - stack.getCount() < 0)
                {
                    stack.shrink(amount);
                    player.getInventory().setItem(i, stack);
                    return true;
                }
                else
                {
                    amount -= stack.getCount();
                    player.getInventory().setItem(i, ItemStack.EMPTY);
                    if(amount == 0)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean areItemStacksEqualIgnoreCount(ItemStack source, ItemStack target)
    {
        if(source.getItem() != target.getItem())
        {
            return false;
        }
        else if(source.getDamageValue() != target.getDamageValue())
        {
            return false;
        }
        else if(source.getTag() == null && target.getTag() != null)
        {
            return false;
        }
        else
        {
            return (source.getTag() == null || source.getTag().equals(target.getTag())) && source.areCapsCompatible(target);
        }
    }
}
