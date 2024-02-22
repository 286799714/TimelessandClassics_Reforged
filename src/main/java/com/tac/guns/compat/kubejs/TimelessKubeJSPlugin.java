package com.tac.guns.compat.kubejs;

import com.tac.guns.Reference;
import com.tac.guns.compat.kubejs.custom.GunItemBuilder;
import com.tac.guns.compat.kubejs.recipe.TimelessWorkbenchRecipeJS;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import dev.latvian.mods.kubejs.recipe.RegisterRecipeHandlersEvent;
import net.minecraft.resources.ResourceLocation;

public class TimelessKubeJSPlugin extends KubeJSPlugin {
    @Override
    public void addRecipes(RegisterRecipeHandlersEvent event) {
        event.register(new ResourceLocation(Reference.MOD_ID, "workbench"), TimelessWorkbenchRecipeJS::new);
    }
    @Override
    public void init() {
        RegistryObjectBuilderTypes.ITEM.addType("tac:gun_item", GunItemBuilder.class, GunItemBuilder::new);
    }
}
