package com.tac.guns.crafting;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 */
public class RecipeType
{
    public static final RecipeType<WorkbenchRecipe> WORKBENCH = register("tac:workbench");

    static <T extends Recipe<?>> RecipeType<T> register(final String key)
    {
        return Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(key), new RecipeType<T>()
        {
            @Override
            public String toString()
            {
                return key;
            }
        });
    }
}
