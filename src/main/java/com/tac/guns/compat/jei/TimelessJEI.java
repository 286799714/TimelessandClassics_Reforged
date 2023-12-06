package com.tac.guns.compat.jei;

import com.tac.guns.Reference;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class TimelessJEI implements IModPlugin {
    private static final ResourceLocation PLUGIN_ID = new ResourceLocation(Reference.MOD_ID, "jei_plugin");
    private final List<TimelessRecipeCategory> categories = new ArrayList<>();
    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    /* TODO: optimize it! really confusing code right?
     */
    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
        categories.clear();
        categories.add(new GunWorkbenchRecipeCategory(helper));
        registration.addRecipeCategories(categories.toArray(IRecipeCategory[]::new));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        categories.forEach(category -> category.registerRecipes(registration));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        categories.forEach(category -> category.registerRecipeCatalyst(registration));
    }
}
