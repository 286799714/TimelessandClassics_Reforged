package com.tac.guns.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.tac.guns.Reference;
import com.tac.guns.init.ModRecipeSerializers;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Ocelot
 */
public class WorkbenchRecipeBuilder
{
    private final Item result;
    private final int count;
    private final List<Pair<Ingredient, Integer>> materials;
    private String group;

    public WorkbenchRecipeBuilder(ItemLike item, int count)
    {
        this.result = item.asItem();
        this.count = count;
        this.materials = new ArrayList<>();
    }

    /**
     * Creates a new builder for a workbench recipe.
     */
    public static WorkbenchRecipeBuilder workbenchRecipe(ItemLike resultIn)
    {
        return new WorkbenchRecipeBuilder(resultIn, 1);
    }

    /**
     * Creates a new builder for a workbench recipe.
     */
    public static WorkbenchRecipeBuilder workbenchRecipe(ItemLike resultIn, int countIn)
    {
        return new WorkbenchRecipeBuilder(resultIn, countIn);
    }

    /**
     * Adds an ingredient of the given item.
     */
    public WorkbenchRecipeBuilder addIngredient(ItemLike itemIn)
    {
        return this.addIngredient(itemIn, 1);
    }

    /**
     * Adds the given ingredient multiple times.
     */
    public WorkbenchRecipeBuilder addIngredient(ItemLike item, int quantity)
    {
        this.materials.add(new Pair<>(Ingredient.of(item), quantity));
        return this;
    }

    public WorkbenchRecipeBuilder addIngredient(TagKey<Item> item, int quantity)
    {
        this.materials.add(new Pair<>(Ingredient.of(item), quantity));
        return this;
    }

    public WorkbenchRecipeBuilder addIngredient(TagKey<Item> item)
    {
        this.materials.add(new Pair<>(Ingredient.of(item), 1));
        return this;
    }

    /**
     * Adds an ingredient of the given item.
     */
    public WorkbenchRecipeBuilder addIngredient(Ingredient itemIn)
    {
        return this.addIngredient(itemIn, 1);
    }

    /**
     * Adds the given ingredient multiple times.
     */
    public WorkbenchRecipeBuilder addIngredient(Ingredient item, int quantity)
    {
        this.materials.add(new Pair<>(item, quantity));
        return this;
    }
    /**
     * Sets the group to place this recipe in.
     */
    public WorkbenchRecipeBuilder setGroup(String groupIn)
    {
        this.group = groupIn;
        return this;
    }

    /**
     * Builds this recipe into an {@link IFinishedRecipe}.
     */
    public void build(Consumer<FinishedRecipe> consumerIn)
    {
        this.build(consumerIn, Registry.ITEM.getKey(this.result));
    }

    /**
     * Builds this recipe into an {@link IFinishedRecipe}. Use {@link #build(Consumer)} if save is the same as the ID for
     * the result.
     */
    public void build(Consumer<FinishedRecipe> consumerIn, String save)
    {
        this.build(consumerIn, Reference.MOD_ID, save);
    }

    public void build(Consumer<FinishedRecipe> consumerIn, String modid, String save)
    {
        ResourceLocation resourcelocation = Registry.ITEM.getKey(this.result);
        if(new ResourceLocation(Reference.MOD_ID, save).equals(resourcelocation))
        {
            throw new IllegalStateException("Workbench Recipe " + save + " should remove its 'save' argument");
        }
        else
        {
            this.build(consumerIn, new ResourceLocation(modid, "craft_"+save));
        }
    }

    /**
     * Builds this recipe into an {@link IFinishedRecipe}.
     */
    public void build(Consumer<FinishedRecipe> consumerIn, ResourceLocation id)
    {
        consumerIn.accept(new Result(id, this.result, this.count, this.group == null ? "" : this.group, this.materials));
    }
    public static class Result implements FinishedRecipe
    {
        private final ResourceLocation id;
        private final Item item;
        private final int count;
        private final String group;
        private final List<Pair<Ingredient, Integer>> materials;

        public Result(ResourceLocation id, ItemLike item, int count, String group, List<Pair<Ingredient, Integer>> materials)
        {
            this.id = id;
            this.item = item.asItem();
            this.count = count;
            this.group = group;
            this.materials = materials;
        }

        @Override
        public void serializeRecipeData(JsonObject json)
        {
            if(!this.group.isEmpty())
                json.addProperty("group", this.group);

            JsonArray input = new JsonArray();
            for(Pair<Ingredient, Integer> material : this.materials)
            {
                JsonObject resultObject = new JsonObject();
                resultObject.add("item", material.getFirst().toJson());
                if(material.getSecond() > 1)
                    resultObject.addProperty("count", material.getSecond());
                input.add(resultObject);
            }
            json.add("materials", input);

            JsonObject resultObject = new JsonObject();
            resultObject.addProperty("item", Registry.ITEM.getKey(this.item).toString());
            if(this.count > 1)
                resultObject.addProperty("count", this.count);
            json.add("result", resultObject);
        }

        @Override
        public ResourceLocation getId()
        {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType()
        {
            return ModRecipeSerializers.WORKBENCH.get();
        }

        @Override
        public JsonObject serializeAdvancement()
        {
            return null;
        }

        @Override
        public ResourceLocation getAdvancementId()
        {
            return null;
        }
    }
}
