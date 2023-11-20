package com.tac.guns.client.event;

import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

import java.util.Map;

public class ModelBakeryProcessLoadingEvent extends Event {
    public final Map<ResourceLocation, UnbakedModel> unbakedCache;
    public final Map<ResourceLocation, UnbakedModel> topLevelModels;
    public final UnbakedModel missingModel;

    public ModelBakeryProcessLoadingEvent(Map<ResourceLocation, UnbakedModel> unbakedCache, Map<ResourceLocation, UnbakedModel> topLevelModels, UnbakedModel missingModel){
        this.unbakedCache = unbakedCache;
        this.topLevelModels = topLevelModels;
        this.missingModel = missingModel;
    }

    public void loadModelToCache(ResourceLocation resourceLocation, UnbakedModel model){
        unbakedCache.put(resourceLocation, model);
        topLevelModels.put(resourceLocation, model);
    }

    public UnbakedModel getMissingModel(){
        return missingModel;
    }
}
