package com.tac.guns.compat.jei;

import com.tac.guns.Reference;
import com.tac.guns.crafting.WorkbenchRecipe;
import com.tac.guns.crafting.WorkbenchRecipes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class GunWorkbenchRecipeCategory extends TimelessRecipeCategory<WorkbenchRecipe>{
    public GunWorkbenchRecipeCategory(IGuiHelper guiHelper) {
        super(guiHelper, new RecipeType<>(new ResourceLocation(Reference.MOD_ID, "workbench"), WorkbenchRecipe.class), "workbench", 0, 0, 144, 90);
    }
    @Override
    protected List<WorkbenchRecipe> getRecipes() {
        ClientLevel level = Minecraft.getInstance().level;
        return WorkbenchRecipes.getAll(level);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, WorkbenchRecipe recipe, IFocusGroup focuses) {
        int length = recipe.getMaterials().size();
        List<List<ItemStack>> inputList = getInputIngredients(recipe);
        for (int i = 0; i < length; i ++) {
            builder.addSlot(RecipeIngredientRole.INPUT, 9 + 20 * (i % 4), 10 + 18 * (i / 4))
                    .addItemStacks((List<ItemStack>) inputList.toArray()[i]);
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 119, 37).addItemStack(recipe.getResultItem());
    }

    //Thanks yor42 for jei recipe setup codes!
    private List<List<ItemStack>> getInputIngredients(WorkbenchRecipe recipe) {
        List<List<ItemStack>> ingredientList = new ArrayList<>();
        recipe.getMaterials().forEach(pair -> {
            ingredientList.add(getItemStackList(pair.getFirst(), pair.getSecond()));
        });
        return ingredientList;
    }

    //Thanks Mekanism for inspirations!
    private List<ItemStack> getItemStackList(Ingredient ingredient, int count) {
        List<ItemStack> stackList = new ArrayList<>();
        for (ItemStack stack : ingredient.getItems()) {
            if (stack.getCount() == count) {
                stackList.add(stack);
            } else {
                ItemStack stackCopy = stack.copy();
                stackCopy.setCount(count);
                stackList.add(stackCopy);
            }
        }
        return stackList;
    }

//    @Override
//    public void draw(WorkbenchRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
//    }
}
