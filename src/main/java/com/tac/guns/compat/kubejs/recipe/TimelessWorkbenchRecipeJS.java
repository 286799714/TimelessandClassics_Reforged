package com.tac.guns.compat.kubejs.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientStackJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.util.ListJS;
import net.minecraft.util.GsonHelper;

public class TimelessWorkbenchRecipeJS extends RecipeJS {
    public String inputName;
    public String outputName;
    public TimelessWorkbenchRecipeJS() {
        this.inputName = "materials";
        this.outputName = "result";
    }

    @Override
    public void create(ListJS args) {
        outputItems.add(parseResultItem(args.get(0)));
        inputItems.addAll(parseIngredientItemStackList(args.get(1)));
    }

    @Override
    public void deserialize() {
        outputItems.add(parseResultItem(json.get(outputName)));
        JsonArray inputArray = GsonHelper.getAsJsonArray(json, inputName);
        for (JsonElement e : inputArray) {
            JsonObject o = e.getAsJsonObject();
            int count = o.has("count") ? o.get("count").getAsInt() : 0;
            inputItems.add(parseIngredientItem(o.get("item")).withCount(count));
        }
    }

    @Override
    public void serialize() {
        if (serializeOutputs) {
            json.add(outputName, outputItems.get(0).toResultJson());
        }
        if (serializeInputs) {
            JsonArray inputArray = new JsonArray();
            for (IngredientJS ingredient: inputItems) {
                inputArray.add(ingredient.toJson());
            }
            json.add(inputName, inputArray);
        }
    }

    @Override
    public JsonElement serializeIngredientStack(IngredientStackJS in) {
        JsonObject json = new JsonObject();
        json.add("item", in.ingredient.toJson());
        if (in.getCount() > 1) {
            json.addProperty("count", in.getCount());
        }
        return json;
    }
}
