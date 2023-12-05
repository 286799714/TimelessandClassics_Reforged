package com.tac.guns.compat.jei;

import com.tac.guns.Reference;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.registries.ForgeRegistries;
import org.stringtemplate.v4.ST;

import java.util.List;

public abstract class TimelessRecipeCategory<T extends Recipe<?>> implements IRecipeCategory<T> {
    protected final RecipeType<T> jeiRecipeType;
    protected String name;
    protected IDrawable icon;
    protected IDrawable background;

    public TimelessRecipeCategory(IGuiHelper guiHelper, RecipeType<T> type, String name, int u, int v, int width, int height) {
        this.jeiRecipeType = type;
        this.name = name;
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, getItemStackFromName(name));
        this.background = guiHelper.drawableBuilder(getBackgroundTexture(), u, v, width, height).setTextureSize(width, height).build();
    }
    @Override
    public Component getTitle() {
        return new TranslatableComponent(Reference.MOD_ID + ".recipe." + name);
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    @SuppressWarnings(value = "removal")
    public final ResourceLocation getUid() {
        return new ResourceLocation(Reference.MOD_ID, name);
    }

    @Override
    @SuppressWarnings(value = "removal")
    public final Class<? extends T> getRecipeClass() {
        return jeiRecipeType.getRecipeClass();
    }
    @Override
    public RecipeType<T> getRecipeType() {
        return this.jeiRecipeType;
    }

    protected ItemStack getItemStackFromName(String name) {
        ResourceLocation location = new ResourceLocation(Reference.MOD_ID, name);
        return new ItemStack(ForgeRegistries.ITEMS.getValue(location));
    }
    public void registerRecipeCatalyst(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(getItemStackFromName(name), getRecipeType());
    }

    public void registerRecipes(IRecipeRegistration registration) {
        if (getRecipes() != null) registration.addRecipes(getRecipeType(), getRecipes());
    }
    protected List<T> getRecipes() {
        return null;
    }

    private ResourceLocation getBackgroundTexture() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/jei/" + name + ".png");
    }
}
