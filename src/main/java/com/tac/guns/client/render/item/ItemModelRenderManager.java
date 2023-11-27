package com.tac.guns.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tac.guns.client.resource.model.CacheableModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ItemModelRenderManager {
    private static final Map<String, ItemModelRenderer<? extends CacheableModel>> rendererMap = new HashMap<>();

    public static <T extends CacheableModel> void registerRenderer(Class<T> modelClass, ItemModelRenderer<T> renderer){
        rendererMap.put(modelClass.getName(), renderer);
    }

    @SuppressWarnings("unchecked")
    public static <T extends CacheableModel> ItemModelRenderer<T> getRenderer(Class<T> modelClass){
        return (ItemModelRenderer<T>) rendererMap.get(modelClass.getName());
    }

    @SuppressWarnings("unchecked")
    public static <T extends CacheableModel> void render(CacheableModel model, ItemStack stack, PoseStack matrices, MultiBufferSource renderBuffer, int light, int overlay){
        ItemModelRenderer<T> renderer = (ItemModelRenderer<T>) rendererMap.get(model.getClass().getName());
        if(renderer == null) return;
        renderer.render((T) model, stack, matrices, renderBuffer, light, overlay);
    }
}
